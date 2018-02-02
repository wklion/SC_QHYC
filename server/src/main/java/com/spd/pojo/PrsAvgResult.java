package com.spd.pojo;

import java.util.List;

/**
 * 返回到客户端的结构
 * @author Administrator
 *
 */
public class PrsAvgResult {

	private ResultDesc desc;
	
	private List<PrsAvg> list;

	public ResultDesc getDesc() {
		return desc;
	}

	public void setDesc(ResultDesc desc) {
		this.desc = desc;
	}

	public List<PrsAvg> getList() {
		return list;
	}

	public void setList(List<PrsAvg> list) {
		this.list = list;
	}

	
}
