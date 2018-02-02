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

public class GridSyncForGrib2 {

	private static String m_strType;	 //模式类型
	private static String m_strElement;	 //要素
	private static String m_strLevel;	 //层次
	private static String m_strPath;	 //数据路径
	
	private static Boolean m_bSum = false;       //原始数据是否为累计值（日本逐3小时降水等，它属于累计值，同化处理时需要减去前一个时次的值）
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
		LogTool.logger.info("正在搜索待同步的文件...");
		String filter = "(.*)date(.*).GRIB2";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateStr = sdf.format(date);
		filter = filter.replaceAll("date", dateStr);
		Calendar calendar = new GregorianCalendar(); 
	    calendar.setTime(date); 
		String path = String.format("%s/%d/%02d/%02d", GridSyncForGrib2.m_strPath, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
		File dataFile = new File(path);
		File[] micapsFiles = dataFile.listFiles(new BaoWenFileFilter(filter));
		ArrayList<File> arrayFile = null;
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
		Datasource dsTarget = dsMySQL;
		
		for(int i=0; i<files.size(); i++)
		{
			File file = files.get(i);
			String fileFullName = file.getName();
			String filename = fileFullName.substring(0, fileFullName.lastIndexOf("."));			
			String[] strs = filename.split("_");
			String strHourSpan = strs[strs.length - 1];
			int hourSpanTotal = Integer.valueOf(strHourSpan.substring(0, 3));
			int hourSpanInterval = Integer.valueOf(strHourSpan.substring(3, strHourSpan.length()));			
			String strDateTime = strs[strs.length - 2];
			String strYear = strDateTime.substring(2, 4);
			String strMonth = strDateTime.substring(4, 6);
			String strDay = strDateTime.substring(6, 8);
			String strHour = strDateTime.substring(8, 10);
			
			String strJson = String.format("{\"Type\":\"GRIB\",\"Alias\":\"%s\",\"Server\":\"%s\"}", 
					"ds"+strYear+strMonth+strDay+strHour, file.getAbsolutePath());
			strJson=strJson.replace('\\', '/');
			Datasource dsGrib = m_workspace.OpenDatasource(strJson);
			
			//如果是需要统计
			String strAliasMem = "dsMem";
			int nStatisticsMethod = -1;
			Boolean bStatistics = m_nStatisticsHourSpan>1 && !m_strStatisticsMethod.equals(""); 
			if(bStatistics){			
				//转换为整数，后面计算快
				if(m_strStatisticsMethod.equals("max"))
					nStatisticsMethod = 0;
				else if(m_strStatisticsMethod.equals("min"))
					nStatisticsMethod = 1;
				else if(m_strStatisticsMethod.equals("sum"))
					nStatisticsMethod = 2;
				strJson = "{\"Type\":\"Memory\",\"Alias\":\""+ strAliasMem +"\",\"Server\":\"\"}";
				dsTarget = m_workspace.GetDatasource(strAliasMem);
				if(dsTarget != null)
					m_workspace.CloseDatasource(dsTarget.GetAlias());
	            dsTarget = m_workspace.CreateDatasource(strJson);
			}
			
			for(int k=0; k<dsGrib.GetDatasetCount(); k++){
				int hourSpan = (k+1)*hourSpanInterval;
				if(!dsGrib.GetDataset(k).GetType().equals("Raster"))
					continue;
				DatasetRaster dr = (DatasetRaster)dsGrib.GetDataset(k);
				String strForecastDate = strYear+strMonth+strDay+strHour;
				String desDatasetName = String.format("t_%s_%s_%s00_p_%s_%03d_%s", GridSyncForGrib2.m_strType, GridSyncForGrib2.m_strElement, strForecastDate, strForecastDate, hourSpan, GridSyncForGrib2.m_strLevel);
				
				String desDatasetNameStatistics = "";
				if(bStatistics)
				{
					int currentStatisticsHourSpan = (int)Math.ceil((double)(hourSpan)/(double)(GridSyncForGrib2.m_nStatisticsHourSpan))*m_nStatisticsHourSpan;
					desDatasetNameStatistics = String.format("t_%s_%s_%s00_p_%s_%03d_%s", GridSyncForGrib2.m_strType, GridSyncForGrib2.m_strElement, strForecastDate, strForecastDate, currentStatisticsHourSpan, GridSyncForGrib2.m_strLevel);
					boolean bfound = dsMySQL.GetDataset(desDatasetNameStatistics) != null;
					if(bfound)
					{
						continue;
					}
				}
				
				Boolean bfound = dsTarget.GetDataset(desDatasetName) != null;
				if(!bfound) {				
					try {					
						int datasetCount = 1;
						for(int j=0; j <datasetCount; j++ ) //UV具有两个数据集，故循环
						{
							String resultDatasetName = desDatasetName;
							if(datasetCount == 2)
								resultDatasetName += j==0?"_u":"_v";
							if(true)
							{
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
					            //格点裁剪
					            Analyst pAnalystRasterClip = Analyst.CreateInstance("RasterClip", m_workspace);
					            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsGrib.GetAlias(), dr.GetName());
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
					            
					            //格点降尺度
//					            if(detaX > m_dResolutionX)
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
									double noDataValue = -9999.0f;
									Rectangle2D rcBounds = rectangle2d;
									String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight());
									String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
											resultDatasetName, dgResample.GetValueType(), cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, noDataValue);
									DatasetRaster dgTarget = dsTarget.CreateDatasetRaster(str);
									if(dgTarget != null){
										dgTarget.Open();
//										Point2D pt2d00 = dgTarget.CellToPoint(new Point2D.Double(0, 0));
										Point2D pt2d00 = new Point2D.Double(Math.round((rcBounds.getX()+dDelta/2)*10000.0)/10000.0, 
												Math.round((rcBounds.getY()+dDelta/2)*10000.0)/10000.0);
								        Point2D cell00 = dgResample.PointToCell(pt2d00);
										int offsetX = (int)(cell00.getX());
										int offsetY = (int)(cell00.getY());
										Scanline sl = new Scanline(dgTarget.GetValueType(), cols);
			    						Scanline slResample = new Scanline(dgResample.GetValueType(), dgResample.GetWidth());
			    						for (int kk = 0; kk<rows; kk++)
							            {
			    							dgTarget.GetScanline(0, kk, sl);
			    							dgResample.GetScanline(0, kk+offsetY, slResample);
			    							for(int ll=0; ll<cols; ll++)
			    								sl.SetValue(ll, slResample.GetValue(ll+offsetX));
			    							dgTarget.SetScanline(0, kk, sl);
							            }
			    						dgTarget.FlushCache();
			    						dgTarget.CalcExtreme();
									}
					            }
					            
					            if(!bStatistics)
					            	LogTool.logger.info(desDatasetName + "成功完成同化");
							}
						}
						
						if(bStatistics){
							if(hourSpan%m_nStatisticsHourSpan == 0){ //累积时效已满足条件
								//创建结果数据集
								DatasetRaster dg0 = (DatasetRaster)dsTarget.GetDataset(0);
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
								for (int r = 0; r<rows; r++)
					            {
									dg.GetScanline(0, r, sl);
									for(int c = 0; c< cols; c++){							
										dValue = dNoDataValue;
										
										for(int l=0; l<dsTarget.GetDatasetCount(); l++){
											DatasetRaster dgTemp = (DatasetRaster)dsTarget.GetDataset(l);
											double dValueTemp = dgTemp.GetValue(c, r);
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
										sl.SetValue(c, dValue);
									}								
	    							dg.SetScanline(0, r, sl);
					            }
								dg.FlushCache();
	    						dg.CalcExtreme();
	    						
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
					LogTool.logger.info(desDatasetName + "已经同化过，无需同化");
				}
			}
		}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
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
			String prefix = String.format("t_%s_%s", GridSyncForGrib2.m_strType, GridSyncForGrib2.m_strElement);
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
			LogTool.logger.info(pceo.GetJson());
		}
	}
	
	
	public static void main(String[] args) {
		args = new String[]{"cfs", "2t", "1000", "E:/Data/CFS/cfs.2016010/00", "false", "24", "max"};
		//args = new String[]{"bj", "r12", "1000", "F:/Work/SPD/MeteoData/nwgd/SCMOC/ER03", "false", "12", "sum"};
		//args = new String[]{"bj", "tmax", "1000", "F:/Work/SPD/MeteoData/nwgd/SCMOC/xxx", "false", "24", "max"};
		//args = new String[]{"bj", "tmin", "1000", "F:/Work/SPD/MeteoData/nwgd/SCMOC/xxx", "false", "24", "min"};
		
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
		
		GridSyncForGrib2 gridSync = new GridSyncForGrib2();
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
		
		//删除三天前的数据，也即仅保留三天内的数据
		calendar.add(calendar.DATE, -1);
		currentDate=calendar.getTime();
		gridSync.delete(currentDate);	
		
		gridSync.destroy();
	}

}
