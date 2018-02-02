package com.spd.grid;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import com.mg.objects.Analyst;
import com.mg.objects.Dataset;
import com.mg.objects.DatasetRaster;
import com.mg.objects.DatasetVector;
import com.mg.objects.Datasource;
import com.mg.objects.MGEventListener;
import com.mg.objects.MGEventObject;
import com.mg.objects.ProgressChangedEventObject;
import com.mg.objects.Scanline;
import com.mg.objects.Workspace;
import com.spd.tool.BaoWenFileFilter;
import com.spd.tool.CommonTool;
import com.spd.tool.LogTool;

public class GridSync {

	private static String m_strType;	 //模式类型
	private static String m_strElement;	 //要素
	private static String m_strLevel;	 //层次
	private static String m_strPath;	 //数据路径
	private static Boolean m_bSum = false;       //原始数据是否为累计值（日本逐3小时降水等，它属于累计值，同化处理时需要减去前一个时次的值）
	//private static String m_nStatisticsElement = "";	//统计要素，例如"tmax 24 max"，表示逐24小时统计最高作为最高气温
	private static int m_nStatisticsHourSpan = -1;  //统计时效 ，例如"tmax 24 max"，表示逐24小时统计最高作为最高气温
	private static String m_strStatisticsMethod = ""; //统计方法 ，例如"tmax 24 max"，表示逐24小时统计最高作为最高气温
	
	private static String m_strAlias;
	private static Workspace m_workspace;
	private static String m_strConnectionInfo;
	
	private static Double m_dResolutionX = 0.05;
	private static Double m_dResolutionY = 0.05;
	private static Double m_left = 104.175;
	private static Double m_bottom = 19.475;
	private static Double m_width = 8.05;
	private static Double m_height = 7.05;
	
	static {
		m_workspace = new Workspace();
		CommonTool commonTool = new CommonTool();
		m_strConnectionInfo = String.format("{\"Type\":\"%s\",\"Alias\":\"%s\",\"Server\":\"%s\",\"User\":\"%s\",\"Password\":\"%s\",\"DB\":\"%s\",\"Port\":\"%s\"}",
				commonTool.getValue("Type"), commonTool.getValue("Alias"), commonTool.getValue("Server"), commonTool.getValue("User"),
				commonTool.getValue("Password"), commonTool.getValue("DB"), commonTool.getValue("Port"));
		m_strAlias = commonTool.getValue("Alias");	
		m_dResolutionX = Double.valueOf(commonTool.getValue("ResolutionX"));
		m_dResolutionY = Double.valueOf(commonTool.getValue("ResolutionY"));
		m_left = Double.valueOf(commonTool.getValue("Left"));
		m_bottom = Double.valueOf(commonTool.getValue("Bottom"));
		m_width = Double.valueOf(commonTool.getValue("Width"));
		m_height = Double.valueOf(commonTool.getValue("Height"));
	}
	
	/**
	 * 获取格点数据源
	 * @return
	 */
	private Datasource getDatasource()
	{
		Datasource ds = m_workspace.GetDatasource(m_strAlias);
		if(ds == null){
			ds = m_workspace.OpenDatasource(m_strConnectionInfo);
		}
//		if(ds == null)	//首次部署时创建数据源
//			ds = m_workspace.CreateDatasource(m_strConnectionInfo);
		return ds;
	}
	
	/**
	 * 查询返回全部的需要同步的文件
	 * @return
	 */
	public ArrayList<File> getFiles(Date date) {
		ArrayList<File> arrayFile = null;
		
		try{
			LogTool.logger.info(m_strType + "_" + m_strElement + "：正在搜索待同步的文件...");
			String filter = "date(.*)";
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
			String dateStr = sdf.format(date);
			filter = filter.replaceAll("date", dateStr);
			File dataFile = new File(GridSync.m_strPath);
			File[] micapsFiles = dataFile.listFiles(new BaoWenFileFilter(filter));
			if(micapsFiles != null && micapsFiles.length > 0){
				arrayFile = new ArrayList<File>(); 
				for(int i=0; i<micapsFiles.length; i++)
					arrayFile.add(micapsFiles[i]);
				//排序一下，以备后用
				Collections.sort(arrayFile, new Comparator<File>() { 
				    @Override
				    public int compare(File o1, File o2) {
				        if (o1.isDirectory() && o2.isFile())
				            return -1;
				        if (o1.isFile() && o2.isDirectory())
				            return 1;
				        //return o2.getName().compareTo(o1.getName()); //倒序
				        return o1.getName().compareTo(o2.getName());   //顺序
				    }
				});	
			}
		}
		catch (Exception e) {
			LogTool.logger.info(e.getMessage());
		}		
		return arrayFile;
	}
	
	public void sync(ArrayList<File> files) {
		if(files == null || files.size() == 0)
		{
			LogTool.logger.info("当前没有需要同化的数据");
			return;
		}
			
		Datasource dsMySQL = getDatasource();
		try {
			if(dsMySQL == null)
				LogTool.logger.info("数据源打开失败！");
			
			//如果是累积值，把原始累积值放到临时数据源中，以便相减
			Datasource dsSum = null;
			String strAliasSum = "dsSum";
			if(m_bSum){
				String strJson = "{\"Type\":\"Memory\",\"Alias\":\""+ strAliasSum +"\",\"Server\":\"\"}";
				dsSum = m_workspace.GetDatasource(strAliasSum);
				if(dsSum!=null)
					m_workspace.CloseDatasource(dsSum.GetAlias());
				dsSum = m_workspace.CreateDatasource(strJson);
			}
			
			//如果是需要统计
			String strAliasMem = "dsMem";
			int nStatisticsMethod = -1;
			Boolean bStatistics = m_nStatisticsHourSpan>1 && !m_strStatisticsMethod.equals(""); 
			Datasource dsTarget = dsMySQL;
			if(bStatistics){			
				//转换为整数，后面计算快
				if(m_strStatisticsMethod.equals("max"))
					nStatisticsMethod = 0;
				else if(m_strStatisticsMethod.equals("min"))
					nStatisticsMethod = 1;
				else if(m_strStatisticsMethod.equals("sum"))
					nStatisticsMethod = 2;
				String strJson = "{\"Type\":\"Memory\",\"Alias\":\""+ strAliasMem +"\",\"Server\":\"\"}";
				dsTarget = m_workspace.GetDatasource(strAliasMem);
				if(dsTarget != null)
					m_workspace.CloseDatasource(dsTarget.GetAlias());
	            dsTarget = m_workspace.CreateDatasource(strJson);
			}
			
			int hourStart = 0;
			int hour = 0;
			//for(File file:files)
			for(int i=0; i<files.size(); i++)
			{
				File file = files.get(i);
				String fileFullName = file.getName();
				String filename = fileFullName.substring(0, fileFullName.lastIndexOf("."));
				String postfix = fileFullName.substring(fileFullName.lastIndexOf(".")+1, fileFullName.length());			
				//T_TYPE_ELE_LEVEL_YYMMDDHH_FFF
				String desDatasetName = String.format("t_%s_%s_%s00_p_%s_%s_%s", GridSync.m_strType, GridSync.m_strElement, filename, filename, postfix, GridSync.m_strLevel);
				String desDatasetNameStatistics = "";
				hour = Integer.valueOf(postfix);
				if(hour == 0) //一是没用，二是欧洲细网格模式当次预报还没有来，就生成一个000的初始场，这对于系统判定最新时次预报造成错误
				{
					if(bStatistics)
					{
						hourStart = 0;
	    				//清空内存数据源
						int memDatasetCount = dsTarget.GetDatasetCount();
						for(int l=memDatasetCount - 1; l>=0; l--){
							dsTarget.DeleteDataset(dsTarget.GetDataset(l).GetName());
						}
						continue;
					}
					else
						continue;
				}
				
				if(bStatistics)
				{				
					//确保是同一次预报
					if(i > 0){
						File filePre = files.get(i-1);
		            	String fileFullNamePre = filePre.getName();
		    			String filenamePre = fileFullNamePre.substring(0, fileFullNamePre.lastIndexOf("."));
		    			String postfixPre = fileFullNamePre.substring(fileFullNamePre.lastIndexOf(".")+1, fileFullNamePre.length());
		    			if(!filename.equals(filenamePre)){ 
		    				hourStart = 0;
		    				//清空内存数据源
							int memDatasetCount = dsTarget.GetDatasetCount();
							for(int l=memDatasetCount - 1; l>=0; l--){
								dsTarget.DeleteDataset(dsTarget.GetDataset(l).GetName());
							}
		    			}	
					}
					
					desDatasetNameStatistics = String.format("t_%s_%s_%s00_p_%s_%s_%s", GridSync.m_strType, GridSync.m_strElement, filename, filename, new DecimalFormat("000").format(hourStart + m_nStatisticsHourSpan), GridSync.m_strLevel);
					boolean bfound = dsMySQL.GetDataset(desDatasetNameStatistics) != null;
					if(bfound)
					{
						hourStart = hour/m_nStatisticsHourSpan*m_nStatisticsHourSpan;
						continue;
					}
				}
				boolean bfound = dsTarget.GetDataset(desDatasetName) != null;
				if(!bfound) //如果是风向风速，数据集+u、v，再判断一下
				{
					if(m_strElement.equals("10uv") || m_strElement.equals("wmax")){
						String desDatasetNameU = desDatasetName+"_u";
						bfound = dsTarget.GetDataset(desDatasetNameU) != null;
					}
			    }
				if(!bfound) {				
					try {					
						String strJson = String.format("{\"Type\":\"Micaps\",\"Alias\":\"%s\",\"Server\":\"%s\"}", 
								filename+"_"+postfix, file.getAbsolutePath());
						strJson=strJson.replace('\\', '/');
						Datasource dsMicaps = m_workspace.OpenDatasource(strJson);
						int datasetCount = dsMicaps.GetDatasetCount();
						for(int j=0; j <datasetCount; j++ ) //UV具有两个数据集，故循环
						{
							String resultDatasetName = desDatasetName;
							if(datasetCount == 2)
								resultDatasetName += j==0?"_u":"_v";
							Dataset dtMicaps = dsMicaps.GetDataset(j);
							if(dtMicaps.GetType().equals("Raster"))
							{
								DatasetRaster dr = (DatasetRaster)dtMicaps;
								//既然引擎无法确定无效值，只能这么处理啦
								Double maxValue = dr.GetMaxValue(); 
								if(maxValue == 9999.0)
									dr.SetNoDataValue(9999.0);
								else if(maxValue == -9999.0)
									dr.SetNoDataValue(-9999.0);
								else if(maxValue == 999.9)
									dr.SetNoDataValue(999.9);
								else if(maxValue == -999.9)
									dr.SetNoDataValue(-999.9);
								dr.CalcExtreme();
								//double detaX = dr.GetBounds().getWidth()/dr.GetWidth();
					            //格点裁剪（比标准产品网格稍微大一点）
					            Analyst pAnalystRasterClip = Analyst.CreateInstance("RasterClip", m_workspace);
					            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsMicaps.GetAlias(), dtMicaps.GetName());
					            pAnalystRasterClip.SetPropertyValue("Input", strJson);
					            strJson = "{\"Type\":\"ESRI Shapefile\",\"Alias\":\"dsClip\",\"Server\":\"./data/T_CLIP.shp\"}";
					            Datasource dsClip = m_workspace.OpenDatasource(strJson);
					            Dataset dtClip = dsClip.GetDataset(0);
					            strJson = ((DatasetVector)dtClip).GetFields();
					            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\",\"Where\":\"%s\"}", dsClip.GetAlias(), dtClip.GetName(), "[ID]=1");
					            pAnalystRasterClip.SetPropertyValue("ClipRegion", strJson);
					            strJson = "{\"Type\":\"Memory\",\"Alias\":\"dsRasterClip\",\"Server\":\"\"}";
					            Datasource dsRasterClip = m_workspace.CreateDatasource(strJson);
					            //strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", detaX > m_dResolutionX ? dsRasterClip.GetAlias():dsTarget.GetAlias(), resultDatasetName);
					            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsRasterClip.GetAlias(), resultDatasetName);
					            pAnalystRasterClip.SetPropertyValue("Output", strJson);
					            pAnalystRasterClip.Execute();
					            pAnalystRasterClip.Destroy();
					            
					            //格点降尺度（统一格距，不仅包括粗网格到细网格，还包括细网格到粗网格）
					            //if(detaX > m_dResolutionX)
				            	Analyst pAnalystResample = Analyst.CreateInstance("Resample", m_workspace);
					            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsRasterClip.GetAlias(), resultDatasetName);
					            pAnalystResample.SetPropertyValue("Input", strJson);
					            pAnalystResample.SetPropertyValue("OutputCellSize", String.format("%s %s", m_dResolutionX, m_dResolutionY));
					            pAnalystResample.SetPropertyValue("ResamplingType", "Bilinear"); //ResamplingType:NearestNeighbor,Bilinear,Bicubic
					            strJson = "{\"Type\":\"Memory\",\"Alias\":\"dsResample\",\"Server\":\"\"}";
					            Datasource dsResample = m_workspace.CreateDatasource(strJson);
					            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsResample.GetAlias(), resultDatasetName);
					            pAnalystResample.SetPropertyValue("Output", strJson);
					            //pAnalystResample.AddListener(new ProgressChangedEventListener());
					            pAnalystResample.Execute();
					            pAnalystResample.Destroy();
					            
					            //赋值到标准（产品）网格
					            Dataset dtResample = dsResample.GetDataset(resultDatasetName);
					            if(dtResample != null){
					            	DatasetRaster dgResample = (DatasetRaster)dtResample;
						            Rectangle2D rectangle2d = new Rectangle2D.Double(m_left, m_bottom, m_width, m_height);
									double dDelta = m_dResolutionX;
									int cols = (int)Math.round(rectangle2d.getWidth()/dDelta);
									int rows = (int)Math.round(rectangle2d.getHeight()/dDelta);
									//double noDataValue = -9999.0f;
									double noDataValue = dgResample.GetNoDataValue();
									Rectangle2D rcBounds = rectangle2d;
									String valueType = dgResample.GetValueType();
									String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight());
									String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
											resultDatasetName, valueType, cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, noDataValue);
									DatasetRaster dgTarget = dsTarget.CreateDatasetRaster(str);
									if(dgTarget != null){
										dgTarget.Open();
										//Point2D pt2d00 = dgTarget.CellToPoint(new Point2D.Double(0, 0)); //只有mysql数据引擎才支持在CreateDatasetRaster时赋予Bounds
										Point2D pt2d00 = new Point2D.Double(Math.round((rcBounds.getX()+dDelta/2)*10000.0)/10000.0, 
												Math.round((rcBounds.getY()+dDelta/2)*10000.0)/10000.0);
								        Point2D cell00 = dgResample.PointToCell(pt2d00);
										int offsetX = (int)(cell00.getX());
										int offsetY = (int)(cell00.getY());
										Scanline sl = new Scanline(valueType, cols);
			    						Scanline slResample = new Scanline(valueType, dgResample.GetWidth());
			    						int rowsResample = dgResample.GetHeight();
			    						Boolean isInteger = false;
			    						if(m_strElement.toLowerCase().equals("tcc"))
			    							isInteger = true;
			    						for (int k=0; k<rows; k++)
							            {
			    							dgTarget.GetScanline(0, k, sl);
			    							int kResample = k+offsetY;
			    							if(kResample < rowsResample){
			    								dgResample.GetScanline(0, kResample, slResample);
				    							for(int l=0; l<cols; l++){
				    								double dvalue = slResample.GetValue(l+offsetX);
				    								if(isInteger)
				    									sl.SetValue(l, Math.round(dvalue));
				    								else
				    									sl.SetValue(l, Math.round(dvalue*10.0)/10.0);
				    							}				    								
				    							dgTarget.SetScanline(0, k, sl);
			    							}    							
							            }
			    						sl.Destroy();
			    						slResample.Destroy();
			    						dgTarget.FlushCache();
			    						dgTarget.CalcExtreme();
									}
					            }
					            
					            //如果是累计值，减去前一时效
					            if(m_bSum){
					            	
					            	//相减之前将累积值存到累积数据源中
					            	Dataset dt = dsTarget.GetDataset(resultDatasetName);
					            	DatasetRaster dg = (DatasetRaster)dt;
					            	int cols = dg.GetWidth();
					            	int rows = dg.GetHeight();			
					            	Rectangle2D rcBounds = dg.GetBounds();
									String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight()); //左 上 宽 高
									String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
											resultDatasetName, dg.GetValueType(), dg.GetWidth(), dg.GetHeight(), "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, dg.GetNoDataValue());
									DatasetRaster dgSum = dsSum.CreateDatasetRaster(str);
									dgSum.Open();
		    						
									Scanline slSum = new Scanline(dg.GetValueType(), dg.GetWidth());
									for (int k = 0; k<dg.GetHeight(); k++)
						            {
										dg.GetScanline(0, k, slSum);
										dgSum.SetScanline(0, k, slSum);
						            }
									slSum.Destroy();
									dgSum.FlushCache();
									dgSum.CalcExtreme();
									
					            	if(i>0){
						            	File filePre = files.get(i-1);
						            	String fileFullNamePre = filePre.getName();
						    			String filenamePre = fileFullNamePre.substring(0, fileFullNamePre.lastIndexOf("."));
						    			String postfixPre = fileFullNamePre.substring(fileFullNamePre.lastIndexOf(".")+1, fileFullNamePre.length());
						    			if(filename.equals(filenamePre)){ //确保是同一次预报
						    				String desDatasetNamePre = String.format("t_%s_%s_%s00_p_%s_%s_%s", GridSync.m_strType, GridSync.m_strElement, filenamePre, filenamePre, postfixPre, GridSync.m_strLevel);
						    				Dataset dtPre = dsSum.GetDataset(desDatasetNamePre); //这里一定是从原始累积值中取，否则就是错误的
						    				if(dtPre != null){
						    					//Dataset dt = dsTarget.GetDataset(resultDatasetName);
						    					if(dt != null){
						    						Boolean isRain = m_strElement.toLowerCase().equals("r3") || m_strElement.toLowerCase().equals("r12");
						    						//DatasetRaster dg = (DatasetRaster)dt;
						    						DatasetRaster dgPre = (DatasetRaster)dtPre;					    						
						    						Scanline sl = new Scanline(dg.GetValueType(), cols);
						    						Scanline slPre = new Scanline(dgPre.GetValueType(), cols);
						    						Scanline slNew = new Scanline(dg.GetValueType(), cols);
						    						for (int k = 0; k<rows; k++)
										            {
						    							dg.GetScanline(0, k, sl);
						    							dgPre.GetScanline(0, k, slPre);
						    							for(int l=0; l<cols; l++)
						    							{
						    								double dNew = sl.GetValue(l) - slPre.GetValue(l); 
						    								if(dNew < 0 && isRain) //降尺度时，可能导致这里相减出现负数（-0.1）。故此将小于0的降水置为0
						    									dNew=0;
						    								slNew.SetValue(l, Math.round(dNew*10.0)/10.0);
						    							}
						    							dg.SetScanline(0, k, slNew);
										            }
						    						sl.Destroy();
						    						slPre.Destroy();
						    						slNew.Destroy();
						    						dg.FlushCache();
						    						dg.CalcExtreme();
						    					}
						    					else{
						    						LogTool.logger.error("未找到当前时效预报："+resultDatasetName);
						    					}
						    				}
						    				else{
						    					LogTool.logger.error("未找到前一时效预报："+desDatasetNamePre);
						    				}
						    			}
					            	}				            	
					            }
					            if(!bStatistics)
					            	LogTool.logger.info(desDatasetName+"成功完成同化");
							}
						}
						
						if(bStatistics){
							if(hour - hourStart == m_nStatisticsHourSpan){ //累积时效已满足条件
								hourStart = hour;
								//创建结果数据集
								DatasetRaster dg0 = (DatasetRaster)dsTarget.GetDataset(0);
								if(m_strElement.equals("wmax")) //日最大风，特殊处理
								{
									//Rectangle2D rcBounds = dg0.GetBounds();
									Rectangle2D rcBounds = new Rectangle2D.Double(m_left, m_bottom, m_width, m_height);
									double dDelta = m_dResolutionX;
									int cols = (int)Math.round(rcBounds.getWidth()/dDelta);
									int rows = (int)Math.round(rcBounds.getHeight()/dDelta);
									String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight()); //左 上 宽 高
									String strU = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
											desDatasetNameStatistics+"_u", dg0.GetValueType(), cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, dg0.GetNoDataValue());
									String strV = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
											desDatasetNameStatistics+"_v", dg0.GetValueType(), cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, dg0.GetNoDataValue());
									DatasetRaster dgU = dsMySQL.CreateDatasetRaster(strU);
									DatasetRaster dgV = dsMySQL.CreateDatasetRaster(strV);
									if(dgU == null || dgV == null)
									{
										LogTool.logger.error("数据集创建失败:"+desDatasetNameStatistics);
										return;
									}
									
									Scanline slU = new Scanline(dgU.GetValueType(), cols);
									Scanline slV = new Scanline(dgV.GetValueType(), cols);
									double dNoDataValue = dgU.GetNoDataValue();
									double dValueU = dNoDataValue;
									double dValueV = dNoDataValue;
									int uvCount = dsTarget.GetDatasetCount();
									if(uvCount % 2 != 0)
									{
										LogTool.logger.error("日最大风处理错误：u、v数据集个数不匹配");
										return;
									}
									for (int j = 0; j<rows; j++)
						            {
										dgU.GetScanline(0, j, slU);
										dgV.GetScanline(0, j, slV);
										for(int k = 0; k< cols; k++){							
											dValueU = dNoDataValue;
											dValueV = dNoDataValue;
											double dWindSpeedMax = 0.0;
											for(int l=0; l<uvCount; l+=2){
												DatasetRaster dgTempU = (DatasetRaster)dsTarget.GetDataset(l);   //第一个是u，第二个是v
												DatasetRaster dgTempV = (DatasetRaster)dsTarget.GetDataset(l+1);
												double u = dgTempU.GetValue(k, j);
												double v = dgTempV.GetValue(k, j);											
												if(u == dNoDataValue || v == dNoDataValue)
													continue;
												double dWindSpeed = Math.sqrt(u*u + v*v);
												if(dWindSpeed > dWindSpeedMax)
												{
													dWindSpeedMax = dWindSpeed;
													dValueU = u;
													dValueV = v;
												}
											}
											slU.SetValue(k, dValueU);
											slV.SetValue(k, dValueV);
										}								
		    							dgU.SetScanline(0, j, slU);
		    							dgV.SetScanline(0, j, slV);
						            }
									slU.Destroy();
									slV.Destroy();
									dgU.FlushCache();
		    						dgU.CalcExtreme();
		    						dgV.FlushCache();
		    						dgV.CalcExtreme();
								}
								else //常规要素
								{
									//Rectangle2D rcBounds = dg0.GetBounds();
									Rectangle2D rcBounds = new Rectangle2D.Double(m_left, m_bottom, m_width, m_height);
									double dDelta = m_dResolutionX;
									int cols = (int)Math.round(rcBounds.getWidth()/dDelta);
									int rows = (int)Math.round(rcBounds.getHeight()/dDelta);
									String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight()); //左 上 宽 高
									String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
											desDatasetNameStatistics, dg0.GetValueType(), cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, dg0.GetNoDataValue());
									DatasetRaster dg = dsMySQL.CreateDatasetRaster(str);
									dg.Open();
									if(dg == null)
									{
										LogTool.logger.error("数据集创建失败:"+desDatasetNameStatistics);
										return;
									}

									Scanline sl = new Scanline(dg.GetValueType(), cols);
									double dNoDataValue = dg.GetNoDataValue();
									double dValue = dNoDataValue;							
									for (int j = 0; j<rows; j++)
						            {
										dg.GetScanline(0, j, sl);
										for(int k = 0; k< cols; k++){							
											dValue = dNoDataValue;
											
											for(int l=0; l<dsTarget.GetDatasetCount(); l++){
												DatasetRaster dgTemp = (DatasetRaster)dsTarget.GetDataset(l);
												double dValueTemp = dgTemp.GetValue(k, j);
												if(dValueTemp == dNoDataValue)
													continue;
												if(nStatisticsMethod == 0){ //最大
													if(dValue == dNoDataValue || dValueTemp > dValue)
														dValue = dValueTemp;
												}
												else if(nStatisticsMethod == 1){ //最小
													if(dValue == dNoDataValue || dValueTemp < dValue)
														dValue = dValueTemp;
												}
												else if(nStatisticsMethod == 2){ //求和
													{
														if(dValue == dNoDataValue)
															dValue = dValueTemp;
														else
															dValue += dValueTemp;
													}
												}
											}
											sl.SetValue(k, Math.round(dValue*10.0)/10.0);
										}								
		    							dg.SetScanline(0, j, sl);
						            }
									sl.Destroy();
									dg.FlushCache();
		    						dg.CalcExtreme();	
								}							
	    						
	    						//清空内存数据源
	    						int memDatasetCount = dsTarget.GetDatasetCount();
	    						for(int l=memDatasetCount - 1; l>=0; l--){
	    							dsTarget.DeleteDataset(dsTarget.GetDataset(l).GetName());
	    						}
	    						
	    						LogTool.logger.info(desDatasetNameStatistics + "成功完成同化");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					LogTool.logger.info(file.getName() + "已经同化过，无需同化");
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			//m_workspace.Destroy(); //添加的时候不要关闭工作空间，改到应用程序结束时关闭
		}
	}
	
	public void delete(Date date)
	{
		try {
			Datasource dsMySQL = getDatasource();
			if(dsMySQL == null)
				return;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHH");
			String dateStr = sdf.format(date);			
			String prefix = String.format("t_%s_%s", GridSync.m_strType, GridSync.m_strElement);
			String prefixLow = prefix.toLowerCase();
			int dsCount = dsMySQL.GetDatasetCount();
			ArrayList<String> arrayDataset = new ArrayList<String>();
			for(int i=dsCount - 1; i>=0; i--)
			{
				Dataset dt = dsMySQL.GetDataset(i);
				if(dt == null)
					continue;
				String strDatasetName = dt.GetName();				
				if(strDatasetName.toLowerCase().startsWith(prefixLow))
				{
					String[] strs = strDatasetName.split("_");
					if(strs.length >= 8)
					{
						if(strs[5].compareTo(dateStr)<0)
							arrayDataset.add(strDatasetName);
					}					
				}
			}
			for(int i=0; i<arrayDataset.size(); i++)
			{
				String strDatasetName = arrayDataset.get(i);
				dsMySQL.DeleteDataset(strDatasetName);
				LogTool.logger.info(strDatasetName+"已删除");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
		}
	}
	
	public void destroy(){
		m_workspace.Destroy();
	}
	
	class ProgressChangedEventListener implements MGEventListener
	{
		public int GetEventID()
		{
			return MGEventObject.MGE_ProgressChanged;
		}
		public void Fire(MGEventObject e)
		{
			ProgressChangedEventObject pceo = (ProgressChangedEventObject)e;
			System.out.println(pceo.GetJson());
		}
	}
	
	
	public static void main(String[] args) {
		//args = new String[]{"WRF", "R24", "1000", "F:/Work/SPD/MeteoData/Micaps_Data/wrf_5km/Rain24/999"}; //测试用，部署时需要注释掉
		//args = new String[]{"EC", "R24", "1000", "F:/Work/SPD/MeteoData/Micaps_Data/Ecmwf_thin/rain24"}; //测试用，部署时需要注释掉
		//args = new String[]{"wrf", "R3", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/rain3"};
		//args = new String[]{"wrf", "r24", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/rain24"};
		//args = new String[]{"prvn", "10uv", "1000", "F:/Work/SPD/MeteoData/wrf_5km/Uv10/999"};
		//args = new String[]{"grapes", "10uv", "1000", "F:/Work/SPD/MeteoData/grapes/grapes9km/Uv10/999"};
		//args = new String[]{"EC", "DIV", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/D/1000"};
		//args = new String[]{"EC", "2T", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/2T/999"};
		//args = new String[]{"japan", "r3", "1000", "F:/Work/SPD/MeteoData/weathermap_data/japan_thin/APCP/0", "true"}; //标记降水为累计
		//args = new String[]{"japan", "tmax", "1000", "F:/Work/SPD/MeteoData/weathermap_data/japan_thin/TMP/2", "false", "24", "max"}; //日最高气温
		//args = new String[]{"japan", "r12", "1000", "F:/Work/SPD/MeteoData/weathermap_data/japan_thin/APCP/0", "true", "12", "sum"}; //标记降水为累计，并且逐12小时求和，日本降水累积
	
		
		
		//args = new String[]{"EC", "wmax", "1000", "F:/Work/SPD/MeteoData/weathermap_data/ecmwf_thin/10uv/999", "false", "24", "max"}; //日最大风
		
		//args = new String[]{"EC", "R12", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/TP/999", "true", "12", "sum"};
		//args = new String[]{"ec", "r3", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/TP/999", "true"}; //欧洲中心3小时降水
		//args = new String[]{"ec", "2t", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/2T/999"}; //欧洲中心2米气温
		//args = new String[]{"ec", "2t", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/2T/999"}; //GFS 2米气温
		//args = new String[]{"ec", "TCC", "1000", "F:/Work/SPD/MeteoData/Ecmwf_thin/TCC/999"}; //欧洲中心2米气温
		//args = new String[]{"japan", "r3", "1000", "F:/Work/SPD/MeteoData/japan_thin/APCP/0", "true"}; //标记降水为累计
		
		MonitorThread monitorThread = new MonitorThread(10); //10 minutes
		monitorThread.setDaemon(true);
		monitorThread.start();
		
		m_strType = args[0];
		m_strElement = args[1];
		m_strLevel = args[2];
		m_strPath = args[3];
		if(args.length >= 5)
			m_bSum = Boolean.valueOf(args[4]);
		if(args.length >= 6)
			m_nStatisticsHourSpan = Integer.valueOf(args[5]);
		if(args.length >= 7)
			m_strStatisticsMethod = args[6];
		
		GridSync gridSync = new GridSync();
		Date currentDate = new Date();
		//同步当天
		ArrayList<File> currentFiles = gridSync.getFiles(currentDate);
		gridSync.sync(currentFiles);
		
		//同步前一天
		Calendar calendar = new GregorianCalendar(); 
	    calendar.setTime(currentDate); 
	    calendar.add(calendar.DATE, -1); 
	    currentDate=calendar.getTime();
	    currentFiles = gridSync.getFiles(currentDate);
		gridSync.sync(currentFiles);
		
		
		//三天前的数据，也即仅保留三天内的数据
		calendar.add(calendar.DATE, -1); 
		currentDate=calendar.getTime();
		gridSync.delete(currentDate);	
				
		gridSync.destroy();
	}

}
