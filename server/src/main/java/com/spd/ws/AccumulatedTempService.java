package com.spd.ws;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.AccumulatedTempBus;
import com.spd.common.AccumulatedTempParam;
import com.spd.common.AccumulatedTempResult;
import com.spd.common.AccumulatedTempYearResult;
import com.spd.common.ActiveAccumulatedTemp;
import com.spd.common.TimesParam;
import com.spd.common.ValidAccumulatedTemp;
import com.spd.pojo.RankResult;
import com.spd.tool.LogTool;

/**
 * 积温计算
 * @author Administrator
 *
 */
@Stateless
@Path("AccumulatedTempService")
public class AccumulatedTempService {

	/**
	 * 积温计算
	 * @param para
	 * @return
	 */
	@POST
	@Path("accumulatedTempByTimes")
	@Produces("application/json")
	public Object accumulatedTempByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		AccumulatedTempParam accumulatedTempParam = new AccumulatedTempParam();
		TimesParam timesParam = new TimesParam();
		int perennialStartYear, perennialEndYear;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			accumulatedTempParam.setTimesParam(timesParam);
			perennialStartYear = jsonObject.getInt("perennialStartYear");
			perennialEndYear = jsonObject.getInt("perennialEndYear");
			accumulatedTempParam.setPerennialEndYear(perennialEndYear);
			accumulatedTempParam.setPerennialStartYear(perennialStartYear);
			double minTmp = jsonObject.getDouble("minTmp");
			accumulatedTempParam.setMinTmp(minTmp);
			AccumulatedTempBus accumulatedTempBus = new AccumulatedTempBus();
			AccumulatedTempResult result = accumulatedTempBus.accumulatedTempByTimes(accumulatedTempParam);
			AccumulatedTempResult result2 = filter(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 计算积温历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("accumulatedTempByYears")
	@Produces("application/json")
	public Object accumulatedTempByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		AccumulatedTempParam accumulatedTempParam = new AccumulatedTempParam();
		TimesParam timesParam = new TimesParam();
		int perennialStartYear, perennialEndYear;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			accumulatedTempParam.setTimesParam(timesParam);
			perennialStartYear = jsonObject.getInt("perennialStartYear");
			perennialEndYear = jsonObject.getInt("perennialEndYear");
			accumulatedTempParam.setPerennialEndYear(perennialEndYear);
			accumulatedTempParam.setPerennialStartYear(perennialStartYear);
			double minTmp = jsonObject.getDouble("minTmp");
			accumulatedTempParam.setMinTmp(minTmp);
			String station_Id_C = jsonObject.getString("station_Id_C");
			accumulatedTempParam.setStation_Id_C(station_Id_C);
			AccumulatedTempBus accumulatedTempBus = new AccumulatedTempBus();
			AccumulatedTempYearResult result = accumulatedTempBus.accumulatedTempByYeaer(accumulatedTempParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	private AccumulatedTempResult filter(AccumulatedTempResult result, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		List<RankResult> result2 = new ArrayList<RankResult>();
		if(flag) {
			AccumulatedTempResult accumulatedTempResult2 = new AccumulatedTempResult();
			LinkedList<String> stationList = new LinkedList<String>();
			try {
				String station_Id_Cs = (String) jsonObject.get("station_Id_Cs");
				String[] station_id_CItems = station_Id_Cs.split(",");
				for(int i = 0; i < station_id_CItems.length; i++) {
					stationList.add(station_id_CItems[i]);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//过滤
			List<ActiveAccumulatedTemp> activeAccumulatedTempList = result.getActiveAccumulatedTempList();
			List<ValidAccumulatedTemp> validAccumulatedTempList = result.getValidAccumulatedTempList();
			List<ActiveAccumulatedTemp> activeAccumulatedTempList2 = new ArrayList<ActiveAccumulatedTemp>();
			List<ValidAccumulatedTemp> validAccumulatedTempList2 = new ArrayList<ValidAccumulatedTemp>();
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < activeAccumulatedTempList.size(); j++) {
					ActiveAccumulatedTemp itemActiveAccumulatedTemp = activeAccumulatedTempList.get(j);
					String itemStation_Id_C = itemActiveAccumulatedTemp.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						activeAccumulatedTempList2.add(itemActiveAccumulatedTemp);
						break;
					}
				}
				for(int j = 0; j < validAccumulatedTempList.size(); j++) {
					ValidAccumulatedTemp itemValidAccumulatedTemp = validAccumulatedTempList.get(j);
					String itemStation_Id_C = itemValidAccumulatedTemp.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						validAccumulatedTempList2.add(itemValidAccumulatedTemp);
						break;
					}
				}
			}
			accumulatedTempResult2.setActiveAccumulatedTempList(activeAccumulatedTempList2);
			accumulatedTempResult2.setValidAccumulatedTempList(validAccumulatedTempList2);
			return accumulatedTempResult2;
		} else {
			return result;
		}
	}
}
