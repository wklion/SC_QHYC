package com.spd.schedule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.spd.dao.cq.impl.CQDao;
import com.spd.dao.cq.impl.MSDao;
import com.spd.tool.CommonConstant;
import com.spd.tool.PropertiesUtil;

/**
 * 对比CIMISS和MSSQL中数据的差别，把不一样的，记录下来
 * @author Administrator
 *
 */
public class CompareDiff {
	//全天雨量 	t_pre_time_2020 全天雨量.csv
	//全天雨量08 	t_pre_time_0820 全天雨量08.csv
	//平均风速 	t_win_s_2mi_avg 平均风速.csv
	//日照	t_ssh 日照.csv
	//最低气温	t_tem_min 最低气温.csv
	//最高气温	t_tem_max 最高气温.csv
	//气压	t_prs_avg 气压.csv
	//相对湿度	t_rhu_avg 相对湿度.csv
	//能见度	t_rhu_avg 能见度.csv
	//平均气温	t_tem_avg 平均气温.csv
	
	private String msTableName; // MSSQL中对应的表名
	
	private String cqTableName; // MySQL中对应的表名
	
	private String csvName; //
	
	private MSDao msDao; // = new MSDao("平均气温");

	private CQDao cqDao; // = new CQDao("t_tem_avg");
	
	public void init(String msTableName, String cqTableName, String csvName) {
		this.msTableName = msTableName;
		this.cqTableName = cqTableName;
		this.csvName = csvName;
		msDao = new MSDao(msTableName);
		cqDao = new CQDao(cqTableName);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//构造需要访问的表、等
		String[] msTableNames = new String[]{"全天雨量", "全天雨量08", "平均风速", "日照", "最低气温", "最高气温", "气压", "相对湿度", "能见度", "平均气温"};
		String[] cqTableNames = new String[]{"t_pre_time_2020", "t_pre_time_0820", "t_win_s_2mi_avg", "t_ssh", "t_tem_min", "t_tem_max", "t_prs_avg", "t_rhu_avg", "t_rhu_avg", "t_tem_avg"};
		for(int j = 0; j < msTableNames.length; j++) {
			CompareDiff compareDiff = new CompareDiff();
			compareDiff.init(msTableNames[j], cqTableNames[j], msTableNames[j]);
			PropertiesUtil.loadSysCofing();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//时间往前推两天
			Date date = new Date(System.currentTimeMillis() - 2 * CommonConstant.DAYTIMES);
			String startTime = sdf.format(date);
			String endTime = sdf.format(date);
			
//			String startTime = "1951-01-01";
//			String endTime = "2017-02-07";
			
			Date startDate = null, endDate = null;
			try {
				startDate = sdf.parse(startTime);
				endDate = sdf.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			long start = startDate.getTime();
			long end = endDate.getTime();
			for(long i = start; i <= end; i += CommonConstant.DAYTIMES) {
				String dateTime = sdf.format(i);
				System.out.println(dateTime);
				String columnName = "m" + dateTime.split("-")[1] + "d" + dateTime.split("-")[2];
				List msList = compareDiff.getMSData(dateTime);
				List cqList = compareDiff.getCQData(columnName, Integer.parseInt(dateTime.substring(0, 4)));
				try {
					compareDiff.compareResult(cqList, msList, columnName, dateTime);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}
	
	/**
	 * 从监测平台库中查询得到的数据
	 * @return
	 */
	public List getCQData(String columnName, int year) {
		List resultList = cqDao.queryData(columnName, year);
		return resultList;
	}
	
	/**
	 * 从MSSQL库中查询得到的数据
	 * @return
	 */
	public List getMSData(String dateTime) {
		List resultList = msDao.queryData(dateTime);
		return resultList;
	}
	
	/**
	 * 对比结果
	 * @param cqList
	 * @param msList
	 * @throws Exception 
	 */
	public void compareResult(List cqList, List msList, String key, String dateTime) throws Exception {
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(msTableName + ".csv", true)));
		String resultFileName = "/home/spd/apache-tomcat-6.0.47/webapps/CompareResult";
		File resultFile = new File(resultFileName);
		if(!resultFile.exists()) {
			resultFile.mkdirs();
		}
		PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFileName + "/" + msTableName + ".csv", true), "GBK")));
//		bw.write("站,CIMISS,MSSQL");
		if(cqList == null || msList == null || cqList.size() == 0 || msList.size() == 0) return;
		for(int i = 0; i < cqList.size(); i++) {
			HashMap cqData = (HashMap) cqList.get(i);
			Object cqObject = cqData.get(key);
			Double cqValue = null;
			if(cqObject != null) {
				if(cqObject instanceof java.math.BigDecimal) {
					cqValue = ((java.math.BigDecimal) cqData.get(key)).doubleValue();
				} else {
					//默认Double
					cqValue = ((Double) cqData.get(key)).doubleValue();
				}
			}
			String stationName = (String) cqData.get("Station_Name");
			HashMap msData = (HashMap) msList.get(0);
			Object msObjectData = msData.get(stationName);
			Double msValue = null;
			if(msObjectData != null) {
				if(msObjectData instanceof BigDecimal) {
					msValue = ((BigDecimal) msObjectData).doubleValue();
				} else if(msObjectData instanceof Float) {
					msValue = ((Float) msObjectData).doubleValue();
				} else if(msObjectData instanceof Double) {
					msValue = (Double) msObjectData;
				}
				//对比一位小数的差别
				int intMSValue = (int) (msValue * 10 + 0.5);
				msValue = intMSValue / 10.0;
			}
			if((cqValue != null && msValue == null) || (cqValue == null && msValue != null)) {
				bw.write(dateTime + "," + stationName + "," + cqValue + "," + msValue);
				bw.write("\n");
			} else if(cqValue != null && msValue != null && cqValue.doubleValue() != msValue.doubleValue()) {
				bw.write(dateTime + "," + stationName + "," + cqValue + "," + msValue);
				bw.write("\n");
			}
		}
		bw.close();
	}
}
