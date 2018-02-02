package com.spd.sc.ws;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.spd.sc.business.SeasonBus;
import com.spd.sc.pojo.PenDiMaxPreParam;
import com.spd.sc.pojo.PenDiMaxPreSeasonYearsParam;
import com.spd.sc.pojo.SeasonByStationYearsParam;
import com.spd.sc.pojo.SeasonByYearParam;
import com.spd.tool.LogTool;

@Stateless
@Path("SeasonService")
public class SeasonService {

	/**
	 * 查询某年的季节
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
			SeasonByYearParam seasonByYearParam = new SeasonByYearParam();
			seasonByYearParam.setYear(year);
			seasonByYearParam.setStartDay(startDay);
			seasonByYearParam.setStartMon(startMon);
			seasonByYearParam.setEndMon(endMon);
			seasonByYearParam.setEndDay(endDay);
			seasonByYearParam.setSeason(season);
			SeasonBus seasonBus = new SeasonBus();
			Object result = seasonBus.getSeasonByYear(seasonByYearParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 根据开始、结束年、站号。查询该站历年每年的季节开始日期，用作前端绘制时序图
	 * @param para
	 * @return
	 */
	@POST
	@Path("getSeasonByStationYears")
	@Produces("application/json")
	public Object getSeasonByStationYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int startMon = 0, startDay = 0, endMon = 0, endDay = 0;
		try {
			jsonObject = new JSONObject(para);
			SeasonByStationYearsParam seasonByStationYearsParam = new SeasonByStationYearsParam();
			String station_Id_C = jsonObject.getString("station_Id_C");
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			String season = jsonObject.getString("season");
			startMon = jsonObject.getInt("startMon");
			startDay = jsonObject.getInt("startDay");
			endMon = jsonObject.getInt("endMon");
			endDay = jsonObject.getInt("endDay");
			seasonByStationYearsParam.setStation_Id_C(station_Id_C);
			seasonByStationYearsParam.setStartYear(startYear);
			seasonByStationYearsParam.setEndYear(endYear);
			seasonByStationYearsParam.setSeason(season);
			seasonByStationYearsParam.setStartDay(startDay);
			seasonByStationYearsParam.setStartMon(startMon);
			seasonByStationYearsParam.setEndDay(endDay);
			seasonByStationYearsParam.setEndMon(endMon);
			SeasonBus seasonBus = new SeasonBus();
			Object result = seasonBus.getSeasonByStationYears(seasonByStationYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 查询盆地大雨开始季
	 * @param para
	 * @return
	 */
	@POST
	@Path("pendiMaxPreSeason")
	@Produces("application/json")
	public Object pendiMaxPreSeason(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			PenDiMaxPreParam penDiMaxPreParam = new PenDiMaxPreParam();
			int year = jsonObject.getInt("year");
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			Double minPre = jsonObject.getDouble("minPre");
			penDiMaxPreParam.setYear(year);
			penDiMaxPreParam.setStation_Id_Cs(station_Id_Cs);
			penDiMaxPreParam.setMinPre(minPre);
			SeasonBus seasonBus = new SeasonBus();
			Object result = seasonBus.pendiMaxPreSeason(penDiMaxPreParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 单站历年大雨开始时间
	 * @param para
	 * @return
	 */
	@POST
	@Path("pendiYearsMaxPreSeason")
	@Produces("application/json")
	public Object pendiYearsMaxPreSeason(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			PenDiMaxPreSeasonYearsParam penDiMaxPreSeasonYearsParam = new PenDiMaxPreSeasonYearsParam();
			String station_Id_C = jsonObject.getString("station_Id_C");
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			penDiMaxPreSeasonYearsParam.setStation_Id_C(station_Id_C);
			penDiMaxPreSeasonYearsParam.setStartYear(startYear);
			penDiMaxPreSeasonYearsParam.setEndYear(endYear);
			SeasonBus seasonBus = new SeasonBus();
			Object result = seasonBus.pendiYearsMaxPreSeason(penDiMaxPreSeasonYearsParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 西南雨季查询计算，根据年份，查询所有的站的开始、结束
	 * @param para
	 * @return
	 */
	@POST
	@Path("southWestRainySeason")
	@Produces("application/json")
	public Object southWestRainySeason(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			int year = jsonObject.getInt("year");
			SeasonBus seasonBus = new SeasonBus();
			Object result = seasonBus.southWestRainySeason(year);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
}
