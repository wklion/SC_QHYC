package com.spd.qhyc.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.spd.qhyc.config.ConfigHelper;
import com.spd.qhyc.database.DataSourceSingleton;
import com.spd.qhyc.model.CimissHosMonthData;
import com.spd.qhyc.model.CimissMonthData;
import com.spd.qhyc.model.Config;
import com.spd.qhyc.util.DBUtil;

/**
 * @作者:wangkun
 * @日期:2017年12月5日
 * @公司:spd
 * @说明:导出降水距平
*/
public class ExportJPOfRainToTxt2 {
	public static void main(String[] args) throws Exception {
		//1、读取配置
		ConfigHelper configHelper = new ConfigHelper();
		Config config = configHelper.getConfig();
		//2、连接数据库
		DruidDataSource dds = DataSourceSingleton.getInstance();
		DruidPooledConnection dpConn = null;
		try {
			dpConn = dds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//3、读取数据
		String sql = "select * from t_month_rain";
		PreparedStatement ps = dpConn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		DBUtil dbUtil = new DBUtil();
		List<CimissMonthData> lsCimissMonthData = dbUtil.populate(rs, CimissMonthData.class);
		//4、获取历史平均
		sql = "select * from v_hos_rain";
		ps = dpConn.prepareStatement(sql);
		rs = ps.executeQuery();
		List<CimissHosMonthData> lsCimissHosMonthData = dbUtil.populate(rs, CimissHosMonthData.class);
		//5、3-4合并计算距平
		List<CimissMonthData> lsJPResult = CombinData(lsCimissMonthData,lsCimissHosMonthData);
		dpConn.close();
		//6、输出结果
		String path = config.getDataOutputPath();
		String fileName = config.getMonthPrecJPFileName();
		String strFile = path+fileName;
		saveLSToTxt(lsJPResult,strFile);
		System.out.println("导出月降水距平完成!");
	}
	/**
	 * @作者:kg
	 * @日期:2017年12月5日
	 * @修改日期:2017年12月5日
	 * @参数:
	 * @返回:
	 * @说明:
	 */
	private static List<CimissMonthData> CombinData(List<CimissMonthData> lsCimissMonthData,List<CimissHosMonthData> lsCimissHosMonthData) {
		List<CimissMonthData> lsJPData = new ArrayList();
		/*for(CimissMonthData cmd:lsCimissMonthData) {
			String sn = cmd.getStationnum();
			int year = cmd.getYear();
			CimissMonthData newCMD = cmd.clone();
			for(CimissHosMonthData chmd:lsCimissHosMonthData) {
				String tempSN = chmd.getStationnum();
				if(sn.equals(tempSN)) {
					double liveVal1 = cmd.getM1();
					double avgVal1 = chmd.getM1();
					double cha1 = liveVal1 - avgVal1;

					double liveVal2 = cmd.getM2();
					double avgVal2 = chmd.getM2();
					double cha2 = liveVal2 - avgVal2;
					
					double liveVal3 = cmd.getM3();
					double avgVal3 = chmd.getM3();
					double cha3 = liveVal3 - avgVal3;
					
					double liveVal4 = cmd.getM4();
					double avgVal4 = chmd.getM4();
					double cha4 = liveVal4 - avgVal4;
					
					double liveVal5 = cmd.getM5();
					double avgVal5 = chmd.getM5();
					double cha5 = liveVal5 - avgVal5;
					
					double liveVal6 = cmd.getM6();
					double avgVal6 = chmd.getM6();
					double cha6 = liveVal6 - avgVal6;
					
					double liveVal7 = cmd.getM7();
					double avgVal7 = chmd.getM7();
					double cha7 = liveVal7 - avgVal7;
					
					double liveVal8 = cmd.getM8();
					double avgVal8 = chmd.getM8();
					double cha8 = liveVal8 - avgVal8;
					
					double liveVal9 = cmd.getM9();
					double avgVal9 = chmd.getM9();
					double cha9 = liveVal9 - avgVal9;
					
					double liveVal10 = cmd.getM10();
					double avgVal10 = chmd.getM10();
					double cha10 = liveVal10 - avgVal10;
					
					double liveVal11 = cmd.getM11();
					double avgVal11 = chmd.getM11();
					double cha11 = liveVal11 - avgVal11;
					
					double liveVal12 = cmd.getM12();
					double avgVal12 = chmd.getM12();
					double cha12 = liveVal12 - avgVal12;
					
					newCMD.setM1(cha1);
					newCMD.setM2(cha2);
					newCMD.setM3(cha3);
					newCMD.setM4(cha4);
					newCMD.setM5(cha5);
					newCMD.setM6(cha6);
					newCMD.setM7(cha7);
					newCMD.setM8(cha8);
					newCMD.setM9(cha9);
					newCMD.setM10(cha10);
					newCMD.setM11(cha11);
					newCMD.setM12(cha12);
					lsJPData.add(newCMD);
					break;
				}
			}
		}*/
		return lsJPData;
	}
	/**
	 * @作者:wangkun
	 * @日期:2017年12月6日
	 * @修改日期:2017年12月6日
	 * @参数:
	 * @返回:
	 * @说明:保存列表到文件
	 */
	private static void saveLSToTxt(List<CimissMonthData> lsJPResult,String strFile) throws Exception{
		FileWriter fw = new FileWriter(strFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
		StringBuilder sb = null;
		for(CimissMonthData cimissMonthData:lsJPResult) {
			sb = new StringBuilder();
			String province = cimissMonthData.getProvince();
			sb.append(province);
			sb.append(" ");
			
			String stationname = cimissMonthData.getStationname();
			sb.append(stationname);
			sb.append(" ");
			
			String stationnum = cimissMonthData.getStationnum();
			sb.append(stationnum);
			sb.append(" ");
			
			int year = cimissMonthData.getYear();
			sb.append(year);
			sb.append(" ");
			
			double m1 = cimissMonthData.getM1();
			m1 = (int)(m1*10)/10.0;
			sb.append(m1);
			sb.append(" ");
			
			double m2 = cimissMonthData.getM2();
			m2 = (int)(m2*10)/10.0;
			sb.append(m2);
			sb.append(" ");
			
			double m3 = cimissMonthData.getM3();
			m3 = (int)(m3*10)/10.0;
			sb.append(m3);
			sb.append(" ");
			
			double m4 = cimissMonthData.getM4();
			m4 = (int)(m4*10)/10.0;
			sb.append(m4);
			sb.append(" ");
			
			double m5 = cimissMonthData.getM5();
			m5 = (int)(m5*10)/10.0;
			sb.append(m5);
			sb.append(" ");
			
			double m6 = cimissMonthData.getM6();
			m6 = (int)(m6*10)/10.0;
			sb.append(m6);
			sb.append(" ");
			
			double m7 = cimissMonthData.getM7();
			m7 = (int)(m7*10)/10.0;
			sb.append(m7);
			sb.append(" ");
			
			double m8= cimissMonthData.getM8();
			m8 = (int)(m8*10)/10.0;
			sb.append(m8);
			sb.append(" ");
			
			double m9 = cimissMonthData.getM9();
			m9 = (int)(m9*10)/10.0;
			sb.append(m9);
			sb.append(" ");
			
			double m10 = cimissMonthData.getM10();
			m10 = (int)(m10*10)/10.0;
			sb.append(m10);
			sb.append(" ");
			
			double m11 = cimissMonthData.getM11();
			m11 = (int)(m11*10)/10.0;
			sb.append(m11);
			sb.append(" ");
			
			double m12 = cimissMonthData.getM12();
			m12 = (int)(m12*10)/10.0;
			sb.append(m12);
			sb.append(" ");
			
			String str = sb.toString().trim();
			bw.write(str);
			bw.newLine();
		}
		bw.close();
	}
}
