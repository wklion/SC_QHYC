package com.spd.pojo;

import java.util.List;

/**
 * 返回到客户端的结构
 * @author Administrator
 *
 */
public class VisMinResult {

	private ResultDesc desc;
	
	private List<VisMin> list;

	public ResultDesc getDesc() {
		return desc;
	}

	public void setDesc(ResultDesc desc) {
		this.desc = desc;
	}

	public List<VisMin> getList() {
		return list;
	}

	public void setList(List<VisMin> list) {
		this.list = list;
	}

	
}
