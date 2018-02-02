package com.spd.sc.ws;

import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.spd.sc.business.CommonBus;


@Stateless
@Path("CommonService")
public class CommonService {

	/**
	 * 查询四川盆地的所有站
	 * @return
	 */
	@POST
	@Path("queryPenDiStations")
	@Produces("application/json")
	public Object queryPenDiStations() {
		CommonBus commonBus = new CommonBus();
		Object result = commonBus.queryPenDiStations();
		return result;
	}
}
