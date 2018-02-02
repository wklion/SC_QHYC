package com.spd.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.cq.impl.T_DataCompleteDaoImpl;
import com.spd.dao.cq.impl.T_tem_avgDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.LogTool;
import com.spd.tool.PropertiesUtil;

/**
 * 资料完整度统计
 * 1. 计算气温的资料数量
 * 2. 计算最新的更新日期（递归计算，知道找到有资料的日期）
 * @author Administrator
 *
 */
public class DataCompleteSync extends Thread {

	private String datetime = "";
	
	private T_tem_avgDaoImpl tem_avgDaoImpl = new T_tem_avgDaoImpl();

	private T_DataCompleteDaoImpl dataCompleteDaoImpl = new T_DataCompleteDaoImpl();
	
	public DataCompleteSync(String datetime) {
		this.datetime = datetime;
		LogTool.logger.info("数据完整度：" + datetime);
	}
	
	/**
	 * 计算资料个数
	 * @return
	 */
	public int queryDataCnt(String station_Id_C) {
		int cnt = tem_avgDaoImpl.getDataCntByStation(station_Id_C);
		return cnt;
	}
	
	/**
	 * 计算最新的资料更新时间
	 * @return
	 */
	public boolean isDataUpdated(String station_Id_C) {
		//1.计算datetime对应的日期有没有数据，有的话，就update，没有的话，就直接跳过
		String[] times = datetime.split("-");
		String column = "m" + times[1] + "d" + times[2];
		boolean isDataUpdate = tem_avgDaoImpl.isDataUpdateByTime(times[0], column, station_Id_C);
		return isDataUpdate;
	}
	
	public List<String> getAllStations() {
		List<String> stations = dataCompleteDaoImpl.getAllStations();
		return stations;
	}
	
	public void update(int id, String station_Id_C, int dataCount) {
		HashMap updateMap = new HashMap();
		updateMap.put("UpdateTime", datetime + " 00:00:00");
		updateMap.put("Station_Id_C", station_Id_C);
		updateMap.put("id", id);
		updateMap.put("DataCount", dataCount);
		List updateList = new ArrayList();
		updateList.add(updateMap);
		dataCompleteDaoImpl.updateDataList(updateList);
	}
	
	public void run() {
		LogTool.logger.info("数据完整度开始");
		//1. 获取所有站点
		List<String> stations = getAllStations();
		//2. 查询所有站的最新更新时间
		HashMap updateMap = dataCompleteDaoImpl.getExistData();
		//3. 循环查询最新数据，然后更新数据
		for(String station_Id_C : stations) {
			boolean isDataUpdated = isDataUpdated(station_Id_C);
			if(isDataUpdated) {
				int updateDataCnt = queryDataCnt(station_Id_C);
				//更新数据库
				int id = (Integer) updateMap.get(station_Id_C);
				update(id, station_Id_C, updateDataCnt);
			}
		}
		LogTool.logger.info("数据完整度结束");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing(); 
		Date date = new Date(System.currentTimeMillis() -  CommonConstant.DAYTIMES);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String datetime = sdf.format(date);
		DataCompleteSync dataCompleteSync = new DataCompleteSync(datetime);
		dataCompleteSync.start();
	}

}
