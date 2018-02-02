package com.spd.ws;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.ClimDataQueryBus;
import com.spd.business.CommonStatisticsFilter;
import com.spd.common.ClimDataQueryParam;
import com.spd.common.ClimDataQueryRangeParam;
import com.spd.common.EleTypes;
import com.spd.common.TimesParam;
import com.spd.tool.LogTool;

/**
 * 资料信息检索
 * @author Administrator
 *
 */
@Stateless
@Path("ClimDataQuery")
public class ClimDataQuery {

	/**
	 * 
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryClimByTimesRangeAndElement")
	@Produces("application/json")
	public Object queryClimByTimesRangeAndElement(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		Set<String> station_Id_CSet = null;
		ClimDataQueryRangeParam climDataQueryRangeParam = new ClimDataQueryRangeParam();
		try {
			jsonObject = new JSONObject(para);
			String startTime = jsonObject.getString("startTime");
			String endTime = jsonObject.getString("endTime");
			timesParam.setStartTimeStr(startTime);
			timesParam.setEndTimeStr(endTime);
			climDataQueryRangeParam.setTimesParam(timesParam);
			String tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			climDataQueryRangeParam.setTableName(tableName);
			//SEQ STATION 分别表示按序列，按站号
			String orderType = jsonObject.getString("orderType");
			climDataQueryRangeParam.setOrderType(orderType);
			ClimDataQueryBus climDataQueryBus = new ClimDataQueryBus();
			List<LinkedHashMap> result = climDataQueryBus.queryClimByTimesRangeAndElement(climDataQueryRangeParam);
			//是否要过滤
			station_Id_CSet = getStationSets(jsonObject);
			CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
			List<LinkedHashMap> result2 = commonStatisticsFilter.filterClimData(result);
			return result2;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		
	}
	
	/**
	 * 根据日期，查询各种统计要素
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryClimByTime")
	@Produces("application/json")
	public Object queryClimByTime(@FormParam("para") String para) {
		ClimDataQueryParam climDataQueryParam = new ClimDataQueryParam();
		JSONObject jsonObject = null;
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			String time = jsonObject.getString("time");
			climDataQueryParam.setTime(time);
			String elements = jsonObject.getString("elements");
			climDataQueryParam.setElements(elements);
			String orderType = jsonObject.getString("orderType");
			climDataQueryParam.setOrderType(orderType);
			climDataQueryParam.setTime(time);
			ClimDataQueryBus climDataQueryBus = new ClimDataQueryBus();
			List result = climDataQueryBus.queryClimByTime(climDataQueryParam);
			station_Id_CSet = getStationSets(jsonObject);
			CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
			List result2 = commonStatisticsFilter.filterClimDataByTimes(result);
			return result2;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	private Set<String> getStationSets(JSONObject jsonObject) {
		Set<String> station_Id_CSet = new LinkedHashSet<String>();
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			String station_Id_Cs;
			try {
				station_Id_Cs = jsonObject.getString("station_Id_Cs");
				String[] station_id_CArray = station_Id_Cs.split(",");
				for(int i = 0; i < station_id_CArray.length; i++) {
					station_Id_CSet.add(station_id_CArray[i]);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return station_Id_CSet;
	}
}
