package com.spd.pojo;

import java.util.List;

/**
 * 返回到客户端的结构
 * @author Administrator
 *
 */
public class TmpMaxCntResult {

	private ResultDesc desc;
	
	private List<TmpMaxCnt> list;

	public ResultDesc getDesc() {
		return desc;
	}

	public void setDesc(ResultDesc desc) {
		this.desc = desc;
	}

	public List<TmpMaxCnt> getList() {
		return list;
	}

	public void setList(List<TmpMaxCnt> list) {
		this.list = list;
	}

	
}
