package com.spd.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.spd.shuiqi.GXShuiKuImpl;
import com.spd.tool.PropertiesUtil;

/**
 * 广西水情水库数据同步。数据每天有08,20, 每天更新两次。
 * @author xianchao
 *
 */
public class GXSQSKExecutor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		// 河流
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		Date date = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
		String startTime = sdf.format(date.getTime());
//		String startTime = "2015-11-10 08:00:00";
		//水库
		GXShuiKuImpl gxShuiKuImpl = new GXShuiKuImpl();
		gxShuiKuImpl.analyst(startTime, startTime);
	}

}
