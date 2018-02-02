package com.spd.grid.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.mg.objects.*;
import com.spd.grid.domain.StationVal;
import com.spd.grid.model.Config;
import com.spd.grid.tool.DBUtil;
import com.spd.grid.tool.DateUtil;
import com.spd.grid.tool.GridUtil;
import com.spd.grid.tool.LogTool;

public class ForcastServiceHelper {
    /**
     * @throws Exception 
     * @作者:杠上花
     * @日期:2018年1月15日
     * @修改日期:2018年1月15日
     * @参数:
     * @返回:
     * @说明:获取观测距平数据
     */
    public List<StationVal> getObserveJPData(Connection dpConn,String elementid,int year,int month) throws Exception{
        String sql = "";
        String tablename = "";
        String hosTableName = "";
        if(elementid.equals("temp")){
            tablename = "t_month_temp";
            hosTableName = "v_hos_temp";
            sql="select tm.stationname,vhr.stationnum,vhr.longitude,vhr.latitude,round(tm.m%d-vhr.m%d,0) as value from %s tm right join %s vhr on tm.stationnum=vhr.stationnum where year=%d";
            sql=String.format(sql, month,month,tablename,hosTableName,year);
        }else{
            tablename = "t_month_rain";
            hosTableName = "v_hos_rain";
            sql="select tm.stationname,vhr.stationnum,vhr.longitude,vhr.latitude,round(100*(tm.m%d-vhr.m%d)/vhr.m%d,0) as value from %s tm right join %s vhr on tm.stationnum=vhr.stationnum where year=%d";
            sql=String.format(sql, month,month,month,tablename,hosTableName,year);
        }
        PreparedStatement ps = dpConn.prepareStatement(sql);
        ResultSet rs=ps.executeQuery();
        DBUtil dbUtil = new DBUtil();
        List<StationVal> lsResult = dbUtil.populate(rs, StationVal.class);
        ps.close();
        return lsResult;
    }
    /**
     * @throws Exception 
     * @作者:杠上花
     * @日期:2018年1月15日
     * @修改日期:2018年1月15日
     * @参数:
     * @返回:
     * @说明:计算高度场实况距平
     */
    public static DatasetRaster calLiveHeightDeparture(Workspace ws,Config config,Calendar calMake,Calendar calForecast,Datasource tempDS) throws Exception {
        Calendar calEnd = (Calendar) calMake.clone();
        calEnd.add(Calendar.YEAR, 1);
        //1、高度场
        String strFile = config.getMonthHgtFile();
        File file = new File(strFile);
        if(!file.exists()) {
            LogTool.logger.error(String.format("实况高度场文件:%s,不存在!", strFile));
            return null;
        }
        strFile = strFile.replace("\\", "/");
        String heightAlias = "height"+DateUtil.format("HHmmss", Calendar.getInstance());
        String strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+heightAlias+"\",\"Server\":\"" + strFile + "\"}";
        Datasource ds = ws.OpenDatasource(strJson);
        if(ds==null || ds.GetDatasetCount()<1) {
            LogTool.logger.error("高度场数据打开失败!");
            return null;
        }
        int index = getIndexFromLiveHeight(ds,calForecast);
        DatasetRaster drHeight = (DatasetRaster) ds.GetDataset(index);
        drHeight.CalcExtreme();
        //2、历史平均
        String path = config.getHgtMonthAvgPath();
        int forecastMonth = calForecast.get(Calendar.MONTH)+1;
        strFile = path + forecastMonth +".tif";
        file = new File(strFile);
        if(!file.exists()) {
            LogTool.logger.error(String.format("文件:%s,不存在!", strFile));
            return null;
        }
        strFile = strFile.replace("\\", "/");
        String heightAvgAlias = "heightAvg"+DateUtil.format("HHmmss", Calendar.getInstance());
        strJson = "{\"Type\":\"GTiff\",\"Alias\":\""+heightAvgAlias+"\",\"Server\":\"" + strFile + "\"}";
        ds = ws.OpenDatasource(strJson);
        if(ds==null || ds.GetDatasetCount()<1) {
            LogTool.logger.error("高度场平均数据打开失败!");
            return null;
        }
        DatasetRaster drHeightAvg = (DatasetRaster) ds.GetDataset(0);
        drHeightAvg.CalcExtreme();
        
        //4、栅格运算
        GridUtil gridUtil = new GridUtil();
        gridUtil.calRaster(ws, drHeight, drHeightAvg, tempDS.GetAlias(), "jpLiveData", "[a]-[b]");
        DatasetRaster drTemp = (DatasetRaster) tempDS.GetDataset("jpLiveData");
        
        ws.CloseDatasource(heightAlias);
        ws.CloseDatasource(heightAvgAlias);
        return drTemp;
    }
    /**
     * @throws Exception 
     * @作者:杠上花
     * @日期:2018年1月15日
     * @修改日期:2018年1月15日
     * @参数:
     * @返回:索引
     * @说明:获取预报月份去年月份在实况NC中的索引
     */
    private static int getIndexFromLiveHeight(Datasource ds,Calendar calForecast) throws Exception {
        int index = 0;
        Calendar calForecastTemp = (Calendar) calForecast.clone();
        calForecastTemp.add(Calendar.YEAR, -1);
        int tartgetYear = calForecastTemp.get(Calendar.YEAR);
        int tartgetMonth = calForecastTemp.get(Calendar.MONTH)+1;
        int startYear = 1948;
        int month = 1;
        int targetLevel = 500;
        int dsCount = ds.GetDatasetCount();
        JSONObject json = null;
        for(int i = 0;i<dsCount;i++) {
            Dataset dataset = ds.GetDataset(i);
            String strMeta = dataset.GetMetadata();
            json = new JSONObject(strMeta);
            int level = json.getInt("NETCDF_DIM_level");
            if(level ==10){//最小
                month++;
            }
            if(month>12){
                month=1;
                startYear++;
            }
            if(startYear == tartgetYear && month == tartgetMonth && level == targetLevel){
                break;
            }
            index++;
        }
        if(index>=dsCount){
            System.out.println("实况格点索引超出范围!");
        }
        return index;
    }
    /**
     * @作者:杠上花
     * @日期:2018年1月15日
     * @修改日期:2018年1月15日
     * @参数:
     * @返回:
     * @说明:计算高度场距平
     */
    public DatasetRaster calHeightDeparture(Workspace ws,Config config,Calendar calMake,Calendar calForecast,Datasource dsTemp) {
        Calendar calEnd = (Calendar) calMake.clone();
        calEnd.add(Calendar.YEAR, 1);
        //1、高度场
        String path = config.getModeHgtPath();
        String strMakeDate = DateUtil.format("yyyyMMdd", calMake);
        String strStartDate = DateUtil.format("yyyyMM", calMake);
        String strEndDate = DateUtil.format("yyyyMM", calEnd);
        String strFileNameFormat = "%s.atm.Z3.%s-%s_prs0500_member.nc";
        String strFileName = String.format(strFileNameFormat,strMakeDate,strStartDate,strEndDate);
        String strFile = path + strFileName;
        File file = new File(strFile);
        if(!file.exists()) {
            LogTool.logger.error(String.format("高度场文件:%s,不存在!", strFile));
            return null;
        }
        strFile = strFile.replace("\\", "/");
        String heightAlias = "height"+DateUtil.format("HHmmss", Calendar.getInstance());
        String strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+heightAlias+"\",\"Server\":\"" + strFile + "\"}";
        Datasource ds = ws.OpenDatasource(strJson);
        if(ds==null || ds.GetDatasetCount()<1) {
            LogTool.logger.error("高度场数据打开失败!");
            return null;
        }
        int index = getIndexFromNC(ds,calMake,calForecast);
        DatasetRaster drHeight = (DatasetRaster) ds.GetDataset(index);
        //2、裁剪
        GridUtil gridUtil = new GridUtil();
        gridUtil.GridClip(ws, heightAlias, drHeight.GetName(), dsTemp.GetAlias(), "heightClip");
        drHeight = (DatasetRaster) dsTemp.GetDataset("heightClip");
        //3、历史平均
        path = config.getHgtMonthAvgPath();
        int forecastMonth = calForecast.get(Calendar.MONTH)+1;
        strFile = path + forecastMonth +".tif";
        file = new File(strFile);
        if(!file.exists()) {
            LogTool.logger.error(String.format("文件:%s,不存在!", strFile));
            return null;
        }
        strFile = strFile.replace("\\", "/");
        String heightAvgAlias = "heightAvg"+DateUtil.format("HHmmss", Calendar.getInstance());
        strJson = "{\"Type\":\"netCDF\",\"Alias\":\""+heightAvgAlias+"\",\"Server\":\"" + strFile + "\"}";
        ds = ws.OpenDatasource(strJson);
        if(ds==null || ds.GetDatasetCount()<1) {
            LogTool.logger.error("高度场平均数据打开失败!");
            return null;
        }
        DatasetRaster drHeightAvg = (DatasetRaster) ds.GetDataset(0);
        drHeightAvg.CalcExtreme();
        //4、栅格运算
        gridUtil.calRaster(ws, drHeight, drHeightAvg, dsTemp.GetAlias(), "jpModeData", "[a]-[b]");
        DatasetRaster drTemp = (DatasetRaster) dsTemp.GetDataset("jpModeData");
        ws.CloseDatasource(heightAlias);
        ws.CloseDatasource(heightAvgAlias);
        return drTemp;
    }
    /**
     * @作者:杠上花
     * @日期:2018年1月15日
     * @修改日期:2018年1月15日
     * @参数:
     * @返回:
     * @说明:获取预报月份的索引
     */
    private static int getIndexFromNC(Datasource ds,Calendar calMake,Calendar calForecast) {
        Calendar calMakeTemp = (Calendar) calMake.clone();
        Calendar calForecastTemp = (Calendar) calForecast.clone();
        int diffMonth = 0;
        while(true) {
            diffMonth++;
            int makeYear = calMakeTemp.get(Calendar.YEAR);
            int makeMonth = calMakeTemp.get(Calendar.MONTH);
            int forecastYear = calForecast.get(Calendar.YEAR);
            int forecastMonth = calForecast.get(Calendar.MONTH);
            if(makeYear == forecastYear && makeMonth == forecastMonth) {
                break;
            }
            calMakeTemp.add(Calendar.MONTH, 1);
        }
        int index = (diffMonth - 1)*24;
        return index;
    }
}
