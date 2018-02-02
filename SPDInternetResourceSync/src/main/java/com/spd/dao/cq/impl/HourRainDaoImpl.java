package com.spd.dao.cq.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.BaseDao;

/**
 * 小时降水
 * @author Administrator
 *
 */
public class HourRainDaoImpl extends BaseDao {

//	private BufferedWriter bw;
//	
//	public HourRainDaoImpl() {
//		try {
//			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("e:/hourrain.csv"), true)));   
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public void queryDataByItem(String station_Id_C, String station_Name, String key, int limit) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("e:/hourrain.csv"), true)));   
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		String sql = "select " + key + ", Station_Id_C, date_format(datetime, '%Y-%m-%d %T') as datetime from t_mwshourrain where Station_Id_C = '" + station_Id_C + "' order by " + key + " desc limit " + limit;
		List resultList = query(getConn(), sql, null);
		if(resultList != null || resultList.size() >= 0) {
			for(int i = 0; i < resultList.size(); i++) {
				HashMap dataMap = (HashMap) resultList.get(i);
				Double rain= (Double) dataMap.get(key);
				String datetime = (String) dataMap.get("datetime");
				String result = station_Id_C + "," + station_Name + "," + datetime + "," + rain + "," + key + "," + (i + 1);
				try {
					bw.write(result);
					bw.write("\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
