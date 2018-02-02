package com.spd.grid.test;

import com.google.gson.Gson;
import com.mg.objects.Datasource;
import com.mg.objects.Workspace;
import com.spd.grid.funModel.DLForcastParam;
import com.spd.grid.ws.ForcastService;

/**
 * @作者:wangkun
 * @日期:2017年6月26日
 * @公司:spd
 * @说明:
*/
public class EOFCCATest {
	private static String root=Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1);
	public static void main(String[] args) throws Exception{
		ForcastService fs = new ForcastService();
		DLForcastParam dlForcastParam = new DLForcastParam();
		dlForcastParam.setElementID("prec");
		dlForcastParam.setMakeDate("201709");
		String[] forecastDate = {"201710","201711","201712"};
		dlForcastParam.setForcastDate(forecastDate);
		Gson gson = new Gson();
		String param = gson.toJson(dlForcastParam);
		//fs.EOFCCA(param);
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年6月26日
	 * @修改日期:2017年6月26日
	 * @参数:
	 * @返回:
	 * @说明:读模式数据,读16年11月数据
	 */
	public static void readModelData(Workspace ws){
		String file = "E:/SC/EFS/data/20161101.atm.Z3.20161101-20161231_prs0500_member.nc";
		String strJson = String.format("{\"Type\":\"netCDF\",\"Alias\":\"%s\",\"Server\":\"%s\"}", "yue",file);
		Datasource ds = ws.OpenDatasource(strJson);
		
	}
	public static void getStations(){
		System.out.println(root);
	}
}
