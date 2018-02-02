var DONGLI_Module = ['mapUtil', 'vue','Common', 'gridUtil', 'vlegend','processControl','mapTool','layerManageUtil','vlegend'];
define(DONGLI_Module, function (mapUtil, Vue,com, gridUtil, vlegend,processControl,mapTool,lmu,vlegend) {
	return {
		forcastMethod: [
			{ id: "dynamicEquation", name: "动力方程", isActive: true },
			{ id: "eofcca", name: "EOF-CCA", isActive: false },
		],
		elements: [
			{ id: "temp", name: "气温", isActive: true },
			{ id: "prec", name: "降水", isActive: false }
		],
		oprations: [
			{ id: "forcast", name: "预报" }
		],
		showFuns: [
			{ id: "showMap", name: "地图", isActive: true },
			{ id: "showChart", name: "图表", isActive: false },
		],
		elementID: "temp",
		elementName: "气温",
		showFunID: "showMap",
		strCurDate: "",
		vueForcastMethod: null,//预测方法
		vueElement: null,//要素
		vueValue: null,//值类型
		vueOpration: null,//操作方式
		vueShowFun: null,//显示方式
		vueOther: null,//其它
		vueYear:null,//年
		vueMonth:null,//月
		vueDate:null,//日期
		data:null,
		geo:null,
		Init: function () {
			var me = this;
			intiRes();
			initEvent();
			async function intiRes() {
				$("#left").css("display", "block");
				$("#left").html(`
					<div id="forcastMethod">
						<h3>预测方法</h3>
						<div class="row_3cols">
							<button class="btn btn-default w100" v-for="(item,index) in forcastMethods" :id="item.id" v-bind:class="{ 'active': item.isActive }" @click="click(item)">{{ item.name }}</button>
						</div>
					</div>
					<div id="element">
						<h3>要素</h3>
						<div class="row_3cols">
							<button class="btn btn-default w100" v-for="(item,index) in elements" :id="item.id" v-bind:class="{ 'active': item.isActive }" @click="click(item)">{{ item.name }}</button>
						</div>
					</div>
					<div id="date">
						<h3>日期</h3>
						<div class="strDate"><button v-for="item in dateUnit" :id="item.id" :class="{'active':item.isActive}" @click="dateUnitChange(item)">{{ item.name }}</button></div>
						<div class="space5"></div>
						<div class="normal_div_row"><span>起报日期：</span><input @change="dateChange" id="resDate" type="month" value="2011-01"/><div class="resStatus iconfont icon-yuandianzhong" :class="{'hasData':status}"  :title="status?'有数据':'无数据'"></div></div>
						<div class="space5"></div>
						<div v-if="curDateUnit === '月'" id="month_hr" class="title_content"><span>预测时间：</span><div v-for="item in monthData" :class="{ 'active': item.isActive }" @click="hrChange(item)">{{ item.name }}</div></div>
						<div v-else-if="curDateUnit === '季'" id="season_hr" class="title_content"><span>预测时间：</span><div v-for="item in seasonData" :class="{ 'active': item.isActive }" @click="hrChange(item)">{{ item.name }}</div></div>
						<div v-else="curDateUnit === '年'" id="year_hr" class="title_content"><span>预测时间：</span><div v-for="item in yearData" :class="{ 'active': item.isActive }" @click="hrChange(item)">{{ item.name }}</div></div>
					</div>
					<div id="opration">
						<h3>操作</h3>
						<div class="row_3cols">
							<button class="btn btn-default w100" v-for="(item,index) in datas" :id="item.id" v-bind:class="{ 'active': item.isActive }" @click="click(item)">{{ item.name }}</button>
						</div>
					</div>
					<div id="showFun_div">
						<h3>显示方式</h3>
						<div class="row_3cols">
							<button class="btn btn-default w100" v-for="(item,index) in datas" :id="item.id" v-bind:class="{ 'active': item.isActive }" @click="click(item)">{{ item.name }}</button>
						</div>
					</div>
				`);
				mapUtil.clearMap();
				mapUtil.map.updateSize();
				me.vueForcastMethod = new Vue({
					el: "#forcastMethod",
					data: {
						forcastMethods: me.forcastMethod
					},
					methods: {
						click: function (target) {
							var id = target.id;
							me.forcastMethodID = id;
							me.forcastMethod.forEach(item => {
								if (item.id === id) {
									item.isActive = true;
								}
								else {
									item.isActive = false;
								}
							});
						}
					}
				});
				me.vueElement = new Vue({
					el: "#element",
					data: {
						elements: me.elements
					},
					methods: {
						click: function (target) {
							me.elementID = target.id;
							me.elementName = target.name;
							me.elements.forEach(item => {
								if (item.id === me.elementID) {
									item.isActive = true;
								}
								else {
									item.isActive = false;
								}
							});
							vueTitle.name = me.elementName;
							me.makeForcast();
						}
					}
				});
				me.vueOpration = new Vue({
					el: "#opration",
					data: {
						datas: me.oprations
					},
					methods: {
						click: function (target) {
							me.oprations.forEach(item => {
								if (item.id === me.elementID) {
									item.isActive = true;
								}
								else {
									item.isActive = false;
								}
							});
							if (target.id === "forcast") {
								me.makeForcast();
							}
						}
					}
				});
				me.vueShowFun = new Vue({
					el: "#showFun_div",
					data: {
						datas: me.showFuns
					},
					methods: {
						click: function (target) {
							var id = target.id;
							me.showFunID = id;
							me.showFuns.forEach(item => {
								if (item.id === id) {
									item.isActive = true;
								}
								else {
									item.isActive = false;
								}
							});
						}
					}
				});
				me.vueDate = new Vue({
					el: "#date",
					data:{
						status:false,
						monthData:[{name:1,isActive:true},{name:2,isActive:false},{name:3,isActive:false},{name:4,isActive:false},{name:5,isActive:false},{name:6,isActive:false},
							{name:7,isActive:false},{name:8,isActive:false},{name:9,isActive:false},{name:10,isActive:false},{name:11,isActive:false},{name:12,isActive:false}],
						seasonData:[{name:1,isActive:true},{name:2,isActive:false},{name:3,isActive:false},{name:4,isActive:false}],
						yearData:[{name:1,isActive:true}],
						curDateUnit:"月",
						dateUnit:[{
							id:"month",
							name:"月",
							isActive:true
						},{
							id:"season",
							name:"季",
							isActive:false
						},{
							id:"year",
							name:"年",
							isActive:false
						}]
					},
					methods:{
						dateChange:function(e){
							var me = this;
							var strDate = e.currentTarget.value;
							var year = strDate.substring(0,4);
							year = parseInt(year);
							strDate = strDate.replace("-","");
							var month = strDate.substring(4,6);
							var makeDate = strDate+"01";
							var endDate = (year + 1) + month;
							var path = Physics_Config.modeHgtResOfMonthDir;

							var fileFormat = makeDate+".atm.Z3."+strDate+"-"+endDate+"_prs0500_member.nc";
							var file = path + fileFormat;
							com.checkResStatus(file).then(function(data){
								me.status = data.suc?true:false;
							});
							me.makeForcast();
						},
						dateUnitChange:function(e){
							this.dateUnit.forEach(item=>{
								item.isActive = false;
							});
							e.isActive = true;
							this.curDateUnit = e.name;
							me.makeForcast();
						},
						hrChange:function(item){
							if(this.curDateUnit === "月"){
								this.monthData.forEach(item=>{
									item.isActive = false;
								});
							}
							else if(this.curDateUnit === "季"){
								this.seasonData.forEach(item=>{
									item.isActive = false;
								});
							}
							item.isActive = true;
							me.titleReflush(item.name);
							me.makeForcast();
						}
					}
				});
				mapTool.Init("right");
				//初始化日期
				var curDate = new Date();
				var curYear = curDate.getFullYear();
				$("#select_year").val(curYear);
				var curMonth = curDate.getMonth()+1;
				$("#select_month").val(curMonth);

				vlegend.Init("map");//初始化图例
				//初始化资料时间
				var dateObj = await com.getResLastDate();
				if(dateObj.suc){
					var strDate = dateObj.suc;
					var strYear = strDate.substring(0,4);
					var strMonth = strDate.substring(4,6);
					strDate = strYear + "-"+strMonth;
					var resDateControl = document.getElementById("resDate");
					resDateControl.value = strDate;
					me.vueDate.status = true;
				}
				else{
					console.log(dateObj.err);
					me.vueDate.status = false;
				}
				vueTitle.name = me.elementName;

				var obj = await com.getBounds();
                if (obj.suc != null) {
					me.geo = JSON.parse(obj.suc);
					com.addCover(me.geo, true);
                }
                else {
                    console.log("获取区域数据失败!");
                }
			}
			function initEvent() {
				mapTool.myVue.fillVal = function(obj){//重新注册填值事件
                    me.fillVal(obj);
                }
                mapTool.myVue.fillColor = function(obj){//重新注册填图事件
                    me.fillColor(obj);
				}
				mapTool.myVue.contourLine = function(obj){//重新注册填图事件
                    me.contourLine(obj);
                }
                mapTool.myVue.invert = function(obj){//重新注册反演事件
                    me.invert(obj);
				}
			}
		},
		makeForcast: function () {//预报
			var me = this;
			if(!me.vueDate.status){
				processControl.hide("无当前时间资料,不能做预报!");
				return;
			}
			processControl.show("正在计算!");
			let url = Url_Config.gridServiceUrl;
			var forecastMethod = me.getForecastMethod();
			url = url + "services/ForcastService/downScaling";
			var resDateControl = document.getElementById("resDate");
			var resDate = resDateControl.value;
			resDate += "-01";
			//resDate = resDate.replace("-","");//资料时间
			//预报月份，数组
			var forecastMonths = me.getForecastMonths();
			var param = {
				makeDate: resDate,
				forcastDate: forecastMonths,
				elementID: me.elementID,
				methodName:forecastMethod.name
			}
			var strParam = JSON.stringify(param);
			com.AJAX(url, strParam, true, function () {
				processControl.hide("预报失败!");
			},
			function (data) {
				if(data.suc == null){
					processControl.hide(data.err);
				}
				else{
					me.data = data.suc;
					processControl.hide("预报成功!");
					mapTool.myVue.items[1].isActive = false;
					document.getElementById("fillVal").click();
				}
			});
		},
		/**
		 * @author:杠上花
		 * @date:2018-01-18
		 * @param:
		 * @return:
		 * @description:标题刷新
		 */
		titleReflush:function(index){
			var me = this;
			var dateControl = document.getElementById("resDate");
			var strDate = dateControl.value;
			strDate = strDate.replace("-","");
			var year = strDate.substring(0,4);
			var month = strDate.substring(4,6);
			var date = new Date(year,month-1);
			var strForecastDate = "";
			var curDateUnit = me.vueDate.curDateUnit;
			index = parseInt(index);
			if(curDateUnit === "月"){
				date = date.addMonths(index -1);
				var strDate = date.format("yyyy-MM");
				strForecastDate = strDate;
			}
			else if(curDateUnit === "季"){
				date = date.addMonths((index-1)*3);
				var strStartMonth = date.format("yyyy-MM");
				date = date.addMonths(2);
				var strEndMonth = date.format("yyyy-MM");
				strForecastDate = strStartMonth + "--" + strEndMonth;
			}
			else if(curDateUnit === "年"){
				var strStartDate = date.format("yyyy-MM");
				date = date.addYears(1);
				var strEndDate = date.format("yyyy-MM");
				strForecastDate = strStartDate + "--" + strEndDate;
			}
			strForecastDate += "月";
			vueTitle.datetime = strForecastDate;
		},
		/**
		 * @author:杠上花
		 * @date:2018-01-18
		 * @param:
		 * @return:
		 * @description:获取预报月份
		 */
		getForecastMonths:function(){
			var me = this;
			var dateControl = document.getElementById("resDate");
			var strDate = dateControl.value;
			strDate = strDate.replace("-","");
			var year = strDate.substring(0,4);
			var month = strDate.substring(4,6);
			var date = new Date(year,month-1);
			var curDateUnit = me.vueDate.curDateUnit;
			var forecastMonth = [];
			var index = 1;
			if(curDateUnit === "月"){
				
				me.vueDate.monthData.forEach(item=>{
					if(item.isActive){
						index = item.name;
						return;
					}
				});
				date = date.addMonths(index -1);
				var strDate = date.format("yyyyMM");
				forecastMonth.push(strDate);
			}
			else if(curDateUnit === "季"){
				me.vueDate.seasonData.forEach(item=>{
					if(item.isActive){
						index = item.name;
						return;
					}
				});
				date = date.addMonths((index-1)*3 - 1);
				for(var i = 0;i<3;i++){
					date = date.addMonths(1);
					var strDate = date.format("yyyyMM");
					forecastMonth.push(strDate);
				}
			}
			else if(curDateUnit === "年"){
				for(var i = 0;i<3;i++){
					date = date.addMonths(i);
					var strDate = date.format("yyyyMM");
					forecastMonth.push(strDate);
				}
			}
			return forecastMonth;
		},
		/**
		 * @author:杠上花
		 * @date:2018-01-18
		 * @param:
		 * @return:
		 * @description:获取预测方法
		 */
		getForecastMethod:function(){
			var me = this;
			var result = null;
			me.forcastMethod.forEach(item=>{
				if(item.isActive){
					result = item;
					return;
				}
			});
			return result;
		},
		/**
		 * @author:杠上花
		 * @date:2018-01-18
		 * @param:
		 * @return:
		 * @description:填值
		 */
		fillVal:function(obj){
			var me = this;
			var name = "填值";
			if(!obj.isActive){
                lmu.Remove(name);
                return;
			}
			mapTool.myVue.items[3].isActive = false;
			obj.isActive = true;
			//取消反演选中
            lmu.Remove("实际值");
			mapTool.myVue.fillValHelper(me.data);
		},
		/**
		 * @author:杠上花
		 * @date:2018-01-18
		 * @param:
		 * @return:
		 * @description:填图
		 */
		fillColor:function(obj,importData){
			var me = this;
            var name = "填图";
            if(!obj.isActive){
                lmu.Remove(name);
                return;
            }
            var invertObj = null;
            mapTool.myVue.items.forEach(item=>{
                if(item.id === "invert"){
                    invertObj = item;
                    return;
                }
			});
			var style = null;
			var tempData = null;
			if(invertObj.isActive){
				tempData = mapTool.myVue.realData;
                if(me.elementID === "prec"){
                    style = month_prec;
                }
                else if(me.elementID === "temp"){
                    style = temp;
				}
				vueTitle.type = "值";
            }
            else{
				tempData = me.data;
				if(me.elementID === "prec"){
					style = month_jp_rain;
				}
				else if(me.elementID === "temp"){
					style = temp_jp;
				}
				vueTitle.type = "距平";
			}
			if(tempData==null){
                processControl.hide("无数据");
                return;
			}
			mapTool.myVue.fillColorHelper(name,tempData,style);
            vlegend.setStyle(style);
		},
		/**
         * @author:杠上花
         * @date:2017-12-28
         * @modifydate:
         * @param:
         * @return:
         * @description:反演
         */
        invert:function(obj){
			var me = this;
            var name = "实际值";
            if(!obj.isActive){
                lmu.Remove(name);
                return;
            }
            if(me.data==null){
                processControl.hide("无数据");
                return;
            }
            var strMonths = me.getForecastMonths();
            mapTool.myVue.invertHelper(me.elementID,strMonths,me.data,name);
		},
		/**
         * @author:杠上花
         * @date:2017-12-30
         * @modifydate:
         * @param:
         * @return:
         * @description:等值线
         */
		contourLine:function(obj){
			var me = this;
			var name = "等值线";
			if(!obj.isActive){
                lmu.Remove(name);
                return;
			}
			var style = me.getStyle();
			mapTool.myVue.contourLineHelper(name,me.data,style);
		},
		/**
         * @author:杠上花
         * @date:2017-12-30
         * @modifydate:
         * @param:
         * @return:
         * @description:获取样式
         */
		getStyle:function(){
			var me = this;
			var style = null;
			if(mapTool.myVue.items[1].isActive){
				if(me.elementID === "prec"){
					style = month_jp_rain;
				}
				else if(me.elementID === "temp"){
					style = temp_jp;
				}
			}
			if(mapTool.myVue.items[3].isActive){
				if(me.elementID === "prec"){
                    style = month_prec;
                }
                else if(me.elementID === "temp"){
                    style = temp;
				}
			}
			return style;
		}
	}
});