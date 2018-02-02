package com.spd.grid.ws;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.mg.objects.Workspace;
import com.spd.grid.domain.Application;
import com.spd.grid.domain.StationVal;
import com.spd.grid.service.Forcast;
import com.spd.grid.service.HosUtil;
import com.spd.grid.tool.Common;
import com.spd.grid.tool.LogTool;

@Stateless
@Path("ClimaticPredictionService")
public class ClimaticPredictionService {
		/**
		 * @作者:wangkun
		 * @日期:2017年7月21日
		 * @修改日期:2017年7月21日
		 * @参数:
		 * @返回:
		 * @说明:动力预测
		 */
		@POST
	   	@Path("DLForcast")
	   	@Produces("application/json")
		public Object DLForcast(@FormParam("para") String para){
				Workspace ws=Application.m_workspace;
				JSONObject jo=null;
				Calendar cal=Calendar.getInstance();
				String yucefun="";//预测方法
				String elementid="";//要素id
				String type="";//返回类型
				String cidu="";//尺度有年季月
				//1、解析参数
				try {
						jo = new JSONObject(para);
						yucefun=jo.getString("yucefun");
						elementid=jo.getString("elementid");
						type=jo.getString("type");
						cidu=jo.getString("cidu");
						Date dt=Common.yyyy_MM.parse(jo.getString("datetime"));
						cal.setTime(dt);
				} catch (Exception ex) {
						LogTool.logger.error("DLForcast()--解析参数出错!");
				}
				//2、处理数据
				
				
				Forcast forcast=new Forcast();
				List<StationVal> result=forcast.Downscaling(ws, elementid, cal, cidu);
				if(result==null)
						return null;
				if(type.equals("freal")){//真实值
						int month=cal.get(Calendar.MONTH)+1;
						HosUtil hu=new HosUtil();
						List<StationVal> lsHos=hu.GetHosData(elementid, month);
						int size=result.size();
						int hosSize=lsHos.size();
						for(int i=0;i<size;i++){
								StationVal sv=result.get(i);
								String stationNum=sv.getStationNum();
								for(int j=0;j<hosSize;j++){
										StationVal svHos=lsHos.get(j);
										if(stationNum.equals(svHos.getStationNum())){
												double val=sv.getValue();
												double hosVal=svHos.getValue();
												double newVal=hosVal+val*hosVal/100;
												sv.setValue(newVal);
										}
								}
						}
				}
				return result;
		}
}
