package com.spd.common;
/**
 * 常年统计下的低温，结果类
 * @author Administrator
 *
 */
public class LowTmpYearsResult {
	//年
	private double year;
	//总出现次数，指定年
	private double sum;
	//常年出现次数
	private double sumYears;
	//距平率
	private double sumAnomalyRate;
	//程度一般总出现次数，指定年
	private double normalSum;
	//程度一般常年出现次数
	private double normalSumYears;
	//程度一般距平率
	private double normalAnomalyRate;
	//程度严重总出现次数，指定年
	private double seriousnessSum;
	//程度严重常年出现次数
	private double seriousnessSumYears;
	//程度严重距平率
	private double seriousnessAnomalyRate;
	public double getYear() {
		return year;
	}
	public void setYear(double year) {
		this.year = year;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
	public double getSumYears() {
		return sumYears;
	}
	public void setSumYears(double sumYears) {
		this.sumYears = sumYears;
	}
	
	public double getSumAnomalyRate() {
		return sumAnomalyRate;
	}
	public void setSumAnomalyRate(double sumAnomalyRate) {
		this.sumAnomalyRate = sumAnomalyRate;
	}
	public double getNormalSum() {
		return normalSum;
	}
	public void setNormalSum(double normalSum) {
		this.normalSum = normalSum;
	}
	public double getNormalSumYears() {
		return normalSumYears;
	}
	public void setNormalSumYears(double normalSumYears) {
		this.normalSumYears = normalSumYears;
	}
	public double getNormalAnomalyRate() {
		return normalAnomalyRate;
	}
	public void setNormalAnomalyRate(double normalAnomalyRate) {
		this.normalAnomalyRate = normalAnomalyRate;
	}
	public double getSeriousnessSum() {
		return seriousnessSum;
	}
	public void setSeriousnessSum(double seriousnessSum) {
		this.seriousnessSum = seriousnessSum;
	}
	public double getSeriousnessSumYears() {
		return seriousnessSumYears;
	}
	public void setSeriousnessSumYears(double seriousnessSumYears) {
		this.seriousnessSumYears = seriousnessSumYears;
	}
	public double getSeriousnessAnomalyRate() {
		return seriousnessAnomalyRate;
	}
	public void setSeriousnessAnomalyRate(double seriousnessAnomalyRate) {
		this.seriousnessAnomalyRate = seriousnessAnomalyRate;
	}
	
	
	
}
