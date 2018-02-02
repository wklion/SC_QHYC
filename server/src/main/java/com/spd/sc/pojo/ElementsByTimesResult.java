package com.spd.sc.pojo;

import java.util.List;

/**
 * 常规要素查询的结果类
 * @author Administrator
 *
 */
public class ElementsByTimesResult {
	//结果类数组对象
	private List<ElementsByTimesResultItem> resultList;

	public List<ElementsByTimesResultItem> getResultList() {
		return resultList;
	}

	public void setResultList(List<ElementsByTimesResultItem> resultList) {
		this.resultList = resultList;
	}
	
}
