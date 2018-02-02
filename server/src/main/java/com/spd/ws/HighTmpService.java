package com.spd.ws;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.spd.business.HighTmpBus;
import com.spd.common.FirstDayResult;
import com.spd.common.HighTmpDaysResult;
import com.spd.common.HighTmpSequence;
import com.spd.common.HighTmpTotal;
import com.spd.common.TimesParam;
import com.spd.tool.LogTool;

/**
 * 高温统计
 * @author Administrator
 *
 */
@Stateless
@Path("HighTmpService")
public class HighTmpService {

	/**
	 * 按时间段统计高温
	 * @param para
	 * @return
	 */
	@POST
	@Path("highTmpByTimes")
	@Produces("application/json")
	public Object highTmpByTimes(@FormParam("para") String para) {
		TimesParam timesParam = new TimesParam();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(para);
			String startTimeStr = jsonObject.getString("startTimeStr");
			String endTimeStr = jsonObject.getString("endTimeStr");
			timesParam.setStartTimeStr(startTimeStr);
			timesParam.setEndTimeStr(endTimeStr);
//			String type = jsonObject.getString("type");
			HighTmpBus highTmpBus = new HighTmpBus();
			HighTmpDaysResult result = highTmpBus.highTmpByTimes(timesParam, "AWS");
			HighTmpDaysResult result2 = filterExtResult(result, jsonObject);
			return result2;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.getLogger(this.getClass()).error(methodName, e);
			return "错误，参数【" + para + "】，错误：" + e.getMessage();
		}
	}
	
	private HighTmpDaysResult filterExtResult(HighTmpDaysResult highTmpDaysResult1, JSONObject jsonObject) {
		boolean flag = jsonObject.has("station_Id_Cs");
		if(flag) {
			HighTmpDaysResult highTmpDaysResult2 = new HighTmpDaysResult();
			LinkedList<String> stationList = new LinkedList<String>();
			try {
				String station_Id_Cs = (String) jsonObject.get("station_Id_Cs");
				String[] station_id_CItems = station_Id_Cs.split(",");
				for(int i = 0; i < station_id_CItems.length; i++) {
					stationList.add(station_id_CItems[i]);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//过滤
			List<HighTmpSequence> highTmpSequenceList2 = new ArrayList<HighTmpSequence>();
			List<HighTmpTotal> highTmpTotalList2 = new ArrayList<HighTmpTotal>();
			for(int i = 0; i < stationList.size(); i++) {
				String station_Id_C = stationList.get(i);
				List<HighTmpSequence> highTmpSequenceList = highTmpDaysResult1.getHighTmpSequenceList();
				for(int j = 0; j < highTmpSequenceList.size(); j++) {
					HighTmpSequence itemHighTmpSequence = highTmpSequenceList.get(j);
					String itemStation_Id_C = itemHighTmpSequence.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						highTmpSequenceList2.add(itemHighTmpSequence);
						break;
					}
				}
				List<HighTmpTotal> highTmpTotalList = highTmpDaysResult1.getHighTmpTotalList();
				for(int j = 0; j < highTmpTotalList.size(); j++) {
					HighTmpTotal itemHighTmpTotal = highTmpTotalList.get(j);
					String itemStation_Id_C = itemHighTmpTotal.getStation_Id_C();
					if(itemStation_Id_C.equals(station_Id_C)) {
						highTmpTotalList2.add(itemHighTmpTotal);
						break;
					}
				}
			}
			highTmpDaysResult2.setHighTmpSequenceList(highTmpSequenceList2);
			highTmpDaysResult2.setHighTmpTotalList(highTmpTotalList2);
			return highTmpDaysResult2;
		} else {
			return highTmpDaysResult1;
		}
	}
}
