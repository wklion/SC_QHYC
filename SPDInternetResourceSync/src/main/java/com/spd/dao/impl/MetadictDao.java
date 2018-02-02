package com.spd.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.spd.common.ResourceItem;
import com.spd.dao.BaseDao;

public class MetadictDao extends BaseDao {

	//获取t_metadict字典表中需要从互联网上抓取的记录。
	private String QUERYINTERNETMETA = "select productCode, interfaceAddress, storePath, isUTC, fileTimeStr, URL, id, level1name, level2name, level3name, productName, attribute1 from t_ProductAttribute where syncType = '网站抓取' and URL != ''";
	
	//获取t_metadict字典表中获取科研所的共享的服务产品
	private String QUERYKEYANSUOMETA = "select * from t_ProductAttribute where level3name = '科研所数值预报产品'";
	//获取FTP下的Word产品
	private String QUERWORDMETA = "select * from t_metadict where display like 'word' and productCode != ''";
	//获取气象台下的产品，不包含word
	private String QUERQXTMETA = "select * from t_ProductAttribute where productCode >= 'M.7200.0198.' and productCode <= 'M.7200.0201.'";
	
	public List<ResourceItem> getQXTMeta() {
		return getMeta(QUERQXTMETA);
	}
	
	private List<ResourceItem> getMeta(String query) {
		List<ResourceItem> listResources = new ArrayList<ResourceItem>(); 
		List list = query(getConn(), query, null);
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				ResourceItem item = new ResourceItem();
				item.setStorePath(tempMap.get("storePath") + "");
				if((Integer)tempMap.get("isUTC") == 1) {
					item.setUTC(true);
				} else {
					item.setUTC(false);
				}
				item.setURL(tempMap.get("URL") + "");
				item.setFileTimeStr(tempMap.get("fileTimeStr") + "");
				item.setId(Integer.parseInt(tempMap.get("id") + ""));
				item.setInterfaceAddress(tempMap.get("interfaceAddress") + "");
				item.setProductCode(tempMap.get("productCode") + "");
				item.setSavePath(tempMap.get("storePath") + "/" + tempMap.get("level1name") + "/" + tempMap.get("level2name") + "/" + tempMap.get("level3name") + "/" + tempMap.get("productName") + "/" + tempMap.get("attribute1"));
				item.setUsername(tempMap.get("username") + "");
				item.setPassword(tempMap.get("password") + "");
				item.setTimeFormat(tempMap.get("timeFormat") + "");
				listResources.add(item);
			}
		}
		return listResources;
	}
	/**
	 * 获取FTP下的word产品
	 * @return
	 */
	public List<ResourceItem> getWordMeta() {
		return getMeta(QUERWORDMETA);
	}
	
	/**
	 * 获取需要从互联网上抓取的图片的配置信息
	 * @return
	 */
	public List<ResourceItem> getInternetMeta() {
		List<ResourceItem> listResources = new ArrayList<ResourceItem>(); 
		List list = query(getConn(), QUERYINTERNETMETA, null);
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				ResourceItem item = new ResourceItem();
				item.setStorePath(tempMap.get("storePath") + "");
				if((Integer)tempMap.get("isUTC") == 1) {
					item.setUTC(true);
				} else {
					item.setUTC(false);
				}
				item.setURL(tempMap.get("URL") + "");
				item.setFileTimeStr(tempMap.get("fileTimeStr") + "");
				item.setId(Integer.parseInt(tempMap.get("id") + ""));
				item.setInterfaceAddress(tempMap.get("interfaceAddress") + "");
				item.setProductCode(tempMap.get("productCode") + "");
				item.setSavePath(tempMap.get("storePath") + "/" + tempMap.get("level1name") + "/" + tempMap.get("level2name") + "/" + tempMap.get("level3name") + "/" + tempMap.get("productName") + "/" + tempMap.get("attribute1"));
				listResources.add(item);
			}
		}
		return listResources;
	}
	
	/**
	 * 获取科研所的共享的服务产品
	 * @return
	 */
	public List<ResourceItem> getKeYanSuoSMBInternetMeta() {
		List<ResourceItem> listResources = new ArrayList<ResourceItem>(); 
		List list = query(getConn(), QUERYKEYANSUOMETA, null);
		if(list != null && list.size() > 0) {
			for(int i=0; i<list.size(); i++) {
				HashMap tempMap = (HashMap) list.get(i);
				ResourceItem item = new ResourceItem();
				item.setStorePath(tempMap.get("storePath") + "");
				if((Integer)tempMap.get("isUTC") == 1) {
					item.setUTC(true);
				} else {
					item.setUTC(false);
				}
				item.setURL(tempMap.get("URL") + "");
				item.setFileTimeStr(tempMap.get("fileTimeStr") + "");
				item.setId(Integer.parseInt(tempMap.get("id") + ""));
				item.setInterfaceAddress(tempMap.get("interfaceAddress") + "");
				item.setProductCode(tempMap.get("productCode") + "");
				String savepath =tempMap.get("storePath") + "/" + tempMap.get("level1name") + "/" + tempMap.get("level2name") + "/" + tempMap.get("level3name") + "/" + tempMap.get("productName") + "/" + tempMap.get("attribute1");
				if(tempMap.get("attribute2") != null && !"".equals(tempMap.get("attribute2"))) {
					savepath += tempMap.get("attribute2");
				}
				item.setSavePath(savepath);
				listResources.add(item);
			}
		}
		return listResources;
	}
}
