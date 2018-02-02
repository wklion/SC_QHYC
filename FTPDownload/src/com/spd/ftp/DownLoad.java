package com.spd.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import com.spd.model.Task;
import com.spd.tool.DateUtil;

import sun.net.www.protocol.ftp.FtpURLConnection;

public class DownLoad {

	public static void main(String[] args) throws Exception {
		ParseConfig pc = new ParseConfig();
		List<Task> lsTask = pc.getConfig();
		Calendar cal = Calendar.getInstance();
		if(args.length>0) {
			Date tempDate = DateUtil.sdf_yyyy_MM_dd.parse(args[0]);
			cal.setTime(tempDate);
		}
		int curYear = cal.get(Calendar.YEAR);
		int curMonth = cal.get(Calendar.MONTH)+1;
		System.out.println("当前日期:"+curYear+"年"+curMonth+"月");
		for(Task task:lsTask) {
			System.out.println("执行:"+task.getName());
			String parentDic = task.getParantDic();
			int timespace = task.getTimeSpace();
			Calendar tempCal = (Calendar) cal.clone();
			String date1 = DateUtil.sdf_yyyyMMdd.format(tempCal.getTime());
			String date3 = DateUtil.sdf_yyyyMM.format(tempCal.getTime());
			tempCal.add(Calendar.MONTH, timespace);
			if(timespace<12) {
				tempCal.add(Calendar.DATE, -1);
			}
			String date2 = DateUtil.sdf_yyyyMMdd.format(tempCal.getTime());
			String date4 = DateUtil.sdf_yyyyMM.format(tempCal.getTime());
			String fileFormat = task.getFormat();
			String fileName = fileFormat.replaceAll("date1", date1);
			fileName = fileName.replaceAll("date2", date2);
			fileName = fileName.replaceAll("date3", date3);
			fileName = fileName.replaceAll("date4", date4);
			String strFile = parentDic + fileName;
			down(task.getName(),strFile,task.getOutputDic());
		}
	}
	private static void down(String name,String strUrl,String outputDic){
		int index = strUrl.lastIndexOf("/");
		String fileName = strUrl.substring(index+1, strUrl.length());
		String strOutputFile = outputDic+fileName;
		System.out.println(name+":"+strUrl);
		File fileOut = new File(strOutputFile);
		if(fileOut.exists()) {
			System.out.println(fileName+"文件已存在!");
			return;
		}
		try {
			URL url = new URL(strUrl);
			FtpURLConnection conn = (FtpURLConnection)url.openConnection();
			int totalLen = conn.getContentLength();
			InputStream inputStream = conn.getInputStream();
			FileOutputStream out=new FileOutputStream(fileOut);
			byte[] bs = new byte[1024*1024];
			int len = 0;
			int curPos = 0;
			int prePro = 0;
			while ((len = inputStream.read(bs)) != -1) {
				out.write(bs, 0, len);
				curPos += len;
				int pro = (int)((100.0*curPos)/totalLen);
				if(pro != prePro) {
					System.out.println("已下载:"+pro+"%");
					prePro = pro;
				}
				out.flush();
            }
			out.close();
			conn.close();
			System.out.println(name+"下载完成!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
