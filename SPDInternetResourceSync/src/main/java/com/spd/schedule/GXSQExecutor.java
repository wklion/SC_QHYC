package com.spd.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.spd.dao.impl.GXShuiQingDao;
import com.spd.shuiqi.GXShuiKuImpl;
import com.spd.shuiqi.GXShuiQingImpl;
import com.spd.tool.PropertiesUtil;

/**
 * 广西水情数据同步。
 * @author xianchao
 *
 */
public class GXSQExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		// 河流
		GXShuiQingImpl qxShuiQingImpl = new GXShuiQingImpl();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		Date date = new Date();
		String startTime = sdf.format(date.getTime() - 60 * 60 * 1000);
		//只取当前一个小时的数据
		String endTime = sdf.format(date);
		qxShuiQingImpl.analyst(startTime, endTime);
//		for(int i=0; i<20; i++) {
//		GXShuiQingDao gxShuiQingDao = new GXShuiQingDao();
//		HashMap<String, Object> existedData = gxShuiQingDao.getExistRiverRegimenInfo(startTime, endTime);
//	}
		//水库
//		GXShuiKuImpl gxShuiKuImpl = new GXShuiKuImpl();
//		gxShuiKuImpl.analyst("2015-11-06 08:00:00", "2015-11-06 08:00:00");
	}

}
