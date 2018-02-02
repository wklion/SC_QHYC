package com.spd.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.WepParam;
import com.spd.common.WepResult;
import com.spd.service.IHail;
import com.spd.service.IWep;

/**
 * 天气现象
 * @author Administrator
 *
 */
public class WepBus {
	
	public WepResult queryByTimes(WepParam wepParam) {
		WepResult wepResult = new WepResult();
		return wepResult;
	}
	
	/**
	 * weps进行过滤
	 * @param wepParam
	 * @param weps
	 * @return
	 */
	public List<WepResult> queryAllByTimes(WepParam wepParam, String weps) {
		IWep wepImpl = (IWep)ContextLoader.getCurrentWebApplicationContext().getBean("WepImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", wepParam.getTimesParam().getStartTimeStr());
		paramMap.put("endTime", wepParam.getTimesParam().getEndTimeStr());
		paramMap.put("Station_Id_Cs", wepParam.getStation_id_Cs());
		List<LinkedHashMap> list = wepImpl.queryAllByTimes(paramMap);
		List<WepResult> result = analyst(list, weps);
		return result;
	}
	
	/**
	 * 封装WepResult结果
	 * @param list
	 * @return
	 */
	private List<WepResult> analyst(List<LinkedHashMap> list, String filter) {
		List<WepResult> wepResultList = new ArrayList<WepResult>();
		for(int i = 0; i < list.size(); i++) {
			LinkedHashMap itemMap = list.get(i);
			WepResult wepResult = new WepResult();
			String station_Id_C = (String) itemMap.get("Station_Id_C");
			String station_Name = (String) itemMap.get("Station_Name");
			String datetime = (String) itemMap.get("datetime");
			String wep_Record = (String) itemMap.get("WEP_Record");
			List<WepResult> itemWepResultList = analystWepRecord(wep_Record, filter);
			if(itemWepResultList != null && itemWepResultList.size() == 1) {
				WepResult itemWepResult = itemWepResultList.get(0);
				itemWepResult.setStation_Id_C(station_Id_C);
				itemWepResult.setStation_Name(station_Name);
				itemWepResult.setDatetime(datetime);
				wepResultList.add(itemWepResult);
			} else if(itemWepResultList != null && itemWepResultList.size() > 1) {
				int markId = 0; // 标记是开始时间、结束时间的序列。默认为0，如果结果中有，则指向有的下标，如果没有的，则指向第一个
				for(int j = 0; j < itemWepResultList.size(); j++) {
					WepResult itemWepResult = itemWepResultList.get(j);
					String startTime = itemWepResult.getStartTime();
					if(startTime != null) {
						markId = j;
						break;
					}
				}
				WepResult itemWepResult = itemWepResultList.get(markId);
				itemWepResult.setStation_Id_C(station_Id_C);
				itemWepResult.setStation_Name(station_Name);
				itemWepResult.setDatetime(datetime);
				wepResultList.add(itemWepResult);
			}
		}
		return wepResultList;
	}
	
	private List<WepResult> analystWepRecord(String wepRecord, String filter) {
		//对于结果为0. 的这种情况，先就过滤掉
		String oriWepRecord = wepRecord; //原始数据 
		if("0.".equals(wepRecord)) {
			return null;
		}
		//处理分号，把全部的分号后的数据除掉
		while(wepRecord.indexOf(";") != -1) {
			int start = wepRecord.indexOf(";");
			int end = wepRecord.indexOf(",", start);
			if(end > start) {
				String temp = wepRecord.substring(0, start) + wepRecord.substring(end, wepRecord.length());
				wepRecord = temp;
			} else {
				wepRecord = wepRecord.substring(0, start);
			}
		}
		//判断wepRecord中是否包含filter，如果不包含的话，返回null
		boolean flag = false;
		if(filter != null && filter.length() > 0) {
			//做判断
			int index1 = wepRecord.indexOf(filter + ",");
			int index2 = wepRecord.indexOf(filter + " ");
			if(index1 == 0 || index2 == 0) {
				flag = true;
			} else {
				if(index1 != -1) {
					//找到了，但还不能是时间
					String str = null;
					try {
						str = wepRecord.substring(index1 - 1, index1);
					} catch(Exception e) {
						e.printStackTrace();
					}
					try {
						Integer.parseInt(str);
					} catch(Exception e) {
						//不是
						flag = true;
					}
				}
				
				if(index2 != -1) {
					//找到了，但还不能是时间
					String str = wepRecord.substring(index2 - 1, index2);
					try {
						Integer.parseInt(str);
					} catch(Exception e) {
						//不是
						flag = true;
					}
				}
			}
		} else {
			flag = true;
		}
		if(!flag) return null;
		List<WepResult> wepResultList = new ArrayList<WepResult>();
		String resultWepRecord = wepRecord.replace("(", "").replace(")", "");
		if(resultWepRecord != null && resultWepRecord.length() > 0) {
			resultWepRecord = resultWepRecord.substring(0, resultWepRecord.length() - 1); //去掉最后的.
			String[] records = resultWepRecord.split(",");
			for(int i = 0; i < records.length; i++) {
				WepResult wepResult = new WepResult();
				String item = records[i];
				if(item.split("\\s").length == 3) {
					//包含开始、结束时间
					String[] values = item.split("\\s");
					if(values[0].equals(filter)) {
						wepResult.setWepRecord(oriWepRecord);
						wepResult.setCode(values[0]);
						wepResult.setName("");//TODO 暂时还没实现
						wepResult.setStartTime(values[1].substring(0, 2) + ":" + values[1].substring(2, 4));
						wepResult.setEndTime(values[2].substring(0, 2) + ":" + values[2].substring(2, 4));
						wepResultList.add(wepResult);
					} else if(filter == null) {
						wepResult.setWepRecord(oriWepRecord);
						wepResult.setCode(filter);
						wepResult.setName("");//TODO 暂时还没实现
						wepResultList.add(wepResult);
					}
				} else {
					//不包含开始、结束时间
					if(item.equals(filter)) {
						wepResult.setWepRecord(wepRecord);
						wepResult.setCode(item);
						wepResultList.add(wepResult);
					} else if(null == filter) {
						wepResult.setWepRecord(wepRecord);
						wepResultList.add(wepResult);
					}
				}
			}
		}
		return wepResultList;
	}
}
