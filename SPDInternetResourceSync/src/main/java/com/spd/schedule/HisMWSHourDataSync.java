package com.spd.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.spd.dao.cq.impl.AWSHourDataDaoImpl;
import com.spd.dao.cq.impl.MWSInsertHourDataDaoImpl;
import com.spd.dao.cq.impl.MWSQueryHourDataDaoImpl;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 历史自动站资料的入库
 * @author Administrator
 *
 */
public class HisMWSHourDataSync {

	public static void main(String[] args) {
		MWSQueryHourDataDaoImpl mwsHourDataDaoImpl = new MWSQueryHourDataDaoImpl();
		MWSInsertHourDataDaoImpl mwsInserttHourDataDaoImpl = new MWSInsertHourDataDaoImpl();
		AWSHourDataDaoImpl awsHourDataDaoImpl = new AWSHourDataDaoImpl();
		PropertiesUtil.loadSysCofing();
		Set<String> stationSet = awsHourDataDaoImpl.getMWSStations();
//		String startStr = "20030602000000";
		String startStr = "20140602000000";
		String endStr = "20150914080000";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date startDate = sdf.parse(startStr);
			Date endDate = sdf.parse(endStr);
			long start = startDate.getTime();
			long end = endDate.getTime();
			for(long i = start; i <= end; i+= CommonConstant.DAYTIMES) {
				String iStr = sdf.format(new Date(i));
				System.out.println(iStr);
				List dataList = mwsHourDataDaoImpl.getDataMCI(iStr, stationSet);
				mwsInserttHourDataDaoImpl.insertValue(dataList);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		for(int i = 1991; i <= 2016; i++) {
//			String startDateTime = i + "0101000000";
//			String endDateTime = i + "1231000000";
//			List dataList = mwsHourDataDaoImpl.getDataMCI(startDateTime, endDateTime, stationSet);
//			mwsInserttHourDataDaoImpl.insertValue(dataList);
//		}
	}

}
