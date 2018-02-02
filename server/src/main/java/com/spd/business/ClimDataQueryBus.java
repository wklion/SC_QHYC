package com.spd.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.ContextLoader;

import com.spd.common.ClimDataQueryParam;
import com.spd.common.ClimDataQueryRangeParam;
import com.spd.common.CommonConstant;
import com.spd.common.TimesParam;
import com.spd.common.TimesRangeParam;
import com.spd.db.DBTable;
import com.spd.db.SequenceTimeValue;
import com.spd.db.TimeValue;
import com.spd.service.ICommon;
import com.spd.tool.CIMISSRest;
import com.spd.tool.CommonTool;
import com.spd.util.CommonUtil;

/**
 * 资料信息检索
 * @author Administrator
 *
 */
public class ClimDataQueryBus {

	private Map<String, String> columnMap;
	
	public void init() {
		if(columnMap == null) {
			columnMap = new HashMap<String, String>();
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
		}
	}
	
	public List<LinkedHashMap> queryClimByTimesRangeAndElement(ClimDataQueryRangeParam climDataQueryRangeParam) {
		TimesParam timesParam = climDataQueryRangeParam.getTimesParam();
		TimesRangeParam timesRangeParam = new TimesRangeParam(); 
		timesRangeParam.setStartTimeStr(timesParam.getStartTimeStr());
		timesRangeParam.setEndTimeStr(timesParam.getEndTimeStr());
		DBTable dbTable = new DBTable();
		String tableName = climDataQueryRangeParam.getTableName();
		dbTable.queryDataByRangeTimes(timesRangeParam, "5%", tableName);
		//结果序列
		List<SequenceTimeValue> currentTimeList = dbTable.getSequenceTimeValueList();
		//站点序列
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		String orderType = climDataQueryRangeParam.getOrderType();
		HashMap paramMap = new HashMap();
		List<LinkedHashMap> stationInfoList = new ArrayList<LinkedHashMap>();
		if("SEQ".equals(orderType)) {
			stationInfoList = iCommon.getAWSStationsOrderBySeq(paramMap);
		} else if("STATION".equals(orderType)) {
			stationInfoList = iCommon.getAWSStationsOrderByIdC(paramMap);
		}
		List<LinkedHashMap> resultList = disposeData(currentTimeList, stationInfoList, climDataQueryRangeParam.getTimesParam());
		return resultList;
	}
	
	private List<LinkedHashMap> disposeData(List<SequenceTimeValue> currentTimeList, List<LinkedHashMap> stationInfoList, TimesParam timesParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//初始化key
		LinkedHashMap itemMap = new LinkedHashMap();
		itemMap.put("Date", null);
		for(int i = 0; i < stationInfoList.size(); i++) {
			LinkedHashMap stationMap = stationInfoList.get(i);
			String station_Id_C = (String) stationMap.get("Station_Id_C");
			String station_Name = (String) stationMap.get("Station_Name");
			itemMap.put(station_Id_C + "_" + station_Name, null);
		}
		//初始化Map的序列
		List<LinkedHashMap> dataMapList = new ArrayList<LinkedHashMap>();
		Date startDate = timesParam.getStartDate();
		Date endDate = timesParam.getEndDate();
		long start = startDate.getTime();
		long end = endDate.getTime();
		for(long time = start; time <= end; time += CommonConstant.DAYTIMES) {
			Date date = new Date(time);
			LinkedHashMap dataMap = clone(itemMap);
			dataMap.put("Date", date);
			dataMapList.add(dataMap);
		}
		//遍历数据
		for(int i = 0; i < currentTimeList.size(); i++) {
			SequenceTimeValue sequenceTimeValue = currentTimeList.get(i);
			String itemStation_Id_C = sequenceTimeValue.getStation_Id_C();
			String station_Key = itemStation_Id_C + "_" + CommonUtil.getInstance().stationNameMap.get(itemStation_Id_C);
			List<TimeValue> timeValueList = sequenceTimeValue.getTimeValues();
			for(int j = 0; j < timeValueList.size(); j++) {
				TimeValue timeValue = timeValueList.get(j);
				Date jDate = timeValue.getDate();
				Double value = timeValue.getValue();
				for(int k = 0; k < dataMapList.size(); k++) {
					LinkedHashMap dataMap = dataMapList.get(k);
					Date kDate = (Date) dataMap.get("Date");
					if(jDate.getTime() == kDate.getTime()) {
						dataMap.put(station_Key, value);
						break;
					}
				}
			}
			
		}
		//把Date类型转换成字符串
		for(int i = 0; i < dataMapList.size(); i++) {
			LinkedHashMap item = dataMapList.get(i);
			Date date = (Date) item.get("Date");
			String timeStr = sdf.format(date);
			String[] timeArr = timeStr.split("-");
			String itemTimeStr = timeArr[0] + "年" + timeArr[1] + "月" + timeArr[2] + "日";
			item.put("Date", itemTimeStr);
		}
		return dataMapList;
	}
	
	/**
	 * 复制一份Map的结构
	 * @param map
	 * @return
	 */
	private LinkedHashMap clone(LinkedHashMap map) {
		LinkedHashMap copy = new LinkedHashMap();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			copy.put(key, null);
		}
		return copy;
	}
	
	public List queryClimByTime(ClimDataQueryParam climDataQueryParam) {
		// 查询CIMISS，处理结果
//		String elements = "Station_Id_C,Station_Name,TEM_Avg,TEM_Max,TEM_Min,PRE_Time_0808,PRE_Time_2020,SSH,RHU_Avg,VIS_Min,WIN_D_Avg_2mi_C,PRS_Avg";
		String elements = climDataQueryParam.getElements();
		init();
		String staLevels = "011,012,013";
		String resultStr = CommonTool.getCIMISSData(climDataQueryParam.getTime(), elements, staLevels);
		//处理结果
		CIMISSRest cimissRest = new CIMISSRest();
		List resultList = cimissRest.analystChnMulDay(resultStr, columnMap);
		String orderType = climDataQueryParam.getOrderType();
		HashMap paramMap = new HashMap();
		List<LinkedHashMap> stationInfoList = new ArrayList<LinkedHashMap>();
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		if("SEQ".equals(orderType)) {
			stationInfoList = iCommon.getAWSStationsOrderBySeq(paramMap);
		} else if("STATION".equals(orderType)) {
			stationInfoList = iCommon.getAWSStationsOrderByIdC(paramMap);
		}
		//排序
		List orderList = dispose(resultList, stationInfoList);
		return orderList;
	}
	
	private List dispose(List dataList, List<LinkedHashMap> stationInfoList) {
		List resultList = new ArrayList();
		for(int i = 0; i < stationInfoList.size(); i++) {
			LinkedHashMap stationMap = stationInfoList.get(i);
			String station_Id_C = (String) stationMap.get("Station_Id_C");
			for(int j = 0; j < dataList.size(); j++) {
				Map dataMap = (Map) dataList.get(j);
				String dataStation = (String) dataMap.get("Station_Id_C");
				if(station_Id_C.equals(dataStation)) {
					resultList.add(dataMap);
					break;
				}
			}
		}
		return resultList;
	}
}
