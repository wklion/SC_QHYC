package com.spd.business;

import java.math.BigDecimal;
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

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.common.CommonTable;
import com.spd.pojo.ExtTmpMaxItem;
import com.spd.pojo.ExtTmpMinItem;
import com.spd.pojo.ItemCnt;
import com.spd.pojo.ItemCommon;
import com.spd.pojo.PreCntItem;
import com.spd.pojo.PreTimeItem;
import com.spd.pojo.PrsAvgItem;
import com.spd.pojo.RHUItem;
import com.spd.pojo.SSHItem;
import com.spd.pojo.TmpAvgItem;
import com.spd.pojo.TmpGapAvgItem;
import com.spd.pojo.TmpMaxAvgItem;
import com.spd.pojo.TmpMaxCntItem;
import com.spd.pojo.TmpMinAvgItem;
import com.spd.pojo.VisMinItem;
import com.spd.pojo.Win_s_2mi_avgItem;
import com.spd.service.ICommon;
import com.spd.tool.Eigenvalue;

/**
 * 统计结果分析的工具类
 * @author Administrator
 *
 */
public class ResultDisposeTool {

	private static HashMap<String, String> stationCountryMap = new HashMap<String, String>();

	private static HashMap<String, String> stationArewaMap = new HashMap<String, String>();
	
	public ResultDisposeTool() {
		initStationAreaMap();
	}
	
	public static void initStationAreaMap() {
		if(stationCountryMap.size() > 0 && stationCountryMap.size() > 0) return;
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		List<LinkedHashMap> resultMapList = iCommon.getAllStations();
		for(int i = 0; i < resultMapList.size(); i++) {
			LinkedHashMap itemMap = resultMapList.get(i);
			String Station_Id_C = (String) itemMap.get("Station_Id_C");
			String country = (String) itemMap.get("Country");
			String area = (String) itemMap.get("area");
			stationCountryMap.put(Station_Id_C, country);
			stationArewaMap.put(Station_Id_C, area);
		}
	}
	
	/**
	 * 创建结果数据中的常规要素，除了统计值，其他的站名，站号，经纬度信息等。
	 * @param list
	 * @return
	 */
	public static Map<String, ItemCommon> createItemCommonMap(List<Map> list) {
		initStationAreaMap();
		Map<String, ItemCommon> itemCommonMap = new HashMap<String, ItemCommon>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			int year = (Integer)map.get("year");
			String station_Id_C = (String) map.get("Station_Id_C");
			if(itemCommonMap.get(station_Id_C) == null) {
				String station_Name = (String) map.get("Station_Name");
				String province = (String) map.get("Province");
				String city = (String) map.get("City");
				String cnty = (String) map.get("Cnty");
//				String city = stationCountryMap.get(station_Id_C);
//				String cnty = stationArewaMap.get(station_Id_C);
				String station_Id_d = (String) map.get("Station_Id_d");
				double lat = (Double) map.get("Lat");
				double lon = (Double) map.get("Lon");
				double alti = (Double) map.get("Alti");
				ItemCommon itemCommon = new ItemCommon();
				itemCommon.setStation_Id_C(station_Id_C);
				itemCommon.setStation_Name(station_Name);
				itemCommon.setProvince(province);
				itemCommon.setCity(city);
				itemCommon.setCnty(cnty);
				itemCommon.setStation_Id_d(station_Id_d);
				itemCommon.setLat(lat);
				itemCommon.setLon(lon);
				itemCommon.setAlti(alti);
				itemCommonMap.put(station_Id_C, itemCommon);
			}
		}
		return itemCommonMap;
	}
	
	public static List<TmpAvgItem> createTmpAvg(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<TmpAvgItem> resultList = new ArrayList<TmpAvgItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			TmpAvgItem tmpAvgItem = new TmpAvgItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / itemCnt.getCnt() * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			tmpAvgItem.setTEM_Avg(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			tmpAvgItem.setAlti(itemCommon.getAlti());
			tmpAvgItem.setCity(itemCommon.getCity());
			tmpAvgItem.setCnty(itemCommon.getCnty());
			tmpAvgItem.setLat(itemCommon.getLat());
			tmpAvgItem.setLon(itemCommon.getLon());
			tmpAvgItem.setProvince(itemCommon.getProvince());
			tmpAvgItem.setStation_Id_C(itemCommon.getStation_Id_C());
			tmpAvgItem.setStation_Id_d(itemCommon.getStation_Id_d());
			tmpAvgItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(tmpAvgItem);
		}
		return resultList;
	}
	
	public static List<TmpMaxAvgItem> createTmpMaxAvg(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<TmpMaxAvgItem> resultList = new ArrayList<TmpMaxAvgItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			TmpMaxAvgItem tmpMaxAvgItem = new TmpMaxAvgItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / itemCnt.getCnt() * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			tmpMaxAvgItem.setTEM_Max(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			tmpMaxAvgItem.setAlti(itemCommon.getAlti());
			tmpMaxAvgItem.setCity(itemCommon.getCity());
			tmpMaxAvgItem.setCnty(itemCommon.getCnty());
			tmpMaxAvgItem.setLat(itemCommon.getLat());
			tmpMaxAvgItem.setLon(itemCommon.getLon());
			tmpMaxAvgItem.setProvince(itemCommon.getProvince());
			tmpMaxAvgItem.setStation_Id_C(itemCommon.getStation_Id_C());
			tmpMaxAvgItem.setStation_Id_d(itemCommon.getStation_Id_d());
			tmpMaxAvgItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(tmpMaxAvgItem);
		}
		return resultList;
	}
	
	public static List<TmpMinAvgItem> createTmpMinAvg(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<TmpMinAvgItem> resultList = new ArrayList<TmpMinAvgItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			TmpMinAvgItem tmpMinAvgItem = new TmpMinAvgItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / itemCnt.getCnt() * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			tmpMinAvgItem.setTEM_Min(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			tmpMinAvgItem.setAlti(itemCommon.getAlti());
			tmpMinAvgItem.setCity(itemCommon.getCity());
			tmpMinAvgItem.setCnty(itemCommon.getCnty());
			tmpMinAvgItem.setLat(itemCommon.getLat());
			tmpMinAvgItem.setLon(itemCommon.getLon());
			tmpMinAvgItem.setProvince(itemCommon.getProvince());
			tmpMinAvgItem.setStation_Id_C(itemCommon.getStation_Id_C());
			tmpMinAvgItem.setStation_Id_d(itemCommon.getStation_Id_d());
			tmpMinAvgItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(tmpMinAvgItem);
		}
		return resultList;
	}
	
	public static List<TmpGapAvgItem> createTmpGapAvg(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<TmpGapAvgItem> resultList = new ArrayList<TmpGapAvgItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			TmpGapAvgItem tmpGapAvgItem = new TmpGapAvgItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / itemCnt.getCnt() * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			tmpGapAvgItem.setTEM_Gap(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			tmpGapAvgItem.setAlti(itemCommon.getAlti());
			tmpGapAvgItem.setCity(itemCommon.getCity());
			tmpGapAvgItem.setCnty(itemCommon.getCnty());
			tmpGapAvgItem.setLat(itemCommon.getLat());
			tmpGapAvgItem.setLon(itemCommon.getLon());
			tmpGapAvgItem.setProvince(itemCommon.getProvince());
			tmpGapAvgItem.setStation_Id_C(itemCommon.getStation_Id_C());
			tmpGapAvgItem.setStation_Id_d(itemCommon.getStation_Id_d());
			tmpGapAvgItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(tmpGapAvgItem);
		}
		return resultList;
	}
	
	public static List<Win_s_2mi_avgItem> createWin_s_2mi_avgAvg(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<Win_s_2mi_avgItem> resultList = new ArrayList<Win_s_2mi_avgItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			Win_s_2mi_avgItem win_s_2mi_avgItem = new Win_s_2mi_avgItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / itemCnt.getCnt() * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			win_s_2mi_avgItem.setWIN_S_2mi_Avg(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			win_s_2mi_avgItem.setAlti(itemCommon.getAlti());
			win_s_2mi_avgItem.setCity(itemCommon.getCity());
			win_s_2mi_avgItem.setCnty(itemCommon.getCnty());
			win_s_2mi_avgItem.setLat(itemCommon.getLat());
			win_s_2mi_avgItem.setLon(itemCommon.getLon());
			win_s_2mi_avgItem.setProvince(itemCommon.getProvince());
			win_s_2mi_avgItem.setStation_Id_C(itemCommon.getStation_Id_C());
			win_s_2mi_avgItem.setStation_Id_d(itemCommon.getStation_Id_d());
			win_s_2mi_avgItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(win_s_2mi_avgItem);
		}
		return resultList;
	}
	
	public static List<PrsAvgItem> createPrsavgAvg(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<PrsAvgItem> resultList = new ArrayList<PrsAvgItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			PrsAvgItem prsAvgItem = new PrsAvgItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / itemCnt.getCnt() * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			prsAvgItem.setPRS_Avg(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			prsAvgItem.setAlti(itemCommon.getAlti());
			prsAvgItem.setCity(itemCommon.getCity());
			prsAvgItem.setCnty(itemCommon.getCnty());
			prsAvgItem.setLat(itemCommon.getLat());
			prsAvgItem.setLon(itemCommon.getLon());
			prsAvgItem.setProvince(itemCommon.getProvince());
			prsAvgItem.setStation_Id_C(itemCommon.getStation_Id_C());
			prsAvgItem.setStation_Id_d(itemCommon.getStation_Id_d());
			prsAvgItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(prsAvgItem);
		}
		return resultList;
	}
	
	public static List<PreTimeItem> createPreTimeItemSum(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<PreTimeItem> resultList = new ArrayList<PreTimeItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			PreTimeItem prsAvgItem = new PreTimeItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			prsAvgItem.setPRE_Time(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			prsAvgItem.setAlti(itemCommon.getAlti());
			prsAvgItem.setCity(itemCommon.getCity());
			prsAvgItem.setCnty(itemCommon.getCnty());
			prsAvgItem.setLat(itemCommon.getLat());
			prsAvgItem.setLon(itemCommon.getLon());
			prsAvgItem.setProvince(itemCommon.getProvince());
			prsAvgItem.setStation_Id_C(itemCommon.getStation_Id_C());
			prsAvgItem.setStation_Id_d(itemCommon.getStation_Id_d());
			prsAvgItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(prsAvgItem);
		}
		return resultList;
	}
	
	public static List<SSHItem> createSSHItemSum(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<SSHItem> resultList = new ArrayList<SSHItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			SSHItem sshItem = new SSHItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			sshItem.setSSH(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			sshItem.setAlti(itemCommon.getAlti());
			sshItem.setCity(itemCommon.getCity());
			sshItem.setCnty(itemCommon.getCnty());
			sshItem.setLat(itemCommon.getLat());
			sshItem.setLon(itemCommon.getLon());
			sshItem.setProvince(itemCommon.getProvince());
			sshItem.setStation_Id_C(itemCommon.getStation_Id_C());
			sshItem.setStation_Id_d(itemCommon.getStation_Id_d());
			sshItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(sshItem);
		}
		return resultList;
	}
	
	public static List<RHUItem> createRHUItemSum(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<RHUItem> resultList = new ArrayList<RHUItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			RHUItem rhuItem = new RHUItem();
			//保留小数位一位
			int rhuAvg = (int)(itemCnt.getSum() / itemCnt.getCnt() * 100);
			double doubleTmpAvg = Math.round(rhuAvg / 10.0);
			rhuItem.setRHU_Avg(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			rhuItem.setAlti(itemCommon.getAlti());
			rhuItem.setCity(itemCommon.getCity());
			rhuItem.setCnty(itemCommon.getCnty());
			rhuItem.setLat(itemCommon.getLat());
			rhuItem.setLon(itemCommon.getLon());
			rhuItem.setProvince(itemCommon.getProvince());
			rhuItem.setStation_Id_C(itemCommon.getStation_Id_C());
			rhuItem.setStation_Id_d(itemCommon.getStation_Id_d());
			rhuItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(rhuItem);
		}
		return resultList;
	}
	public static List<PreTimeItem> createPreTimeItemSumByYears(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap, int years) {
		List<PreTimeItem> resultList = new ArrayList<PreTimeItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			PreTimeItem prsAvgItem = new PreTimeItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / years * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			prsAvgItem.setPRE_Time(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			prsAvgItem.setAlti(itemCommon.getAlti());
			prsAvgItem.setCity(itemCommon.getCity());
			prsAvgItem.setCnty(itemCommon.getCnty());
			prsAvgItem.setLat(itemCommon.getLat());
			prsAvgItem.setLon(itemCommon.getLon());
			prsAvgItem.setProvince(itemCommon.getProvince());
			prsAvgItem.setStation_Id_C(itemCommon.getStation_Id_C());
			prsAvgItem.setStation_Id_d(itemCommon.getStation_Id_d());
			prsAvgItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(prsAvgItem);
		}
		return resultList;
	}
	
	
	public static List<SSHItem> createSSHItemSumByYears(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap, int years) {
		List<SSHItem> resultList = new ArrayList<SSHItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			SSHItem sshItem = new SSHItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / years * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			sshItem.setSSH(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			sshItem.setAlti(itemCommon.getAlti());
			sshItem.setCity(itemCommon.getCity());
			sshItem.setCnty(itemCommon.getCnty());
			sshItem.setLat(itemCommon.getLat());
			sshItem.setLon(itemCommon.getLon());
			sshItem.setProvince(itemCommon.getProvince());
			sshItem.setStation_Id_C(itemCommon.getStation_Id_C());
			sshItem.setStation_Id_d(itemCommon.getStation_Id_d());
			sshItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(sshItem);
		}
		return resultList;
	}
	
	public static List<RHUItem> createRHUItemSumByYears(Map<String, ItemCnt> resultMap, Map<String, ItemCommon> itemCommonMap) {
		List<RHUItem> resultList = new ArrayList<RHUItem>();
		Set<String> keySet = resultMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String station_Id_C = it.next();
			ItemCnt itemCnt = resultMap.get(station_Id_C);
			RHUItem rhuItem = new RHUItem();
			//保留小数位一位
			int tmpAvg = (int)(itemCnt.getSum() / itemCnt.getCnt()  * 100);
			double doubleTmpAvg = Math.round(tmpAvg / 10.0);
			rhuItem.setRHU_Avg(doubleTmpAvg / 10.0);
			ItemCommon itemCommon = itemCommonMap.get(station_Id_C);
			rhuItem.setAlti(itemCommon.getAlti());
			rhuItem.setCity(itemCommon.getCity());
			rhuItem.setCnty(itemCommon.getCnty());
			rhuItem.setLat(itemCommon.getLat());
			rhuItem.setLon(itemCommon.getLon());
			rhuItem.setProvince(itemCommon.getProvince());
			rhuItem.setStation_Id_C(itemCommon.getStation_Id_C());
			rhuItem.setStation_Id_d(itemCommon.getStation_Id_d());
			rhuItem.setStation_Name(itemCommon.getStation_Name());
			resultList.add(rhuItem);
		}
		return resultList;
	}
	
	
	/**
	 * 连续时间段的求和
	 * @return
	 */
	public static Map<String, ItemCnt> sumItemsByRangeTimes(List<Map> list, Date startDate, Date endDate, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		Map<String, ItemCnt> resultMap = new HashMap<String, ItemCnt>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			int year = (Integer)map.get("year");
			String station_Id_C = (String) map.get("Station_Id_C");
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateStr = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6) + " 00:00:00";
					try {
						long dateTime = sdf.parse(dateStr).getTime();
						if(dateTime >= startTime && dateTime <= endTime) {
							Object objValue = map.get(key);
							Double value = null;
							if("BigDecimal".equals(columnType) && objValue != null) {
								value = ((BigDecimal)objValue).doubleValue();
							} else {
								value = (Double) map.get(key);
							}
							value = Eigenvalue.dispose(value);
							if(value == null) {
								continue;
							}
//							if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//								continue;
//							}
							if(value != null) {
								ItemCnt itemCnt = resultMap.get(station_Id_C);
								if(itemCnt == null) {
									itemCnt = new ItemCnt();
									itemCnt.setCnt(1);
									itemCnt.setSum(value);
								} else {
									itemCnt.setCnt(itemCnt.getCnt() + 1);
									itemCnt.setSum(itemCnt.getSum() + value);
								}
								resultMap.put(station_Id_C, itemCnt);
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * 查询最小能见度
	 * @param list
	 * @return
	 */
	public static List<VisMinItem> VisMinByYears(List<Map> list, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		List<VisMinItem> resultList = new ArrayList<VisMinItem>();
		Map<String, VisMinItem> mapResult = new HashMap<String, VisMinItem>();
		for(Map map : list) {
			VisMinItem visMinItem = null;
			Set<String> set = map.keySet();
			String Station_Id_C = (String)map.get("Station_Id_C");
			int year = (Integer)map.get("year");
			if(mapResult.containsKey(Station_Id_C)) {
				visMinItem = mapResult.get(Station_Id_C);
			} else {
				visMinItem = new VisMinItem();
				visMinItem.setAlti((Double)map.get("Alti"));
				visMinItem.setCity((String)map.get("City"));
				visMinItem.setCnty((String)map.get("Cnty"));
				visMinItem.setStation_Name((String)map.get("Station_Name"));
				visMinItem.setProvince((String)map.get("Province"));
				visMinItem.setStation_Id_C((String)map.get("Station_Id_C"));
				visMinItem.setStation_Id_d((String)map.get("Station_Id_d"));
				visMinItem.setLat((Double)map.get("Lat"));
				visMinItem.setLon((Double)map.get("Lon"));
				mapResult.put(Station_Id_C, visMinItem);
			}
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
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
//					if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//						continue;
//					}
					if(value != null) {
						if(visMinItem.getVIS_Min_OTime() == null) {
							visMinItem.setVIS_Min(value);
							visMinItem.setVIS_Min_OTime(year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6));
						} else {
							if(value < visMinItem.getVIS_Min()) {
								visMinItem.setVIS_Min(value);
								visMinItem.setVIS_Min_OTime(dateDay);
							} else if(value == visMinItem.getVIS_Min()) {
								visMinItem.setVIS_Min_OTime(visMinItem.getVIS_Min_OTime() + "," + dateDay);
							}
						}
					}
				}
			}
		}
//		if(visMinItem.getVIS_Min_OTime() != null) {
//			resultList.add(visMinItem);
//		}
		Set<String> set = mapResult.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			VisMinItem visMinItem = mapResult.get(key);
			resultList.add(visMinItem);
		}
		return resultList;
	}
	
	/**
	 * 历年同期高温统计
	 * @param list
	 * @return
	 */
	public static List<ExtTmpMaxItem> extMaxTmpByYears(List<Map> list, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		List<ExtTmpMaxItem> resultList = new ArrayList<ExtTmpMaxItem>();
		Map<String, ExtTmpMaxItem> mapResult = new HashMap<String, ExtTmpMaxItem>();
		for(Map map : list) {
			ExtTmpMaxItem extTmpMaxItem = null;
			Set<String> set = map.keySet();
			String Station_Id_C = (String)map.get("Station_Id_C");
			int year = (Integer)map.get("year");
			if(mapResult.containsKey(Station_Id_C)) {
				extTmpMaxItem = mapResult.get(Station_Id_C);
			} else {
				extTmpMaxItem = new ExtTmpMaxItem();
				extTmpMaxItem.setAlti((Double)map.get("Alti"));
				extTmpMaxItem.setCity((String)map.get("City"));
				extTmpMaxItem.setCnty((String)map.get("Cnty"));
				extTmpMaxItem.setStation_Name((String)map.get("Station_Name"));
				extTmpMaxItem.setProvince((String)map.get("Province"));
				extTmpMaxItem.setStation_Id_C((String)map.get("Station_Id_C"));
				extTmpMaxItem.setStation_Id_d((String)map.get("Station_Id_d"));
				extTmpMaxItem.setLat((Double)map.get("Lat"));
				extTmpMaxItem.setLon((Double)map.get("Lon"));
				mapResult.put(Station_Id_C, extTmpMaxItem);
			}
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
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
//					if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//						continue;
//					}
					if(value != null) {
						if(extTmpMaxItem.getTEM_Max_OTime() == null) {
							extTmpMaxItem.setTEM_Max(value);
							extTmpMaxItem.setTEM_Max_OTime(dateDay);
						} else {
							if(value > extTmpMaxItem.getTEM_Max()) {
								extTmpMaxItem.setTEM_Max(value);
								extTmpMaxItem.setTEM_Max_OTime(dateDay);
							} else if(value == extTmpMaxItem.getTEM_Max()) {
								extTmpMaxItem.setTEM_Max_OTime(extTmpMaxItem.getTEM_Max_OTime() + "," + dateDay);
							}
						}
					}
				}
			}
		}
		Set<String> set = mapResult.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			ExtTmpMaxItem visMinItem = mapResult.get(key);
			resultList.add(visMinItem);
		}
		return resultList;
	}
	
	/**
	 * 历年同期低温统计
	 * @param list
	 * @return
	 */
	public static List<ExtTmpMinItem> extMinTmpByYears(List<Map> list, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		List<ExtTmpMinItem> resultList = new ArrayList<ExtTmpMinItem>();
		Map<String, ExtTmpMinItem> mapResult = new HashMap<String, ExtTmpMinItem>();
		for(Map map : list) {
			ExtTmpMinItem extTmpMinItem = null;
			Set<String> set = map.keySet();
			String Station_Id_C = (String)map.get("Station_Id_C");
			int year = (Integer)map.get("year");
			if(mapResult.containsKey(Station_Id_C)) {
				extTmpMinItem = mapResult.get(Station_Id_C);
			} else {
				extTmpMinItem = new ExtTmpMinItem();
				extTmpMinItem.setAlti((Double)map.get("Alti"));
				extTmpMinItem.setCity((String)map.get("City"));
				extTmpMinItem.setCnty((String)map.get("Cnty"));
				extTmpMinItem.setStation_Name((String)map.get("Station_Name"));
				extTmpMinItem.setProvince((String)map.get("Province"));
				extTmpMinItem.setStation_Id_C((String)map.get("Station_Id_C"));
				extTmpMinItem.setStation_Id_d((String)map.get("Station_Id_d"));
				extTmpMinItem.setLat((Double)map.get("Lat"));
				extTmpMinItem.setLon((Double)map.get("Lon"));
				mapResult.put(Station_Id_C, extTmpMinItem);
			}
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
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
//					if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//						continue;
//					}
					if(value != null) {
						if(extTmpMinItem.getTEM_Min_OTime() == null) {
							extTmpMinItem.setTEM_Min(value);
							extTmpMinItem.setTEM_Min_OTime(dateDay);
						} else {
							if(value < extTmpMinItem.getTEM_Min()) {
								extTmpMinItem.setTEM_Min(value);
								extTmpMinItem.setTEM_Min_OTime(dateDay);
							} else if(value == extTmpMinItem.getTEM_Min()) {
								extTmpMinItem.setTEM_Min_OTime(extTmpMinItem.getTEM_Min_OTime() + "," + dateDay);
							}
						}
					}
				}
			}
		}
		Set<String> set = mapResult.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			ExtTmpMinItem extTmpMinItem = mapResult.get(key);
			resultList.add(extTmpMinItem);
		}
		return resultList;
	}
	/**
	 * 统计极端高温，以及出现的时间
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<ExtTmpMaxItem> analystExtMaxTmp(List<Map> list, Date startDate, Date endDate, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		Map<String, ExtTmpMaxItem> resultMap = new HashMap<String, ExtTmpMaxItem>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		List<ExtTmpMaxItem> resultList = new ArrayList<ExtTmpMaxItem>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			int year = (Integer)map.get("year");
			ExtTmpMaxItem extTmpMaxItem = resultMap.get((String)map.get("Station_Id_C"));
			if(extTmpMaxItem == null) {
				extTmpMaxItem = new ExtTmpMaxItem();
				extTmpMaxItem.setAlti((Double)map.get("Alti"));
				extTmpMaxItem.setCity((String)map.get("City"));
				extTmpMaxItem.setCnty((String)map.get("Cnty"));
				extTmpMaxItem.setStation_Name((String)map.get("Station_Name"));
				extTmpMaxItem.setProvince((String)map.get("Province"));
				extTmpMaxItem.setStation_Id_C((String)map.get("Station_Id_C"));
				extTmpMaxItem.setStation_Id_d((String)map.get("Station_Id_d"));
				extTmpMaxItem.setLat((Double)map.get("Lat"));
				extTmpMaxItem.setLon((Double)map.get("Lon"));
			}
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
					try {
						long dateTime = sdf.parse(dateStr).getTime();
						if(dateTime >= startTime && dateTime <= endTime) {
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
//							if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//								continue;
//							}
							if(value != null) {
								if(extTmpMaxItem.getTEM_Max_OTime() == null) {
									extTmpMaxItem.setTEM_Max(value);
									extTmpMaxItem.setTEM_Max_OTime(dateDay);
								} else {
									if(value > extTmpMaxItem.getTEM_Max()) {
										extTmpMaxItem.setTEM_Max(value);
										extTmpMaxItem.setTEM_Max_OTime(dateDay);
									} else if(value == extTmpMaxItem.getTEM_Max()) {
										extTmpMaxItem.setTEM_Max_OTime(extTmpMaxItem.getTEM_Max_OTime() + "," + dateDay);
									}
								}
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			if(extTmpMaxItem.getTEM_Max_OTime() != null) {
				resultMap.put((String)map.get("Station_Id_C"), extTmpMaxItem);
			}
		}
		Set<String> set = resultMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			resultList.add(resultMap.get(it.next()));
		}
		return resultList;
	}
	
	/**
	 * 统计极端低温，以及出现的时间
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<ExtTmpMinItem> analystExtMinTmp(List<Map> list, Date startDate, Date endDate, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		Map<String, ExtTmpMinItem> resultMap = new HashMap<String, ExtTmpMinItem>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		List<ExtTmpMinItem> resultList = new ArrayList<ExtTmpMinItem>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			int year = (Integer)map.get("year");
			ExtTmpMinItem extTmpMinItem = resultMap.get((String)map.get("Station_Id_C"));//new ExtTmpMinItem();
			if(extTmpMinItem == null) {
				extTmpMinItem = new ExtTmpMinItem();
				extTmpMinItem.setAlti((Double)map.get("Alti"));
				extTmpMinItem.setCity((String)map.get("City"));
				extTmpMinItem.setCnty((String)map.get("Cnty"));
				extTmpMinItem.setStation_Name((String)map.get("Station_Name"));
				extTmpMinItem.setProvince((String)map.get("Province"));
				extTmpMinItem.setStation_Id_C((String)map.get("Station_Id_C"));
				extTmpMinItem.setStation_Id_d((String)map.get("Station_Id_d"));
				extTmpMinItem.setLat((Double)map.get("Lat"));
				extTmpMinItem.setLon((Double)map.get("Lon"));
			}
			
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
					try {
						long dateTime = sdf.parse(dateStr).getTime();
						if(dateTime >= startTime && dateTime <= endTime) {
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
//							if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//								continue;
//							}
							if(value != null) {
								if(extTmpMinItem.getTEM_Min_OTime() == null) {
									extTmpMinItem.setTEM_Min(value);
									extTmpMinItem.setTEM_Min_OTime(dateDay);
								} else {
									if(value < extTmpMinItem.getTEM_Min()) {
										extTmpMinItem.setTEM_Min(value);
										extTmpMinItem.setTEM_Min_OTime(dateDay);
									} else if(value == extTmpMinItem.getTEM_Min()) {
										extTmpMinItem.setTEM_Min_OTime(extTmpMinItem.getTEM_Min_OTime() + "," + dateDay);
									}
								}
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			if(extTmpMinItem.getTEM_Min_OTime() != null) {
				resultMap.put((String)map.get("Station_Id_C"), extTmpMinItem);
//				resultList.add(extTmpMinItem);
			}
		}
		Set<String> set = resultMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			resultList.add(resultMap.get(it.next()));
		}
		return resultList;
	}
	public static List<VisMinItem> analystVisMin(List<Map> list, Date startDate, Date endDate, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		Map<String, VisMinItem> resultMap = new HashMap<String, VisMinItem>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		List<VisMinItem> resultList = new ArrayList<VisMinItem>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			int year = (Integer)map.get("year");
			String station_Id_C = (String) map.get("Station_Id_C");
			VisMinItem visMinItem = resultMap.get(station_Id_C);
			if(visMinItem == null) {
				visMinItem = new VisMinItem();
				visMinItem.setAlti((Double)map.get("Alti"));
				visMinItem.setCity((String)map.get("City"));
				visMinItem.setCnty((String)map.get("Cnty"));
				visMinItem.setStation_Name((String)map.get("Station_Name"));
				visMinItem.setProvince((String)map.get("Province"));
				visMinItem.setStation_Id_C((String)map.get("Station_Id_C"));
				visMinItem.setStation_Id_d((String)map.get("Station_Id_d"));
				visMinItem.setLat((Double)map.get("Lat"));
				visMinItem.setLon((Double)map.get("Lon"));
			}
			
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
					try {
						long dateTime = sdf.parse(dateStr).getTime();
						if(dateTime >= startTime && dateTime <= endTime) {
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
//							if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//								continue;
//							}
							if(value != null) {
								if(visMinItem.getVIS_Min_OTime() == null) {
									visMinItem.setVIS_Min(value);
									visMinItem.setVIS_Min_OTime(year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6));
								} else {
									if(value < visMinItem.getVIS_Min()) {
										visMinItem.setVIS_Min(value);
										visMinItem.setVIS_Min_OTime(dateDay);
									} else if(value == visMinItem.getVIS_Min()) {
										visMinItem.setVIS_Min_OTime(visMinItem.getVIS_Min_OTime() + "," + dateDay);
									}
								}
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			if(visMinItem.getVIS_Min_OTime() != null) {
				resultMap.put(station_Id_C, visMinItem);
			}
		}
		Set<String> set = resultMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			resultList.add(resultMap.get(it.next()));
		}
		return resultList;
	}
	
	
	public static Map<String, ItemCnt> sumItemsByStation(List<Map> list) {
		Map<String, ItemCnt> resultMap = new HashMap<String, ItemCnt>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			String station_Id_C = (String) map.get("Station_Id_C");
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					Double value = null;
					Object objValue = map.get(key);
					if(objValue instanceof BigDecimal && objValue != null) {
						value = ((BigDecimal)objValue).doubleValue();
					} else {
						value = (Double) map.get(key);
					}
					value = Eigenvalue.dispose(value);
					if(value == null) {
						continue;
					}
//					if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//						continue;
//					}
					if(value != null) {
						ItemCnt itemCnt = resultMap.get(station_Id_C);
						if(itemCnt == null) {
							itemCnt = new ItemCnt();
							itemCnt.setCnt(1);
							itemCnt.setSum(value);
						} else {
							itemCnt.setCnt(itemCnt.getCnt() + 1);
							itemCnt.setSum(itemCnt.getSum() + value);
						}
						resultMap.put(station_Id_C, itemCnt);
					}
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * 根据站点统计降水日数
	 * @param list
	 * @return
	 */
	public static List<PreCntItem> queryPreCntByTimeRange(List<Map> list, Date startDate, Date endDate, String tableName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		List<PreCntItem> resultList = new ArrayList<PreCntItem>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			int year = (Integer)map.get("year");
			PreCntItem preCntItem = new PreCntItem();
			preCntItem.setAlti((Double)map.get("Alti"));
			preCntItem.setCity((String)map.get("City"));
			preCntItem.setCnty((String)map.get("Cnty"));
			preCntItem.setStation_Name((String)map.get("Station_Name"));
			preCntItem.setProvince((String)map.get("Province"));
			preCntItem.setStation_Id_C((String)map.get("Station_Id_C"));
			preCntItem.setStation_Id_d((String)map.get("Station_Id_d"));
			preCntItem.setLat((Double)map.get("Lat"));
			preCntItem.setLon((Double)map.get("Lon"));
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
					try {
						long dateTime = sdf.parse(dateStr).getTime();
						if(dateTime >= startTime && dateTime <= endTime) {
							Double value = null;
							Object objValue = map.get(key);
							if(objValue instanceof BigDecimal && objValue != null) {
								value = ((BigDecimal)objValue).doubleValue();
							} else {
								value = (Double) map.get(key);
							}
							value = Eigenvalue.dispose(value);
							if(value == null) {
								continue;
							}
//							if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//								continue;
//							}
							if(value != null) {
								if(value > 0) {
									preCntItem.setCnt(preCntItem.getCnt() + 1);
								}
								if(value >= 25 && value < 50) {
									preCntItem.setGet25lt50cnt(preCntItem.getGet25lt50cnt() + 1);
								}
								if(value >= 50 && value < 100) {
									preCntItem.setGet50lt100cnt(preCntItem.getGet50lt100cnt() + 1);
								}
								if(value > 100) {
									preCntItem.setGet100(preCntItem.getGet100() + 1);
								}
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			resultList.add(preCntItem);
		}
		return resultList;
	}
	
	/**
	 * 根据日期范围统计高温日数
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<TmpMaxCntItem> queryTmpMaxCntByTimeRange(List<Map> list, Date startDate, Date endDate, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		List<TmpMaxCntItem> resultList = new ArrayList<TmpMaxCntItem>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			int year = (Integer)map.get("year");
			TmpMaxCntItem tmpMaxCntItem = new TmpMaxCntItem();
			tmpMaxCntItem.setAlti((Double)map.get("Alti"));
			tmpMaxCntItem.setCity((String)map.get("City"));
			tmpMaxCntItem.setCnty((String)map.get("Cnty"));
			tmpMaxCntItem.setStation_Name((String)map.get("Station_Name"));
			tmpMaxCntItem.setProvince((String)map.get("Province"));
			tmpMaxCntItem.setStation_Id_C((String)map.get("Station_Id_C"));
			tmpMaxCntItem.setStation_Id_d((String)map.get("Station_Id_d"));
			tmpMaxCntItem.setLat((Double)map.get("Lat"));
			tmpMaxCntItem.setLon((Double)map.get("Lon"));
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
					try {
						long dateTime = sdf.parse(dateStr).getTime();
						if(dateTime >= startTime && dateTime <= endTime) {
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
//							if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//								continue;
//							}
							if(value != null) {
								if(value >= 35) {
									tmpMaxCntItem.setGte35(tmpMaxCntItem.getGte35() + 1);
								}
								if(value >= 35 && value < 37) {
									tmpMaxCntItem.setGte35lt37(tmpMaxCntItem.getGte35lt37() + 1);
								} 
								if(value >= 37) {
									tmpMaxCntItem.setGte37(tmpMaxCntItem.getGte37() + 1);
								}
								if(value >= 37 && value < 40) {
									tmpMaxCntItem.setGte37lt40(tmpMaxCntItem.getGte37lt40() + 1);
								}
								if(value >= 40) {
									tmpMaxCntItem.setGte40(tmpMaxCntItem.getGte40() + 1);
								}
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			resultList.add(tmpMaxCntItem);
		}
		return resultList;
	}
	
	
	/**
	 * 历年同期统计降水日数
	 * @param list
	 * @return
	 */
	public static List<PreCntItem> queryPreCntByYears(List<Map> list, int years, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		List<PreCntItem> tempResultList = new ArrayList<PreCntItem>();
		List<PreCntItem> resultList = new ArrayList<PreCntItem>();
		Map<String, PreCntItem> mapResult = new HashMap<String, PreCntItem>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			String Station_Id_C = (String)map.get("Station_Id_C");
			int year = (Integer)map.get("year");
			PreCntItem preCntItem = new PreCntItem();
			preCntItem.setAlti((Double)map.get("Alti"));
			preCntItem.setCity((String)map.get("City"));
			preCntItem.setCnty((String)map.get("Cnty"));
			preCntItem.setStation_Name((String)map.get("Station_Name"));
			preCntItem.setProvince((String)map.get("Province"));
			preCntItem.setStation_Id_C((String)map.get("Station_Id_C"));
			preCntItem.setStation_Id_d((String)map.get("Station_Id_d"));
			preCntItem.setLat((Double)map.get("Lat"));
			preCntItem.setLon((Double)map.get("Lon"));
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
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
//					if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//						continue;
//					}
					if(value != null) {
						value = Eigenvalue.dispose(value);
						if(value == null) {
							continue;
						}
//						if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//							continue;
//						}
						if(value != null) {
							if(value > 0) {
								preCntItem.setCnt(preCntItem.getCnt() + 1);
							}
						}
					}
				}
			}
			tempResultList.add(preCntItem);
		}
		// 求和
		for(PreCntItem preCntItem : tempResultList) {
			String station_id_c = preCntItem.getStation_Id_C();
			if(mapResult.get(station_id_c) == null) {
				mapResult.put(station_id_c, preCntItem);
			} else {
				double oriCnt = mapResult.get(station_id_c).getCnt();
				preCntItem.setCnt(oriCnt + preCntItem.getCnt()); 
				mapResult.put(station_id_c, preCntItem);
			}
		}
		//求平均
		Set<String> set = mapResult.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			PreCntItem preCntItem = mapResult.get(key);
			int tmpCnt = (int)(preCntItem.getCnt() / years  * 100);
			double doubleCntAvg = Math.round(tmpCnt / 10.0);
			preCntItem.setCnt(doubleCntAvg / 10.0);
			resultList.add(preCntItem);
		}
		return resultList;
	}
	
	/**
	 * 历年同期，统计高温日数
	 * @param list
	 * @param years
	 * @return
	 */
	public static List<TmpMaxCntItem> queryTmpMaxCntByYears(List<Map> list, int years, String tableName) {
		String columnType = CommonTable.getInstance().getTypeByTableName(tableName);
		List<TmpMaxCntItem> tempResultList = new ArrayList<TmpMaxCntItem>();
		List<TmpMaxCntItem> resultList = new ArrayList<TmpMaxCntItem>();
		Map<String, TmpMaxCntItem> mapResult = new HashMap<String, TmpMaxCntItem>();
		for(Map map : list) {
			Set<String> set = map.keySet();
			String Station_Id_C = (String)map.get("Station_Id_C");
			int year = (Integer)map.get("year");
			TmpMaxCntItem tmpMaxCntItem = new TmpMaxCntItem();
			tmpMaxCntItem.setAlti((Double)map.get("Alti"));
			tmpMaxCntItem.setCity((String)map.get("City"));
			tmpMaxCntItem.setCnty((String)map.get("Cnty"));
			tmpMaxCntItem.setStation_Name((String)map.get("Station_Name"));
			tmpMaxCntItem.setProvince((String)map.get("Province"));
			tmpMaxCntItem.setStation_Id_C((String)map.get("Station_Id_C"));
			tmpMaxCntItem.setStation_Id_d((String)map.get("Station_Id_d"));
			tmpMaxCntItem.setLat((Double)map.get("Lat"));
			tmpMaxCntItem.setLon((Double)map.get("Lon"));
			Iterator<String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				if(key.matches("m\\d{2}d\\d{2}")) {
					String dateDay = year + "-" + key.substring(1, 3) + "-" + key.substring(4, 6);
					String dateStr = dateDay + " 00:00:00";
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
//					if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//						continue;
//					}
					if(value != null) {
//						if(value >= CommonConstant.MAXINVALID || value <= CommonConstant.MININVALID) {
//							continue;
//						}
						value = Eigenvalue.dispose(value);
						if(value == null) {
							continue;
						}
						if(value != null) {
							if(value >= 35) {
								tmpMaxCntItem.setGte35(tmpMaxCntItem.getGte35() + 1);
							}
							if(value >= 35 && value < 37) {
								tmpMaxCntItem.setGte35lt37(tmpMaxCntItem.getGte35lt37() + 1);
							} 
							if(value >= 37) {
								tmpMaxCntItem.setGte37(tmpMaxCntItem.getGte37() + 1);
							}
							if(value >= 37 && value < 40) {
								tmpMaxCntItem.setGte37lt40(tmpMaxCntItem.getGte37lt40() + 1);
							}
							if(value >= 40) {
								tmpMaxCntItem.setGte40(tmpMaxCntItem.getGte40() + 1);
							}
						}
					}
				}
			}
			tempResultList.add(tmpMaxCntItem);
		}
		// 求和
		for(TmpMaxCntItem preCntItem : tempResultList) {
			String station_id_c = preCntItem.getStation_Id_C();
			if(mapResult.get(station_id_c) == null) {
				mapResult.put(station_id_c, preCntItem);
			} else {
				TmpMaxCntItem tempTmpMaxCntItem = mapResult.get(station_id_c);
				double oriGTE35Cnt = tempTmpMaxCntItem.getGte35();
				tempTmpMaxCntItem.setGte35(oriGTE35Cnt + preCntItem.getGte35());
				double oriGte35lt37Cnt = tempTmpMaxCntItem.getGte35lt37();
				tempTmpMaxCntItem.setGte35lt37(oriGte35lt37Cnt + preCntItem.getGte35lt37());
				double oriGTE37Cnt = tempTmpMaxCntItem.getGte37();
				tempTmpMaxCntItem.setGte37(oriGTE37Cnt + preCntItem.getGte37());
				double oriGTE37lt40Cnt = tempTmpMaxCntItem.getGte37lt40();
				tempTmpMaxCntItem.setGte37lt40(oriGTE37lt40Cnt + preCntItem.getGte37lt40());
				double oriGTE40Cnt = tempTmpMaxCntItem.getGte40();
				tempTmpMaxCntItem.setGte40(oriGTE40Cnt + preCntItem.getGte40());
				mapResult.put(station_id_c, tempTmpMaxCntItem);
			}
		}
		//求平均
		Set<String> set = mapResult.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()) {
			String key = it.next();
			TmpMaxCntItem tmpMaxCntItem = mapResult.get(key);
			int tmpGte35Cnt = (int)(tmpMaxCntItem.getGte35() / years  * 100);
			double tmpGte35CntAvg = Math.round(tmpGte35Cnt / 10.0);
			tmpMaxCntItem.setGte35(tmpGte35CntAvg / 10.0);
			
			int tmpGte35lt37 = (int)(tmpMaxCntItem.getGte35lt37() / years  * 100);
			double tmpGte35lt37Avg = Math.round(tmpGte35lt37 / 10.0);
			tmpMaxCntItem.setGte35lt37(tmpGte35lt37Avg / 10.0);
			
			int tmpGte37Cnt = (int)(tmpMaxCntItem.getGte37() / years  * 100);
			double tmpGte37CntAvg = Math.round(tmpGte37Cnt / 10.0);
			tmpMaxCntItem.setGte37(tmpGte37CntAvg / 10.0);
			
			int tmpGte37lt40 = (int)(tmpMaxCntItem.getGte37lt40() / years  * 100);
			double tmpGte37lt40Avg = Math.round(tmpGte37lt40 / 10.0);
			tmpMaxCntItem.setGte37lt40(tmpGte37lt40Avg / 10.0);
			
			int tmpGte40Cnt = (int)(tmpMaxCntItem.getGte40() / years  * 100);
			double tmpGte40CntAvg = Math.round(tmpGte40Cnt / 10.0);
			tmpMaxCntItem.setGte40(tmpGte40CntAvg / 10.0);
			resultList.add(tmpMaxCntItem);
		}
		return resultList;
	}
	
}
