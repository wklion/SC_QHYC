package com.spd.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.DataCompleteResult;
import com.spd.service.IDataComplete;
import com.spd.util.CommonUtil;

/**
 * 数据完整度查询
 * @author Administrator
 *
 */
public class DataCompleteBus {

	/**
	 * 查询数据完整度
	 * @return
	 */
	public List<DataCompleteResult> getDataComplete() {
		List<DataCompleteResult> dataCompleteResultList = new ArrayList<DataCompleteResult>();
		//1. 查询数据库
		IDataComplete dataCompleteImpl = (IDataComplete)ContextLoader.getCurrentWebApplicationContext().getBean("DataCompleteImpl");
		HashMap paramMap = new HashMap();
		List<LinkedHashMap> resultList = dataCompleteImpl.getDataComplete(paramMap);
		if(resultList != null && resultList.size() > 0) {
			for(int i = 0; i < resultList.size(); i++) {
				DataCompleteResult dataCompleteResult = new DataCompleteResult();
				HashMap itemMap = resultList.get(i);
				int missCnt = ((Long) itemMap.get("missCnt")).intValue();
				double missRate = ((BigDecimal) itemMap.get("missRate")).doubleValue();
				int predictCnt = ((Long) itemMap.get("predictCnt")).intValue();
				int realCnt = (Integer) itemMap.get("realCnt");
				String startTime = (String) itemMap.get("StartTime");
				String updateTime = (String) itemMap.get("UpdateTime");
				String station_Id_C = (String) itemMap.get("Station_Id_C");
				dataCompleteResult.setMissCnt(missCnt);
				dataCompleteResult.setMissRate(missRate);
				dataCompleteResult.setPredictCnt(predictCnt);
				dataCompleteResult.setRealCnt(realCnt);
				dataCompleteResult.setStartTime(startTime);
				dataCompleteResult.setUpdateTime(updateTime);
				dataCompleteResult.setStation_Id_C(station_Id_C);
				dataCompleteResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
				dataCompleteResultList.add(dataCompleteResult);
			}
		}
		//2. 统计结果，计算缺测率
		return dataCompleteResultList;
	}
}
