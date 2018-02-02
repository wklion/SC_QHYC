package com.spd.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.spd.sync.IinternetImgDownload;
import com.spd.sync.impl.InternetImgDownloadImpl;

public class MD5Tool {

	private MessageDigest messageDigest;
	
	public MD5Tool() {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
//	static {
//		try {
//			messageDigest = MessageDigest.getInstance("MD5");
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MD5Tool md5Tool = new MD5Tool();
//		messageDigest = MessageDigest.getInstance("MD5");
//		String result = getFileMD5String(new File("d:/wgmsshr.GIF"));
		File file = new File("F:/Server/Server平台/厦门/资料同步/targetDir/天气图数据/西太风场wgmsshr");
		File[] subFiles = file.listFiles();
		for(File subFile:subFiles) {
			String result = md5Tool.getFileMD5String(subFile);
			System.out.println(result);
//			String result1 = md5Tool.getURLMD5String("http://www.cwb.gov.tw/V7/observe/satellite/Data/s3p/s3p-2015-01-16-09-00.jpg");
		}
	}
	
	/**
	 * 先下载到临时文件，计算出MD5之后，再删除
	 * @param urlAddress
	 * @return
	 */
	public String getURLMD5String2(String urlAddress) {
		File file = null;
		try {
			file = File.createTempFile("image", ".jpg");
			IinternetImgDownload internetImgDownload = new InternetImgDownloadImpl();
			boolean flag = internetImgDownload.downloadImg(urlAddress, file.getParent(), file.getName());
			if(flag) {
				String result = getFileMD5String(file);
				return result;
			}
		} catch (IOException e) {
//			e.printStackTrace();
		} finally {
			file.delete();
		}
		return null;
	}
	/**
	 * 根据URL地址获取MD5值
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String getURLMD5String(String urlAddress) {
//		LogTool.logger.info("enter MD5Tool.getURLMD5String,url:" + urlAddress);
		InputStream is = null;
		try {
			URL url = new URL(urlAddress);
//			LogTool.logger.info("after new URL,address:" + urlAddress);
			URLConnection urlConnection = url.openConnection();
//			LogTool.logger.info("after openConnection");
			is = urlConnection.getInputStream();
//			LogTool.logger.info("after getInputStream");
			byte[] buffer = new byte[1024];  
	        int numRead = 0;  
	        while ((numRead = is.read(buffer)) > 0) {  
	        	messageDigest.update(buffer, 0, numRead);  
	        }  
//	        LogTool.logger.info("after while read");
	        return bufferToHex(messageDigest.digest());  
		} catch (Exception e) {
//			e.printStackTrace();
			LogTool.logger.error("getURLMD5String exception " + e.getMessage());
		} finally {
			try {
				if(is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return null;
	}
	
	public String getFileMD5String(File file) throws IOException {        
//		LogTool.logger.info("enter getFileMD5String");
        InputStream fis;  
        fis = new FileInputStream(file);  
        byte[] buffer = new byte[1024];  
        int numRead = 0;  
//        LogTool.logger.info("getFileMD5String before while");
        while ((numRead = fis.read(buffer)) > 0) {  
        	messageDigest.update(buffer, 0, numRead);  
        }  
//        LogTool.logger.info("getFileMD5String after while");
        fis.close();  
        return bufferToHex(messageDigest.digest());  
    } 
	
	private  String bufferToHex(byte bytes[]) {  
        return bufferToHex(bytes, 0, bytes.length);  
    } 
	
	private  String bufferToHex(byte bytes[], int m, int n) {  
        StringBuffer stringbuffer = new StringBuffer(2 * n);  
        int k = m + n;  
        for (int l = m; l < k; l++) {  
            appendHexPair(bytes[l], stringbuffer);  
        }  
        return stringbuffer.toString();  
    }  
	
	protected char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',  
        '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
	
	private  void appendHexPair(byte bt, StringBuffer stringbuffer) {  
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同   
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换   
        stringbuffer.append(c0);  
        stringbuffer.append(c1);  
    } 
	
}
