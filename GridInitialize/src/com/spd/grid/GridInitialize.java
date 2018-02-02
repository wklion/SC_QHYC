package com.spd.grid;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mg.objects.Analyst;
import com.mg.objects.Dataset;
import com.mg.objects.DatasetRaster;
import com.mg.objects.DatasetVector;
import com.mg.objects.Datasource;
import com.mg.objects.GeoPoint;
import com.mg.objects.GeoRegion;
import com.mg.objects.Recordset;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;
import com.spd.domain.GridInfo;
import com.spd.domain.Scheme;
import com.spd.grid.mapper.GridProductMapper;
import com.spd.grid.service.SqlSessionUtil;
import com.spd.tool.CommonTool;
import com.spd.tool.LogTool;

/*
 * 格点预报初始化
 * 流程：
 * 		1、查询默认方案t_griddefaultscheme
 * 		2、根据默认方案，按要素，从数据库（数据源）中取得相应（最新时次预报所对应的时间范围）格点场
 * 		3、按规则命名并保存格点场
 * */
public class GridInitialize {
	private Workspace m_workspace;
	
	//数据库连接信息
	private String m_strIp;
	private String m_strPort;
	private String m_strDB;
	private String m_strUser;
	private String m_strPassword;
	
	//默认方案
	private ArrayList<Scheme> m_defaultScheme = null;
	private Datasource m_datasource = null;
	
	//省、市、县
	private static String TAG_PROVINCE = "prvn";
	private static String TAG_CITY = "cty";
	private static String TAG_COUNTY = "cnty";
	
	private static Double m_dResolutionX = 0.05;
	private static Double m_dResolutionY = 0.05;
	private static Double m_left = 104.175;
	private static Double m_bottom = 19.475;
	private static Double m_width = 8.05;
	private static Double m_height = 7.05;
	
	private void start()
	{
		CommonTool commonTool = new CommonTool();
		m_strIp = commonTool.getValue("Server");
		m_strPort = commonTool.getValue("Port");
		m_strDB = commonTool.getValue("DB");
		m_strUser = commonTool.getValue("User");
		m_strPassword = commonTool.getValue("Password");
		
		m_dResolutionX = Double.valueOf(commonTool.getValue("ResolutionX"));
		m_dResolutionY = Double.valueOf(commonTool.getValue("ResolutionY"));
		m_left = Double.valueOf(commonTool.getValue("Left"));
		m_bottom = Double.valueOf(commonTool.getValue("Bottom"));
		m_width = Double.valueOf(commonTool.getValue("Width"));
		m_height = Double.valueOf(commonTool.getValue("Height"));
		
		String strConnectionInfo = String.format("{\"Type\":\"%s\",\"Alias\":\"%s\",\"Server\":\"%s\",\"User\":\"%s\",\"Password\":\"%s\",\"DB\":\"%s\",\"Port\":\"%s\"}",
				commonTool.getValue("Type"), commonTool.getValue("Alias"), m_strIp, m_strUser,m_strPassword, m_strDB, m_strPort);
		try{
			m_workspace = new Workspace();
			m_datasource = m_workspace.OpenDatasource(strConnectionInfo);
			
			this.gridSync(TAG_PROVINCE);
			this.gridSync(TAG_CITY);
			//this.gridSync(TAG_COUNTY);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		this.destroy();
	}
	
	/*
	 * 读取默认方案
	 * */
	private void readDefaultScheme(String type)
	{
		try {
			Date dateNow = new Date();
			Integer hour = dateNow.getHours();
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", m_strIp, m_strPort, m_strDB), m_strUser, m_strPassword);
			Statement  stmt = conn.createStatement();
			String sql = String.format("select * from t_griddefaultscheme where type = '%s' and (startHour <= %d and %d < endHour or startHour <= %d and %d < endHour)", type, hour, hour, hour - 24, hour - 24);
			ResultSet resultSet = stmt.executeQuery(sql);
			m_defaultScheme = new ArrayList<Scheme>();
			while(resultSet.next()) {
				m_defaultScheme.add(new Scheme(resultSet.getString("type"), resultSet.getString("makeTime"), resultSet.getInt("startHour"), resultSet.getInt("endHour"), resultSet.getInt("forecastHour"), resultSet.getString("element"), resultSet.getString("model"), resultSet.getString("modelMakeTime"), resultSet.getInt("modelForecastHour"), resultSet.getString("hourspan"), resultSet.getDouble("defaultDataValue"), resultSet.getInt("valid")));
			}
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/*
	 * 格点同步，生成初始产品
	 * */
	private void gridSync(String type)
	{
		try {
			String level = "1000";
			String version = type.equals("prvn")?"r":"p";
			readDefaultScheme(type);
			if(m_defaultScheme != null && m_defaultScheme.size() > 0)
			{
				Date dateMake = null;
				Date dateForecast = null;
				Date dateNow = new Date();
				ArrayList<String> departCodes = this.getDepartCodeByType(type);			
				for(int i=0; i<m_defaultScheme.size(); i++) {
					int valid = m_defaultScheme.get(i).valid;
					if(valid == 0)
						continue;
					String makeTime = m_defaultScheme.get(i).makeTime;
					Integer startHour = m_defaultScheme.get(i).startHour;
					Integer endHour = m_defaultScheme.get(i).endHour;
					Integer forecastHour = m_defaultScheme.get(i).forecastHour;
					String element = m_defaultScheme.get(i).element;
					String model = m_defaultScheme.get(i).model;
					String modelMakeTime = m_defaultScheme.get(i).modelMakeTime;
					Integer modelForecastHour = m_defaultScheme.get(i).modelForecastHour;
					String hourspan = m_defaultScheme.get(i).hourspan;
					String[] arrayHourSpan = hourspan.split(",");
					double defaultDataValue = m_defaultScheme.get(i).defaultDataValue;
					
					//制作时间
					dateMake = new Date();
					if(startHour<0 && dateNow.getHours()>endHour){
						Calendar calendar = new GregorianCalendar(); 
					    calendar.setTime(dateMake); 
					    calendar.add(calendar.DATE, 1);  //生成下一天的产品
					    dateMake = calendar.getTime();
					}
					String[] arrayMakeTime = makeTime.split(":");
					dateMake.setHours(Integer.valueOf(arrayMakeTime[0]));
					dateMake.setMinutes(Integer.valueOf(arrayMakeTime[1]));
					dateMake.setSeconds(0);
					String strDateMake = new SimpleDateFormat("yyMMddHHmm").format(dateMake);
					String strDateMakeStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateMake);
					
					//预报时间
					dateForecast = new Date();
					if(startHour<0 && dateNow.getHours()>endHour){
						Calendar calendar = new GregorianCalendar(); 
					    calendar.setTime(dateForecast); 
					    calendar.add(calendar.DATE, 1);  //生成下一天的产品
					    dateForecast = calendar.getTime();
					}
					dateForecast.setHours(forecastHour);
					dateForecast.setMinutes(0);
					dateForecast.setSeconds(0);
					String strDateForecast = new SimpleDateFormat("yyMMddHH").format(dateForecast);
					String strDateForecastStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateForecast);
					
					//判断产品是否已订正
					ArrayList<String> arrayDepartModified = new ArrayList<String>(); //记录已订正的部门
					ArrayList<GridInfo> gridInfos = queryGridInfo(type, element, strDateMakeStandard, strDateForecastStandard); //获取全部产品信息，不用传部门进去查，因为有type了
					if(gridInfos.size() > 0){
						for(GridInfo gridInfo : gridInfos){
							if(!gridInfo.getUserName().equals("") || !gridInfo.getLastModifyTime().equals(""))
							{
								if(!arrayDepartModified.contains(gridInfo.getDepartCode()))
									arrayDepartModified.add(gridInfo.getDepartCode());
							}					
						}						
					}
					Boolean isProductModified = arrayDepartModified.size()>0;
					
					if(type.equals(TAG_PROVINCE) || (type.equals(TAG_CITY) || type.equals(TAG_COUNTY))&&!isProductModified) //一、省（自治区）台处理流程。如果市县产品未订正，则全部更新。
					{
						//如果（省台）产品已订正，则跳过。
						if(isProductModified){
							LogTool.logger.info(type+"_"+element+"产品已订正，跳过");
							continue;
						}
						
						//Date dateModel = this.getLastDateTime(model, element);						
						
						Date dateModel = null; //模式时间
						Date dateModelMake = null; //模式制作时间，主要针对省/区、市、县产品
						if(modelForecastHour != -999 && !modelMakeTime.equals("")) //取固定时次预报
						{
							Calendar calendar = new GregorianCalendar(); 
						    calendar.setTime(dateForecast); 
						    calendar.add(calendar.HOUR_OF_DAY, modelForecastHour);  //HOUR_OF_DAY：24小时制，HOUR：12小时制
						    dateModel = calendar.getTime();
						    
						    dateModelMake = calendar.getTime();
						    String[] arrayModelMakeTime = modelMakeTime.split(":");
						    dateModelMake.setHours(Integer.valueOf(arrayModelMakeTime[0]));
						    dateModelMake.setMinutes(Integer.valueOf(arrayModelMakeTime[1]));
						    dateModelMake.setSeconds(0);
						}
						else //取最新时次预报
						{
							dateModel = this.getLastDateTime(model, element);	
							dateModelMake = dateModel;
						}
						
						//if(dateModel != null)
						if(true)
						{
							int offsetHours = -1;
							if(dateModel != null){
								long diff = dateForecast.getTime() - dateModel.getTime();
								offsetHours = (int)(diff / (1000 * 60 * 60));
							}							
							
							for(int j=0; j<arrayHourSpan.length; j++){
								int nHourSpan = Integer.valueOf(arrayHourSpan[j]);
								int nHourSpanModel = nHourSpan + offsetHours;
								
								//2、判断模式和模式预报时间是否一致，如果一致则不用更新。
								Boolean bSame = false;
								if(dateModel != null)
								{									
									if(gridInfos.size() > 0)
									{	
										for(GridInfo gi : gridInfos){
											if(gi.getHourSpan() != nHourSpan)
												continue;
											if(gi.getNWPModel().equals(model) && gi.getNWPModelTime().equals(new SimpleDateFormat("yyMMddHH").format(dateModel)))
											{
												LogTool.logger.info(type+"_"+element+"数据来源相同，无需更新");
												bSame = true;
											}
											if((type.equals(TAG_CITY) || type.equals(TAG_COUNTY)))
											{
												if(!model.equals(TAG_PROVINCE) && gi.getNWPModel().equals(TAG_PROVINCE))//对于市台产品，如果模式来源于区台，则不用数值模式去更新
												{
													LogTool.logger.info(type+"_"+element+"已是区台指导报，不更新");
													bSame = true;
												}
											}	
											break;
										}
									}
//									if(bSame){
//										continue;
//									}
								}
								else //如果没有模式，而产品已存在，则不更新。比如空气污染气象条件等级预报
								{ 
									Boolean bExist = false;
									if(gridInfos.size() > 0)
									{	
										for(GridInfo gi : gridInfos){
											if(gi.getHourSpan() == nHourSpan)
											{
												bExist = true;
												break;
											}
										}
									}
									if(bExist)
										continue;
								}
								
								String datasetName = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), level);
								String datasetNameModel = null;
								if(dateModel != null){
									String strDateModel =  new SimpleDateFormat("yyMMddHH").format(dateModel);
									String strDateModelMake =  new SimpleDateFormat("yyMMddHHmm").format(dateModelMake);
									datasetNameModel = String.format("t_%s_%s_%s_p_%s_%s_%s", model, element, strDateModelMake, strDateModel, new DecimalFormat("000").format(nHourSpanModel), level);
								}
									
								ArrayList<String> uv = new ArrayList<String>(); 
								if(element.equals("10uv") || element.equals("wmax")){								
									uv.add("_u");
									uv.add("_v");
								}
								else{
									uv.add("");
								}
								for(int l=0; l<uv.size(); l++){
									String datasetNameTarget = datasetName+uv.get(l);
									String datasetNameSrc = null;
									Dataset dtModel = null;
									if(datasetNameModel != null)
									{
										datasetNameSrc = datasetNameModel+uv.get(l);
										dtModel = m_datasource.GetDataset(datasetNameSrc);
									}
									
//									if(dtModel == null){
//										LogTool.logger.info(datasetNameSrc+"不存在");
//									}
//									else{
									{
										Dataset dtTarget = m_datasource.GetDataset(datasetNameTarget);
										if(dtTarget != null)
										{
											if(dtModel == null)
											{
												LogTool.logger.info(datasetNameTarget+"：模式为空，跳过");
												continue;
											}
											if(bSame) //解决：通过元数据判断来源相同，但数据集可能不存在。放这里可避免这种情况
												continue;		
//											m_datasource.DeleteDataset(dtTarget.GetName()); //删除旧（过期）的初始场
//											LogTool.logger.info(datasetNameTarget+"过期已删除");
											LogTool.logger.info(datasetNameTarget+"已过期");
										}
										else{
											LogTool.logger.info(datasetNameTarget+"：目标数据集为空");
										}
										
										
										Rectangle2D rectangle2d = new Rectangle2D.Double(m_left, m_bottom, m_width, m_height);
										Double dDelta = m_dResolutionX;
										int cols = (int)Math.round(rectangle2d.getWidth()/dDelta);
										int rows = (int)Math.round(rectangle2d.getHeight()/dDelta);
										double noDataValue = -9999.0f;
										String valueType = "Single";
										DatasetRaster drModel = null;
										if(dtModel != null)
										{
											drModel = (DatasetRaster)dtModel;
											noDataValue = drModel.GetNoDataValue();
											valueType = drModel.GetValueType();
											if(cols != drModel.GetWidth() || rows != drModel.GetHeight()){
												LogTool.logger.info(datasetNameTarget+"：模式与目标场行列数不一致");
											}
										}
										
										DatasetRaster dr = null;
										if(dtTarget == null){				
											Rectangle2D rcBounds = rectangle2d;
											String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight());
											String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
													datasetNameTarget, valueType, cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, noDataValue);
											dr = m_datasource.CreateDatasetRaster(str);
										}
										else {
											dr = (DatasetRaster)dtTarget;
										}
										dr.Open();
										
										Scanline sl = new Scanline(dr.GetValueType(), cols);
										for (int k = 0; k<rows; k++)
							            {
											if(drModel == null){
												for(int m=0; m<cols; m++)
													sl.SetValue(m, defaultDataValue);
								                dr.SetScanline(0, k, sl);
											}
											else{
												drModel.GetScanline(0, k, sl);
								                dr.SetScanline(0, k, sl);	
											}											
							            }										
										dr.FlushCache();
										dr.CalcExtreme();
										sl.Destroy();
										
										
										//保存格点产品信息										
										String tabelName = datasetNameTarget;
										int totalHourSpan = Integer.valueOf(arrayHourSpan[arrayHourSpan.length - 1]);										
										//String modelTime = dateModel == null?"":new SimpleDateFormat("yyMMddHH").format(dateModel);
										String modelTime = dtModel == null?"":new SimpleDateFormat("yyMMddHH").format(dateModel); //这里必须通过dtModel判断，一是好理解，二是dateModel不会为空时dtModel可能空
										String lastModifyTime  = "";
										String remark = "";
										String userName = "";
										String forecaster = "";
										String issuer = "";
										int nlevel = level == "" ? 1000 : Integer.valueOf(level);
										
										List<GridInfo> gridproducts = new ArrayList<GridInfo>();
										for(String departCode:departCodes){
											GridInfo gridproduct = new GridInfo();
											gridproduct.setDepartCode(departCode);
											gridproduct.setType(type);
											gridproduct.setElement(element);
											gridproduct.setForecastTime(strDateForecastStandard);
											gridproduct.setHourSpan(nHourSpan);
											gridproduct.setTotalHourSpan(totalHourSpan);
											gridproduct.setLevel(nlevel);
											gridproduct.setVerstion(version);
											gridproduct.setTabelName(tabelName);
											gridproduct.setNWPModel(model);
											gridproduct.setNWPModelTime(modelTime);
											gridproduct.setUserName(userName);
											gridproduct.setForecaster(forecaster);
											gridproduct.setIssuer(issuer);
											gridproduct.setMakeTime(strDateMakeStandard);
											gridproduct.setLastModifyTime(lastModifyTime);
											gridproduct.setRemark(remark);
											gridproducts.add(gridproduct);
										}
										this.addGridInfo(gridproducts);
										
										LogTool.logger.info(datasetNameTarget+"已完成");
									}	
								}													
							}	
							process6hTo3h(type, element, strDateMake, version, strDateForecast, model, offsetHours);
						}
//						else{
//							LogTool.logger.info("未获取到"+model+"_"+element+"最新时间：");
//						}
					}			
					else //二、（有部分市县已订正情况下的）市县台处理流程：如果某市县已订正，则不更新该地区格点，其他市县需要更新
					{
						LogTool.logger.info("部分地区已订正，更新其他地区...");
						
						//获取已订正的地区边界
						if(arrayDepartModified.size() == departCodes.size()) //如果全部都订正了，则不用更新
							continue;
						ArrayList<GeoRegion> arrayArea = new ArrayList<GeoRegion>();
						for(String departCode:arrayDepartModified){
							GeoRegion geo = getGeoRegion(departCode);
							arrayArea.add(geo);
						}
						
						//Date dateModel = this.getLastDateTime(model, element);
						
						Date dateModel = null; //模式时间
						Date dateModelMake = null; //模式制作时间，主要针对省/区、市、县产品
						if(modelForecastHour != -999 && !modelMakeTime.equals("")) //取固定时次预报
						{
							Calendar calendar = new GregorianCalendar(); 
						    calendar.setTime(dateForecast); 
						    calendar.add(calendar.HOUR_OF_DAY, modelForecastHour);  //HOUR_OF_DAY：24小时制，HOUR：12小时制
						    dateModel = calendar.getTime();
						    
						    dateModelMake = calendar.getTime();
						    String[] arrayModelMakeTime = modelMakeTime.split(":");
						    dateModelMake.setHours(Integer.valueOf(arrayModelMakeTime[0]));
						    dateModelMake.setMinutes(Integer.valueOf(arrayModelMakeTime[1]));
						    dateModelMake.setSeconds(0);
						}
						else //取最新时次预报
						{
							dateModel = this.getLastDateTime(model, element);	
							dateModelMake = dateModel;
						}
						
						if(dateModel != null)
						{
							//2、判断模式和模式预报时间是否一致，如果一致则不用更新。
							//3、判断模式是否来源区台prvn，说明是区台指导报，如果是则不用数值模式去更新。
							Boolean isSame = false;
							String nwpModel = "";
							if(gridInfos.size() > 0){
								for(GridInfo gi:gridInfos){
									if(!arrayDepartModified.contains(gi.getDepartCode())){
										if(gi.getNWPModel().equals(model) && gi.getNWPModelTime().equals(new SimpleDateFormat("yyMMddHH").format(dateModel)))
											isSame = true;
										break;
									}
								}
							}
							if(isSame)
								continue;
							if(!model.equals(TAG_PROVINCE) && nwpModel.equals(TAG_PROVINCE))
								continue;
							
							Analyst pAnalyst = Analyst.CreateInstance("SpatialRel", m_workspace);				            
				            pAnalyst.SetPropertyValue("SpatialRel", "Contain");
							
							long diff = dateForecast.getTime() - dateModel.getTime();
							int offsetHours = (int)(diff / (1000 * 60 * 60));
							
							for(int j=0; j<arrayHourSpan.length; j++){
								int nHourSpan = Integer.valueOf(arrayHourSpan[j]);
								int nHourSpanModel = nHourSpan + offsetHours;
								String datasetName = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), level);
								String strDateModel = new SimpleDateFormat("yyMMddHH").format(dateModel);
								String strDateModelMake =  new SimpleDateFormat("yyMMddHH00").format(dateModelMake);
								String datasetNameModel = String.format("t_%s_%s_%s_p_%s_%s_%s", model, element, strDateModelMake, strDateModel, new DecimalFormat("000").format(nHourSpanModel), level);
								ArrayList<String> uv = new ArrayList<String>(); 
								if(element.equals("10uv") || element.equals("wmax")){								
									uv.add("_u");
									uv.add("_v");
								}
								else{
									uv.add("");
								}
								for(int l=0; l<uv.size(); l++){
									String datasetNameTarget = datasetName+uv.get(l);
									String datasetNameSrc = datasetNameModel+uv.get(l);
									Dataset dt = m_datasource.GetDataset(datasetNameTarget);
									
									if(dt != null)
									{
										DatasetRaster dr = (DatasetRaster)dt;
										Dataset dtModel = m_datasource.GetDataset(datasetNameSrc);	
										if(dtModel == null){
											LogTool.logger.info(datasetNameSrc+"不存在");
										}
										else{
											DatasetRaster drModel = (DatasetRaster)dtModel;
											
											int height = drModel.GetHeight();
											int width = drModel.GetWidth();
											Scanline sl = new Scanline(drModel.GetValueType(), width);
											for (int k = 0; k<height; k++)
								            {
												drModel.GetScanline(0, k, sl);
								                //dr.SetScanline(0, k, sl);
												for(int m=0; m<width; m++)
												{
													//判断此格点是否已订正
													Boolean isModified = false;
													//Point2D pt2d = drModel.CellToPoint(new Point(m, k));
													Point2D pt2d = drModel.CellToPoint(new Point2D.Double(m, k));
													for(GeoRegion area:arrayArea){
														Rectangle2D bounds = area.GetBounds();
														if(pt2d.getX() < bounds.getMinX() || pt2d.getX() > bounds.getMaxX() ||
																pt2d.getY() < bounds.getMinY() || bounds.getY() > bounds.getMaxY())
															continue;
														String str = String.format("\"Geometry\":\"%X\"", area.GetHandle());
											            pAnalyst.SetPropertyValue("A", "{" + str + "}");
									                    GeoPoint geoPoint = new GeoPoint(pt2d.getX(), pt2d.getY());
									                    str = String.format("\"Geometry\":\"%X\"", geoPoint.GetHandle());
									                    pAnalyst.SetPropertyValue("B", "{" + str + "}");
									                    pAnalyst.Execute();
									                    String strOutput = pAnalyst.GetPropertyValue("Output");
									                    if(strOutput.equals("true")){
									                    	isModified = true;
									                    	break;
									                    }
									                    	
													}
													//如果未订正，则更新；否则，不更新
													if(!isModified) 
														dr.SetValue(m, k, sl.GetValue(m));
												}
								            }
											dr.FlushCache();
											dr.CalcExtreme();
											sl.Destroy();
											
											//保存格点产品信息										
											String tabelName = datasetNameTarget;
											int totalHourSpan = Integer.valueOf(arrayHourSpan[arrayHourSpan.length - 1]);
											//String modelTime = new SimpleDateFormat("yyMMddHH").format(dateModel);
											String modelTime = dtModel == null?"":new SimpleDateFormat("yyMMddHH").format(dateModel); //这里必须通过dtModel判断，一是好理解，二是dateModel不会为空时dtModel可能空
											String lastModifyTime  = "";
											String remark = "";
											String userName = "";
											String forecaster = "";
											String issuer = "";
											int nlevel = level == "" ? 1000 : Integer.valueOf(level);
											
											List<GridInfo> gridproducts = new ArrayList<GridInfo>();
											for(String departCode:departCodes){
												if(arrayDepartModified.contains(departCode))
													continue;
												GridInfo gridproduct = new GridInfo();
												gridproduct.setDepartCode(departCode);
												gridproduct.setType(type);
												gridproduct.setElement(element);
												gridproduct.setForecastTime(strDateForecastStandard);
												gridproduct.setHourSpan(nHourSpan);
												gridproduct.setTotalHourSpan(totalHourSpan);
												gridproduct.setLevel(nlevel);
												gridproduct.setVerstion(version);
												gridproduct.setTabelName(tabelName);
												gridproduct.setNWPModel(model);
												gridproduct.setNWPModelTime(modelTime);
												gridproduct.setUserName(userName);
												gridproduct.setForecaster(forecaster);
												gridproduct.setIssuer(issuer);
												gridproduct.setMakeTime(strDateMakeStandard);
												gridproduct.setLastModifyTime(lastModifyTime);
												gridproduct.setRemark(remark);
												gridproducts.add(gridproduct);
											}
											this.addGridInfo(gridproducts);
											
											LogTool.logger.info(datasetNameTarget+"已局部更新");
										}
									}	
								}													
							}
							pAnalyst.Destroy();
							process6hTo3h(type, element, strDateMake, version, strDateForecast, model, offsetHours);
						}
					}
				}
				
				if(dateMake != null && dateForecast != null)
					syncWeather(type, dateMake, dateForecast, level);
			}
		}
		catch(Exception e)
		{
			LogTool.logger.error(e.getMessage());
		}		
	}
	
	/*
	 * 天气要素
	 * */
	private void syncWeather(String type,Date dateMake, Date dateForecast, String level)
	{
		String element = "w";
		String elementTCC = "tcc";
		String elementR12 = "r12";
		int[] arrayHourSpan = {12,24,36,48,60,72,84,96,108,120,132,144,156,168};
		
		String strDateMake = new SimpleDateFormat("yyMMddHHmm").format(dateMake);
		String strDateMakeStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateMake);
		String strDateForecast = new SimpleDateFormat("yyMMddHH").format(dateForecast);		
		String strDateForecastStandard = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateForecast);
		
		ArrayList<String> departCodes = this.getDepartCodeByType(type);
		//判断产品是否已订正
		ArrayList<String> arrayDepartModified = new ArrayList<String>(); //记录已订正的部门
		ArrayList<GridInfo> gridInfos = queryGridInfo(type, element, strDateMakeStandard, strDateForecastStandard); //获取全部产品信息，不用传部门进去查，因为有type了
		if(gridInfos.size() > 0){
			for(GridInfo gridInfo : gridInfos){
				if(!gridInfo.getUserName().equals("") || !gridInfo.getLastModifyTime().equals(""))
				{
					if(!arrayDepartModified.contains(gridInfo.getDepartCode()))
						arrayDepartModified.add(gridInfo.getDepartCode());
				}					
			}						
		}
		Boolean isProductModified = arrayDepartModified.size()>0;
		
		ArrayList<GridInfo> gridInfosR12 = queryGridInfo(type,elementR12, strDateMakeStandard, strDateForecastStandard); //获取参考要素产品信息，对比时间是否需要更新
		
		if(type.equals(TAG_PROVINCE) || (type.equals(TAG_CITY) || type.equals(TAG_COUNTY))&&!isProductModified) //一、省（自治区）台处理流程。如果市县产品未订正，则全部更新。
		{
			//如果（省台）产品已订正，则跳过。
			if(isProductModified){
				LogTool.logger.info(type+"_"+element+"产品已订正，跳过");
				return;
			}
			
			for(int j=0; j<arrayHourSpan.length; j++){
				int nHourSpan = arrayHourSpan[j];				
				//判断时间是否一致，如果一致则不用更新。
				String model = "";
				String dateModel = "";
				for(GridInfo giR12:gridInfosR12){
					if(giR12.getHourSpan() == nHourSpan){
						model = giR12.getNWPModel();
						dateModel = giR12.getNWPModelTime();
						break;
					}
				}
				if(model.equals("") || dateModel.equals("")){
					LogTool.logger.info(type+"天气更新错误：未查询到降水或云量模式时间");
					continue;
				}
				
				Boolean bSame = false;
				if(gridInfos.size() > 0)
				{	
					for(GridInfo gi : gridInfos){
						if(gi.getHourSpan() == nHourSpan)
						{							
							if(gi.getNWPModel().equals(model) && gi.getNWPModelTime().equals(dateModel))
							{
								LogTool.logger.info(type+"_"+element+"数据来源相同，无需更新");
								bSame = true;
							}
							if((type.equals(TAG_CITY) || type.equals(TAG_COUNTY)))
							{
								if(!model.equals(TAG_PROVINCE) && gi.getNWPModel().equals(TAG_PROVINCE))//对于市台产品，如果模式来源于区台，则不用数值模式去更新
								{
									LogTool.logger.info(type+"_"+element+"已是区台指导报，不更新");
									bSame = true;
								}
							}	
							break;
						}
					}
				}
				if(bSame)
					continue;				
				
				String version = type.equals("prvn")?"r":"p";
				String datasetNameTarget = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), level);
				String datasetNameR12 = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, elementR12, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), level);
				String datasetNameTCC = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, elementTCC, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), level);
				Dataset dtTarget = m_datasource.GetDataset(datasetNameTarget);
				Dataset dtR12 = m_datasource.GetDataset(datasetNameR12);
				Dataset dtTCC = m_datasource.GetDataset(datasetNameTCC);
				if(dtR12 == null)
				{
					LogTool.logger.info(datasetNameR12+"不存在");
					continue;
				}
				if(dtTCC == null)
				{
					LogTool.logger.info(datasetNameTCC+"不存在");
					continue;
				}
				if(dtTarget != null)
				{
//					m_datasource.DeleteDataset(dtTarget.GetName()); //删除旧（过期）的初始场
//					LogTool.logger.info(datasetNameTarget+"过期已删除");
					LogTool.logger.info(datasetNameTarget+"已过期");
				}
				
				DatasetRaster drR12 = (DatasetRaster)dtR12;
				DatasetRaster drTCC = (DatasetRaster)dtTCC;
				Rectangle2D rcBounds = drR12.GetBounds();
				String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight());
				String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
						datasetNameTarget, drR12.GetValueType(), drR12.GetWidth(), drR12.GetHeight(), "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, drR12.GetNoDataValue());
				DatasetRaster drTarget = null;
				if(dtTarget == null)
					drTarget = m_datasource.CreateDatasetRaster(str);
				else
					drTarget = (DatasetRaster)dtTarget;
				drTarget.Open();
				
				double noDataValue = drR12.GetNoDataValue();
				int rows = drTarget.GetHeight();
				int cols = drTarget.GetWidth();
				Scanline slTarget = new Scanline(drTarget.GetValueType(), drTarget.GetWidth());
				Scanline slR12 = new Scanline(drR12.GetValueType(), drR12.GetWidth());
				Scanline slTCC = new Scanline(drTCC.GetValueType(), drTCC.GetWidth());
				for (int k = 0; k<rows; k++)
	            {
					drR12.GetScanline(0, k, slR12);
					drTCC.GetScanline(0, k, slTCC);
					for(int l=0; l<cols; l++)
					{
						double dValueR12 = slR12.GetValue(l);
						double dValueTCC = slTCC.GetValue(l);
						double dValueTarget = getWeatherCode(noDataValue, dValueR12, dValueTCC);
						slTarget.SetValue(l, dValueTarget);
					}
	                drTarget.SetScanline(0, k, slTarget);
	            }
				drTarget.FlushCache();
				drTarget.CalcExtreme();
				slTarget.Destroy();
				slR12.Destroy();
				slTCC.Destroy();
				
				
				//保存格点产品信息										
				String tabelName = datasetNameTarget;
				int totalHourSpan = Integer.valueOf(arrayHourSpan[arrayHourSpan.length - 1]);
				String modelTime = dateModel;
				String lastModifyTime  = "";
				String remark = "";
				String userName = "";
				String forecaster = "";
				String issuer = "";
				int nlevel = level == "" ? 1000 : Integer.valueOf(level);
				
//				for(String departCode : departCodes){
//					addGridInfo(departCode, type, element, forecastTime, nHourSpan, totalHourSpan, nlevel, tabelName, model, modelTime, userName, forecaster, issuer, makeTime, lastModifyTime, remark);
//				}	
				List<GridInfo> gridproducts = new ArrayList<GridInfo>();
				for(String departCode:departCodes){
					GridInfo gridproduct = new GridInfo();
					gridproduct.setDepartCode(departCode);
					gridproduct.setType(type);
					gridproduct.setElement(element);
					gridproduct.setForecastTime(strDateForecastStandard);
					gridproduct.setHourSpan(nHourSpan);
					gridproduct.setTotalHourSpan(totalHourSpan);
					gridproduct.setLevel(nlevel);
					gridproduct.setVerstion(version);
					gridproduct.setTabelName(tabelName);
					gridproduct.setNWPModel(model);
					gridproduct.setNWPModelTime(modelTime);
					gridproduct.setUserName(userName);
					gridproduct.setForecaster(forecaster);
					gridproduct.setIssuer(issuer);
					gridproduct.setMakeTime(strDateMakeStandard);
					gridproduct.setLastModifyTime(lastModifyTime);
					gridproduct.setRemark(remark);
					gridproducts.add(gridproduct);
				}
				this.addGridInfo(gridproducts);
				
				LogTool.logger.info(datasetNameTarget+"已完成");
			}
		}
		else //二、（有部分市县已订正情况下的）市县台处理流程：如果某市县已订正，则不更新该地区格点，其他市县需要更新
		{
			LogTool.logger.info("部分地区已订正，更新其他地区...");
			
			//获取已订正的地区边界
			if(arrayDepartModified.size() == departCodes.size()) //如果全部都订正了，则不用更新
				return;
			ArrayList<GeoRegion> arrayArea = new ArrayList<GeoRegion>();
			for(String departCode:arrayDepartModified){
				GeoRegion geo = getGeoRegion(departCode);
				arrayArea.add(geo);
			}
			
			Analyst pAnalyst = Analyst.CreateInstance("SpatialRel", m_workspace);				            
            pAnalyst.SetPropertyValue("SpatialRel", "Contain");			
			
			for(int j=0; j<arrayHourSpan.length; j++){
				int nHourSpan = arrayHourSpan[j];				
				//判断时间是否一致，如果一致则不用更新。
				//判断模式是否来源区台prvn，说明是区台指导报，如果是则不用数值模式去更新。
				String model = "";
				String dateModel = "";
				for(GridInfo giR12:gridInfosR12){
					if(giR12.getHourSpan() == nHourSpan){
						model = giR12.getNWPModel();
						dateModel = giR12.getNWPModelTime();
						break;
					}
				}
				if(model.equals("") || dateModel.equals("")){
					LogTool.logger.info(type+"天气更新错误：未查询到降水或云量模式时间");
					continue;
				}
				
				Boolean isSame = false;
				String nwpModel = "";
				if(gridInfos.size() > 0){
					for(GridInfo gi:gridInfos){
						if(!arrayDepartModified.contains(gi.getDepartCode())){							
							if(gi.getNWPModel().equals(model) && gi.getNWPModelTime().equals(dateModel))
								isSame = true;
							break;
						}
					}
				}
				if(isSame)
					continue;
				if(!model.equals(TAG_PROVINCE) && nwpModel.equals(TAG_PROVINCE))
					continue;
				
				String version = type.equals("prvn")?"r":"p";
				String datasetNameTarget = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), level);
				String datasetNameR12 = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, elementR12, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), level);
				String datasetNameTCC = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, elementTCC, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), level);
				Dataset dtTarget = m_datasource.GetDataset(datasetNameTarget);
				Dataset dtR12 = m_datasource.GetDataset(datasetNameR12);
				Dataset dtTCC = m_datasource.GetDataset(datasetNameTCC);
				if(dtTarget == null)
				{
					LogTool.logger.info(datasetNameTarget+"不存在");
					continue;
				}
				if(dtR12 == null)
				{
					LogTool.logger.info(datasetNameR12+"不存在");
					continue;
				}
				if(dtTCC == null)
				{
					LogTool.logger.info(datasetNameTCC+"不存在");
					continue;
				}
								
				DatasetRaster drTarget = (DatasetRaster)dtTarget;
				DatasetRaster drR12 = (DatasetRaster)dtR12;
				DatasetRaster drTCC = (DatasetRaster)dtTCC;
				
				double noDataValue = drTarget.GetNoDataValue();
				int height = drTarget.GetHeight();
				int width = drTarget.GetWidth();
				Scanline slTarget = new Scanline(drTarget.GetValueType(), width);
				Scanline slR12 = new Scanline(drR12.GetValueType(), width);
				Scanline slTCC = new Scanline(drTCC.GetValueType(), width);
				for (int k = 0; k<height; k++)
	            {
					drR12.GetScanline(0, k, slR12);
					drTCC.GetScanline(0, k, slTCC);
					for(int m=0; m<width; m++)
					{
						//判断此格点是否已订正
						Boolean isModified = false;
						//Point2D pt2d = drTarget.CellToPoint(new Point(m, k));
						Point2D pt2d = drTarget.CellToPoint(new Point2D.Double(m, k));
						for(GeoRegion area:arrayArea){
							Rectangle2D bounds = area.GetBounds();
							if(pt2d.getX() < bounds.getMinX() || pt2d.getX() > bounds.getMaxX() ||
									pt2d.getY() < bounds.getMinY() || bounds.getY() > bounds.getMaxY())
								continue;
							String str = String.format("\"Geometry\":\"%X\"", area.GetHandle());
				            pAnalyst.SetPropertyValue("A", "{" + str + "}");
		                    GeoPoint geoPoint = new GeoPoint(pt2d.getX(), pt2d.getY());
		                    str = String.format("\"Geometry\":\"%X\"", geoPoint.GetHandle());
		                    pAnalyst.SetPropertyValue("B", "{" + str + "}");
		                    pAnalyst.Execute();
		                    String strOutput = pAnalyst.GetPropertyValue("Output");
		                    if(strOutput.equals("true")){
		                    	isModified = true;
		                    	break;
		                    }
		                    	
						}
						//如果未订正，则更新；否则，不更新
						if(!isModified) 
						{
							double dValueR12 = slR12.GetValue(m);
							double dValueTCC = slTCC.GetValue(m);
							double dValueTarget =getWeatherCode(noDataValue, dValueR12, dValueTCC);
							slTarget.SetValue(m, dValueTarget);
						}
					}
					drTarget.SetScanline(0, k, slTarget);
	            }
				drTarget.FlushCache();
				drTarget.CalcExtreme();
				slTarget.Destroy();
				slR12.Destroy();
				slTCC.Destroy();
				
				
				//保存格点产品信息										
				String tabelName = datasetNameTarget;
				int totalHourSpan = Integer.valueOf(arrayHourSpan[arrayHourSpan.length - 1]);
				String modelTime = dateModel;
				String lastModifyTime  = "";
				String remark = "";
				String userName = "";
				String forecaster = "";
				String issuer = "";
				int nlevel = level == "" ? 1000 : Integer.valueOf(level);
				
//				for(String departCode : departCodes)
//				{
//					if(arrayDepartModified.contains(departCode))
//						continue;
//					addGridInfo(departCode, type, element, forecastTime, nHourSpan, totalHourSpan, nlevel, tabelName, model, modelTime, userName, forecaster, issuer, makeTime, lastModifyTime, remark);
//				}
				List<GridInfo> gridproducts = new ArrayList<GridInfo>();
				for(String departCode:departCodes){
					if(arrayDepartModified.contains(departCode))
						continue;
					GridInfo gridproduct = new GridInfo();
					gridproduct.setDepartCode(departCode);
					gridproduct.setType(type);
					gridproduct.setElement(element);
					gridproduct.setForecastTime(strDateForecastStandard);
					gridproduct.setHourSpan(nHourSpan);
					gridproduct.setTotalHourSpan(totalHourSpan);
					gridproduct.setLevel(nlevel);
					gridproduct.setVerstion(version);
					gridproduct.setTabelName(tabelName);
					gridproduct.setNWPModel(model);
					gridproduct.setNWPModelTime(modelTime);
					gridproduct.setUserName(userName);
					gridproduct.setForecaster(forecaster);
					gridproduct.setIssuer(issuer);
					gridproduct.setMakeTime(strDateMakeStandard);
					gridproduct.setLastModifyTime(lastModifyTime);
					gridproduct.setRemark(remark);
					gridproducts.add(gridproduct);
				}
				this.addGridInfo(gridproducts);
				
				LogTool.logger.info(datasetNameTarget+"已局部更新");
			}
			pAnalyst.Destroy();
		}
	}
	
	//6小时转3小时。哎，这个只能这样特殊处理了。
	private void process6hTo3h(String type,String element,String strDateMake,String version,String strDateForecast, String model, int offsetHours)
	{
		try {
			if(!model.equals("ec"))
				return;
			if(!element.equals("r3") && !element.equals("2t") &&!element.equals("rh") &&!element.equals("10uv") &&!element.equals("tcc"))
				return;
			if(offsetHours != 12 && offsetHours != 24)
				return;
			
			List<Integer> arrayHourSpan = new ArrayList<Integer>();
			if(offsetHours==12){
				arrayHourSpan.add(63);
				arrayHourSpan.add(69);
			}	
			else if(offsetHours==24)
			{
				arrayHourSpan.add(51);
				arrayHourSpan.add(57);
				arrayHourSpan.add(63);
				arrayHourSpan.add(69);
			}
			else
				return;
			
			if(element.equals("10uv")){
				for(Integer nHourSpan:arrayHourSpan){
					String datasetName1_u = String.format("t_%s_%s_%s_%s_%s_%s_%s_u", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), "1000");
					String datasetName2_u = String.format("t_%s_%s_%s_%s_%s_%s_%s_u", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan+3), "1000");
					String datasetName1_v = String.format("t_%s_%s_%s_%s_%s_%s_%s_v", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), "1000");
					String datasetName2_v = String.format("t_%s_%s_%s_%s_%s_%s_%s_v", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan+3), "1000");
					Dataset dt1_u = m_datasource.GetDataset(datasetName1_u);
					Dataset dt2_u = m_datasource.GetDataset(datasetName2_u);
					Dataset dt1_v = m_datasource.GetDataset(datasetName1_v);
					Dataset dt2_v = m_datasource.GetDataset(datasetName2_v);
					if(dt1_u == null || dt1_u == null || dt1_v == null || dt1_v == null)
						continue;
					DatasetRaster dg1_u = (DatasetRaster)dt1_u;
					DatasetRaster dg2_u = (DatasetRaster)dt2_u;
					DatasetRaster dg1_v = (DatasetRaster)dt1_v;
					DatasetRaster dg2_v = (DatasetRaster)dt2_v;
					dg1_u.CalcExtreme();
					dg1_v.CalcExtreme();
					dg2_u.CalcExtreme();
					dg2_v.CalcExtreme();
					double min1 = dg1_u.GetMinValue();
					double max1 = dg1_u.GetMaxValue();
					double min2 = dg2_u.GetMinValue();
					double max2 = dg2_u.GetMaxValue();
					if(min1==0 && max1==0 && !(min2==0 && max2==0))
					{
//						//因为行列可能不同，所以删除重新创建
//						m_datasource.DeleteDataset(datasetName1_u);
//						String valueType = dg2_u.GetValueType();
//						int cols = dg2_u.GetWidth();
//						int rows = dg2_u.GetHeight();
//						double noDataValue = dg2_u.GetNoDataValue();
//						Rectangle2D rcBounds = dg2_u.GetBounds();
//						String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight()); //左 上 宽 高
//						String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
//								datasetName1_u, "Single", cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, noDataValue);
//						dg1_u = m_datasource.CreateDatasetRaster(str);
//						dg1_u.Open();
//						
//						m_datasource.DeleteDataset(datasetName1_v);
//						rcBounds = dg2_v.GetBounds();
//						strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight()); //左 上 宽 高
//						str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
//								datasetName1_v, "Single", cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, noDataValue);
//						dg1_v = m_datasource.CreateDatasetRaster(str);
//						dg1_v.Open();
						
						int rows = dg2_u.GetHeight();
						
						Scanline sl2_u = new Scanline(dg2_u.GetValueType(), dg2_u.GetWidth());
						Scanline sl2_v = new Scanline(dg2_v.GetValueType(), dg2_v.GetWidth());
						for(int i=0; i<rows; i++){
							dg2_u.GetScanline(0, i, sl2_u);
							dg1_u.SetScanline(0, i, sl2_u);
							dg2_v.GetScanline(0, i, sl2_v);							
							dg1_v.SetScanline(0, i, sl2_v);
						}						
						dg1_u.FlushCache();
						dg1_u.CalcExtreme();	
						dg1_v.FlushCache();
						dg1_v.CalcExtreme();
						sl2_u.Destroy();
						sl2_v.Destroy();
					}
					LogTool.logger.info(datasetName1_u+"完成6小时转3小时");
					LogTool.logger.info(datasetName1_v+"完成6小时转3小时");
				}		
			}
			else {
				for(Integer nHourSpan:arrayHourSpan){
					String datasetName1 = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan), "1000");
					String datasetName2 = String.format("t_%s_%s_%s_%s_%s_%s_%s", type, element, strDateMake, version, strDateForecast, new DecimalFormat("000").format(nHourSpan+3), "1000");
					Dataset dt1 = m_datasource.GetDataset(datasetName1);
					Dataset dt2 = m_datasource.GetDataset(datasetName2);
					if(dt1 == null || dt1 == null)
						continue;
					DatasetRaster dg1 = (DatasetRaster)dt1;
					DatasetRaster dg2 = (DatasetRaster)dt2;
					dg1.CalcExtreme();
					dg2.CalcExtreme();
					double min1 = dg1.GetMinValue();
					double max1 = dg1.GetMaxValue();
					double min2 = dg2.GetMinValue();
					double max2 = dg2.GetMaxValue();
					if(min1==0 && max1==0 && !(min2==0 && max2==0))
					{
						//因为行列可能不同，所以删除重新创建
						m_datasource.DeleteDataset(datasetName1);
						String valueType = dg2.GetValueType();
						int cols = dg2.GetWidth();
						int rows = dg2.GetHeight();
						double noDataValue = dg2.GetNoDataValue();
						Rectangle2D rcBounds = dg2.GetBounds();
						String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight()); //左 上 宽 高
						String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
								datasetName1, dg2.GetValueType(), cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, noDataValue);
						dg1 = m_datasource.CreateDatasetRaster(str);
						dg1.Open();
						dg2.Open();
						
						
						int method = 0;
						if(element.equals("r3"))
							method = 0;	//均分
						else if(element.equals("2t") || element.equals("rh") || element.equals("10uv") || element.equals("tcc"))
							method = 1;	//拷贝
						
						Scanline sl1 = new Scanline(dg1.GetValueType(), dg1.GetWidth());
						Scanline sl2 = new Scanline(dg2.GetValueType(), dg2.GetWidth());
						for(int i=0; i<rows; i++){
							dg2.GetScanline(0, i, sl2);
							if(method == 0)
							{
								dg1.GetScanline(0, i, sl1);
								for(int j=0; j<cols; j++){
									double d2 = sl2.GetValue(j);
									double d1 = noDataValue;
									if(d2 != noDataValue)
									{
										d1 = Math.round(d2/2.0*10.0)/10.0;
										d2 = d1;
									}
									sl1.SetValue(j, d1);
									sl2.SetValue(j, d2);
								}
								dg1.SetScanline(0, i, sl1);
								dg2.SetScanline(0, i, sl2);
							}
							else if(method == 1){
								dg1.SetScanline(0, i, sl2);
							}
						}
						if(method == 0)
						{
							dg1.FlushCache();
							dg1.CalcExtreme();
							dg2.FlushCache();
							dg2.CalcExtreme();
						}
						else if(method == 1){
							dg1.FlushCache();
							dg1.CalcExtreme();
						}	
						sl1.Destroy();
						sl2.Destroy();
					}
					LogTool.logger.info(datasetName1+"完成6小时转3小时");
				}		
			}				
		} catch (Exception e) {
			LogTool.logger.info("6小时转3小时错误："+e.getMessage());
		}		
	}
	
	private double getWeatherCode(double noDataValue, double dValueR12, double dValueTCC)
	{
		double dValueTarget = noDataValue;
		if(dValueR12 != noDataValue && dValueR12>0.0)
		{
			//按24小时标准
			if(dValueR12<=0.1) //微量降雨，按小雨处理
				dValueTarget = 7.0;
			else if(dValueR12<10.0) //小雨
				dValueTarget = 7.0;
			else if(dValueR12<25.0) //中雨
				dValueTarget = 8.0;
			else if(dValueR12<50.0) //大雨
				dValueTarget = 9.0;
			else if(dValueR12<100.0) //暴雨
				dValueTarget = 10.0;
			else if(dValueR12<250.0) //大暴雨
				dValueTarget = 11.0;
			else //特大暴雨
				dValueTarget = 12.0;
		}
		else { //否则根据总云量换算，晴0=[0-30]，多云1=(30,70]，阴2=(70-100]
			if(dValueTCC != noDataValue)
			{
				if(dValueTCC > 7.0)
					dValueTarget = 2.0;
				else if (dValueTCC > 3.0)
					dValueTarget = 1.0;
				else
					dValueTarget = 0.0;
			}
		}
		return dValueTarget;
	}
	
	/**
	 * 添加按照Mybatis的方式
	 * @param gridproducts
	 */
	public void addGridInfo(List<GridInfo> gridproducts){
//		long start = System.currentTimeMillis();
		SqlSession sqlSession = null;
		sqlSession = SqlSessionUtil.getSqlSession();
		try {
			GridProductMapper gridProductMapper = sqlSession.getMapper(GridProductMapper.class);
			// 在同一个事务里，所以不用担心数据会被改错。
			gridProductMapper.deleteGridProducts(gridproducts);
			gridProductMapper.addGridProducts(gridproducts);
			sqlSession.commit();
		} catch(Exception e) {
			sqlSession.rollback();
			e.printStackTrace();
		} finally {
			sqlSession.close();
//			long end = System.currentTimeMillis();
//			LogTool.logger.info("花费时间：" + (end - start));
		}
	}
		
	/*
	 * 查询格点信息
	 * */
	private ArrayList<GridInfo> queryGridInfo(String type,String element,String makeTime,String forecastTime){
		ArrayList<GridInfo> result = new ArrayList<GridInfo>();
		try {
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", m_strIp, m_strPort, m_strDB), m_strUser, m_strPassword);
			Statement  stmt = conn.createStatement();
			String sql = String.format("select * from t_gridproduct where type='%s' and element='%s' and makeTime='%s' and forecastTime='%s'", type, element, makeTime, forecastTime);
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()) {
				GridInfo gi = new GridInfo(); 
				gi.setId(resultSet.getInt("id"));
				gi.setDepartCode(resultSet.getString("departCode"));
				gi.setType(resultSet.getString("type"));
				gi.setElement(resultSet.getString("element"));
				gi.setForecastTime(resultSet.getString("forecastTime"));
				gi.setHourSpan(resultSet.getInt("hourSpan"));
				gi.setTotalHourSpan(resultSet.getInt("totalHourSpan"));
				gi.setLevel(resultSet.getInt("level"));
				gi.setTabelName(resultSet.getString("tabelName"));
				gi.setNWPModel(resultSet.getString("nwpModel"));
				gi.setNWPModelTime(resultSet.getString("nwpModelTime"));
				gi.setUserName(resultSet.getString("userName"));
				gi.setForecaster(resultSet.getString("forecaster"));
				gi.setIssuer(resultSet.getString("issuer"));
				gi.setMakeTime(resultSet.getString("makeTime"));
				gi.setLastModifyTime(resultSet.getString("lastModifyTime"));
				gi.setRemark(resultSet.getString("remark"));
				result.add(gi);
			}
			stmt.close();
			conn.close();
		 } 
		 catch (Exception e){
			 e.printStackTrace();
		 } 
		 return result;
	}
	
	/*
	 * （根据MySQL的数据源元数据表）获取数据集名
	 * */
	private ArrayList<String> getDatasetNames(String datasetNamePrefix){
		ArrayList<String> result = new ArrayList<String>();
		try {
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", m_strIp, m_strPort, m_strDB), m_strUser,m_strPassword);
			Statement  stmt = conn.createStatement();
			String sql = String.format("select MGName from mgdatasetrasterinfo where MGName like '%s%%'", datasetNamePrefix);
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()) {
				result.add(resultSet.getString("MGName"));
			}
			stmt.close();
			conn.close();
		 } 
		 catch (Exception e){
			 e.printStackTrace();
		 } 
		 return result;
	}
	
	/*
	 *获取数值模式最新预报时间
	 * */
	private Date getLastDateTime(String type, String element)
	{
		String strStartWith = String.format("t_%s_%s", type, element);
		ArrayList<String> datasetNamesArrayList = this.getDatasetNames(strStartWith);
		String strLastDateTime = null;
		int nIndexDateTime = 5; //时次索引，对于数值模式产品制作时间和预报时间是相同的
		for(Integer i=0; i<datasetNamesArrayList.size(); i++)
		{
			String strDatasetName = datasetNamesArrayList.get(i).toLowerCase();
			if(strDatasetName.startsWith(strStartWith)) //这里过滤
			{
				String[] strs = strDatasetName.split("_");
				if(strs != null && strs.length >= 8)
				{			
					String strDateTime = strs[nIndexDateTime];
					if(strLastDateTime == null || strLastDateTime.compareTo(strDateTime) < 0)
						strLastDateTime = strDateTime;
				}
			}
		}
		
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMddHH:mm:ss");
		Date date = null;
		if(strLastDateTime != null){
			try {
				strLastDateTime = "20"+strLastDateTime+":00:00";
				//LogTool.logger.info(strLastDateTime); //test
				date = sdf.parse(strLastDateTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}		
		return date;
	}
	
	/*
	 * 获取所有部门
	 * */
	private ArrayList<String> getDeparts()
	{
		ArrayList<String> result = new ArrayList<String>();
		try{
			Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", m_strIp, m_strPort, m_strDB), m_strUser, m_strPassword);
			Statement  stmt = conn.createStatement();
			String sql = String.format("select * from t_depart");
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()) {
				result.add(resultSet.getString("DepartCode"));
			}
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return result;
	}
		
	private ArrayList<String> getDepartCodeByType(String type)
	{
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> arrayDepart = this.getDeparts();
		int codeLength = -1;
		if(type.equals(TAG_PROVINCE)){
			codeLength = 2;
		}
		else if(type.equals(TAG_CITY)){
			codeLength = 4;
		}
		else if(type.equals(TAG_COUNTY)){
			codeLength = 6;
		}
		for(String departCode : arrayDepart){
			if(departCode.length() == codeLength)
				result.add(departCode);
		}
		return result;
	}
	
	private GeoRegion getGeoRegion(String areaCode){
		GeoRegion result = null;
		try {			
			String strSHPFileName = "";
			if(areaCode.length() == 2) //省
				strSHPFileName = "T_ADMINDIV_PROVINCE";
			else if(areaCode.length() == 4)  //市
				strSHPFileName = "T_ADMINDIV_CITY";
			else if(areaCode.length() == 6)  //县
				strSHPFileName = "T_ADMINDIV_COUNTY";
			String strAlias = strSHPFileName;
			Datasource ds = m_workspace.GetDatasource(strAlias);
			if(ds == null)
			{
				String strJson = String.format("{\"Type\":\"ESRI Shapefile\",\"Alias\":\"%s\",\"Server\":\"%s\"}", strAlias, "./data/"+strSHPFileName+".shp");
				ds = m_workspace.OpenDatasource(strJson);
			}
			if(ds != null){
				DatasetVector dtv = (DatasetVector)ds.GetDataset(0);
				String strJson = String.format("{\"Where\":\"[CODE]='%s'\"}", areaCode);
				Recordset rs = dtv.Query(strJson, null);
				if(rs != null){
					rs.MoveFirst();
					result = (GeoRegion)rs.GetGeometry();
					rs.Destroy();
				}
			}
			
		} catch (Exception e) {
			LogTool.logger.info("获取行政区划几何对象，详情【" + e.getMessage() + "】");
			e.printStackTrace();
		}
		return result;
	}
	
	private void destroy(){
		m_workspace.CloseDatasource(m_datasource.GetAlias());
		m_workspace.Destroy();
	}	
	
	/*
	 * main函数-入口
	 * */
	public static void main(String[] args) {
		MonitorThread monitorThread = new MonitorThread(50); //50 minutes
		monitorThread.setDaemon(true);
		monitorThread.start();
		
		GridInitialize gi = new GridInitialize();
		gi.start();
	}

}
