package com.spd.address.impl;

import java.util.ArrayList;
import java.util.List;

import com.spd.address.IAddressManager;
import com.spd.common.Resource;
/**
 * ResourceConfig.xml中type为1的类型
 * @author xianchao
 *
 */
public class Type1AddressManagerImpl implements IAddressManager {

	/**
	 * 直接是图片绝对地址或者是图片和图片的正则表达式两种情况。
	 */
	public List<String> getImageAddress(Resource resource) {
		String imageAddress = resource.getUrlAddress();
		String imageRegex = resource.getImageRegex();
		List<String> resultList = CommonAddressManager.getValidAddress(imageAddress, imageRegex, resource.isUTC());
//		List<String> resultList = new ArrayList<String>();
//		resultList.add(imageAddress);
		return resultList;
	}

}
