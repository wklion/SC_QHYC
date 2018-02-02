package com.spd.grid;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
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

public class GridSyncForDerfOfPrec {

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
		String filter="Z_NAFP_C_BAQH_date_P_BCC_AGCM2.2_(LSPR|CVPR|2MT)_1.0_MN_00.nc";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd000000");
		String dateStr = sdf.format(date);
		filter = filter.replaceAll("date", dateStr);
		Calendar calendar = new GregorianCalendar(); 
	    calendar.setTime(date); 
		File dataFile = new File(m_strPath);
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
		if(files.size()!=2){
			LogTool.logger.info("获取文件出错!");
			return;
		}
		File fileA=files.get(0);
		File fileB=files.get(1);//结果数据由LSPR和CVPR一起运算的
		String fileFullNameA = fileA.getName();
		String fileFullNameB = fileB.getName();
		//时间取一个文件即可
		String[] strs = fileFullNameA.split("_");
		String strDateTime = strs[4];
		String strYear = strDateTime.substring(2, 4);
		String strMonth = strDateTime.substring(4, 6);
		String strDay = strDateTime.substring(6, 8);
		
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", 
				"dsA", fileA.getAbsolutePath());
		strJson=strJson.replace('\\', '/');
		Datasource dsGribA = m_workspace.OpenDatasource(strJson);
		strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", 
				"dsB", fileB.getAbsolutePath());
		strJson=strJson.replace('\\', '/');
		Datasource dsGribB = m_workspace.OpenDatasource(strJson);
		
		for(int k=0;k<dsGribA.GetDatasetCount();k++){
			if(!dsGribA.GetDataset(k).GetType().equals("Raster")&&!dsGribB.GetDataset(k).GetType().equals("Raster"))
				continue;
			if(k==35){//只需35天的
				break;
			}
			DatasetRaster drA = (DatasetRaster)dsGribA.GetDataset(k);
			DatasetRaster drB = (DatasetRaster)dsGribB.GetDataset(k);
			String strForecastDate = strYear+strMonth+strDay;
			String desDatasetName = String.format("t_%s_%s_%s_p_%s_%03d_%s", m_strType, m_strElement, strForecastDate+"0000", strForecastDate+"00", 24*(1+k), m_strLevel);
			
			Double maxValue = drA.GetMaxValue(); 
			if(maxValue == 9999.0){
				drA.SetNoDataValue(9999.0);
				drB.SetNoDataValue(9999.0);
			}
			else if(maxValue == -9999.0){
				drA.SetNoDataValue(-9999.0);
				drB.SetNoDataValue(-9999.0);
			}
			else if(maxValue == 999.9){
				drA.SetNoDataValue(999.9);
				drB.SetNoDataValue(999.9);
			}
			else if(maxValue == -999.9){
				drA.SetNoDataValue(-999.9);
				drB.SetNoDataValue(-999.9);
			}
			drA.CalcExtreme();
			drB.CalcExtreme();
			
			//A格点裁剪
            Analyst pAnalystRasterClip = Analyst.CreateInstance("RasterClip", m_workspace);
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsGribA.GetAlias(), drA.GetName());
            pAnalystRasterClip.SetPropertyValue("Input", strJson);
            strJson = "{\"Type\":\"ESRI Shapefile\",\"Alias\":\"dsClipA\",\"Server\":\"./data/T_CLIP.shp\"}";
            Datasource dsClipA = m_workspace.OpenDatasource(strJson);
            Dataset dtClipA = dsClipA.GetDataset(0);
            strJson = ((DatasetVector)dtClipA).GetFields();
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\",\"Where\":\"%s\"}", dsClipA.GetAlias(), dtClipA.GetName(), "[ID]=1");
            pAnalystRasterClip.SetPropertyValue("ClipRegion", strJson);
            strJson = "{\"Type\":\"Memory\",\"Alias\":\"dsRasterClipA\",\"Server\":\"\"}";
            Datasource dsRasterClipA = m_workspace.CreateDatasource(strJson);
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsRasterClipA.GetAlias(), desDatasetName);
            pAnalystRasterClip.SetPropertyValue("Output", strJson);
            pAnalystRasterClip.Execute();
            pAnalystRasterClip.Destroy();
            
          //B格点裁剪
            pAnalystRasterClip = Analyst.CreateInstance("RasterClip", m_workspace);
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsGribB.GetAlias(), drB.GetName());
            pAnalystRasterClip.SetPropertyValue("Input", strJson);
            strJson = "{\"Type\":\"ESRI Shapefile\",\"Alias\":\"dsClipB\",\"Server\":\"./data/T_CLIP.shp\"}";
            Datasource dsClipB = m_workspace.OpenDatasource(strJson);
            Dataset dtClipB = dsClipB.GetDataset(0);
            strJson = ((DatasetVector)dtClipB).GetFields();
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\",\"Where\":\"%s\"}", dsClipB.GetAlias(), dtClipB.GetName(), "[ID]=1");
            pAnalystRasterClip.SetPropertyValue("ClipRegion", strJson);
            strJson = "{\"Type\":\"Memory\",\"Alias\":\"dsRasterClipB\",\"Server\":\"\"}";
            Datasource dsRasterClipB = m_workspace.CreateDatasource(strJson);
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsRasterClipB.GetAlias(), desDatasetName);
            pAnalystRasterClip.SetPropertyValue("Output", strJson);
            pAnalystRasterClip.Execute();
            pAnalystRasterClip.Destroy();
            
          //A格点降尺度
        	Analyst pAnalystResample = Analyst.CreateInstance("Resample", m_workspace);
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsRasterClipA.GetAlias(), desDatasetName);
            pAnalystResample.SetPropertyValue("Input", strJson);
            pAnalystResample.SetPropertyValue("OutputCellSize", String.format("%s %s", m_dResolutionX, m_dResolutionY));
            pAnalystResample.SetPropertyValue("ResamplingType", "Bilinear"); 			            
            strJson = "{\"Type\":\"Memory\",\"Alias\":\"dsResampleA\",\"Server\":\"\"}";
            Datasource dsResampleA = m_workspace.CreateDatasource(strJson);
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsResampleA.GetAlias(), desDatasetName);
            pAnalystResample.SetPropertyValue("Output", strJson);
            pAnalystResample.Execute();
            pAnalystResample.Destroy();
            
          //B格点降尺度
        	pAnalystResample = Analyst.CreateInstance("Resample", m_workspace);
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsRasterClipB.GetAlias(), desDatasetName);
            pAnalystResample.SetPropertyValue("Input", strJson);
            pAnalystResample.SetPropertyValue("OutputCellSize", String.format("%s %s", m_dResolutionX, m_dResolutionY));
            pAnalystResample.SetPropertyValue("ResamplingType", "Bilinear"); 			            
            strJson = "{\"Type\":\"Memory\",\"Alias\":\"dsResampleB\",\"Server\":\"\"}";
            Datasource dsResampleB = m_workspace.CreateDatasource(strJson);
            strJson = String.format("{\"Datasource\":\"%s\",\"Dataset\":\"%s\"}", dsResampleB.GetAlias(), desDatasetName);
            pAnalystResample.SetPropertyValue("Output", strJson);
            pAnalystResample.Execute();
            pAnalystResample.Destroy();
            
          //赋值到标准（产品）网格
            Dataset dtResampleA = dsResampleA.GetDataset(desDatasetName);
            Dataset dtResampleB = dsResampleB.GetDataset(desDatasetName);
            if(dtResampleA != null&&dtResampleB!=null){
            	DatasetRaster dgResampleA = (DatasetRaster)dtResampleA;
            	DatasetRaster dgResampleB = (DatasetRaster)dtResampleB;
            	Rectangle2D rectangle2d = new Rectangle2D.Double(m_left, m_bottom, m_width, m_height);
				double dDelta = m_dResolutionX;
				int cols = (int)Math.round(rectangle2d.getWidth()/dDelta);
				int rows = (int)Math.round(rectangle2d.getHeight()/dDelta);
				double noDataValue = -9999.0f;
				Rectangle2D rcBounds = rectangle2d;
				String strBounds = String.format("\"left\":%f,\"bottom\":%f,\"right\":%f,\"top\":%f", rcBounds.getX(), rcBounds.getY(), rcBounds.getX() + rcBounds.getWidth(), rcBounds.getY() + rcBounds.getHeight());
				String str = String.format("{\"Name\":\"%s\",\"ValueType\":\"%s\",\"Width\":%d,\"Height\":%d,\"BlockSize\":\"256 256\",\"Projection\":\"%s\",\"Bounds\":{%s},\"NoDataValue\":%f}",
						desDatasetName, dgResampleA.GetValueType(), cols, rows, "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", strBounds, noDataValue);
				DatasetRaster dgTarget = dsTarget.CreateDatasetRaster(str);
				if(dgTarget != null){
					dgTarget.Open();
					Boolean bTemp = m_strElement=="2t";
					Point2D pt2d00 = new Point2D.Double(Math.round((rcBounds.getX()+dDelta/2)*10000.0)/10000.0, 
							Math.round((rcBounds.getY()+dDelta/2)*10000.0)/10000.0);
			        Point2D cell00 = dgResampleA.PointToCell(pt2d00);
					int offsetX = (int)(cell00.getX());
					int offsetY = (int)(cell00.getY());
					Scanline sl = new Scanline(dgTarget.GetValueType(), cols);
					Scanline slResampleA = new Scanline(dgResampleA.GetValueType(), dgResampleA.GetWidth());
					Scanline slResampleB = new Scanline(dgResampleB.GetValueType(), dgResampleB.GetWidth());
					for (int kk = 0; kk<rows; kk++)
		            {
						dgTarget.GetScanline(0, kk, sl);
						dgResampleA.GetScanline(0, kk+offsetY, slResampleA);
						dgResampleB.GetScanline(0, kk+offsetY, slResampleB);
						for(int ll=0; ll<cols; ll++){
							if(bTemp){
								sl.SetValue(ll, slResampleA.GetValue(ll+offsetX)/10); //气温除10
							}
							else{
								sl.SetValue(ll, ((slResampleA.GetValue(ll+offsetX)+slResampleB.GetValue(ll+offsetX))/2)*3600*1000*24); //降水量prec=(cprat.1+lsprate.2)/2*3600*1000*24
							}
						}			    								
						dgTarget.SetScanline(0, kk, sl);
		            }
					dgTarget.FlushCache();
					dgTarget.CalcExtreme();
					LogTool.logger.info(desDatasetName + "成功完成同化");
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
			String prefix = String.format("t_%s_%s", m_strType, m_strElement);
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
		//args = new String[]{"derf", "r24", "1000", "C:/Users/wk/Desktop/Test"};
		args = new String[]{"derf", "2t", "1000", "E:/Data/Derf/Temp"};
		//args = new String[]{"derf", "r24", "1000", "E:/Data/Derf/precipitation"};
		
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
		
		GridSyncForDerfOfPrec gridSync = new GridSyncForDerfOfPrec();
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
