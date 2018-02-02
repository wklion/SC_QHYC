package com.spd.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.ContinuousRainStatisticsBus;
import com.spd.business.DisasterBus;
import com.spd.business.FogBus;
import com.spd.business.FrostBus;
import com.spd.business.HailBus;
import com.spd.business.HighTmpBus;
import com.spd.business.HourRainBus;
import com.spd.business.LowTmpBus;
import com.spd.business.MCIBus;
import com.spd.business.MaxWindBus;
import com.spd.business.RainStormBus;
import com.spd.business.SnowBus;
import com.spd.business.StrongCoolingBus;
import com.spd.business.ThundBus;
import com.spd.business.WepBus;
import com.spd.common.ContinueousRainYearsResult;
import com.spd.common.ContinuousRainResult;
import com.spd.common.ContinuousRainsDefineParam;
import com.spd.common.ContinuousRainsParam;
import com.spd.common.DisasterLowTmpParam;
import com.spd.common.DisasterRainFloodParam;
import com.spd.common.DisasterRainStormFinResult;
import com.spd.common.DisasterRainStormParam;
import com.spd.common.EleTypes;
import com.spd.common.FogResult;
import com.spd.common.FogYearsParam;
import com.spd.common.FrostRangeParam;
import com.spd.common.FrostResult;
import com.spd.common.FrostYearsParam;
import com.spd.common.HailSequenceResult;
import com.spd.common.HighTmpRangeParam;
import com.spd.common.HighTmpYearsParam;
import com.spd.common.HourRainExtParam;
import com.spd.common.HourRainHisExtParam;
import com.spd.common.HourRainHisRankParam;
import com.spd.common.HourRainRangeParam;
import com.spd.common.HourRainSequenceParam;
import com.spd.common.HourRainSortParam;
import com.spd.common.HourTimesParam;
import com.spd.common.LowTmpByRangeParam;
import com.spd.common.LowTmpResult;
import com.spd.common.MCILevelParam;
import com.spd.common.MCISequenceResult;
import com.spd.common.MCIStationSequenceResult;
import com.spd.common.MCIStatisticsParam;
import com.spd.common.MaxWindRangeParam;
import com.spd.common.MaxWindResult;
import com.spd.common.MaxWindYearsParam;
import com.spd.common.RainStormRangeParam;
import com.spd.common.RainStormYearsParam;
import com.spd.common.RainStormYearsResult;
import com.spd.common.SnowResult;
import com.spd.common.SnowYearsParam;
import com.spd.common.StrongCoolingParam;
import com.spd.common.StrongCoolingResult;
import com.spd.common.StrongCoolingYearsParam;
import com.spd.common.ThundResult;
import com.spd.common.ThundYearsParam;
import com.spd.common.TimesParam;
import com.spd.common.TimesRangeParam;
import com.spd.common.TimesYearsParam;
import com.spd.common.WepParam;
import com.spd.tool.LogTool;

/**
 * 灾害统计相关
 * @author Administrator
 *
 */
@Stateless
@Path("DisasterService")
public class DisasterService {
	
	private static DisasterBus disasterBus = new DisasterBus();
	/**
	 * 暴雨统计， 全部返回，客户端决定哪些展示，哪些不展示
	 * @param para
	 * @return
	 */
	@POST
	@Path("rainstorm")
	@Produces("application/json")
	public Object rainstorm(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		//分别对应的是暴雨，大暴，特大
		double level1 = 0, level2 = 0, level3 = 0;
		//开始结束时间 格式：yyyy-MM-dd
		String startTime = "", endTime = "", tableName = "";
		try {
			jsonObject = new JSONObject(para);
			level1 = jsonObject.getDouble("level1");
			level2 = jsonObject.getDouble("level2");
			level3 = jsonObject.getDouble("level3");
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			DisasterRainStormParam disasterRainStormParam = new DisasterRainStormParam();
			int startYear = Integer.parseInt(startTime.substring(0, 4));
			int startMon = Integer.parseInt(startTime.substring(5, 7));
			int startDay = Integer.parseInt(startTime.substring(8, 10));
			int endYear = Integer.parseInt(endTime.substring(0, 4));
			int endMon = Integer.parseInt(endTime.substring(5, 7));
			int endDay = Integer.parseInt(endTime.substring(8, 10));
			disasterRainStormParam.setLevel1(level1);
			disasterRainStormParam.setLevel2(level2);
			disasterRainStormParam.setLevel3(level3);
			disasterRainStormParam.setStartYear(startYear);
			disasterRainStormParam.setStartMon(startMon);
			disasterRainStormParam.setStartDay(startDay);
			disasterRainStormParam.setEndYear(endYear);
			disasterRainStormParam.setEndMon(endMon);
			disasterRainStormParam.setEndDay(endDay);
			disasterRainStormParam.setTableName(tableName);
			disasterRainStormParam.setStartTime(startTime);
			disasterRainStormParam.setEndTime(endTime);
			Object result = disasterBus.rainstorm(disasterRainStormParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 洪涝
	 * @param para
	 * @return
	 */
	@POST
	@Path("flood")
	@Produces("application/json")
	public Object flood(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		// 
		double level11DayRain, level12DayRain, level13DayRain, level21DayRain, level22DayRain, level23DayRain, level31DayRain, level32DayRain, level33DayRain;
		
		//开始结束时间 格式：yyyy-MM-dd
		String startTime = "", endTime = "", tableName = "";
		try {
			jsonObject = new JSONObject(para);
			
			level11DayRain = jsonObject.getDouble("level11DayRain");
			level12DayRain = jsonObject.getDouble("level12DayRain");
			level13DayRain = jsonObject.getDouble("level13DayRain");
			level21DayRain = jsonObject.getDouble("level21DayRain");
			level22DayRain = jsonObject.getDouble("level22DayRain");
			level23DayRain = jsonObject.getDouble("level23DayRain");
			level31DayRain = jsonObject.getDouble("level31DayRain");
			level32DayRain = jsonObject.getDouble("level32DayRain");
			level33DayRain = jsonObject.getDouble("level33DayRain");
			
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			DisasterRainFloodParam disasterRainFloodParam = new DisasterRainFloodParam();
			int startYear = Integer.parseInt(startTime.substring(0, 4));
			int startMon = Integer.parseInt(startTime.substring(5, 7));
			int startDay = Integer.parseInt(startTime.substring(8, 10));
			int endYear = Integer.parseInt(endTime.substring(0, 4));
			int endMon = Integer.parseInt(endTime.substring(5, 7));
			int endDay = Integer.parseInt(endTime.substring(8, 10));
			disasterRainFloodParam.setTableName(tableName);
			disasterRainFloodParam.setStartYear(startYear);
			disasterRainFloodParam.setStartMon(startMon);
			disasterRainFloodParam.setStartDay(startDay);
			disasterRainFloodParam.setEndYear(endYear);
			disasterRainFloodParam.setEndMon(endMon);
			disasterRainFloodParam.setEndDay(endDay);

			disasterRainFloodParam.setLevel11DayRain(level11DayRain);
			disasterRainFloodParam.setLevel12DayRain(level12DayRain);
			disasterRainFloodParam.setLevel13DayRain(level13DayRain);
			disasterRainFloodParam.setLevel21DayRain(level21DayRain);
			disasterRainFloodParam.setLevel22DayRain(level22DayRain);
			disasterRainFloodParam.setLevel23DayRain(level23DayRain);
			disasterRainFloodParam.setLevel31DayRain(level31DayRain);
			disasterRainFloodParam.setLevel32DayRain(level32DayRain);
			disasterRainFloodParam.setLevel33DayRain(level33DayRain);
			
			disasterRainFloodParam.setStartTime(startTime);
			disasterRainFloodParam.setEndTime(endTime);
			Object result = disasterBus.flood(disasterRainFloodParam);
			long end = System.currentTimeMillis();
			System.out.println("花费时间【" + (end -start) + "】");
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 低温阴雨
	 * @param para
	 * @return
	 */
	@POST
	@Path("lowTmpRain")
	@Produces("application/json")
	public Object lowTmpRain(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		int startMon, startDay, endMon, endDay, sequenceDays, rainDays, year;
		boolean isFilterRainDays = false;
		double avgTmp;
		DisasterLowTmpParam disasterLowTmpParam = new DisasterLowTmpParam();
		try {
			jsonObject = new JSONObject(para);
			startMon = jsonObject.getInt("startMon");
			startDay = jsonObject.getInt("startDay");
			endMon = jsonObject.getInt("endMon");
			endDay = jsonObject.getInt("endDay");
			avgTmp = jsonObject.getDouble("avgTmp");
			sequenceDays = jsonObject.getInt("sequenceDays");
			year = jsonObject.getInt("year");
//			isFilterRainDays = jsonObject.getBoolean("isFilterRainDays");
			disasterLowTmpParam.setStartMon(startMon);
			disasterLowTmpParam.setStartDay(startDay);
			disasterLowTmpParam.setEndMon(endMon);
			disasterLowTmpParam.setEndDay(endDay);
			disasterLowTmpParam.setSequenceDays(sequenceDays);
			disasterLowTmpParam.setYear(year);
			disasterLowTmpParam.setAvgTmp(avgTmp);
			disasterLowTmpParam.setFilterRainDays(isFilterRainDays);
			if(isFilterRainDays) {
				rainDays = jsonObject.getInt("rainDays");
				disasterLowTmpParam.setRainDays(rainDays);
			}
			Object result = disasterBus.lowTmpRain(disasterLowTmpParam);
			long end = System.currentTimeMillis();
			System.out.println("lowTmpRain花费时间【" + (end - start) + "】");
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段范围做连阴雨的查询和对比
	 * @param para
	 * @return
	 */
	@POST
	@Path("continuousRainsByRange")
	@Produces("application/json")
	public Object continuousRainsByRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		ContinuousRainsParam continuousRainsParam = new ContinuousRainsParam();
		TimesRangeParam timesRangeParam = new TimesRangeParam();
		ContinuousRainsDefineParam continuousRainsDefineParam = new ContinuousRainsDefineParam();
		String startTimeStr = "", endTimeStr = ""; 
//		int startYear = 0, endYear = 0;
		try {
			jsonObject = new JSONObject(para);
			startTimeStr = jsonObject.getString("startTimeStr");
			endTimeStr = jsonObject.getString("endTimeStr");
			timesRangeParam.setStartTimeStr(startTimeStr);
			timesRangeParam.setEndTimeStr(endTimeStr);
			continuousRainsParam.setTimesRangeParam(timesRangeParam);
//			startYear = jsonObject.getInt("startYear");
//			endYear = jsonObject.getInt("endYear");
//			continuousRainsParam.setStartYear(startYear);
//			continuousRainsParam.setEndYear(endYear);
			String stationIds = jsonObject.getString("station_Id_Cs");
			continuousRainsParam.setStationIds(stationIds);
			
			int slightNoSSHDays = jsonObject.getInt("slightNoSSHDays");
			int slightPreDays = jsonObject.getInt("slightPreDays");
			double slightMinValue = jsonObject.getDouble("slightMinValue");
			continuousRainsDefineParam.setSlightNoSSHDays(slightNoSSHDays);
			continuousRainsDefineParam.setSlightPreDays(slightPreDays);
			continuousRainsDefineParam.setSlightMinValue(slightMinValue);
			int severityNoSSHDays = jsonObject.getInt("severityNoSSHDays");
			int severityPreDays = jsonObject.getInt("severityPreDays");
			double severityMinValue = jsonObject.getDouble("severityMinValue");
			int terminPreDays = jsonObject.getInt("terminPreDays");
			double terminValue = jsonObject.getDouble("terminValue");
			continuousRainsDefineParam.setSeverityNoSSHDays(severityNoSSHDays);
			continuousRainsDefineParam.setSeverityPreDays(severityPreDays);
			continuousRainsDefineParam.setSeverityMinValue(severityMinValue);
			continuousRainsDefineParam.setTerminPreDays(terminPreDays);
			continuousRainsDefineParam.setTerminValue(terminValue);
			continuousRainsParam.setContinuousRainsDefineParam(continuousRainsDefineParam);
			
//			TimesYearsParam timesYearsParam = new TimesYearsParam(timesRangeParam.getStartMon(), timesRangeParam.getStartDay(), timesRangeParam.getEndMon(),
//					timesRangeParam.getEndDay(), startYear, endYear);
			ContinuousRainStatisticsBus continuousRainStatisticsBus = new ContinuousRainStatisticsBus();
			ContinuousRainResult result = continuousRainStatisticsBus.continuousRainsByRange(continuousRainsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 连阴雨 对单个站，或者一定区域内的站，或者指定站，进行逐年的时次对比
	 * @param para
	 * @return
	 */
	@POST
	@Path("continuousRainsYearsSequnence")
	@Produces("application/json")
	public Object continuousRainsYearsSequnence(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		ContinuousRainsParam continuousRainsParam = new ContinuousRainsParam();
		TimesRangeParam timesRangeParam = new TimesRangeParam();
		ContinuousRainsDefineParam continuousRainsDefineParam = new ContinuousRainsDefineParam();
		String startTimeStr = "", endTimeStr = ""; 
		int startYear = 0, endYear = 0; //历史年
		int perennialStartYear = 0, perennialEndYear = 0; //历史年
		try {
			jsonObject = new JSONObject(para);
			startTimeStr = jsonObject.getString("startTimeStr");
			endTimeStr = jsonObject.getString("endTimeStr");
			timesRangeParam.setStartTimeStr(startTimeStr);
			timesRangeParam.setEndTimeStr(endTimeStr);
			continuousRainsParam.setTimesRangeParam(timesRangeParam);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			perennialStartYear = jsonObject.getInt("perennialStartYear");
			perennialEndYear = jsonObject.getInt("perennialEndYear");
			continuousRainsParam.setStartYear(startYear);
			continuousRainsParam.setEndYear(endYear);
			String stationIds = jsonObject.getString("station_Id_Cs");
			continuousRainsParam.setStationIds(stationIds);
			
			int slightNoSSHDays = jsonObject.getInt("slightNoSSHDays");
			int slightPreDays = jsonObject.getInt("slightPreDays");
			double slightMinValue = jsonObject.getDouble("slightMinValue");
			continuousRainsDefineParam.setSlightNoSSHDays(slightNoSSHDays);
			continuousRainsDefineParam.setSlightPreDays(slightPreDays);
			continuousRainsDefineParam.setSlightMinValue(slightMinValue);
			int severityNoSSHDays = jsonObject.getInt("severityNoSSHDays");
			int severityPreDays = jsonObject.getInt("severityPreDays");
			double severityMinValue = jsonObject.getDouble("severityMinValue");
			int terminPreDays = jsonObject.getInt("terminPreDays");
			double terminValue = jsonObject.getDouble("terminValue");
			continuousRainsDefineParam.setSeverityNoSSHDays(severityNoSSHDays);
			continuousRainsDefineParam.setSeverityPreDays(severityPreDays);
			continuousRainsDefineParam.setSeverityMinValue(severityMinValue);
			continuousRainsDefineParam.setTerminPreDays(terminPreDays);
			continuousRainsDefineParam.setTerminValue(terminValue);
			continuousRainsParam.setContinuousRainsDefineParam(continuousRainsDefineParam);
			
			TimesYearsParam timesYearsParam = new TimesYearsParam(timesRangeParam.getStartMon(), timesRangeParam.getStartDay(), timesRangeParam.getEndMon(),
					timesRangeParam.getEndDay(), startYear, endYear);
			TimesYearsParam perennialTimesYearsParam = new TimesYearsParam(timesRangeParam.getStartMon(), timesRangeParam.getStartDay(), timesRangeParam.getEndMon(),
					timesRangeParam.getEndDay(), perennialStartYear, perennialEndYear);
			ContinuousRainStatisticsBus continuousRainStatisticsBus = new ContinuousRainStatisticsBus();
			List<ContinueousRainYearsResult> result = continuousRainStatisticsBus.continuousRainsYearsSequnence(continuousRainsParam, timesYearsParam, perennialTimesYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	
	/**
	 * 低温统计，按时间范围，统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("lowTmpByRange")
	@Produces("application/json")
	public Object lowTmpByRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		LowTmpByRangeParam lowTmpByRangeParam = null;//new LowTmpByRangeParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			lowTmpByRangeParam = new LowTmpByRangeParam(startTimeStr, endTimeStr);
			int level1SequenceSeason = jsonObject.getInt("level1SequenceSeason");
			lowTmpByRangeParam.setLevel1SequenceSeason(level1SequenceSeason);
			double level1SequenceTmp = jsonObject.getDouble("level1SequenceTmp");
			lowTmpByRangeParam.setLevel1SequenceTmp(level1SequenceTmp);
			
			if(jsonObject.has("constatStartYear")) {
				int constatStartYear = jsonObject.getInt("constatStartYear");
				lowTmpByRangeParam.setConstatStartYear(constatStartYear);
			}
			if(jsonObject.has("constatEndYear")) {
				int constatEndYear = jsonObject.getInt("constatEndYear");
				lowTmpByRangeParam.setConstatEndYear(constatEndYear);
			}
			
			String level1ExceptMonthesStr = jsonObject.getString("level1ExceptMonthes");
			if(level1ExceptMonthesStr != null && level1ExceptMonthesStr.length() > 0) {
				String[] temp = level1ExceptMonthesStr.trim().split(",");
				int[] level1ExceptMonthes = new int[temp.length];
				for(int i=0; i<level1ExceptMonthes.length;i++) {
					level1ExceptMonthes[i] = Integer.parseInt(temp[i]);
				}
				lowTmpByRangeParam.setLevel1ExceptMonthes(level1ExceptMonthes);
			}
			
			int level2SequenceSeason = jsonObject.getInt("level2SequenceSeason");
			lowTmpByRangeParam.setLevel2SequenceSeason(level2SequenceSeason);
			double level2SequenceTmp = jsonObject.getDouble("level2SequenceTmp");
			lowTmpByRangeParam.setLevel2SequenceTmp(level2SequenceTmp);
			String level2ExceptMonthesStr = jsonObject.getString("level2ExceptMonthes");
			if(level2ExceptMonthesStr != null && level2ExceptMonthesStr.length() > 0) {
				String[] temp = level2ExceptMonthesStr.trim().split(",");
				int[] level2ExceptMonthes = new int[temp.length];
				for(int i=0; i<level2ExceptMonthes.length;i++) {
					level2ExceptMonthes[i] = Integer.parseInt(temp[i]);
				}
				lowTmpByRangeParam.setLevel2ExceptMonthes(level2ExceptMonthes);
			}
			LowTmpBus lowTmpBus = new LowTmpBus();
			LowTmpResult lowTmpResult = lowTmpBus.lowTmpByRange(lowTmpByRangeParam);
			DisasterFilter disasterFilter = new DisasterFilter();
			LowTmpResult result2 = disasterFilter.filterLowTmpResult(lowTmpResult, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 灾害低温,历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("lowTmpByYear")
	@Produces("application/json")
	public Object lowTmpByYear(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		LowTmpByRangeParam lowTmpByRangeParam = null;//new LowTmpByRangeParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			lowTmpByRangeParam = new LowTmpByRangeParam(startTimeStr, endTimeStr);
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			lowTmpByRangeParam.setStartYear(startYear);
			lowTmpByRangeParam.setEndYear(endYear);
			
			if(jsonObject.has("constatStartYear")) {
				int constatStartYear = jsonObject.getInt("constatStartYear");
				lowTmpByRangeParam.setConstatStartYear(constatStartYear);
			}
			if(jsonObject.has("constatEndYear")) {
				int constatEndYear = jsonObject.getInt("constatEndYear");
				lowTmpByRangeParam.setConstatEndYear(constatEndYear);
			}
			
			int level1SequenceSeason = jsonObject.getInt("level1SequenceSeason");
			lowTmpByRangeParam.setLevel1SequenceSeason(level1SequenceSeason);
			double level1SequenceTmp = jsonObject.getDouble("level1SequenceTmp");
			lowTmpByRangeParam.setLevel1SequenceTmp(level1SequenceTmp);
			String level1ExceptMonthesStr = jsonObject.getString("level1ExceptMonthes");
			if(level1ExceptMonthesStr != null && level1ExceptMonthesStr.length() > 0) {
				String[] temp = level1ExceptMonthesStr.trim().split(",");
				int[] level1ExceptMonthes = new int[temp.length];
				for(int i=0; i<level1ExceptMonthes.length;i++) {
					level1ExceptMonthes[i] = Integer.parseInt(temp[i]);
				}
				lowTmpByRangeParam.setLevel1ExceptMonthes(level1ExceptMonthes);
			}
			
			int level2SequenceSeason = jsonObject.getInt("level2SequenceSeason");
			lowTmpByRangeParam.setLevel2SequenceSeason(level2SequenceSeason);
			double level2SequenceTmp = jsonObject.getDouble("level2SequenceTmp");
			lowTmpByRangeParam.setLevel2SequenceTmp(level2SequenceTmp);
			String level2ExceptMonthesStr = jsonObject.getString("level2ExceptMonthes");
			if(level2ExceptMonthesStr != null && level2ExceptMonthesStr.length() > 0) {
				String[] temp = level2ExceptMonthesStr.trim().split(",");
				int[] level2ExceptMonthes = new int[temp.length];
				for(int i=0; i<level2ExceptMonthes.length;i++) {
					level2ExceptMonthes[i] = Integer.parseInt(temp[i]);
				}
				lowTmpByRangeParam.setLevel2ExceptMonthes(level2ExceptMonthes);
			}
			if(jsonObject.has("station_Id_Cs")) {
				String station_Id_Cs = jsonObject.getString("station_Id_Cs");
				lowTmpByRangeParam.setStation_Id_Cs(station_Id_Cs);
			}
			LowTmpBus lowTmpBus = new LowTmpBus();
			Object result = lowTmpBus.lowTmpByYears(lowTmpByRangeParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 大风统计,按时间段
	 * @param para
	 * @return
	 */
	@POST
	@Path("maxWindByRange")
	@Produces("application/json")
	public Object maxWindByRange(@FormParam("para") String para) {
		MaxWindRangeParam maxWindRangeParam = new MaxWindRangeParam(); 
		
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			maxWindRangeParam.setTimesParam(timesParam);
			
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				maxWindRangeParam.setStationType(stationType);
			}
			
			MaxWindBus maxWindBus = new MaxWindBus();
			MaxWindResult result = maxWindBus.maxWindByRange(maxWindRangeParam);
			DisasterFilter disasterFilter = new DisasterFilter();
			MaxWindResult result2 = disasterFilter.filterMaxWindResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 大风统计,历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("maxWindByYear")
	@Produces("application/json")
	public Object maxWindByYear(@FormParam("para") String para) {
		MaxWindYearsParam maxWindYearsParam = new MaxWindYearsParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		int startYear = 0, endYear = 0, perennialStartYear = 0, perennialEndYear = 0;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			maxWindYearsParam.setTimesParam(timesParam);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			perennialStartYear = jsonObject.getInt("perennialStartYear");
			perennialEndYear = jsonObject.getInt("perennialEndYear");
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			maxWindYearsParam.setStation_Id_Cs(station_Id_Cs.trim().split(","));
			maxWindYearsParam.setStartYear(startYear);
			maxWindYearsParam.setEndYear(endYear);
			maxWindYearsParam.setPerennialStartYear(perennialStartYear);
			maxWindYearsParam.setPerennialEndYear(perennialEndYear);
			MaxWindBus maxWindBus = new MaxWindBus();
			Object result = maxWindBus.maxWindByYear(maxWindYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段统计降雪
	 * @param para
	 * @return
	 */
	@POST
	@Path("snowByRange")
	@Produces("application/json")
	public Object snowByRange(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			SnowBus snowBus = new SnowBus();
			SnowResult result = snowBus.snowByRange(timesParam);
			DisasterFilter disasterFilter = new DisasterFilter();
			SnowResult result2 = disasterFilter.filterSnowResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 历年同期统计降雪
	 * @param para
	 * @return
	 */
	@POST
	@Path("snowByYears")
	@Produces("application/json")
	public Object snowByYears(@FormParam("para") String para) {
		SnowYearsParam snowYearsParam = new SnowYearsParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		int startYear = 0, endYear = 0, perennialStartYear = 0, perennialEndYear = 0;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			snowYearsParam.setTimesParam(timesParam);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			perennialStartYear = jsonObject.getInt("perennialStartYear");
			perennialEndYear = jsonObject.getInt("perennialEndYear");
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			snowYearsParam.setStation_Id_Cs(station_Id_Cs.trim().split(","));
			snowYearsParam.setStartYear(startYear);
			snowYearsParam.setEndYear(endYear);
			snowYearsParam.setPerennialStartYear(perennialStartYear);
			snowYearsParam.setPerennialEndYear(perennialEndYear);
			SnowBus snowBus = new SnowBus();
			Object result = snowBus.snowByYears(snowYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段统计雾
	 * @param para
	 * @return
	 */
	@POST
	@Path("fogByRange")
	@Produces("application/json")
	public Object fogByRange(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			FogBus fogBus = new FogBus();
			FogResult result = fogBus.fogByRange(timesParam);
			DisasterFilter disasterFilter = new DisasterFilter();
			FogResult result2 = disasterFilter.filterFogResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 历年同期统计雾
	 * @param para
	 * @return
	 */
	@POST
	@Path("fogByYears")
	@Produces("application/json")
	public Object fogByYears(@FormParam("para") String para) {
		FogYearsParam fogYearsParam = new FogYearsParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		int startYear = 0, endYear = 0, perennialStartYear = 0, perennialEndYear = 0;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			fogYearsParam.setTimesParam(timesParam);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			perennialStartYear = jsonObject.getInt("perennialStartYear");
			perennialEndYear = jsonObject.getInt("perennialEndYear");
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			fogYearsParam.setStation_Id_Cs(station_Id_Cs.trim().split(","));
			fogYearsParam.setStartYear(startYear);
			fogYearsParam.setEndYear(endYear);
			fogYearsParam.setPerennialStartYear(perennialStartYear);
			fogYearsParam.setPerennialEndYear(perennialEndYear);
			FogBus fogBus = new FogBus();
			Object result = fogBus.fogByYears(fogYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段统计高温
	 * @param para
	 * @return
	 */
	@POST
	@Path("highTmpByRange")
	@Produces("application/json")
	public Object highTmpByRange(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			double level1HighTmp = jsonObject.getDouble("level1HighTmp");
			double level2HighTmp = jsonObject.getDouble("level2HighTmp");
			double level3HighTmp = jsonObject.getDouble("level3HighTmp");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			HighTmpBus highTmpBus = new HighTmpBus();
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			String[] stations = station_Id_Cs.split(",");
			HighTmpRangeParam highTmpRangeParam = new HighTmpRangeParam(); 
			highTmpRangeParam.setTimesParam(timesParam);
			highTmpRangeParam.setLevel1HighTmp(level1HighTmp);
			highTmpRangeParam.setLevel2HighTmp(level2HighTmp);
			highTmpRangeParam.setLevel3HighTmp(level3HighTmp);
			highTmpRangeParam.setStations(stations);
			Object result = highTmpBus.highTmpByRange(highTmpRangeParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	
	/**
	 * 历年同期统计高温
	 * @param para
	 * @return
	 */
	@POST
	@Path("highTmpByYears")
	@Produces("application/json")
	public Object highTmpByYears(@FormParam("para") String para) {
		HighTmpYearsParam highTmpYearsParam = new HighTmpYearsParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		int startYear = 0, endYear = 0, perennialStartYear = 0, perennialEndYear = 0;
		try {
			jsonObject = new JSONObject(para);
			double level1HighTmp = jsonObject.getDouble("level1HighTmp");
			double level2HighTmp = jsonObject.getDouble("level2HighTmp");
			double level3HighTmp = jsonObject.getDouble("level3HighTmp");
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			highTmpYearsParam.setTimesParam(timesParam);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			perennialStartYear = jsonObject.getInt("perennialStartYear");
			perennialEndYear = jsonObject.getInt("perennialEndYear");
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			highTmpYearsParam.setStation_Id_Cs(station_Id_Cs.trim().split(","));
			highTmpYearsParam.setStartYear(startYear);
			highTmpYearsParam.setEndYear(endYear);
			highTmpYearsParam.setLevel1HighTmp(level1HighTmp);
			highTmpYearsParam.setLevel2HighTmp(level2HighTmp);
			highTmpYearsParam.setLevel3HighTmp(level3HighTmp);
			highTmpYearsParam.setPerennialStartYear(perennialStartYear);
			highTmpYearsParam.setPerennialEndYear(perennialEndYear);
			HighTmpBus highTmpBus = new HighTmpBus();
			Object result = highTmpBus.highTmpByYears(highTmpYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段统计强降温
	 * @param para
	 * @return
	 */
	@POST
	@Path("strongCoolingByRange")
	@Produces("application/json")
	public Object strongCoolingByRange(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			double level1WinterTmp = jsonObject.getDouble("level1WinterTmp");
			double level1springAutumnTmp = jsonObject.getDouble("level1springAutumnTmp");
			double level1SummerTmp = jsonObject.getDouble("level1SummerTmp");
			double level2WinterTmp = jsonObject.getDouble("level2WinterTmp");
			double level2springAutumnTmp = jsonObject.getDouble("level2springAutumnTmp");
			double level2SummerTmp = jsonObject.getDouble("level2SummerTmp");
			boolean level1SummerFlag = jsonObject.getBoolean("level1SummerFlag");
			boolean level2SummerFlag = jsonObject.getBoolean("level2SummerFlag");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			StrongCoolingParam strongCoolingParam = new StrongCoolingParam();
			StrongCoolingBus strongCoolingBus = new StrongCoolingBus();
			strongCoolingParam.setLevel1springAutumnTmp(level1springAutumnTmp);
			strongCoolingParam.setLevel1SummerTmp(level1SummerTmp);
			strongCoolingParam.setLevel1WinterTmp(level1WinterTmp);
			strongCoolingParam.setLevel2springAutumnTmp(level2springAutumnTmp);
			strongCoolingParam.setLevel2SummerTmp(level2SummerTmp);
			strongCoolingParam.setLevel2WinterTmp(level2WinterTmp);
			strongCoolingParam.setLevel1SummerFlag(level1SummerFlag);
			strongCoolingParam.setLevel2SummerFlag(level2SummerFlag);
			strongCoolingParam.setTimesParam(timesParam);
			boolean flag = jsonObject.has("station_Id_Cs");
			if(flag) {
				String station_Id_Cs = (String) jsonObject.get("station_Id_Cs");
				strongCoolingParam.setStation_id_Cs(station_Id_Cs);
			}
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				strongCoolingParam.setStationType(stationType);
			}
			StrongCoolingResult result = strongCoolingBus.strongCoolingByRange(strongCoolingParam);
			return result;
//			DisasterFilter disasterFilter = new DisasterFilter();
//			StrongCoolingResult result2 = disasterFilter.filterStrongCooling(result, jsonObject);
//			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 历年同期 统计强降温
	 * @param para
	 * @return
	 */
	@POST
	@Path("strongCoolingByYears")
	@Produces("application/json")
	public Object strongCoolingByYears(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			int perennialStartYear = jsonObject.getInt("perennialStartYear");
			int perennialEndYear = jsonObject.getInt("perennialEndYear");
			double level1WinterTmp = jsonObject.getDouble("level1WinterTmp");
			double level1springAutumnTmp = jsonObject.getDouble("level1springAutumnTmp");
			double level1SummerTmp = jsonObject.getDouble("level1SummerTmp");
			double level2WinterTmp = jsonObject.getDouble("level2WinterTmp");
			double level2springAutumnTmp = jsonObject.getDouble("level2springAutumnTmp");
			double level2SummerTmp = jsonObject.getDouble("level2SummerTmp");
			boolean level1SummerFlag = jsonObject.getBoolean("level1SummerFlag");
			boolean level2SummerFlag = jsonObject.getBoolean("level2SummerFlag");
//			String stationType = jsonObject.getString("stationType");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			StrongCoolingYearsParam strongCoolingYearsParam = new StrongCoolingYearsParam();
			strongCoolingYearsParam.setLevel1SummerFlag(level1SummerFlag);
			strongCoolingYearsParam.setLevel2SummerFlag(level2SummerFlag);
			StrongCoolingBus strongCoolingBus = new StrongCoolingBus();
			strongCoolingYearsParam.setLevel1springAutumnTmp(level1springAutumnTmp);
			strongCoolingYearsParam.setLevel1SummerTmp(level1SummerTmp);
			strongCoolingYearsParam.setLevel1WinterTmp(level1WinterTmp);
			strongCoolingYearsParam.setLevel2springAutumnTmp(level2springAutumnTmp);
			strongCoolingYearsParam.setLevel2SummerTmp(level2SummerTmp);
			strongCoolingYearsParam.setLevel2WinterTmp(level2WinterTmp);
			strongCoolingYearsParam.setTimesParam(timesParam);
			strongCoolingYearsParam.setStartYear(startYear);
			strongCoolingYearsParam.setEndYear(endYear);
			strongCoolingYearsParam.setPerennialStartYear(perennialStartYear);
			strongCoolingYearsParam.setPerennialEndYear(perennialEndYear);
//			strongCoolingYearsParam.setStationType(stationType);
			if(jsonObject.has("station_Id_Cs")) {
				String station_Id_Cs = jsonObject.getString("station_Id_Cs");
				strongCoolingYearsParam.setStation_Id_Cs(station_Id_Cs);
			}
			Object result = strongCoolingBus.strongCoolingByYears(strongCoolingYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段统计雷暴
	 * @param para
	 * @return
	 */
	@POST
	@Path("thundByRange")
	@Produces("application/json")
	public Object thundByRange(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			ThundBus thundBus = new ThundBus();
			ThundResult result = thundBus.thundByRange(timesParam);
			DisasterFilter disasterFilter = new DisasterFilter();
			ThundResult result2 = disasterFilter.filterThundResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 历年同期统计雷暴
	 * @param para
	 * @return
	 */
	@POST
	@Path("thundByYears")
	@Produces("application/json")
	public Object thundByYears(@FormParam("para") String para) {
		ThundYearsParam thundYearsParam = new ThundYearsParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			int perennialStartYear = jsonObject.getInt("perennialStartYear");
			int perennialEndYear = jsonObject.getInt("perennialEndYear");
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			thundYearsParam.setTimesParam(timesParam);
			thundYearsParam.setStartYear(startYear);
			thundYearsParam.setEndYear(endYear);
			thundYearsParam.setStation_Id_Cs(station_Id_Cs.split(","));
			thundYearsParam.setPerennialStartYear(perennialStartYear);
			thundYearsParam.setPerennialEndYear(perennialEndYear);
			ThundBus thundBus = new ThundBus();
			Object result = thundBus.thundByYears(thundYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段统计霜冻
	 * @param para
	 * @return
	 */
	@POST
	@Path("frostByRange")
	@Produces("application/json")
	public Object frostByRange(@FormParam("para") String para) {
		FrostRangeParam frostRangeParam = new FrostRangeParam();  
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			frostRangeParam.setTimesParam(timesParam);
			//一般冻害，连续天数
			frostRangeParam.setLevel1PersistDays(jsonObject.getInt("level1PersistDays"));
			//一般冻害，低温下限
			frostRangeParam.setLevel1LowTmp(jsonObject.getDouble("level1LowTmp"));
			//一般冻害，任意天数气温低于某个界限
			frostRangeParam.setLevel1LTLowTmpDays(jsonObject.getInt("level1LTLowTmpDays"));
			//一般冻害，任意天数气温低于的界限
			frostRangeParam.setLevel1LTLowTmp(jsonObject.getDouble("level1LTLowTmp"));
			//严重冻害，连续天数
			frostRangeParam.setLevel2PersistDays(jsonObject.getInt("level2PersistDays"));
			//严重冻害，低温下限
			frostRangeParam.setLevel2LowTmp(jsonObject.getDouble("level2LowTmp"));
			//严重冻害，任意天数气温低于某个界限
			frostRangeParam.setLevel2LTLowTmpDays(jsonObject.getInt("level2LTLowTmpDays"));
			//严重冻害，任意天数气温低于的界限
			frostRangeParam.setLevel2LTLowTmp(jsonObject.getDouble("level2LTLowTmp"));
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				frostRangeParam.setStationType(stationType);
			}
			FrostBus forstBus = new FrostBus();
			FrostResult result = forstBus.frostByRange(frostRangeParam);
			DisasterFilter disasterFilter = new DisasterFilter();
			FrostResult result2 = disasterFilter.filterFrostResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 历年统计霜冻
	 * @param para
	 * @return
	 */
	@POST
	@Path("frostByYears")
	@Produces("application/json")
	public Object frostByYears(@FormParam("para") String para) {
		FrostYearsParam frostYearsParam = new FrostYearsParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			frostYearsParam.setTimesParam(timesParam);
			//一般冻害，连续天数
			frostYearsParam.setLevel1PersistDays(jsonObject.getInt("level1PersistDays"));
			//一般冻害，低温下限
			frostYearsParam.setLevel1LowTmp(jsonObject.getDouble("level1LowTmp"));
			//一般冻害，任意天数气温低于某个界限
			frostYearsParam.setLevel1LTLowTmpDays(jsonObject.getInt("level1LTLowTmpDays"));
			//一般冻害，任意天数气温低于的界限
			frostYearsParam.setLevel1LTLowTmp(jsonObject.getDouble("level1LTLowTmp"));
			//严重冻害，连续天数
			frostYearsParam.setLevel2PersistDays(jsonObject.getInt("level2PersistDays"));
			//严重冻害，低温下限
			frostYearsParam.setLevel2LowTmp(jsonObject.getDouble("level2LowTmp"));
			//严重冻害，任意天数气温低于某个界限
			frostYearsParam.setLevel2LTLowTmpDays(jsonObject.getInt("level2LTLowTmpDays"));
			//严重冻害，任意天数气温低于的界限
			frostYearsParam.setLevel2LTLowTmp(jsonObject.getDouble("level2LTLowTmp"));
			frostYearsParam.setStartYear(jsonObject.getInt("startYear"));
			frostYearsParam.setEndYear(jsonObject.getInt("endYear"));
			frostYearsParam.setPerennialStartYear(jsonObject.getInt("perennialStartYear"));
			frostYearsParam.setPerennialEndYear(jsonObject.getInt("perennialEndYear"));
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			frostYearsParam.setStation_Id_Cs(station_Id_Cs.split(","));
			FrostBus forstBus = new FrostBus();
			Object result = forstBus.frostByYears(frostYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段统计暴雨
	 * @param para
	 * @return
	 */
	@POST
	@Path("rainstormByRange")
	@Produces("application/json")
	public Object rainstormByRange(@FormParam("para") String para) {
		RainStormRangeParam rainStormRangeParam = new RainStormRangeParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String type = jsonObject.getString("type");
			String stationType = jsonObject.getString("stationType");
			String tableName = EleTypes.getTableName(type);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			rainStormRangeParam.setTimesParam(timesParam);
			rainStormRangeParam.setLevel1(jsonObject.getDouble("level1"));
			rainStormRangeParam.setLevel2(jsonObject.getDouble("level2"));
			rainStormRangeParam.setLevel3(jsonObject.getDouble("level3"));
			rainStormRangeParam.setTableName(tableName);
			rainStormRangeParam.setStationType(stationType);
			RainStormBus rainStormBus = new RainStormBus();
			DisasterRainStormFinResult result = rainStormBus.rainStormByRange(rainStormRangeParam);
			DisasterFilter disasterFilter = new DisasterFilter();
			DisasterRainStormFinResult result2 = disasterFilter.filterDisasterRainStormFinResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 历年同期统计暴雨
	 * @param para
	 * @return
	 */
	@POST
	@Path("rainstormByYears")
	@Produces("application/json")
	public Object rainstormByYears(@FormParam("para") String para) {
		RainStormYearsParam rainStormYearsParam = new RainStormYearsParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String type = jsonObject.getString("type");
			String tableName = EleTypes.getTableName(type);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			int perennialStartYear = jsonObject.getInt("perennialStartYear");
			int perennialEndYear = jsonObject.getInt("perennialEndYear");
			rainStormYearsParam.setPerennialStartYear(perennialStartYear);
			rainStormYearsParam.setPerennialEndYear(perennialEndYear);
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			rainStormYearsParam.setTimesParam(timesParam);
			rainStormYearsParam.setLevel1(jsonObject.getDouble("level1"));
			rainStormYearsParam.setLevel2(jsonObject.getDouble("level2"));
			rainStormYearsParam.setLevel3(jsonObject.getDouble("level3"));
			rainStormYearsParam.setTableName(tableName);
			rainStormYearsParam.setStartYear(startYear);
			rainStormYearsParam.setEndYear(endYear);
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			rainStormYearsParam.setStation_Id_Cs(station_Id_Cs.split(","));
			RainStormBus rainStormBus = new RainStormBus();
			List<RainStormYearsResult> result = rainStormBus.rainstormByYears(rainStormYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间点统计干旱
	 * @param para
	 * @return
	 */
	@POST
	@Path("mciByTime")
	@Produces("application/json")
	public Object mciByTime(@FormParam("para") String para) {
		MCILevelParam mciLevelParam = new MCILevelParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			double level1 = jsonObject.getDouble("level1");
			double level2 = jsonObject.getDouble("level2");
			double level3 = jsonObject.getDouble("level3");
			double level4 = jsonObject.getDouble("level4");
			mciLevelParam.setLevel1(level1);
			mciLevelParam.setLevel2(level2);
			mciLevelParam.setLevel3(level3);
			mciLevelParam.setLevel4(level4);
			String dateTime = jsonObject.getString("datetime");
			MCIBus mciBus = new MCIBus();
			List<MCISequenceResult> result = mciBus.mciByTime(mciLevelParam, dateTime);
			DisasterFilter disasterFilter = new DisasterFilter();
			List<MCISequenceResult> result2 = disasterFilter.filterMCISequenceResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 干旱站次统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("mciStatisticsByTime")
	@Produces("application/json")
	public Object mciStatisticsByTime(@FormParam("para") String para) {
		MCIStatisticsParam mciStatisticsParam = new MCIStatisticsParam();
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			double level1 = jsonObject.getDouble("level1");
			double level2 = jsonObject.getDouble("level2");
			double level3 = jsonObject.getDouble("level3");
			double level4 = jsonObject.getDouble("level4");
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			
			mciStatisticsParam.setLevel1(level1);
			mciStatisticsParam.setLevel2(level2);
			mciStatisticsParam.setLevel3(level3);
			mciStatisticsParam.setLevel4(level4);
			mciStatisticsParam.setTimesParam(timesParam);
			if(jsonObject.has("station_Id_Cs")) {
				String station_id_Cs = jsonObject.getString("station_Id_Cs");
				String[] station_id_CArray = station_id_Cs.split(",");
				List<String> station_id_CList = new ArrayList<String>();
				for(int i = 0; i < station_id_CArray.length; i++) {
					station_id_CList.add(station_id_CArray[i]);
				}
				mciStatisticsParam.setStation_id_Cs(station_id_CList);
			}
			MCIBus mciBus = new MCIBus();
			List<MCIStationSequenceResult> result = mciBus.mciStatisticsByTime(mciStatisticsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 土壤湿度统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("agmesoilStatisticsByTime")
	@Produces("application/json")
	public Object agmesoilStatisticsByTime(@FormParam("para") String para) {
		try {
			JSONObject jsonObject = new JSONObject(para);
			String datetime = jsonObject.getString("datetime");
			MCIBus mciBus = new MCIBus();
			Object result = mciBus.agmesoilStatisticsByTime(datetime);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 小时雨量	降水极值
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainExt")
	@Produces("application/json")
	public Object hourRainExt(@FormParam("para") String para) {
		HourTimesParam hourTimesParam = new HourTimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			hourTimesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			String type = jsonObject.getString("type");
			hourTimesParam.setEndTimeStr(endTimeStr);
			hourTimesParam.setType(type);
			if(type.equals("AREA")) {
				String areaCode = jsonObject.getString("areaCode");
				hourTimesParam.setAreaCode(areaCode);
			}
			if(jsonObject.has("resultType")) {
				String resultType = jsonObject.getString("resultType");
				hourTimesParam.setResultType(resultType);
			}
			HourRainBus hourRainBus = new HourRainBus();
			Object result = hourRainBus.hourRainExtAnalyst(hourTimesParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 小时雨量	累积降水
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainAccumulate")
	@Produces("application/json")
	public Object hourRainAccumulate(@FormParam("para") String para) {
		HourTimesParam hourTimesParam = new HourTimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			hourTimesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			hourTimesParam.setEndTimeStr(endTimeStr);
			String type = jsonObject.getString("type");
			hourTimesParam.setType(type);
			if(type.equals("AREA")) {
				String areaCode = jsonObject.getString("areaCode");
				hourTimesParam.setAreaCode(areaCode);
			}
			if(jsonObject.has("resultType")) {
				String resultType = jsonObject.getString("resultType");
				hourTimesParam.setResultType(resultType);
			}
			HourRainBus hourRainBus = new HourRainBus();
			Object result = hourRainBus.hourRainAccumulateAnalyst(hourTimesParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 小时雨量	过程降水
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainSequence")
	@Produces("application/json")
	public Object hourRainSequence(@FormParam("para") String para) {
		HourTimesParam hourTimesParam = new HourTimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			hourTimesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			hourTimesParam.setEndTimeStr(endTimeStr);
			String type = jsonObject.getString("type");
			hourTimesParam.setType(type);
			if(type.equals("AREA")) {
				String areaCode = jsonObject.getString("areaCode");
				hourTimesParam.setAreaCode(areaCode);
			}
			if(jsonObject.has("resultType")) {
				String resultType = jsonObject.getString("resultType");
				hourTimesParam.setResultType(resultType);
			}
			HourRainBus hourRainBus = new HourRainBus();
			Object result = hourRainBus.hourRainSequence(hourTimesParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 小时雨量	时段位次
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainRankTimesStatistics")
	@Produces("application/json")
	public Object hourRainRankTimesStatistics(@FormParam("para") String para) {
		HourRainRangeParam hourRainRangeParam = new HourRainRangeParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			HourTimesParam extTimesParam = new HourTimesParam();
			String extStartTimeStr = jsonObject.getString("extStartTimeStr");
			extTimesParam.setStartTimeStr(extStartTimeStr);
			String extEndTimeStr = jsonObject.getString("extEndTimeStr");
			extTimesParam.setEndTimeStr(extEndTimeStr);
			hourRainRangeParam.setExtTimesParam(extTimesParam);
			HourTimesParam rankTimesParam = new HourTimesParam();
			String rankStartTimeStr = jsonObject.getString("rankStartTimeStr");
			rankTimesParam.setStartTimeStr(rankStartTimeStr);
			String rankEndTimeStr = jsonObject.getString("rankEndTimeStr");
			rankTimesParam.setEndTimeStr(rankEndTimeStr);
			hourRainRangeParam.setRankTimesParam(rankTimesParam);
			hourRainRangeParam.setHour(jsonObject.getInt("hour"));
			String type = jsonObject.getString("type");
			hourRainRangeParam.setType(type);
			if(type.equals("AREA")) {
				String areaCode = jsonObject.getString("areaCode");
				extTimesParam.setAreaCode(areaCode);
				rankTimesParam.setAreaCode(areaCode);
			}
			if(jsonObject.has("resultType")) {
				String resultType = jsonObject.getString("resultType");
				extTimesParam.setResultType(resultType);
				rankTimesParam.setResultType(resultType);
			}
			HourRainBus hourRainBus = new HourRainBus();
			Object result = hourRainBus.hourRainRankTimesStatistics(hourRainRangeParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 小时雨量	同期位次
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainRankYearsStatistics")
	@Produces("application/json")
	public Object hourRainRankYearsStatistics(@FormParam("para") String para) {
		HourRainHisRankParam hourRainHisRankParam = new HourRainHisRankParam();
		HourTimesParam hourTimesParam = new HourTimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			hourTimesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			hourTimesParam.setEndTimeStr(endTimeStr);
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			hourRainHisRankParam.setStartYear(startYear);
			hourRainHisRankParam.setEndYear(endYear);
			hourRainHisRankParam.setHour(jsonObject.getInt("hour"));
			HourRainBus hourRainBus = new HourRainBus();
			String type = jsonObject.getString("type");
			hourRainHisRankParam.setType(type);
			if(type.equals("AREA")) {
				String areaCode = jsonObject.getString("areaCode");
				hourTimesParam.setAreaCode(areaCode);
			}
			if(jsonObject.has("resultType")) {
				String resultType = jsonObject.getString("resultType");
				hourTimesParam.setResultType(resultType);
			}
			hourRainHisRankParam.setHourTimesParam(hourTimesParam);
			Object result = hourRainBus.hourRainRankYearsStatistics(hourRainHisRankParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 小时雨量	历年极值
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainExtYearsStatistics")
	@Produces("application/json")
	public Object hourRainExtYearsStatistics(@FormParam("para") String para) {
		HourRainHisExtParam hourRainHisExtParam = new HourRainHisExtParam();
		HourTimesParam hourTimesParam = new HourTimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			hourTimesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			hourTimesParam.setEndTimeStr(endTimeStr);
			
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			hourRainHisExtParam.setStartYear(startYear);
			hourRainHisExtParam.setEndYear(endYear);
			hourRainHisExtParam.setHour(jsonObject.getInt("hour"));
			
			if(jsonObject.has("type")) {
				String type = jsonObject.getString("type");
				hourTimesParam.setType(type);
				if(type.equals("AREA")) {
					String areaCode = jsonObject.getString("areaCode");
					hourTimesParam.setAreaCode(areaCode);
				}
			} else {
				String station_Id_C = jsonObject.getString("Station_Id_C");
				hourRainHisExtParam.setStation_Id_C(station_Id_C);
			}
			hourRainHisExtParam.setHourTimesParam(hourTimesParam);
			HourRainBus hourRainBus = new HourRainBus();
			Object result = hourRainBus.hourRainExtYearsStatistics(hourRainHisExtParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 指定时间段内查询极值，以及极值对应的日期
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainExtByTimes")
	@Produces("application/json")
	public Object hourRainExtByTimes(@FormParam("para") String para) {
		HourRainExtParam hourRainExtParam = new HourRainExtParam();
		HourTimesParam hourTimesParam = new HourTimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			hourTimesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			hourTimesParam.setEndTimeStr(endTimeStr);
			String type = jsonObject.getString("type");
			hourRainExtParam.setType(type);
			if(type.equals("AREA")) {
				String areaCode = jsonObject.getString("areaCode");
				hourTimesParam.setAreaCode(areaCode);
			}
			if(jsonObject.has("resultType")) {
				String resultType = jsonObject.getString("resultType");
				hourTimesParam.setResultType(resultType);
			}
			hourRainExtParam.setHourTimesParam(hourTimesParam);
			HourRainBus hourRainBus = new HourRainBus();
			Object result = hourRainBus.hourRainExtByTimes(hourRainExtParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 逐时演变
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainChange")
	@Produces("application/json")
	public Object hourRainChange(@FormParam("para") String para) {
		HourRainSequenceParam hourRainSequenceParam = new HourRainSequenceParam();
		HourTimesParam hourTimesParam = new HourTimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			hourTimesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			hourTimesParam.setEndTimeStr(endTimeStr);
			hourRainSequenceParam.setHourTimesParam(hourTimesParam);
			String station_Id_C = jsonObject.getString("Station_Id_C");
			hourRainSequenceParam.setStation_Id_C(station_Id_C);
			HourRainBus hourRainBus = new HourRainBus();
			Object result = hourRainBus.hourRainChange(hourRainSequenceParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 查询小时雨量建站时间
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainStation")
	@Produces("application/json")
	public Object hourRainStation(@FormParam("para") String para) {
		HourRainBus hourRainBus = new HourRainBus();
		JSONObject jsonObject = null;
		List<String> stationList = new ArrayList<String>();
		if(para != null) {
			try {
				jsonObject = new JSONObject(para);
				String station_Id_Cs = jsonObject.getString("station_Id_Cs");
				String[] station_Id_CArray = station_Id_Cs.split(",");
				for(int i = 0; i < station_Id_CArray.length; i++) {
					stationList.add(station_Id_CArray[i]);
				}
			} catch(Exception e) {
				e.printStackTrace();
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				LogTool.getLogger(this.getClass()).error(methodName, e);
				return "错误，参数【" + para + "】，错误：" + e.getMessage();
			}	
		}
		Object result = hourRainBus.hourRainStation(stationList);
		return result;
	}
	
	/**
	 * 小时雨量,降序排列
	 * @param para
	 * @return
	 */
	@POST
	@Path("hourRainSortByStation")
	@Produces("application/json")
	public Object hourRainSortByStation(@FormParam("para") String para) {
		HourRainBus hourRainBus = new HourRainBus();
		HourRainSortParam hourRainSortParam = new HourRainSortParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String station_Id_C = jsonObject.getString("Station_Id_C");
			int limit = jsonObject.getInt("limit");
			String type = jsonObject.getString("type");
			hourRainSortParam.setStation_Id_C(station_Id_C);
			hourRainSortParam.setLimit(limit);
			hourRainSortParam.setType(type);
			Object result = hourRainBus.hourRainSortByStation(hourRainSortParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	
	/**
	 * 冰雹，按时间段统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("hailByRange")
	@Produces("application/json")
	public Object hailByRange(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			HailBus hailBus = new HailBus();
			List<HailSequenceResult> result = hailBus.queryByTimes(timesParam);
			DisasterFilter disasterFilter = new DisasterFilter();
			List<HailSequenceResult> result2 = disasterFilter.filterHailSequenceResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段、天气现象类型查询天气现象。
	 * @param para
	 * @return
	 */
	@POST
	@Path("wepByRange")
	@Produces("application/json")
	public Object wepByRange(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			WepBus wepBus = new WepBus();
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			WepParam wepParam = new WepParam();
			wepParam.setTimesParam(timesParam);
			String station_id_Cs = jsonObject.getString("station_Id_Cs");
			String[] station_id_CArray = station_id_Cs.split(",");
			List<String> station_id_CList = new ArrayList<String>();
			for(int i = 0; i < station_id_CArray.length; i++) {
				station_id_CList.add(station_id_CArray[i]);
			}
			wepParam.setStation_id_Cs(station_id_CList);
			String weps = jsonObject.getString("weps");
			wepParam.setTimesParam(timesParam);
			Object result = null;
			if("*".equals(weps)) {
				wepParam.setWeps("*");
				result = wepBus.queryAllByTimes(wepParam, null);
			} else {
				wepParam.setWeps(weps);
				result = wepBus.queryAllByTimes(wepParam, weps);
			}
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
}
