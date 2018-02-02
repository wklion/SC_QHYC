package com.spd.pojo;

import java.util.List;

/**
 * 返回到客户端的结构
 * @author Administrator
 *
 */
public class ExtTmpResult {

	private ResultDesc desc;
	
	private List<ExtTmp> list;

	public ResultDesc getDesc() {
		return desc;
	}

	public void setDesc(ResultDesc desc) {
		this.desc = desc;
	}

	public List<ExtTmp> getList() {
		return list;
	}

	public void setList(List<ExtTmp> list) {
		this.list = list;
	}

	
}
