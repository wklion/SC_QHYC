package com.spd.efs.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.spd.efs.pojo.Rain;

public class RainDaoImpl implements RainDao{

	public List<Rain> getRainTemAvgData() {
		List<Rain> result = new ArrayList<Rain>();
		String sql  = "select t.STATION_ID_C as stationNum,t.STATION_NAME as stationName, truncate(AVG(t.RAIN),2) as rain from t_surf_chn_mul_day_main t GROUP BY t.STATION_ID_C";
		List<Record> list = Db.find(sql);
		for(Record record : list){
			Rain rain = new Rain();
			rain.setStationNum(record.getStr("stationNum"));
			rain.setStationName(record.getStr("stationName"));
			rain.setRain_avg(record.getDouble("rain"));
			rain.setRain_anomaly(1);
			result.add(rain);
			
		}
		return result;
	}
    
	public List<Rain> getPlateauRainSeasonStartTimeData() {
		List<Rain> result = new ArrayList<Rain>();
		String sql = "select t.STATION_ID_C as stationNum,t.STATION_NAME as stationName,t.RAIN as rain,t.`YEAR` as year,t.MON as month,t.`DAY` as day from t_surf_chn_mul_day t where t.mon BETWEEN 4 and 6 and t.`DAY` >=21 GROUP BY t.STATION_ID_C";
		List<Record> list = Db.find(sql);
		for(Record record : list){
			Rain rain = new Rain();
			rain.setStationNum(record.getStr("stationNum"));
			rain.setStationName(record.getStr("stationName"));
			rain.setRain_avg(record.getDouble("rain"));
			rain.setRain_anomaly(1);
			result.add(rain);
			
		}
		return result;
	}

	public List<Rain> getAllStationData() {
		List<Rain> result = new ArrayList<Rain>();
		String sql = "select t.STATION_ID_C as stationNum from t_rain t GROUP BY t.STATION_ID_C";
		List<Record> list = Db.find(sql);
		for(Record record : list){
			Rain rain = new Rain();
			rain.setStationNum(record.getStr("stationNum"));
			result.add(rain);
			
		}
		return result;
	}

}
