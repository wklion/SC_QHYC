package com.spd.qhyc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LocalDatagram {
	public String getDatagramFile(String dicPath,Calendar cal){
		File file=new File(dicPath);
		String findFileName="";
		if(!file.exists()){
			System.out.println(dicPath+"目录不存在!");
			return findFileName;
		}
		File[] files=file.listFiles();
		//获取30天前的时间
		SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
		String strDate=format.format(cal.getTime());
		
		for(int f=0;f<files.length;f++){
			String tempFileName=files[f].getName();
			if(tempFileName.contains(strDate)){
				findFileName=tempFileName;
				break;
			}
		}
		return findFileName;
	}
	public Map<String,String> AnalysisDatagram(String dicPath,Calendar cal) throws IOException, FileNotFoundException{
		String filePath=getDatagramFile(dicPath,cal);
		Map<String,String> hashMap=new HashMap<String,String>();
		if(!filePath.equals("")){//存在
			System.out.println("文件:"+filePath);
			InputStreamReader read = new InputStreamReader(new FileInputStream(dicPath+filePath),"GBK");
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                String stationNum=lineTxt.substring(0, 5);
                String stationInfo=lineTxt.substring(5, lineTxt.length());
                stationInfo = stationInfo.trim();
                hashMap.put(stationNum, stationInfo);
            }
            read.close();
		}
		return hashMap;
	}
}
