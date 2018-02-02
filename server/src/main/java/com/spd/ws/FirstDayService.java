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

import com.spd.business.FirstDayBus;
import com.spd.common.EleTypes;
import com.spd.common.FirstDayParam;
import com.spd.common.FirstDayResult;
import com.spd.tool.LogTool;

/**
 * 初日统计
 * @author Administrator
 *
 */
@Stateless
@Path("FirstDayService")
public class FirstDayService {

	/**
	 * 初日降水、初日高温
	 * @param para
	 * @return
	 */
	@POST
	@Path("rainTmpFirst")
	@Produces("application/json")
	public Object rainTmpFirst(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String tableName = null;
		double value; // 对比的值，比如 气温>= 35、降水>=10mm
		int year; // 比较年份
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("type"));
			value = jsonObject.getDouble("value");
			year = jsonObject.getInt("year");
			FirstDayParam firstDayParam = new FirstDayParam();
			firstDayParam.setTableName(tableName);
			firstDayParam.setValue(value);
			firstDayParam.setYear(year);
			int startMon = 0, startDay = 0, endMon = 0, endDay = 0;
			startMon = jsonObject.getInt("startMon");
			startDay = jsonObject.getInt("startDay");
			endMon = jsonObject.getInt("endMon");
			endDay = jsonObject.getInt("endDay");
			firstDayParam.setStartMon(startMon);
			firstDayParam.setStartDay(startDay);
			firstDayParam.setEndMon(endMon);
			firstDayParam.setEndDay(endDay);
			if(jsonObject.has("constatStartYear")) {
				int constatStartYear = jsonObject.getInt("constatStartYear");
				firstDayParam.setConstatStartYear(constatStartYear);
			}
			if(jsonObject.has("constatEndYear")) {
				int constatEndYear = jsonObject.getInt("constatEndYear");
				firstDayParam.setConstatEndYear(constatEndYear);
			}
			FirstDayBus firstDayBus = new FirstDayBus();
			List<FirstDayResult> result = firstDayBus.rainTmpFirst(firstDayParam);
			List<FirstDayResult> result2 = filterExtResult(result, jsonObject); 
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	private List<FirstDayResult> filterExtResult(List<FirstDayResult> list, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			List<FirstDayResult> list2 = new ArrayList<FirstDayResult>();
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
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < list.size(); j++) {
					FirstDayResult item = list.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						list2.add(item);
						break;
					}
				}
			}
			return list2;
		} else {
			return list;
		}
	}
}
