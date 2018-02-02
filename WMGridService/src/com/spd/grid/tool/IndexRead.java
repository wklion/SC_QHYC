/*
 * author:wangkun
 * createtime:20161018
 * lasttime:20161018
 * description:读取RMM数据
 * */
package com.spd.grid.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class IndexRead {
	public static String RMMFILEPATH="E:/SC/EFS/data/rmm.74toRealtime.txt";
	public static String MJOFILEPATH="E:/SC/EFS/data/proj_norm_order.txt";
	/**
	 * 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年8月20日
	 * @RETURN:Map<String,Double>
	 * @PARAM:dataname- 数据名称，dtStart-开始日期，dtEnd-结束日期
	 * @DESCRIPTION:读取RMM数据，包括RMM1和RMM2
	 */
	public Map<String,Double> GetRMMData(String dataname,Date dtStart,Date dtEnd){
		TreeMap<String,Double> result=new TreeMap<>();
		int index=3;
		if(dataname.toLowerCase().equals("rmm2")){
			index=4;
		}
		Calendar calUse=Calendar.getInstance();//过程中使用
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		String rmmFile=ComfigureUtil.config.getRmmFile();
		File fi=new File(rmmFile);
		if(!fi.exists()){
			LogTool.logger.error(rmmFile+"不存在!");
			return null;
		}
		try{
			FileReader fr=new FileReader(rmmFile);
			BufferedReader bufferedReader=new BufferedReader(fr);
			try {
				String data=bufferedReader.readLine();
				data=bufferedReader.readLine();//前2行是头文件，不要
				while((data=bufferedReader.readLine())!=null){
					if(data==null||data.equals("")){
						break;
					}
					String[] datas=data.trim().split("\\s+");
					if(datas.length<5){
						break;
					}
					int year=Integer.parseInt(datas[0]);
					int month=Integer.parseInt(datas[1]);
					int day=Integer.parseInt(datas[2]);
					calUse.set(year, month-1, day,0,0,0);
					Date curDate=calUse.getTime();
					if(curDate.getTime()-dtStart.getTime()<-1000){
						continue;
					}
					if(curDate.getTime()-dtEnd.getTime()>1000){
						break;
					}
					String date = simpleDateFormat.format(calUse.getTime());
					String strVal=decimalFormat.format(Double.parseDouble(datas[index]));
					Double val=Double.parseDouble(strVal);
					result.put(date, val);
				}
			} catch (IOException e) {
				LogTool.logger.error("文件读取失败!",e);
			}
		}
		catch(FileNotFoundException e){
			LogTool.logger.error(RMMFILEPATH+"未找到!");
		}
		return result;
	}
	/**
	 * 
	 * @throws Exception 
	 * @AUTHOR:WANGKUN
	 * @DATE:2016年8月20日
	 * @RETURN:Map<String,Double>
	 * @PARAM:dataname- 数据名称，dtStart-开始日期，dtEnd-结束日期,pentad-坐标
	 * @DESCRIPTION:读取MJO
	 */
	public Map<String,Double> GetMJOData(String dataname,Date dtStart,Date dtEnd,String pentad) throws Exception{
		TreeMap<String,Double> result=new TreeMap<>();
		String[] pentads=new String[]{"20w","70e","80e","100e","120e","140e","160e","120w","40w","10w"};
		int index=0;
		int size=pentads.length;
		for(int i=0;i<size;i++)
		{
			String val=pentads[i];
			if(pentad.toLowerCase().equals(val))
			{
				index=i;
				break;
			}
		}
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat simpleDateFormatTo=new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat decimalFormat = new DecimalFormat("#.00"); 
		String mjoFile=ComfigureUtil.config.getMjoFile();
		File fi=new File(mjoFile);
		if(!fi.exists()){
			LogTool.logger.error(mjoFile+"不存在!");
			return null;
		}
		try{
			FileReader fr=new FileReader(mjoFile);
			BufferedReader bufferedReader=new BufferedReader(fr);
			try {
				String data=bufferedReader.readLine();
				data=bufferedReader.readLine();//前2行是头文件，不要
				while((data=bufferedReader.readLine())!=null){
					if(data==null||data.equals("")){
						break;
					}
					String[] datas=data.trim().split("\\s+");
					if(datas.length<size){
						break;
					}
					Date curDate=simpleDateFormat.parse(datas[0]);
					if(curDate.getTime()-dtStart.getTime()<-1000){
						continue;
					}
					if(curDate.getTime()-dtEnd.getTime()>1000){
						break;
					}
					String date = simpleDateFormatTo.format(curDate.getTime());
					String strVal=datas[index+1];
					if(strVal.contains("*"))
					{
						break;
					}
					else
					{
						strVal=decimalFormat.format(Double.parseDouble(strVal));
						Double val=Double.parseDouble(strVal);
						result.put(date, val);
					}
				}
			} catch (IOException e) {
				LogTool.logger.error("文件读取失败!",e);
			}
		}
		catch(FileNotFoundException e){
			LogTool.logger.error(RMMFILEPATH+"未找到!");
		}
		return result;
	}
}
