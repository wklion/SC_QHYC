var IntegrationForecast_Module = ['mapUtil', 'vue','vlegend', 'gridUtil','processControl','Common','displayUtil','layerManageUtil','mapTool'];
/**
 * @author:杠上花
 * @date:2017-09-01
 * @param:
 * @return:
 * @description:模式数据浏览
 */
define(IntegrationForecast_Module, function (mapUtil,Vue,vlegend, gridUtil,processControl,com,displayUtil,lmu,mapTool) {
    return{
        vueIntegrationForecast:null,
        data:null,
        myChart:null,
        elementName:"降水",
        Init:function(){
            var me = this;
            initRes(); //初始化资源
            initEvent();
            function initRes(){
                $("#left").css("display","block");
                $("#left").html(`
                    <div id="integrationForecast_div">
                    <div id="premethod">
                    <div class="menu_title">预测方法</div>
                    <div class="row_3cols">
                        <button class="btn btn-default w100" v-for="method in mothods" :class="{active:method.isActive}" @click="methodClick(method)">{{method.name}}</button>
                    </div> 
                    </div> 
                    <div id="calmethod">
                    <div class="menu_title">计算方法</div>
                    <div class="row_3cols">
                    <button class="btn btn-default w100" v-for="fun in funs" :class="{active:fun.isActive}" @click="funClick(fun)">{{fun.name}}</button>
                    </div>
                    </div>
                    <div id="elements">
                    <div class="menu_title">要素</div>
                    <div class="row_3cols">
                    <button class="btn btn-default w100" v-for="item in elements" :class="{active:item.isActive}" @click="elementClick(item)">{{item.name}}</button>
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
                    <div class="space5"></div>
                    <div id="download" class="centerEle">
                        <button class="btn btn-default w200" @click="iDownAndCal">下载并计算</button>
                    </div>
                    </div>
                `);
                mapUtil.clearMap();
                mapUtil.map.updateSize();
                mapTool.Init("right");
                vueTitle.name=me.elementName;
                me.vueIntegrationForecast = new Vue({
                    el: "#integrationForecast_div",
                    data: {
                        mothods:[{
                            id:"downscaling",
                            name:"动力方程",
                            isActive:true,
                            enable:true
                        },{
                            id:"eof-cca",
                            name:"EOF-CCA",
                            isActive:false,
                            enable:true
                        }],
                        funs:[{
                            id:"arithmeticMean",
                            name:"算术平均",
                            isActive:true,
                            enable:true
                        },{
                            id:"optimization",
                            name:"最优集成",
                            isActive:false,
                            enable:false
                        },{
                            id:"weighte",
                            name:"权重集成",
                            isActive:false,
                            enable:true
                        }],
                        elements:[{
                            id:"prec",
                            name:"降水",
                            isActive:true,
                            enable:true
                        },{
                            id:"temp",
                            name:"气温",
                            isActive:false,
                            enable:true
                        }],
                        months:[{
                            name:1,
                            isActive:false,
                            enable:true
                        },{
                            name:2,
                            isActive:false,
                            enable:true
                        },{
                            name:3,
                            isActive:false,
                            enable:true
                        },{
                            name:4,
                            isActive:false,
                            enable:true
                        },{
                            name:5,
                            isActive:false,
                            enable:true
                        },{
                            name:6,
                            isActive:false,
                            enable:true
                        },{
                            name:7,
                            isActive:false,
                            enable:true
                        },{
                            name:8,
                            isActive:false,
                            enable:true
                        },{
                            name:9,
                            isActive:false,
                            enable:true
                        },{
                            name:10,
                            isActive:false,
                            enable:true
                        },{
                            name:11,
                            isActive:false,
                            enable:true
                        },{
                            name:12,
                            isActive:false,
                            enable:true
                        }],
                        year:2018,
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
                        methodClick:function(obj){
                            obj.isActive = !obj.isActive;
                        },
                        funClick:function(obj){
                            if(!obj.enable){
                                processControl.hide("该预测方法还未实现!");
                                return;
                            }
                            this.funs.forEach(item=>{
                                if(item.id === obj.id){
                                    item.isActive = true;
                                }
                                else{
                                    item.isActive = false;
                                }
                            });
                        },
                        elementClick:function(obj){
                            this.elements.forEach(item=>{
                                if(item.id === obj.id){
                                    item.isActive = true;
                                }
                                else{
                                    item.isActive = false;
                                }
                                me.elementName=obj.name;
                                vueTitle.name=me.elementName;
                            });
                        },
                        monthClick:function(obj){
                            this.months.forEach(item=>{
                                if(item.name === obj.name){
                                    item.isActive = true;
                                }
                                else{
                                    item.isActive = false;
                                }
                            });
                        },
                        iDownAndCal:function(){
                            me.downAndCal();
                        },
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
                        },
                        dateUnitChange:function(e){
							this.dateUnit.forEach(item=>{
								item.isActive = false;
							});
							e.isActive = true;
							this.curDateUnit = e.name;
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
						}
                    },
                    watch:{
                        year(curYear,oldYear){
                            if(curYear<1||curYear>2100){
                                this.year = oldYear;
                                console.log("年份超出范围");
                            }
                        }
                    },
                    created:function(){
                        var date = new Date();
                        var month = date.getMonth()+1;
                        this.months.forEach(item=>{
                            if(item.name == month){
                                item.isActive = true;
                                return;
                            }
                        });
                        var year = date.getFullYear();
                        this.year = year;
                    }
                });
            }      
            function initEvent(){
                mapTool.myVue.fillVal = function(obj){//重新注册填值事件
                    me.fillVal(obj);
                }
                mapTool.myVue.fillColor = function(obj){//重新注册填图事件
                    me.fillColor(obj);
                }
                mapTool.myVue.contourLine = function(obj){//重新注册等值线事件
                    me.contourLine(obj);
                }
                mapTool.myVue.invert = function(obj){//重新注册反演事件
                    me.invert(obj);
                }
                me.getLastDate();
                com.getBounds().then(function(data){
                    if (data.suc != null) {
                        me.geo = JSON.parse(data.suc);
                        com.addCover(me.geo, true);
                    }
                    else {
                        console.log("获取区域数据失败!");
                    }
                });
                
            }
        },
        titleReflush:function(index){
			var me = this;
			var dateControl = document.getElementById("resDate");
			var strDate = dateControl.value;
			strDate = strDate.replace("-","");
			var year = strDate.substring(0,4);
			var month = strDate.substring(4,6);
			var date = new Date(year,month-1);
			var strForecastDate = "";
			var curDateUnit = me.vueIntegrationForecast.curDateUnit;
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
         * @date:2017-12-22
         * @modifydate:
         * @param:
         * @return:
         * @description:下载并计算
         */
        downAndCal:async function(){
            var me = this;
            me.down();
            //displayUtil.displayStationData("降水",result);
        },
        /**
         * @author:杠上花
         * @date:2017-12-22
         * @modifydate:
         * @param:
         * @return:
         * @description:下载
         */
        down:function(){
            var me = this;
            var methods = [];
            me.vueIntegrationForecast.mothods.forEach(item=>{
                if(item.isActive){
                    methods.push(item.name);
                }
            });
            var elementObj = me.getSelectElement();
            var elementID = elementObj.id;
            var forecastMonths = me.getForecastMonths();
            var resDateControl = document.getElementById("resDate");
			var resDate = resDateControl.value;
			resDate += "-01";
            var param = {
                elementID:elementID,
                methods:methods,
                makeDate:resDate,
                forecastDates:forecastMonths
            };
            var url = Url_Config.gridServiceUrl + "services/ForcastService/getForecastData";
            param = JSON.stringify(param);
            com.AJAX(url, param, true, function () {
                processControl.hide("最新资料失败!");
			},
			function (data) {
				if(data.suc == null){
                    processControl.hide(data.err);
				}
				else{
                   me.cal(data.suc);
				}
            });
        },
        /**
         * @author:杠上花
         * @date:2017-12-22
         * @modifydate:
         * @param:
         * @return:
         * @description:计算
         */
        cal:function(datas){
            var me = this;
            var calFunName = me.getCalFun();
            if(calFunName === "算术平均"){
                me.data = me.arithmeticMeanCal(datas);
            }
            else{
                processControl.hide("该方法尚未实现!");
            }
            mapTool.myVue.items[1].isActive = false;
            document.getElementById("fillVal").click();
        },
        /**
         * @author:杠上花
         * @date:2017-12-22
         * @modifydate:
         * @param:
         * @return:
         * @description:算术平均计算
         */
        arithmeticMeanCal:function(datas){
            var me = this;
            var tempDatas = [];
            me.vueIntegrationForecast.mothods.forEach(item=>{
                if(item.isActive){
                    var data = datas[item.name];
                    tempDatas.push(data);
                }
            });
            var methodSize = tempDatas.length;
            if(tempDatas.length<1){
                return;
            }
            var result = [];
            var size = tempDatas[0].length;
            for(var i=0;i<size;i++){
                var sum = 0;
                for(var j=0;j<methodSize;j++){
                    var val = tempDatas[j][i].value;
                    sum += val;
                }
                var avg = sum/methodSize;
                var newObject = $.extend(true, {}, tempDatas[0][i]);
                newObject.value = avg;
                result.push(newObject);
            }
            return result;
            // var mapStationVal = new Map();
            // mapData.forEach((items,key)=>{
            //     items.forEach(item=>{
            //         var sn = item.stationNum;
            //         var val = item.val;
            //         var obj = mapStationVal.get(sn);
            //         if(obj != undefined){
            //             val = (val+obj)/2;
            //         }
            //         mapStationVal.set(sn,val);
            //     });
            // });
            // var result = [];
            // //转成数组
            // mapStationVal.forEach((item,key)=>{
            //     var obj = mapStation.get(key);
            //     if(obj==undefined){
            //         return;
            //     }
            //     obj.value = item;
            //     result.push(obj);
            // });
            // return result;
        },
        /**
         * @author:杠上花
         * @date:2017-12-22
         * @modifydate:
         * @param:
         * @return:
         * @description:获取激活月份
         */
        getMonth:function(){
            var me = this;
            var month = 1;
            me.vueIntegrationForecast.months.forEach(item=>{
                if(item.isActive){
                    month = item.name;
                    return;
                }
            });
            return month;
        },
        /**
         * @author:杠上花
         * @date:2017-12-22
         * @modifydate:
         * @param:
         * @return:
         * @description:获取要素ID
         */
        getElementID:function(){
            var me = this;
            var elementID = "prec";
            me.vueIntegrationForecast.elements.forEach(item=>{
                if(item.isActive){
                    elementID = item.id;
                    return;
                }
            });
            return elementID;
        },
        /**
         * @author:杠上花
         * @date:2017-12-23
         * @modifydate:
         * @param:
         * @return:
         * @description:获取计算方法
         */
        getCalFun:function(){
            var me = this;
            var calFunName = "算术平均";
            me.vueIntegrationForecast.funs.forEach(item=>{
                if(item.isActive){
                    calFunName = item.name;
                    return;
                }
            });
            return calFunName;
        },
        /**
         * @author:wangkun
         * @date:2017-1-24
         * @modifydate:
         * @param:
         * @return:
         * @description:获取最新资料时间
         */
        getLastDate:function(){
            var me = this;
            var elementObj = me.getSelectElement();
            var elementID = elementObj.id;
            var methodObj = me.getSelectMethod();
            var methodName = methodObj.name;
            var param = {
                elementID:elementID,
                methodName:methodName
            };
            param = JSON.stringify(param);
            let url = Url_Config.gridServiceUrl;
            url = url + "services/ForcastService/getLastForecastDataDate";
            com.AJAX(url, param, true, function () {
                processControl.hide("最新资料时间失败!");
                me.vueIntegrationForecast.status = false;
			},
			function (data) {
				if(data.suc == null){
                    processControl.hide(data.err);
                    me.vueIntegrationForecast.status = false;
				}
				else{
                    var strDate = data.suc;
                    strDate = strDate.replace("-","");
					var strYear = strDate.substring(0,4);
					var strMonth = strDate.substring(4,6);
					strDate = strYear + "-"+strMonth;
					var resDateControl = document.getElementById("resDate");
					resDateControl.value = strDate;
					me.vueIntegrationForecast.status = true;
				}
			});
        },
        /**
         * @author:wangkun
         * @date:2017-1-24
         * @modifydate:
         * @param:
         * @return:
         * @description:获取选中要素
         */
        getSelectElement:function(){
            var me = this;
            var obj = null;
            me.vueIntegrationForecast.elements.forEach(item=>{
                if(item.isActive){
                    obj = item;
                    return;
                }
            });
            return obj;
        },
        /**
         * @author:wangkun
         * @date:2017-1-24
         * @modifydate:
         * @param:
         * @return:
         * @description:获取选中方法
         */
        getSelectMethod:function(){
            var me = this;
            var obj = me.vueIntegrationForecast.mothods[0];
            return obj;
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
			var curDateUnit = me.vueIntegrationForecast.curDateUnit;
			var forecastMonth = [];
			var index = 1;
			if(curDateUnit === "月"){
				
				me.vueIntegrationForecast.monthData.forEach(item=>{
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
				me.vueIntegrationForecast.seasonData.forEach(item=>{
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
		fillColor:function(obj){
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
            var elementID = me.getElementID();
			var style = null;
			var tempData = null;
			if(invertObj.isActive){
				tempData = mapTool.myVue.realData;
                if(elementID === "prec"){
                    style = month_prec;
                }
                else if(elementID === "temp"){
                    style = temp;
				}
				vueTitle.type = "值";
            }
            else{
				tempData = me.data;
				if(elementID === "prec"){
					style = month_jp_rain;
				}
				else if(elementID === "temp"){
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
        invert:async function(obj){
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
            var elementID = me.getElementID();
			if(mapTool.myVue.items[1].isActive){
				if(elementID === "prec"){
					style = month_jp_rain;
				}
				else if(elementID === "temp"){
					style = temp_jp;
				}
			}
			if(mapTool.myVue.items[3].isActive){
				if(elementID === "prec"){
                    style = month_prec;
                }
                else if(elementID === "temp"){
                    style = temp;
				}
			}
			return style;
		}
    }
})