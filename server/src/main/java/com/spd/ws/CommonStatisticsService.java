package com.spd.ws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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

import com.spd.business.CommonStatisticsDispose;
import com.spd.business.CommonStatisticsFilter;
import com.spd.business.StatisticsBus;
import com.spd.common.TmpDaysYearParam;
import com.spd.common.TmpDaysYearResult;
import com.spd.pojo.AvgMaxTmp;
import com.spd.pojo.AvgMinTmp;
import com.spd.pojo.AvgTmp;
import com.spd.pojo.AvgTmpResult;
import com.spd.pojo.ExtTmp;
import com.spd.pojo.ExtTmpMaxItem;
import com.spd.pojo.ExtTmpMinItem;
import com.spd.pojo.PreCnt;
import com.spd.pojo.PreCntItem;
import com.spd.pojo.PreSum;
import com.spd.pojo.PreTimeItem;
import com.spd.pojo.PrsAvg;
import com.spd.pojo.PrsAvgItem;
import com.spd.pojo.RHU;
import com.spd.pojo.RHUItem;
import com.spd.pojo.SSH;
import com.spd.pojo.SSHItem;
import com.spd.pojo.TmpAvgItem;
import com.spd.pojo.TmpMaxAvgItem;
import com.spd.pojo.TmpMaxCnt;
import com.spd.pojo.TmpMaxCntItem;
import com.spd.pojo.TmpMaxCntResult;
import com.spd.pojo.TmpMinAvgItem;
import com.spd.pojo.VisMin;
import com.spd.pojo.VisMinItem;
import com.spd.pojo.Win_s_2mi_avgItem;
import com.spd.pojo.Win_s_2min_avg;
import com.spd.tool.LogTool;

/**
 * 常规统计服务，把客户端的请求一次性接收，返回处理结果，使客户端不再处理相关逻辑
 * @author Administrator
 *
 */
@Stateless
@Path("CommonStatisticsService")
public class CommonStatisticsService {

	private static StatisticsBus statisticsBus = new StatisticsBus();
	
	/**
	 * 平均气温统计
	 * @param para 
	 * 格式：{"startTime":"2016-02-01 00:00:00", "endTime":"2016-02-10 00:00:00", "contrastType":"range", "contrastStartTime":"2015-02-01 00:00:00", "contrastEndTIme":"2015-02-10 00:00:00", "stationType":"AWS"}
	 * 或者：{"startTime":"2016-02-01 00:00:00", "endTime":"2016-02-10 00:00:00", "contrastType":"sameTeam", "startYear":1981, "endYear":2010,"stationType":"AWS"}
	 * @return
	 */
	@POST
	@Path("queryAvgTem")
	@Produces("application/json")
	public Object queryAvgTem(@FormParam("para") String para) {
		// 参数分为两部分，第一部分：时间范围。第二部分：时间范围或者历年同期
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		// 时段范围的结果。
		List<TmpAvgItem> resultList = (List<TmpAvgItem>) statisticsBus.queryAvgTemByTimeRange(startTime, endTime);
		List<TmpAvgItem> contrastList = null;
		if("range".equals(contrastType)) {
			// 对比时段范围
			contrastList = (List<TmpAvgItem>) statisticsBus.queryAvgTemByTimeRange(contrastStartTime, contrastEndTime);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<TmpAvgItem>) statisticsBus.queryAvgTemByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<AvgTmp> avgTmpResult = commonStatisticsDispose.avgTmpAnomaly(resultList, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<AvgTmp> avgTmpResult2 = commonStatisticsFilter.filterAvgTem(avgTmpResult);
		return avgTmpResult2;
	}
	
	/**
	 * 高温均值统计
	 * @param para 
	 * 格式：{"startTime":"2016-02-01 00:00:00", "endTime":"2016-02-10 00:00:00", "contrastType":"range", "contrastStartTime":"2015-02-01 00:00:00", "contrastEndTIme":"2015-02-10 00:00:00"}
	 * 或者：{"startTime":"2016-02-01 00:00:00", "endTime":"2016-02-10 00:00:00", "contrastType":"sameTeam", "startYear":1981, "endYear":2010}
	 * @return
	 */
	@POST
	@Path("queryAvgTemMax")
	@Produces("application/json")
	public Object queryAvgTemMax(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		// 时段范围的结果。
		List<TmpMaxAvgItem> resultList = (List<TmpMaxAvgItem>) statisticsBus.queryAvgTemMaxByTimeRange(startTime, endTime);
		List<TmpMaxAvgItem> contrastList = null;
		if("range".equals(contrastType)) {
			// 对比时段范围
			contrastList = (List<TmpMaxAvgItem>) statisticsBus.queryAvgTemMaxByTimeRange(contrastStartTime, contrastEndTime);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<TmpMaxAvgItem>) statisticsBus.queryAvgTemMaxByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<AvgMaxTmp> avgMaxTmpList = commonStatisticsDispose.avgTmpMaxAnomaly(resultList, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<AvgMaxTmp> avgTmpResult2 = commonStatisticsFilter.filterAvgTemMax(avgMaxTmpList);
		return avgTmpResult2;
	}
	
	/**
	 * 低温均值统计
	 * @param para 
	 * 格式：{"startTime":"2016-02-01 00:00:00", "endTime":"2016-02-10 00:00:00", "contrastType":"range", "contrastStartTime":"2015-02-01 00:00:00", "contrastEndTIme":"2015-02-10 00:00:00"}
	 * 或者：{"startTime":"2016-02-01 00:00:00", "endTime":"2016-02-10 00:00:00", "contrastType":"sameTeam", "startYear":1981, "endYear":2010}
	 * @return
	 */
	@POST
	@Path("queryAvgTemMin")
	@Produces("application/json")
	public Object queryAvgTemMin(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		// 时段范围的结果。
		List<TmpMinAvgItem> resultList = (List<TmpMinAvgItem>) statisticsBus.queryAvgTemMinByTimeRange(startTime, endTime);
		List<TmpMinAvgItem> contrastList = null;
		if("range".equals(contrastType)) {
			// 对比时段范围
			contrastList = (List<TmpMinAvgItem>) statisticsBus.queryAvgTemMinByTimeRange(contrastStartTime, contrastEndTime);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<TmpMinAvgItem>) statisticsBus.queryAvgTemMinByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<AvgMinTmp> avgMinTmpList = commonStatisticsDispose.avgTmpMinAnomaly(resultList, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<AvgMinTmp> avgMinTmpList2 = commonStatisticsFilter.filterAvgMinTmp(avgMinTmpList);
		return avgMinTmpList2;
	}
	
	/**
	 * 统计降水总量
	 * @param para
	 * 格式：{"startTime":"2016-02-01 00:00:00", "endTime":"2016-02-10 00:00:00", "type":"2020", "contrastType":"range", "contrastStartTime":"2014-02-01 00:00:00", "contrastEndTime":"2016-02-10 00:00:00"}
	 * 或者：{"startYear":1981, "endYear":2010, "type":"0808", "contrastType":"sameTeam", "startYear":1981, "endYear":2010}
	 * @return
	 */
	@POST
	@Path("queryPreSum")
	@Produces("application/json")
	public Object queryPreSum(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "", type = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			type = jsonObject.getString("type");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		List<PreTimeItem> resultList = (List<PreTimeItem>) statisticsBus.queryPreSumByTimeRange(startTime, endTime, type, stationType);
		List<PreTimeItem> contrastList = null;
		if("range".equals(contrastType)) {
			contrastList = (List<PreTimeItem>) statisticsBus.queryPreSumByTimeRange(contrastStartTime, contrastEndTime, type, stationType);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<PreTimeItem>) statisticsBus.queryPreSumByYears(startYear, endYear, startMonth, endMonth, startDay, endDay, type);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<PreSum> preSumResult = commonStatisticsDispose.preSumMaxAnomaly(resultList, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<PreSum> preSumResult2 = commonStatisticsFilter.filterSumPre(preSumResult);
		return preSumResult2;
	}
	
	/**
	 * 统计相对湿度
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryRHU")
	@Produces("application/json")
	public Object queryRHU(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		List<RHUItem> resultList = (List<RHUItem>) statisticsBus.queryRHUAvgByTimeRange(startTime, endTime);
		List<RHUItem> contrastList = null;
		if("range".equals(contrastType)) {
			contrastList = (List<RHUItem>) statisticsBus.queryRHUAvgByTimeRange(contrastStartTime, contrastEndTime);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<RHUItem>) statisticsBus.queryRHUByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<RHU> rhuList =  commonStatisticsDispose.rHUAnomaly(resultList, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<RHU> rhuList2 =  commonStatisticsFilter.filterRHU(rhuList);
		return rhuList2;
	}
	
	/**
	 * 统计平均风速
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryWin_s_2mi_avg")
	@Produces("application/json")
	public Object queryWin_s_2mi_avg(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		List<Win_s_2mi_avgItem> resultList = (List<Win_s_2mi_avgItem>) statisticsBus.queryWin_s_2mi_avgByTimeRange(startTime, endTime);
		List<Win_s_2mi_avgItem> contrastList = null;
		if("range".equals(contrastType)) {
			contrastList = (List<Win_s_2mi_avgItem>) statisticsBus.queryWin_s_2mi_avgByTimeRange(contrastStartTime, contrastEndTime);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<Win_s_2mi_avgItem>) statisticsBus.queryWin_s_2mi_avgByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<Win_s_2min_avg> win_s_2min_avgList = commonStatisticsDispose.win_s_2mi_avgItemAnomaly(resultList, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<Win_s_2min_avg> win_s_2min_avgList2 = commonStatisticsFilter.filterWin_s_2min_avg(win_s_2min_avgList);
		return win_s_2min_avgList2;
	}
	
	/**
	 * 统计平均气压
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryPrsAvg")
	@Produces("application/json")
	public Object queryPrsAvg(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		List<PrsAvgItem> resultList = (List<PrsAvgItem>) statisticsBus.queryPrsAvgByTimeRange(startTime, endTime);
		List<PrsAvgItem> contrastList = null;
		if("range".equals(contrastType)) {
			contrastList = (List<PrsAvgItem>) statisticsBus.queryPrsAvgByTimeRange(contrastStartTime, contrastEndTime);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<PrsAvgItem>) statisticsBus.queryPrsAvgByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<PrsAvg> prsAvgList = commonStatisticsDispose.prsAvgItemAnomaly(resultList, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<PrsAvg> prsAvgList2 = commonStatisticsFilter.filterPrsAvg(prsAvgList);
		return prsAvgList2;
	}
	
	/**
	 * 统计极端气温，包括极端高温，极端低温，以前极端气温出现的时间。
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryExtTmp")
	@Produces("application/json")
	public Object queryExtTmp(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, stationType;
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			stationType = jsonObject.getString("stationType");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		station_Id_CSet = getStationSets(jsonObject);
		List<ExtTmpMaxItem> extMaxResultList = (List<ExtTmpMaxItem>) statisticsBus.queryExtMaxTmpByTimeRange(startTime, endTime);
		List<ExtTmpMinItem> extMinResultList = (List<ExtTmpMinItem>) statisticsBus.queryExtMinTmpByTimeRange(startTime, endTime);
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<ExtTmp> extTmpResult = commonStatisticsDispose.extTmpResultAnomaly(extMaxResultList, extMinResultList, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<ExtTmp> extTmpResult2 = commonStatisticsFilter.filterExtTmp(extTmpResult);
		return extTmpResult2;
	}
	
	
	/**
	 * 统计能见度低值。
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryVisMin")
	@Produces("application/json")
	public Object queryVisMin(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, stationType;
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			stationType = jsonObject.getString("stationType");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		station_Id_CSet = getStationSets(jsonObject);
		List<VisMinItem> extMaxResultList = (List<VisMinItem>) statisticsBus.queryVisMinByTimeRange(startTime, endTime);
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<VisMin> visMinResult = commonStatisticsDispose.visMin(extMaxResultList, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<VisMin> visMinResult2 = commonStatisticsFilter.filterVisMin(visMinResult);
		return visMinResult2;
	}
	
	/**
	 * 统计降水日数
	 * @param para
	 * 格式：{"startTime":"2016-02-01 00:00:00", "endTime":"2016-02-10 00:00:00", "type":"2020", "contrastType":"range", "contrastStartTime":"2014-02-01 00:00:00", "contrastEndTime":"2016-02-10 00:00:00"}
	 * 或者：{"startYear":1981, "endYear":2010, "type":"0808", "contrastType":"sameTeam", "startYear":1981, "endYear":2010}
	 * @return
	 */
	@POST
	@Path("queryPreCnt")
	@Produces("application/json")
	public Object queryPreCnt(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		List<PreCntItem> resultList = (List<PreCntItem>) statisticsBus.queryPreCntByTimeRange(startTime, endTime);
		List<PreCntItem> contrastList = null;
		if("range".equals(contrastType)) {
			contrastList = (List<PreCntItem>) statisticsBus.queryPreCntByTimeRange(contrastStartTime, contrastEndTime);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<PreCntItem>) statisticsBus.queryPreCntByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<PreCnt> preCntResult = commonStatisticsDispose.queryPreCntAnomaly(resultList, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<PreCnt> preCntResult2 = commonStatisticsFilter.filterPreCnt(preCntResult);
		return preCntResult2;
	}
	
	/**
	 * 高温日数统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryTmpMaxCnt")
	@Produces("application/json")
	public Object queryTmpMaxCnt(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime, stationType;
		long start, end;
		Set<String> station_Id_CSet = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			stationType = jsonObject.getString("stationType");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		station_Id_CSet = getStationSets(jsonObject);
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		List<TmpMaxCntItem> resultList = (List<TmpMaxCntItem>) statisticsBus.queryTmpMaxCntByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<TmpMaxCnt> tmpMaxCntResult = commonStatisticsDispose.tmpMaxCnt(resultList, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<TmpMaxCnt> tmpMaxCntResult2 = commonStatisticsFilter.filterTmpMaxCnt(tmpMaxCntResult);
		return tmpMaxCntResult2;
	}
	
	/**
	 * 统计日照
	 * @param para
	 * @return
	 */
	@POST
	@Path("querySSH")
	@Produces("application/json")
	public Object querySSH(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startTime, endTime, contrastStartTime = "", contrastEndTime = "";
		int startYear = 0, endYear = 0, startMonth = 0, endMonth = 0, startDay = 0, endDay = 0;
		String contrastType = "", stationType = "";
		Date startDate, endDate;
		Set<String> station_Id_CSet = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			contrastType = jsonObject.getString("contrastType");
			stationType = jsonObject.getString("stationType");
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
			} else if("sameTeam".equals(contrastType)) {
				// 历年同期
				startYear = jsonObject.getInt("startYear");
				endYear = jsonObject.getInt("endYear");
				startMonth = Integer.parseInt(startTime.substring(5, 7));
				endMonth = Integer.parseInt(endTime.substring(5, 7));
				startDay = Integer.parseInt(startTime.substring(8, 10));
				endDay = Integer.parseInt(endTime.substring(8, 10));
			} else {
				return "未知的对比类型：【" + contrastType + "】";
			}
			station_Id_CSet = getStationSets(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "日期转换异常" + e.getMessage();
		}
		
		List<SSHItem> resultList = (List<SSHItem>) statisticsBus.querySSHByTimeRange(startTime, endTime);
		List<SSHItem> contrastList = null;
		if("range".equals(contrastType)) {
			contrastList = (List<SSHItem>) statisticsBus.querySSHByTimeRange(contrastStartTime, contrastEndTime);
		} else if("sameTeam".equals(contrastType)) {
			contrastList = (List<SSHItem>) statisticsBus.querySSHSumByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		}
		CommonStatisticsDispose commonStatisticsDispose = new CommonStatisticsDispose();
		List<SSH> sshResultList = commonStatisticsDispose.sshAnomaly(resultList,startDate, endDate, contrastList, contrastType, stationType);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<SSH> sshResultList2 = commonStatisticsFilter.filterSSH(sshResultList);
		return sshResultList2;
	}
	
	/**
	 * 计算年度空调度日、采暖度日，如果是采暖度日，查询，返回全部的低于指定温度的值。如果是空调度日，查询，返回全部高于指定温度的值。
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryTmpDaysByYear")
	@Produces("application/json")
	public Object queryTmpDaysByYear(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			TmpDaysYearParam tmpDaysYearParam = new TmpDaysYearParam();
			String type = jsonObject.getString("type");
			double tmp = jsonObject.getDouble("tmp");
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			String station_Id_C = jsonObject.getString("station_Id_C");
			tmpDaysYearParam.setType(type);
			tmpDaysYearParam.setTmp(tmp);
			tmpDaysYearParam.setStartYear(startYear);
			tmpDaysYearParam.setEndYear(endYear);
			tmpDaysYearParam.setStation_Id_C(station_Id_C);
			List<TmpDaysYearResult> resultList = statisticsBus.queryTmpDaysByYear(tmpDaysYearParam);
			return resultList;
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
