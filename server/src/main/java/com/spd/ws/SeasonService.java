package com.spd.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.SeasonBus;
import com.spd.common.SeasonResult;
import com.spd.common.SeasonYearsResult;
import com.spd.pojo.RankResult;
import com.spd.tool.LogTool;

/**
 * 计算站对应的季节，历史上的。
 * 
 * @author Administrator
 *
 */
@Stateless
@Path("SeasonService")
public class SeasonService {

	/**
	 *  计算历史上的季节
	 * @param para
	 * @return
	 */
	@POST
	@Path("getHisSeason")
	@Produces("application/json")
	public Object getHisSeason(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String season = "";
		int startMon = 0, startDay = 0, endMon = 0, endDay = 0;
		try {
			jsonObject = new JSONObject(para);
			season = jsonObject.getString("season");
			startMon = jsonObject.getInt("startMon");
			startDay = jsonObject.getInt("startDay");
			endMon = jsonObject.getInt("endMon");
			endDay = jsonObject.getInt("endDay");
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		SeasonBus seasonBus = new SeasonBus();
		Object result = seasonBus.getHisSeason(season, startMon, startDay, endMon, endDay);
		return result;
	}
	
	/**
	 * 计算某一年的季节
	 * @param para
	 * @return
	 */
	@POST
	@Path("getSeasonByYear")
	@Produces("application/json")
	public Object getSeasonByYear(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int year = 0;
		String season = "";
		int startMon = 0, startDay = 0, endMon = 0, endDay = 0;
		try {
			jsonObject = new JSONObject(para);
			year = jsonObject.getInt("year");
			startMon = jsonObject.getInt("startMon");
			startDay = jsonObject.getInt("startDay");
			endMon = jsonObject.getInt("endMon");
			endDay = jsonObject.getInt("endDay");
			season = jsonObject.getString("season");
			SeasonBus seasonBus = new SeasonBus();
			//计算对应的下一个季节的日期，以及是什么季节
			String[] nextParam = seasonBus.calcNextSeason(year, season);
			List<SeasonResult> start = seasonBus.getSeasonByYear(year, season, startMon, startDay, endMon, endDay, "5%");
			List<SeasonResult> start2 = filter(start, jsonObject);
			
			List<SeasonResult> end = seasonBus.getSeasonByYear(Integer.parseInt(nextParam[5]), nextParam[0],
					Integer.parseInt(nextParam[1]), Integer.parseInt(nextParam[3]), Integer.parseInt(nextParam[2]), Integer.parseInt(nextParam[4]), "5%");
			List<SeasonResult> end2 = filter(end, jsonObject);
			seasonBus.calcPersistDays(start2, end2);
			return start2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		
	}
	
	/**
	 * 查询历年单站同期的季节
	 * @param para
	 * @return
	 */
	@POST
	@Path("getSeasonByStationAndYears")
	@Produces("application/json")
	public Object getSeasonByStationAndYears(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		int startYear = 0, endYear = 0;
		String season = "", station_Id_C = "";
		int startMon = 0, startDay = 0, endMon = 0, endDay = 0;
		List<SeasonYearsResult>  seasonYearsResultStartList = new ArrayList<SeasonYearsResult>();
		try {
			jsonObject = new JSONObject(para);
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			startMon = jsonObject.getInt("startMon");
			startDay = jsonObject.getInt("startDay");
			endMon = jsonObject.getInt("endMon");
			endDay = jsonObject.getInt("endDay");
			season = jsonObject.getString("season");
			station_Id_C = jsonObject.getString("station_Id_C");
			//多次调用每年的查询
			for(int i = startYear; i <= endYear; i++) {
				SeasonYearsResult seasonYearsResult = getSingleStation(station_Id_C, i, startMon, startDay, endMon, endDay, season);
				if(seasonYearsResult != null) {
					seasonYearsResultStartList.add(seasonYearsResult);
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("花费时间：【" + (end - start) + "】");
			return seasonYearsResultStartList;
			//TODO 暂时这么处理,因为历年中,没有超过12月才入冬的.
//			if("WINTER".equals(season)) {
//				endMon = 12;
//				endDay = 31;
//			}
//			SeasonBus seasonBus = new SeasonBus();
//			List<SeasonYearsResult>  seasonYearsResultStartList = seasonBus.getSeasonByStationAndYears(season, startMon, startDay, endMon, endDay, startYear, endYear, station_Id_C);
//			//计算对应的下一个季节的日期，以及是什么季节
//			String[] nextParam1 = seasonBus.calcNextSeason(startYear, season);
//			List<SeasonYearsResult>  seasonYearsResultEndList = seasonBus.getSeasonByStationAndYears(nextParam1[0], 
//					Integer.parseInt(nextParam1[1]), Integer.parseInt(nextParam1[2]), Integer.parseInt(nextParam1[3]), 
//					Integer.parseInt(nextParam1[4]), startYear, endYear, station_Id_C);
//			seasonBus.calcPersistDays2(seasonYearsResultStartList, seasonYearsResultEndList);
//			filterYears(seasonYearsResultStartList, startYear, endYear);
//			return seasonYearsResultStartList;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 查询单站，单年的季节
	 * @param station_Id_C
	 * @param year
	 * @param startMon
	 * @param startDay
	 * @param endMon
	 * @param endDay
	 * @param season
	 * @return
	 */
	private SeasonYearsResult getSingleStation(String station_Id_C, int year, int startMon, int startDay, int endMon, int endDay, String season) {
		SeasonBus seasonBus = new SeasonBus();
		String[] nextParam = seasonBus.calcNextSeason(year, season);
		List<SeasonResult> start = seasonBus.getSeasonByYear(year, season, startMon, startDay, endMon, endDay, station_Id_C);
		List<SeasonResult> end = seasonBus.getSeasonByYear(Integer.parseInt(nextParam[5]), nextParam[0],
				Integer.parseInt(nextParam[1]), Integer.parseInt(nextParam[3]), Integer.parseInt(nextParam[2]), Integer.parseInt(nextParam[4]), station_Id_C);
		seasonBus.calcPersistDays(start, end);
		//把start对象转换成为SeasonYearsResult对象
		if(start == null || start.size() == 0) return null;
		SeasonResult itemSeasonResult = start.get(0);
		SeasonYearsResult seasonYearsResult = new SeasonYearsResult();
		seasonYearsResult.setStation_Id_C(itemSeasonResult.getStation_Id_C());
		seasonYearsResult.setStation_Name(itemSeasonResult.getStation_Name());
		seasonYearsResult.setAnomaly(itemSeasonResult.getAnomaly());
		seasonYearsResult.setArea(itemSeasonResult.getArea());
		seasonYearsResult.setDescription(itemSeasonResult.getDescription());
		seasonYearsResult.setHisStartDate(itemSeasonResult.getHisStartDate());
		seasonYearsResult.setPersistDays(itemSeasonResult.getPersistDays());
		seasonYearsResult.setStartDate(itemSeasonResult.getStartDate());
		seasonYearsResult.setYear(year);
		return seasonYearsResult;
	}
	
	/**
	 * 处理历年同期的统计结果,按年份重新排序,删除掉多余的
	 * @param seasonYearsResultStartList
	 */
	private void filterYears(List<SeasonYearsResult>  seasonYearsResultStartList, int startYear, int endYear) {
		//1. 删除掉不在年份内的
		for(int i = seasonYearsResultStartList.size() - 1; i > 0; i--) {
			SeasonYearsResult item = seasonYearsResultStartList.get(i);
			int year = item.getYear();
			if(year > endYear || year < startYear) {
				seasonYearsResultStartList.remove(i);
			}
		}
		//2. 排序
		Collections.sort(seasonYearsResultStartList, new Comparator<SeasonYearsResult>(){
			public int compare(SeasonYearsResult o1, SeasonYearsResult o2) {
				int year1 = o1.getYear();
				int year2 = o2.getYear();
				return year1 - year2;
			}
		});
	}
	
	private List<SeasonResult> filter(Object result, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
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
			List<SeasonResult> list = (List<SeasonResult>) result;
			List<SeasonResult> list2 = new ArrayList<SeasonResult>();
			//过滤
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < list.size(); j++) {
					SeasonResult item = list.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						list2.add(item);
						break;
					}
				}
			}
			return list2;
		} else {
			return (List<SeasonResult>) result;
		}
	}
}
