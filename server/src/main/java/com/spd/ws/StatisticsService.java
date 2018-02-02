package com.spd.ws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.StatisticsBus;
import com.spd.tool.LogTool;

/**
 * 常规统计分析服务
 * @author Administrator
 *
 */

@Stateless
@Path("StatisticsService")
public class StatisticsService {
	
	private static StatisticsBus statisticsBus = new StatisticsBus();
	/**
	 * 根据时段统计平均气温， 连续时段
	 * @param para 参数形式 {"startTime":"2016-01-01 00:00:00","endTime":"2016-01-31 00:00:00"}
	 * @return
	 */
	@POST
	@Path("queryAvgTemByTimeRange")
	@Produces("application/json")
	public Object queryAvgTemByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryAvgTemByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 统计平均气温, 历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryAvgTemByYears")
	@Produces("application/json")
	public Object queryAvgTemByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryAvgTemByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 高温均值统计，按时间段
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryAvgTemMaxByTimeRange")
	@Produces("application/json")
	public Object queryAvgTemMaxByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryAvgTemMaxByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 高温均值统计，历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryAvgTemMaxByYears")
	@Produces("application/json")
	public Object queryAvgTemMaxByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryAvgTemMaxByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 低温均值统计，按时间段
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryAvgTemMinByTimeRange")
	@Produces("application/json")
	public Object queryAvgTemMinByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryAvgTemMinByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 低温均值统计，历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryAvgTemMinByYears")
	@Produces("application/json")
	public Object queryAvgTemMinByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryAvgTemMinByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 平均风速， 时间段范围
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryWin_s_2mi_avgByTimeRange")
	@Produces("application/json")
	public Object queryWin_s_2mi_avgByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryWin_s_2mi_avgByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	/**
	 * 平均风速，历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryWin_s_2mi_avgByYears")
	@Produces("application/json")
	public Object queryWin_s_2mi_avgByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryWin_s_2mi_avgByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 平均气压， 时间段范围
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryPrsAvgByTimeRange")
	@Produces("application/json")
	public Object queryPrsAvgByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryPrsAvgByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	/**
	 * 平均气压，历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryPrsAvgByYears")
	@Produces("application/json")
	public Object queryPrsAvgByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryPrsAvgByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 按时间段范围统计降水
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryPreSumByTimeRange")
	@Produces("application/json")
	public Object queryPreSumByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		String type = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
			type = jsonObject.getString("type");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryPreSumByTimeRange(startTime, endTime, type, null);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 历年同期，统计降水
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryPreSumByYears")
	@Produces("application/json")
	public Object queryPreSumByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		String type = "";
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
			type = jsonObject.getString("type");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryPreSumByYears(startYear, endYear, startMonth, endMonth, startDay, endDay, type);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 按时间段范围统计日照对数
	 * @param para
	 * @return
	 */
	@POST
	@Path("querySSHByTimeRange")
	@Produces("application/json")
	public Object querySSHByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.querySSHByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 历年同期日照对数统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("querySSHByYears")
	@Produces("application/json")
	public Object querySSHByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.querySSHSumByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 按时间段范围统计相对湿度
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryRHUAvgByTimeRange")
	@Produces("application/json")
	public Object queryRHUAvgByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryRHUAvgByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 历年同期相对湿度统计
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryRHUByYears")
	@Produces("application/json")
	public Object queryRHUByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryRHUByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 按时间段范围统计能见度低值，和出现时间
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryVisMinByTimeRange")
	@Produces("application/json")
	public Object queryVisMinByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryVisMinByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 按年份统计能见度低值，和出现时间
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryVisMinByYears")
	@Produces("application/json")
	public Object queryVisMinByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryVisMinByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 极端高温，按时间段范围
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryExtMaxTmpByTimeRange")
	@Produces("application/json")
	public Object queryExtMaxTmpByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryExtMaxTmpByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 极端高温，历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryExtMaxTmpByYears")
	@Produces("application/json")
	public Object queryExtMaxTmpByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryExtMaxTmpByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	/**
	 * 极端低温，按时间段范围
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryExtMinTmpByTimeRange")
	@Produces("application/json")
	public Object queryExtMinTmpByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryExtMinTmpByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 极端低温，历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryExtMinTmpByYears")
	@Produces("application/json")
	public Object queryExtMinTmpByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryExtMinTmpByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 按时间范围统计降水日数
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryPreCntByTimeRange")
	@Produces("application/json")
	public Object queryPreCntByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryPreCntByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 统计降水日数，历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryPreCntByYears")
	@Produces("application/json")
	public Object queryPreCntByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryPreCntByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
	/**
	 * 按时间范围统计高温日数
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryTmpMaxCntByTimeRange")
	@Produces("application/json")
	public Object queryTmpMaxCntByTimeRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		Date startDate, endDate;
		String startTime, endTime;
		long start, end;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			jsonObject = new JSONObject(para);
			startTime = jsonObject.getString("startTime");
			endTime = jsonObject.getString("endTime");
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
			return "日期转换错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		start = startDate.getTime();
		end = endDate.getTime();
		if(start > end) {
			return "开始时间不能比结束时间大";
		}
		long start1 = System.currentTimeMillis();
		Object result = statisticsBus.queryTmpMaxCntByTimeRange(startTime, endTime);
		long end1 = System.currentTimeMillis();
		System.out.println("花费时间【" + (end1 - start1) + "】");
		return result;
	}
	
	/**
	 * 统计高温日数，历年同期
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryTmpMaxCntByYears")
	@Produces("application/json")
	public Object queryTmpMaxCntByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startYear, endYear, startMonth, endMonth, startDay, endDay; 
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMonth = jsonObject.getInt("startMonth");
			endMonth = jsonObject.getInt("endMonth");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		StatisticsBus statisticsBus = new StatisticsBus();
		long start = System.currentTimeMillis();
		result = statisticsBus.queryTmpMaxCntByYears(startYear, endYear, startMonth, endMonth, startDay, endDay);
		long end = System.currentTimeMillis();
		System.out.println("花费时间：【" + (end - start) + "】");
		return result;
	}
	
}
