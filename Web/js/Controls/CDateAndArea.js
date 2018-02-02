/**
 * 作者：	 wangkun
 * 邮箱:     304413137@qq.com  
 * 创建时间: 2016-05-10
 * 说明:     系统测评
 */
function CDateAndArea(panelid) {
	var t=this;
	var isMax=true;
	var strHtml="<div id='dateandarea_div'></div>";
	$("#"+panelid).append(strHtml);
	strHtml="<div id='daHead_div'><span class='iconfont'>&#xe611;</span></div><div id='daContent_div'></div>";
	$("#dateandarea_div").append(strHtml);
	strHtml="<span>日期</span><input type='text' id='datepicker'><hr>";
	$("#daContent_div").append(strHtml);
	strHtml="<span>区域</span><select id='daArea'><option value='盆地西北部'>盆地西北部</option><option value='盆地南部'>盆地南部</option>"
	+"<option value='攀西地区'>攀西地区</option><option value='盆地中部'>盆地中部</option><option value='盆地西南部'>盆地西南部</option>"
	+"<option value='盆地东北部'>盆地东北部</option><option value='川西高原'>川西高原</option></select>";
	$("#daContent_div").append(strHtml);
	$("#datepicker").datepicker();
	$("#datepicker").datepicker("option","dateFormat","yy-mm-dd");
	var date=new Date().format("yyyy-MM-dd");
	$("#datepicker").val(date);
	$("#daHead_div").bind("click",MaxOrMin);
	function MaxOrMin(){
		if(isMax){//变小
			$("#daContent_div").hide();
			isMax=false;
		}
		else{
			$("#daContent_div").show();
			isMax=true;
		}
	}
}