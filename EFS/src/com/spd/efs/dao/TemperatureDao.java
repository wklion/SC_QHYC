package com.spd.efs.dao;

import java.util.List;

import com.spd.efs.pojo.Temperature;

public interface TemperatureDao {
	
	/**
	 * 获取前20项温度数据
	 * @return
	 */
	public List<Temperature>  getAllTemData();
	
	public List<Temperature> initColumnarData();

}
