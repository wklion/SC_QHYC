package com.spd.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.net.ftp.FTPClient;

import sun.net.www.protocol.ftp.FtpURLConnection;

public class FileDown {

	public static void main(String[] args) {
		String strUrl = "ftp://ftp.cdc.noaa.gov/Datasets/ncep.reanalysis2/pressure/air.2017.nc";
		String strOutputFile = "C:/Users/wklion/Desktop/temp/wk.nc";
		//File fileIn = new File(url);
		File fileOut = new File(strOutputFile);
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
					System.out.println("“—œ¬‘ÿ:"+pro+"%");
					prePro = pro;
				}
				out.flush();
            }
			out.close();
			conn.close();
			System.out.println("suc");
			/*FileInputStream in=new FileInputStream(fileIn);
			FileOutputStream out=new FileOutputStream(fileOut);
			FileChannel inC=in.getChannel();
			FileChannel outC=out.getChannel();
			ByteBuffer b=null;
			int length=10240;
			while(true){
				if(inC.position()==inC.size()){
					inC.close();
	                outC.close();
				}
				if((inC.size()-inC.position())<length){
	                length=(int)(inC.size()-inC.position());
	            }else
	                length=10240;
	            b=ByteBuffer.allocateDirect(length);
	            inC.read(b);
	            b.flip();
	            outC.write(b);
	            outC.force(false);
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
