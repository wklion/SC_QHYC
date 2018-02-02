package com.spd.address;

import java.util.List;

import com.spd.common.Resource;

public interface IAddressManager {
	/**
	 * 根据Resource中的配置来获取该配置信息下对应的URL中满足要求的图片地址集合
	 * @param resource
	 * @return
	 */
	public List<String> getImageAddress(Resource resource);
	
}
