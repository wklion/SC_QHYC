package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.spd.dao.BaseDao;

/**
 * 连阴雨预警区域统计表
 * @author Administrator
 *
 */
public class AlertContinuousRainsAreaDaoImpl extends BaseDao {

	private String INSERTDATA = "insert into t_continuerainareaalert (%s) values (%s) ";

	private String UPDATEDATA = "update t_continuerainareaalert set StartTime = ?, EndTime = ?, ForecastDate = ?, SumStations = ?, SumPre = ?, PreDays = ? where id = ? ";
	
	private String QUERYSURFCHNMULSTRUCT = "select * from t_continuerainareaalert where 1=2";
	
	
	
	public void insert(List dataList, String forecastDate) {
		int id = getExistDataId(forecastDate);
		if(id == -1) {
			insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
		} else {
			if(dataList != null && dataList.size() > 0) {
				Map map = (Map) dataList.get(0);
				map.put("id", id);
				List dataList2 = new ArrayList();
				dataList2.add(map);
				updateBatch2(UPDATEDATA, dataList2, getSurfChnMulResultSetMetaData(), new String[]{"StartTime", "EndTime", "ForecastDate", "SumStations", "SumPre", "PreDays"});
			}
		}
	}
	
	public int getExistDataId(String startTime) {
		String query = "select id from t_continuerainareaalert where StartTime = '" + startTime + "'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			Map map = (Map) list.get(0);
			int id = (Integer) map.get("id");
			return id;
		} else {
			return -1;
		}
	}
	
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
}
