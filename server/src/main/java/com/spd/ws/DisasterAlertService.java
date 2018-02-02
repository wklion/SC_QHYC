package com.spd.ws;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.spd.business.DisasterAlertBus;
import com.spd.tool.LogTool;

/**
 * 气候灾害预警
 * @author Administrator
 *
 */
@Stateless
@Path("DisasterAlertService")
public class DisasterAlertService {
	
	private DisasterAlertBus disasterAlertBus = new DisasterAlertBus();
	
	/**
	 * 查询当前日期的气候预警
	 * @param para
	 * @return
	 */
	@POST
	@Path("getCurrentAreaAlert")
	@Produces("application/json")
	public Object getCurrentAreaAlert(@FormParam("para") String para) {
		Object result = disasterAlertBus.getCurrentAreaAlert();
		return result;
	}
	
	/**
	 * 查询对应的单站预警
	 * @param para
	 * @return
	 */
	@POST
	@Path("getStationAlert")
	@Produces("application/json")
	public Object getStationAlert(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String type = "", forecastDate = "";
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			type = jsonObject.getString("type");
			forecastDate = jsonObject.getString("ForecastDate");
			if("LowTmp".equalsIgnoreCase(type)) {
				result = disasterAlertBus.getLowTmpStationAlert(forecastDate);
			} else if("HighTmp".equalsIgnoreCase(type)) {
				result = disasterAlertBus.getHighTmpStationAlert(forecastDate);
			} else if("ContinueRain".equalsIgnoreCase(type)) {
				result = disasterAlertBus.getContinueRainStationAlert(forecastDate);
			} else if("MCIArea".equals(type)) {
				result = disasterAlertBus.getMCIAreaAlert(forecastDate);
			}
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 根据预报时间，查询预报数据
	 * @param para
	 * @return
	 */
	@POST
	@Path("getForecastByForecastTime")
	@Produces("application/json")
	public Object getForecastByForecastTime(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String forecastDate = "";
		Object result = null;
		try {
			jsonObject = new JSONObject(para);
			forecastDate = jsonObject.getString("ForecastDate");
			result = disasterAlertBus.getForecastByForecastTime(forecastDate);
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
}
