package com.spd.pojo;

import java.util.List;

/**
 * 返回到客户端的结构
 * @author Administrator
 *
 */
public class PreSumResult {

	private ResultDesc desc;
	
	private List<PreSum> list;

	public ResultDesc getDesc() {
		return desc;
	}

	public void setDesc(ResultDesc desc) {
		this.desc = desc;
	}

	public List<PreSum> getList() {
		return list;
	}

	public void setList(List<PreSum> list) {
		this.list = list;
	}

	
}
