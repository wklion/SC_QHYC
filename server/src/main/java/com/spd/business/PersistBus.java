package com.spd.business;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.spd.common.PersistParam;
import com.spd.common.PersistRainParam;
import com.spd.common.PersistResult;
import com.spd.common.PersistTmpParam;
import com.spd.common.RainResult;
import com.spd.common.TmpResult;
import com.spd.service.ICommon;
import com.spd.service.IPersist;
import com.spd.tool.CommonTool;
import com.spd.tool.Eigenvalue;
import com.spd.util.CommonUtil;

public class PersistBus {

	/**
	 * 持续统计
	 * @param persistParam
	 * @return
	 */
	public Object persist(PersistParam persistParam) {
		//1. 查询数据库
		IPersist iPersist = (IPersist)ContextLoader.getCurrentWebApplicationContext().getBean("PersistImpl");
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		String stationIdCs = persistParam.getStationIdCs();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		String items = "";
		try {
			items = CommonTool.createItemStrByRange(sdf.parse(persistParam.getStartDateTime()), sdf.parse(persistParam.getEndDateTime()));
		} catch(Exception e) {
			return null;
		}
		HashMap paramMap = new HashMap();
		paramMap.put("tableName", persistParam.getTableName());
		paramMap.put("items", items);
		paramMap.put("startYear", persistParam.getStartYear());
		paramMap.put("endYear", persistParam.getEndYear());
		paramMap.put("stationType", persistParam.getStationType());
		List<Map> resultList = null;
		if("*".equals(stationIdCs)) {
			//查询全部
			resultList = iPersist.persistAll(paramMap);
		} else {
			//查询对应的国家站的区域站
			CommonBus commonBus = new CommonBus();
			String allStations = commonBus.getAllStationsByNationStations(persistParam.getStationIdCs());
			if("ALL".equals(persistParam.getStationType())) {
				paramMap.put("Station_Id_Cs", allStations);
			} else {
				paramMap.put("Station_Id_Cs", persistParam.getStationIdCs());
			}
			resultList = iPersist.persistByStations(paramMap);
		}
		//2. 组装结果
		List<PersistResult> listResult = analyst(persistParam, resultList);
		String stationType = persistParam.getStationType();
		if(null != stationType && !"".equals(stationType)) {
			//过滤
			for(int i = listResult.size() - 1; i >= 0; i--) {
				PersistResult persistResult = listResult.get(i);
				String station_Id_C = persistResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					listResult.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					listResult.remove(i);
				}
			}
		}
		//3. 返回数据
		return listResult;
	}
	
	/**
	 * 气温持续统计
	 * @param persistTmpParam
	 * @return
	 */
	public List<TmpResult> tmp(PersistTmpParam persistTmpParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//1.查询结果
		IPersist iPersist = (IPersist)ContextLoader.getCurrentWebApplicationContext().getBean("PersistImpl");
		String items = "";
		try {
			items = CommonTool.createItemStrByRange(sdf.parse(persistTmpParam.getStartDateTime()), sdf.parse(persistTmpParam.getEndDateTime()));
		} catch(Exception e) {
			return null;
		}
		HashMap paramMap = new HashMap();
		paramMap.put("items", items);
		paramMap.put("startYear", persistTmpParam.getStartYear());
		paramMap.put("endYear", persistTmpParam.getEndYear());
		paramMap.put("stationType", persistTmpParam.getStationType());
		List<Map> resultList = iPersist.tmp(paramMap);
		//2.处理结果
		List<TmpResult> tmpResultList = analyst(persistTmpParam, resultList);
		//3.返回
		String stationType = persistTmpParam.getStationType();
		if(null != stationType && !"".equals(stationType)) {
			//过滤
			for(int i = tmpResultList.size() - 1; i >= 0; i--) {
				TmpResult tmpResult = tmpResultList.get(i);
				String station_Id_C = tmpResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					tmpResultList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					tmpResultList.remove(i);
				}
			}
		}
		return tmpResultList;
	}
	
	/**
	 * 连晴连雨
	 * @param persistRainParam
	 * @return
	 */
	public List<RainResult> rain(PersistRainParam persistRainParam) {
		List<RainResult> resultList = new ArrayList<RainResult>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//1.查询结果
		IPersist iPersist = (IPersist)ContextLoader.getCurrentWebApplicationContext().getBean("PersistImpl");
		String items = "";
		try {
			items = CommonTool.createItemStrByRange(sdf.parse(persistRainParam.getStartDateTime()), sdf.parse(persistRainParam.getEndDateTime()));
		} catch(Exception e) {
			return null;
		}
		HashMap paramMap = new HashMap();
		paramMap.put("items", items);
		paramMap.put("startYear", persistRainParam.getStartYear());
		paramMap.put("endYear", persistRainParam.getEndYear());
		paramMap.put("tableName", persistRainParam.getTableName());
		paramMap.put("stationType", persistRainParam.getStationType());
		List<Map> list = iPersist.rain(paramMap);
		resultList = analyst(persistRainParam, list);
		String stationType = persistRainParam.getStationType();
		if(null != stationType && !"".equals(stationType)) {
			//过滤
			for(int i = resultList.size() - 1; i >= 0; i--) {
				RainResult rainResult = resultList.get(i);
				String station_Id_C = rainResult.getStation_Id_C();
				if("AWS".equals(stationType) && !station_Id_C.startsWith("5")) {
					resultList.remove(i);
				} else if("MWS".equals(stationType) && station_Id_C.startsWith("5")) {
					resultList.remove(i);
				}
			}
		}
		return resultList;
	}
	
	private List<RainResult> analyst(PersistRainParam persistRainParam, List<Map> resultList) {
		List<RainResult> persistResultList = new ArrayList<RainResult>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startDateTime = persistRainParam.getStartDateTime();
		String endDateTime = persistRainParam.getEndDateTime();
		//数据
		Map<String, List> persistDateMap = createDateMap(startDateTime, endDateTime, resultList, persistRainParam.getTableName());
		//站点和站号的对应Map
//		Map<String, String> stationIDNameMap = createStationIDNameMap(resultList);
		Set<String> set = persistDateMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			List dateList = persistDateMap.get(key);
			Date startDate = null;
			String startTime = "", endTime = "";
			//上一个值
			boolean preFlag = false;
			double sumRain = 0;
			int days = 0;
			int index = -1;//记录当前不为0的记录的下标
			for(int i=0; i<dateList.size()-1; i++) {
				String tempStr = (String) dateList.get(i);
				String nextTempStr = (String) dateList.get(i+1);
				double value = Double.parseDouble(tempStr.split("_")[1]);
				double nextValue = Double.parseDouble(nextTempStr.split("_")[1]);
				if(persistRainParam.getChangeType().equals("RAIN")) {
					if(value != 0 && nextValue == 0) {
						index = i;
					} else if(i == dateList.size() - 2 && nextValue > 0) {//考虑到最后一个元素的情况
						index = dateList.size() - 1;
					}
					Date currentDate = null;
					try {
						currentDate = sdf.parse(tempStr.split("_")[0]);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					if(0 == i) {
						startDate = currentDate;
					}
					//从后往前遍历，指导找到开始位置
					RainResult rainResult = new RainResult();
					double rainSum = 0;
					Date preTempDate = null;
					for(int j=index; j>=0; j--) {
						String str = (String) dateList.get(j);
						double rain = Double.parseDouble(str.split("_")[1]);
						rainSum += rain;
						String dateStr = str.split("_")[0];
						Date date = null;
						try {
							date = sdf.parse(dateStr);
						} catch (ParseException e) {
							e.printStackTrace();
							break;
						}
						if(j==index) {
							rainResult.setEndTime(dateStr);
						} 
						if(rain != 0) {
							preTempDate = date;
						}
						if(rain == 0 || j == 0) {
							if(preTempDate == null) {
								continue;
							}
							rainResult.setStartTime(sdf.format(preTempDate));
							rainResult.setDays(index - j);
							rainResult.setRain(CommonTool.roundDouble(rainSum));
							rainResult.setStation_Id_C(key);
							rainResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
							index = 0;
							persistResultList.add(rainResult);
							break;
						}
						
					}
					
				} else if(persistRainParam.getChangeType().equals("SUN")) {
					//晴天
					if(value == 0 && nextValue != 0) {
						index = i;
					} else if(i == dateList.size() - 2 && nextValue == 0) {//考虑到最后一个元素的情况
						index = dateList.size() - 1;
					}
					Date currentDate = null;
					try {
						currentDate = sdf.parse(tempStr.split("_")[0]);
					} catch (ParseException e) {
						e.printStackTrace();
						continue;
					}
					if(0 == i) {
						startDate = currentDate;
					}
					//从后往前遍历，指导找到开始位置
					RainResult rainResult = new RainResult();
					double rainSum = 0;
					Date preTempDate = null;
					for(int j=index; j>=0; j--) {
						String str = (String) dateList.get(j);
						double rain = Double.parseDouble(str.split("_")[1]);
						rainSum += rain;
						String dateStr = str.split("_")[0];
						Date date = null;
						try {
							date = sdf.parse(dateStr);
						} catch (ParseException e) {
							e.printStackTrace();
							break;
						}
						if(j==index) {
							rainResult.setEndTime(dateStr);
						} 
						if(rain == 0) {
							preTempDate = date;
						}
						if(rain != 0) {
							rainResult.setStartTime(sdf.format(preTempDate));
							rainResult.setDays(index - j);
							rainResult.setRain(0);
							rainResult.setStation_Id_C(key);
							rainResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
							index = -1;
							persistResultList.add(rainResult);
							break;
						} else if(j == 0) {
							rainResult.setStartTime(sdf.format(preTempDate));
							rainResult.setDays(index - j + 1);
							rainResult.setRain(0);
							rainResult.setStation_Id_C(key);
							rainResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
							index = -1;
							persistResultList.add(rainResult);
							break;
						}
						
					}
					
				}
			}
		}
		return persistResultList;
	}
	
	private Map<String, List> createDateMap(String startDateTime, String endDateTime, List<Map> resultList, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		Map<String, List> persistDateMap = new HashMap<String, List>();
		Map<String, String> stationIDNameMap = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long start = 0L, end = 0L;
		try {
			start = sdf.parse(startDateTime).getTime();
			end = sdf.parse(endDateTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		// 遍历比较的结果
		for(int i=0; i<resultList.size(); i++) {
			Map map = resultList.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			stationIDNameMap.put(station_Id_C, station_Name);
			List dateArray = persistDateMap.get(station_Id_C);
			if(dateArray == null) {
				dateArray = new ArrayList();
			}
			int year = (Integer)map.get("year");
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			//1. 找出符合条件的全部日期，找的时候，就按从小到大进行排序
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值， 过滤条件，介于开始结束时间
					String currentTimeStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					long current = 0L;
					Date date = null;
					try {
						current = sdf.parse(currentTimeStr).getTime();
						date = new Date(current);
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
//					if(value > CommonConstant.MAXINVALID || value < CommonConstant.MININVALID || value == null) {
//						continue;
//					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
					dateArray.add(currentTimeStr + "_" + value);
				}
			}
			//排序
			dateArray = sortArray(dateArray);
			persistDateMap.put(station_Id_C, dateArray);
		}
		return persistDateMap;
	}
	
//	private Map<String, String> createStationIDNameMap(List<Map> resultList) {
//		Map<String, String> stationIDNameMap = new HashMap<String, String>();
//		for(int i=0; i<resultList.size(); i++) {
//			Map map = resultList.get(i);
//			String station_Id_C = (String) map.get("Station_Id_C");
//			String station_Name = (String) map.get("Station_Name");
//			stationIDNameMap.put(station_Id_C, station_Name);
//		}
//		return stationIDNameMap;
//	}
	private List<TmpResult> analyst(PersistTmpParam persistTmpParam, List<Map> resultList) {
		List<TmpResult> tmpResultList = new ArrayList<TmpResult>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startDateTime = persistTmpParam.getStartDateTime();
		String endDateTime = persistTmpParam.getEndDateTime();
		Map<String, List> persistDateMap = createDateMap(startDateTime, endDateTime, resultList, "t_tem_avg");
//		Map<String, String> stationIDNameMap = createStationIDNameMap(resultList);
		Set<String> set = persistDateMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			List dateList = persistDateMap.get(key);
			int lastDays = 0;
			Date startDate = null, preDate = null;
			double startValue = 0;
			double preValue = 0;
			double scopeValue = 0;//幅度
			boolean flag = false;
			for(int i=0; i<dateList.size(); i++) {
				String tempStr = (String) dateList.get(i);
				double value = Double.parseDouble(tempStr.split("_")[1]);
				Date currentDate = null;
				try {
					currentDate = sdf.parse(tempStr.split("_")[0]);
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				if(0 == i) {
					startDate = currentDate;
					startValue = value;
					preValue = value;
					preDate = currentDate;
					continue;
				} 
				long times = currentDate.getTime() - preDate.getTime();
				if(persistTmpParam.getType().equals("UP")) {
					//升序
					if(value - preValue > 0) {
						scopeValue = value - startValue;
						flag = true;
					} else if(times == 24 * 60 * 60 * 1000 && value - preValue != 0
							&& startDate.getTime() != preDate.getTime()){
						TmpResult tmpResult = new TmpResult();
						tmpResult.setStation_Id_C(key);
						tmpResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
						tmpResult.setStartTime(sdf.format(startDate));
						tmpResult.setEndTime(sdf.format(preDate));
						tmpResult.setScopeValue(CommonTool.roundDouble(scopeValue));
						tmpResult.setDays((preDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
						tmpResultList.add(tmpResult);
						startDate = currentDate;
						startValue = value;
						scopeValue = 0;
					}
				} else if(persistTmpParam.getType().equals("DOWN")) {
					//降序
					if(value - preValue < 0) {
						scopeValue = startValue - value;
						flag = true;
					} else if(times == 24 * 60 * 60 * 1000 && value - preValue != 0 
							&& startDate.getTime() != preDate.getTime()) {
						TmpResult tmpResult = new TmpResult();
						tmpResult.setStation_Id_C(key);
						tmpResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
						tmpResult.setStartTime(sdf.format(startDate));
						tmpResult.setEndTime(sdf.format(preDate));
						tmpResult.setScopeValue(CommonTool.roundDouble(scopeValue));
						tmpResult.setDays((preDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
						tmpResultList.add(tmpResult);
						startDate = currentDate;
						startValue = value;
						scopeValue = 0;
					}
				}
				preValue = value;
				preDate = currentDate;
				if(i == dateList.size() - 1 && scopeValue != 0 
						&& flag && times == 24 * 60 * 60 * 1000
						&& startDate.getTime() != preDate.getTime()) {
					TmpResult tmpResult = new TmpResult();
					tmpResult.setStation_Id_C(key);
					tmpResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
					tmpResult.setStartTime(sdf.format(startDate));
					tmpResult.setEndTime(sdf.format(preDate));
					tmpResult.setScopeValue(CommonTool.roundDouble(scopeValue));
					tmpResult.setDays((preDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
					tmpResultList.add(tmpResult);
				}
			}
		}
		return tmpResultList;
	}
	/**
	 * 处理中间结果
	 * @param resultList
	 * @return
	 */
	private List<PersistResult> analyst(PersistParam persistParam, List<Map> resultList) {
		String columnType = CommonTable.getInstance().getTypeByTableName(persistParam.getTableName());
		List<PersistResult> persistResultList = new ArrayList<PersistResult>();
		// 57513:2015-03-01_0.3,2015-03-02_10.4... key:station_id_c value:date_value
		Map<String, List> persistDateMap = new HashMap<String, List>();
		Map<String, PersistResult> persistResultMap = new HashMap<String, PersistResult>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startDateTime = persistParam.getStartDateTime();
		String endDateTime = persistParam.getEndDateTime();
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
		for(int i=0; i<resultList.size(); i++) {
			Map map = resultList.get(i);
			String station_Id_C = (String) map.get("Station_Id_C");
			String station_Name = (String) map.get("Station_Name");
			List dateArray = persistDateMap.get(station_Id_C);
			if(dateArray == null) {
				dateArray = new ArrayList();
			}
			int year = (Integer)map.get("year");
			// 遍历取值
			Set set = map.keySet();
			Iterator it = set.iterator();
			//1. 找出符合条件的全部日期，找的时候，就按从小到大进行排序
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					//具体值， 过滤条件，介于开始结束时间
					String currentTimeStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					long current = 0L;
					Date date = null;
					try {
						current = sdf.parse(currentTimeStr).getTime();
						date = new Date(current);
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
					filter(persistParam, dateArray, value, date);
				}
			}
			//排序
			dateArray = sortArray(dateArray);
			persistDateMap.put(station_Id_C, dateArray);
		}
		//2. 再找到连续的时间段
		Set<String> set = persistDateMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			PersistResult persistResult = new PersistResult();
			String key = it.next();
			persistResult.setStation_Id_C(key);
			persistResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
			List dateList = persistDateMap.get(key);
			Date tmpDate = null;
			int lastDays = 0;
			if(dateList.size() > 0) {
				String tempStr = (String) dateList.get(0);
				Date currentDate = null;
				try {
					currentDate = sdf.parse(tempStr.split("_")[0]);
					tmpDate = currentDate;
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				double value  = Double.parseDouble(tempStr.split("_")[1]);
				persistResult.setStartTime(sdf.format(tmpDate));
				persistResult.setEndTime(sdf.format(tmpDate));
				persistResult.setDays(1);
				persistResult.setSumValue(value);
			} else {
				continue;
			}
			for(int i=1; i<dateList.size(); i++) {
				String tempStr = (String) dateList.get(i);
				Date currentDate = null;
				try {
					currentDate = sdf.parse(tempStr.split("_")[0]);
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				double value  = Double.parseDouble(tempStr.split("_")[1]);
				if(currentDate.getTime() - tmpDate.getTime() == 24 * 60 * 60 * 1000) {
					//连续的时间
					tmpDate = currentDate;
					persistResult.setEndTime(sdf.format(currentDate));
					persistResult.setDays(persistResult.getDays() + 1);
					persistResult.setSumValue(persistResult.getSumValue() + value);
				} else {
					persistResultList.add(persistResult);
					persistResult = new PersistResult();
					persistResult.setStartTime(sdf.format(currentDate));
					persistResult.setEndTime(sdf.format(currentDate));
					persistResult.setDays(1);
					persistResult.setSumValue(Double.parseDouble(tempStr.split("_")[1]));
					persistResult.setStation_Id_C(key);
					persistResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(key));
					tmpDate = currentDate;
				}
				if(i == dateList.size() - 1) {
					persistResultList.add(persistResult);
				}
			}
		}
		//计算平均
		for(PersistResult persistResult : persistResultList) {
			persistResult.setAvgValue(CommonTool.roundDouble(persistResult.getSumValue() / persistResult.getDays()));
			persistResult.setSumValue(CommonTool.roundDouble(persistResult.getSumValue()));
		}
		return persistResultList;
	}
	
	private void filter(PersistParam persistParam, List dateArray, double value, Date date) {
		String filterTypeName = persistParam.getFilterType();
		FilterTypes filterTypes = FilterTypes.getFilterTypeName(filterTypeName);
		double max = persistParam.getMax();
		double min = persistParam.getMin();
		double contrast = persistParam.getContrast();
		switch(filterTypes) {
		case GET: // >=
			if(value >= contrast) {
				addArray(dateArray, date, value);
			}
			break;
		case GT: // >
			if(value > contrast) {
				addArray(dateArray, date, value);
			}
			break;
		case LET: // <=
			if(value <= contrast) {
				addArray(dateArray, date, value);
			}
			break;
		case LT: // <
			if(value < contrast) {
				addArray(dateArray, date, value);
			}
			break;
		case BETWEEN: // between and
			if(value >= min && value <= max) {
				addArray(dateArray, date, value);
			}
			break;
		case EQUALS:
			if(value == contrast) {
				addArray(dateArray, date, value);
			}
			break;
		default:
			break;
		}
	}
	
	private List sortArray(List dateArray) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List resultArray = new ArrayList();
		int size = dateArray.size();
		int index = 0;
		while(resultArray.size() < size) {
			String firStr = dateArray.get(0) + "";
			try {
				Date firDate = sdf.parse(firStr.split("_")[0]);
				for(int i=0; i<dateArray.size(); i++) {
					String str = dateArray.get(i) + "";
					Date date = sdf.parse(str.split("_")[0]);
					if(date.getTime() < firDate.getTime()) {
						index = i;
						firDate = sdf.parse(str.split("_")[0]);
					}
				}
				resultArray.add(dateArray.get(index));
				dateArray.remove(index);
				index = 0;
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		return resultArray;
	}
	
	private void addArray(List dateArray, Date date, double value) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateArray.add(sdf.format(date) + "_" + value);
	}
}
