package com.spd.qhyc.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.spd.qhyc.app.StationVal;
import com.spd.grid.service.impl.FactorDllLibary;
import com.spd.qhyc.model.XNStation;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.util.DBUtil;
import com.spd.qhyc.util.DateUtil;
import com.sun.jna.Native;
public class Forest_rain {
	static Logger logger = LogManager.getLogger("mylog");
	static String fInsertSql = "insert into t_forecast_month_prec_data(method,year,month,stationNum,val) values(?,?,?,?,?)";
	public static void main(String[] args) {
	try{	
		int year,month,scheme;
		//1连接数据库 2获取方案 3取得方案对应的因子 4根据因子预测降水 5降水存在指定文件下
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		//2、连接数据库
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;	
		try {
			dpConn = dds.getConnection();
			dpConn.setAutoCommit(false);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		Calendar calNow=Calendar.getInstance();
		calNow.add(Calendar.MONTH,1);
		if (args.length > 0) {
			if (args[0].length() != 6) {
				logger.error("输入日期错误,应是201712这样的格式");
				return;
			}
			calNow = DateUtil.parse("yyyyMM", args[0]);
		}
		year=calNow.get(Calendar.YEAR);
		month=calNow.get(Calendar.MONTH)+1;
		String scheme_monthsql="select distinct ics.id from t_index_corr ic left join t_index_corr_scheme ics on ic.schemeID=ics.id  where month=%d and ics.elementID='prec' and ics.isDefault=1";
		scheme_monthsql=String.format(scheme_monthsql, month);
		PreparedStatement ps_scheme=dpConn.prepareStatement(scheme_monthsql);
		ResultSet rs_schemeSet=ps_scheme.executeQuery();
		rs_schemeSet.first();
		scheme=rs_schemeSet.getInt("id");	
		inputfactorfile(dpConn,scheme);

		String corrFile = "e:/test/"+"corr_and_reg";
			FactorDllLibary factorDll = (FactorDllLibary) Native.loadLibrary(corrFile, FactorDllLibary.class);
			factorDll.reg(year, month,0);

		////2、打开站点文件
			String strStationFile = "e:/test/"+"staid.txt";
			File stationFile = new File(strStationFile);
			if(!stationFile.exists()){
				System.out.println("站点文件staid.txt不存在!");
			}
			InputStreamReader reader = new InputStreamReader(new FileInputStream(stationFile));
			BufferedReader br = new BufferedReader(reader);
			List<String> lsStation = new ArrayList();
			String line = "";
			line = br.readLine();
			while (line != null) {
				String strStationNum = line.trim();
				lsStation.add(strStationNum);
				line = br.readLine();
			}
         //获取站点信息
		String sql = "select * from t_xnstation";
		PreparedStatement ps=dpConn.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<XNStation> lsXNStation = dbUtil.populate(rs, XNStation.class);
//		dpConn.close();
		//选择用到的站点数据
		List<XNStation> lsSelectStation = new ArrayList();
		for(String strS:lsStation){
			Boolean isFind = false;
			for(XNStation xnStation:lsXNStation){
				String strSationNum = xnStation.getStation_Id_C();
				if(strSationNum.equals(strS)){
					lsSelectStation.add(xnStation);
					isFind = true;
					break;
				}
			}
			if(!isFind){
				System.out.println(strS);
			}
		}
		//5、获取预报数据
		String strPreFile = "e:/test/";
		
//		int flag = calRegForecastParam.getFlag();
//		if(flag==0){
//			strPreFile+="pre_r.txt";
//		}
//		else{
//			strPreFile+="pre_t.txt";
//		}
		strPreFile+="pre_r.txt";
		File preFile = new File(strPreFile);
		if(!preFile.exists()){ 
			System.out.println("文件不存在");
		}
		reader = new InputStreamReader(new FileInputStream(preFile));
		br = new BufferedReader(reader);
		line = "";
		line = br.readLine();
		int index = 0;
		List<StationVal> lsStationVal = new ArrayList();
		StationVal stationVal = null;
		while (line != null&&!line.equals("")) {
			line = line.trim();
			double val = Double.parseDouble(line);
			XNStation xnStation = lsSelectStation.get(index);
			stationVal = new StationVal();
			stationVal.setStationName(xnStation.getStation_Name());
			stationVal.setStationNum(xnStation.getStation_Id_C());
			stationVal.setLongitude(xnStation.getLon());
			stationVal.setLatitude(xnStation.getLat());
			stationVal.setValue(val);
			lsStationVal.add(stationVal);
			index++;
			line = br.readLine();
		}
		//--------------------------------------------
         String method="多元回归";
//		fuzhi(method,dpConn,year,month,lsStationVal);
         fuzhi_jp(method,dpConn,year,month,lsStationVal);
			
		//-------------------------------------
		reader.close();
		dpConn.close();
		System.out.println("over");
		
		//
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

		// TODO Auto-generated method stub
		//方案      
}
	private static void fuzhi_jp(String mt,DruidPooledConnection conn,int year,int month,List<StationVal> list)throws SQLException{
		String number="";
		DruidPooledPreparedStatement ps3 = (DruidPooledPreparedStatement) conn.prepareStatement(fInsertSql);
		for(StationVal sta:list)
		{
		number=sta.getStationNum();
		double jupin;
		if(String.valueOf(sta.getValue())=="NaN")
		{
			jupin=0;
		}
		else{
			jupin=sta.getValue();
		}
		if(jupin<-1)
		{
			jupin=-9999;
		}
		BigDecimal jupin_bd=new BigDecimal(Double.toString(jupin));
		jupin=jupin_bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		 ps3.setString(1, mt);
	        ps3.setInt(2, year);
	        ps3.setInt(3, month);
	        ps3.setString(4, number);
	        ps3.setDouble(5, jupin);
			ps3.addBatch();
			
		}
		ps3.executeBatch();
		conn.commit();
		ps3.close();
	}
	private static void inputfactorfile(DruidPooledConnection conn,int n)throws Exception {
		String sql="select * from t_index_corr where schemeID=%d";
		sql=String.format(sql, n);
		DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) conn.prepareStatement(sql);
	    ResultSet rs=ps.executeQuery();
		String filepath="E:/test/";
		String file=filepath+"tiaoxuanhouyinzi.txt";
		File filefactor=new File(file);
		if (filefactor.exists()) {
			filefactor.delete();
		}
		filefactor.createNewFile();
		BufferedWriter out=new BufferedWriter(new FileWriter(file));
  	    int num = 0;
  	   ArrayList<String> list=new ArrayList<String>();
        while (rs.next()) {
    	  String key="";
    	  String line="";
    	   for(int i=1;i<=12;i++)
    	   {
    		  key="month"+i;  
    		  int x=rs.getInt(key);
    		  if(x==1){
    			  num+=1;
      			line=i+" "+rs.getInt("indexID");
    			  list.add(line);
    		  }	  
    	   }   		  //个数  

	    }
		out.write(num+"\r\n");
		for(int a=0;a<list.size();a++)
		{
		  String xline=list.get(a);
		  out.write(xline+"\r\n");
		}
   		out.flush();
  		out.close();
	}
	private static void  fuzhi(String mt,DruidPooledConnection conn,int year,int month,List<StationVal> list) throws SQLException {	
	   String number="";
	   DruidPooledPreparedStatement ps2 = (DruidPooledPreparedStatement) conn.prepareStatement(fInsertSql);
	   for(StationVal sta:list)
	   {
		String historysql = "select * from v_month_prec_avg where stationnum=%s";
		number=sta.getStationNum();
		historysql = String.format(historysql,number);
		DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) conn.prepareStatement(historysql);
		ResultSet rs = ps.executeQuery();
		 rs.last();
	     int rows=rs.getRow();
		 if(rows==0)
		 {
			   continue;
		 }
        rs.first();
        String key="m"+month;
        double jupin=sta.getValue();
        double historyvalue=rs.getDouble(key); 
        double nowvalue=calculaterain(jupin, historyvalue);
        ps2.setString(1, mt);
        ps2.setInt(2, year);
        ps2.setInt(3, month);
        ps2.setString(4, number);
        ps2.setDouble(5, nowvalue);
		ps2.addBatch();	
		ps.close();
	   }
	ps2.executeBatch();  
	conn.commit();
	ps2.close();
				
	}
	   private static double calculaterain(double jupingnv,double historyrain) {
           BigDecimal his=new BigDecimal(Double.toString(historyrain));
           BigDecimal jup=new BigDecimal(Double.toString(jupingnv));
		   return his.add(his.multiply(jup)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}
