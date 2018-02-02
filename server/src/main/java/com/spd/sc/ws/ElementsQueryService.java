package com.spd.sc.ws;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.common.EleTypes;
import com.spd.common.TimesParam;
import com.spd.common.TimesYearsParam;
import com.spd.sc.business.ElementsQueryBus;
import com.spd.sc.pojo.ElementsByTimesParam;
import com.spd.sc.pojo.ElementsByYearsParam;
import com.spd.tool.LogTool;

/**
 * 常规气象要素查询服务
 * @author Administrator
 *
 */
@Stateless
@Path("ElementsQueryService")
public class ElementsQueryService {

	/**
	 * 查询指定时间段内的结果序列
	 * @return
	 */
	@POST
	@Path("queryElementsByTimes")
	@Produces("application/json")
	public Object queryElementsByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			String startTime = jsonObject.getString("startTime");
			String endTime = jsonObject.getString("endTime");
			TimesParam timesParam = new TimesParam();
			timesParam.setStartTimeStr(startTime);
			timesParam.setEndTimeStr(endTime);
			String tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			ElementsByTimesParam elementsByTimesParam = new ElementsByTimesParam();
			elementsByTimesParam.setStation_Id_Cs(station_Id_Cs);
			elementsByTimesParam.setTableName(tableName);
			elementsByTimesParam.setTimesParam(timesParam);
			ElementsQueryBus elementsQueryBus = new ElementsQueryBus();
			Object result = elementsQueryBus.queryElementsByTimes(elementsByTimesParam);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 查询历年的要素，形成结果序列
	 * @param para
	 * @return
	 */
	@POST
	@Path("queryElementsByYears")
	@Produces("application/json")
	public Object queryElementsByYears(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			ElementsByYearsParam elementsByYearsParam = new ElementsByYearsParam();
			String station_Id_Cs = jsonObject.getString("station_Id_Cs");
			elementsByYearsParam.setStation_Id_Cs(station_Id_Cs);
			String tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
			elementsByYearsParam.setTableName(tableName);
			String startTime = jsonObject.getString("startTime");
			String endTime = jsonObject.getString("endTime");
			int startYear = jsonObject.getInt("startYear");
			int endYear = jsonObject.getInt("endYear");
			TimesParam timesParam = new TimesParam();
			timesParam.setStartTimeStr(startTime);
			timesParam.setEndTimeStr(endTime);
			TimesYearsParam timesYearsParam = new TimesYearsParam(timesParam, startYear, endYear);
			elementsByYearsParam.setTimesYearsParam(timesYearsParam);
			ElementsQueryBus elementsQueryBus = new ElementsQueryBus();
			Object result = elementsQueryBus.queryElementsByYears(elementsByYearsParam);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
}
