package com.spd.ws;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.spd.common.EleTypes;
import com.spd.tool.LogTool;

/**
 * 气候变化分析
 * @author Administrator
 *
 */
@Stateless
@Path("ClimChangeAnalystService")
public class ClimChangeAnalystService {
	
	/**
	 * 查询监测要素的结果值
	 * @param para 参数分为几类：1. 要素类型  2.检测时段 3.站，以及区域 
	 * @return
	 */
	@POST
	@Path("getAnalystDatas")
	@Produces("application/json")
	public Object getAnalystDatas(@FormParam("para") String para) {
		JSONObject jsonObject = null;
		String tableName = "";
		try {
			jsonObject = new JSONObject(para);
			tableName = EleTypes.getTableName(jsonObject.getString("EleType"));
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		return null;
	}
}
