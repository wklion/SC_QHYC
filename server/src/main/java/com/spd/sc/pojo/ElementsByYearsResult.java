package com.spd.sc.pojo;

import java.util.List;

/**
 * 历年要素查询的结果类
 * @author Administrator
 *
 */
public class ElementsByYearsResult {
	//结果类数组对象
	private List<ElementsByYearsResultItem> resultList;

	public List<ElementsByYearsResultItem> getResultList() {
		return resultList;
	}

	public void setResultList(List<ElementsByYearsResultItem> resultList) {
		this.resultList = resultList;
	}

}
