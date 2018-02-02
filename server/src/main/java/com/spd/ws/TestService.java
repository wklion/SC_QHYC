package com.spd.ws;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.spd.common.ClimTime;
import com.spd.common.ClimTimeType;

@Stateless
@Path("TestService")
public class TestService {

	@POST
	@Path("testRange")
	@Produces("application/json")
	public Object testRange(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String startStr = "", endStr = "", climTypeStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date startDate = null, endDate = null;
		try {
			jsonObject = new JSONObject(para);
			startStr = jsonObject.getString("startStr");
			endStr = jsonObject.getString("endStr");
			startDate = sdf.parse(startStr);
			endDate = sdf.parse(endStr);
			climTypeStr = jsonObject.getString("climTypeStr");
			ClimTime clim = new ClimTime();
			ClimTimeType climType = ClimTimeType.getClimTimeType(climTypeStr);
			clim.getClimTimeByTimes(startDate, endDate, climType);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
