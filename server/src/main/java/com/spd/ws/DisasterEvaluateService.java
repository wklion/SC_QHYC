package com.spd.ws;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.spd.business.ContinuesRainsEvaluateBus;
import com.spd.business.DisasterEvaluateBus;
import com.spd.business.DisasterStormBus;
import com.spd.business.LowTmpEvaluateBus;
import com.spd.business.RainStormEvaluateBus;
import com.spd.business.SnowBus;
import com.spd.business.StrongCoolingEvaluateBus;
import com.spd.common.RainStormAreaParam;
import com.spd.common.RainStormYearsParam;
import com.spd.common.TimesParam;
import com.spd.common.evaluate.AutumnRainsParam;
import com.spd.common.evaluate.ContinueRainAreaParam;
import com.spd.common.evaluate.ContinueRainStationParam;
import com.spd.common.evaluate.ContinueRainYearParam;
import com.spd.common.evaluate.HighTmpAreaYearsParam;
import com.spd.common.evaluate.LowTmpAreaParam;
import com.spd.common.evaluate.LowTmpStationParam;
import com.spd.common.evaluate.LowTmpYearParam;
import com.spd.common.evaluate.RainStormAreaTimesRangeParam;
import com.spd.common.evaluate.RainStormStationParam;
import com.spd.common.evaluate.SnowAreaParam;
import com.spd.common.evaluate.StrongCoolingAreaParam;
import com.spd.common.evaluate.StrongCoolingStationParam;
import com.spd.tool.LogTool;

/**
 * 灾害评估
 * @author Administrator
 *
 */
@Stateless
@Path("DisasterEvaluateService")
public class DisasterEvaluateService {

	/**
	 * 指定时间段统计暴雨
	 * @param para
	 * @return
	 */
	@POST
	@Path("rainstormByRange")
	@Produces("application/json")
	public Object rainstormByRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		RainStormAreaParam rainStormAreaParam = new RainStormAreaParam();
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			rainStormAreaParam.setTimesParam(timesParam);
			double minDayPre = jsonObject.getDouble("minDayPre");
			int minDayStations = jsonObject.getInt("minDayStations");
			double weight1 = jsonObject.getDouble("weight1");
			double weight2 = jsonObject.getDouble("weight2");
			double weight3 = jsonObject.getDouble("weight3");
			double weight4 = jsonObject.getDouble("weight4");
			rainStormAreaParam.setMinDayPre(minDayPre);
			rainStormAreaParam.setMinDayStations(minDayStations);
			rainStormAreaParam.setWeight1(weight1);
			rainStormAreaParam.setWeight2(weight2);
			rainStormAreaParam.setWeight3(weight3);
			rainStormAreaParam.setWeight4(weight4);
			RainStormEvaluateBus rainStormEvaluateBus = new RainStormEvaluateBus();
			Object result = rainStormEvaluateBus.rainstormByRange(rainStormAreaParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 高温评估
	 * @param para
	 * @return
	 */
	@POST
	@Path("highTmpByRange")
	@Produces("application/json")
	public Object highTmpByRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.highTmpByRange(timesParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 高温历年统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("highTmpByYears")
	@Produces("application/json")
	public Object highTmpByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		HighTmpAreaYearsParam highTmpAreaYearsParam = new HighTmpAreaYearsParam();
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			highTmpAreaYearsParam.setTimesParam(timesParam);
			highTmpAreaYearsParam.setStartYear(jsonObject.getInt("startYear"));
			highTmpAreaYearsParam.setEndYear(jsonObject.getInt("endYear"));
			highTmpAreaYearsParam.setPerennialStartYear(jsonObject.getInt("perennialStartYear"));
			highTmpAreaYearsParam.setPerennialEndYear(jsonObject.getInt("perennialEndYear"));
			highTmpAreaYearsParam.setYHILevel1(jsonObject.getDouble("YHILevel1"));
			highTmpAreaYearsParam.setYHILevel2(jsonObject.getDouble("YHILevel2"));
			highTmpAreaYearsParam.setYHILevel3(jsonObject.getDouble("YHILevel3"));
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.highTmpByYears(highTmpAreaYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 暴雨年度统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("rainstormByYears")
	@Produces("application/json")
	public Object rainstormByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam = new RainStormAreaTimesRangeParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			rainStormAreaTimesRangeParam.setTimesParam(timesParam);
			double maxPre = jsonObject.getDouble("maxPre");
			rainStormAreaTimesRangeParam.setMaxPre(maxPre);
			double maxSignalPre = jsonObject.getDouble("maxSignalPre");
			rainStormAreaTimesRangeParam.setMaxSignalPre(maxSignalPre);
			int maxPersistDays = jsonObject.getInt("maxPersistDays");
			rainStormAreaTimesRangeParam.setMaxPersistDays(maxPersistDays);
			int  maxStationCnt = jsonObject.getInt("maxStationCnt");
			rainStormAreaTimesRangeParam.setMaxStationCnt(maxStationCnt);
			double minPre = jsonObject.getDouble("minPre");
			rainStormAreaTimesRangeParam.setMinPre(minPre);
			double minSignalPre = jsonObject.getDouble("minSignalPre");
			rainStormAreaTimesRangeParam.setMinSignalPre(minSignalPre);
			int minPersistDays = jsonObject.getInt("minPersistDays");
			rainStormAreaTimesRangeParam.setMinPersistDays(minPersistDays);
			int minStationCnt = jsonObject.getInt("minStationCnt");
			rainStormAreaTimesRangeParam.setMinStationCnt(minStationCnt);
			String type = jsonObject.getString("type");
			rainStormAreaTimesRangeParam.setType(type);
			double weight1 = jsonObject.getDouble("weight1");
			double weight2 = jsonObject.getDouble("weight2");
			double weight3 = jsonObject.getDouble("weight3");
			double weight4 = jsonObject.getDouble("weight4");
			rainStormAreaTimesRangeParam.setWeight1(weight1);
			rainStormAreaTimesRangeParam.setWeight2(weight2);
			rainStormAreaTimesRangeParam.setWeight3(weight3);
			rainStormAreaTimesRangeParam.setWeight4(weight4);
			double level1 = jsonObject.getDouble("level1");
			double level2 = jsonObject.getDouble("level2");
			double level3 = jsonObject.getDouble("level3");
			double level4 = jsonObject.getDouble("level4");
			rainStormAreaTimesRangeParam.setLevel1(level1);
			rainStormAreaTimesRangeParam.setLevel2(level2);
			rainStormAreaTimesRangeParam.setLevel3(level3);
			rainStormAreaTimesRangeParam.setLevel4(level4);
			
			rainStormAreaTimesRangeParam.setStartYear(jsonObject.getInt("startYear"));
			rainStormAreaTimesRangeParam.setEndYear(jsonObject.getInt("endYear"));
			rainStormAreaTimesRangeParam.setPerennialStartYear(jsonObject.getInt("perennialStartYear"));
			rainStormAreaTimesRangeParam.setPerennialEndYear(jsonObject.getInt("perennialEndYear"));
			
			DisasterStormBus disasterStormBus = new DisasterStormBus();
			Object result = disasterStormBus.rainstormByYears(rainStormAreaTimesRangeParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	/**
	 * 秋雨计算
	 * @param para
	 * @return
	 */
	@POST
	@Path("autumnRains")
	@Produces("application/json")
	public Object autumnRains(@FormParam("para") String para) {
		AutumnRainsParam autumnRainsParam = new AutumnRainsParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			double level1 = jsonObject.getDouble("level1");
			double level2 = jsonObject.getDouble("level2");
			double level3 = jsonObject.getDouble("level3");
			double level4 = jsonObject.getDouble("level4");
			autumnRainsParam.setLevel1(level1);
			autumnRainsParam.setLevel2(level2);
			autumnRainsParam.setLevel3(level3);
			autumnRainsParam.setLevel4(level4);
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.autumnRains(autumnRainsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段查询秋雨对应的雨量等信息
	 * @param para
	 * @return
	 */
	@POST
	@Path("autumnRainsByTimes")
	@Produces("application/json")
	public Object autumnRainsByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.autumnRainsByTimes(timesParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 年度查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("autumnRainsByYear")
	@Produces("application/json")
	public Object autumnRainsByYear(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int year = 0;
		try {
			jsonObject = new JSONObject(para);
			year = jsonObject.getInt("year");
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.autumnRainsByYear(year);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 单站干旱过程，按时间段查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("mciStationByTimes")
	@Produces("application/json")
	public Object mciStationByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.mciStationByTimes(timesParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 单站干旱年度查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("mciStationByYears")
	@Produces("application/json")
	public Object mciStationByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.mciStationByYears(startYear, endYear);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 区域干旱过程，按时间段查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("mciAreaByTimes")
	@Produces("application/json")
	public Object mciAreaByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.mciAreaByTimes(timesParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 区域干旱年度查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("mciAreaByYears")
	@Produces("application/json")
	public Object mciAreaByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			DisasterEvaluateBus disasterEvaluateBus = new DisasterEvaluateBus();
			Object result = disasterEvaluateBus.mciAreaByYears(startYear, endYear);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按时间段查询区域暴雨过程
	 * @param para
	 * @return
	 */
	@POST
	@Path("areaStormByTimes")
	@Produces("application/json")
	public Object areaStormByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		RainStormAreaTimesRangeParam rainStormAreaTimesRangeParam = new RainStormAreaTimesRangeParam();
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			rainStormAreaTimesRangeParam.setTimesParam(timesParam);
			double maxPre = jsonObject.getDouble("maxPre");
			rainStormAreaTimesRangeParam.setMaxPre(maxPre);
			double maxSignalPre = jsonObject.getDouble("maxSignalPre");
			rainStormAreaTimesRangeParam.setMaxSignalPre(maxSignalPre);
			int maxPersistDays = jsonObject.getInt("maxPersistDays");
			rainStormAreaTimesRangeParam.setMaxPersistDays(maxPersistDays);
			int  maxStationCnt = jsonObject.getInt("maxStationCnt");
			rainStormAreaTimesRangeParam.setMaxStationCnt(maxStationCnt);
			double minPre = jsonObject.getDouble("minPre");
			rainStormAreaTimesRangeParam.setMinPre(minPre);
			double minSignalPre = jsonObject.getDouble("minSignalPre");
			rainStormAreaTimesRangeParam.setMinSignalPre(minSignalPre);
			int minPersistDays = jsonObject.getInt("minPersistDays");
			rainStormAreaTimesRangeParam.setMinPersistDays(minPersistDays);
			int minStationCnt = jsonObject.getInt("minStationCnt");
			rainStormAreaTimesRangeParam.setMinStationCnt(minStationCnt);
			String type = jsonObject.getString("type");
			rainStormAreaTimesRangeParam.setType(type);
			double weight1 = jsonObject.getDouble("weight1");
			double weight2 = jsonObject.getDouble("weight2");
			double weight3 = jsonObject.getDouble("weight3");
			double weight4 = jsonObject.getDouble("weight4");
			rainStormAreaTimesRangeParam.setWeight1(weight1);
			rainStormAreaTimesRangeParam.setWeight2(weight2);
			rainStormAreaTimesRangeParam.setWeight3(weight3);
			rainStormAreaTimesRangeParam.setWeight4(weight4);
			double level1 = jsonObject.getDouble("level1");
			double level2 = jsonObject.getDouble("level2");
			double level3 = jsonObject.getDouble("level3");
			double level4 = jsonObject.getDouble("level4");
			rainStormAreaTimesRangeParam.setLevel1(level1);
			rainStormAreaTimesRangeParam.setLevel2(level2);
			rainStormAreaTimesRangeParam.setLevel3(level3);
			rainStormAreaTimesRangeParam.setLevel4(level4);
			DisasterStormBus disasterStormBus = new DisasterStormBus();
			Object result = disasterStormBus.areaStormByTimes(rainStormAreaTimesRangeParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 单点暴雨查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("stationStormByTimes")
	@Produces("application/json")
	public Object stationStormByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		RainStormStationParam rainStormStationParam = new RainStormStationParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			rainStormStationParam.setTimesParam(timesParam);
			double maxStationPreTotal = jsonObject.getDouble("maxStationPreTotal");
			double minStationPreTotal = jsonObject.getDouble("minStationPreTotal");
			int maxStationCntTotal = jsonObject.getInt("maxStationCntTotal");
			int minStationCntTotal = jsonObject.getInt("minStationCntTotal");
			rainStormStationParam.setMaxStationCntTotal(maxStationCntTotal);
			rainStormStationParam.setMaxStationPreTotal(maxStationPreTotal);
			rainStormStationParam.setMinStationCntTotal(minStationCntTotal);
			rainStormStationParam.setMinStationPreTotal(minStationPreTotal);
			double level1 = jsonObject.getDouble("level1");
			double level2 = jsonObject.getDouble("level2");
			double level3 = jsonObject.getDouble("level3");
			double level4 = jsonObject.getDouble("level4");
			rainStormStationParam.setLevel1(level1);
			rainStormStationParam.setLevel2(level2);
			rainStormStationParam.setLevel3(level3);
			rainStormStationParam.setLevel4(level4);
			DisasterStormBus disasterStormBus = new DisasterStormBus();
			Object result = disasterStormBus.stationStormByTimes(rainStormStationParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 单站连阴雨按时间段查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("continueRainStatiionByTimes")
	@Produces("application/json")
	public Object continueRainStatiionByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		ContinueRainStationParam continueRainStationParam = new ContinueRainStationParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			continueRainStationParam.setTimesParam(timesParam);
			int maxSingleDays = jsonObject.getInt("maxSingleDays");
			continueRainStationParam.setMaxSingleDays(maxSingleDays);
			int maxSingleRainDays = jsonObject.getInt("maxSingleRainDays");
			continueRainStationParam.setMaxSingleRainDays(maxSingleRainDays);
			double maxSinglePre = jsonObject.getDouble("maxSinglePre");
			continueRainStationParam.setMaxSinglePre(maxSinglePre);
			int minSingleDays = jsonObject.getInt("minSingleDays");
			continueRainStationParam.setMinSingleDays(minSingleDays);
			int minSingleRainDays = jsonObject.getInt("minSingleRainDays");
			continueRainStationParam.setMinSingleRainDays(minSingleRainDays);
			double minSinglePre = jsonObject.getDouble("minSinglePre");
			continueRainStationParam.setMinSinglePre(minSinglePre);
			double persistDaysIndex = jsonObject.getDouble("persistDaysIndex");
			continueRainStationParam.setPersistDaysIndex(persistDaysIndex);
			double preDaysIndex = jsonObject.getDouble("preDaysIndex");
			continueRainStationParam.setPreDaysIndex(preDaysIndex);
			double preIndex = jsonObject.getDouble("preIndex");
			continueRainStationParam.setPreIndex(preIndex);
			double strengthIndex1 = jsonObject.getDouble("strengthIndex1");
			continueRainStationParam.setStrengthIndex1(strengthIndex1);
			double strengthIndex2 = jsonObject.getDouble("strengthIndex2");
			continueRainStationParam.setStrengthIndex2(strengthIndex2);
			double strengthIndex3 = jsonObject.getDouble("strengthIndex3");
			continueRainStationParam.setStrengthIndex3(strengthIndex3);
			double strengthIndex4 = jsonObject.getDouble("strengthIndex4");
			continueRainStationParam.setStrengthIndex4(strengthIndex4);
			ContinuesRainsEvaluateBus continuesRainsEvaluateBus = new ContinuesRainsEvaluateBus();
			Object result = continuesRainsEvaluateBus.continueRainStatiionByTimes(continueRainStationParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 区域连阴雨按时间段查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("continueRainAreaByTimes")
	@Produces("application/json")
	public Object continueRainAreaByTimes(@FormParam("para") String para) {
		ContinueRainAreaParam continueRainAreaParam = new ContinueRainAreaParam();
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			continueRainAreaParam.setTimesParam(timesParam);
			int maxPersistDays = jsonObject.getInt("maxPersistDays");
			continueRainAreaParam.setMaxPersistDays(maxPersistDays);
			int maxSumStations = jsonObject.getInt("maxSumStations");
			continueRainAreaParam.setMaxSumStations(maxSumStations);
			int maxRainDays = jsonObject.getInt("maxRainDays");
			continueRainAreaParam.setMaxRainDays(maxRainDays);
			double maxSumPres = jsonObject.getDouble("maxSumPres");
			continueRainAreaParam.setMaxSumPres(maxSumPres);
			int minPersistDays = jsonObject.getInt("minPersistDays");
			continueRainAreaParam.setMinPersistDays(minPersistDays);
			int minSumStations = jsonObject.getInt("minSumStations");
			continueRainAreaParam.setMinSumStations(minSumStations);
			int minRainDays = jsonObject.getInt("minRainDays");
			continueRainAreaParam.setMinRainDays(minRainDays);
			double minSumPres = jsonObject.getDouble("minSumPres");
			continueRainAreaParam.setMinSumPres(minSumPres);
			double strengthIndex1 = jsonObject.getDouble("strengthIndex1");
			continueRainAreaParam.setStrengthIndex1(strengthIndex1);
			double strengthIndex2 = jsonObject.getDouble("strengthIndex2");
			continueRainAreaParam.setStrengthIndex2(strengthIndex2);
			double strengthIndex3 = jsonObject.getDouble("strengthIndex3");
			continueRainAreaParam.setStrengthIndex3(strengthIndex3);
			double strengthIndex4 = jsonObject.getDouble("strengthIndex4");
			continueRainAreaParam.setStrengthIndex4(strengthIndex4);
			
			double index1 = jsonObject.getDouble("index1");
			continueRainAreaParam.setIndex1(index1);
			double index2 = jsonObject.getDouble("index2");
			continueRainAreaParam.setIndex2(index2);
			double index3 = jsonObject.getDouble("index3");
			continueRainAreaParam.setIndex3(index3);
			double index4 = jsonObject.getDouble("index4");
			continueRainAreaParam.setIndex4(index4);
			
			ContinuesRainsEvaluateBus continuesRainsEvaluateBus = new ContinuesRainsEvaluateBus();
			Object result = continuesRainsEvaluateBus.continueRainAreaByTimes(continueRainAreaParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 连阴雨年度指标
	 * @param para
	 * @return
	 */
	@POST
	@Path("continueRainByYear")
	@Produces("application/json")
	public Object continueRainByYear(@FormParam("para") String para) {
		ContinueRainYearParam continueRainYearParam = new ContinueRainYearParam();
		ContinueRainStationParam continueRainStationParam = new ContinueRainStationParam();
		ContinueRainAreaParam continueRainAreaParam = new ContinueRainAreaParam();
		JSONObject jsonObject = null;
		TimesParam timesParam = new TimesParam();
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			int perennialStartYear = jsonObject.getInt("perennialStartYear");
			int perennialEndYear = jsonObject.getInt("perennialEndYear");
			
			//单站
			int maxSingleDays = jsonObject.getInt("maxSingleDays");
			continueRainStationParam.setMaxSingleDays(maxSingleDays);
			int maxSingleRainDays = jsonObject.getInt("maxSingleRainDays");
			continueRainStationParam.setMaxSingleRainDays(maxSingleRainDays);
			double maxSinglePre = jsonObject.getDouble("maxSinglePre");
			continueRainStationParam.setMaxSinglePre(maxSinglePre);
			int minSingleDays = jsonObject.getInt("minSingleDays");
			continueRainStationParam.setMinSingleDays(minSingleDays);
			int minSingleRainDays = jsonObject.getInt("minSingleRainDays");
			continueRainStationParam.setMinSingleRainDays(minSingleRainDays);
			double minSinglePre = jsonObject.getDouble("minSinglePre");
			continueRainStationParam.setMinSinglePre(minSinglePre);
			double persistDaysIndex = jsonObject.getDouble("persistDaysIndex");
			continueRainStationParam.setPersistDaysIndex(persistDaysIndex);
			double preDaysIndex = jsonObject.getDouble("preDaysIndex");
			continueRainStationParam.setPreDaysIndex(preDaysIndex);
			double preIndex = jsonObject.getDouble("preIndex");
			continueRainStationParam.setPreIndex(preIndex);
			continueRainStationParam.setTimesParam(timesParam);
			continueRainStationParam.setStartYear(startYear);
			continueRainStationParam.setEndYear(endYear);
			continueRainStationParam.setPerennialStartYear(perennialStartYear);
			continueRainStationParam.setPerennialEndYear(perennialEndYear);
			//区域
			int maxPersistDays = jsonObject.getInt("maxPersistDays");
			continueRainAreaParam.setMaxPersistDays(maxPersistDays);
			int maxSumStations = jsonObject.getInt("maxSumStations");
			continueRainAreaParam.setMaxSumStations(maxSumStations);
			int maxRainDays = jsonObject.getInt("maxRainDays");
			continueRainAreaParam.setMaxRainDays(maxRainDays);
			double maxSumPres = jsonObject.getDouble("maxSumPres");
			continueRainAreaParam.setMaxSumPres(maxSumPres);
			int minPersistDays = jsonObject.getInt("minPersistDays");
			continueRainAreaParam.setMinPersistDays(minPersistDays);
			int minSumStations = jsonObject.getInt("minSumStations");
			continueRainAreaParam.setMinSumStations(minSumStations);
			int minRainDays = jsonObject.getInt("minRainDays");
			continueRainAreaParam.setMinRainDays(minRainDays);
			double minSumPres = jsonObject.getDouble("minSumPres");
			continueRainAreaParam.setMinSumPres(minSumPres);
			continueRainAreaParam.setTimesParam(timesParam);
			double strengthIndex1 = jsonObject.getDouble("strengthIndex1");
			double strengthIndex2 = jsonObject.getDouble("strengthIndex2");
			double strengthIndex3 = jsonObject.getDouble("strengthIndex3");
			double strengthIndex4 = jsonObject.getDouble("strengthIndex4");
			continueRainAreaParam.setStrengthIndex1(strengthIndex1);
			continueRainAreaParam.setStrengthIndex2(strengthIndex2);
			continueRainAreaParam.setStrengthIndex3(strengthIndex3);
			continueRainAreaParam.setStrengthIndex4(strengthIndex4);
			//年度
			double maxStationStrength = jsonObject.getDouble("maxStationStrength");
			double maxAreaStrength = jsonObject.getDouble("maxAreaStrength");
			double minStationStrength = jsonObject.getDouble("minStationStrength");
			double minAreaStrength = jsonObject.getDouble("minAreaStrength");
			double yearStrengthIndex1 = jsonObject.getDouble("yearStrengthIndex1");
			double yearStrengthIndex2 = jsonObject.getDouble("yearStrengthIndex2");
			double yearStrengthIndex3 = jsonObject.getDouble("yearStrengthIndex3");
			double yearStrengthIndex4 = jsonObject.getDouble("yearStrengthIndex4");
			continueRainYearParam.setMaxStationStrength(maxStationStrength);
			continueRainYearParam.setMaxAreaStrength(maxAreaStrength);
			continueRainYearParam.setMinStationStrength(minStationStrength);
			continueRainYearParam.setMinAreaStrength(minAreaStrength);
			continueRainYearParam.setStrengthIndex1(yearStrengthIndex1);
			continueRainYearParam.setStrengthIndex2(yearStrengthIndex2);
			continueRainYearParam.setStrengthIndex3(yearStrengthIndex3);
			continueRainYearParam.setStrengthIndex4(yearStrengthIndex4);
			continueRainYearParam.setTimesParam(timesParam);
			ContinuesRainsEvaluateBus continuesRainsEvaluateBus = new ContinuesRainsEvaluateBus();
			Object result = continuesRainsEvaluateBus.continueRainByYear(continueRainYearParam, continueRainStationParam, continueRainAreaParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 单站强降温，时间段查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("strongCoolingStationByTimes")
	@Produces("application/json")
	public Object strongCoolingStationByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			StrongCoolingEvaluateBus strongCoolingEvaluateBus = new StrongCoolingEvaluateBus();
			StrongCoolingStationParam strongCoolingStationParam = new StrongCoolingStationParam();
			strongCoolingStationParam.setTimesParam(timesParam);
			Object result = strongCoolingEvaluateBus.strongCoolingStationByTimes(strongCoolingStationParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 区域强降温，时间段查询
	 * @param para
	 * @return
	 */
	@POST
	@Path("strongCoolingAreaByTimes")
	@Produces("application/json")
	public Object strongCoolingAreaByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			StrongCoolingAreaParam strongCoolingAreaParam = new StrongCoolingAreaParam();
			strongCoolingAreaParam.setTimesParam(timesParam);
			int maxStations = jsonObject.getInt("maxStations");
			int minStations = jsonObject.getInt("minStations");
			int maxPersistDays = jsonObject.getInt("maxPersistDays");
			int minPersistDays = jsonObject.getInt("minPersistDays");
			strongCoolingAreaParam.setMaxStations(maxStations);
			strongCoolingAreaParam.setMinStations(minStations);
			strongCoolingAreaParam.setMaxPersistDays(maxPersistDays);
			strongCoolingAreaParam.setMinPersistDays(minPersistDays);
			double maxCoolingTmp = jsonObject.getDouble("maxCoolingTmp");
			double minCoolingTmp = jsonObject.getDouble("minCoolingTmp");
			strongCoolingAreaParam.setMaxCoolingTmp(maxCoolingTmp);
			strongCoolingAreaParam.setMinCoolingTmp(minCoolingTmp);
			double weight1 = jsonObject.getDouble("weight1");
			strongCoolingAreaParam.setWeight1(weight1);
			double weight2 = jsonObject.getDouble("weight2");
			strongCoolingAreaParam.setWeight2(weight2);
			double weight3 = jsonObject.getDouble("weight3");
			strongCoolingAreaParam.setWeight3(weight3);
			double weight4 = jsonObject.getDouble("weight4");
			strongCoolingAreaParam.setWeight4(weight4);
			Double level1 = jsonObject.getDouble("level1");
			strongCoolingAreaParam.setLevel1(level1);
			Double level2 = jsonObject.getDouble("level2");
			strongCoolingAreaParam.setLevel2(level2);
			Double level3 = jsonObject.getDouble("level3");
			strongCoolingAreaParam.setLevel3(level3);
			StrongCoolingEvaluateBus strongCoolingEvaluateBus = new StrongCoolingEvaluateBus();
			Object result = strongCoolingEvaluateBus.strongCoolingAreaByTimes(strongCoolingAreaParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 年度强降温
	 * @param para
	 * @return
	 */
	@POST
	@Path("strongCoolingByYear")
	@Produces("application/json")
	public Object strongCoolingByYear(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			StrongCoolingAreaParam strongCoolingAreaParam = new StrongCoolingAreaParam();
			strongCoolingAreaParam.setTimesParam(timesParam);
			int maxStations = jsonObject.getInt("maxStations");
			int minStations = jsonObject.getInt("minStations");
			int maxPersistDays = jsonObject.getInt("maxPersistDays");
			int minPersistDays = jsonObject.getInt("minPersistDays");
			strongCoolingAreaParam.setMaxStations(maxStations);
			strongCoolingAreaParam.setMinStations(minStations);
			strongCoolingAreaParam.setMaxPersistDays(maxPersistDays);
			strongCoolingAreaParam.setMinPersistDays(minPersistDays);
			double maxCoolingTmp = jsonObject.getDouble("maxCoolingTmp");
			double minCoolingTmp = jsonObject.getDouble("minCoolingTmp");
			strongCoolingAreaParam.setMaxCoolingTmp(maxCoolingTmp);
			strongCoolingAreaParam.setMinCoolingTmp(minCoolingTmp);
			double weight1 = jsonObject.getDouble("weight1");
			strongCoolingAreaParam.setWeight1(weight1);
			double weight2 = jsonObject.getDouble("weight2");
			strongCoolingAreaParam.setWeight2(weight2);
			double weight3 = jsonObject.getDouble("weight3");
			strongCoolingAreaParam.setWeight3(weight3);
			double weight4 = jsonObject.getDouble("weight4");
			strongCoolingAreaParam.setWeight4(weight4);
			Double level1 = jsonObject.getDouble("level1");
			strongCoolingAreaParam.setLevel1(level1);
			Double level2 = jsonObject.getDouble("level2");
			strongCoolingAreaParam.setLevel2(level2);
			Double level3 = jsonObject.getDouble("level3");
			strongCoolingAreaParam.setLevel3(level3);
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			int perennialStartYear = jsonObject.getInt("perennialStartYear");
			int perennialEndYear = jsonObject.getInt("perennialEndYear");
			strongCoolingAreaParam.setStartYear(startYear);
			strongCoolingAreaParam.setEndYear(endYear);
			strongCoolingAreaParam.setPerennialStartYear(perennialStartYear);
			strongCoolingAreaParam.setPerennialEndYear(perennialEndYear);
			StrongCoolingEvaluateBus strongCoolingEvaluateBus = new StrongCoolingEvaluateBus();		
			Object result = strongCoolingEvaluateBus.strongCoolingByYear(strongCoolingAreaParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 低温单站统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("lowTmpStationByTimes")
	@Produces("application/json")
	public Object lowTmpStationByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		LowTmpStationParam lowTmpStationParam = new LowTmpStationParam();
		try {
			jsonObject = new JSONObject(para);
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			lowTmpStationParam.setTimesParam(timesParam);
			LowTmpEvaluateBus lowTmpEvaluateBus = new LowTmpEvaluateBus(); 
			Object result = lowTmpEvaluateBus.lowTmpStationByTimes(lowTmpStationParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 低温区域统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("lowTmpAreaByTimes")
	@Produces("application/json")
	public Object lowTmpAreaByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		LowTmpAreaParam lowTmpAreaParam = new LowTmpAreaParam();
		try {
			jsonObject = new JSONObject(para);
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			lowTmpAreaParam.setTimesParam(timesParam);
			int maxPersistDays = jsonObject.getInt("maxPersistDays");
			lowTmpAreaParam.setMaxPersistDays(maxPersistDays);
			int minPersistDays = jsonObject.getInt("minPersistDays");
			lowTmpAreaParam.setMinPersistDays(minPersistDays);
			int maxSumStation = jsonObject.getInt("maxSumStation");
			lowTmpAreaParam.setMaxSumStation(maxSumStation);
			int minSumStation = jsonObject.getInt("minSumStation");
			lowTmpAreaParam.setMinSumStation(minSumStation);
			double maxSumAnomaly = jsonObject.getDouble("maxSumAnomaly");
			lowTmpAreaParam.setMaxSumAnomaly(maxSumAnomaly);
			double minSumAnomaly = jsonObject.getDouble("minSumAnomaly");
			lowTmpAreaParam.setMinSumAnomaly(minSumAnomaly);
			double persistDayWeight = jsonObject.getDouble("persistDayWeight");
			lowTmpAreaParam.setPersistDayWeight(persistDayWeight);
			double sumStationWeight = jsonObject.getDouble("sumStationWeight");
			lowTmpAreaParam.setSumStationWeight(sumStationWeight);
			double anomalyWeight = jsonObject.getDouble("anomalyWeight");
			lowTmpAreaParam.setAnomalyWeight(anomalyWeight);
			double level1 = jsonObject.getDouble("level1");
			lowTmpAreaParam.setLevel1(level1);
			double level2 = jsonObject.getDouble("level2");
			lowTmpAreaParam.setLevel2(level2);
			double level3 = jsonObject.getDouble("level3");
			lowTmpAreaParam.setLevel3(level3);
			double level4 = jsonObject.getDouble("level4");
			lowTmpAreaParam.setLevel4(level4);
			LowTmpEvaluateBus lowTmpEvaluateBus = new LowTmpEvaluateBus(); 
			Object result = lowTmpEvaluateBus.lowTmpAreaByTimes(lowTmpAreaParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	
	/**
	 * 年度低温统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("lowTmpByYear")
	@Produces("application/json")
	public Object lowTmpByYear(@FormParam("para") String para) {
		LowTmpYearParam lowTmpYearParam = new LowTmpYearParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			lowTmpYearParam.setTimesParam(timesParam);
			int maxPersistDays = jsonObject.getInt("maxPersistDays");
			lowTmpYearParam.setMaxPersistDays(maxPersistDays);
			int minPersistDays = jsonObject.getInt("minPersistDays");
			lowTmpYearParam.setMinPersistDays(minPersistDays);
			int maxSumStation = jsonObject.getInt("maxSumStation");
			lowTmpYearParam.setMaxSumStation(maxSumStation);
			int minSumStation = jsonObject.getInt("minSumStation");
			lowTmpYearParam.setMinSumStation(minSumStation);
			double maxSumAnomaly = jsonObject.getDouble("maxSumAnomaly");
			lowTmpYearParam.setMaxSumAnomaly(maxSumAnomaly);
			double minSumAnomaly = jsonObject.getDouble("minSumAnomaly");
			lowTmpYearParam.setMinSumAnomaly(minSumAnomaly);
			double persistDayWeight = jsonObject.getDouble("persistDayWeight");
			lowTmpYearParam.setPersistDayWeight(persistDayWeight);
			double sumStationWeight = jsonObject.getDouble("sumStationWeight");
			lowTmpYearParam.setSumStationWeight(sumStationWeight);
			double anomalyWeight = jsonObject.getDouble("anomalyWeight");
			lowTmpYearParam.setAnomalyWeight(anomalyWeight);
//			double level1 = jsonObject.getDouble("level1");
//			lowTmpYearParam.setLevel1(level1);
//			double level2 = jsonObject.getDouble("level2");
//			lowTmpYearParam.setLevel2(level2);
//			double level3 = jsonObject.getDouble("level3");
//			lowTmpYearParam.setLevel3(level3);
			int startYear = jsonObject.getInt("startYear");
			lowTmpYearParam.setStartYear(startYear);
			int endYear = jsonObject.getInt("endYear");
			lowTmpYearParam.setEndYear(endYear);
			int standardStartYear = jsonObject.getInt("standardStartYear");
			lowTmpYearParam.setStandardStartYear(standardStartYear);
			int standardEndYear = jsonObject.getInt("standardEndYear");
			lowTmpYearParam.setStandardEndYear(standardEndYear);
			LowTmpEvaluateBus lowTmpEvaluateBus = new LowTmpEvaluateBus(); 
			Object result = lowTmpEvaluateBus.lowTmpByYear(lowTmpYearParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}	
	}
	
	/**
	 * 降雪的区域评估，查询所有的结果
	 * @param para
	 * @return
	 */
	@POST
	@Path("snowArea")
	@Produces("application/json")
	public Object snowArea(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try{
			jsonObject = new JSONObject(para);
			SnowAreaParam snowAreaParam = new SnowAreaParam();
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			snowAreaParam.setTimesParam(timesParam);
			double IA = jsonObject.getDouble("IA");
			snowAreaParam.setIA(IA);
			double IB = jsonObject.getDouble("IB");
			snowAreaParam.setIB(IB);
			double IC = jsonObject.getDouble("IC");
			snowAreaParam.setIC(IC);
			double ID = jsonObject.getDouble("ID");
			snowAreaParam.setID(ID);
			double level1 = jsonObject.getDouble("level1");
			snowAreaParam.setLevel1(level1);
			double level2 = jsonObject.getDouble("level2");
			snowAreaParam.setLevel2(level2);
			double level3 = jsonObject.getDouble("level3");
			snowAreaParam.setLevel3(level3);
			double level4 = jsonObject.getDouble("level4");
			snowAreaParam.setLevel4(level4);
			SnowBus snowBus = new SnowBus();
			Object result = snowBus.snowArea(snowAreaParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		
	}
}
