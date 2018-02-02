package com.spd.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.spd.common.CommonConstant;
import com.spd.service.IDisasterAlert;

/**
 * 灾害预警
 * @author Administrator
 *
 */
public class DisasterAlertBus {

	private IDisasterAlert iDisasterAlert = (IDisasterAlert)ContextLoader.getCurrentWebApplicationContext().getBean("DisasterAlert");
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public Object getCurrentAreaAlert() {
		HashMap paramMap = new HashMap();
		//TODO 为了防止遗漏，最好把当前天，前一天的都计算起来
		String forecastDate = sdf.format(new Date());
		paramMap.put("ForecastDate", forecastDate);
		List<Map> rainAreaResultList = iDisasterAlert.getCurrentContinueRainAreaAlert(paramMap);
		List<Map> highTmpResultList = iDisasterAlert.getCurrentHighTmpAreaAlert(paramMap);
		List<Map> lowTmpResultList = iDisasterAlert.getCurrentLowTmpAreaAlert(paramMap);
		//MCI区域
		List<Map> mciAreaResultList = iDisasterAlert.getCurrentMCIAreaAlert(paramMap);
		//头一天的
		forecastDate = sdf.format(new Date(System.currentTimeMillis() - CommonConstant.DAYTIMES));
		paramMap.put("ForecastDate", forecastDate);
		List<Map> rainAreaResultList2 = iDisasterAlert.getCurrentContinueRainAreaAlert(paramMap);
		List<Map> highTmpResultList2 = iDisasterAlert.getCurrentHighTmpAreaAlert(paramMap);
		List<Map> lowTmpResultList2 = iDisasterAlert.getCurrentLowTmpAreaAlert(paramMap);
		List<Map> mciAreaResultList2 = iDisasterAlert.getCurrentMCIAreaAlert(paramMap);
		
		rainAreaResultList = dispose(rainAreaResultList);
		highTmpResultList = dispose(highTmpResultList);
		lowTmpResultList = dispose(lowTmpResultList);
		rainAreaResultList2 = dispose(rainAreaResultList2);
		highTmpResultList2 = dispose(highTmpResultList2);
		lowTmpResultList2 = dispose(lowTmpResultList2);
		mciAreaResultList = dispose(mciAreaResultList);
		mciAreaResultList2 = dispose(mciAreaResultList2);
		
		List<Map> resultList = new ArrayList<Map>();
		if(rainAreaResultList != null) resultList.addAll(rainAreaResultList);
		if(highTmpResultList != null) resultList.addAll(highTmpResultList);
		if(lowTmpResultList != null) resultList.addAll(lowTmpResultList);
		if(rainAreaResultList2 != null) resultList.addAll(rainAreaResultList2);
		if(highTmpResultList2 != null) resultList.addAll(highTmpResultList2);
		if(lowTmpResultList2 != null) resultList.addAll(lowTmpResultList2);
		if(mciAreaResultList != null) resultList.addAll(mciAreaResultList);
		if(mciAreaResultList2 != null) resultList.addAll(mciAreaResultList2);
		return resultList;
	}
	
	private List<Map> dispose(List<Map> dataResultList) {
		//判断数据是否有效
		if(dataResultList != null && dataResultList.size() > 1) {
			return dataResultList;
		}
		if(dataResultList == null) {
			return null;
		}
		if(dataResultList.size() == 1) {
			Map dataMap = dataResultList.get(0);
			if(dataMap.containsKey("id")) {
				return dataResultList;
			} else {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 查询单站低温预警
	 * @param forecastDate
	 * @return
	 */
	public Object getLowTmpStationAlert(String forecastDate) {
		HashMap paramMap = new HashMap();
		paramMap.put("ForecastDate", forecastDate);
		List<Map> resultList = iDisasterAlert.getLowTmpStationAlert(paramMap);
		return resultList;
	}
	
	/**
	 * 查询单站高温预警
	 * @param forecastDate
	 * @return
	 */
	public Object getHighTmpStationAlert(String forecastDate) {
		HashMap paramMap = new HashMap();
		paramMap.put("ForecastDate", forecastDate);
		List<Map> resultList = iDisasterAlert.getHighTmpStationAlert(paramMap);
		return resultList;
	}
	
	/**
	 * 查询单站连阴雨预警
	 * @param forecastDate
	 * @return
	 */
	public Object getContinueRainStationAlert(String forecastDate) {
		HashMap paramMap = new HashMap();
		paramMap.put("ForecastDate", forecastDate);
		List<Map> resultList = iDisasterAlert.getContinueRainStationAlert(paramMap);
		return resultList;
	}
	
	/**
	 * 查询MCI区域预警
	 * @param forecastDate
	 * @return
	 */
	public Object getMCIAreaAlert(String forecastDate) {
		HashMap paramMap = new HashMap();
		paramMap.put("ForecastDate", forecastDate);
		List<Map> resultList = iDisasterAlert.getMCIAreaAlert(paramMap);
		return resultList;
	}
	
	/**
	 * 查询预报的数据
	 * @param forecastDate
	 * @return
	 */
	public Object getForecastByForecastTime(String forecastDate) {
		HashMap paramMap = new HashMap();
		paramMap.put("ForecastDate", forecastDate);
		List<Map> resultList = iDisasterAlert.getForecastByForecastTime(paramMap);
		return resultList;
	}
}
