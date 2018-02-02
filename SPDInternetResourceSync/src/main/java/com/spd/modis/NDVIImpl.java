package com.spd.modis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.spd.dao.impl.ModisNdviDao;
import com.spd.tool.LogTool;

/**
 * NDVI数据同步
 * @author xianchao
 *
 */
public class NDVIImpl {

	private static String BASEURL = "http://e4ftl01.cr.usgs.gov/MOLT/MOD13A3.005/";
	
	private static String[] AREASCOPE = "h23v04,h23v05,h24v04,h24v05,h25v03,h25v04,h25v05,h25v06,h26v03,h26v04,h26v05,h26v06,h27v04,h27v05,h27v06,h28v05,h28v06,h28v07,h28v08,h2906,h29v07,h29v08".split(",");
	
	private ModisNdviDao modisNdviDao = new ModisNdviDao();
	
	/**
	 * 取到需要同步的第一级目录
	 * @return
	 */
	public String[][] getAllNeedSyncDirs() {
		Document doc = null;
		try {
			URL url = new URL(BASEURL);
			doc = Jsoup.parse(url, 60*1000);
			Elements rootElements = doc.getElementsByTag("a");
			int size = rootElements.size();
			// 取最后两个
			String[][] results = new String[2][2];
			Element element1 = rootElements.get(size - 2);
			Element element2 = rootElements.get(size - 1);
			results[0][0] = element1.attr("href");
			results[0][1] = element1.absUrl("href");
			results[1][0] = element2.attr("href");
			results[1][1] = element2.absUrl("href");
			return results;
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.logger.error(this.getClass() + "." + methodName + " error :" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 根据路径名获取到该路径下需要同步的文件名
	 * @param dirName
	 * @return
	 */
	public void  getAllNeedSyncFilesByDirName(String dirName, String saveDir) {
		Document doc = null;
		try {
			URL url = new URL(dirName);
			doc = Jsoup.parse(url, 60*1000);
			Elements rootElements = doc.getElementsByTag("a");
			int size = rootElements.size();
			for(int i=0; i<size; i++) {
				List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				Element element = rootElements.get(i);
				String fileName = element.attr("href");
				if(fileName.length() < 40) {
					//经验值
					continue;
				}
				String imageURL = element.absUrl("href");
				boolean flag = false;
				for(String itemAreascope : AREASCOPE) {
					if(fileName.indexOf(itemAreascope) != -1) {
						// 对比地理范围
						flag = true;
						break;
					}
				}
				if(!flag) {
					continue;
				}
				// 对比数据库，是否已经下载过
				
				boolean isFileDownloaded = modisNdviDao.isFileDownloaded(fileName);
				if(isFileDownloaded) {
					continue;
				}
				//新数据，下载
				long startDown = System.currentTimeMillis();
				boolean isDownload = downloadImg(imageURL, saveDir, fileName);
				long endDown = System.currentTimeMillis();
				System.out.println("下载，花费时间【" + (endDown - startDown) + "】");
				if(!isDownload) {
					continue;
				}
				long startSuccess = System.currentTimeMillis();
				boolean isDownloadSuccess = isDownloadSuccess(imageURL, saveDir + "/" + fileName);
				long endSuccess = System.currentTimeMillis();
				System.out.println("判断下载成功，花费时间【" + (endSuccess - startSuccess) + "】");
				if(!isDownloadSuccess) {
					continue;
				}
				System.out.println(imageURL + " 下载 成功 ");
				Map<String, Object> map = new HashMap<String, Object>();
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyydddHHmmss");
				String[] temps = fileName.split("\\.");
				String observTimesStr = "";
				String indexStr = "";
				if(fileName.endsWith("jpg")) {
					observTimesStr = temps[5];
					indexStr = temps[3]; 
				} else {
					observTimesStr = temps[4];
					indexStr = temps[2]; 
				}
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date observDate = sdf1.parse(observTimesStr);
				String observTimes = sdf2.format(observDate);
				int xIndex = Integer.parseInt(indexStr.substring(1, 3));
				int yIndex = Integer.parseInt(indexStr.substring(5, 6));
				String format = temps[temps.length - 1];
				map.put("ObservTimes", observTimes);
				map.put("XIndex", xIndex);
				map.put("YIndex", yIndex);
				map.put("FileName", fileName);
				map.put("Format", format);
				map.put("SavePath", saveDir);
				dataList.add(map);
				modisNdviDao.insertModisNdviValue(dataList);
			}
		} catch(Exception e) {
			e.printStackTrace();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogTool.logger.error(this.getClass() + "." + methodName + " error :" + e.getMessage());
		}
//		return dataList;
	}
	
	
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
//			LogTool.logger.error("网络图片下载失败,url【" + urlAddress + "】");
			return false;
		} finally {
			try {
				if(rbc != null) {
					rbc.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean isDownloadSuccess(String urlAddress, String filePath) {
		try {
			URL url = new URL(urlAddress);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			int length = httpURLConnection.getContentLength();
			httpURLConnection.disconnect();
			File file = new File(filePath);
			long fileLength = file.length();
			if(fileLength == length) {
				return true;
			}
			//没有下载成功，则把该图片删除掉
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyydddHHmmss");
		String time = "2015310090232";
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(time);
		System.out.println(sdf2.format(date));
//		NDVIImpl ndviImpl = new NDVIImpl();
//		String[] dirs = ndviImpl.getAllNeedSyncDirs();
//		for(String dir : dirs) {
//			ndviImpl.getAllNeedSyncFilesByDirName(dir);
//		}
	}
}
