package com.spd.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.tool.MD5Tool;
import com.spd.tool.PropertiesUtil;

public class ZongHeGuanCeBuLu {

	private String out = new String("out.txt");
	
	private List<String> address = new ArrayList<String>();

	private Set<String> set = new HashSet<String>();

	public Set<String> getSet() {
		return set;
	}

	public void setSet(Set<String> set) {
		this.set = set;
	}
	
//	private List<List<String>> itemAddress = new ArrayList<List<String>>();
	
	/**
	 * @param args
	 */
	
//	public void init() {
//		address.add("/DATA/images/CONT");
//		address.add("/DATA/images/CONT_GIS");
//		address.add("/DATA/images/RADA_SWAN");
//		address.add("/DATA/images/RAD_CQ");
//		address.add("/DATA/images/RAD_QJ");
//		address.add("/DATA/images/RAD_WZ");
//		address.add("/DATA/images/RAD_YC");
//	}
	
	public void getAllAddress(String dir) {
		File file = new File(dir);
		File[] listFiles = file.listFiles();
		for(File listFile : listFiles) {
			if(listFile.isDirectory()) {
				getAllAddress(listFile.getAbsolutePath());
			} else {
				set.add(listFile.getParent());
			}
		}
	}
	
	public Map getDBExist(String dir) {
		String sql = "select filename from t_doc where website = '" + dir + "'";
		ZongHeGuanCeBuLuDao dao = new ZongHeGuanCeBuLuDao();
		List<Map> list = dao.getZongHeGuanCe(sql);
		Map<String, String> map = new HashMap<String, String>();
		for(Map item:list) {
			String filename = (String) item.get("filename");
			map.put(filename, "");
		}
		return map;
	}
	
	private int getTypeIdByDir(String dir) {
		File file = new File(dir);
		File[] itemFiles = file.listFiles();
		ZongHeGuanCeBuLuDao dao = new ZongHeGuanCeBuLuDao();
		for(File itemFile : itemFiles) {
			String fileName = itemFile.getName();
			String productCode = fileName.substring(0, 12);
			int typeid = dao.getTypeId(productCode);
			return typeid;
		}
		return -1;
	}
	
	private List<String> getUnputDBData(String dir) throws IOException {
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(out, true)));
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		File file = new File(dir);
		File[] items = file.listFiles();
		List<String> result = new ArrayList<String>();
		int typeid = getTypeIdByDir(dir);
		Map map = getDBExist(dir);
//		Map map = getDBExist("/DATA/images/CONT/CQ_AWS_HUM_24HUR.MAX");
		MD5Tool md5Tool = new MD5Tool();
		String sql = "insert into t_doc (savepath, website, typeid, md5, filename, intime, filetime) values (";
		for(File subFile : items) {
			String fileName = subFile.getName();
			if(!map.containsKey(fileName)) {
				//没有同步的
				try {
					String md5 = md5Tool.getFileMD5String(subFile);
					//A.7200.0106.20150511000000.gif
					String fileTime = fileName.substring(12, 26);
					Date time = sdf1.parse(fileTime);
					String fileTime2 = sdf2.format(time);
					String str = sql + "'" + dir + "', '" + dir + "', " + typeid + ", '" + md5 + "', '" + fileName + "', '" + fileTime2 + "', '" + fileTime2 + "');";
					pw.write(str + "\n");
//					System.out.println(str);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		pw.close();
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		PropertiesUtil.loadSysCofing();
		PropertiesUtil.loadFTPConfig();
		ZongHeGuanCeBuLu zongHeGuanCeBuLu = new ZongHeGuanCeBuLu();
		zongHeGuanCeBuLu.getAllAddress(args[0]);
		Set set = zongHeGuanCeBuLu.getSet();
		Iterator it = set.iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			zongHeGuanCeBuLu.getUnputDBData(key);
		}
//		System.out.println(set);
//		List<Map> list = zongHeGuanCeBuLu.getDBExist("/DATA/images/CONT/CQ_AWS_HUM_01HUR");
//		System.out.println(list);
	}

}
