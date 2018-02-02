package com.spd.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.ExtBus;
import com.spd.business.StatisticsBus;
import com.spd.common.EleTypes;
import com.spd.common.ExtHisResult;
import com.spd.common.ExtParam;
import com.spd.common.ExtResult;
import com.spd.common.RankParam;
import com.spd.tool.LogTool;

/**
 * 极值统计
 * @author Administrator
 *
 */
@Stateless
@Path("ExtStatisticsService")
public class ExtStatisticsService {

	private static ExtBus extBus = new ExtBus();
	
	/**
	 * 极值统计
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
		boolean isHistory = false;
		int startYear = 0, endYear = 0, startMon = 0, endMon = 0, startDay = 0, endDay = 0;
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			if(jsonObject.has("isHistory")) {
				isHistory = jsonObject.getBoolean("isHistory");
			}
			startYear = Integer.parseInt(startTime.substring(0, 4));
			endYear = Integer.parseInt(endTime.substring(0, 4));
			startMon = Integer.parseInt(startTime.substring(5, 7));
			endMon = Integer.parseInt(endTime.substring(5, 7));
			startDay = Integer.parseInt(startTime.substring(8, 10));
			endDay = Integer.parseInt(endTime.substring(8, 10));
			// 构造参数
			ExtParam extParam = new ExtParam();
			extParam.setStartYear(startYear);
			extParam.setStartMon(startMon);
			extParam.setStartDay(startDay);
			extParam.setEndYear(endYear);
			extParam.setEndMon(endMon);
			extParam.setEndDay(endDay);
			extParam.setTableName(tableName);
			extParam.setStartDateTime(startTime);
			extParam.setEndDateTime(endTime);
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				extParam.setStationType(stationType);
			}
			//调用接口
			Object result = null;
			if(isHistory) {
				result = extBus.statisticsHisRangTime(extParam);
				result = filterExtHisResult((List<ExtHisResult>) result, jsonObject);
			} else {
				result = extBus.statisticsRangTime(extParam);
				result = filterExtResult((List<ExtResult>) result, jsonObject);
			}
			long end = System.currentTimeMillis();
			System.out.println("极值统计，花费时间【" + (end - start) + "】");
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	public List<ExtResult> filterExtResult(List<ExtResult> list, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			List<ExtResult> list2 = new ArrayList<ExtResult>();
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
					ExtResult item = list.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						list2.add(item);
						break;
					}
				}
			}
			for(int i = 0; i < list2.size(); i++) {
				ExtResult extResult = list2.get(i);
				extResult.setIndex(i + 1);
			}
			return list2;
		} else {
			return list;
		}
	}
	
	public List<ExtHisResult> filterExtHisResult(List<ExtHisResult> list, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			List<ExtHisResult> list2 = new ArrayList<ExtHisResult>();
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
					ExtHisResult item = list.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						list2.add(item);
						break;
					}
				}
			}
			//重置索引
			for(int i = 0; i < list2.size(); i++) {
				ExtHisResult extHisResult = list2.get(i);
				extHisResult.setIndex(i + 1);
			}
			return list2;
		} else {
			return list;
		}
	}
}
