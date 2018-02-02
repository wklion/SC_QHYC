package com.spd.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.CommonTable;
import com.spd.common.DaysParam;
import com.spd.common.DaysResult;
import com.spd.common.ExtHisResult;
import com.spd.common.FilterTypes;
import com.spd.service.IDays;
import com.spd.service.IRank;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;
import com.spd.util.CommonUtil;

/**
 * 日数统计处理
 * @author Administrator
 *
 */
public class DaysBus {

	private CommonUtil commonUtil = CommonUtil.getInstance();

	/**
	 * 日数统计
	 * @param daysParam
	 * @return
	 */
	public List<DaysResult> daysAnaly(DaysParam daysParam) {
		// 1. 查询数据库
		IDays iDays = (IDays)ContextLoader.getCurrentWebApplicationContext().getBean("DaysImpl");
		HashMap paramMap = new HashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		String items = "";
		try {
			items = CommonTool.createItemStrByRange(sdf.parse(daysParam.getStartDateTime()), sdf.parse(daysParam.getEndDateTime()));
		} catch(Exception e) {
			return null;
		}
		
		paramMap.put("tableName", daysParam.getTableName());
		paramMap.put("items", items);
		paramMap.put("startYear", daysParam.getStartYear());
		paramMap.put("endYear", daysParam.getEndYear());
		paramMap.put("StationType", daysParam.getStationType());
		List<Map> statisticsListResult = iDays.statisticsDays(paramMap);
		//判断是否跨年
		boolean isOverYear = CommonTool.isOverYear(daysParam.getStartMon(), daysParam.getStartDay(), daysParam.getEndMon(), daysParam.getEndDay());
		HashMap hisParam = new HashMap();
		hisParam.put("tableName", daysParam.getTableName());
		hisParam.put("items", items);
		if(isOverYear) {
			hisParam.put("startYear", daysParam.getConstantStartYear() - 1);
		} else {
			hisParam.put("startYear", daysParam.getConstantStartYear());
		}
		hisParam.put("endYear", daysParam.getConstantEndYear());
		List<Map> statisticsHisListResult = iDays.statisticsHisDays(hisParam);
		// 2. 处理结果
		List<DaysResult> daysResultList = disposeResult(statisticsListResult, statisticsHisListResult, daysParam, isOverYear);
		//3. 添加上地区
		int index = 1;
		for(DaysResult daysResult : daysResultList) {
			daysResult.setArea(commonUtil.stationAreaMap.get(daysResult.getStation_Id_C()));
			daysResult.setStation_Name(commonUtil.stationNameMap.get(daysResult.getStation_Id_C()));
			daysResult.setIndex(index++);
		}
		String stationType = daysParam.getStationType();
		if(null != stationType && !"".equals(stationType)) {
			//过滤
			for(int i = daysResultList.size() - 1; i >= 0; i--) {
				DaysResult daysResult = daysResultList.get(i);
				String station_Id_C = daysResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					daysResultList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					daysResultList.remove(i);
				}
			}
		}
		//从大到小排序
		Collections.sort(daysResultList, new DaysResult());
		//重置索引
		for(int i = 0; i < daysResultList.size(); i++) {
			DaysResult daysResult = daysResultList.get(i);
			daysResult.setIndex(i + 1);
		}
		return daysResultList;
	}
	
	/**
	 * 处理结果
	 * @param statisticsListResult
	 * @return
	 */
	private List<DaysResult> disposeResult(List<Map> statisticsListResult, List<Map> statisticsHisListResult, DaysParam daysParam, boolean isOverYear) {
		List<DaysResult> daysResultList = new ArrayList<DaysResult>();
		Map<String, DaysResult> resultMap = new HashMap<String, DaysResult>();
		String startDateTime = daysParam.getStartDateTime();
		String endDateTime = daysParam.getEndDateTime();
		String tableName = daysParam.getTableName();
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long start = 0L, end = 0L;
		try {
			start = sdf.parse(startDateTime).getTime();
			end = sdf.parse(endDateTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
//		int index = 1;
		// 遍历比较的结果
		for(int i=0; i<statisticsListResult.size(); i++) {
			Map map = statisticsListResult.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			DaysResult daysResult = resultMap.get(station_Id_C);
			if(daysResult == null) {
				daysResult = new DaysResult();
//				daysResult.setIndex(index++);
				daysResult.setStation_Id_C(station_Id_C);
				daysResult.setStation_Name(station_Name);
			}
			int year = (Integer)map.get("year");
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值， 过滤条件，介于开始结束时间
					String currentTimeStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					long current = 0L;
					try {
						current = sdf.parse(currentTimeStr).getTime();
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					if(current < start || current > end) {
						continue;
					}
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					filter(daysParam, daysResult, value);
				}
			}
			resultMap.put(station_Id_C, daysResult);
		}
		//遍历历史结果
		Map<String, DaysResult> daysHisResultMap = analystHis(statisticsHisListResult, daysParam, isOverYear);
		//组合结果
		Set<String> set = resultMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			DaysResult daysResult = resultMap.get(key);
			DaysResult daysHisResult = daysHisResultMap.get(key);
			daysResult.setHisAvgDays(daysHisResult.getDays());
			daysResult.setAnomaly(CommonTool.roundDouble(daysResult.getDays() - daysHisResult.getDays()));
			daysResultList.add(daysResult);
		}
		return daysResultList;
	}
	
	private Map<String, DaysResult> analystHis(List<Map> statisticsHisListResult, DaysParam daysParam, boolean isOverYear) {
		//保存全部年份的总和
		String tableName = daysParam.getTableName();
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		Map<String, DaysResult> hisMap = new HashMap<String, DaysResult>();
		Map<String, Integer> validYearCntMap = new HashMap<String, Integer>();
		int index = 0;
		for(int i=0; i<statisticsHisListResult.size(); i++) {
			Map map = statisticsHisListResult.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			DaysResult daysResult = hisMap.get(station_Id_C);
			if(daysResult == null){
				daysResult = new DaysResult();
			}
			daysResult.setIndex(index++);
			daysResult.setStation_Id_C(station_Id_C);
			daysResult.setStation_Name(station_Name);
			int year = (Integer) map.get("year");
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			// 判断一年的数据是否全部无效
			boolean isAllInvalid  = true;
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//如果跨年的话，把历年结果中第一年的01-01到结束的月-日对应的数据
					String monStr = key.substring(1, 3);
					String dayStr = key.substring(4, 6);
					boolean isCurTimeOverYear = CommonTool.isCurTimeOverYear(Integer.parseInt(monStr), Integer.parseInt(dayStr), 1,
							daysParam.getStartMon(), 1, daysParam.getStartDay());
					if(isOverYear && year == daysParam.getConstantStartYear() - 1 && isCurTimeOverYear) {
						continue;
					}
					//具体值
					Double value = null;
					Object objValue = map.get(key);
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					isAllInvalid = false;
					filter(daysParam, daysResult, value);
				}
			}
			if(!isAllInvalid) {
				Integer validYearCnt  = validYearCntMap.get(station_Id_C);
				if(validYearCnt == null) {
					validYearCntMap.put(station_Id_C, 1);
				} else {
					validYearCntMap.put(station_Id_C, validYearCnt + 1);
				}
			}
			hisMap.put(station_Id_C, daysResult);
		}
		Set<String> set = hisMap.keySet();
		Iterator it = set.iterator();
		while(it.hasNext()) {
			String station_Id_C = (String) it.next();
			DaysResult daysResult = hisMap.get(station_Id_C);
			Integer validYearCnt = validYearCntMap.get(station_Id_C);
			if(validYearCnt != null) {
				daysResult.setDays(CommonTool.roundDouble(daysResult.getDays() / validYearCnt));
			}
		}
		//TODO 暂时不考虑跨年的情况。
//		int startYear = daysParam.getStartYear();
//		int endYear = daysParam.getStartYear();
//		if(endYear > startYear) {
//			//跨年
//		} else {
//			//不跨年
//		}
		return hisMap;
	}
	
	
	private void filter(DaysParam daysParam, DaysResult daysResult, double value) {
		String filterTypeName = daysParam.getFilterType();
		FilterTypes filterTypes = FilterTypes.getFilterTypeName(filterTypeName);
		double max = daysParam.getMax();
		double min = daysParam.getMin();
		double contrast = daysParam.getContrast();
		switch(filterTypes) {
		case GET: // >=
			if(value >= contrast) {
				daysResult.setDays(daysResult.getDays() + 1);
			}
			break;
		case GT: // >
			if(value > contrast) {
				daysResult.setDays(daysResult.getDays() + 1);
			}
			break;
		case LET: // <=
			if(value <= contrast) {
				daysResult.setDays(daysResult.getDays() + 1);
			}
			break;
		case LT: // <
			if(value < contrast) {
				daysResult.setDays(daysResult.getDays() + 1);
			}
			break;
		case BETWEEN: // between and
			if(value >= min && value <= max) {
				daysResult.setDays(daysResult.getDays() + 1);
			}
			break;
		case EQUALS:
			if(value == contrast) {
				daysResult.setDays(daysResult.getDays() + 1);
			}
			break;
		default:
			break;
		}
	}
}
