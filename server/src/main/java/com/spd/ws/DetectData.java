package com.spd.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.common.DatectDataSumAnomalyParam;
import com.spd.tool.LogTool;

/**
 * 数据监测，实现距平，累积距平，线性趋势（线性倾向率）、MK趋势（文档）、滑动平均、滑动t检验、MK突变检验、小波分析等
 * @author Administrator
 *
 */
@Stateless
@Path("DetectData")
public class DetectData {

	/**
	 * 累积距平分析
	 * @param para
	 * @return
	 */
	@POST
	@Path("sumAnomaly")
	@Produces("application/json")
	public Object sumAnomaly(@FormParam("para") String para) {
		List<DatectDataSumAnomalyParam> paramList = new ArrayList<DatectDataSumAnomalyParam>();
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(para);
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject itemJSONObject = jsonArray.getJSONObject(i);
				DatectDataSumAnomalyParam datectDataSumAnomalyParam = new DatectDataSumAnomalyParam();
				datectDataSumAnomalyParam.setAnomaly(itemJSONObject.getDouble("anomaly"));
				datectDataSumAnomalyParam.setValue(itemJSONObject.getDouble("value"));
				datectDataSumAnomalyParam.setYear(itemJSONObject.getInt("year"));
				if(itemJSONObject.has("yearsStr")) {
					datectDataSumAnomalyParam.setYearsStr(itemJSONObject.getString("yearsStr"));
				}
				paramList.add(datectDataSumAnomalyParam);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "参数错误，参数【" + para + "】，错误：" + e.getMessage();
		}
		return null;
	}
}
