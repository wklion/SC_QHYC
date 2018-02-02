package com.spd.pojo;

import java.util.List;

/**
 * 返回到客户端的结构
 * @author Administrator
 *
 */
public class AvgMaxTmpResult {

	private ResultDesc desc;
	
	private List<AvgMaxTmp> list;

	public ResultDesc getDesc() {
		return desc;
	}

	public void setDesc(ResultDesc desc) {
		this.desc = desc;
	}

	public List<AvgMaxTmp> getList() {
		return list;
	}

	public void setList(List<AvgMaxTmp> list) {
		this.list = list;
	}

	
}
