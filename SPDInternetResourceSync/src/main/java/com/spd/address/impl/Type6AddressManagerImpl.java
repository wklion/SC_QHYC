package com.spd.address.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.spd.address.IAddressManager;
import com.spd.common.Resource;
/**
 * 根据请求地址的上级路径找到文件路径下的全部图片。
 * @author xianchao
 *
 */
public class Type6AddressManagerImpl implements IAddressManager {

	public List<String> getImageAddress(Resource resource) {
		return null;
	}

	public List<String> getImageAddress(String address) throws Exception {
		URL url = new URL(address);
		List<String> urlList = new ArrayList<String>();
		Document doc = null;
		try {
			doc = Jsoup.parse(url, 60*1000);
			Elements tableElements = doc.getElementsByTag("table");
			for(int i=0; i<tableElements.size(); i++) {
				Element itemTable = tableElements.get(i);
				Elements hrefElements = itemTable.getElementsByTag("a");
				for(int j=0; j<hrefElements.size(); j++) {
					Element hrefElement = hrefElements.get(j);
					String href =  hrefElement.absUrl("href");
					urlList.add(href);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return urlList;
	}
	
}
