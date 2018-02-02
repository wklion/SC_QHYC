package com.spd.sync.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.spd.sync.IinternetImgDownload;
import com.spd.tool.LogTool;

public class InternetImgDownloadImpl implements IinternetImgDownload{

//	public static void main(String[] args) {
//		InternetImgDownloadImpl iid = new InternetImgDownloadImpl();
//		iid.downloadImg("http://tropic.ssec.wisc.edu/real-time/westpac/winds/wgmsvor.GIF", "d:", "a.gif");
//	}
	/**
	 * 采用新的NIO的方式
	 */
	public boolean  downloadImg(String urlAddress, String dir, String fileName) {
		FileOutputStream fos = null;
		ReadableByteChannel rbc = null;
		try {
			URL website = new URL(urlAddress);
			rbc = Channels.newChannel(website.openStream());
			File file = new File(dir + "/" + fileName);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(dir + "/" + fileName);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		} catch(Exception e) {
			LogTool.logger.error("网络图片下载失败,url【" + urlAddress + "】");
			return false;
		} finally {
			try {
				rbc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	public boolean downloadImgBak(String urlAddress, String dir, String fileName) {
		//TODO 图片存储的名字时候需要考虑更为复杂的情况
		InputStream is = null;
		FileOutputStream fos = null;
		int length = -1;
//		LogTool.logger.info("begin downloadImg : " + urlAddress);
		try {
			URL url = new URL(urlAddress);
			is = url.openStream();
//			URLConnection urlConnection = url.openConnection();
//			is = urlConnection.getInputStream();
//			LogTool.logger.info("after urlConnection.getInputStream");
			//文件名
//			String fileName = CommonTool.getValidImgName(urlAddress, isRename);
			File file = new File(dir + "/" + fileName);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
//			if(file.exists()) {
//				return false;
//			}
			fos = new FileOutputStream(file);
//			LogTool.logger.info("after new FileOutputStream");
//			int length = is.available();
//			if(length <= 0) {
//				return false;
//			}
//			is.read(buffer);
//			int length = -1;
			byte[] buffer = new byte[2048];
			while(is.available() >= 0 && ((length = is.read(buffer)) > 0)) {
//				length = is.read(buffer);
//				LogTool.logger.info("while before fos.write, length :" + length);
//				length = is.read(buffer);
				fos.write(buffer, 0, length);
//				Thread.sleep(10);
//				LogTool.logger.info("while after fos.write");
			}
//			LogTool.logger.info("after for fos.write");
			
		} catch (Exception e) {
//			e.printStackTrace();
			LogTool.logger.error("网络图片下载失败, length:" + length);
			return false;
		} finally {
			try {
				if(fos != null) {
					fos.close();
				}
				if(is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
//			LogTool.logger.info("after download Image finally");
		}
		return true;
	}

}
