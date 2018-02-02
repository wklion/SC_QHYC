package com.spd.datadeal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Calendar;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spd.jdbc.DbPoolConnection;
import com.spd.util.DateUtil;

public class MonthPrec {

	public static void main(String[] args) throws Exception {
		String fParam = "userId=BCLZ_ZXT_zxtybs&pwd=yubaoshi&interfaceId=getSurfEleInRegionByTime&dataCode=SURF_CHN_MUL_MON&times=%s&adminCodes=500000,510000,520000,530000,540000&elements=Station_Name,Province,Station_Id_C,PRE_Time_0808&staLevels=011,012,013&dataFormat=json";
		String fInsertSql = "insert into t_month_rain(province,stationname,stationnum,year,m%d) values(?,?,?,?,?)";
		String fUpdateSql = "update t_month_rain set m%d=? where year=? and stationnum=?";
		Calendar calStart = Calendar.getInstance();//起始日期
		calStart.set(Calendar.YEAR, 1981);
		calStart.set(Calendar.MONTH, 0);
		calStart.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar calEnd = Calendar.getInstance();//结束日期
		
		DbPoolConnection dbp = DbPoolConnection.getInstance();
		DruidPooledConnection conn = dbp.getConnection();
		conn.setAutoCommit(false);
		
		while(calStart.compareTo(calEnd)==-1) {//晚于
			String strDate = DateUtil.sdf_yyyyMMdd000000.format(calStart.getTime());
			
			System.out.println("当前日期:"+strDate);
			int year = calStart.get(Calendar.YEAR);
			int month = calStart.get(Calendar.MONTH)+1;
			String sql = "";
			if(month==1) {
				sql = String.format(fInsertSql, month);
			}
			else {
				sql = String.format(fUpdateSql, month);
			}
			DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) conn.prepareStatement(sql);
			String param = String.format(fParam, strDate);
			URI uri = new URI("http", "10.166.89.55", "/cimiss-web/api", param, "");
			URL url = uri.toURL();
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
			String rstData = retStr.toString();
			Gson gson = new Gson();
			JsonObject jo = new JsonParser().parse(rstData).getAsJsonObject();
			int returnCode =  jo.get("returnCode").getAsInt();
    	 	if(returnCode!=0){
    	 		System.out.println("查询有误!");
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
	 			String staionCode = jsonSub.get("Station_Id_C").getAsString();
	 			Double val = jsonSub.get("PRE_Time_0808").getAsDouble();
	 			if(month==1) {
	 				ps.setString(1, province);
					ps.setString(2, staionName);
					ps.setString(3, staionCode);
					ps.setInt(4, year);
					ps.setDouble(5, val);
	 			}
	 			else {
	 				ps.setDouble(1, val);
	 				ps.setInt(2, year);
					ps.setString(3, staionCode);
	 			}
				ps.addBatch();
    	 	}
       	 	ps.executeBatch();
			conn.commit();
			calStart.add(Calendar.MONTH, 1);
		}
	}

}
