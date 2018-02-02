package com.spd.schedule.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.dao.cq.impl.AWSHourDataDaoImpl;
import com.spd.dao.cq.impl.MWSHourDataDaoImpl;
import com.spd.tool.PropertiesUtil;

/**
 * 重庆区域站文件数据解析入库
 * @author Administrator
 *
 */
public class CQAWS {

	private String dir = "D:/SPD/重庆气候中心/资料/小时降水/降水统计";
	
	private AWSHourDataDaoImpl awsHourDataDaoImpl = new AWSHourDataDaoImpl();
	
	private Set<String> stations = awsHourDataDaoImpl.getStations();
	
	public File[] getAllFiles() {
		File file = new File(dir);
		File[] files = file.listFiles();
		return files;
	}
	
	public void analyst(File[] files) {
		for(File file : files) {
			List dataList = new ArrayList();
			String fileName = file.getName();
			if(!fileName.endsWith("txt")) continue;
			try {
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line = "";
				while((line = in.readLine()) != null) {
					if(!line.startsWith("5")) continue;
					String[] arrs = line.split("\t");
					Map<String, Object> map = new HashMap<String, Object>();
					if(!stations.contains(arrs[0])) continue;
					map.put("station_Id_C", arrs[0]);
					map.put("datetime", arrs[1].substring(0, 4) + "-" + arrs[1].substring(4, 6) + "-" + arrs[1].substring(6, 8) + " " + arrs[1].substring(8, 10) + ":00:00");
					Integer R1Int = null, R3Int = null, R6Int = null, R12Int = null, R24Int = null;
					try {
						R1Int = Integer.parseInt(arrs[2]);
						map.put("R1", R1Int / 10.0);
					} catch(Exception e) {
						
					}
					try {
						R3Int = Integer.parseInt(arrs[3]);
						map.put("R3", R3Int / 10.0);
					} catch(Exception e) {
						
					}
					try {
						R6Int = Integer.parseInt(arrs[4]);
						map.put("R6", R6Int / 10.0);
					} catch(Exception e) {
						
					}
					try {
						R12Int = Integer.parseInt(arrs[5]);
						map.put("R12", R12Int / 10.0);
					} catch(Exception e) {
						
					}
					try {
						R24Int = Integer.parseInt(arrs[6]);
						map.put("R24", R24Int / 10.0);
					} catch(Exception e) {
						
					}
					if((R1Int != null && R1Int !=0) || (R3Int != null && R3Int !=0) || (R6Int != null && R6Int !=0) 
							|| (R12Int != null && R12Int !=0) || (R24Int != null && R24Int !=0)) {
						dataList.add(map);
					}
					if(dataList.size() == 5000) {
						awsHourDataDaoImpl.insertValue(dataList);
						dataList = new ArrayList();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			awsHourDataDaoImpl.insertValue(dataList);
			System.out.println(file.getName());
		}
	}
	
	public static void main(String[] args) {
		PropertiesUtil.loadSysCofing();
		CQAWS cqaws = new CQAWS();
		File[] files = cqaws.getAllFiles();
		cqaws.analyst(files);
	}

}
