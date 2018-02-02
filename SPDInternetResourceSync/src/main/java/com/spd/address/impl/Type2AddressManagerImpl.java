package com.spd.address.impl;

import java.util.ArrayList;
import java.util.List;

import com.spd.address.IAddressManager;
import com.spd.common.Resource;
/**
 * ResourceConfig.xml中type为2的类型
 * @author xianchao
 *
 */
public class Type2AddressManagerImpl implements IAddressManager {

	/**
	 * 网站地址，地址里有很多IMG标签，通过这些标签，找到图片，再匹配图片规则来判断是否合法
	 */
	public List<String> getImageAddress(Resource resource) {
		//这个即为图片的连接地址
		String urlAddress = resource.getUrlAddress();
		String urlAddressRegex = resource.getUrlAddressRegex();
//		urlAddress = CommonAddressManager.getValidAddress(urlAddress, urlAddressRegex, resource.isUTC());
		List<String> urlAddressList = CommonAddressManager.getValidAddress(urlAddress, urlAddressRegex, resource.isUTC());
		List<String> resultImageAddress = new ArrayList<String>();
		for(String tempUrlAddress:urlAddressList) {
			List<String> resultUrlAddressList = CommonAddressManager.getValidImgAddress(tempUrlAddress, resource.isUTC());
			String imageRegex = resource.getImageRegex();
			for(String resultUrlAddress : resultUrlAddressList) {
				List<String> tempImageAddress = CommonAddressManager.getImagePath(resultUrlAddress, imageRegex, resource.isUTC());
				resultImageAddress.addAll(tempImageAddress);
			}
		}
		return resultImageAddress;
	}

}
