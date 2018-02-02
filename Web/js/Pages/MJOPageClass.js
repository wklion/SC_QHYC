/**
 * @author: wangkun
 * @date:   2016/9/22.
 * @description MJO指数
 */
function MJOPageClass(){
	var t=this;
	t.element=["RMM1","RMM2","MJO"];
	t.searchdays=40;//最大搜索天数
	t.hdays=41;//波峰后推天数
	t.ldays=21;//波谷后推天数
	t.forcastdays=3;//预报天数
	t.currentele="RMM1";
	t.times=[];
	t.values=[];
	t.upTime="";
	t.downTime="";
	t.upIndex=0;//波峰位置
	t.downIndex=0;//波谷位置
	t.chart={};
	t.pentad="20e";
	t.selectedplotlineid="";//波峰或波谷标志
	t.xIndex=1;//x轴值
	this.renderMenu= function () {
		//设置
		var strhtml='<div class="commonline" style="text-align: right;"><button id="btnsettting"></button></div>';
		$("#menu_bd").html(strhtml);

		strhtml='<div class="panel panel-default">'
		+'<div class="panel-heading">条件设置</div>'
		+'<div class="panel-body">'
		+'<div class="row divmarginb"><div class="col-md-4 text-right">开始时间:</div><div class="col-md-8 text-left"><input type="text" id="sdatepicker"></div></div>'
		+'<div class="row divmarginb"><div class="col-md-4 text-right">结束时间:</div><div class="col-md-8 text-left"><input type="text" id="edatepicker"></div></div>'
		+'<div class="row divmarginb"><div class="col-md-4 text-right">要&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;素:</div><div class="col-md-8 text-left"><select id="selectele"><option>RMM1</option><option>RMM2</option><option>MJO</option></select></div></div>'
		+'<div class="row divmarginb"><div class="col-md-12 text-center"><button id="viewdata" class="btn btn-default btnfull80">显示结果</button></div></div>';
		$("#menu_bd").append(strhtml);
		strhtml='<div class="panel panel-default">'
		+'<div class="panel-heading">模型计算</div>'
		+'<div class="panel-body">'
		+'<div class="row divmarginb"><div class="col-md-12 text-center">'
		+'<div class="btn-group btnfull80" id="selectfg">'
		+'<button class="btn btn-default dropdown-toggle" data-toggle="dropdown" style="width:100%;">选择波峰波谷</button>'
		+'<ul class="dropdown-menu" role="menu">'
		+'<li><a href="#" id="selectbf">波峰</a></li>'
		+'<li><a href="#" id="selectbg">波谷</a></li>'
		+'</ul>'
		+'</div></div></div>'
		+'<div class="row divmarginb"><div class="col-md-12 text-center"><button class="btn btn-default btnfull80" id="recal">显示结果</button></div></div></div>';
		$("#menu_bd").append(strhtml);
		strhtml='<div id="MJOView"><div id="MJOChartDiv" style="height:80%;width:100%;"></div><div id="MJOResultDiv" style="height:20%;border-top: 1px solid black;"></div></div>';
		$("#workspace_div").append(strhtml);
		/*s初始化时间*/
		var datenow=new Date();
		$("#sdatepicker").datepicker();
		$("#sdatepicker").datepicker("option","dateFormat","yy-mm-dd");
		datenow.addDays(-60);//开始时间向后60天
		var sdate=datenow.format("yyyy-MM-dd");
		$("#sdatepicker").val(sdate);

		$("#edatepicker").datepicker();
		$("#edatepicker").datepicker("option","dateFormat","yy-mm-dd");
		sdate=new Date().addDays(-1).format("yyyy-MM-dd");
		$("#edatepicker").val(sdate);
		/*e初始化时间*/
		$("#btnsettting").bind("click",t.showSetting);
		$("#selectele").change(function(){
			t.CurrentElementChange();
		});
		$("#viewdata").bind("click",t.ViewData);
		$("#mapSwitch_div").hide();
		$("#selectfg a").bind("click",t.ActiveFG);
		$("#recal").bind("click",t.CalTxtResult);
	}
	this.showSetting=function(){
			//检查localstorage
			for(var i=0;i<t.element.length;i++){
				var ele=t.element[i];
				var val=localStorage.getItem(ele);
				if(val==undefined){//未找到，则添加，把默认值加进去
					var tempVal={searchdays:t.searchdays,hdays:t.hdays,ldays:t.ldays,forcastdays:t.forcastdays};
					var strJSON=JSON.stringify(tempVal);
					localStorage.setItem(ele,strJSON);
				}
			}
			if($("#divMsgBack").length==0){
				var strhtml='<div id="divMsgBack"></div><div id="divMsg"><a id="aClose">X</a><div id="divMsgContent">'
				+'<div class="flex" id="divds"><span class="title3 conC onepart">数据源:</span><span class="onepart"><select><option>RMM1</option><option>RMM2</option><option>MJO</option></select></span></div>'
				+'<div class="flex" id="searchdays"><span class="title3 conC onepart">最大搜索天数：</span><span class="onepart"><input type="text" placeholder="40"/></span></div>'
				+'<div class="flex" id="hdays"><span class="title3 conC onepart">波峰后推天数：</span><span class="onepart"><input type="text" placeholder="41"/></span></div>'
				+'<div class="flex" id="ldays"><span class="title3 conC onepart">波谷后推天数：</span><span class="onepart"><input type="text" placeholder="40"/></span></div>'
				+'<div class="flex" id="forcastdays"><span class="title3 conC onepart">预报天数：</span><span class="onepart"><input type="text" placeholder="20"/></span></div>'
				+'<div class="flex" style="margin-left:79%;"><span class="conC" id="btnOK">确定</span></div></div></div>';
				$("body").append(strhtml);
				$("#aClose").bind("click",function(){
					document.body.id = '';
				});
				$("#divds").change(function(){
					t.ElementChange();
				});
				$("#btnOK").bind("click",function(){
					t.SaveProfile();
				});
				t.ElementChange();
		}
		document.body.id="msgBody";
	}
	//要素改变
	this.ElementChange=function(){
		var ele=$("#divds option:selected").val();
		var val=localStorage.getItem(ele);
		var json=jQuery.parseJSON(val);
		$("#searchdays input").val(json["searchdays"]);
		$("#hdays input").val(json["hdays"]);
		$("#ldays input").val(json["ldays"]);
		$("#forcastdays input").val(json["forcastdays"]);
	}
	//保存配置文件
	this.SaveProfile=function(){
		var ele=$("#divds option:selected").val();
		var searchdays=$("#searchdays input").val();
		var hdays=$("#hdays input").val();
		var ldays=$("#ldays input").val();
		var forcastdays=$("#forcastdays input").val();
		var tempVal={searchdays:searchdays,hdays:hdays,ldays:ldays,forcastdays:forcastdays};
		var strJSON=JSON.stringify(tempVal);
		localStorage.setItem(ele,strJSON);
	}
	//当前要素改变
	this.CurrentElementChange=function(){
		var ele=$("#selectele option:selected").val();
		t.currentele=ele;
		var val=localStorage.getItem(ele);
		var json=jQuery.parseJSON(val);
		t.searchdays=json.searchdays;
		t.hdays=json.hdays;
		t.ldays=json.ldays;
		t.forcastdays=json.forcastdays;
	}
	//显示RMM数据
	this.ViewData=function(){
		var startDate=$("#sdatepicker").val();
		var endDate=$("#edatepicker").val();
		var url="";
		var data="";
		if(t.currentele.toLowerCase()=="mjo")
		{
			url="services/EFSService/GetMJO";
			data={'para': '{"element":"'+t.currentele+'","startdate":"'+startDate+'","enddate":"'+endDate+'","pentad":"'+t.pentad+'"}'};
		}
		else
		{
			url="services/EFSService/GetRMM";
			data={'para': '{"element":"'+t.currentele+'","startdate":"'+startDate+'","enddate":"'+endDate+'"}'};
		}
		$.ajax({
            type: 'post',
            url: Url_Config.gridServiceUrl + url,
            data: data,
            dataType: 'json',
            error: function () {
                $("#div_progress").css("display", "none");
                alert('获取数据出错!');
            },
            success: function (data){
                $("#div_progress_title").html("数据处理完成!");
                $("#div_progress").fadeOut(2000,function(){});
                if(data==undefined||data.length<1){
                	return;
                }
                t.times=[];
                t.values=[];
                for(key in data){
                	t.times.push(key);
                	var val=data[key];
                	t.values.push(val);
                }
                t.UpdateChart(t.times,t.values);
                t.GetWaveUpDate();
				t.GetWaveDownDate();
				t.AddBiaoXian();
                t.CalTxtResult();
            }
        });
	}
	//更新Chart
	this.UpdateChart=function(labels,data){
		t.chart=new Highcharts.Chart({
			chart: {
				renderTo:'MJOChartDiv',
                type: 'spline',
                events:{
                	click:t.AddFlagLine
                }
            },
            title: {
                text: ''
            },
            xAxis: {
                categories: labels,
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0"></td>' 
                +'<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                footerFormat: '</table>',
                useHTML: true
            },
            plotOptions: {
            	spline: {
            		dataLabels: {
            			enabled: true
            		},
            	}
            },
            credits: {
                enabled: false
            },
            series: [{
                name: t.currentele,
                data:data,
                events:{
                	click:function(e){
                		if(t.selectedplotlineid!=""){
                			var index=e.point.index;
                			if(index<0){
                				return;
                			}
                			t.chart.xAxis[0].removePlotLine(t.selectedplotlineid);
                			t.chart.xAxis[0].addPlotLine({
						        color:'red',            //线的颜色，定义为红色
						        dashStyle:'dash',//标示线的样式，默认是solid（实线），这里定义为长虚线
						        value:index,                //定义在哪个值上显示标示线，这里是在x轴上刻度为3的值处垂直化一条线
						        width:2,                //标示线的宽度，2px
						        id:t.selectedplotlineid
	    					});
	    					t.selectedplotlineid="";
                		}
                	}
                }
            }]
            /*yAxis: {
                max: 1,
                title:{
                    text:''
                }
            },*/
		});
		}
	//计算波峰时间
	this.GetWaveUpDate=function(){
		var useData=[];
		var useTime=[];
		var dataSize=t.values.length;
		var sIndex=dataSize-t.searchdays>0?dataSize-t.searchdays:0;
		var needDataCount=sIndex>0?t.searchdays:dataSize;
		for(var i=0;i<needDataCount;i++)
		{
			var val=t.values[i+sIndex];
			var time=t.times[i+sIndex];
			useData.push(val);
			useTime.push(time);
		}
		var temUserData=useData.slice(0);
		var max=ArrayMax(temUserData);
		var index=temUserData.indexOf(max);
		var oldIndex=index,n=temUserData.length;
		while(temUserData.length>1&&(index==0||index==temUserData.length-1))
		{
			if(index==0)
			{
				temUserData=temUserData.slice(1);
			}
			else if(index==temUserData.length-1)
			{
				temUserData.pop();
			}
			max=ArrayMax(temUserData);
			index=temUserData.indexOf(max);
		}
		if(oldIndex+index==n-1&&(oldIndex==0||index==0))
			index=oldIndex;
		var upVal=useData[index];
		t.upTime=useTime[index];
		t.upIndex=sIndex+index;
	}
	//计算波谷时间
	this.GetWaveDownDate=function(){
		var dataSize=t.values.length;
		var useValues=[];
		var useDates=[];
		for(var i=t.upIndex;i<dataSize;i++)
		{
			var val=t.values[i];
			var date=t.times[i];
			useValues.push(val);
			useDates.push(date);
		}
		var temUserData=useValues.slice(0);
		var min=ArrayMin(temUserData);
		var index=temUserData.indexOf(min);
		var oldIndex=index,n=temUserData.length;
		while(temUserData.length>1&&(index==0||index==temUserData.length-1))
		{
			if(index==0)
			{
				temUserData=temUserData.slice(1);
			}
			else if(index==temUserData.length-1)
			{
				temUserData.pop();
			}
			min=ArrayMin(temUserData);
			index=temUserData.indexOf(min);
		}
		if(oldIndex+index==n-1&&(oldIndex==0||index==0))
			index=oldIndex;
		index+=t.upIndex;
		t.downIndex=index;
		t.downTime=t.times[index];
	}
	//计算降水时间
	this.CalRainTime=function(){
		var upDate=new Date(Date.parse(t.upTime.replace(/-/g,"/")));
		var downDate=new Date(Date.parse(t.downTime.replace(/-/g,"/")));
		upDate.addDays(t.hdays);
		downDate.addDays(t.ldays);
		var days=parseInt((upDate.getTime()-downDate.getTime())/(1000*3600*24*2));//后面那个2是取中间天数
		var midDate=downDate.addDays(days);
		var rainDate=[];
		var strStartDate=midDate.addDays(-3).format("yyyy-MM-dd");
		var strEndDate=midDate.addDays(6).format("yyyy-MM-dd");//由于-了3天，故+6天
		rainDate.push(strStartDate);
		rainDate.push(strEndDate);
		return rainDate;
	}
	//计算文字结果
	this.CalTxtResult=function() {
		var rainDate=t.CalRainTime();
		var result='前';
		result+=t.searchdays;//搜索天数
		result+='天';
		result+=t.currentele;//要素
		result+='的波峰日期是：';
		result+=t.upTime;//波峰
		result+='，波谷日期是：';
		result+=t.downTime;//波谷
		result+=';通过预测模型计算，强降水出现的日期为：';
		result+=rainDate[0];
		result+='至';
		result+=rainDate[1];
		result+='。';
		$("#MJOResultDiv").html(result);
	}
	this.Test=function(evt){
		alert("down");
	}
	//显示标线
	this.AddBiaoXian=function(){
		t.chart.xAxis[0].addPlotLine({
	        color:'red',            //线的颜色，定义为红色
	        dashStyle:'dash',//标示线的样式，默认是solid（实线），这里定义为长虚线
	        value:t.upIndex,                //定义在哪个值上显示标示线，这里是在x轴上刻度为3的值处垂直化一条线
	        width:4,                 //标示线的宽度，2px
	        id:"xbf"
    	});
    	t.chart.xAxis[0].addPlotLine({
	        color:'green',            //线的颜色，定义为红色
	        dashStyle:'dash',//标示线的样式，默认是solid（实线），这里定义为长虚线
	        value:t.downIndex,                //定义在哪个值上显示标示线，这里是在x轴上刻度为3的值处垂直化一条线
	        width:4,                //标示线的宽度，2px
	        id:"xbg"
    	});
	}
	//选择波峰波谷
	this.ActiveFG=function(){
		var id=this.id;
		if(id=="selectbf"){
			t.selectedplotlineid="xbf";
		}
		else if(id=="selectbg"){
			t.selectedplotlineid="xbg";
		}
	}
	//增加辅助线
	this.AddFlagLine=function(e){
		var val=e.xAxis[0].value;
		val=Math.floor(val);
		if(t.selectedplotlineid=="")
			return;
		var fid="";
		var color="";
		if(t.selectedplotlineid=="xbf"){
			color="red";
			t.upTime=t.chart.series[0].data[val].category;
		}
		else if(t.selectedplotlineid=="xbg"){
			color="green";
			t.downTime=t.chart.series[0].data[val].category;
		}
		t.chart.xAxis[0].removePlotLine(t.selectedplotlineid);
		t.chart.xAxis[0].addPlotLine({
	        color:color,            //线的颜色，定义为红色
	        dashStyle:'dash',//标示线的样式，默认是solid（实线），这里定义为长虚线
	        value:val,                //定义在哪个值上显示标示线，这里是在x轴上刻度为3的值处垂直化一条线
	        width:4,                //标示线的宽度，2px
	        id:t.selectedplotlineid
	    });

	}
}
MJOPageClass.prototype = new PageBase();