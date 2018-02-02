package com.spd.address.impl;

import java.util.List;

import com.spd.address.IAddressManager;
import com.spd.common.Resource;
/**
 * ResourceConfig.xml中type为4的类型
 * @author xianchao
 *
 */
public class Type4AddressManagerImpl implements IAddressManager {

	public List<String> getImageAddress(Resource resource) {
		String imageAddress = resource.getUrlAddress();
		List<String> resultList = CommonAddressManager.getValidImgAddress(imageAddress, resource.isUTC());
		return resultList;
	}

	public List<String> getImageAddress(String imageAddress, boolean isUTC) {
		List<String> resultList = CommonAddressManager.getValidImgAddress(imageAddress, isUTC);
		return resultList;
	}
	
}
