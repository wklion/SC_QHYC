package com.spd.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.RankBus;
import com.spd.common.EleTypes;
import com.spd.common.ExtResult;
import com.spd.common.RankParam;
import com.spd.pojo.RankResult;
import com.spd.tool.LogTool;

/**
 * 位次分析
 * @author Administrator
 *
 */
@Stateless
@Path("RankServices")
public class RankServices {
	
	/**
	 * 位次分析
	 * @param para 
	 * 要素类型： EleType
	 * 统计方式：桌面系统中叫：期间。StatisticsType
	 * 过滤条件：桌面系统中叫：日值。FilterType
	 * 时段：StartTime，EndTime
	 * 查询的结果年份：Year
	 * 对比年份范围：ContrastStartYear, ContrastEndYear
	 * 排序方式：High, Low
	 * 是否处理并列位次：Tie
	 * 是否处理缺测日数：MissingRatio
	 * @return
	 */
	@POST
	@Path("rank")
	@Produces("application/json")
	public Object rank(@FormParam("para") String para) {
		long start = System.currentTimeMillis();
		JSONObject jsonObject = null;
		String tableName, filterType, statisticsType;
		double min = 0, max = 0, contrast = 0; // 界限值
		int startMon, endMon, startDay, endDay; //时段界限。月，日
		int startYear, endYear, currentYear; //对比年份。
		String sortType; // 排位方式
		boolean tie = false; // 并列位次 
		double missingRatio = 0; // 缺测率
		RankParam rankParam = new RankParam();
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			filterType = jsonObject.getString("FilterType");
			statisticsType = jsonObject.getString("StatisticsType");
			if(filterType != null && !"".equals(filterType)) {
				if(jsonObject.has("min")) {
					min = jsonObject.getDouble("min");
					rankParam.setMin(min);
				}
				if(jsonObject.has("max")) {
					max = jsonObject.getDouble("max");
					rankParam.setMax(max);
				}
				if(jsonObject.has("contrast")) {
					contrast = jsonObject.getDouble("contrast");
					rankParam.setContrast(contrast);
				}
			}
			startMon = jsonObject.getInt("startMon");
			endMon = jsonObject.getInt("endMon");
			startDay = jsonObject.getInt("startDay");
			endDay = jsonObject.getInt("endDay");
			startYear = jsonObject.getInt("startYear");
			endYear = jsonObject.getInt("endYear");
			currentYear = jsonObject.getInt("currentYear");
			sortType = jsonObject.getString("sortType");
			if(jsonObject.has("Tie")) {
				tie = jsonObject.getBoolean("Tie");
			}
			if(jsonObject.has("MissingRatio")) {
				missingRatio = jsonObject.getDouble("MissingRatio");
			}
			RankBus rankBus = new RankBus();
			// 构造参数
			rankParam.setCurrentYear(currentYear);
			rankParam.setEndDay(endDay);
			rankParam.setEndMon(endMon);
			rankParam.setEndYear(endYear);
			rankParam.setFilterType(filterType);
			rankParam.setMax(max);
			rankParam.setMin(min);
			rankParam.setMissingRatio(missingRatio);
			rankParam.setStartDay(startDay);
			rankParam.setStartMon(startMon);
			rankParam.setStartYear(startYear);
			rankParam.setStatisticsType(statisticsType);
			rankParam.setTableName(tableName);
			rankParam.setTie(tie);
			rankParam.setSortType(sortType);
			boolean hasStationType = jsonObject.has("stationType");
			if(hasStationType) {
				String stationType = jsonObject.getString("stationType");
				rankParam.setStationType(stationType);
			}
			
			Object result = rankBus.rank(rankParam);
			long end = System.currentTimeMillis();
			System.out.println("位次分析花费时间【" + (end - start) + "】");
			List<RankResult> result2 = filter(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	private List<RankResult> filter(Object result, JSONObject jsonObject) {
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
			List<RankResult> list = (List<RankResult>) result;
			List<RankResult> list2 = new ArrayList<RankResult>();
			//过滤
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				for(int j = 0; j < list.size(); j++) {
					RankResult item = list.get(j);
					String itemStation_Id_C = item.getStation_Id_C();
					if(station_Id_C.equals(itemStation_Id_C)) {
						list2.add(item);
						break;
					}
				}
			}
			//重新排序
			for(int i = 0; i < list2.size(); i++) {
				RankResult rankResult = list2.get(i);
				rankResult.setIndex(i + 1);
			}
			return list2;
		} else {
			return (List<RankResult>) result;
		}
	}
}
