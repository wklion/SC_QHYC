/*诊断分析*/
define(['commonfun','stationutil','areautil','bootpick','mapUtil','ChartService'],function(cf,su,au,bp,mu,cs){
	let tempparam={
		avg:{name:'平均温度'},
		low:{name:'最低温度'},
		hight:{name:'最高温度'},
		jp:{name:'距平'}
	};
	let rainparam={
		dayrain:{name:'逐日降水量'},
		totalrain:{name:'累计降水量'},
		jp:{name:'降水距平'}
	};
	let other={
		season:{name:'季节时间'},
		pdrain:{name:'盆地大雨'},
		xnrain:{name:'西南雨季'},
		hxrain:{name:'华西秋雨'},
		pdgh:{name:'盆地干旱'}
	};
	var datetypeid="";
	var displayid="";
	return {
		Init:function(){//初始化
			$("#slider li").bind("click",cf.ActiveButton);
			$("#slider li").bind("click",this.LoadUI);
			$("#slider li:first").click();
		},
		LoadUI:function(){//加载UI
			var self=require('zdfx');
			let id=this.id;
			if(id=="sjcx"){
				self.InitSJCX();
			}
			else if(id=="zdfx"){
				self.InitZDFX();
			}
		},
		InitSJCX:function(){//数据查询
			let html='';
			//要素
			html+='<div id="element" class="panel panel-default">';
			html+='<div class="panel-heading">要素</div>';
			html+='<div class="panel-body">';
			html+='<div id="elementp" class="parentdiv">';
			html+='<button id="temp" class="btn btn-default w90">气温</button>';
			html+='<button id="rain" class="btn btn-default w90">降水</button>';
			html+='<button id="other" class="btn btn-default w90">其它</button>';
			html+='</div>';
			html+='<div id="elementc" class="childdiv"></div>';
			html+='</div>';
			html+='<div id="elementc" class="childdiv"></div>';
			html+='</div>';
			//站点
			html+='<div id="stationdiv" class="panel panel-default">';
			html+='<div class="panel-heading">站点</div>';
			html+='<div class="panel-body">';
			html+='<div id="areadiv" class="parentdiv">';
			html+='<button id="station" class="btn btn-default w60">站点</button>';
			html+='<button id="city" class="btn btn-default w60">市</button>';
			html+='<button id="area" class="btn btn-default w60">区域</button>';
			html+='<button id="privince" class="btn btn-default w60">全省</button>';
			html+='<button id="xn" class="btn btn-default w60">西南</button>';
			html+='</div>';
			html+='<div id="stationc" class="childdiv" style="height: 100px;overflow-y: auto;"></div>';
			html+='</div>';
			html+='</div>';
			//时间类型
			html+='<div id="datetype" class="panel panel-default">';
			html+='<div class="panel-heading">时间类型</div>';
			html+='<div class="panel-body">';
			html+='<button id="zr" class="btn btn-default w80">逐日</button>';
			html+='<button id="zy" class="btn btn-default w80">逐月</button>';
			html+='<button id="zn" class="btn btn-default w80">逐年</button>';
			html+='</div>';
			html+='</div>';
			//选择时间
			html+='<div id="datetime" class="panel panel-default">';
			html+='<div class="panel-heading">选择时间</div>';
			html+='<div class="panel-body">';
			html+='<div><span>开始日期:</span><input type="text" id="sdatepicker"></div>';
			html+='<div><span>结束日期:</span><input type="text" id="edatepicker"></div>';
			html+='</div>';
			html+='</div>';
			//操作
			html+='<div id="opration" class="panel panel-default">';
			html+='<div class="panel-heading">操作</div>';
			html+='<div class="panel-body">';
			html+='<button class="btn btn-default w70" id="query">查询</button>';
			html+='<button class="btn btn-default w100" id="queryandadd">查询追加</button>';
			html+='<button class="btn btn-default w70" id="test">测试</button>';
			html+='</div>';
			html+='</div>';
			//显示方式
			html+='<div id="display" class="panel panel-default">';
			html+='<div class="panel-heading">显示方式</div>';
			html+='<div class="panel-body">';
			html+='<button class="btn btn-default w70" id="dismap">地图</button>';
			html+='<button class="btn btn-default w70" id="dischart">图表</button>';
			html+='</div>';
			html+='</div>';
			$("#left").html(html);

			$("#element button").bind("click",cf.ActiveButton);
			$("#elementp button").bind("click",this.ELementChange);
			$("#datetype button").bind("click",cf.ActiveButton);
			$("#datetype button").bind("click",this.DataTypeChange);
			$("#display button").bind("click",cf.ActiveButton);
			$("#display button").bind("click",this.DisplayChange);
			$("#stationdiv button").bind("click",cf.ActiveButton);
			$("#areadiv button").bind("click",this.StationChange);
			var date=new Date();
			$("#sdatepicker").datepicker();
			$("#edatepicker").datepicker();
			let edp=$("#edatepicker").data('datepicker');
			edp._process_options({
				minViewMode:0,
				format:'yyyy-mm-dd',
				startView:0
			});
			edp._setDate(date);
			date=date.addMonths(-1);
			let sdp=$("#sdatepicker").data('datepicker');
			sdp._process_options({
				minViewMode:0,
				format:'yyyy-mm-dd',
				startView:0
			});
			sdp._setDate(date);
			//激活
			$("#element button:first").click();
			$("#areadiv button:first").click();
			$("#datetype button:first").click();
			$("#display button:first").click();
		},
		InitZDFX:function(){//诊断分析
			let html='';
			$("#left").html(html);
		},
		ELementChange:function(){//要素切换
			var id=this.id;
			var html='';
			if(id=="temp"){
				for(let obj in tempparam){
					let name=tempparam[obj].name;
					html+='<button class="btn btn-default w90">'+name+'</button>';
				}
				$("#elementc").html(html);
			}
			else if(id=="rain"){
				for(let obj in rainparam){
					let name=rainparam[obj].name;
					html+='<button class="btn btn-default w90">'+name+'</button>';
				}
				$("#elementc").html(html);
			}
			else if(id=="other"){
				for(let obj in other){
					let name=other[obj].name;
					html+='<button class="btn btn-default w90">'+name+'</button>';
				}
				$("#elementc").html(html);
			}
			$("#elementc button:first").bind("click",cf.ToggleButton);
			$("#elementc button").click();
		},
		StationChange:function(){//站点选择
			let id=this.id;
			var html='';
			if(id=="station"){
				su.getscstation("disscstation",function(data){
					if(data==undefined||data.length==0){
						console.log("站点请求出错!");
						return;
					}
					let size=data.length;
					for(let i=0;i<size;i++){
						let name=data[i].stationName;
						html+='<button class="btn btn-default w70">'+name+'</button>';
					}
					$("#stationc").html(html);
					$("#stationc button").bind("click",cf.ToggleButton);
					$("#stationc button:first").click();
				});
			}
			else if(id=="city"){
				au.GetCity(function(data){
					if(data==undefined||data.length==0){
						console.log("区域请求出错!");
						return;
					}
					let size=data.length;
					for(let i=0;i<size;i++){
						let name=data[i];
						html+='<button class="btn btn-default w90">'+name+'</button>';
					}
					$("#stationc").html(html);
					$("#stationc button").bind("click",cf.ToggleButton);
					$("#stationc button:first").click();
				});
			}
			else if(id=="area"){
				au.GetArea(function(data){
					if(data==undefined||data.length==0){
						console.log("区域请求出错!");
						return;
					}
					let size=data.length;
					for(let i=0;i<size;i++){
						let name=data[i];
						html+='<button class="btn btn-default w100">'+name+'</button>';
					}
					$("#stationc").html(html);
					$("#stationc button").bind("click",cf.ToggleButton);
					$("#stationc button:first").click();
				});
			}
			else if(id=="privince"){
				html='<button class="btn btn-default w100">四川</button>';
				$("#stationc").html(html);
				$("#stationc button").bind("click",cf.ToggleButton);
					$("#stationc button:first").click();
			}
			else if(id=="xn"){
				html='<button class="btn btn-default w100">西南</button>';
				$("#stationc").html(html);
				$("#stationc button").bind("click",cf.ToggleButton);
				$("#stationc button:first").click();
			}
		},
		DataTypeChange:function(){//时间类型
			datetypeid=this.id;
		},
		DisplayChange:function(){//显示方式
			displayid=this.id;
			if(displayid=="dismap"){
				$("#chart").addClass("hidden");
                $("#map").removeClass("hidden");
				mu.initWeatherMap();
			}
			else{
				$("#chart").removeClass("hidden");
                $("#map").addClass("hidden");
				cs.init();
			}
		}
	}
});