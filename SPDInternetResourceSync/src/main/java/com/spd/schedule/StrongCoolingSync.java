package com.spd.schedule;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.spd.dao.cq.impl.StrongCoolingStationDaoImpl;
import com.spd.dao.cq.impl.T_tem_avgDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 强降温
 * @author Administrator
 *
 */
public class StrongCoolingSync {

	private static int[] MONTHS = new int[]{1, 2, 3, 4, 10, 11, 12};
	
	private T_tem_avgDaoImpl tem_avgDaoImpl = new T_tem_avgDaoImpl();
	//冬
	private static int[] WINTERS = new int[]{12, 1, 2};
	//春秋
	private static int[] SPRINGS = new int[]{3, 4, 10, 11};
	//强降温冬季标准
	private static double TMPWINDERLEVEL1 = 6;
	//强降温春季标准
	private static double TMPSPRINGLEVEL1 = 8;
	//特强降温冬季标准
	private static double TMPWINDERLEVEL2 = 8;
	//特强降温春季标准
	private static double TMPSPRINGLEVEL2 = 10;
	
	public void sync(String datetime) {
		//1. 判断是否在指定的月份里面
		boolean isInTime = isInTime(datetime);
		if(!isInTime) return;
		//2. 找到所有的降温的日站序列
		List<LinkedHashMap> listDataMap = new ArrayList<LinkedHashMap>();
		getAllDatas(datetime, listDataMap);
		//3. 过滤掉开始部分不满足条件的
		List<LinkedHashMap> listDataMap2 = filter(listDataMap);
		//3. 在dataMap中根据条件，查找符合条件的强降温序列
		getStrongCoolingDatas(listDataMap2, datetime);
	}
	
	/**
	 * 过滤早期不满足的
	 * @param listDataMap
	 */
	private List<LinkedHashMap> filter(List<LinkedHashMap> listDataMap) {
		List<LinkedHashMap> resultMapList = new ArrayList<LinkedHashMap>();
		for(int i = 0; i < listDataMap.size(); i++) {
			LinkedHashMap itemMap = listDataMap.get(i);
			if(itemMap.size() <= 5) {
				resultMapList.add(itemMap);
				continue;
			}
			Double[] valueArray = new Double[itemMap.size() - 1];
			String[] keyArray = new String[itemMap.size() - 1];
			Iterator it = itemMap.keySet().iterator();
			int index = 0;
			int month = 0;
			while(it.hasNext()) {
				String key = (String) it.next();
				if(!"Station_Id_C".equals(key)) {
					Double value = (Double) itemMap.get(key);
					valueArray[index] = value;
					keyArray[index++] = key;
					month = Integer.parseInt(key.substring(5, 7));
				} 
			}
			int startIndex = 0;
			for(int j = keyArray.length - 1; j >= 3; j--) {
				Double value1 = valueArray[j];
				Double value2 = valueArray[j - 3];
				boolean isInRange = isInRange(value1, value2, month);
				if(isInRange) {
					startIndex = j;
					break;
				}
			}
			LinkedHashMap itemResultMap = new LinkedHashMap();
			itemResultMap.put("Station_Id_C", itemMap.get("Station_Id_C"));
			for(int j = 0; j <= startIndex; j++) {
				itemResultMap.put(keyArray[j], valueArray[j]);
			}
			resultMapList.add(itemResultMap);
		}
		return resultMapList;
	}
	
	/**
	 * 判断是否满足强降温
	 * @param value1
	 * @param value2
	 * @param month
	 * @return
	 */
	private boolean isInRange(Double value1, Double value2, int month) {
		BigDecimal bigValue1 = new BigDecimal(value1 + "");
		BigDecimal bigValue2 = new BigDecimal(value2 + "");
		for(int i = 0; i < WINTERS.length; i++) {
			if(month == WINTERS[i]) {
				if(bigValue1.subtract(bigValue2).doubleValue() >= TMPWINDERLEVEL1) {
					return true;
				}
			}
		}
		for(int i = 0; i < SPRINGS.length; i++) {
			if(month == SPRINGS[i]) {
				if(bigValue1.subtract(bigValue2).doubleValue() >= TMPSPRINGLEVEL1) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void getStrongCoolingDatas(List<LinkedHashMap> listDataMap, String datetime) {
		HashMap resultMap = new HashMap();
		StrongCoolingStationDaoImpl strongCoolingStationDaoImpl = new StrongCoolingStationDaoImpl();
		List insertDataList = new ArrayList();
		for(int i = 0; i < listDataMap.size(); i++) {
			LinkedHashMap dataMap = listDataMap.get(i);
			Iterator it = dataMap.keySet().iterator();
			String station_Id_C = "";
			while(it.hasNext()) {
				String key = (String) it.next();
				if("Station_Id_C".equals(key)) {
					station_Id_C = (String) dataMap.get("Station_Id_C");
				} 
			}
			//数据，1. 取到所有的72小时内的降水，然后找到最大的 2. 找到降温总量
			Double[] subTmp = getCoolData(dataMap);
			if(subTmp == null) continue;
			//判断是否满足强降温条件
			String level = getLevel(subTmp[1], datetime);
			if(level != null) {
				//取到结果，存在Map中，后续入库
				HashMap insertMap = new HashMap();
				insertMap.put("Station_Id_C", station_Id_C);
				insertMap.put("CoolTmp", CommonTool.roundDouble(subTmp[0]));
				insertMap.put("Cool72HTmp", CommonTool.roundDouble(subTmp[1]));
				insertMap.put("level", level);
				String[] times = getTimeRange(dataMap);
				insertMap.put("StartTime", times[0] + " 00:00:00");
				insertMap.put("EndTime", times[1] + " 00:00:00");
				insertDataList.add(insertMap);
			}
		}
		strongCoolingStationDaoImpl.insert(insertDataList, datetime);
	}
	
	/**
	 * 判断是否满足强降温条件
	 * @param subTmp
	 * @param datetime
	 * @return
	 */
	private String getLevel(Double subTmp, String datetime) {
		String result = null;
		int month = Integer.parseInt(datetime.substring(5, 7));
		String type = "";
		for(int i = 0; i < SPRINGS.length; i++) {
			if(SPRINGS[i] == month) {
				type = "SPRING";
				break;
			}
		}
		for(int i = 0; i < WINTERS.length; i++) {
			if(WINTERS[i] == month) {
				type = "WINTER";
				break;
			}
		}
		//判断是否强降温
		if("SPRING".equals(type)) {
			if(subTmp >= TMPSPRINGLEVEL1 && subTmp < TMPSPRINGLEVEL2) {
				//春季强降温
				result = "强降温";
			} else if (subTmp >= TMPSPRINGLEVEL2) {
				result = "特强降温";
			}
		} else if("WINTER".equals(type)) {
			if(subTmp >= TMPWINDERLEVEL1 && subTmp < TMPWINDERLEVEL2) {
				//春季强降温
				result = "强降温";
			} else if (subTmp >= TMPWINDERLEVEL2) {
				result = "特强降温";
			}
		}
		return result;
	}
	
	/**
	 * 找到过程的开始时间、结束时间
	 * @param dataMap
	 * @return
	 */
	private String[] getTimeRange(HashMap dataMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Iterator it = dataMap.keySet().iterator();
		long startTime = Long.MAX_VALUE, endTime = Long.MIN_VALUE;
		while(it.hasNext()) {
			String key = (String) it.next();
			if(!key.equals("Station_Id_C")) {
				try {
					Date date = sdf.parse(key);
					long time = date.getTime();
					if(time < startTime) {
						startTime = time;
					}
					if(time > endTime) {
						endTime = time;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return new String[]{sdf.format(new Date(startTime)), sdf.format(new Date(endTime))};
	}
	
	/**
	 * 找到过程中总体降水，最大降水幅度
	 * @param dataMap
	 * @return
	 */
	private Double[] getCoolData(HashMap dataMap) {
		Double[] resultDouble = new Double[2];
		if(dataMap.size() <= 2) return null;
		//1. 存在数组中，然后排序（降序）
		Double[] dataArray = new Double[dataMap.size() - 1];
		Iterator it = dataMap.keySet().iterator();
		int i = 0;
		while(it.hasNext()) {
			String key = (String) it.next();
			if(!"Station_Id_C".equals(key)) {
				Double value = (Double) dataMap.get(key);
				dataArray[i++] = value;
			}
		}
		//排序
		for(int j = 0; j < dataArray.length - 1; j++) {
			Double valueJ = dataArray[j];
			int index = 0;
			for(int k = j + 1; k < dataArray.length; k++) {
				Double valueK = dataArray[k];
				if(valueJ < valueK) {
					index = k;
				}
			}
			//交换j和index
			if(index != 0) {
				Double temp = valueJ;
				Double temp2 = dataArray[index];
				dataArray[index] = temp;
				dataArray[j] = temp2;
			}
		}
		//计算全部的72小时内的降温，并且找到最大的
		Double totalSubTmp = 0.0, maxSubTmp = 0.0;
		totalSubTmp = dataArray[0] - dataArray[dataArray.length - 1];
		//全部24小时降温
		for(int j = 0; j < dataArray.length - 1; j++){
			Double j1 = dataArray[j];
			Double j2 = dataArray[j + 1];
			BigDecimal bj1 = new BigDecimal(j1 + "");
			BigDecimal bj2 = new BigDecimal(j2 + "");
			Double tempMaxSubTmp = bj1.subtract(bj2).doubleValue();
			if(tempMaxSubTmp > maxSubTmp) {
				maxSubTmp = tempMaxSubTmp;
			}
		}
		//全部48小时降温
		if(dataArray.length >= 3) {
			for(int j = 0; j < dataArray.length - 2; j++){
				Double j1 = dataArray[j];
				Double j2 = dataArray[j + 2];
				BigDecimal bj1 = new BigDecimal(j1 + "");
				BigDecimal bj2 = new BigDecimal(j2 + "");
				Double tempMaxSubTmp = bj1.subtract(bj2).doubleValue();
				if(tempMaxSubTmp > maxSubTmp) {
					maxSubTmp = tempMaxSubTmp;
				}
			}
		}
		//全部72小时降温
		if(dataArray.length >= 4) {
			for(int j = 0; j < dataArray.length - 3; j++){
				Double j1 = dataArray[j];
				Double j2 = dataArray[j + 3];
				BigDecimal bj1 = new BigDecimal(j1 + "");
				BigDecimal bj2 = new BigDecimal(j2 + "");
				Double tempMaxSubTmp = bj1.subtract(bj2).doubleValue();
				if(tempMaxSubTmp > maxSubTmp) {
					maxSubTmp = tempMaxSubTmp;
				}
			}
		}
		resultDouble[0] = totalSubTmp;
		resultDouble[1] = maxSubTmp;
		return resultDouble;
	}
	
	private boolean getAllDatas(String datetime, List<LinkedHashMap> listDataMap) {
		//只要有满足条件的，就一直往前找，直到找不到为止
		boolean returnFlag = false;
		while(true) {
			HashMap<String, Double> itemMap = tem_avgDaoImpl.getTemByTime(datetime);
			if(itemMap == null) return true;
			if(listDataMap.size() == 0) {
				//第一次，不用判断条件
				Iterator it = itemMap.keySet().iterator();
				while(it.hasNext()) {
					LinkedHashMap tmpMap = new LinkedHashMap();
					String station_Id_C = (String) it.next();
					tmpMap.put("Station_Id_C", station_Id_C);
					tmpMap.put(datetime, itemMap.get(station_Id_C));
					listDataMap.add(tmpMap);
				}
				String preDatetime = CommonTool.addDays(datetime, -1);
				boolean flag = getAllDatas(preDatetime, listDataMap);
				if(flag) {
					return true;
				}
			} else {
				boolean flag = true;//满足有降温条件
				String forDatetime = CommonTool.addDays(datetime, 1);
				Iterator it = itemMap.keySet().iterator();
				while(it.hasNext()) {
					String key = (String) it.next();
					Double value = itemMap.get(key);
//					String preTime = CommonTool.addDays(key, -1);
					for(int i = 0; i < listDataMap.size(); i++) {
						HashMap tmpMap = listDataMap.get(i);
						String itemStation_Id_C = (String) tmpMap.get("Station_Id_C");
						if(key.equals(itemStation_Id_C)) {
							Double preValue = (Double) tmpMap.get(forDatetime);
							if(preValue != null && value != null && value < 999 && value >= preValue) {
								//满足降温条件
								tmpMap.put(datetime, value);
								flag = false;
							}
							break;
						}
					}
				}
				if(!flag) {
					String preDatetime = CommonTool.addDays(datetime, -1);
					boolean f = getAllDatas(preDatetime, listDataMap);
					if(f) {
						return true;
					}
				} else {
					return true;
				}
			}
		}
//		if(!returnFlag) {
//			String preDatetime = CommonTool.addDays(datetime, -1);
//			getAllDatas(preDatetime, listDataMap);
//		}
	}
	/**
	 * 判断是否在指定的月份里面
	 * @param datetime 
	 * @return
	 */
	private boolean isInTime(String datetime) {
		String monthStr = datetime.substring(5, 7);
		int month = Integer.parseInt(monthStr);
		for(int i = 0; i < MONTHS.length; i++) {
			if(month == MONTHS[i]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		StrongCoolingSync strongCoolingSync = new StrongCoolingSync();
		//测试开始
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = "1955-01-14";
		String endTime = "2017-01-09";
		try {
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
				String time = sdf.format(new Date(i));
				System.out.println(time);
				strongCoolingSync.sync(time);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//测试结束
//		String datetime = "1962-02-13";
//		strongCoolingSync.sync(datetime);
	}

}
