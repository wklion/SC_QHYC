package com.spd.datadeal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.util.Calendar;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spd.jdbc.DbPoolConnection;
import com.spd.tool.LogTool;
import com.spd.util.CommonUtil;
import com.spd.util.DateUtil;

/**
 * @����:wangkun
 * @����:2017��10��16��
 * @��˾:spd
 * @˵��:
*/
public class MonthPrecByDay {
	static String fInsertSql = "insert into t_month_temp(province,stationname,stationnum,year,m%d) values(?,?,?,?,?)";
	static String fUpdateSql = "update t_month_temp set m%d=? where year=? and stationnum=?";
	public static void main(String[] args) throws Exception {
		Boolean debug = true;
		DbPoolConnection dbp = DbPoolConnection.getInstance();
		DruidPooledConnection conn = dbp.getConnection();
		conn.setAutoCommit(false);
		
		Calendar calNow = Calendar.getInstance();
		Calendar calStart = null;
		if(debug){
			calStart = Calendar.getInstance();
			calStart.set(Calendar.YEAR, 1981);
			//calStart.set(Calendar.YEAR, 2015);
			calStart.set(Calendar.MONTH, 0);
			calStart.set(Calendar.DAY_OF_MONTH, 1);
			int curYear = calNow.get(Calendar.YEAR);
			int curMonth = calNow.get(Calendar.MONTH);
			while(true){
				queryMonthData(calStart,conn);
				calStart.add(Calendar.MONTH, 1);
				int tempYear = calStart.get(Calendar.YEAR);
				int tempMonth = calStart.get(Calendar.MONTH);
				if(curYear==tempYear&&curMonth==tempMonth){
					break;
				}
			}
		}
		else{
			int day = calNow.get(Calendar.DAY_OF_MONTH);
			if(day<7){//�ϸ���
				calNow.add(Calendar.MONTH, -1);
				queryMonthData(calNow,conn);
				calNow.add(Calendar.MONTH, 1);
			}
			//�����
			queryMonthData(calNow,conn);
		}
		conn.close();
	}
	private static void queryMonthData(Calendar cal,DruidPooledConnection conn) throws Exception{
		String thisStrDate = DateUtil.format(cal, "yyyy-MM");
		LogTool.logger.info("����ִ��"+thisStrDate);
		Calendar tempCal = (Calendar) cal.clone();
		int curYear = tempCal.get(Calendar.YEAR);
		int curMonth = tempCal.get(Calendar.MONTH)+1;
		//��ѯ�����Ƿ����
		String exitSql = "select * from t_month_temp where year=%s";
		exitSql = String.format(exitSql, curYear);
		DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) conn.prepareStatement(exitSql);
		ResultSet rs = ps.executeQuery();
		rs.last();
		int rows = rs.getRow();
		rs.close();
		Boolean isInsert = rows>0?false:true;
		String sql = "";
		if(isInsert){
			LogTool.logger.info("ִ�в���");
			sql = String.format(fInsertSql, curMonth);//����
		}
		else{
			LogTool.logger.info("ִ�и���");
			sql = String.format(fUpdateSql, curMonth);//����
		}
		ps = (DruidPooledPreparedStatement) conn.prepareStatement(sql);
		tempCal.set(Calendar.DATE, 1);//��Ϊ��һ��
		String strStartDateTime = DateUtil.format(tempCal, "yyyyMMdd000000");
		tempCal.add(Calendar.MONTH, 1);
		tempCal.add(Calendar.DATE, -1);
		String strEndDateTime = DateUtil.format(tempCal, "yyyyMMdd000000");
		//String fParam = "userId=BCCD_QHZX_PJS&pwd=pjs20160106&interfaceId=statSurfEleInRegion"
		String fParam = "userId=BENN_QXT_SPD&pwd=5840232&interfaceId=statSurfEleInRegion"
				+ "&dataCode=SURF_CHN_MUL_DAY&elements=Station_Name,Province,Station_Id_C&statEles=AVG_TEM_Avg"
				+ "&timeRange=[%s,%s]&adminCodes=500000,510000,520000,530000,540000&dataFormat=json";
		String param = String.format(fParam, strStartDateTime,strEndDateTime);
		//String host = "10.158.89.55";
		String host = "10.158.89.55";
		URI uri = new URI("http", host, "/cimiss-web/api", param, "");
		URL url = uri.toURL();
		LogTool.logger.info("��ʼcimiss��ѯ");
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
		LogTool.logger.info("��ѯcimiss�ɹ�!");
		String rstData = retStr.toString();
		Gson gson = new Gson();
		JsonObject jo = new JsonParser().parse(rstData).getAsJsonObject();
		int returnCode =  jo.get("returnCode").getAsInt();
	 	if(returnCode!=0){
	 		System.out.println("��ѯ����!");
 			return;
	 	}
	 	JsonArray ja =  jo.get("DS").getAsJsonArray();
	 	int dataCount = ja.size();
	 	for(int i=0;i<dataCount;i++){
	 		String objData = ja.get(i).toString();
 			JsonObject jsonSub = new JsonParser().parse(objData).getAsJsonObject();
 			String province = jsonSub.get("Province").getAsString();
 			String stationNum = jsonSub.get("Station_Id_C").getAsString();
 			Boolean isNum = CommonUtil.isNumeric(stationNum.charAt(0));
 			if(!isNum) {//��������վ
 				LogTool.logger.info(stationNum+"������!");
 				continue;
 			}
 			String staionName = jsonSub.get("Station_Name").getAsString();
 			if(staionName.equals("�ɶ�")){
 				province = "�Ĵ�ʡ";
 			}
 			String staionCode = jsonSub.get("Station_Id_C").getAsString();
 			double val = 0;
 			if(!jsonSub.get("AVG_TEM_Avg").getAsString().equals("")){
 				val = jsonSub.get("AVG_TEM_Avg").getAsDouble();
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
	 	LogTool.logger.info("ִ����������!");
	 	ps.executeBatch();
	 	LogTool.logger.info("�����������!");
		conn.commit();
	}
}
