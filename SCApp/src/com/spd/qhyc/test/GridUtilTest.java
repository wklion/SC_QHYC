package com.spd.qhyc.test;

import java.util.Calendar;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.application.WorkspaceHelper;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.model.XNStation;
import com.spd.qhyc.util.GridUtil;
import com.spd.qhyc.util.StationUtil;
import com.mg.objects.Workspace;

/**
 * @作者:杠上花
 * @日期:2018年1月25日
 * @公司:spd
 * @说明:
*/
public class GridUtilTest {

	public static void main(String[] args) throws Exception {
		GridUtil gridUtil = new GridUtil();
		Workspace ws = WorkspaceHelper.getWorkspace();
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
		}
		StationUtil stationUtil = new StationUtil();
        List<XNStation> lsXNStation = stationUtil.GetXNSatation("",dpConn);
		//double[][] result = gridUtil.getMonthHosModeStationFromGrid(ws, 2, config, lsXNStation);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        gridUtil.getForecastModeStationData(ws, cal, 2, config, lsXNStation);
		System.out.println("over");
	}
	
}
