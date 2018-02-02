package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.spd.config.CommonConfig;
import com.spd.dao.cq.impl.AWSHourDataDaoImpl;
import com.spd.dao.cq.impl.MSDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 历史自动站资料的入库
 * @author Administrator
 *
 */
public class HisAWSHourDataSync {

	public static void main(String[] args) {
		AWSHourDataDaoImpl awsHourDataDaoImpl = new AWSHourDataDaoImpl();
		MSDaoImpl msDaoImpl = new MSDaoImpl();
		PropertiesUtil.loadSysCofing();
		Set<String> stationSet = awsHourDataDaoImpl.getStations();
		String startTimeStr = "1991-01-01";
		String endTimeStr = "2016-11-05";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null, endDate = null;
		try {
			startDate = sdf.parse(startTimeStr);
			endDate = sdf.parse(endTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long start = startDate.getTime();
		long end = endDate.getTime();
		for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
			Date date = new Date(i);
			String strDate = sdf.format(date);
			String startDateTime = strDate + " 00:00:00";
			String endDateTime = strDate + " 23:00:00";
			System.out.println(strDate);
			List dataList = msDaoImpl.getHourRainData(startDateTime, endDateTime, stationSet);
			awsHourDataDaoImpl.insertValue(dataList);
		}
//		for(int i = 1991; i <= 2016; i++) {
//			String startDateTime = i + "-01-01 00:00:00";
//			String endDateTime = i + "-12-31 00:00:00";
//			List dataList = msDaoImpl.getHourRainData(startDateTime, endDateTime, stationSet);
//			awsHourDataDaoImpl.insertValue(dataList);
//		}
	}

}
