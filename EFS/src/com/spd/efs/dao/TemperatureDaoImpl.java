package com.spd.efs.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.spd.efs.pojo.Temperature;

public class TemperatureDaoImpl implements TemperatureDao {

	public List<Temperature> getAllTemData() {
		List<Temperature> result = new ArrayList<Temperature>();
		String sql = "select t.STATION_ID_C as stationNum,t.STATION_NAME as stationName,t.TEM_AVG as tem_avg_1,AVG(t.TEM_AVG) as tem_avg_2 from t_surf_chn_mul_day_main t GROUP BY STATION_ID_C";
		List<Record> list = Db.find(sql);
		for(Record record:list){
			Temperature  tem = new Temperature();
			tem.setStationNum(record.getStr("stationNum"));
			tem.setStationName(record.getStr("stationName"));
			tem.setTem_avg(record.getDouble("tem_avg_2"));
			tem.setTem_anomaly((record.getDouble("tem_avg_2"))-(record.getDouble("tem_avg_1")));
			result.add(tem);
		}
		return  result;
	}
	/**
	 * 初始化温度柱状图表数据
	 * @return
	 */
	public List<Temperature> initColumnarData(){
		List<Temperature> result = new ArrayList<Temperature>();
		String sql = "select AVG(t.TEM_AVG) as tem_avg from t_surf_chn_mul_day_main t GROUP BY STATION_ID_C";
		List<Record> list = Db.find(sql);
		for(Record record:list){
			Temperature  tem = new Temperature();
			tem.setTem_avg(record.getDouble("tem_avg"));
			result.add(tem);
		}
		return  result;
	}

}
