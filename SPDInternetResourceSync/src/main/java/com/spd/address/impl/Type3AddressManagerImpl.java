package com.spd.address.impl;

import java.util.ArrayList;
import java.util.List;

import com.spd.address.IAddressManager;
import com.spd.common.Resource;
/**
 * ResourceConfig.xml中type为3的类型
 * @author xianchao
 *
 */
public class Type3AddressManagerImpl implements IAddressManager {

	public List<String> getImageAddress(Resource resource) {
		List<String> resultImagePathList = new ArrayList<String>();
		String urlAddress = resource.getUrlAddress();
//		String urlAddressRegex = resource.getUrlAddressRegex();
		//第一层地址匹配
		List<String> resultUrlAddressList = CommonAddressManager.getValidImgAddress(urlAddress, resource.isUTC());
//		String subAddress = resource.getSubAddress();
		String imageRegex = resource.getImageRegex();
		//第二次匹配的地址
//		String subAddress = resource.getSubAddress();
		String subUrlAddressRegex = resource.getSubAddressRegex();
		//如果urlAddressRegex不为空，则继续匹配新的地址，否则到此为止，以这个网址为图片所在的网址，开始查找图片
		if(subUrlAddressRegex == null || subUrlAddressRegex.equals("")) {
			for(String resultUrlAddress : resultUrlAddressList) {
				List<String> imageSubAddressList = CommonAddressManager.getHrefPath(resultUrlAddress, imageRegex, resource.isUTC());
				if(imageSubAddressList != null && imageSubAddressList.size() > 0) {
					for(String imageSubAddress : imageSubAddressList) {
						resultImagePathList.add(imageSubAddress);
//						List<String> imagePathList = CommonAddressManager.getImagePath(subAddress, imageRegex);
//						for(String imagePath : imagePathList) {
//							resultImagePathList.add(imagePath);
//						}
					}
				}
			}
		} else {
			for(String resultUrlAddress : resultUrlAddressList) {
				List<String> urlAddressList = CommonAddressManager.getHrefPath(resultUrlAddress, subUrlAddressRegex, resource.isUTC());
				for(String url : urlAddressList) {
					List<String> imageSubAddressList = CommonAddressManager.getHrefPath(url, imageRegex, resource.isUTC());
					if(imageSubAddressList != null && imageSubAddressList.size() > 0) {
						for(String imageSubAddress : imageSubAddressList) {
							resultImagePathList.add(imageSubAddress);
						}
					}
				}
				
			}
		}
//		subAddress = CommonAddressManager.getValidAddress(subAddress, subAddressRegex);
		
		return resultImagePathList;
	}

}
