define(['jquery','commonfun','ChartService','stationutil','date','data'],function($,cf,cs,su,date,dat){
	return{
		init:function(){
			//扩展
			//cf.KuoZhan();
			//注册事件
			$("#date button").bind("click",date.CD_Click);
			$("#date button:first").click();
			$("#spread").bind("click",cf.MapSpread);
		    //$("#dismap").bind("click",cf.ActiveButton);
		    //$("#dischart").bind("click",cf.ActiveButton);
		    $("#disscstation").bind("click",su.displayonmap);
		    $("#disxnstation").bind("click",su.displayonmap);
		    //$("#dismap").bind("click",cf.ChangeDisplay);
		    //$("#dischart").bind("click",cf.ChangeDisplay);
		    $("#dealdata").bind("click",cf.ActiveButton);
		    $("#dealdata").bind("click",dat.HResDeal);
		    $("#test").bind("click",cf.Test);
		    /*cf.GetStation(function(data){//获取站点数据
		    	var size=data.length;
		    	var strHtml='';
		    	for(var i=0;i<size;i++){
		    		var val=data[i];
		    		strHtml+='<option>'+val+'</option>';
		    	}
		    	$("#station select").html(strHtml);
		    });*/
		    //初始化图表控件
		    //cs.init();
		    //初始化地图
		    //$("#dismap").click();
		    //日期
		    require(['bootpick'],function(){
		    	/*$("#datepicker").datepicker({
		    		todayBtn: true,
		    		autoclose: true,
		    		todayHighlight:true,
		    		startView:1,
		    		minView:0
		    	});*/
		    	/*var date=new Date();
		    	$("#datepicker").datepicker('setDate',date);*/
		    	$("#datepicker").datepicker();
		    });
		}
	}
});