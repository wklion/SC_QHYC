package com.spd.address.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.spd.address.IAddressManager;
import com.spd.common.Resource;
/**
 * 基于目录结构下的图片访问，
 * @author xianchao
 *
 */
public class Type5AddressManagerImpl implements IAddressManager {

	public List<String> getImageAddress(Resource resource) {
		return null;
	}
	
	public List<String> getImageAddress(String imageAddress, boolean isUTC) {
		
		List<String> resultList = CommonAddressManager.getValidImgAddress(imageAddress, isUTC);
		return resultList;
	}

	public static void main(String[] args) throws Exception {
		URL url = new URL("http://10.1.64.154/imageService/nmc/P_WEAP-S99-EME-ACHN-L70-P9/");
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
//		OutputStream out = connection.getOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		while((length = in.read(buffer)) != -1) {
			System.out.println(new String(buffer, 0, length));
		}
//		String path = url.getPath();
//		System.out.println(path);
	}
}
