package com.spd.qhyc.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.util.CommonUtil;
import com.spd.qhyc.util.DateUtil;

public class CimissMonthPrec {
	static Logger logger = LogManager.getLogger("mylog");
	static String fInsertSql = "insert into t_month_rain(province,stationname,stationnum,year,m%d) values(?,?,?,?,?)";
	static String fUpdateSql = "update t_month_rain set m%d=? where year=? and stationnum=?";
	public static void main(String[] args) throws Exception {
		//1、获取配置
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
		Calendar calNow = Calendar.getInstance();
		if(args.length>0){
			if(args[0].length()!=6) {
				logger.error("输入日期错误,应是201712这样的格式");
				return;
			}
			calNow = DateUtil.parse("yyyyMM", args[0]);
		}
		queryMonthData(calNow,dpConn,config);
		dpConn.close();
	}
	private static void queryMonthData(Calendar cal,DruidPooledConnection conn,Config config) throws Exception {
		String thisStrDate = DateUtil.format("yyyy-MM", cal);
		logger.info("正在执行"+thisStrDate);
		Calendar tempCal = (Calendar) cal.clone();
		int curYear = tempCal.get(Calendar.YEAR);
		int curMonth = tempCal.get(Calendar.MONTH)+1;
		//查询数据是否存在
		String exitSql = "select * from t_month_rain where year=%s";//%s固定字符类型
		exitSql = String.format(exitSql, curYear);
		DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) conn.prepareStatement(exitSql);
		ResultSet rs = ps.executeQuery();
		rs.last();
		int rows = rs.getRow();
		rs.close();
		Boolean isInsert = rows>0?false:true;
		String sql = "";
		if(isInsert){
			logger.info("执行插入");
			sql = String.format(fInsertSql, curMonth);//插入
		}
		else{
			logger.info("执行更新");
			sql = String.format(fUpdateSql, curMonth);//更新
		}
		tempCal.set(Calendar.DATE, 1);//设为第一天
		String strStartDateTime = DateUtil.format("yyyyMMdd000000",tempCal);
		tempCal.add(Calendar.MONTH, 1);
		tempCal.add(Calendar.DATE, -1);
		String strEndDateTime = DateUtil.format("yyyyMMdd000000",tempCal);
		String cimissUserID = config.getCimissUserID();
		String cimissPassword = config.getCimissPassword();
		String host = config.getCimissHost();
		String strAreaCodes = config.getAreaCodes();
		String[] areaCodes = strAreaCodes.split(",");
		for(String code:areaCodes) {
			System.out.println("执行:"+code);
			logger.info("执行:"+code);
			ps = (DruidPooledPreparedStatement) conn.prepareStatement(sql);
			String fParam = "userId=%s&pwd=%s&interfaceId=statSurfEleInRegion"
					+ "&dataCode=SURF_CHN_MUL_DAY&elements=Station_Name,Province,Station_Id_C&statEles=SUM_PRE_Time_0808"
					+ "&timeRange=[%s,%s]&adminCodes=%s&dataFormat=json";
			String param = String.format(fParam, cimissUserID,cimissPassword,strStartDateTime,strEndDateTime,code);
			URI uri = new URI("http", host, "/cimiss-web/api", param, "");
			URL url = uri.toURL();
			System.out.println("开始cimiss查询");
			logger.info("开始cimiss查询");
			logger.info(uri.toString());
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout( 1000 * 60 * 2 );
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
			String line = reader.readLine();
			StringBuilder retStr = new StringBuilder();
			while (line != null) {
			      retStr.append(line).append("\r\n");
			      line = reader.readLine();
			}
			reader.close();
			logger.info("查询cimiss成功!");
			String rstData = retStr.toString();
			Gson gson = new Gson();
			JsonObject jo = new JsonParser().parse(rstData).getAsJsonObject();
			int returnCode =  jo.get("returnCode").getAsInt();
		 	if(returnCode!=0){
		 		logger.error("查询有误!");
	 			return;
		 	}
		 	JsonArray ja =  jo.get("DS").getAsJsonArray();
		 	int dataCount = ja.size();
		 	for(int i=0;i<dataCount;i++){
		 		String objData = ja.get(i).toString();
	 			JsonObject jsonSub = new JsonParser().parse(objData).getAsJsonObject();
	 			String province = jsonSub.get("Province").getAsString();
	 			String staionName = jsonSub.get("Station_Name").getAsString();
	 			if(staionName.equals("成都")){
	 				province = "四川省";
	 			}
	 			if(province.equals("")||staionName.equals("")) {
	 				continue;
	 			}
	 			String stationNum = jsonSub.get("Station_Id_C").getAsString();
	 			Boolean isNum = CommonUtil.isNumeric(stationNum.charAt(0));
	 			if(!isNum) {//过滤区域站
	 				logger.info(stationNum+"被过滤!");
	 				continue;
	 			}
	 			String staionCode = jsonSub.get("Station_Id_C").getAsString();
	 			double val = 0;
	 			if(!jsonSub.get("SUM_PRE_Time_0808").getAsString().equals("")){
	 				val = jsonSub.get("SUM_PRE_Time_0808").getAsDouble();
	 			}
	 			if(isInsert) {
	 				ps.setString(1, province);
					ps.setString(2, staionName);
					ps.setString(3, staionCode);
					ps.setInt(4, curYear);
					ps.setDouble(5, val);
	 			}
	 			else {
	 				ps.setDouble(1, val);
	 				ps.setInt(2, curYear);
					ps.setString(3, staionCode);
	 			}
				ps.addBatch();
		 	}
		 	logger.info("执行批量操作!");
		 	ps.executeBatch();
		 	logger.info("批量操作完成!");
		 	conn.commit();
		 	ps.clearBatch();
		}
		ps.close();
	}
}
