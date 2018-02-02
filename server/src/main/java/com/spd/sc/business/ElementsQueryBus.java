package com.spd.sc.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonTable;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.sc.pojo.ElementsByTimesParam;
import com.spd.sc.pojo.ElementsByTimesResult;
import com.spd.sc.pojo.ElementsByTimesResultItem;
import com.spd.sc.pojo.ElementsByYearsParam;
import com.spd.sc.pojo.ElementsByYearsResult;
import com.spd.sc.pojo.ElementsByYearsResultItem;
import com.spd.service.ICommon;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;

/**
 * 常规气象要素查询服务
 * @author Administrator
 *
 */
public class ElementsQueryBus {

	/**
	 * 查询指定时间段内的结果序列
	 * @param elementsByTimesParam
	 * @return
	 */
	public ElementsByTimesResult queryElementsByTimes(ElementsByTimesParam elementsByTimesParam) {
		//1. 构造字段
		//1.1 构造一个 "年 + 字段"的查询条件，构造SQL，把不同条件的查询，用union all的方式连接起来。
		LinkedHashMap<Integer, String> itemYearMap = CommonTool.createItemStrByTimes(elementsByTimesParam.getTimesParam());
		//2. 结果类中，再次判断时间序列，满足正确的先后次序
		List<String> sqls = createSQL(elementsByTimesParam, itemYearMap);
		//3. 查询
		List<LinkedHashMap> resultLists = new ArrayList<LinkedHashMap>();
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		for(String sql : sqls) {
			HashMap paramMap = new HashMap();
			paramMap.put("query", sql);
			List<LinkedHashMap> resultList = iCommon.query(paramMap);
			resultLists.addAll(resultList);
		}
		//4. 组装、返回结果。
		ElementsByTimesResult elementsByTimesResult = chg2Result(resultLists, elementsByTimesParam.getTableName());
		return elementsByTimesResult;
	}
	
	public ElementsByYearsResult queryElementsByYears(ElementsByYearsParam elementsByYearsParam) {
		//1. 调用查询
		DBTable dbTable = new DBTable();
		dbTable.queryDataByYears(elementsByYearsParam.getTimesYearsParam(), elementsByYearsParam.getStation_Id_Cs(), elementsByYearsParam.getTableName());
		List<SequenceTimeValue> yearsTemAvgValueList = dbTable.getSequenceTimeValueList();
		//2. 处理结果
		ElementsByYearsResult elementsByYearsResult = dispose(yearsTemAvgValueList, elementsByYearsParam.getTableName());
		return elementsByYearsResult;
	}
	
	private ElementsByYearsResult dispose(List<SequenceTimeValue> yearsTemAvgValueList, String tableName) {
		ElementsByYearsResult elementsByYearsResult = new ElementsByYearsResult();
		List<ElementsByYearsResultItem> resultList = new ArrayList<ElementsByYearsResultItem>();
		if(yearsTemAvgValueList == null || yearsTemAvgValueList.size() == 0) {
			return elementsByYearsResult;
		}
		LinkedHashMap<Integer, List<TimeValue>> dataMap = new LinkedHashMap<Integer, List<TimeValue>>();
		for(int i = 0; i < yearsTemAvgValueList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = yearsTemAvgValueList.get(i);
			int year = sequenceTimeValue.getYear();
			List<TimeValue> currentList = sequenceTimeValue.getTimeValues();
			List<TimeValue> yearList = dataMap.get(year);
			if(yearList != null) {
				yearList.addAll(currentList);
				dataMap.put(year, yearList);
			} else {
				dataMap.put(year, currentList);
			}
		}
		//每年单独处理
		Iterator<Integer> it = dataMap.keySet().iterator();
		while(it.hasNext()) {
			Integer year = it.next();
			List<TimeValue> timeValueList = dataMap.get(year);
			Double resultValue = 0.0, maxResult = -999.0, minResult = 99999.0;
			for(int i = 0; i < timeValueList.size(); i++) {
				TimeValue itemTimeValue = timeValueList.get(i);
				if("t_tem_avg".equals(tableName)) {
					resultValue += itemTimeValue.getValue();
				} else if("t_tem_max".equals(tableName)) {
					Double itemValue = itemTimeValue.getValue();
					if(itemValue > maxResult) {
						maxResult = itemValue;
					}
				} else if("t_tem_min".equals(tableName)) {
					Double itemValue = itemTimeValue.getValue();
					if(itemValue < minResult) {
						minResult = itemValue;
					}
				} else if("t_pre_time_2020".equals(tableName)) {
					resultValue += itemTimeValue.getValue();
				}
			}
			//集中处理
			if("t_tem_avg".equals(tableName) || "t_pre_time_2020".equals(tableName)) {
				resultValue /= timeValueList.size();
				resultValue = CommonTool.roundDouble(resultValue);
			} else if("t_tem_max".equals(tableName)) {
				resultValue = maxResult;
			} else if("t_tem_min".equals(tableName)) {
				resultValue = minResult;
			}
			ElementsByYearsResultItem elementsByYearsResultItem = new ElementsByYearsResultItem();
			elementsByYearsResultItem.setYear(year);
			elementsByYearsResultItem.setValue(resultValue);
			resultList.add(elementsByYearsResultItem);
		}
		Collections.sort(resultList);
		elementsByYearsResult.setResultList(resultList);
		return elementsByYearsResult;
	}
	
	private ElementsByTimesResult chg2Result(List<LinkedHashMap> resultList, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		ElementsByTimesResult elementsByTimesResult = new ElementsByTimesResult();
		List<ElementsByTimesResultItem> elementsByTimesResultItemList = new ArrayList<ElementsByTimesResultItem>();
		if(resultList == null || resultList.size() == 0) {
			return elementsByTimesResult;
		}
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			if(itemMap == null) continue;
			Iterator it = itemMap.keySet().iterator();
			Integer year = (Integer) itemMap.get("year");
			while(it.hasNext()) {
				String key = (String) it.next();
				if(!"year".equals(key)) {
					Object objValue = itemMap.get(key);
					Double value = null;
					if("BigDecimal".equals(columnType) && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) itemMap.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
					
					ElementsByTimesResultItem elementsByTimesResultItem = new ElementsByTimesResultItem();
					elementsByTimesResultItem.setValue(value);
					elementsByTimesResultItem.setDatetime(year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6));
					elementsByTimesResultItemList.add(elementsByTimesResultItem);
				} 
			}
		}
		elementsByTimesResult.setResultList(elementsByTimesResultItemList);
		return elementsByTimesResult;
	}
	
	private List<String> createSQL(ElementsByTimesParam elementsByTimesParam, LinkedHashMap<Integer, String> itemYearMap) {
		List<String> sqls = new ArrayList<String>();
		String tableName = elementsByTimesParam.getTableName();
		Iterator<Integer> it = itemYearMap.keySet().iterator();
		int index = 0; //计数器
		while(it.hasNext()) {
			StringBuffer sql = new StringBuffer();
			Integer year = it.next();
			String items = itemYearMap.get(year);
			items = disposeItems(items, tableName);
			sql.append("select " + items + ", year from " + tableName);
			String[] station_Id_Cs = elementsByTimesParam.getStation_Id_Cs().split(",");
			String stationsStr = "";
			for(int i = 0; i < station_Id_Cs.length; i++) {
				stationsStr += "'" + station_Id_Cs[i] + "'";
				if(i != station_Id_Cs.length - 1) {
					stationsStr += ",";
				}
			}
			sql.append(" where Station_Id_C in (").append(stationsStr).append(") and year = ").append(year);
			sqls.add(sql.toString());
		}
		
		return sqls;
	}
	
	private String disposeItems(String items, String tableName) {
		String result = "";
		String[] item = items.split(",");
		for(int i = 0; i < item.length; i++) {
			if("t_tem_avg".equals(tableName)) {
				item[i] = "avg(" + item[i] + ") as " + item[i];
			} else if("t_tem_max".equals(tableName)) {
				item[i] = "max(" + item[i] + ") as " + item[i];
			} else if("t_tem_min".equals(tableName)) {
				item[i] = "min(" + item[i] + ") as " + item[i];
			} else if("t_pre_time_2020".equals(tableName)) {
				item[i] = "sum(" + item[i] + ") as " + item[i];
			}
			result += item[i];
			if(i != item.length - 1) {
				result += ",";
			}
		}
		return result;
	}
}
