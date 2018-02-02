package com.spd.grid.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.spd.grid.domain.ApplicationContextFactory;
import com.spd.grid.domain.DatasourceConnectionConfigInfo;
import com.spd.grid.domain.StationVal;
import com.spd.grid.jdbc.DataSource;
import com.spd.grid.service.HosUtil;
import com.spd.grid.service.LiveUtil;
import com.spd.grid.tool.LogTool;

@Stateless
@Path("DBService")
public class DBService {
	@POST
	@Path("getRealVal")
	@Produces("application/json")
	public Object getRealVal(@FormParam("para") String para){
    	ArrayList<Object> result = null;
		JSONObject jsonObject=null;
		SimpleDateFormat needFmt=new SimpleDateFormat("月MM日dd");
		String needFields="";
		String filterField="";
		result = new ArrayList<Object>();
		try {
			jsonObject = new JSONObject(para);
			String datetime = jsonObject.getString("datetime");
			SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd");
			Date date=myFmt.parse(datetime);
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH,1);
			for(int d=0;d<30;d++){
				Date CurrentDate=cal.getTime();
				String curDate=needFmt.format(CurrentDate);
				curDate=curDate.replace("月","M");
				curDate=curDate.replace("日","D");
				if(d==0){
					filterField=curDate;
				}
				needFields+=curDate+",";
				cal.add(Calendar.DAY_OF_MONTH,1);
			}
			needFields=needFields.substring(0,needFields.length()-1);
			
			DataSource dataSource=DataSource.getBaseInstance();
			Connection conn=dataSource.getBaseConnection();
			Statement  stmt = conn.createStatement();
			String sqlTemp = "select STATION_ID_C as stationnum,%s from t_hos_avgtemp where STARTTIME='%s'";
			sqlTemp=String.format(sqlTemp, needFields,filterField);
			ResultSet resultSet = stmt.executeQuery(sqlTemp);
			while(resultSet.next()) {
				Object[] obj=new Object[32];
				for(int r=0;r<31;r++){
					if(r==0){
						obj[r]=resultSet.getString(r+1);
					}
					else{
						obj[r]=resultSet.getDouble(r+1);
					}
					
				}
				obj[31]="temp";
				result.add(obj);
			}
			String sqlPrec = "select STATION_ID_C as stationnum,%s from t_hos_rain where STARTTIME='%s'";
			sqlPrec=String.format(sqlPrec, needFields,filterField);
			resultSet = stmt.executeQuery(sqlPrec);
			while(resultSet.next()) {
				Object[] obj=new Object[32];
				for(int r=0;r<31;r++){
					if(r==0){
						obj[r]=resultSet.getString(r+1);
					}
					else{
						obj[r]=resultSet.getDouble(r+1);
					}
				}
				obj[31]="prec";
				result.add(obj);
			}
			stmt.close();
			conn.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@POST
	@Path("GetLiveYearMonthData")
	@Produces("application/json")
	public Object GetLiveYearMonthData(@FormParam("para") String para ){
		String dateTime="";//日期
		String elementid="";
		try{
			JSONObject jo = new JSONObject(para);
			elementid=jo.getString("elementid");
			dateTime=jo.getString("datetime");
		}
		catch(Exception ex){
			LogTool.logger.error("GetLiveYearMonthData()--解析参数出错!");
		}
		String[] strDate=dateTime.split("-");
		int year=Integer.parseInt(strDate[0]);
		int month=Integer.parseInt(strDate[1]);
		LiveUtil lu=new LiveUtil();
		List<StationVal> lsResult=lu.GetLiveMonthData(elementid,year,month);
		return lsResult;
	}
	@POST
	@Path("GetHosYearMonthData")
	@Produces("application/json")
	public Object GetHosYearMonthData(@FormParam("para") String para){
		String dateTime="";//日期
		String elementid="";
		try{
			JSONObject jo = new JSONObject(para);
			elementid=jo.getString("elementid");
			dateTime=jo.getString("datetime");
		}
		catch(Exception ex){
			LogTool.logger.error("GetHosYearMonthData()--解析参数出错!");
		}
		String[] strDate=dateTime.split("-");
		int month=Integer.parseInt(strDate[1]);
		HosUtil hu=new HosUtil();
		List<StationVal> lsResult=hu.GetHosMonthData(elementid,month);
		return lsResult;
	}
}
