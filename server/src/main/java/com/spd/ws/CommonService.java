package com.spd.ws;

import javax.ejb.Stateless;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.CommonBus;
import com.spd.tool.LogTool;

/**
 * 通用的一些服务
 * @author Administrator
 *
 */
@Stateless
@Path("CommonService")
public class CommonService {

	/**
	 * 查询站点
	 * @param para
	 * @return
	 */
	@POST
	@Path("getStationsByLevel")
	@Produces("application/json")
	public Object getStationsByLevel(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		int level = -1;
		try {
			jsonObject = new JSONObject(para);
			level = jsonObject.getInt("level");
			CommonBus commonBus = new CommonBus();
			Object result = commonBus.getStationsByLevel(level);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	/**
	 * 获取全部的国家城市站
	 * @param para
	 * @return
	 */
	@POST
	@Path("getAllNationCityStations")
	@Produces("application/json")
	public Object getAllNationCityStations(@FormParam("para") String para) {
		CommonBus commonBus = new CommonBus();
		Object result = commonBus.getAllNationCityStations();
		return result;
	}
	
	/**
	 * 获取全部的站点，自动站和区域站
	 * @param para
	 * @return
	 */
	@POST
	@Path("getAllStations")
	@Produces("application/json")
	public Object getAllStations(@FormParam("para") String para) {
		CommonBus commonBus = new CommonBus();
		Object result = commonBus.getAllStations();
		return result;
	}
	
	/**
	 * 根据登录用户，获取对应的站点
	 * @param para
	 * @return
	 */
	@POST
	@Path("getStationsByUser")
	@Produces("application/json")
	public Object getStationsByUser(@FormParam("para") String para,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ServletContext servletContext  = request.getSession().getServletContext().getContext("/SPDUser");
		Object o = servletContext.getAttribute("session");
		HttpSession session = (HttpSession) o;
		String userName = (String) session.getAttribute("user");
		CommonBus commonBus = new CommonBus();
		Object result = commonBus.getStationsByUser(userName);
		return result;
	}
}
