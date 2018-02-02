package com.spd.ws;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.CommonStatisticsFilter;
import com.spd.business.DataCompleteBus;
import com.spd.common.DataCompleteResult;
import com.spd.tool.LogTool;

/**
 * 数据完整度相关服务查询
 * @author Administrator
 *
 */
@Stateless
@Path("DataCompleteService")
public class DataCompleteService {

	/**
	 * 查询数据完整度
	 * @param para
	 * @return
	 */
	@POST
	@Path("getDataComplete")
	@Produces("application/json")
	public Object getDataComplete(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			if(para != null) {
				jsonObject = new JSONObject(para);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		DataCompleteBus dataCompleteBus = new DataCompleteBus();
		List<DataCompleteResult> dataCompleteResultList = dataCompleteBus.getDataComplete();
		if(jsonObject == null) {
			return dataCompleteResultList;
		}
		Set<String> station_Id_CSet = getStationSets(jsonObject);
		CommonStatisticsFilter commonStatisticsFilter = new CommonStatisticsFilter(station_Id_CSet);
		List<DataCompleteResult> dataCompleteResultList2 = commonStatisticsFilter.filterDataCompleteResult(dataCompleteResultList);
		return dataCompleteResultList2;
	}
	
	private Set<String> getStationSets(JSONObject jsonObject) {
		Set<String> station_Id_CSet = new LinkedHashSet<String>();
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			String station_Id_Cs;
			try {
				station_Id_Cs = jsonObject.getString("station_Id_Cs");
				String[] station_id_CArray = station_Id_Cs.split(",");
				for(int i = 0; i < station_id_CArray.length; i++) {
					station_Id_CSet.add(station_id_CArray[i]);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return station_Id_CSet;
	}
}
