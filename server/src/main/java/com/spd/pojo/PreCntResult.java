package com.spd.pojo;

import java.util.List;

/**
 * 返回到客户端的结构
 * @author Administrator
 *
 */
public class PreCntResult {

	private ResultDesc desc;
	
	private List<PreCnt> list;

	public ResultDesc getDesc() {
		return desc;
	}

	public void setDesc(ResultDesc desc) {
		this.desc = desc;
	}

	public List<PreCnt> getList() {
		return list;
	}

	public void setList(List<PreCnt> list) {
		this.list = list;
	}

	
}
