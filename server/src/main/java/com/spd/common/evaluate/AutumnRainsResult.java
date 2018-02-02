package com.spd.common.evaluate;

import java.util.List;

public class AutumnRainsResult {
	//表格中的内容
	private List<AutumnRainsItemResult> autumnRainsItemResultList;
	//长度指数常年值 (表格中不展示)
	private Double contrastLengthIndexI;
	//雨量指数常年值（表格中不展示）
	private Double contrastPreIndex;
	//综合强度指数常年值（表格中不展示）
	private Double contrastIntensityIndex;
	
	public List<AutumnRainsItemResult> getAutumnRainsItemResultList() {
		return autumnRainsItemResultList;
	}
	public void setAutumnRainsItemResultList(
			List<AutumnRainsItemResult> autumnRainsItemResultList) {
		this.autumnRainsItemResultList = autumnRainsItemResultList;
	}
	public Double getContrastLengthIndexI() {
		return contrastLengthIndexI;
	}
	public void setContrastLengthIndexI(Double contrastLengthIndexI) {
		this.contrastLengthIndexI = contrastLengthIndexI;
	}
	public Double getContrastPreIndex() {
		return contrastPreIndex;
	}
	public void setContrastPreIndex(Double contrastPreIndex) {
		this.contrastPreIndex = contrastPreIndex;
	}
	public Double getContrastIntensityIndex() {
		return contrastIntensityIndex;
	}
	public void setContrastIntensityIndex(Double contrastIntensityIndex) {
		this.contrastIntensityIndex = contrastIntensityIndex;
	}
	
}
