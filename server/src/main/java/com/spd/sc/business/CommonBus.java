package com.spd.sc.business;

import java.util.List;

import org.springframework.web.context.ContextLoader;

import com.spd.sc.pojo.Station;
import com.spd.service.ICommon;

public class CommonBus {

	public List<Station> queryPenDiStations() {
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		List<Station> stationList = iCommon.queryPenDiStations();
		return stationList;
	}
}
