package com.spd.grid.ws;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.grid.domain.Station;
import com.spd.grid.station.StationUtil;
import com.spd.weathermap.util.CommonTool;
import com.spd.weathermap.util.LogTool;

/**
 * @作者:wangkun
 * @日期:2016年12月27日
 * @公司:spd
 * @说明:站点服务
 */
@Stateless
@Path("StationService")
public class StationService {
	@POST
	@Path("GetSCStation")
	@Produces("application/json")
	public Object GetSCStation(@FormParam("para") String para){
		StationUtil su=new StationUtil();
		List<Station> lsResult=su.GetSCSatation();
		return lsResult;
	}
	@POST
	@Path("GetXNStation")
	@Produces("application/json")
	public Object GetXNStation(@FormParam("para") String para) throws Exception{
		StationUtil su=new StationUtil();
		List lsResult=su.GetXNSatation("");
		return lsResult;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年8月30日
	 * @修改日期:2017年8月30日
	 * @参数:
	 * @返回:
	 * @说明:根据区划获取站点
	 */
	@POST
	@Path("getStationByBlock")
	@Produces("application/json")
	public Object getStationByBlock(@FormParam("para") String para){
		JSONObject jsonObject;
		String blockName = "";
		try {
			jsonObject = new JSONObject(para);
			blockName = CommonTool.getJSONStr(jsonObject, "blockname");
		} catch (JSONException e) {
			LogTool.logger.error("获取参数出错!");
		}
		StationUtil su=new StationUtil();
		List<Station> lsResult=su.getStationByBlock(blockName);
		return lsResult;
	}
}
