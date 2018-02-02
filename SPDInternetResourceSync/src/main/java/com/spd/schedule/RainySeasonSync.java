package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.cq.impl.HouPreResultDaoImpl;
import com.spd.dao.cq.impl.T_pre_time_2020DaoImpl;
import com.spd.dao.sc.impl.RainySeasonDaoImpl;
import com.spd.pojo.HuaDong;
import com.spd.pojo.HuaDongItem;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;

/**
 * 西南雨季数据同步
 * @author Administrator
 *
 */
public class RainySeasonSync {

	//雨季开始的开始日期
	private static int SEASONSTARTSTARTTIME = 425;
	//雨季开始的结束日期
	private static int SEASONSTARTENDTIME = 1001;
	//雨季开始的开始日期
	private static int SEASONENDSTARTTIME = 1001;
	//雨季开始的结束日期
	private static int SEASONENDENDTIME = 1231;
	//滑动序列常数
	private static int HUADONGSIZE = 5;
	//第一次满足条件和第二次的最短日差
	private static int DAYSMINCOUNT = 15;
	
	private RainySeasonDaoImpl rainySeasonDaoImpl = new RainySeasonDaoImpl();

	private HouPreResultDaoImpl houPreResultDaoImpl = new HouPreResultDaoImpl();
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		RainySeasonSync rainySeasonSync = new RainySeasonSync();
		int start = 1981;
		int end = 2017;
		for(int i = start; i <= end; i++) {
			System.out.println(i);
			rainySeasonSync.syncStart(i + "-09-30");
			rainySeasonSync.syncEnd(i + "-12-31");
		}
	}
	
	/**
	 * 计算雨季结束
	 * @param datetime
	 */
	public void syncEnd(String datetime) {
		//1. 10-01到12-31号期间范围
		int year = Integer.parseInt(datetime.split("-")[0]);
		boolean end = isEnd(datetime);
		if(!end) return;
		//2. 查询10-01到当前时间的降水序列
		String startTime = datetime.substring(0, 4) + "-10-01";
		String itemsStr = CommonTool.createItemStrByRangeDate(startTime, datetime);
		HashMap<String, PreResult> yearsPreMap = getPreList(year, itemsStr);
		//3. 计算滑动平均
		List<HuaDong> huaDongList = chgHuaDong(yearsPreMap, itemsStr, year, startTime);
		//4. 查询历史全年的候平均雨量
		HashMap<String, Double> yearsHourPreMap = houPreResultDaoImpl.getYearsData(datetime, "0112");
		//5. 计算比历年同期平均小的，记录下所有的位置，依次查找，找到两个之间，差距在15天以内的，第一个就是正确的结果
		List<RainySeason> rainySeasonList = caleEnd(yearsHourPreMap, huaDongList, yearsPreMap, startTime);
		//6. 查询已经有的结果，计算降水总量
		calePre(rainySeasonList, year);
		//7. 结果update
		List<String> updateSQLList = update(rainySeasonList);
		rainySeasonDaoImpl.updateData(updateSQLList);
	}
	
	public List<String> update(List<RainySeason> rainySeasonList) {
		List<String> updateSQLList = new ArrayList<String>();
		for(RainySeason rainySeason : rainySeasonList) {
			String sql = "update t_rainyseason set EndTime = '" + rainySeason.getEndTime() + "', PersistDays = " +
					rainySeason.getPersistDays()
			+ ", PreSum = " + rainySeason.getPreSum() + " where id = " + rainySeason.getId() + " and EndTime is null";
			updateSQLList.add(sql);
		}
		return updateSQLList;
	}
	
	public void calePre(List<RainySeason> rainySeasonList, int year) {
		List existList = rainySeasonDaoImpl.getDataByYear(year);
		//1. 计算好开始、结束时间
		for(RainySeason rainySeason : rainySeasonList) {
			String station_Id_C = rainySeason.getStation_Id_C();
			for(int i = 0; i < existList.size(); i++) {
				HashMap dataMap = (HashMap) existList.get(i);
				String startTime = (String) dataMap.get("StartTime");
				String itemStation_Id_C = (String) dataMap.get("Station_Id_C");
				if(station_Id_C.equals(itemStation_Id_C)) {
					rainySeason.setStartTime(startTime);
					int id = (Integer) dataMap.get("id");
					rainySeason.setId(id);
					rainySeason.setPersistDays(CommonTool.caleDays(startTime, rainySeason.getEndTime()));
					break;
				}
			}
		}
		//2. 计算雨量序列
		String itemsStr = CommonTool.createItemStrByRangeDate(year + "-04-25", year + "-12-31");
		String[] items = itemsStr.split(",");
		List preList = getPre(year, itemsStr);
		//3. 根据开始、结束时间、计算雨量总和。
		for(RainySeason rainySeason : rainySeasonList) {
			String station_Id_C = rainySeason.getStation_Id_C();
			for(int i = 0; i < preList.size(); i++) {
				HashMap preMap = (HashMap) preList.get(i);
				String itemStation_Id_C = (String) preMap.get("Station_Id_C");
				if(station_Id_C.equals(itemStation_Id_C)) {
					Double sum = 0.0;
					for(int j = 0; j < items.length; j++) {
						Object preObj = preMap.get(items[j]);
						if(preObj != null) {
							Double pre = (Double) preObj;
							if(pre > 9999 || pre < -999) {
								continue;
							} else {
								sum += pre;
							}
						}
					}
					rainySeason.setPreSum(sum);
					break;
				}
			}
		}
	}
	/**
	 * 计算雨季开始
	 * @param datetime
	 */
	public void syncStart(String datetime) {
		int year = Integer.parseInt(datetime.split("-")[0]);
		//1. 04-21 到10-01为开始期
		boolean start = isStart(datetime);
		if(!start) return;
		//2. 查询4.21到现在为止的按站的降水序列
		String startTime = datetime.substring(0, 4) + "-04-21";
		String itemsStr = CommonTool.createItemStrByRangeDate(startTime, datetime);
		HashMap<String, PreResult> yearsPreMap = getPreList(year, itemsStr);
		Set<String> existStartSet = getExistStart(year);
		//2. 如果数据库中已经存在结果，则直接跳过
		removeRepeat(yearsPreMap, existStartSet);
		//3. 查询历史的5-10月候雨量
		HashMap<String, Double> yearsHourPreMap = houPreResultDaoImpl.getYearsData(datetime, "0510");
		//3. 计算对应的滑动平均
		List<HuaDong> huaDongList = chgHuaDong(yearsPreMap, itemsStr, year, startTime);
		//4. 计算比历年滑动平均大的值，然后把所有序列位置记录下来，依次查找，找到两个之间，差距在15天以内的，第一个就是正确的结果
		List<RainySeason> rainySeasonList = caleStart(yearsHourPreMap, huaDongList, yearsPreMap, startTime);
		//7. 结果入库
		addReainSeason(rainySeasonList);
	}
	
	private void addReainSeason(List<RainySeason> rainySeasonList) {
		List dataList = new ArrayList();
		for(int i = 0; i < rainySeasonList.size(); i++) {
			RainySeason rainySeason = rainySeasonList.get(i);
			Map dataMap = new HashMap();
			dataMap.put("Station_Id_C", rainySeason.getStation_Id_C());
			dataMap.put("StartTime", rainySeason.getStartTime() + " 00:00:00");
			dataMap.put("year", rainySeason.getYear());
			dataList.add(dataMap);
		}
		rainySeasonDaoImpl.insertFogValue(dataList);
	}
	
	/**
	 * 
	 * @param yearsHourPreMap 历年候平均
	 * @param huaDongList 滑动降水序列
	 * @param yearsPreMap 原始降水序列
	 * @param startTime 开始时间
	 * @return
	 */
	private List<RainySeason> caleEnd(HashMap<String, Double> yearsHourPreMap, List<HuaDong> huaDongList, HashMap<String, PreResult> yearsPreMap, String startTime) {
		//1. 定义一个Map，key:station，value:为>历年平均候雨量的位置。57516:1,5,7...
		int year = Integer.parseInt(startTime.substring(0, 4));
		List<RainySeason> rainySeasonList = new ArrayList<RainySeason>();
		Map<String, String> houLTMap = new HashMap<String, String>(); //满足小于于的条件
		for(int i = 0; i < huaDongList.size(); i++) {
			HuaDong huaDong = huaDongList.get(i);
			String station_Id_C = huaDong.getStation_Id_C();
			List<HuaDongItem> itemList = huaDong.getHuaDongItemList();
			Double yearsHourPre = yearsHourPreMap.get(station_Id_C);
			if(yearsHourPre == null) {
				continue;
			}
			for(int j = 0; j < itemList.size(); j++) {
				HuaDongItem item = itemList.get(j);
				Double huaDongValue = item.getValue();
				if(huaDongValue <= yearsHourPre) {
					String houStr = houLTMap.get(station_Id_C);
					if(houStr == null) {
						houLTMap.put(station_Id_C, j + "");
					} else {
						houLTMap.put(station_Id_C, houStr + "," + j);
					}
				}
			}
		}
		//2. 遍历Map，找到两个位次相隔15以内的，找到对应的原始序列
		
		Iterator<String> houIt = houLTMap.keySet().iterator();
		while(houIt.hasNext()) {
			String key = houIt.next();
			String indexStr = houLTMap.get(key);
			String[] indexs = indexStr.split(",");
			if(indexs.length >= 2) {
				for(int i = 0; i < indexs.length - 1; i++) {
					boolean flag = false;
					for(int j = i + 1; j < indexs.length; j++) {
						int startIndex = Integer.parseInt(indexs[i]);
						int endIndex = Integer.parseInt(indexs[j]);
						// 如果后续15天,是从滑动的后一个序号开始的话,就是这个判断,如果是要滑动5天之后,则要多一个判断
						if(endIndex - startIndex < DAYSMINCOUNT) {
//						if(endIndex - startIndex < DAYSMINCOUNT && endIndex - startIndex >= HUADONGSIZE) {
							RainySeason rainySeason = new RainySeason();
							String endDatetime = caleStartTime(startTime, startIndex);
							rainySeason.setEndTime(endDatetime);
							rainySeason.setStation_Id_C(key);
							rainySeason.setYear(year);
							rainySeasonList.add(rainySeason);
							flag = true;
							break;
						}
					}
					if(flag) {
						break;
					}
				}
			}
		}
		return rainySeasonList;
	}
	
	private List<RainySeason> caleStart(HashMap<String, Double> yearsHourPreMap, List<HuaDong> huaDongList, HashMap<String, PreResult> yearsPreMap, String startTime) {
		//1. 定义一个Map，key:station，value:为>历年平均候雨量的位置。57516:1,5,7...
		List<RainySeason> rainySeasonList = new ArrayList<RainySeason>();
		Map<String, String> houMap = new HashMap<String, String>();
		for(int i = 0; i < huaDongList.size(); i++) {
			HuaDong huaDong = huaDongList.get(i);
			String station_Id_C = huaDong.getStation_Id_C();
			List<HuaDongItem> itemList = huaDong.getHuaDongItemList();
			Double yearsHourPre = yearsHourPreMap.get(station_Id_C);
			if(yearsHourPre == null) {
				continue;
			}
			for(int j = 0; j < itemList.size(); j++) {
				HuaDongItem item = itemList.get(j);
				Double huaDongValue = item.getValue();
				if(huaDongValue >= yearsHourPre) {
					String houStr = houMap.get(station_Id_C);
					if(houStr == null) {
						houMap.put(station_Id_C, j + "");
					} else {
						houMap.put(station_Id_C, houStr + "," + j);
					}
				}
			}
		}
		//2. 遍历Map，找到两个位次相隔15以内的，找到对应的原始序列
		Iterator<String> houIt = houMap.keySet().iterator();
		while(houIt.hasNext()) {
			String key = houIt.next();
			String indexStr = houMap.get(key);
			String[] indexs = indexStr.split(",");
			if(indexs.length >= 2) {
				for(int i = 0; i < indexs.length - 1; i++) {
					boolean flag = false;
					for(int j = i + 1; j < indexs.length; j++) {
						int startIndex = Integer.parseInt(indexs[i]);
						int endIndex = Integer.parseInt(indexs[j]);
						// 如果后续15天,是从滑动的后一个序号开始的话,就是这个判断,如果是要滑动5天之后,则要多一个判断
						if(endIndex - startIndex < DAYSMINCOUNT) {
//						if(endIndex - startIndex < DAYSMINCOUNT && endIndex - startIndex >= HUADONGSIZE) {
//							startMap.put(key, startIndex);
							RainySeason rainySeason = new RainySeason();
							rainySeason.setStation_Id_C(key);
							String startDatetime = caleStartTime(startTime, startIndex);
							rainySeason.setStartTime(startDatetime);
							rainySeason.setYear(Integer.parseInt(startTime.substring(0, 4)));
							rainySeasonList.add(rainySeason);
							flag = true;
							break;
						}
					}
					if(flag) {
						break;
					}
				}
			}
		}
		return rainySeasonList;
	}
	
	private String caleStartTime(String startTime, int index) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = sdf.parse(startTime);
			long start = startDate.getTime();
			long startIndex = start + index * CommonConstant.DAYTIMES;
			String result = sdf.format(startIndex);
			return result;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<StartResult> calcAllFixedIndexs(List<HuaDong> huaDongList, HashMap<String, Double> yearsHourPreMap) {
		List<StartResult> startResultList = new ArrayList<StartResult>();
		for(int i = 0; i < huaDongList.size(); i++) {
			HuaDong huaDong = huaDongList.get(i);
			String station_Id_C = huaDong.getStation_Id_C();
			List<HuaDongItem> huaDongItemList = huaDong.getHuaDongItemList();
			StartResult startResult = new StartResult();
			List<Integer> indexs = new ArrayList<Integer>();
			for(int j = 0; j < huaDongItemList.size(); j++) {
				HuaDongItem huaDongItem = huaDongItemList.get(j);
				Double value = huaDongItem.getValue();
				int index = huaDongItem.getIndex();
				Double yearsHourPre = yearsHourPreMap.get(station_Id_C);
				if(value > yearsHourPre) {
					indexs.add(index);
				}
			}
			startResult.setIndexs(indexs);
			startResult.setStation_Id_C(station_Id_C);
			startResultList.add(startResult);
		}
		return startResultList;
	}
	
	private List<RainySeason> caleStart(List<StartResult> startResultList, HashMap<String, PreResult> yearsPreMap, int year) {
		List<RainySeason> rainySeasonList = new ArrayList<RainySeason>();
		for(int i = 0; i < startResultList.size(); i++) {
			StartResult startResult = startResultList.get(i);
			String station_Id_C = startResult.getStation_Id_C();
			List<Integer> indexs = startResult.getIndexs();
			for(int j = 1; j < indexs.size(); j++) {
				Integer start = indexs.get(j - 1);
				Integer end = indexs.get(j);
				Integer days = end - start;
				if(days <= DAYSMINCOUNT) {
					//满足条件
					RainySeason rainySeason = new RainySeason();
					rainySeason.setStation_Id_C(station_Id_C);
					//在原始降水序列中查找最大的那天的雨量
					PreResult preResult = yearsPreMap.get(station_Id_C);
					List<Double> preList = preResult.getPreList();
					Double maxPre = 0.0;
					int startIndex = 0;
					for(int k = start; k < start + HUADONGSIZE; k++) {
						if(preList.get(k) > maxPre) {
							maxPre = preList.get(k);
							startIndex = k;
						}
					}
					//根据索引k，计算对应的日期
					String startTime = CommonTool.addDays(year + "-04-21", startIndex);
					rainySeason.setStartTime(startTime);
					rainySeasonList.add(rainySeason);
					break;
				} 
			}
		}
		return rainySeasonList;
	}
	
	class RainySeason {
		//站号
		private String station_Id_C;
		//开始日期
		private String startTime;
		//结束日期
		private String endTime;
		//持续天数
		private Integer persistDays;
		//年
		private int year;
		//降水总量
		private double preSum;
		//id
		private int id;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getStation_Id_C() {
			return station_Id_C;
		}
		public void setStation_Id_C(String stationIdC) {
			station_Id_C = stationIdC;
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public Integer getPersistDays() {
			return persistDays;
		}
		public void setPersistDays(Integer persistDays) {
			this.persistDays = persistDays;
		}
		public int getYear() {
			return year;
		}
		public void setYear(int year) {
			this.year = year;
		}
		public double getPreSum() {
			return preSum;
		}
		public void setPreSum(double preSum) {
			this.preSum = preSum;
		}
		
	}
	class StartResult {
		//站号
		private String station_Id_C;
		//索引位置
		private List<Integer> indexs;
		
		public String getStation_Id_C() {
			return station_Id_C;
		}
		public void setStation_Id_C(String stationIdC) {
			station_Id_C = stationIdC;
		}
		public List<Integer> getIndexs() {
			return indexs;
		}
		public void setIndexs(List<Integer> indexs) {
			this.indexs = indexs;
		}
		
	}
	/**
	 *  查询第一个待定的结果
	 * @param huaDongList 滑动结果
	 * @param yearsHourPreMap 常年的结果
	 * @param yearsPreList 原始降水序列结果 
	 * @return
	 */
	private List<FirstResult> calcFirst(List<HuaDong> huaDongList, HashMap<String, Double> yearsHourPreMap, HashMap<String, PreResult> yearsPreMap) {
		List<FirstResult> resultList = new ArrayList<FirstResult>();
		for(int i = 0; i < huaDongList.size(); i++) {
			HuaDong huaDong = huaDongList.get(i);
			FirstResult firstResult = new FirstResult();
			String station_Id_C = huaDong.getStation_Id_C();
			firstResult.setStation_Id_C(station_Id_C);
			List<HuaDongItem> huaDongItemList = huaDong.getHuaDongItemList();
			Double yearHourPre = yearsHourPreMap.get(station_Id_C);
			for(int j = 0; j < huaDongItemList.size(); j++) {
				HuaDongItem huaDongItem = huaDongItemList.get(j);
				Double huaDongValue = huaDongItem.getValue();
				int huaDongIndex = huaDongItem.getIndex();
				if(huaDongValue >= yearHourPre) {
					//查找原始序列中最大的值。
					PreResult preResultList = yearsPreMap.get(station_Id_C);
					List<Double> preList = preResultList.getPreList();
					Double maxValue = 0.0;
					String maxDatetime = null;
					for(int k = huaDongIndex; k < huaDongIndex + HUADONGSIZE; k++) {
						Double itemValue = preList.get(k);
						if(maxValue < itemValue) {
							maxValue = itemValue;
							maxDatetime = ""; //TODO 未实现
							firstResult.setStartTime(maxDatetime);
						}
					}
					resultList.add(firstResult);
				}
			}
		}
		return resultList;
	}
	
	class FirstResult {
		//站号
		private String station_Id_C;
		//开始时间
		private String startTime;
		public String getStation_Id_C() {
			return station_Id_C;
		}
		public void setStation_Id_C(String stationIdC) {
			station_Id_C = stationIdC;
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
	}
	private List<HuaDong> chgHuaDong(HashMap<String, PreResult> yearsPreMap, String itemsStr, int year, String startTime) {
		List<HuaDong> huaDongList = new ArrayList<HuaDong>();
		Iterator<String> it = yearsPreMap.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			PreResult preResultList = yearsPreMap.get(key);
			HuaDong huaDong = new HuaDong();
			huaDong.setStation_Id_C(key);
			List<HuaDongItem> huaDongItemList = new ArrayList<HuaDongItem>();
			List<Double> preList = preResultList.getPreList();
			for(int j = HUADONGSIZE - 1; j < preList.size(); j++) {
				HuaDongItem huaDongItem = new HuaDongItem();
				Double preSum = 0.0;
				for(int k = j - HUADONGSIZE + 1; k <= j; k++) {
					Double pre = preList.get(k);
					if(pre == null) {
						pre = 0.0;
					}
					preSum += pre;
				}
				int index = j - HUADONGSIZE + 1; 
				String datetime = caleStartTime(startTime, index);
				huaDongItem.setIndex(index);
				huaDongItem.setValue(preSum);
				huaDongItem.setDatetime(datetime);
				huaDongItemList.add(huaDongItem);
			}
			huaDong.setHuaDongItemList(huaDongItemList);
			huaDongList.add(huaDong);
		}
		return huaDongList;
	}
	
	private void removeRepeat(HashMap<String, PreResult> yearsPreMap, Set<String> existStartSet) {
		Iterator<String> it = existStartSet.iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(yearsPreMap.containsKey(key)) {
				yearsPreMap.remove(key);
			}
		}
	}
	/**
	 * 查询降水序列
	 * @param year
	 * @return
	 */
	private HashMap<String, PreResult> getPreList(int year, String items) {
		T_pre_time_2020DaoImpl pre_time_2020DaoImpl = new T_pre_time_2020DaoImpl();
		List resultList = pre_time_2020DaoImpl.getDataItemsByYear(year, items);
		HashMap<String, PreResult> preResultMap = new HashMap<String, PreResult>();
		for(int i = 0; i < resultList.size(); i++) {
			PreResult preResult = new PreResult(); 
			List<Double> preList = new ArrayList<Double>();
			LinkedHashMap itemMap = (LinkedHashMap) resultList.get(i);
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			preResult.setStation_Id_C(station_Id_C);
			Iterator it = itemMap.keySet().iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					Double value = (Double) itemMap.get(key);
					preList.add(value);
				}
			}
			preResult.setPreList(preList);
			preResultMap.put(station_Id_C, preResult);
		}
		return preResultMap;
	}
	
	private List getPre(int year, String items) {
		T_pre_time_2020DaoImpl pre_time_2020DaoImpl = new T_pre_time_2020DaoImpl();
		List resultList = pre_time_2020DaoImpl.getDataItemsByYear(year, items);
		return resultList;
	}
	
	class PreResult {
		//站号
		private String station_Id_C;
		//序列
		private List<Double> preList;
		
		public String getStation_Id_C() {
			return station_Id_C;
		}
		public void setStation_Id_C(String stationIdC) {
			station_Id_C = stationIdC;
		}
		public List<Double> getPreList() {
			return preList;
		}
		public void setPreList(List<Double> preList) {
			this.preList = preList;
		}
	}
	private Set<String> getExistStart(int year) {
		return rainySeasonDaoImpl.getExist(year);
	}
	
	private boolean isStart(String datetime) {
		String[] temps = datetime.split("-");
		int result = Integer.parseInt(temps[1] + temps[2]);
		if(result >= SEASONSTARTSTARTTIME && result < SEASONSTARTENDTIME) {
			return true;
		}
		return false;
	}
	
	private boolean isEnd(String datetime) {
		String[] temps = datetime.split("-");
		int result = Integer.parseInt(temps[1] + temps[2]);
		if(result > SEASONENDSTARTTIME && result <= SEASONENDENDTIME) {
			return true;
		}
		return false;
	}
}
