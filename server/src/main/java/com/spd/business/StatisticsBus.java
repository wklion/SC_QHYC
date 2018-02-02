package com.spd.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.Station;
import com.spd.common.TmpDaysYearParam;
import com.spd.common.TmpDaysYearResult;
import com.spd.service.ICommon;
import com.spd.service.IStatistics;
import com.spd.tool.CommonTool;
import com.spd.tool.LogTool;

/**
 * 中间层，连接服务的Service层，和后台数据查询的dao层。实现数据的组装，处理等。
 * @author Administrator
 *
 */

public class StatisticsBus {

	
	/**
	 * 统计历年同期平均气温均值
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryAvgTemByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryAvgTemByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgTmpMulYearDis(list);
		return result;
	}
	
	/**
	 * 根据时段统计平均气温
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object queryAvgTemByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryAvgTemByYears(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgTmpDis(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 高温均值统计，按时间段
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryAvgTemMaxByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryAvgTemMaxByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgTmpMaxDis(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 低温均值统计，按时间段
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryAvgTemMinByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryAvgTemMinByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgTmpMinDis(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 高温均值统计，历年同期
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryAvgTemMaxByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryAvgTemMaxByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgTmpMaxMulYearDis(list);
		return result;
	}
	
	/**
	 * 低温均值统计，历年同期
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryAvgTemMinByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryAvgTemMinByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgTmpMinMulYearDis(list);
		return result;
	}
	
	/**
	 * 平均风速，历年同期
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryWin_s_2mi_avgByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryWin_s_2mi_avgByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgWin_s_2mi_avgMulYearDis(list);
		return result;
	}
	
	/**
	 * 平均风速，按时间段
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryWin_s_2mi_avgByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryWin_s_2mi_avgByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgWin_s_2mi_avgDis(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 平均气压，按时间段
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryPrsAvgByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryPrsAvgByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgPrsAvgDis(list, startDate, endDate);
		return result;
	}
	
	
	
	/**
	 * 平均气压，按年同期统计
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryPrsAvgByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryPrsAvgByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.avgPrsAvgYearsDis(list);
		return result;
	}
	
	/**
	 * 按时间段范围统计降水
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryPreSumByTimeRange(String startTime, String endTime, String type, String stationType) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("stationType", stationType);
		if("0808".equals(type)) {
			paramMap.put("tableName", "t_pre_time_0808");
		} else if("2020".equals(type)) {
			paramMap.put("tableName", "t_pre_time_2020");
		} else if("0820".equals(type)) {
			paramMap.put("tableName", "t_pre_time_0820");
		} else if("2008".equals(type)) {
			paramMap.put("tableName", "t_pre_time_2008");
		}
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryPreSumByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.preAvgByTimeRange(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 降水总量，历年同期
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryPreSumByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay, String type) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		if("0808".equals(type)) {
			paramMap.put("tableName", "t_pre_time_0808");
		} else if("2020".equals(type)) {
			paramMap.put("tableName", "t_pre_time_2020");
		} else if("0820".equals(type)) {
			paramMap.put("tableName", "t_pre_time_0820");
		} else if("2008".equals(type)) {
			paramMap.put("tableName", "t_pre_time_2008");
		}
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryPreSumByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		//总共包含的年份数
		int years = endYear - startYear + 1;
		Object result = resultDataDispose.avgPreTimeAvgYearsDis(list, years);
		return result;
	}
	
	/**
	 * 按时间范围统计日照对数
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 */
	public Object querySSHByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.querySSHByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.disSSHByTimeRange(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 按时间范围统计相对湿度
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 */
	public Object queryRHUAvgByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryRHUAvgByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.disRHUAvgByTimeRange(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 按时间段范围统计能见度低值，和出现时间
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryVisMinByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryVisMinByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.disVisMinByTimeRange(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 极端高温，按时间段范围
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryExtMaxTmpByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryExtMaxTmpByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.disExtMaxTmpByTimeRange(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 极端低温，按时间段范围
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryExtMinTmpByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryExtMinTmpByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.disExtMinTmpByTimeRange(list, startDate, endDate);
		return result;
	}
	
	/**
	 * 根据时间范围统计降水日数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object queryPreCntByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryPreCntByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		return resultDataDispose.queryPreCntByTimeRange(list, startDate, endDate);
	}
	
	/**
	 * 根据时间范围统计高温日数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object queryTmpMaxCntByTimeRange(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return null;
		}
		int startYear = Integer.parseInt(startTime.substring(0, 4));
		int endYear = Integer.parseInt(endTime.substring(0, 4));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		String columns = createItemStrByRange(startDate, endDate);
		paramMap.put("items", columns);
		List<Map> list = statistics.queryAvgTemMaxByTimeRange(paramMap);
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		return resultDataDispose.queryTmpMaxCntByTimeRange(list, startDate, endDate);
	}
	
	/**
	 * 统计降水日数， 历年同期
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryPreCntByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryPreCntByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.queryPreCntByYears(list, (endYear - startYear + 1));
		return result;
	}
	
	/**
	 * 统计高温日数， 历年同期
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryTmpMaxCntByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		// 都是查询同一个表。
		List<Map> list = statistics.queryAvgTemMaxByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.queryTmpMaxCntByYears(list, (endYear - startYear + 1));
		return result;
	}
	
	/**
	 * 按年份统计能见度低值，和出现时间
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Object queryVisMinByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryVisMinByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.disVisMinByYears(list);
		return result;
	}
	
	/**
	 * 极端高温，历年同期
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryExtMaxTmpByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryExtMaxTmpByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.disExtMaxTmpByYears(list);
		return result;
	}
	
	/**
	 * 极端低温，历年同期
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryExtMinTmpByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryExtMinTmpByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		Object result = resultDataDispose.disExtMinTmpByYears(list);
		return result;
	}
	
	/**
	 * 日照对数，历年同期统计
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @param type
	 * @return
	 */
	public Object querySSHSumByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.querySSHSumByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		//总共包含的年份数
		int years = endYear - startYear + 1;
		Object result = resultDataDispose.avgSSHAvgYearsDis(list, years);
		return result;
	}
	
	/**
	 * 历年同期相对湿度
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	public Object queryRHUByYears(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay) {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", startYear);
		paramMap.put("endYear", endYear);
		paramMap.put("items", createItemStrByTimes(startYear, endYear, startMonth, endMonth, startDay, endDay));
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		List<Map> list = statistics.queryRHUByYears(paramMap);
		//组装数据的逻辑
		ResultDataDispose resultDataDispose = new ResultDataDispose();
		//总共包含的年份数
		int years = endYear - startYear + 1;
		Object result = resultDataDispose.avgRHUAvgYearsDis(list, years);
		return result;
	}
	
	/**
	 * 计算年度空调度日、采暖度日
	 * @param tmpDaysYearParam
	 * @return
	 */
	public List<TmpDaysYearResult> queryTmpDaysByYear(TmpDaysYearParam tmpDaysYearParam) {
		List<TmpDaysYearResult> tmpDaysYearResultList = new ArrayList<TmpDaysYearResult>();
		//1. 查询每一天的平均气温
		IStatistics statistics = (IStatistics)ContextLoader.getCurrentWebApplicationContext().getBean("StatisticsImpl");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startYear", tmpDaysYearParam.getStartYear());
		paramMap.put("endYear", tmpDaysYearParam.getEndYear());
		paramMap.put("station_Id_C", tmpDaysYearParam.getStation_Id_C());
		Double baseTmp = tmpDaysYearParam.getTmp();
		List<Map> list = statistics.queryTmpDaysByYear(paramMap);
		//2. 统计分析结果
		for(int i = 0; i < list.size(); i++) {
			TmpDaysYearResult tmpDaysYearResult = new TmpDaysYearResult();
			Map itemMap = list.get(i);
			int year = (Integer) itemMap.get("year");
			tmpDaysYearResult.setYear(year);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			tmpDaysYearResult.setStation_Id_C(station_Id_C);
			List<Double> resultList = new ArrayList<Double>();
			Iterator it = itemMap.keySet().iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					BigDecimal bigDecimal = (BigDecimal) itemMap.get(key);
					Double value = bigDecimal.doubleValue();
					if(value != null) {
						if("HEAT".equals(tmpDaysYearParam.getType())) {
							//采暖
							if(value <= baseTmp) {
								resultList.add(value);
							}
						} else if("COOL".equals(tmpDaysYearParam.getType())) {
							//空调
							if(value >= baseTmp) {
								resultList.add(value);
							}
						}
					}
				}
			}
			tmpDaysYearResult.setResultList(resultList);
			tmpDaysYearResult.setType(tmpDaysYearParam.getType());
			tmpDaysYearResultList.add(tmpDaysYearResult);
		}
		return tmpDaysYearResultList;
	}
	
	/**
	 * 连续日期查询字段构造
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private String createItemStrByRange(Date startDate, Date endDate) {
		SimpleDateFormat sdfMon = new SimpleDateFormat("MM");
		SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
		Set<String> columns = new HashSet<String>();
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		StringBuffer result = new StringBuffer();
		for(long time=startTime; time<=endTime; time += 24 * 60 * 60 * 1000) {
			columns.add("m" + sdfMon.format(time) + "d" + sdfDay.format(time));
		}
		Iterator it = columns.iterator();
		while(it.hasNext()) {
			result.append(it.next()).append(",");
		}
		return result.toString().substring(0, result.length() - 1);
	}
	
	/**
	 * 多年历史同期的构造查询字段
	 * @param startYear
	 * @param endYear
	 * @param startMonth
	 * @param endMonth
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	private String createItemStrByTimes(int startYear, int endYear, int startMonth, int endMonth, int startDay, int endDay){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfMon = new SimpleDateFormat("MM");
		SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
		Set<String> columns = new HashSet<String>();
		StringBuffer result = new StringBuffer();
		for(int i = startYear; i <= endYear; i++) {
			boolean isOverYear = CommonTool.isOverYear(startMonth, startDay, endMonth, endDay);
			String start = "", end = "";
			if(isOverYear) {
				start = i + "-" + String.format("%02d", startMonth) + "-" + String.format("%02d", startDay) + " 00:00:00";
				end = i + 1 + "-" + String.format("%02d", endMonth) + "-" + String.format("%02d", endDay) + " 00:00:00";
			} else {
				start = i + "-" + String.format("%02d", startMonth) + "-" + String.format("%02d", startDay) + " 00:00:00";
				end = i + "-" + String.format("%02d", endMonth) + "-" + String.format("%02d", endDay) + " 00:00:00";
			}
			
			long startTime = 0L, endTime = 0L;
			try {
				startTime = sdf.parse(start).getTime();
				endTime = sdf.parse(end).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			for(long time = startTime; time <= endTime; time += 24 * 60 * 60 * 1000) {
				columns.add("m" + sdfMon.format(time) + "d" + sdfDay.format(time));
			}
		}
		Iterator it = columns.iterator();
		while(it.hasNext()) {
			result.append(it.next()).append(",");
		}
		return result.toString().substring(0, result.length() - 1);
	}
}
