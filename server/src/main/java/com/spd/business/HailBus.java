package com.spd.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.common.HailSequenceResult;
import com.spd.common.TimesParam;
import com.spd.service.IHail;
import com.spd.util.CommonUtil;

/**
 * 冰雹
 * @author Administrator
 *
 */
public class HailBus {

	public List<HailSequenceResult> queryByTimes(TimesParam timesParam) {
		List<HailSequenceResult> hailSequenceResultList = new ArrayList<HailSequenceResult>();
		IHail hailImpl = (IHail)ContextLoader.getCurrentWebApplicationContext().getBean("HailImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("startTime", timesParam.getStartTimeStr());
		paramMap.put("endTime", timesParam.getEndTimeStr());
		List<LinkedHashMap> resultList = hailImpl.queryByTimes(paramMap);
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap item = resultList.get(i);
			HailSequenceResult hailSequenceResult = new HailSequenceResult();
			hailSequenceResult.setArea((String) item.get("area"));
			hailSequenceResult.setDate((String) item.get("datetime"));
			hailSequenceResult.setDiameter((Double)item.get("diameter"));
			hailSequenceResult.setEndTime((String) item.get("endTime"));
			hailSequenceResult.setStartTime((String) item.get("startTime"));
			String station_Id_C = (String) item.get("Station_Id_C");
			hailSequenceResult.setStation_Id_C(station_Id_C);
			hailSequenceResult.setStation_Name(CommonUtil.getInstance().stationNameMap.get(station_Id_C));
			hailSequenceResultList.add(hailSequenceResult);
		}
		return hailSequenceResultList;
	}
}
