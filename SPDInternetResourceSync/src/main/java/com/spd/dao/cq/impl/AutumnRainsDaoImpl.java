package com.spd.dao.cq.impl;

import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.spd.dao.BaseDao;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;

/**
 * 秋雨相关
 * @author Administrator
 *
 */
public class AutumnRainsDaoImpl extends BaseDao {

	private String tableName;
	
	private String INSERTDATA  = "insert into t_autumnrains (%s) values (%s) ";

	private String DELETEDATA = "delete from  t_autumnrains where year = ";
	
	private String QUERYSURFCHNMULSTRUCT  = "select * from t_autumnrains where 1=2";
	
	private String columnName = "Pre";
	
	
	public HashMap<String, Object> getExistDataTmp(String datetime) {
		//mysql
		String query = "select id, Station_Id_C, date_format(datetime, '%Y-%m-%d') as datetime from " + tableName + " where datetime = '" + datetime + "'";		
		List list = query(getConn(), query, null);
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				String key = tempMap.get("Station_Id_C") + "_" + tempMap.get("datetime");
				hashMap.put(key, tempMap.get("id"));
			}
		}
		return hashMap;
	}
	
	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List getMDRainsCntByTimes(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String year = startTime.substring(0, 4);
		String items = CommonTool.createItemStrByRange(startDate, endDate);
		String[] itemArray = items.split(",");
		String sql = "select Station_Id_C, " + items + " from t_pre_time_2020 where Station_Id_C like '5%' and year = " + year;
		List list = query(getConn(), sql, null);
		//把结果处理成 cnt, datetime的形式
		long start = startDate.getTime();
		long end = endDate.getTime();
		HashMap timeMap = new HashMap();
		for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
			timeMap.put(sdf.format(new Date(i)), 0);
		}
		LinkedHashMap<String, Integer> cntMap = new LinkedHashMap<String, Integer>();
		for(int i = 0; i < list.size(); i++) {
			HashMap dataMap = (HashMap) list.get(i);
			for(int j = 0; j < itemArray.length; j++) {
				String item = itemArray[j];
				String dateTimeStr = CommonTool.createTimeStrByColumn(item, year);
				Double itemValue = (Double) dataMap.get(item);
				if(itemValue != null && itemValue >= 0.1 && itemValue <= 999) {
					if(cntMap.get(dateTimeStr) == null) {
						cntMap.put(dateTimeStr, 0);
					}
					cntMap.put(dateTimeStr, cntMap.get(dateTimeStr) + 1);
				}
			}
		}
		List resultList = new ArrayList();
		for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
			String timeStr = sdf.format(new Date(i));
			if(cntMap.containsKey(timeStr)) {
				HashMap itemMap = new HashMap();
				itemMap.put("datetime", timeStr);
				itemMap.put("cnt", cntMap.get(timeStr));
				resultList.add(itemMap);
			} else {
				HashMap itemMap = new HashMap();
				itemMap.put("datetime", timeStr);
				itemMap.put("cnt", 0);
				resultList.add(itemMap);
			}
		}
		return resultList;
	}
	/**
	 * 根据开始结束时间,查询每天的降水站数
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List getRainsCntByTimes(String startTime, String endTime) {
		String query = "select count(1) as cnt, date_format(datetime, '%Y-%m-%d') as datetime from t_rainstorm2020 where pre >= 0.1 and datetime >= '" + startTime + "' and datetime <= '" + endTime + "' and station_id_C like '5%' group by datetime";
		List list = query(getConn(), query, null);
		//处理一遍，把没有数据的天加上去
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//把中间缺失的补全
		List resultList = new ArrayList();
		for(long time = startDate.getTime(); time <= endDate.getTime(); time += CommonConstant.DAYTIMES) {
			String tempTime = sdf.format(new Date(time));
			boolean flag = true;
			for(int i = 0; i < list.size(); i++) {
				HashMap dataMap = (HashMap) list.get(i);
				String datetime = (String) dataMap.get("datetime");
				if(tempTime.equals(datetime)) {
					resultList.add(dataMap);
					flag = false;
					break;
				}
			}
			if(flag) {
				HashMap dataMap = new HashMap();
				dataMap.put("datetime", tempTime);
				dataMap.put("cnt", 0L);
				resultList.add(dataMap);
			}
		}
		return resultList;
	}
	
	/**
	 * 根据开始结束时间,查询每天的降水量
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List getRainsDaySumtByTimes(String startTime, String endTime) {
		String query = "select sum(pre) as pre, date_format(datetime, '%Y-%m-%d') as datetime from t_rainstorm2020 where pre >= 0.1 and datetime >= '" + startTime + "' and datetime <= '" + endTime + "' and station_id_C like '5%' group by datetime";
		List list = query(getConn(), query, null);
		return list;
	}
	
	/**
	 * 查询一段时间以内，日雨量总和
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Double getRainsSumtByTimes(String startTime, String endTime) {
		String query = " select sum(pre) / 34 as pre from t_rainstorm2020 where datetime >= '" + startTime + "' and datetime <= '" + endTime + "' and station_id_C like '5%'";
		List list = query(getConn(), query, null);
		if(list != null && list.size() == 1) {
			HashMap tempMap = (HashMap) list.get(0);
			Double sum = (Double) tempMap.get("pre");
			return sum;
		}
		return null;
	}
	
	public List queryIndex(String type) {
		String query = "select * from t_autumnrainsconstant where type = '" + type + "'";
		List list = query(getConn(), query, null);
		return list;
	}
	/**
	 * 判断该年的数据是否已经存在
	 * @param year
	 */
	public boolean isExistByYear(int year) {
		String query = "select * from t_autumnrains where year = " + year;
		List list = query(getConn(), query, null);
		if(list != null & list.size() > 0) {
			return true;
		}
		return false;
	}
	
	public void insert(List dataList) {
		insertBatch(INSERTDATA, dataList, getSurfChnMulResultSetMetaData());
	}
	
	public void update(List dataList, int year) {
		//删除,暂时不管事务的问题。
		boolean flag = update(DELETEDATA + year);
		if(flag) {
			insert(dataList);
		}
	}
	public ResultSetMetaData getSurfChnMulResultSetMetaData() {
		return getTableStruct(getConn(), QUERYSURFCHNMULSTRUCT);
	}
	
}
