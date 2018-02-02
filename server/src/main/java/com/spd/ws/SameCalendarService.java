package com.spd.ws;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.spd.business.SameCaleBus;
import com.spd.common.EleTypes;
import com.spd.common.RankParam;
import com.spd.common.SameCaleParam;
import com.spd.tool.LogTool;

/**
 * 历年同期分析服务
 * @author Administrator
 *
 */
@Stateless
@Path("SameCalendarService")
public class SameCalendarService {

	/**
	 * 站点：Station_ID_C，如果为*则表示查询全部
	 * 要素类型： EleType
	 * 统计方式：桌面系统中叫：期间。StatisticsType
	 * 过滤条件：桌面系统中叫：日值。FilterType
	 * 时段：StartTime，EndTime
	 * 查询的结果年份：startYear, endYear
	 * 气候标准值年份范围：ContrastStartYear, ContrastEndYear
	 * 是否处理缺测日数：MissingRatio
	 * @param para
	 * @return
	 */
	@POST
	@Path("same")
	@Produces("application/json")
	public Object same(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName, filterType = null, statisticsType;
		double min = 0, max = 0, contrast = 0; // 界限值
		int startMon, endMon, startDay, endDay; //时段界限。月，日
		int startYear, endYear, standardStartYear, standardEndYear; //对比年份。
		double missingRatio = 0; // 缺测率
		String station_Id_C = "";
		SameCaleParam sameCaleParam = new SameCaleParam();
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			if(jsonObject.has("FilterType")) {
				filterType = jsonObject.getString("FilterType");
			}
			statisticsType = jsonObject.getString("StatisticsType");
			station_Id_C = jsonObject.getString("station_Id_C");
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			if(jsonObject.has("startMon") && jsonObject.has("endMon") && jsonObject.has("startDay") && jsonObject.has("endDay")) {
				startMon = jsonObject.getInt("startMon");
				endMon = jsonObject.getInt("endMon");
				startDay = jsonObject.getInt("startDay");
				endDay = jsonObject.getInt("endDay");
				sameCaleParam.setStartMon(startMon);
				sameCaleParam.setStartDay(startDay);
				sameCaleParam.setEndMon(endMon);
				sameCaleParam.setEndDay(endDay);
			} else if(jsonObject.has("monthes")) {
				String monthesStr = jsonObject.getString("monthes");
				String[] monthesStrArray = monthesStr.trim().split(",");
				int[] monthes = new int[monthesStrArray.length];
				for(int i = 0; i < monthes.length; i++) {
					monthes[i] = Integer.parseInt(monthesStrArray[i]);
				}
				sameCaleParam.setMonthes(monthes);
			}
			
			standardStartYear = jsonObject.getInt("standardStartYear");
			standardEndYear = jsonObject.getInt("standardEndYear");
			if(jsonObject.has("missingRatio")) {
				missingRatio = jsonObject.getDouble("missingRatio");
			}
			if(filterType != null && !"".equals(filterType)) {
				if(jsonObject.has("min")) {
					min = jsonObject.getDouble("min");
					sameCaleParam.setMin(min);
				}
				if(jsonObject.has("max")) {
					max = jsonObject.getDouble("max");
					sameCaleParam.setMax(max);
				}
				if(jsonObject.has("contrast")) {
					contrast = jsonObject.getDouble("contrast");
					sameCaleParam.setContrast(contrast);
				}
			}
			if(jsonObject.has("resultDisplayType")) {
				Integer resultDisplayType = jsonObject.getInt("resultDisplayType");
				sameCaleParam.setResultDisplayType(resultDisplayType);
			} else {
				//默认按年的方式
				sameCaleParam.setResultDisplayType(1);
			}
			if(jsonObject.has("groupByStation")) {
				//结果是否需要按照站号分组
				sameCaleParam.setGroupByStation(jsonObject.getBoolean("groupByStation"));
			}
			sameCaleParam.setTableName(tableName);
			sameCaleParam.setFilterType(filterType);
			sameCaleParam.setStatisticsType(statisticsType);
			sameCaleParam.setStartYear(startYear);
			sameCaleParam.setEndYear(endYear);
			sameCaleParam.setStandardStartYear(standardStartYear);
			sameCaleParam.setStandardEndYear(standardEndYear);
			sameCaleParam.setMissingRatio(missingRatio);
			sameCaleParam.setStation_ID_C(station_Id_C);
			
			SameCaleBus sameCaleBus = new SameCaleBus();
			Object result = null;
			if(sameCaleParam.isGroupByStation()) {
				result = sameCaleBus.groupByStationSame(sameCaleParam);
			} else {
				result = sameCaleBus.same(sameCaleParam);
			}
			long end = System.currentTimeMillis();
			System.out.println("花费时间【" + (end - start) + "】");
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按照站号分组，进行历年同期对比
	 * @param para
	 * @return
	 */
	@POST
	@Path("sameByStation")
	@Produces("application/json")
	public Object sameByStation(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName, filterType, statisticsType;
		double min = 0, max = 0, contrast = 0; // 界限值
		int startMon, endMon, startDay, endDay; //时段界限。月，日
		int startYear, endYear, standardStartYear, standardEndYear; //对比年份。
		double missingRatio = 0; // 缺测率
		String station_Id_C = "";
		SameCaleParam sameCaleParam = new SameCaleParam();
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			filterType = jsonObject.getString("FilterType");
			statisticsType = jsonObject.getString("StatisticsType");
			station_Id_C = jsonObject.getString("station_Id_C");
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			if(jsonObject.has("startMon") && jsonObject.has("endMon") && jsonObject.has("startDay") && jsonObject.has("endDay")) {
				startMon = jsonObject.getInt("startMon");
				endMon = jsonObject.getInt("endMon");
				startDay = jsonObject.getInt("startDay");
				endDay = jsonObject.getInt("endDay");
				sameCaleParam.setStartMon(startMon);
				sameCaleParam.setStartDay(startDay);
				sameCaleParam.setEndMon(endMon);
				sameCaleParam.setEndDay(endDay);
			} else if(jsonObject.has("monthes")) {
				String monthesStr = jsonObject.getString("monthes");
				String[] monthesStrArray = monthesStr.trim().split(",");
				int[] monthes = new int[monthesStrArray.length];
				for(int i = 0; i < monthes.length; i++) {
					monthes[i] = Integer.parseInt(monthesStrArray[i]);
				}
				sameCaleParam.setMonthes(monthes);
			}
			
			standardStartYear = jsonObject.getInt("standardStartYear");
			standardEndYear = jsonObject.getInt("standardEndYear");
			if(jsonObject.has("missingRatio")) {
				missingRatio = jsonObject.getDouble("missingRatio");
			}
			if(filterType != null && !"".equals(filterType)) {
				if(jsonObject.has("min")) {
					min = jsonObject.getDouble("min");
					sameCaleParam.setMin(min);
				}
				if(jsonObject.has("max")) {
					max = jsonObject.getDouble("max");
					sameCaleParam.setMax(max);
				}
				if(jsonObject.has("contrast")) {
					contrast = jsonObject.getDouble("contrast");
					sameCaleParam.setContrast(contrast);
				}
			}
			if(jsonObject.has("resultDisplayType")) {
				Integer resultDisplayType = jsonObject.getInt("resultDisplayType");
				sameCaleParam.setResultDisplayType(resultDisplayType);
			} else {
				//默认按年的方式
				sameCaleParam.setResultDisplayType(1);
			}
			sameCaleParam.setTableName(tableName);
			sameCaleParam.setFilterType(filterType);
			sameCaleParam.setStatisticsType(statisticsType);
			sameCaleParam.setStartYear(startYear);
			sameCaleParam.setEndYear(endYear);
			sameCaleParam.setStandardStartYear(standardStartYear);
			sameCaleParam.setStandardEndYear(standardEndYear);
			sameCaleParam.setMissingRatio(missingRatio);
			sameCaleParam.setStation_ID_C(station_Id_C);
			
			SameCaleBus sameCaleBus = new SameCaleBus();
			Object result = sameCaleBus.sameByStation(sameCaleParam);
			long end = System.currentTimeMillis();
			System.out.println("花费时间【" + (end - start) + "】");
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 按照站号分组，用于线性趋势分析
	 * @param para
	 * @return
	 */
	@POST
	@Path("linearByStation")
	@Produces("application/json")
	public Object linearByStation(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName, filterType, statisticsType;
		double min = 0, max = 0, contrast = 0; // 界限值
		int startMon, endMon, startDay, endDay; //时段界限。月，日
		int startYear, endYear, standardStartYear, standardEndYear; //对比年份。
		double missingRatio = 0; // 缺测率
		String station_Id_C = "";
		SameCaleParam sameCaleParam = new SameCaleParam();
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			filterType = jsonObject.getString("FilterType");
			statisticsType = jsonObject.getString("StatisticsType");
			station_Id_C = jsonObject.getString("station_Id_C");
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			if(jsonObject.has("startMon") && jsonObject.has("endMon") && jsonObject.has("startDay") && jsonObject.has("endDay")) {
				startMon = jsonObject.getInt("startMon");
				endMon = jsonObject.getInt("endMon");
				startDay = jsonObject.getInt("startDay");
				endDay = jsonObject.getInt("endDay");
				sameCaleParam.setStartMon(startMon);
				sameCaleParam.setStartDay(startDay);
				sameCaleParam.setEndMon(endMon);
				sameCaleParam.setEndDay(endDay);
			} else if(jsonObject.has("monthes")) {
				String monthesStr = jsonObject.getString("monthes");
				String[] monthesStrArray = monthesStr.trim().split(",");
				int[] monthes = new int[monthesStrArray.length];
				for(int i = 0; i < monthes.length; i++) {
					monthes[i] = Integer.parseInt(monthesStrArray[i]);
				}
				sameCaleParam.setMonthes(monthes);
			}
			
			standardStartYear = jsonObject.getInt("standardStartYear");
			standardEndYear = jsonObject.getInt("standardEndYear");
			if(jsonObject.has("missingRatio")) {
				missingRatio = jsonObject.getDouble("missingRatio");
			}
			if(filterType != null && !"".equals(filterType)) {
				if(jsonObject.has("min")) {
					min = jsonObject.getDouble("min");
					sameCaleParam.setMin(min);
				}
				if(jsonObject.has("max")) {
					max = jsonObject.getDouble("max");
					sameCaleParam.setMax(max);
				}
				if(jsonObject.has("contrast")) {
					contrast = jsonObject.getDouble("contrast");
					sameCaleParam.setContrast(contrast);
				}
			}
			if(jsonObject.has("resultDisplayType")) {
				Integer resultDisplayType = jsonObject.getInt("resultDisplayType");
				sameCaleParam.setResultDisplayType(resultDisplayType);
			} else {
				//默认按年的方式
				sameCaleParam.setResultDisplayType(1);
			}
			sameCaleParam.setTableName(tableName);
			sameCaleParam.setFilterType(filterType);
			sameCaleParam.setStatisticsType(statisticsType);
			sameCaleParam.setStartYear(startYear);
			sameCaleParam.setEndYear(endYear);
			sameCaleParam.setStandardStartYear(standardStartYear);
			sameCaleParam.setStandardEndYear(standardEndYear);
			sameCaleParam.setMissingRatio(missingRatio);
			sameCaleParam.setStation_ID_C(station_Id_C);
			
			SameCaleBus sameCaleBus = new SameCaleBus();
			Object result = sameCaleBus.linearByStation(sameCaleParam);
			long end = System.currentTimeMillis();
			System.out.println("花费时间【" + (end - start) + "】");
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 站点：Station_ID_C，如果为*则表示查询全部
	 * 要素类型： EleType
	 * 统计方式：桌面系统中叫：期间。StatisticsType
	 * 过滤条件：桌面系统中叫：日值。FilterType
	 * 时段：StartTime，EndTime
	 * 查询的结果年份：startYear, endYear
	 * 气候标准值年份范围：ContrastStartYear, ContrastEndYear
	 * 是否处理缺测日数：MissingRatio
	 * @param para
	 * @return
	 */
	@POST
	@Path("sameBatch")
	@Produces("application/json")
	public Object sameBatch(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName, filterType, statisticsType;
		double min = 0, max = 0, contrast = 0; // 界限值
		int startMon, endMon, startDay, endDay; //时段界限。月，日
		int startYear, endYear, standardStartYear, standardEndYear; //对比年份。
		double missingRatio = 0; // 缺测率
		String station_Id_Cs = "";
		SameCaleParam sameCaleParam = new SameCaleParam();
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			filterType = jsonObject.getString("FilterType");
			statisticsType = jsonObject.getString("StatisticsType");
			station_Id_Cs = jsonObject.getString("station_Id_C");
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMon = jsonObject.getInt("startMon");
			endMon = jsonObject.getInt("endMon");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
			standardStartYear = jsonObject.getInt("standardStartYear");
			standardEndYear = jsonObject.getInt("standardEndYear");
			if(jsonObject.has("missingRatio")) {
				missingRatio = jsonObject.getDouble("missingRatio");
			}
			if(filterType != null && !"".equals(filterType)) {
				if(jsonObject.has("min")) {
					min = jsonObject.getDouble("min");
					sameCaleParam.setMin(min);
				}
				if(jsonObject.has("max")) {
					max = jsonObject.getDouble("max");
					sameCaleParam.setMax(max);
				}
				if(jsonObject.has("contrast")) {
					contrast = jsonObject.getDouble("contrast");
					sameCaleParam.setContrast(contrast);
				}
			}
			if(jsonObject.has("resultDisplayType")) {
				Integer resultDisplayType = jsonObject.getInt("resultDisplayType");
				sameCaleParam.setResultDisplayType(resultDisplayType);
			} else {
				//默认按年的方式
				sameCaleParam.setResultDisplayType(1);
			}
			sameCaleParam.setTableName(tableName);
			sameCaleParam.setFilterType(filterType);
			sameCaleParam.setStatisticsType(statisticsType);
			sameCaleParam.setStartYear(startYear);
			sameCaleParam.setStartMon(startMon);
			sameCaleParam.setStartDay(startDay);
			sameCaleParam.setEndYear(endYear);
			sameCaleParam.setEndMon(endMon);
			sameCaleParam.setEndDay(endDay);
			sameCaleParam.setStandardStartYear(standardStartYear);
			sameCaleParam.setStandardEndYear(standardEndYear);
			sameCaleParam.setMissingRatio(missingRatio);
			
			ArrayList<Object> result = new ArrayList<Object>();
			String[] strs = station_Id_Cs.split(",");
			for(int i=0; i<strs.length; i++){
				String station_Id_C = strs[i];
				sameCaleParam.setStation_ID_C(station_Id_C);
				
				SameCaleBus sameCaleBus = new SameCaleBus();
				Object resultItem = sameCaleBus.same(sameCaleParam);
				result.add(resultItem);
				long end = System.currentTimeMillis();
				System.out.println("花费时间【" + (end - start) + "】");
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
