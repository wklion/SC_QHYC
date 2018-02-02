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

import com.spd.business.DaysBus;
import com.spd.business.ExtBus;
import com.spd.common.DaysParam;
import com.spd.common.DaysResult;
import com.spd.common.EleTypes;
import com.spd.common.ExtResult;
import com.spd.tool.LogTool;

/**
 * 日数统计接口
 * @author Administrator
 *
 */
@Stateless
@Path("DaysStatisticsService")
public class DaysStatisticsService {

	private static ExtBus extBus = new ExtBus();
	
	/**
	 * 日数统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("ext")
	@Produces("application/json")
	public Object ext(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName;
		String startTime, endTime;
		String filterType = "";
		double max, min, contrast;
		int startYear = 0, endYear = 0, startMon = 0, endMon = 0, startDay = 0, endDay = 0;
		DaysParam daysParam = new DaysParam();
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			startYear = Integer.parseInt(startTime.substring(0, 4));
			startMon = Integer.parseInt(startTime.substring(5, 7));
			startDay = Integer.parseInt(startTime.substring(8, 10));
			endYear = Integer.parseInt(endTime.substring(0, 4));
			endMon = Integer.parseInt(endTime.substring(5, 7));
			endDay = Integer.parseInt(endTime.substring(8, 10));
			filterType = jsonObject.getString("FilterType");
			daysParam.setTableName(tableName);
			daysParam.setStartDateTime(startTime);
			daysParam.setEndDateTime(endTime);
			daysParam.setFilterType(filterType);
			daysParam.setStartYear(startYear);
			daysParam.setStartMon(startMon);
			daysParam.setStartDay(startDay);
			daysParam.setEndYear(endYear);
			daysParam.setEndMon(endMon);
			daysParam.setEndDay(endDay);
			//startYear, endYear被占用的情况下的处理方式
			int constantStartYear = jsonObject.getInt("startYear");
			daysParam.setConstantStartYear(constantStartYear);
			int constantEndYear = jsonObject.getInt("endYear");
			daysParam.setConstantEndYear(constantEndYear);
			if(filterType != null && !"".equals(filterType)) {
				if(jsonObject.has("min")) {
					min = jsonObject.getDouble("min");
					daysParam.setMin(min);
				}
				if(jsonObject.has("max")) {
					max = jsonObject.getDouble("max");
					daysParam.setMax(max);
				}
				if(jsonObject.has("contrast")) {
					contrast = jsonObject.getDouble("contrast");
					daysParam.setContrast(contrast);
				}
			}
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				daysParam.setStationType(stationType);
			}
			//调用接口
			DaysBus daysBus = new DaysBus();
			Object result = daysBus.daysAnaly(daysParam);
			Object result2 = filterDaysResult((List<DaysResult>) result, jsonObject);
			long end = System.currentTimeMillis();
			System.out.println("极值统计，花费时间【" + (end - start) + "】");
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	public List<DaysResult> filterDaysResult(List<DaysResult> list, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			List<DaysResult> list2 = new ArrayList<DaysResult>();
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
					DaysResult item = list.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						list2.add(item);
						break;
					}
				}
			}
			//重新排序
			for(int i = 0; i < list2.size(); i++) {
				DaysResult daysResult = list2.get(i);
				daysResult.setIndex(i + 1);
			}
			return list2;
		} else {
			return list;
		}
	}
}
