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

import com.spd.business.CommonStatisticsDispose;
import com.spd.business.CommonStatisticsFilter;
import com.spd.business.TmpGapBus;
import com.spd.common.TimesParam;
import com.spd.common.TmpGapByYearsResult;
import com.spd.common.TmpGapTimesParam;
import com.spd.common.TmpGapTimesResult;
import com.spd.pojo.TmpGapAvgItem;
import com.spd.pojo.TmpGapAvgYearResult;
import com.spd.tool.LogTool;

/**
 * 计算文件日较差、年较差
 * @author Administrator
 *
 */
@Stateless
@Path("TmpGapService")
public class TmpGapService {

	/**
	 * 计算气温的日较差
	 * @param para
	 * @return
	 */
	@POST
	@Path("getTmpByTimes")
	@Produces("application/json")
	public Object getTmpByTimes(@FormParam("para") String para) {
		TmpGapBus tmpGapBus = new TmpGapBus();
		JSONObject jsonObject = null;
		String startTime = "", endTime = "", contrastStartTime = "", contrastEndTime = "";
		String stationType = "", contrastType = "";
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			stationType = jsonObject.getString("stationType");
			contrastType = jsonObject.getString("contrastType");
			
			TmpGapTimesParam tmpGapTimesParam = new TmpGapTimesParam();
			if("range".equals(contrastType)) {
				// 对比时段范围
				contrastStartTime = jsonObject.getString("contrastStartTime");
				contrastEndTime = jsonObject.getString("contrastEndTime");
				TimesParam contrastTimeParam = new TimesParam();
				contrastTimeParam.setStartTimeStr(contrastStartTime);
				contrastTimeParam.setEndTimeStr(contrastEndTime);
				tmpGapTimesParam.setContrastTimeParam(contrastTimeParam);
			} else if("sameTeam".equals(contrastType)) {
				//历年同期
				int startYear = jsonObject.getInt("startYear");
				int endYear = jsonObject.getInt("endYear");
				tmpGapTimesParam.setStartYear(startYear);
				tmpGapTimesParam.setEndYear(endYear);
			}
			station_Id_CSet = getStationSets(jsonObject);
			TimesParam timesParam = new TimesParam();
			timesParam.setStartTimeStr(startTime);
			timesParam.setEndTimeStr(endTime);
			tmpGapTimesParam.setTimesParam(timesParam);
			tmpGapTimesParam.setStationType(stationType);
			List<TmpGapAvgItem> tmpGapAvgList = tmpGapBus.getTmpByTimes(tmpGapTimesParam);
			CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
			if("range".equals(contrastType)) {
				tmpGapTimesParam.setTimesParam(tmpGapTimesParam.getContrastTimeParam());
				List<TmpGapAvgItem> tmpGapAvgContrastList = tmpGapBus.getTmpByTimes(tmpGapTimesParam);
				List<TmpGapTimesResult> tmpGapTimesResultList = tmpGapBus.compareTmpGaps(tmpGapAvgList, tmpGapAvgContrastList);
				tmpGapTimesResultList = commonStatisticsFilter.filterTmpGapTimesResult(tmpGapTimesResultList);
				return tmpGapTimesResultList;
			} else if("sameTeam".equals(contrastType)) {
				List<TmpGapAvgItem> yearsTmpGapAvgList = tmpGapBus.getTmpByYear(tmpGapTimesParam);
				List<TmpGapTimesResult> tmpGapTimesResultList = tmpGapBus.compareTmpGaps(tmpGapAvgList, yearsTmpGapAvgList);
				tmpGapTimesResultList = commonStatisticsFilter.filterTmpGapTimesResult(tmpGapTimesResultList);
				return tmpGapTimesResultList;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		return null;
	}
	
	/**
	 * 计算气温年较差
	 * @param para
	 * @return
	 */
	@POST
	@Path("getTmpByYear")
	@Produces("application/json")
	public Object getTmpByYear(@FormParam("para") String para) {
		TmpGapBus tmpGapBus = new TmpGapBus();
		String stationType = "";
		int year;
		JSONObject jsonObject = null;
		Set<String> station_Id_CSet = null;
		try {
			jsonObject = new JSONObject(para);
			stationType = jsonObject.getString("stationType");
			year = jsonObject.getInt("year");
			station_Id_CSet = getStationSets(jsonObject);
			CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
			List<TmpGapAvgYearResult> resultList = tmpGapBus.getTmpByYear(year, stationType);
			resultList = commonStatisticsFilter.filterTmpGapResult(resultList);
			return resultList;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 计算历年年较差
	 * @param para
	 * @return
	 */
	@POST
	@Path("getTmpGapByYears")
	@Produces("application/json")
	public Object getTmpGapByYears(@FormParam("para") String para) {
		TmpGapBus tmpGapBus = new TmpGapBus();
		JSONObject jsonObject = null;
		String station_Id_C = "";
		int startYear, endYear, standardStartYear, standardEndYear;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			standardStartYear = jsonObject.getInt("standardStartYear");
			standardEndYear = jsonObject.getInt("standardEndYear");
			station_Id_C = jsonObject.getString("station_Id_C");
			List<TmpGapByYearsResult> resultList = tmpGapBus.getTmpGapByYears(startYear, endYear, standardStartYear, standardEndYear, station_Id_C);
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
