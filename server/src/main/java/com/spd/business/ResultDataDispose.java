package com.spd.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.spd.pojo.ExtTmpMaxItem;
import com.spd.pojo.ExtTmpMinItem;
import com.spd.pojo.ItemCnt;
import com.spd.pojo.ItemCommon;
import com.spd.pojo.PreCntItem;
import com.spd.pojo.PreTimeItem;
import com.spd.pojo.PrsAvgItem;
import com.spd.pojo.RHUItem;
import com.spd.pojo.SSHItem;
import com.spd.pojo.TmpAvgItem;
import com.spd.pojo.TmpGapAvgItem;
import com.spd.pojo.TmpMaxAvgItem;
import com.spd.pojo.TmpMaxCntItem;
import com.spd.pojo.TmpMinAvgItem;
import com.spd.pojo.VisMinItem;
import com.spd.pojo.Win_s_2mi_avgItem;

/**
 * 结果数据的组装
 * @author Administrator
 *
 */
public class ResultDataDispose {

	/**
	 * 统计历年平均气温同期
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object avgTmpMulYearDis(List<Map> list) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<TmpAvgItem> resultList = new ArrayList<TmpAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createTmpAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 统计历年高温均值同期
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object avgTmpMaxMulYearDis(List<Map> list) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<TmpMaxAvgItem> resultList = new ArrayList<TmpMaxAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createTmpMaxAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 统计历年气温日较差
	 * @param list
	 * @return
	 */
	public List<TmpGapAvgItem> tmpGapMulYearDis(List<Map> list) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<TmpGapAvgItem> resultList = new ArrayList<TmpGapAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createTmpGapAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 平均气温数据的组装，连续时间
	 * @param list
	 * @return
	 */
	public Object avgTmpDis(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<TmpAvgItem> resultList = new ArrayList<TmpAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "t_tem_avg");
		resultList = ResultDisposeTool.createTmpAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 计算日气温较差
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TmpGapAvgItem> avgTmpGapDis(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<TmpGapAvgItem> resultList = new ArrayList<TmpGapAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "v_tem_gap");
		resultList = ResultDisposeTool.createTmpGapAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 高温均值统计
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object avgTmpMaxDis(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<TmpMaxAvgItem> resultList = new ArrayList<TmpMaxAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "t_tem_max");
		resultList = ResultDisposeTool.createTmpMaxAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 统计历年低温均值同期
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object avgTmpMinMulYearDis(List<Map> list) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<TmpMinAvgItem> resultList = new ArrayList<TmpMinAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createTmpMinAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 低温均值统计
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object avgTmpMinDis(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<TmpMinAvgItem> resultList = new ArrayList<TmpMinAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "t_tem_min");
		resultList = ResultDisposeTool.createTmpMinAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 历年平均风速统计
	 * @param list
	 * @return
	 */
	public Object avgWin_s_2mi_avgMulYearDis(List<Map> list) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<Win_s_2mi_avgItem> resultList = new ArrayList<Win_s_2mi_avgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createWin_s_2mi_avgAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 按时段统计风速
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object avgWin_s_2mi_avgDis(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<Win_s_2mi_avgItem> resultList = new ArrayList<Win_s_2mi_avgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "t_win_s_2mi_avg");
		resultList = ResultDisposeTool.createWin_s_2mi_avgAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 按时段值统计平均气压
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object avgPrsAvgDis(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<PrsAvgItem> resultList = new ArrayList<PrsAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "t_prs_avg");
		resultList = ResultDisposeTool.createPrsavgAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	/**
	 * 按历年同期统计平均气压
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object avgPrsAvgYearsDis(List<Map> list) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<PrsAvgItem> resultList = new ArrayList<PrsAvgItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createPrsavgAvg(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 按时段值统计降水总量
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object preAvgByTimeRange(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<PreTimeItem> resultList = new ArrayList<PreTimeItem>();
		//不区分0808,0820,2020,2008
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "t_pre_time");
		resultList = ResultDisposeTool.createPreTimeItemSum(resultMap, ItemCommonMap);
		return resultList;
	}
	
	
	/**
	 * 按年统计降水总量
	 * @param list
	 * @return
	 */
	public Object avgPreTimeAvgYearsDis(List<Map> list, int years) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<PreTimeItem> resultList = new ArrayList<PreTimeItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createPreTimeItemSumByYears(resultMap, ItemCommonMap, years);
		return resultList;
	}
	
	/**
	 * 按时间范围统计日照对数
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object disSSHByTimeRange(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<SSHItem> resultList = new ArrayList<SSHItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "t_ssh");
		resultList = ResultDisposeTool.createSSHItemSum(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 按年统计日照对数总量
	 * @param list
	 * @return
	 */
	public Object avgSSHAvgYearsDis(List<Map> list, int years) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<SSHItem> resultList = new ArrayList<SSHItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createSSHItemSumByYears(resultMap, ItemCommonMap, years);
		return resultList;
	}
	
	/**
	 * 按时间范围统计相对湿度
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object disRHUAvgByTimeRange(List<Map> list, Date startDate, Date endDate) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<RHUItem> resultList = new ArrayList<RHUItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByRangeTimes(list, startDate, endDate, "t_rhu_avg");
		resultList = ResultDisposeTool.createRHUItemSum(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 按年统计日照对数总量
	 * @param list
	 * @return
	 */
	public Object avgRHUAvgYearsDis(List<Map> list, int years) {
		Map<String, ItemCommon> ItemCommonMap = ResultDisposeTool.createItemCommonMap(list);
		List<RHUItem> resultList = new ArrayList<RHUItem>();
		Map<String, ItemCnt> resultMap = ResultDisposeTool.sumItemsByStation(list);
		resultList = ResultDisposeTool.createRHUItemSumByYears(resultMap, ItemCommonMap);
		return resultList;
	}
	
	/**
	 * 统计能见度低值
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object disVisMinByTimeRange(List<Map> list, Date startDate, Date endDate) {
		List<VisMinItem> resultList = ResultDisposeTool.analystVisMin(list, startDate, endDate, "t_vis_min");
		return resultList;
	}
	
	/**
	 * 极端高温统计
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object disExtMaxTmpByTimeRange(List<Map> list, Date startDate, Date endDate) {
		List<ExtTmpMaxItem> resultList = ResultDisposeTool.analystExtMaxTmp(list, startDate, endDate, "t_tem_max");
		return resultList;
	}
	
	/**
	 * 历年同期高温统计
	 * @param list
	 * @return
	 */
	public Object disExtMaxTmpByYears(List<Map> list) {
		List<ExtTmpMaxItem> resultList = ResultDisposeTool.extMaxTmpByYears(list, "t_tem_max");
		return resultList;
	}
	
	/**
	 * 历年同期低温统计
	 * @param list
	 * @return
	 */
	public Object disExtMinTmpByYears(List<Map> list) {
		List<ExtTmpMinItem> resultList = ResultDisposeTool.extMinTmpByYears(list, "t_tem_min");
		return resultList;
	}
	
	/**
	 * 极端低温统计
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object disExtMinTmpByTimeRange(List<Map> list, Date startDate, Date endDate) {
		List<ExtTmpMinItem> resultList = ResultDisposeTool.analystExtMinTmp(list, startDate, endDate, "t_tem_min");
		return resultList;
	}
	
	/**
	 * 历年同期的能见度低值
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object disVisMinByYears(List<Map> list) {
		List<VisMinItem> resultList = ResultDisposeTool.VisMinByYears(list, "t_vis_min");
		return resultList;
	}
	
	/**
	 * 按时间段范围统计降水日数
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object queryPreCntByTimeRange(List<Map> list, Date startDate, Date endDate) {
		List<PreCntItem> resultList = ResultDisposeTool.queryPreCntByTimeRange(list, startDate, endDate, "t_pre_time_2020");
		return resultList;
	}
	
	/**
	 * 按时间段范围统计高温日数
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object queryTmpMaxCntByTimeRange(List<Map> list, Date startDate, Date endDate) {
		List<TmpMaxCntItem> resultList = ResultDisposeTool.queryTmpMaxCntByTimeRange(list, startDate, endDate, "t_tem_max");
		return resultList;
	}
	
	
	/**
	 * 历年同期统计降水日数
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object queryPreCntByYears(List<Map> list, int years) {
		List<PreCntItem> resultList = ResultDisposeTool.queryPreCntByYears(list, years, "t_pre_time_2020");
		return resultList;
	}
	
	/**
	 * 历年同期统计高温日数
	 * @param list
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Object queryTmpMaxCntByYears(List<Map> list, int years) {
		List<TmpMaxCntItem> resultList = ResultDisposeTool.queryTmpMaxCntByYears(list, years, "t_tem_max");
		return resultList;
	}
}
