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

import com.spd.business.PersistBus;
import com.spd.common.EleTypes;
import com.spd.common.ExtResult;
import com.spd.common.PersistParam;
import com.spd.common.PersistRainParam;
import com.spd.common.PersistTmpParam;
import com.spd.common.RainResult;
import com.spd.common.TmpResult;
import com.spd.tool.LogTool;

/**
 * 持续统计
 * @author Administrator
 *
 */
@Stateless
@Path("PersistStatisticsService")
public class PersistStatisticsService {
	
	/**
	 * 持续统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("persist")
	@Produces("application/json")
	public Object persist(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName, filterType;
		String startTime, endTime;
		double min = 0, max = 0, contrast = 0; // 界限值
		int startMon, endMon, startDay, endDay; //时段界限。月，日
		int startYear, endYear; //对比年份。
		String stationIdCs; // 过滤的站点
		PersistParam persistParam = new PersistParam();
		try {
			jsonObject = new JSONObject(para);
			//要素
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			//过滤
			filterType = jsonObject.getString("FilterType");
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			startYear = Integer.parseInt(startTime.substring(0, 4));
			startMon = Integer.parseInt(startTime.substring(5, 7));
			startDay = Integer.parseInt(startTime.substring(8, 10));
			endYear = Integer.parseInt(endTime.substring(0, 4));
			endMon = Integer.parseInt(endTime.substring(5, 7));
			endDay = Integer.parseInt(endTime.substring(8, 10));
			persistParam.setStartDateTime(startTime);
			persistParam.setEndDateTime(endTime);
			persistParam.setStartYear(startYear);
			persistParam.setStartMon(startMon);
			persistParam.setStartDay(startDay);
			persistParam.setEndYear(endYear);
			persistParam.setEndMon(endMon);
			persistParam.setEndDay(endDay);
			persistParam.setTableName(tableName);
			persistParam.setFilterType(filterType);
			if(filterType != null && !"".equals(filterType)) {
				if(jsonObject.has("min")) {
					min = jsonObject.getDouble("min");
					persistParam.setMin(min);
				}
				if(jsonObject.has("max")) {
					max = jsonObject.getDouble("max");
					persistParam.setMax(max);
				}
				if(jsonObject.has("contrast")) {
					contrast = jsonObject.getDouble("contrast");
					persistParam.setContrast(contrast);
				}
			}
			stationIdCs = jsonObject.getString("station_Id_Cs");
			persistParam.setStationIdCs(stationIdCs);
			PersistBus persistBus = new PersistBus();
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				persistParam.setStationType(stationType);
			}
			Object result = persistBus.persist(persistParam);
			long end = System.currentTimeMillis();
			System.out.println("持续统计，花费时间【" + (end - start) + "】");
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 气温统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("tmp")
	@Produces("application/json")
	public Object tmp(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName, type;
		String startTime, endTime;
		int startMon, endMon, startDay, endDay; //时段界限。月，日
		int startYear, endYear; //对比年份。
		PersistTmpParam persistTmpParam = new PersistTmpParam();
		try {
			jsonObject = new JSONObject(para);
			//要素
			//变化类型
			type = jsonObject.getString("type");
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			startYear = Integer.parseInt(startTime.substring(0, 4));
			startMon = Integer.parseInt(startTime.substring(5, 7));
			startDay = Integer.parseInt(startTime.substring(8, 10));
			endYear = Integer.parseInt(endTime.substring(0, 4));
			endMon = Integer.parseInt(endTime.substring(5, 7));
			endDay = Integer.parseInt(endTime.substring(8, 10));
			persistTmpParam.setStartDateTime(startTime);
			persistTmpParam.setEndDateTime(endTime);
			persistTmpParam.setStartYear(startYear);
			persistTmpParam.setStartMon(startMon);
			persistTmpParam.setStartDay(startDay);
			persistTmpParam.setEndYear(endYear);
			persistTmpParam.setEndMon(endMon);
			persistTmpParam.setEndDay(endDay);
			persistTmpParam.setType(type);
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				persistTmpParam.setStationType(stationType);
			}
			PersistBus persistBus = new PersistBus();
			List<TmpResult> result = persistBus.tmp(persistTmpParam);
			List<TmpResult> result2 = filterExtResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 晴雨
	 * @param para
	 * @return
	 */
	@POST
	@Path("rain")
	@Produces("application/json")
	public Object rain(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName, type;
		String startTime, endTime, changeType;
		int startMon, endMon, startDay, endDay; //时段界限。月，日
		int startYear, endYear; //对比年份。
		PersistRainParam persistRainParam = new PersistRainParam();
		try {
			jsonObject = new JSONObject(para);
			//类型
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			changeType = jsonObject.getString("changeType");
			startYear = Integer.parseInt(startTime.substring(0, 4));
			startMon = Integer.parseInt(startTime.substring(5, 7));
			startDay = Integer.parseInt(startTime.substring(8, 10));
			endYear = Integer.parseInt(endTime.substring(0, 4));
			endMon = Integer.parseInt(endTime.substring(5, 7));
			endDay = Integer.parseInt(endTime.substring(8, 10));
			persistRainParam.setStartDateTime(startTime);
			persistRainParam.setStartYear(startYear);
			persistRainParam.setStartMon(startMon);
			persistRainParam.setStartDay(startDay);
			persistRainParam.setEndDateTime(endTime);
			persistRainParam.setEndYear(endYear);
			persistRainParam.setEndMon(endMon);
			persistRainParam.setEndDay(endDay);
			persistRainParam.setTableName(tableName);
			persistRainParam.setChangeType(changeType);
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				persistRainParam.setStationType(stationType);
			}
			PersistBus persistBus = new PersistBus();
			List<RainResult> result = persistBus.rain(persistRainParam);
			List<RainResult> result2 = filterRaintResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	private List<TmpResult> filterExtResult(List<TmpResult> list, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			List<TmpResult> list2 = new ArrayList<TmpResult>();
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
					TmpResult item = list.get(j);
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
	
	private List<RainResult> filterRaintResult(List<RainResult> list, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			List<RainResult> list2 = new ArrayList<RainResult>();
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
					RainResult item = list.get(j);
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
