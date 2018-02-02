package com.spd.common;

/**
 * 历年同期降水统计，分析结果类
 * @author Administrator
 *
 */
public class ContinueousRainYearsResult {
	//年份
	private Integer year;
	//总次数
	private Double cnt;
	//常年总次数
	private Double contrastCnt;
	//距平
	private Double cntAnomalyRatio;
	//轻度次数
	private Double slightCnt;
	//常年轻度次数
	private Double contrastSlightCnt;
	//轻度距平率
	private double slightCntAnomalyRatio;
	//严重次数
	private Double severityCnt;
	//常年严重次数
	private Double contrastSeverityCnt;
	//严重距平率
	private double severityCntAnomalyRatio;
	
	
	
	public Integer getYear() {
		return year;
	}



	public void setYear(Integer year) {
		this.year = year;
	}



	public Double getCnt() {
		return cnt;
	}



	public void setCnt(Double cnt) {
		this.cnt = cnt;
	}



	public Double getContrastCnt() {
		return contrastCnt;
	}



	public void setContrastCnt(Double contrastCnt) {
		this.contrastCnt = contrastCnt;
	}



	public Double getCntAnomalyRatio() {
		return cntAnomalyRatio;
	}



	public void setCntAnomalyRatio(Double cntAnomalyRatio) {
		this.cntAnomalyRatio = cntAnomalyRatio;
	}



	public Double getSlightCnt() {
		return slightCnt;
	}



	public void setSlightCnt(Double slightCnt) {
		this.slightCnt = slightCnt;
	}



	public Double getContrastSlightCnt() {
		return contrastSlightCnt;
	}



	public void setContrastSlightCnt(Double contrastSlightCnt) {
		this.contrastSlightCnt = contrastSlightCnt;
	}



	public double getSlightCntAnomalyRatio() {
		return slightCntAnomalyRatio;
	}



	public void setSlightCntAnomalyRatio(double slightCntAnomalyRatio) {
		this.slightCntAnomalyRatio = slightCntAnomalyRatio;
	}



	public Double getSeverityCnt() {
		return severityCnt;
	}



	public void setSeverityCnt(Double severityCnt) {
		this.severityCnt = severityCnt;
	}



	public Double getContrastSeverityCnt() {
		return contrastSeverityCnt;
	}



	public void setContrastSeverityCnt(Double contrastSeverityCnt) {
		this.contrastSeverityCnt = contrastSeverityCnt;
	}



	public double getSeverityCntAnomalyRatio() {
		return severityCntAnomalyRatio;
	}



	public void setSeverityCntAnomalyRatio(double severityCntAnomalyRatio) {
		this.severityCntAnomalyRatio = severityCntAnomalyRatio;
	}



	public ContinueousRainYearsResult copy() {
		ContinueousRainYearsResult item = new ContinueousRainYearsResult();
		item.setCnt(cnt);
		item.setCntAnomalyRatio(cntAnomalyRatio);
		item.setContrastCnt(contrastCnt);
		item.setContrastSeverityCnt(contrastSeverityCnt);
		item.setContrastSlightCnt(contrastSlightCnt);
		item.setSeverityCnt(contrastSeverityCnt);
		item.setSeverityCntAnomalyRatio(severityCntAnomalyRatio);
		item.setSlightCnt(contrastSlightCnt);
		item.setSlightCntAnomalyRatio(slightCntAnomalyRatio);
		item.setYear(year);
		return item;
	}
}
