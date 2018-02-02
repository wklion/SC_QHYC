package com.spd.ws;

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

import com.spd.business.CommonStatisticsFilter;
import com.spd.business.WinAvgCloCovHourBus;
import com.spd.common.CloCovParam;
import com.spd.common.CloCovResult;
import com.spd.common.TimesParam;
import com.spd.common.WinAvg2MinParam;
import com.spd.common.WinAvg2MinResult;
import com.spd.tool.LogTool;

/**
 * 小时的瞬时风、云量相关服务
 * @author Administrator
 *
 */
@Stateless
@Path("WinAvgCloCovHourService")
public class WinAvgCloCovHourService {

	/**
	 * 查询瞬时风，根据时间段
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryWinAvg2MinByTimeRange")
	@Produces("application/json")
	public Object queryWinAvg2MinByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTime = jsonObject.getString("startTime");
			String endTime = jsonObject.getString("endTime");
			String stationType = jsonObject.getString("stationType");
			WinAvg2MinParam winAvg2MinParam = new WinAvg2MinParam();
			TimesParam timesParam = new TimesParam();
			timesParam.setStartTimeStr(startTime);
			timesParam.setEndTimeStr(endTime);
			winAvg2MinParam.setTimesParam(timesParam);
			winAvg2MinParam.setStationType(stationType);
			WinAvgCloCovHourBus winAvgCloCovHourBus = new WinAvgCloCovHourBus();
			List<WinAvg2MinResult> list = winAvgCloCovHourBus.queryWinAvg2MinByTimeRange(winAvg2MinParam);
			//区县用户登陆，需要过滤
			Set<String> station_Id_CSet = getStationSets(jsonObject);
			CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
			list = commonStatisticsFilter.filterWinAvg(list);
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 查询云量
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryCloCovByTimeRange")
	@Produces("application/json")
	public Object queryCloCovByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTime = jsonObject.getString("startTime");
			String endTime = jsonObject.getString("endTime");
			String stationType = jsonObject.getString("stationType");
			CloCovParam cloCovParam = new CloCovParam();
			TimesParam timesParam = new TimesParam();
			timesParam.setStartTimeStr(startTime);
			timesParam.setEndTimeStr(endTime);
			cloCovParam.setTimesParam(timesParam);
			cloCovParam.setStationType(stationType);
			WinAvgCloCovHourBus winAvgCloCovHourBus = new WinAvgCloCovHourBus();
			List<CloCovResult> list = winAvgCloCovHourBus.queryCloCovByTimeRange(cloCovParam);
			//区县用户登陆，需要过滤
			Set<String> station_Id_CSet = getStationSets(jsonObject);
			CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
			list = commonStatisticsFilter.filterCloCov(list);
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
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
