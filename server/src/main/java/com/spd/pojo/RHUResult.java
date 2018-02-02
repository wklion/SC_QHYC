package com.spd.pojo;

import java.util.List;

/**
 * 返回到客户端的结构
 * @author Administrator
 *
 */
public class RHUResult {

	private ResultDesc desc;
	
	private List<RHU> list;

	public ResultDesc getDesc() {
		return desc;
	}

	public void setDesc(ResultDesc desc) {
		this.desc = desc;
	}

	public List<RHU> getList() {
		return list;
	}

	public void setList(List<RHU> list) {
		this.list = list;
	}

	
}
