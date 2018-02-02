
define([
    'mapUtil',
    'vue',
    'Common',
    'displayUtil',
    'mapTool',
    'processControl',
    'echarts'
], function (mapUtil, Vue, com, displayUtil, mapTool, processControl,echart) {
    return {
        elements: [
            { id: "prec", name: "降雨", flag: 0, isActive: true },
            { id: "temp", name: "气温", flag: 1, isActive: false },
        ],
        month_season: [
            { id: "month", name: "月份", flag: 0, isActive: true },
            { id: "season", name: "季节", flag: 1, isActive: false },
        ],
        predictdete: [
            { id: "PS", name: "PS检验方法",isActive:false},
            { id: "CC", name: "CC检验方法",isActive:true}
        ],
        areaCodes:["5","50","51","52","53","54"],
        Init: function () {
            var me = this;
            initRes();
            initEvent();
            mapUtil.map.updateSize();
            function initRes() {
                $("#left").css("display","block");
                $("#left").html(`
                    <div id="element">
                        <div class="menu_title">要素</div>
                        <div class="row_3cols">
                        <button class="btn btn-default w100" v-for="(item,index) in elements" :flag="item.flag" :id="item.id" v-bind:class="{'active':item.isActive}" @click="click(item)">{{item.name}}</button>
                        </div>
                    </div>
                    <div id="forecastMethod">
                        <div class="menu_title">预测方法</div>
                        <div class="row_3cols">
                            <button class="btn btn-default w100" v-for="(item,index) in names" :id="item.id" :class="{'active':item.isActive}" @click="click(item)">{{item.name}}</button>
                        </div>
                    </div>
                    <div id="date">
                        <h3>日期</h3>
                        <div class="strDate"><button v-for="item in dateUnit" :id="item.id" :class="{'active':item.isActive}" @click="dateUnitChange(item)">{{ item.name }}</button></div>
                        <div class="space5"></div>
                        <div class="normal_div_row"><span>起报日期：</span><input @change="dateChange" id="resDate" type="month" value="2017-01"/><div class="resStatus iconfont icon-yuandianzhong" :class="{'hasData':status}"  :title="status?'有数据':'无数据'"></div></div>
                    </div>
                    <div id="detection">
                        <div class="menu_title">检验方法</div>
                        <div class="normal_div_row">
                            <button class="btn btn-default" v-for="(item,index) in predictdete" :class="{'active':item.isActive}" :id="item.id" @click="click(item)">{{ item.name }}</button>
                        </div>
                    </div>
                `); 
                $("#right").append(`
                    <div id="upMap" class="upMap delete"></div>
                `);
                me.VueElement=new Vue({
                    el: "#element",
                    data: {
                        selectID:"temp",
                        elements: me.elements
                    },
                    methods:{
                        click:function(target){
                            me.elementID = target.id;
                            this.selectID = target.id;
                            me.elementName = target.name;
                            me.elements.forEach(item => {
                                item.isActive = false;
                            });
                            target.isActive = true;
                            me.getTestData();
                        }
                    }
                });
                me.vueDate = new Vue({
                    el: "#date",
                    data:{
                        status:true,
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
                            var strDate = e.currentTarget.value;
                            var year = strDate.substring(0,4);
                            year = parseInt(year);
                            strDate = strDate.replace("-","");
                            var month = strDate.substring(4,6);
                            var makeDate = strDate+"01";
                            me.checkResExit(makeDate);
                        },
                        dateUnitChange:function(e){
                            this.dateUnit.forEach(item=>{
                                item.isActive = false;
                            });
                            e.isActive = true;
                            this.curDateUnit = e.name;
                            me.getTestData();
                        }
                    }
                });
                me.forecastMethod = new Vue({
                    el: "#forecastMethod",
                    data:{
                        names:[{id:"dynamicEquation",name:"动力方程",isActive:false},
                        {id:"eofcca",name:"EOF-CCA",isActive:true}]
                    },
                    methods:{
                        click:function(item){
                            this.names.forEach(thisItem=>{
                                thisItem.isActive = false;
                            });
                            item.isActive = true;
                            me.getTestData();
                        }
                    }
                });
                me.Predictdete=new Vue({
                    el:"#detection",
                    data:{
                        predictdete: me.predictdete,
                    },
                    methods:{
                        click:function(item){
                            this.predictdete.forEach(thisItem=>{
                                thisItem.isActive = false;
                            });
                            item.isActive = true;
                            var option = me.myChart.getOption();
                            if(item.id === "PS"){
                                option.yAxis[0].name = "百分率";
                            }
                            else{
                                option.yAxis[0].name = "相关系数";
                            }
                            me.myChart.setOption(option);
                            me.getTestData();
                        }
                    }

                });
                //初始化图表
                me.myChart = echart.init(document.getElementById("upMap"));
                var option = {
                    title:{
                      text: '预报检验',
                      left:'center',
                      textStyle:{
                          color:'red',
                          fontSize:'36'
                      }
                    },
                    color:["#FF0000","#FFA500","#FFFF00","#008000","#00FFFF","#0000FF","#FF00FF","#FF0080","#9932CC"],
                    textStyle: {
                      color: "black"
                    },
                    grid: {
                        bottom: 100
                    },
                    tooltip:{
                        show:true,
                    },
                    toolbox:{
                        show:true,
                        feature : {
                            mark : {show: true},
                            dataView : {show: true, readOnly: false},
                            magicType: {show: true, type: ['line', 'bar']},
                            restore : {show: true},
                            saveAsImage : {show: true}
                        },
                        iconStyle:{
                            color:"rgb(128,255,255)"
                        }
                    },
                    legend: {
                      data: ['西南','重庆','四川',"云南","贵州","西藏"],
                      bottom:30,
                      textStyle:{
                        color:"black",
                        fontSize:'22'
                      }
                    },
                    xAxis: [{
                      type: 'category',
                      name:'日期',
                      data: ["201709","201710","201711","201712","201801","201802","201803","201804","201805","201806","201807","201808","201809"],
                      axisPointer: {
                        type: 'shadow'
                      }
                    }],
                    yAxis: [
                      {
                        type: 'value',
                        name:'相关系数'
                      }
                    ],
                    series: [{
                        name:"西南",
                        type:"bar",
                        data:[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]
                    },{
                        name:"重庆",
                        type:"bar",
                        data:[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]
                    },{
                        name:"四川",
                        type:"bar",
                        data:[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]
                    },{
                        name:"云南",
                        type:"bar",
                        data:[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]
                    },{
                        name:"贵州",
                        type:"bar",
                        data:[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]
                    },{
                        name:"西藏",
                        type:"bar",
                        data:[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]
                    },]
                };
                me.myChart.setOption(option);
                //初始时间
                var date = new MyDate();
                date = date.addYears(-1);
                var strDate = date.format("yyyy-MM");
                var resDateControl = document.getElementById("resDate");
                resDateControl.value = strDate;
            }
            function initEvent(){
                //me.getLastDate();
                me.getTestData();
            }          
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
                me.vueDate.status = false;
			},
			function (data) {
				if(data.suc == null){
                    processControl.hide(data.err);
                    me.vueDate.status = false;
				}
				else{
                    var strDate = data.suc;
                    strDate = strDate.replace("-","");
					var strYear = strDate.substring(0,4);
					var strMonth = strDate.substring(4,6);
					strDate = strYear + "-"+strMonth;
					var resDateControl = document.getElementById("resDate");
					resDateControl.value = strDate;
                    me.vueDate.status = true;
                    me.getTestData();
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
            me.VueElement.elements.forEach(item=>{
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
            var obj = null;
            me.forecastMethod.names.forEach(item=>{
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
         * @description:检验资料是否存
         */
        checkResExit:function(makeDate){
            var me = this;
            var elementObj = me.getSelectElement();
            var elementID = elementObj.id;
            var methodObj = me.getSelectMethod();
            var methodName = methodObj.name;
            var param = {
                elementID:elementID,
                methodName:methodName,
                makeDate:makeDate
            };
            param = JSON.stringify(param);
            let url = Url_Config.gridServiceUrl;
            url = url + "services/ForcastService/checkResExit";
            com.AJAX(url, param, true, function () {
                processControl.hide("最新资料时间失败!");
                me.vueDate.status = false;
                me.clearData();
			},
			function (data) {
				if(data.suc == null){
                    me.clearData();
                    processControl.hide("没有该日期数据!");
                    me.vueDate.status = false;
				}
				else{
                    me.vueDate.status = true;
                    me.getTestData();
				}
			});
        },
        /**
         * @author:wangkun
         * @date:2017-1-27
         * @modifydate:
         * @param:
         * @return:
         * @description:获取检验数据
         */
        getTestData:function(){
            var me = this;
            if(!me.vueDate.status){
                me.clearData();
                processControl.hide("没有该日期数据!");
                return;
            }
            var elementObj = me.getSelectElement();
            var elementID = elementObj.id;
            var methodObj = me.getSelectMethod();
            var methodName = methodObj.name;
            var testObj = me.getTestFunction();
            var testFunctionName = testObj.id;
            var dateControl = document.getElementById("resDate");
            var strDate = dateControl.value + "-01";
            var param = {
                elementID:elementID,
                forecastName:methodName,
                makeDate:strDate,
                testName:testFunctionName
            };
            param = JSON.stringify(param);
            let url = Url_Config.gridServiceUrl;
            var unit = me.vueDate.curDateUnit;
            if(unit == "月"){
                url = url + "services/ForcastService/getMonthTest";
            }
            else if(unit == "季"){
                url = url + "services/ForcastService/getSeasonTest";
            }
            else{
                url = url + "services/ForcastService/getYearTest";
            }
            com.AJAX(url, param, true, function () {
                me.clearData();
                processControl.hide("获取检验数据失败!");
			},
			function (data) {
				if(data.suc == null || data.suc.length<1){
                    me.clearData();
                    processControl.hide("获取检验数据失败或没有检验数据!");
				}
				else{
                    me.reflushData(data.suc);
				}
			});
        },
        /**
         * @author:wangkun
         * @date:2017-1-27
         * @modifydate:
         * @param:
         * @return:
         * @description:获取检验方法
         */
        getTestFunction:function(){
            var me = this;
            var obj = null;
            me.Predictdete.predictdete.forEach(item=>{
                if(item.isActive){
                    obj = item;
                    return;
                }
            });
            return obj;
        },
        /**
         * @author:wangkun
         * @date:2017-1-27
         * @modifydate:
         * @param:
         * @return:
         * @description:刷新数据,getTestData方法调用它
         */
        reflushData:function(data){
            var me = this;
            var dateControl = document.getElementById("resDate");
            var strDate = dateControl.value;
            strDate = strDate.replace("-","");
            var year = strDate.substring(0,4);
            var month = strDate.substring(4,6);
            month = parseInt(month)-1;
            var areaCodeSize = me.areaCodes.length;
            var xData = me.calXData();
            var title = me.getTitle();
            var option = me.myChart.getOption();
            option.xAxis[0].data = xData;
            option.title[0].text = title;
            me.myChart.setOption(option);
            var unit = me.vueDate.curDateUnit;
            var unitSize = 1;
            if(unit == "月"){
                unitSize = 13;
            }
            else if(unit == "季"){
                unitSize = 4;
            }
            for(var i=0;i<areaCodeSize;i++){
                option = me.myChart.getOption();
                var areaCode = me.areaCodes[i];
                var dataItem = [];
                var date = new MyDate(year,month,1);
                for(var m=1;m<=unitSize;m++){
                    var strDate = date.format("yyyyMM");
                    var val = 0;
                    data.forEach(item=>{
                        var curAreaCode = item.areaCode;
                        if(unit == "月"){
                            var curForecastDate = item.forecastDate;
                            if(curAreaCode == areaCode && curForecastDate == strDate){
                                val = item.val;
                                return;
                            }
                        }
                        else if(unit == "季"){
                            var season = item.season;
                            if(curAreaCode == areaCode && m == season){
                                val = item.val;
                                return;
                            }
                        }
                        else{
                            if(curAreaCode == areaCode){
                                val = item.val;
                                return;
                            }
                        }
                    });
                    dataItem.push(val);
                    date = date.addMonths(1);
                }
                option.series[i].data = dataItem;
                me.myChart.setOption(option);
            }
        },
        /**
         * @author:wangkun
         * @date:2017-1-28
         * @modifydate:
         * @param:
         * @return:
         * @description:计算X轴坐标
         */
        calXData:function(){
            var me = this;
            var dateControl = document.getElementById("resDate");
            var strDate = dateControl.value;
            strDate = strDate.replace("-","");
            var year = strDate.substring(0,4);
            var month = strDate.substring(4,6);
            month = parseInt(month) - 1;
            var date = new MyDate(year,month,1);
            var strs = [];
            var unit = me.vueDate.curDateUnit;
            if(unit == "月"){
                for(var i=0;i<13;i++){
                    var strDate = date.format("yyyyMM");
                    date = date.addMonths(1);
                    strs.push(strDate);
                }
            }
            else if(unit == "季"){
                for(var i=0;i<12;i++){
                    var strDate = "";
                    for(var j=0;j<3;j++){
                        if(j == 1){
                            strDate += "~";
                            date = date.addMonths(1);
                            continue;
                        }
                        strDate += date.format("yyyyMM");
                        date = date.addMonths(1);
                    }
                    strs.push(strDate);
                }
            }
            else{
                var strDate = date.format("yyyyMM");
                strDate += "~";
                date = date.addMonths(11);
                strDate += date.format("yyyyMM");
                strs.push(strDate);
            }
            return strs;
        },
        /**
         * @author:wangkun
         * @date:2017-1-29
         * @modifydate:
         * @param:
         * @return:
         * @description:清除数据
         */
        clearData:function(){
            var me = this;
            var option = me.myChart.getOption();
            var size = option.series.length;
            for(var i=0;i<size;i++){
                var dataSize = option.series[i].data.length;
                for(var j=0;j<dataSize;j++){
                    option.series[i].data[j] = 0.0;
                }
            }
            me.myChart.setOption(option);
        },
        /**
         * @author:wangkun
         * @date:2017-1-30
         * @modifydate:
         * @param:
         * @return:
         * @description:获取标题
         */
        getTitle:function(){
            var me = this;
            var title = "";
            var methodObj = me.getSelectMethod();
            var methodName = methodObj.name;
            title += methodName;
            title += "预测";
            var elementObj = me.getSelectElement();
            var elementName = elementObj.name;
            title += elementName;
            var testObj = me.getTestFunction();
            var testFunctionName = testObj.name;
            title += "--"+testFunctionName;
            return title;
        }
    }
    
});