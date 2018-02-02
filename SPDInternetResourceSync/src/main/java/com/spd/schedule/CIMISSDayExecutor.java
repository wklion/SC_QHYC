package com.spd.schedule;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.cimiss.CIMISSRest;
import com.spd.dao.cq.impl.CLOCovHourDaoImpl;
import com.spd.dao.cq.impl.CQAWSStation;
import com.spd.dao.cq.impl.FogDaoImpl;
import com.spd.dao.cq.impl.HailDaoImpl;
import com.spd.dao.cq.impl.HighTmpDaoImpl;
import com.spd.dao.cq.impl.LowTmpDaoImpl;
import com.spd.dao.cq.impl.SnowDaoImpl;
import com.spd.dao.cq.impl.Surf_chn_mul_dayDao;
import com.spd.dao.cq.impl.T_RainStormDaoImpl;
import com.spd.dao.cq.impl.T_pre_time_0808DaoImpl;
import com.spd.dao.cq.impl.T_pre_time_0820DaoImpl;
import com.spd.dao.cq.impl.T_pre_time_2008DaoImpl;
import com.spd.dao.cq.impl.T_pre_time_2020DaoImpl;
import com.spd.dao.cq.impl.T_prs_avgDaoImpl;
import com.spd.dao.cq.impl.T_rhu_avgDaoImpl;
import com.spd.dao.cq.impl.T_sshDaoImpl;
import com.spd.dao.cq.impl.T_tem_avgDaoImpl;
import com.spd.dao.cq.impl.T_tem_maxDaoImpl;
import com.spd.dao.cq.impl.T_tem_minDaoImpl;
import com.spd.dao.cq.impl.T_vis_minDaoImpl;
import com.spd.dao.cq.impl.T_win_s_2mi_avgDaoImpl;
import com.spd.dao.cq.impl.ThundDaoImpl;
import com.spd.dao.cq.impl.WepDaoImpl;
import com.spd.dao.cq.impl.WinAvgHourDaoImpl;
import com.spd.dao.cq.impl.WinInstMaxDaoImpl;
import com.spd.pojo.RecordConfig;
import com.spd.tool.CommonConstant;
import com.spd.tool.CommonTool;
import com.spd.tool.PropertiesUtil;
/**
 * 从CIMISS中获取日值统计资料。
 */
public class CIMISSDayExecutor {

	private static Map<String, String> columnMap = new HashMap<String, String>();

	private static Map<String, String> hourColumnMap = new HashMap<String, String>();
	
	public static void init() {

		columnMap.put("Admin_Code_CHN","varchar(20)");
		columnMap.put("Alti","double");
		columnMap.put("Aur","varchar(10)");
		columnMap.put("AUR_OTime","varchar(10)");
		columnMap.put("City","varchar(50)");
		columnMap.put("CLO_Cov_Avg","double");
		columnMap.put("CLO_Cov_Low_Avg","double");
		columnMap.put("Cnty","varchar(50)");
		columnMap.put("Datetime","datetime");
		columnMap.put("Day","int(11)");
		columnMap.put("Dew","varchar(10)");
		columnMap.put("DrSnow","varchar(10)");
		columnMap.put("DrSnow_OTime","varchar(10)");
		columnMap.put("DuWhr","varchar(10)");
		columnMap.put("EICE","varchar(10)");
		columnMap.put("EICED_NS","double");
		columnMap.put("EICED_WE","double");
		columnMap.put("EICET_NS","double");
		columnMap.put("EICET_WE","double");
		columnMap.put("EICEW_NS","double");
		columnMap.put("EICEW_WE","double");
		columnMap.put("EVP","double");
		columnMap.put("EVP_Big","double");
		columnMap.put("FlDu","varchar(10)");
		columnMap.put("FlDu_OTime","varchar(10)");
		columnMap.put("FlSa","varchar(10)");
		columnMap.put("FlSa_OTime","varchar(10)");
		columnMap.put("Fog","varchar(10)");
		columnMap.put("Fog_OTime","varchar(10)");
		columnMap.put("Frost","varchar(10)");
		columnMap.put("FRS_1st_Bot","double");
		columnMap.put("FRS_1st_Top","double");
		columnMap.put("FRS_2nd_Bot","double");
		columnMap.put("FRS_2nd_Top","double");
		columnMap.put("GaWIN","varchar(10)");
		columnMap.put("GaWIN_OTime","varchar(10)");
		columnMap.put("Glaze","varchar(10)");
		columnMap.put("GLAZE_OTime","varchar(10)");
		columnMap.put("GSS","varchar(10)");
		columnMap.put("GST_Avg","double");
		columnMap.put("GST_Avg_10cm","double");
		columnMap.put("GST_Avg_15cm","double");
		columnMap.put("GST_Avg_160cm","double");
		columnMap.put("GST_Avg_20cm","double");
		columnMap.put("GST_Avg_320cm","double");
		columnMap.put("GST_Avg_40cm","double");
		columnMap.put("GST_Avg_5cm","double");
		columnMap.put("GST_Avg_80cm","double");
		columnMap.put("GST_Max","double");
		columnMap.put("GST_Max_Otime","varchar(10)");
		columnMap.put("GST_Min","double");
		columnMap.put("GST_Min_OTime","varchar(10)");
		columnMap.put("Hail","varchar(10)");
		columnMap.put("HAIL_OTime","varchar(10)");
		columnMap.put("Haze","varchar(10)");
		columnMap.put("ICE","varchar(10)");
		columnMap.put("IcePri","varchar(10)");
		columnMap.put("Lat","double");
		columnMap.put("LGST_Avg","double");
		columnMap.put("LGST_Max","double");
		columnMap.put("LGST_Max_OTime","varchar(10)");
		columnMap.put("LGST_Min","double");
		columnMap.put("LGST_Min_OTime","varchar(10)");
		columnMap.put("Lit","varchar(10)");
		columnMap.put("Lon","double");
		columnMap.put("Mist","varchar(10)");
		columnMap.put("Mon","int(11)");
		columnMap.put("PRE_Max_1h","double");
		columnMap.put("PRE_OTime","varchar(10)");
		columnMap.put("PRE_Time_0808","double");
		columnMap.put("PRE_Time_0820","double");
		columnMap.put("PRE_Time_2008","double");
		columnMap.put("PRE_Time_2020","double");
		columnMap.put("Province","varchar(50)");
		columnMap.put("PRS_Avg","double");
		columnMap.put("PRS_Max","double");
		columnMap.put("PRS_Max_OTime","varchar(10)");
		columnMap.put("PRS_Min","double");
		columnMap.put("PRS_Min_OTime","varchar(10)");
		columnMap.put("PRS_Sea_Avg","double");
		columnMap.put("PRS_Sensor_Alti","double");
		columnMap.put("Rain","varchar(10)");
		columnMap.put("REP_CORR_ID","int(11)");
		columnMap.put("RHU_Avg","double");
		columnMap.put("RHU_Min","double");
		columnMap.put("RHU_Min_OTIME","varchar(10)");
		columnMap.put("SaSt","varchar(10)");
		columnMap.put("SaSt_OTime","varchar(10)");
		columnMap.put("SCO","varchar(10)");
		columnMap.put("Smoke","varchar(10)");
		columnMap.put("Snow","varchar(10)");
		columnMap.put("SnowSt","varchar(10)");
		columnMap.put("SnowSt_OTime","varchar(10)");
		columnMap.put("Snow_Depth","double");
		columnMap.put("Snow_OTime","varchar(10)");
		columnMap.put("Snow_PRS","double");
		columnMap.put("SoRi","varchar(10)");
		columnMap.put("SoRi_OTime","varchar(10)");
		columnMap.put("Squa","varchar(10)");
		columnMap.put("SQUA_OTime","varchar(10)");
		columnMap.put("SSH","double");
		columnMap.put("Station_Id_C","varchar(20)");
		columnMap.put("Station_Id_d","varchar(20)");
		columnMap.put("Station_levl","varchar(20)");
		columnMap.put("Station_Name","varchar(50)");
		columnMap.put("Sunrist_Time","varchar(10)");
		columnMap.put("Sunset_Time","varchar(10)");
		columnMap.put("TEM","double");
		columnMap.put("TEM_Avg","double");
		columnMap.put("TEM_Max","double");
		columnMap.put("TEM_Max_OTime","varchar(10)");
		columnMap.put("TEM_Min","double");
		columnMap.put("TEM_Min_OTime","varchar(10)");
		columnMap.put("Thund","varchar(10)");
		columnMap.put("THUND_OTime","varchar(10)");
		columnMap.put("Tord","varchar(10)");
		columnMap.put("Tord_OTime","varchar(10)");
		columnMap.put("Town","varchar(50)");
		columnMap.put("VAP_Avg","double");
		columnMap.put("VIS_Min","double");
		columnMap.put("VIS_Min_OTime","varchar(10)");
		columnMap.put("WEP_Record","varchar(10)");
		columnMap.put("WEP_Sumary","varchar(500)");
		columnMap.put("WIN_D","double");
		columnMap.put("WIN_D_Avg_2mi_C","double");
		columnMap.put("WIN_D_INST_Max","double");
		columnMap.put("WIN_D_S_Max","double");
		columnMap.put("WIN_S","double");
		columnMap.put("WIN_S_10mi_Avg","double");
		columnMap.put("WIN_S_2mi_Avg","double");
		columnMap.put("WIN_S_Inst_Max","double");
		columnMap.put("WIN_S_INST_Max_OTime","varchar(10)");
		columnMap.put("WIN_S_Max","double");
		columnMap.put("WIN_S_Max_OTime","varchar(10)");
		columnMap.put("Year","int(11)");
		columnMap.put("HAIL_Diam_Max", "double");

		hourColumnMap.put("Station_Name", "");
		hourColumnMap.put("Station_Id_C","varchar(20)");
		hourColumnMap.put("Station_Id_d","varchar(20)");
		hourColumnMap.put("Station_levl","varchar(20)");
		hourColumnMap.put("Station_Name","varchar(50)");
		hourColumnMap.put("Datetime","datetime");
		hourColumnMap.put("Lat","double");
		hourColumnMap.put("Lon","double");
		hourColumnMap.put("CLO_Cov", "int");
		hourColumnMap.put("CLO_Cov_Low", "int");
		hourColumnMap.put("WIN_D_Avg_2mi", "int");
		hourColumnMap.put("WIN_S_Avg_2mi", "double");
	}
	
	public static String getCIMISSData(String timeStr, String codes) {
		String url = "http://10.194.89.55/cimiss-web/api?userId=BCCD_QHZX_PJS&pwd=pjs20160106&interfaceId=getSurfEleInRegionByTimeRange";
		url += "&dataCode=SURF_CHN_MUL_DAY&elements=";
		Set<String> keySet = columnMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			if("HAIL_Diam_Max".equals(key)) {
				continue;
			}
			url = url + key + ",";
		}
		url += "&timeRange=[" + timeStr + "," + timeStr + "]&adminCodes=" + codes + "&dataFormat=json";
//		System.out.println(url);
		CIMISSRest cimissRest = new CIMISSRest();
		String result = cimissRest.callCIMISS(url);
		return result;
	}
	/**
	 * 冰雹
	 * @param timeStr
	 * @return
	 */
	public static String getCIMISSHailData(String timeStr) {
		String url = "http://10.194.89.55/cimiss-web/api?userId=BCCD_QHZX_PJS&pwd=pjs20160106&interfaceId=getSurfEleInRegionByTime&dataCode=SURF_CHN_WSET_FTM&times=" + timeStr + "&adminCodes=500000&elements=Province,City,Cnty,Town,Datetime,Station_Id_C,Station_Name,HAIL_Diam_Max&dataFormat=json";
		System.out.println(url);
		CIMISSRest cimissRest = new CIMISSRest();
		String result = cimissRest.callCIMISS(url);
		return result;
	}
	
	/**
	 * 同步降雪资料
	 * @param result
	 */
	public void syncSnow(String result, String timeStr) {
		CIMISSRest cimissRest = new CIMISSRest();
		SnowDaoImpl snowDao = new SnowDaoImpl();
		HashMap<String, Object> existData = snowDao.getExistSnow(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		List dataList = new ArrayList();
		if(resultList == null || resultList.size() == 0) return;
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("Year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String snow = (String) tempMap.get("Snow");
			String snow_OTime = (String) tempMap.get("Snow_OTime");
			String gSS = (String) tempMap.get("GSS");
			String station_Name = (String) tempMap.get("Station_Name");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			Double snow_Depth = (Double) tempMap.get("Snow_Depth");
			String datetimeStr = (String) tempMap.get("Datetime");
			if((gSS != null && !"".equals(gSS) && !"0".equals(gSS) && !"999999".equals(gSS) && !"999999.0".equals(gSS)) ||
					(snow != null && !"".equals(snow) && !"0".equals(snow) && !"999999".equals(snow) && !"999999.0".equals(snow)) 
					//|| (snow_Depth != null && !"".equals(snow_Depth) && !"0".equals(snow_Depth) && !"999999".equals(snow_Depth) && !"999999.0".equals(snow_Depth))
					|| (snow_Depth != null && snow_Depth != 0 && snow_Depth != 999999)) {
				String key = (String) tempMap.get("Station_Id_C") + "_" + datetimeStr.substring(0, 10);
				if(existData.containsKey(key)) {
					continue;
				}
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("year", year);
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("Station_Name", station_Name);
				mapData.put("Station_Id_d", Station_Id_d);
				mapData.put("Snow_OTime", snow_OTime);
				mapData.put("Lon", lon);
				mapData.put("Lat", lat);
				mapData.put("Lat", lat);
				if(snow_Depth == 999990) {
					snow_Depth = 0.1;//微量，处理成0.1
				}
				mapData.put("Snow_Depth", snow_Depth);
				if(snow != null && !snow.equals("")) {
					mapData.put("Snow", Integer.parseInt(snow));
				}
				if(gSS != null && !"".equals(gSS)) {
					mapData.put("GSS", Integer.parseInt(gSS));
				}
				mapData.put("datetime", datetimeStr);
				dataList.add(mapData);
			}
		}
		snowDao.insertSnowValue(dataList);
	}
	
	/**
	 * 雾同步
	 * @param result
	 * @param timeStr
	 */
	public void syncFog(String result, String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CIMISSRest cimissRest = new CIMISSRest();
		FogDaoImpl fogDao = new FogDaoImpl();
		HashMap<String, Object> existData = fogDao.getExistFog(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		if(resultList == null) {
			return;
		}
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("Year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String station_Name = (String) tempMap.get("Station_Name");
//			Fog, Fog_OTime, VIS_Min, VIS_Min_OTime, RHU_Avg, RHU_Min, RHU_Min_OTIME, Mist, WEP_Record
			String wep_Record = (String) tempMap.get("WEP_Record");
			wep_Record = wep_Record.replaceAll("\\r", "").replaceAll("\\n", "");
//			System.out.println(wep_Record);
//			String fogStr = (String) tempMap.get("Fog");
//			String mistStr = (String) tempMap.get("Mist");
			String fogMath = ".*?\\D42\\D.*?";
			String mistMath = ".*?\\D10\\D.*?";
			
			Double fog = 0.0, mist = 0.0;
			if(wep_Record.matches(fogMath)) {
				fog = 1.0;
			}
			if(wep_Record.matches(mistMath)) {
				mist = 1.0;
			}
			if(fog == 0.0 && mist == 0.0) {
				continue;
			}
//			Double fog = Double.parseDouble(fogStr);
//			Double mist = Double.parseDouble(mistStr);
			
//			if(fogStr == null || "".equals(fogStr)  || "999999".equals(fogStr)) {
//				if(mistStr == null || "".equals(mistStr)  || "999999".equals(mistStr)) {
//					continue;
//				}
//			}
			
//			Double fog = Double.parseDouble(fogStr);
//			Double mist = Double.parseDouble(mistStr);
//			if(0 == fog || fog > 9999) {
//				if(0 == mist || mist > 9999) {
//					continue;
//				}
//			}
			String fog_OTime = (String) tempMap.get("Fog_OTime");
			Double vis_Min = (Double) tempMap.get("VIS_Min");
			String vis_Min_OTime = (String) tempMap.get("VIS_Min_OTime");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			Double rhu_Avg = (Double) tempMap.get("RHU_Avg");
			Double rhu_Min = (Double) tempMap.get("RHU_Min");
			String rhu_Min_OTIME = (String) tempMap.get("RHU_Min_OTIME");
			String datetime = (String) tempMap.get("Datetime");
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
				continue;
			}
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("Year", year);
			mapData.put("Station_Id_C", station_Id_C);
			mapData.put("Station_Name", station_Name);
			mapData.put("Station_Id_d", Station_Id_d);
			mapData.put("Fog", fog);
			mapData.put("Mist", mist);
			mapData.put("Fog_OTime", fog_OTime);
			mapData.put("Lon", lon);
			mapData.put("Lat", lat);
			mapData.put("Lat", lat);
			mapData.put("VIS_Min", vis_Min);
			mapData.put("VIS_Min_OTime", vis_Min_OTime);
			mapData.put("RHU_Avg", rhu_Avg);
			mapData.put("RHU_Min", rhu_Min);
			mapData.put("RHU_Min_OTIME", rhu_Min_OTIME);
			mapData.put("datetime", datetime);
			dataList.add(mapData);
		}
//		System.out.println("dataList.size:" + dataList.size());
//		System.out.println("before insertFogValue");
		fogDao.insertFogValue(dataList);
//		System.out.println("after insertFogValue");
	}
	/**
	 * 大风同步
	 * @param result
	 * @param timeStr
	 */
	public void syncWinInstMax(String result, String timeStr) {
		CIMISSRest cimissRest = new CIMISSRest();
		WinInstMaxDaoImpl winInstMaxDao = new WinInstMaxDaoImpl();
		HashMap<String, Object> existData = winInstMaxDao.getExistWinAvg(Integer.parseInt(timeStr.substring(0, 4)),
				Integer.parseInt(timeStr.substring(0, 4)));
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		if(resultList == null) return;
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			Map<String, Object> mapData = new HashMap<String, Object>();
			HashMap tempMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			mapData.put("Station_Name", (String) tempMap.get("Station_Name"));
			mapData.put("Province", (String) tempMap.get("Province"));
			mapData.put("City", (String) tempMap.get("City"));
			mapData.put("Cnty", (String) tempMap.get("Cnty"));
			mapData.put("Town", (String) tempMap.get("Town"));
			mapData.put("Station_Id_C", (String) tempMap.get("Station_Id_C"));
			mapData.put("Station_Id_d", (String) tempMap.get("Station_Id_d"));
			mapData.put("Station_levl", (String) tempMap.get("Station_levl"));
			mapData.put("Admin_Code_CHN", (String) tempMap.get("Admin_Code_CHN"));
			String datetime = (String) tempMap.get("Datetime");
			mapData.put("Datetime", datetime);
			String key = station_Id_C + "_" + datetime;
			mapData.put("Lat", (Double) tempMap.get("Lat"));
			mapData.put("Lon", (Double) tempMap.get("Lon"));
			mapData.put("Alti", (Double) tempMap.get("Alti"));
			mapData.put("Year", (Integer) tempMap.get("Year"));
			Double win_S_Inst_Max = (Double) tempMap.get("WIN_S_Inst_Max");
			if(win_S_Inst_Max == null || win_S_Inst_Max > CommonConstant.MAXINVALID || win_S_Inst_Max < CommonConstant.MININVALID) {
				continue;
			}
			//风向需要处理
			Double win_D_INST_Max = (Double) tempMap.get("WIN_D_INST_Max");
			if(win_D_INST_Max != 999999 && win_D_INST_Max > 999000) {
				win_D_INST_Max = win_D_INST_Max - 999000;
			}
			mapData.put("WIN_D_INST_Max", win_D_INST_Max);
			//级别需要处理
			if(win_S_Inst_Max >= 13.9 && win_S_Inst_Max < 20.8) {
				mapData.put("Level", 1);
			} else if(win_S_Inst_Max >= 20.8 && win_S_Inst_Max < 28.5) {
				mapData.put("Level", 2);
			} else if(win_S_Inst_Max >= 28.5) {
				mapData.put("Level", 3);
			} else {
				continue;
			}
			mapData.put("WIN_S_Inst_Max", (Double) tempMap.get("WIN_S_Inst_Max"));
			if(!existData.containsKey(key)) {
				dataList.add(mapData);
			}
		}
		winInstMaxDao.insertWinValue(dataList);
	}
	
	/**
	 * 高温同步
	 * @param result
	 * @param timeStr
	 */
	public void syncTmpMax(String result, String timeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CIMISSRest cimissRest = new CIMISSRest();
		HighTmpDaoImpl highTmpDao = new HighTmpDaoImpl();
		HashMap<String, Object> existData = highTmpDao.getExistMaxTmp(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		if(resultList == null) {
			return;
		}
		List dataList = new ArrayList();
		List updateDataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("Year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String station_Name = (String) tempMap.get("Station_Name");
//			year Datetime MaxTmp
			Double tmpMax = (Double) tempMap.get("TEM_Max");
			
			if(null == tmpMax || tmpMax < 30 || tmpMax > 9999 ||  tmpMax < -999) {
				continue;
			}
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			String datetime = (String) tempMap.get("Datetime");
			String tem_Max_OTime = (String) tempMap.get("TEM_Max_OTime");
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("TEM_Max", tmpMax);
				mapData.put("TEM_Max_OTime", tem_Max_OTime);
				mapData.put("id", existData.get(key));
				updateDataList.add(mapData);
			} else {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("Year", year);
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("Station_Name", station_Name);
				mapData.put("Station_Id_d", Station_Id_d);
				mapData.put("Lon", lon);
				mapData.put("Lat", lat);
				mapData.put("Lat", lat);
				mapData.put("TEM_Max", tmpMax);
				mapData.put("TEM_Max_OTime", tem_Max_OTime);
				mapData.put("datetime", datetime);
				dataList.add(mapData);
			}
		}
		highTmpDao.insertMaxTmpValue(dataList);
		highTmpDao.update(updateDataList);
	}
	
	/**
	 * 雷暴
	 * @param result
	 * @param timeStr
	 */
	public void syncThund(String result, String timeStr) {
		CIMISSRest cimissRest = new CIMISSRest();
		ThundDaoImpl thundDaoImpl = new ThundDaoImpl();
		HashMap<String, Object> existData = thundDaoImpl.getExistThund(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		if(resultList == null) {
			return;
		}
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("Year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String station_Name = (String) tempMap.get("Station_Name");
//			year Datetime MaxTmp
			Integer thund = null;
			try {
				thund = Integer.parseInt((String) tempMap.get("Thund"));
			} catch(Exception e) {
				continue;
			}
			if(null == thund || 0 == thund || 999999 == thund) {
				continue;
			}
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			String datetime = (String) tempMap.get("Datetime");
			String thund_OTime = (String) tempMap.get("THUND_OTime");
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
				continue;
			}
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("Year", year);
			mapData.put("Station_Id_C", station_Id_C);
			mapData.put("Station_Name", station_Name);
//			mapData.put("Station_Id_d", Station_Id_d);
			mapData.put("Lon", lon);
			mapData.put("Lat", lat);
			mapData.put("Lat", lat);
			mapData.put("Thund", thund);
			mapData.put("THUND_OTime", thund_OTime);
			mapData.put("datetime", datetime);
			dataList.add(mapData);
		}
		thundDaoImpl.insertThundValue(dataList);
	}
	
	/**
	 * 低温
	 * @param result
	 * @param timeStr
	 */
	public void syncLowTmp(String result, String timeStr) {
		CIMISSRest cimissRest = new CIMISSRest();
		LowTmpDaoImpl lowTmpDao = new LowTmpDaoImpl();
		HashMap<String, Object> existData = lowTmpDao.getExistMinTmp(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		if(resultList == null) {
			return;
		}
		List dataList = new ArrayList();
		List updateDataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("Year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String station_Name = (String) tempMap.get("Station_Name");
//			year Datetime MaxTmp
			Double tmpMax = (Double) tempMap.get("TEM_Min");
			
			if(null == tmpMax || tmpMax > 10 || tmpMax > 9999 || tmpMax < -999) {
				continue;
			}
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			String datetime = (String) tempMap.get("Datetime");
			String tem_Max_OTime = (String) tempMap.get("TEM_Min_OTime");
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("TEM_Min", tmpMax);
				mapData.put("TEM_Min_OTime", tem_Max_OTime);
				mapData.put("id", existData.get(key));
				updateDataList.add(mapData);
			} else {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("Year", year);
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("Station_Name", station_Name);
				mapData.put("Station_Id_d", Station_Id_d);
				mapData.put("Lon", lon);
				mapData.put("Lat", lat);
				mapData.put("Lat", lat);
				mapData.put("TEM_Min", tmpMax);
				mapData.put("TEM_Min_OTime", tem_Max_OTime);
				mapData.put("datetime", datetime);
				dataList.add(mapData);
			}
		}
		lowTmpDao.insertMinTmpValue(dataList);
		lowTmpDao.update(updateDataList);
	}
	
	/**
	 * 暴雨
	 * @param result
	 * @param timeStr
	 */
	public void syncRainStorm(String result, String timeStr, String tableName) {
		CIMISSRest cimissRest = new CIMISSRest();
		T_RainStormDaoImpl rainStormDaoImpl = new T_RainStormDaoImpl(tableName);
		HashMap<String, Object> existData = rainStormDaoImpl.getExistDataTmp(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		
		if(resultList == null) {
			return;
		}
		System.out.println("resultList.size:" + resultList.size());
		List dataList = new ArrayList();
		List updateDataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("Year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String station_Name = (String) tempMap.get("Station_Name");
			String preColumn = "";
			if("t_rainstorm0808".equals(tableName)) {
				preColumn = "PRE_Time_0808";
			} else if("t_rainstorm0820".endsWith(tableName)) {
				preColumn = "PRE_Time_0820";
			} else if("t_rainstorm2020".equals(tableName)) {
				preColumn = "PRE_Time_2020";
			} else if("t_rainstorm2008".equals(tableName)) {
				preColumn = "PRE_Time_2008";
			}
			Double pre = null;
			try {
				pre = (Double) tempMap.get(preColumn);
			} catch(Exception e) {
				continue;
			}
			if(null == pre || 0 == pre || pre >= 99999) {
				continue;
			}
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			String datetime = (String) tempMap.get("Datetime");
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
//				continue;
				Map<String, Object> updateMapData = new HashMap<String, Object>();
//				updateMapData.put("Year", year);
//				updateMapData.put("Station_Id_C", station_Id_C);
//				updateMapData.put("Station_Name", station_Name);
//				updateMapData.put("Lon", lon);
//				updateMapData.put("Lat", lat);
//				updateMapData.put("Lat", lat);
				updateMapData.put("Pre", pre);
//				updateMapData.put("datetime", datetime);
				updateMapData.put("id", existData.get(key));
				updateDataList.add(updateMapData);
			} else {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("Year", year);
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("Station_Name", station_Name);
				mapData.put("Lon", lon);
				mapData.put("Lat", lat);
				mapData.put("Lat", lat);
				mapData.put("Pre", pre);
				mapData.put("datetime", datetime);
				dataList.add(mapData);
			}
		}
//		System.out.println("insertsize:" + dataList.size());
//		System.out.println("updatesize:" + updateDataList.size());
		rainStormDaoImpl.insert(dataList);
		rainStormDaoImpl.update(updateDataList);
	}
	
	/**
	 * 实况
	 */
	public void sync(String result, String timeStr) {
		// 实况
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd000000");
//		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
//		String startStr = "20160129000000";
//		String endStr = "20160222000000";
//		long startTime = sdf2.parse(startStr).getTime();
//		long endTime = sdf2.parse(endStr).getTime();
//		for(long i = startTime; i <= endTime; i+= 24 * 60 * 60 * 1000) {
		CIMISSRest cimissRest = new CIMISSRest();
//		SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
////		int year = Integer.parseInt(sdfYear.format(new Date(System.currentTimeMillis())));
		int year = Integer.parseInt(timeStr.substring(0, 4));
		List itemDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
		//分要素数据 08-08降水
////		List itemPreTime0808DataList = cimissRest.analystChnMulDayItemData(result, columnMap);
//		List preTime0808DataList = cimissRest.analystChnMulDayItemByName(itemDataList, "PRE_Time_0808");
//		T_pre_time_0808DaoImpl t_pre_time0808DaoImp = new T_pre_time_0808DaoImpl(year);
//		if(preTime0808DataList != null) {
//			t_pre_time0808DaoImp.disposeDataList(preTime0808DataList);
//		}
		// 20-20 降水
//		List itemPreTime2020DataList = cimissRest.analystChnMulDayItemData(result, columnMap);
		List preTime2020DataList = cimissRest.analystChnMulDayItemByName(itemDataList, "PRE_Time_2020");
		T_pre_time_2020DaoImpl t_pre_time2020DaoImp = new T_pre_time_2020DaoImpl(year);
		if(preTime2020DataList != null) {
			t_pre_time2020DaoImp.disposeDataList(preTime2020DataList);
		}
		// 08-20 降水
//		List itemPreTime0820DataList = cimissRest.analystChnMulDayItemData(result, columnMap);
//		List preTime0820DataList = cimissRest.analystChnMulDayItemByName(itemDataList, "PRE_Time_0820");
//		T_pre_time_0820DaoImpl t_pre_time0820DaoImp = new T_pre_time_0820DaoImpl(year);
//		if(preTime0820DataList != null) {
//			t_pre_time0820DaoImp.disposeDataList(preTime0820DataList);
//		}
//		// 20-08 降水
////		List itemPreTime2008DataList = cimissRest.analystChnMulDayItemData(result, columnMap);
//		List preTime2008DataList = cimissRest.analystChnMulDayItemByName(itemDataList, "PRE_Time_2008");
//		T_pre_time_2008DaoImpl t_pre_time2008DaoImp = new T_pre_time_2008DaoImpl(year);
//		if(preTime2008DataList != null) {
//			t_pre_time2008DaoImp.disposeDataList(preTime2008DataList);
//		}
		
		// prs_avg 平均气压统计
//		List itemPrsAvgDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
//		List preAvgDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "PRS_Avg");
//		T_prs_avgDaoImpl t_prs_avgDaoImpl = new T_prs_avgDaoImpl(year);
//		if(preAvgDataList != null) {
//			t_prs_avgDaoImpl.disposeDataList(preAvgDataList);
//		}
//		//相对湿度统计表
////		List itemRhuAvgDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
//		List rhuAvgDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "RHU_Avg");
//		T_rhu_avgDaoImpl t_rhu_avgDaoImpl = new T_rhu_avgDaoImpl(year);
//		if(rhuAvgDataList != null) {
//			t_rhu_avgDaoImpl.disposeDataList(rhuAvgDataList);
//		}
//		//日照对数统计表
////		List itemSSHDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
//		List sshDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "SSH");
//		T_sshDaoImpl t_sshDaoImpl = new T_sshDaoImpl(year);
//		if(sshDataList != null) {
//			t_sshDaoImpl.disposeDataList(sshDataList);
//		}
		//平均气温统计
//		List itemTmpAvgDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
		List tmpAvgDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "TEM_Avg");
		T_tem_avgDaoImpl t_tem_avgDaoImpl = new T_tem_avgDaoImpl(year);
		if(tmpAvgDataList != null) {
			t_tem_avgDaoImpl.disposeDataList(tmpAvgDataList);
		}
		//最大气温统计
//		List itemTemMaxDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
		List temMaxDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "TEM_Max");
		T_tem_maxDaoImpl t_tem_maxDaoImpl = new T_tem_maxDaoImpl(year);
		if(temMaxDataList != null) {
			t_tem_maxDaoImpl.disposeDataList(temMaxDataList);
		}
		//最低气温统计
//		List itemTemMinDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
		List temMinDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "TEM_Min");
		T_tem_minDaoImpl t_tem_minDaoImpl = new T_tem_minDaoImpl(year);
		if(temMinDataList != null) {
			t_tem_minDaoImpl.disposeDataList(temMinDataList);
		}
//		// 能见度统计
////		List itemVisMinDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
//		List visMinDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "VIS_Min");
//		T_vis_minDaoImpl t_vis_minDaoImpl = new T_vis_minDaoImpl(year);
//		if(visMinDataList != null) {
//			t_vis_minDaoImpl.disposeDataList(visMinDataList);
//		}
//		// 平均风速统计表
////		List itemWinS2MiAvgDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
//		List winS2MiAvgDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "WIN_S_2mi_Avg");
//		T_win_s_2mi_avgDaoImpl t_win_s_2mi_avgDaoImpl = new T_win_s_2mi_avgDaoImpl(year);
//		if(winS2MiAvgDataList != null) {
//			t_win_s_2mi_avgDaoImpl.disposeDataList(winS2MiAvgDataList);
//		}
////		//天气现象
////		List wepDataList = cimissRest.analystChnMulDayItemByName(itemDataList, "WEP_Record");
////		T_win_s_2mi_avgDaoImpl wepDaoImpl = new T_win_s_2mi_avgDaoImpl(year);
////		if(wepDataList != null) {
////			wepDaoImpl.disposeDataList(wepDataList);
////		}
////		}
//		// 日值数据
//		Surf_chn_mul_dayDao surf_chn_mul_dayDao = new Surf_chn_mul_dayDao(year);
//		List dataList = cimissRest.analystChnMulDay(result, columnMap);
//		if(dataList != null) {
//			surf_chn_mul_dayDao.disposeDataList(dataList, timeStr);
//		}
	}
	
	
	public static void createSSHParam() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd000000");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startStr = "20111201000000";
		String endStr = "20111231000000";
		long startTime = 0L, endTime = 0L;
		try {
			startTime = sdf2.parse(startStr).getTime();
			endTime = sdf2.parse(endStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		FileWriter writer  = null;
		try {
			writer = new FileWriter("d:/sshtime.txt", true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(long i = startTime; i <= endTime; i+= 24 * 60 * 60 * 1000) {
			String timeStr = sdf.format(i);
			String url = "http://10.194.89.55/cimiss-web/api?userId=BCCD_QHZX_PJS&pwd=pjs20160106&interfaceId=getSurfEleInRegionByTimeRange";
			url += "&dataCode=SURF_CHN_MUL_DAY&elements=";
			Set<String> keySet = columnMap.keySet();
			Iterator<String> it = keySet.iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				url = url + key + ",";
			}
			url += "&timeRange=[" + timeStr + "," + timeStr + "]&orderby=Datetime:ASC&staLevels=011,012,013,014&adminCodes=500000&dataFormat=json";
			System.out.println(url);
			CIMISSRest cimissRest = new CIMISSRest();
			String result = cimissRest.callCIMISS(url);
			List itemPrsAvgDataList = cimissRest.analystChnMulDayItemData(result, columnMap);
			if(itemPrsAvgDataList == null) {
				return;
			}
			for(int j=0; j<itemPrsAvgDataList.size(); j++) {
				Map<String, Object> dataMap = (Map<String, Object>) itemPrsAvgDataList.get(j);
				String Station_Id_C = (String) dataMap.get("Station_Id_C");
				double Lon = (Double)dataMap.get("Lon");
				double Lat = (Double)dataMap.get("Lat");
				double Alti = (Double)dataMap.get("Alti");
				int Mon = (Integer)dataMap.get("Mon");
				int Day = (Integer)dataMap.get("Day");
				String Sunrist_Time = (String) dataMap.get("Sunrist_Time");
				String Sunset_Time = (String) dataMap.get("Sunset_Time");
				String Datetime = (String) dataMap.get("Datetime");
				String sunristTimeStr = Datetime.substring(0, 11) + "0" + Sunrist_Time.substring(0, 1) + ":" + Sunrist_Time.substring(1, 3) + ":00";
				String sunsetTimeStr = Datetime.substring(0, 11) +  Sunset_Time.substring(0, 2) + ":" + Sunset_Time.substring(2, 4) + ":00";
				try {
					Date sunristDate = sdf3.parse(sunristTimeStr);
					Date sunsetDate = sdf3.parse(sunsetTimeStr);
					long sunrist = sunristDate.getTime();
					long sunset = sunsetDate.getTime();
					double SunTime = (sunset - sunrist) / 1000 / 60;
					String str = "insert into t_sshtime(Station_Id_C, Lon, Lat, Alti, Mon, Day, Sunrist_Time, Sunset_Time, SunTime) values ('" + Station_Id_C + "'," +
							Lon + ", " + Lat + ", " + Alti + ", " + Mon + "," + Day + ", '" + Sunrist_Time + "', '" + Sunset_Time + "', " + SunTime + ");";
					writer.write(str);
					writer.write("\n");
					writer.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 冰雹数据同步
	 */
	public void syncHail(String timeStr) {
		String result = getCIMISSHailData(timeStr);
		HailDaoImpl hailDaoImpl = new HailDaoImpl();
		HashMap<String, Object> existData = hailDaoImpl.getExistHail(timeStr);
		CIMISSRest cimissRest = new CIMISSRest();
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		if(resultList == null) {
			return;
		}
		List dataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String station_Name = (String) tempMap.get("Station_Name");
			String datetime = (String) tempMap.get("Datetime");
			Double HAIL_Diam_M = (Double) tempMap.get("HAIL_Diam_Max");
			String key = station_Id_C + "_" + datetime.substring(0, 10);
			Set<String> set = existData.keySet();
			if(set.contains(key)) {
				continue;
			}
			if(HAIL_Diam_M > 9999) {
				continue;
			}
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("Station_Id_C", station_Id_C);
			mapData.put("Station_Name", station_Name);
			mapData.put("diameter", HAIL_Diam_M);
			mapData.put("datetime", datetime);
			dataList.add(mapData);
		}
		hailDaoImpl.insertHailValue(dataList);
	}
	
	/**
	 * 天气现象
	 * @param result
	 * @param timeStr
	 */
	public void syncWep(String result, String timeStr) {
		CIMISSRest cimissRest = new CIMISSRest();
		WepDaoImpl wepDaoImpl = new WepDaoImpl();
		HashMap<String, Object> existData = wepDaoImpl.getExistWep(timeStr);
		List resultList = cimissRest.analystChnMulDayItemData(result, columnMap);
		if(resultList == null) {
			return;
		}
		List dataList = new ArrayList();
		List updateDataList = new ArrayList();
		for(int i=0; i<resultList.size(); i++) {
			HashMap tempMap = (HashMap) resultList.get(i);
			int year = (Integer) tempMap.get("Year");
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(station_Id_C.startsWith("A")) continue; // 区域站对天气现象没有观测
			if(!isCQStation) continue;
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String station_Name = (String) tempMap.get("Station_Name");
//			year Datetime MaxTmp
//			Double tmpMax = (Double) tempMap.get("TEM_Min");
//			
//			if(null == tmpMax || tmpMax > 10 || tmpMax > 9999 || tmpMax < -999) {
//				continue;
//			}
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			String datetime = (String) tempMap.get("Datetime");
			String WEP_Record = (String) tempMap.get("WEP_Record");
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime.substring(0, 10);
			if(existData.containsKey(key)) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("WEP_Record", WEP_Record);
				mapData.put("id", existData.get(key));
				updateDataList.add(mapData);
			} else {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("Station_Name", station_Name);
				mapData.put("Lon", lon);
				mapData.put("Lat", lat);
				mapData.put("WEP_Record", WEP_Record);
				mapData.put("Datetime", datetime);
				dataList.add(mapData);
			}
		}
		wepDaoImpl.insertWepValue(dataList);
		wepDaoImpl.updateWepValue(updateDataList);
	}
	
	/**
	 * 区域高温
	 */
	public void syncAreaHighTmp(String dateTimeStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date date = new Date();
//		String dateTimeStr = sdf.format(date);
//		dateTimeStr = "2016-07-30";
		AreaHighTmpSync areaHighTmpSync = new AreaHighTmpSync();
		
		boolean flag = areaHighTmpSync.syncAreaHighTmp(dateTimeStr);
		if(!flag) return;
		String[] timesRange = areaHighTmpSync.syncAreaHighTmpProcess(dateTimeStr);
		if(timesRange == null) return;
//		String[] timesRange = new String[]{"2016-07-20", "2016-07-26"};
		areaHighTmpSync.syncAreaHighTmpSI(timesRange[0], timesRange[1]);
		areaHighTmpSync.syncAreaHighAreaResult(timesRange[0], timesRange[1]);
		int year = Integer.parseInt(dateTimeStr.substring(0, 4));
		areaHighTmpSync.syncAreaHighTmpYearResult(year);
	}
	
	/**
	 * 秋雨的同步
	 */
	public void syncAutumnRains(String datetime) {
		AutumnRainsSync autumnRainsSync = new AutumnRainsSync();
		//计算开始结束期，以及指数
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null; 
		try {
			date = sdf2.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		autumnRainsSync.sync(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String yearStr = sdf.format(date);
		int year = Integer.parseInt(yearStr);
		//记录中间过程
		autumnRainsSync.caleHisRangeByTimes(year);
	}
	
	/**
	 * 区域暴雨评估
	 * @param timeStr
	 */
	public void syncAreaRainStorm(String timeStr) {
		AreaRainStormSync areaRainStormSync = new AreaRainStormSync();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date date = new Date();
//		String datetimeStr = sdf.format(date);
//		System.out.println(datetimeStr);
		//新的统计方式
//		Object[] objs = areaRainStormSync.syncRainStormAreaNew(datetimeStr, "t_rainstorm2020");
//		areaRainStormSync.analyst(objs, "t_rainstorm2020", datetimeStr);
//		Object[] objs2 = areaRainStormSync.syncRainStormAreaNew(datetimeStr, "t_rainstorm0808");
//		areaRainStormSync.analyst(objs2, "t_rainstorm0808", datetimeStr);
		//旧的方式
//		areaRainStormSync.syncRainStormAreaPre(datetimeStr);
		areaRainStormSync.sync(timeStr);
	}
	
	/**
	 * 单站连阴雨
	 * @param datetime
	 */
	public void syncContinueStationRain(String datetime){
		ContinueStationRainNewSync continueStationRainSync = new ContinueStationRainNewSync();
		continueStationRainSync.sync(datetime);
	}
	
	/**
	 * 区域连阴雨
	 * @param datetime
	 */
	public void syncContinueAreaRain(String datetime) {
		ContinueAreaRainNewSync continueAreaRainSync  = new ContinueAreaRainNewSync();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		continueAreaRainSync.sync(datetime);
//		Date date = null;
//		try {
//			date = sdf.parse(datetime);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		System.out.println(datetime);
//		Date lastedDate = continueAreaRainSync.getLastedTime();
//		if(lastedDate == null || date.getTime() > lastedDate.getTime()) {
//			continueAreaRainSync.sync(datetime);
//		}
	}
	
	public void syncStrongCoolingStation(String datetime) {
		StrongCoolingSync strongCoolingSync = new StrongCoolingSync();
		strongCoolingSync.sync(datetime);
	}
	
	public void syncStrongCoolingArea(String datetime) {
		StrongCoolingAreaNewSync strongCoolingAreaNewSync = new StrongCoolingAreaNewSync();
		strongCoolingAreaNewSync.sync(datetime);
	}
	
	/**
	 * 低温单站
	 */
	public void syncLowTmpStation(String datetime) {
		LowTmpStationSync lowTmpStationSync = new LowTmpStationSync();
		lowTmpStationSync.sync(datetime);
	}
	
	/**
	 * 低温区域
	 */
	public void syncLowTmpArea(String datetime) {
		LowTmpAreaSync lowTmpAreaSync = new LowTmpAreaSync();
		lowTmpAreaSync.sync(datetime);
	}
	/**
	 * 降雪区域
	 * @param datetime
	 */
	public void syncSnowArea(String datetime) {
		SnowAreaSync snowAreaSync = new SnowAreaSync();
		snowAreaSync.sync(datetime);
	}
	
	public void syncHourData(String datetime) {
		//1. 取到CIMISS查询后的结果
		String result = getHourDataByTime(datetime);
		//2. 解析结果
		CIMISSRest cimissRest = new CIMISSRest();
		List itemDataList = cimissRest.analystChnMulDayItemData(result, hourColumnMap);
		syncHourPreData(itemDataList, datetime);
		syncHourCloData(itemDataList, datetime);
	}
	
	/**
	 * 同步小时的资料、2分降水
	 * @param datetime
	 */
	public void syncHourCloData (List itemDataList, String timeStr) {
		if(itemDataList == null) return;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List dataList = new ArrayList();
		CLOCovHourDaoImpl cloCovHourDaoImpl = new CLOCovHourDaoImpl();
		HashMap<String, Object> existData = cloCovHourDaoImpl.getExistData(timeStr);
		for(int i=0; i<itemDataList.size(); i++) {
			HashMap tempMap = (HashMap) itemDataList.get(i);
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String station_Name = (String) tempMap.get("Station_Name");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			String datetime = (String) tempMap.get("Datetime");
			//datetime转换为中国时
			Date date = null;
			try {
				date = sdf.parse(datetime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			date = new Date(date.getTime() + 8 * CommonConstant.HOURTIMES);
			datetime = sdf.format(date);
			Integer CLO_Cov = (Integer) tempMap.get("CLO_Cov");
			Integer CLO_Cov_Low = (Integer) tempMap.get("CLO_Cov_Low");
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime;
			if(!existData.containsKey(key)) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("Station_Name", station_Name);
				mapData.put("Lon", lon);
				mapData.put("Lat", lat);
				mapData.put("CLO_Cov", CLO_Cov);
				mapData.put("CLO_Cov_Low", CLO_Cov_Low);
				mapData.put("Datetime", datetime);
				mapData.put("Hours", Integer.parseInt(datetime.substring(11, 13)));
				dataList.add(mapData);
			}
		}
		cloCovHourDaoImpl.insertValues(dataList);
	}
	
	/**
	 * 同步小时云量资料
	 * @param datetime
	 */
	public void syncHourPreData(List itemDataList, String timeStr) {
		if(itemDataList == null) return;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List dataList = new ArrayList();
		List updateDataList = new ArrayList();
		WinAvgHourDaoImpl winAvgHourDaoImpl = new WinAvgHourDaoImpl();
		HashMap<String, Object> existData = winAvgHourDaoImpl.getExistData(timeStr);
		for(int i=0; i<itemDataList.size(); i++) {
			HashMap tempMap = (HashMap) itemDataList.get(i);
			String station_Id_C = (String) tempMap.get("Station_Id_C");
			boolean isCQStation = CQAWSStation.isCQStation(station_Id_C);
			if(!isCQStation) continue;
			String Station_Id_d = (String) tempMap.get("Station_Id_d");
			String station_Name = (String) tempMap.get("Station_Name");
			double lon = (Double) tempMap.get("Lon");
			double lat = (Double) tempMap.get("Lat");
			String datetime = (String) tempMap.get("Datetime");
			//datetime转换为中国时
			Date date = null;
			try {
				date = sdf.parse(datetime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			date = new Date(date.getTime() + 8 * CommonConstant.HOURTIMES);
			datetime = sdf.format(date);
			Integer WIN_D_Avg_2mi = (Integer) tempMap.get("WIN_D_Avg_2mi");
			Double WIN_S_Avg_2mi = (Double) tempMap.get("WIN_S_Avg_2mi");
			String key = (String) tempMap.get("Station_Id_C") + "_" + datetime;
			if(!existData.containsKey(key)) {
				Map<String, Object> mapData = new HashMap<String, Object>();
				mapData.put("Station_Id_C", station_Id_C);
				mapData.put("Station_Name", station_Name);
				mapData.put("Lon", lon);
				mapData.put("Lat", lat);
				mapData.put("WIN_D_Avg_2mi", WIN_D_Avg_2mi);
				mapData.put("WIN_S_Avg_2mi", WIN_S_Avg_2mi);
				mapData.put("Datetime", datetime);
				mapData.put("Hours", Integer.parseInt(datetime.substring(11, 13)));
				dataList.add(mapData);
			}
		}
		winAvgHourDaoImpl.insertValues(dataList);
	}

	private String getHourDataByTime(String datetime) {
		//构造四个时次的数据
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		Date date = null, preDate = null;
		try {
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		preDate = new Date(date.getTime() - CommonConstant.DAYTIMES);
		String times = sdf2.format(preDate) + "180000," + datetime + "," + datetime.substring(0, 8) + "060000," + datetime.substring(0, 8) + "120000";
		String url = "http://10.194.89.55/cimiss-web/api?userId=BCCD_QHZX_PJS&pwd=pjs20160106&interfaceId=getSurfEleInRegionByTime&" +
				"dataCode=SURF_CHN_MUL_HOR&times=" + times + "&adminCodes=500000&" +
				"elements=Station_Name,Datetime,Station_Id_C,Lat,Lon,WIN_D_Avg_2mi,WIN_S_Avg_2mi,CLO_Cov,CLO_Cov_Low&dataFormat=json";
		CIMISSRest cimissRest = new CIMISSRest();
		String result = cimissRest.callCIMISS(url);
		return result;
	}
	/** 拆分同步程序为三个线程，
	 *  Thread1:实况
	 *  Thread2：单要素资料
	 *  Thread3：灾害统计
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		PropertiesUtil.loadSysCofing();
		CIMISSDayExecutor cimissDayExecutor = new CIMISSDayExecutor();
//		MonitorThread monitorThread = new MonitorThread(60);
//		monitorThread.setDaemon(true);
//		monitorThread.start();
		init();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		long time = 0;
		if(args.length == 1) {
			time = sdf2.parse(args[0]).getTime();
		} else {
			time = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
		}
//		String timeStr = sdf.format(time);
		//测试时间段
		String startTimeStr = "20170619";
		String endTimeStr = "20170721";
		Date startDate = sdf.parse(startTimeStr);
		Date endDate = sdf.parse(endTimeStr);
		for(long i = startDate.getTime(); i <= endDate.getTime(); i += CommonConstant.DAYTIMES) {
			String timeStr = sdf.format(i) + "000000";
			System.out.println("开始：" + timeStr);
			String[] codes = "500000,510000,520000,530000,540000".split(",");
			for(String code : codes) {
				String result = getCIMISSData(timeStr, code);
		//		//实况
				String timeStr2 = sdf2.format(i);
				RealTimeThread realTimeThread = new RealTimeThread(result, timeStr2);
				realTimeThread.start();
				MeteoEleThread meteoEleThread = new MeteoEleThread(result, timeStr2);
				meteoEleThread.start();
			}
		}
		//季节
//		RainySeasonSync rainySeasonSync = new RainySeasonSync();
//		rainySeasonSync.syncStart(sdf2.format(time));
//		rainySeasonSync.syncEnd(sdf2.format(time));
	}

}
