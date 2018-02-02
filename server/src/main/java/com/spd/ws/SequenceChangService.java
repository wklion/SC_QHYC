package com.spd.ws;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.spd.business.SequenceChangBus;
import com.spd.common.SequenceChangeParam;
import com.spd.common.TimesParam;
import com.spd.tool.LogTool;

/**
 * 连续变化
 * @author Administrator
 *
 */
@Stateless
@Path("SequenceChangService")
public class SequenceChangService {

	/**
	 * 统计时间段内的连续变化
	 * @param para
	 * @return
	 */
	@POST
	@Path("sequenceChangByTimes")
	@Produces("application/json")
	public Object sequenceChangByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			SequenceChangeParam sequenceChangeParam = new SequenceChangeParam();
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			sequenceChangeParam.setTimesParam(timesParam);
			String station_Id_C = jsonObject.getString("station_Id_C");
			sequenceChangeParam.setStation_Id_C(station_Id_C);
			String statisticsType = jsonObject.getString("statisticsType");
			sequenceChangeParam.setStatisticsType(statisticsType);
			String climTimeTypeStr = jsonObject.getString("climTimeType");
			sequenceChangeParam.setClimTimeType(climTimeTypeStr);
			int standardStartYear = jsonObject.getInt("standardStartYear");
			int standardEndYear = jsonObject.getInt("standardEndYear");
			sequenceChangeParam.setStandardStartYear(standardStartYear);
			sequenceChangeParam.setStandardEndYear(standardEndYear);
			String eleTypesStr = jsonObject.getString("eleTypes");
			sequenceChangeParam.setEleTypes(eleTypesStr);
			SequenceChangBus sequenceChangBus = new SequenceChangBus();
			Object result = sequenceChangBus.sequenceChangByTimes(sequenceChangeParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 统计时间段内的按站连续变化
	 * @param para
	 * @return
	 */
	@POST
	@Path("sequenceChangeStationsByTimes")
	@Produces("application/json")
	public Object sequenceChangeStationsByTimes(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			SequenceChangeParam sequenceChangeParam = new SequenceChangeParam();
			TimesParam timesParam = new TimesParam();
			String startTimeStr = jsonObject.getString("startTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setEndTimeStr(endTimeStr);
			sequenceChangeParam.setTimesParam(timesParam);
			String station_Id_C = jsonObject.getString("station_Id_C");
			sequenceChangeParam.setStation_Id_C(station_Id_C);
			String statisticsType = jsonObject.getString("statisticsType");
			sequenceChangeParam.setStatisticsType(statisticsType);
			String climTimeTypeStr = jsonObject.getString("climTimeType");
			sequenceChangeParam.setClimTimeType(climTimeTypeStr);
			int standardStartYear = jsonObject.getInt("standardStartYear");
			int standardEndYear = jsonObject.getInt("standardEndYear");
			sequenceChangeParam.setStandardStartYear(standardStartYear);
			sequenceChangeParam.setStandardEndYear(standardEndYear);
			String eleTypesStr = jsonObject.getString("eleTypes");
			sequenceChangeParam.setEleTypes(eleTypesStr);
			SequenceChangBus sequenceChangBus = new SequenceChangBus();
			Object result = sequenceChangBus.sequenceChangeStationsByTimes(sequenceChangeParam);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
}
