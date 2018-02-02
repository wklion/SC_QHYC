package com.spd.qhyc.util;

import java.util.Calendar;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.app.StationVal;

/**
 * @作者:杠上花
 * @日期:2018年1月22日
 * @公司:spd
 * @说明:
*/
public class DBDataHelper {
	/**
	 * @作者:杠上花
	 * @日期:2018年1月22日
	 * @修改日期:2018年1月22日
	 * @参数:
	 * @返回:
	 * @说明:预测数据入库
	 */
	public void forecastDataInsert(Calendar calMake,Calendar calForecast,DruidPooledConnection dpConn,List<StationVal> lsStationVal){
		String strMakeDate = DateUtil.format("yyyyMM", calMake);
		int forecastYear = calForecast.get(Calendar.YEAR);
	}
}
