package com.spd.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.spd.tool.LogTool;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CIMISSRest {

	private  Set<String> columnSet = new HashSet<String>();
	
	public CIMISSRest() {
		columnSet.add("Station_Name");
		columnSet.add("Province");
		columnSet.add("City");
		columnSet.add("Cnty");
		columnSet.add("Town");
		columnSet.add("Station_Id_C");
		columnSet.add("Station_Id_d");
		columnSet.add("Lat");
		columnSet.add("Lon");
		columnSet.add("Alti");
		columnSet.add("PRS_Sensor_Alti");
		columnSet.add("Station_levl");
		columnSet.add("Admin_Code_CHN");
		columnSet.add("Year");
	}
	/**
	 * 
	 * @param param 参照CIMISS文档。地址：http://10.230.89.55/cimissapiweb/apicustomapiclassdefine_list.action
	 * 使用的userId=supermap 密码 pwd=supermap_123
	 * @return
	 */
	public String callCIMISS(String url) {
		Client client = Client.create();
	    WebResource webResource = client.resource(url); 
	    ClientResponse response = webResource.type("application/x-www-form-urlencoded").post(ClientResponse.class);
	    int status = response.getStatus();
	    if (status == 200) {
	    	return response.getEntity(String.class);
	    }
//    	LogTool.logger.error("访问【" + url + "】失败，返回编码【" + status + "】");
	    return "";
	}
	
	public List analystChnMulDayItemByName(List dataList, String columnName) {
		List resultList = new ArrayList();
		String itemKeyName = "";
		if(dataList == null) {
			return null;
		}
		for(int i=0; i<dataList.size(); i++) {
			Map<String, Object> dataMap = (Map<String, Object>) dataList.get(i);
			Set<String> keySet = dataMap.keySet();
			Iterator<String> it = keySet.iterator();
			itemKeyName = "m" + String.format("%02d", dataMap.get("Mon")) + "d" + String.format("%02d", dataMap.get("Day"));
			Map<String, Object> mapData = new HashMap<String, Object>();
			while(it.hasNext()) {
				String key = it.next();
				if(columnSet.contains(key)) {
					mapData.put(key, dataMap.get(key));
				} else if(key.equals(columnName)) {
					mapData.put(itemKeyName, dataMap.get(key));
				}
			}
			resultList.add(mapData);
		}
		return resultList;
	}
	
	
	public List analystChnMulDayItemData(String data, Map<String, String> columnMap) {
		Gson gson = new Gson();
		Object o = gson.fromJson(data, Object.class);
		List insertDataList = new ArrayList();
		com.google.gson.internal.LinkedTreeMap linkedTreeMap = (com.google.gson.internal.LinkedTreeMap) o;
		ArrayList ds = (ArrayList) linkedTreeMap.get("DS");
		if(ds == null) {
			return null;
		}
		for(int i=0; i<ds.size(); i++) {
			com.google.gson.internal.LinkedTreeMap item = (com.google.gson.internal.LinkedTreeMap)ds.get(i);
			Set set = item.keySet();
			Iterator it = set.iterator();
			Map<String, Object> mapData = new HashMap<String, Object>();
			while(it.hasNext()) {
				String key = (String) it.next();
				Object value = item.get(key);
				String columnType = columnMap.get(key);
				if(columnType.startsWith("int")) {
					try {
						int columnValue = Integer.parseInt((String)value);
						mapData.put(key, columnValue);
					} catch(Exception e) {
						mapData.put(key, null);
					}
				} else if(columnType.startsWith("double")) {
					try {
						double columnValue = Double.parseDouble((String)value);
						mapData.put(key, columnValue);
					} catch(Exception e) {
						mapData.put(key, null);
					}
				}  else {
					mapData.put(key, (String)value);
				}
			}
			insertDataList.add(mapData);
		}
		return insertDataList;
	}
	
	public List analystChnMulDay(String data, Map<String, String> columnMap) {
		Gson gson = new Gson();
		Object o = gson.fromJson(data, Object.class);
		List insertDataList = new ArrayList();
		com.google.gson.internal.LinkedTreeMap linkedTreeMap = (com.google.gson.internal.LinkedTreeMap) o;
		ArrayList ds = (ArrayList) linkedTreeMap.get("DS");
		if(ds == null) return null;
		for(int i=0; i<ds.size(); i++) {
			com.google.gson.internal.LinkedTreeMap item = (com.google.gson.internal.LinkedTreeMap)ds.get(i);
			String existDataKey = item.get("Datetime") + "_" + item.get("Station_Id_C");
//			if(existData.containsKey(existDataKey)) {
//				continue;
//			}
			Set set = item.keySet();
			Iterator it = set.iterator();
			Map<String, Object> mapData = new HashMap<String, Object>();
			while(it.hasNext()) {
				String key = (String) it.next();
				Object value = item.get(key);
				String columnType = columnMap.get(key);
				if(columnType.startsWith("int")) {
					try {
						int columnValue = Integer.parseInt((String)value);
						mapData.put(key, columnValue);
					} catch(Exception e) {
						mapData.put(key, null);
					}
				} else if(columnType.startsWith("double")) {
					try {
						double columnValue = Double.parseDouble((String)value);
						mapData.put(key, columnValue);
					} catch(Exception e) {
						mapData.put(key, null);
					}
				}  else {
					mapData.put(key, (String)value);
				}
			}
			insertDataList.add(mapData);
//			System.out.println(item);
		}
//		System.out.println(ds);
		return insertDataList;
	}
}
