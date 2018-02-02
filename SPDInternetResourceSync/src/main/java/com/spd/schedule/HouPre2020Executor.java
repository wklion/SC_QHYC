package com.spd.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.spd.dao.cq.impl.HouPreResultDaoImpl;
import com.spd.dao.cq.impl.T_pre_time_2020DaoImpl;
import com.spd.tool.PropertiesUtil;

/**
 * 候降水
 * @author Administrator
 *
 */
public class HouPre2020Executor {

	public void sync(int year) {
		//1. 查询每年的所有站的降水资料。
		List dataList = queryData(year);
		//2. 按站进行分组，进行统计平均值（5-10,1-12月）
		List result0510List = dispose(dataList, 5, 10, year, "0510");
		List result0112List = dispose(dataList, 1, 12, year, "0112");
		//3. 结果入库
		HouPreResultDaoImpl houPreResultDaoImpl = new HouPreResultDaoImpl();
		houPreResultDaoImpl.insert(result0510List);
		houPreResultDaoImpl.insert(result0112List);
	}
	
	public List queryData(int year) {
		T_pre_time_2020DaoImpl pre_time_2020DaoImpl = new T_pre_time_2020DaoImpl();
		List resultList = pre_time_2020DaoImpl.getDataByYear(year);
		return resultList;
	}
	
	public List dispose(List dataList, int startMon, int endMon, int year, String type) {
		List resultList = new ArrayList();
		for(int i = 0; i < dataList.size(); i++) {
			Double sumPre = 0.0;
			HashMap dataMap = (HashMap) dataList.get(i);
			String station_Id_C = (String) dataMap.get("Station_Id_C");
			Iterator it = dataMap.keySet().iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				if(!key.startsWith("m")) {
					continue;
				}
				int month = Integer.parseInt(key.substring(1, 3));
				if(month >= startMon && month <= endMon) {
					Double pre = (Double) dataMap.get(key);
					if(pre != null && pre >=0 && pre < 999) {
						sumPre += pre;
					}
				}
			}
			HashMap resultMap = new HashMap();
			resultMap.put("Station_Id_C", station_Id_C);
			resultMap.put("type", type);
			resultMap.put("year", year);
			if("0510".equals(type)) {
				resultMap.put("HouPre", sumPre / 36);
			} else {
				resultMap.put("HouPre", sumPre / 72);
			}
			resultList.add(resultMap);
		}
		return resultList;
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		HouPre2020Executor houPre2020Executor = new HouPre2020Executor();
		for(int i = 1983; i <= 2010; i++) {
			System.out.println(i);
			houPre2020Executor.sync(i);
		}
	}
}
