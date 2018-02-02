var ModelResView_Module = ['mapUtil', 'vue', 'commonfun', 'layerManageUtil', 'vlegend', 'gridUtil', 'fileCacheUtil', 'processControl', 'Common', 'mapTool'];
/**
 * @author:杠上花
 * @date:2017-09-01
 * @param:
 * @return:
 * @description:模式数据浏览
 */
define(ModelResView_Module, function (maputil, Vue, com, lmu, vlegend, gridUtil, fileCacheUtil, processControl, common) {
    return {
        resData: [{
            id: "prec",
            name: "降水",
            isActive: false,
            level: [],
            fileFormat: "CurDate.atm.PREC.DateStartEnd_sfc_member.nc",
            style: [heatMap_Rain24Styles, month_prec_model]
        },
        {
            id: "temp",
            name: "气温",
            isActive: false,
            level: [],
            fileFormat: "CurDate.atm.TREFHT.DateStartEnd_sfc_member.nc"
        },
        {
            id: "height",
            name: "高度场",
            isActive: false,
            level: [
                { val: 1000, isActive: false },
                { val: 850, isActive: false },
                { val: 500, isActive: true },
                { val: 400, isActive: false },
                { val: 300, isActive: false },
                { val: 200, isActive: false },
                { val: 100, isActive: false }],
            fileFormat: "CurDate.atm.Z3.DateStartEnd_prslevel_member.nc"
        },
        ],
        resHourspan: [],
        vueElement: null,
        vueHourspan: null,
        vuePlay: null,
        vueGridClip: null,
        curElementID: "prec",
        curElementName: "降水",
        curUnit: "day",
        layerFillColor: null,
        playInterval: null,
        newHS: null,
        geo: null,
        ssee: "201709",
        ele: [],
        /**
         * @author:杠上花
         * @date:2017-09-01
         * @param:
         * @return:
         * @description:初始化,入口
         */
        Init: async function () {
            var me = this;
            var status = await fileCacheUtil.Init();
            initRes(); //初始化资源
            initEvent(); //初始化事件
            /**
             * @author:杠上花
             * @date:2017-09-01
             * @param:
             * @return:
             * @description:初始化资源
             */
            async function initRes() {
                $("#spread").css("display", "none");
                $("#title").css("display","block");
                $("#left").empty();
                $("#left").css("display", "none");
                $("#map").append(`
                    <div id="element" class="mode_element delete">
                        <div id="element_div">
                            <div v-for="(item,index) in resData" :id="item.id" :title="item.name" @click="click" :class="{ 'active': item.isActive }"></div>
                        </div>
                        <div class="level">
                            <span>层次</span>
                            <span v-for="item in levels" class="item" :class="{ 'active': item.isActive }" @click="levelClick(item)">{{ item.val }}</span>
                        </div>
                    </div>
                `);
                $("#map").append(`
                    <div id="hourspan_div" class="dv_hs_div delete" v-on:mousemove="mouseMove">
                        <div id="day" class="day" v-on:click="getUnit">日</div>
                        <div id="month" class="month" v-on:click="getUnit">月</div>
                        <div id="hourspan" style="display:flex;flex:1;">
                            <div v-for="(item,index) in resHourspan" class="hs_item" @click="hourspanClick($event)">{{ index+1 }}</div>
                        </div>
                    </div>
                `);
                $("#map").append(`
                    <div id="play" class="delete" :class="{ 'media_play': play,'media_pause':pause }" @click="click"></div>
                `);
                $("#map").append(`
                    <div id="vueType" class="delete vueType">
                        <div v-for="item in tools" :class="[item.class,{'active': item.isActive }]" :title="item.name" @click="click(item)"></div>
                    </div>
                `);
                $("#map").append(`
                    <div id="coverTool" class="delete coverTool iconfont icon-coupon" :class="{active:status}" :title="title" @click="click"></div>
                `);
                $("#map").append(`
                    <div id="timeticker" class="choice delete">
                        <input id="ip" type="month" value="2017-09" @change="change"/>
                    </div>
                `);
                vlegend.Init("map");
                maputil.map.updateSize();
                me.layerFillColor = lmu.addLayer("热点图层", null, null, "grid");
                me.layerFillColor.isShowLabel = false;
                me.layerFillColor.isGradient = false;
                me.layerFillColor.alpha = 200;
                me.layerFillColor.isShowAll = true;

                var obj = await common.getBounds();
                if (obj.suc != null) {
                    me.geo = JSON.parse(obj.suc);
                }
                else {
                    console.log("获取区域数据失败!");
                }
            }
            /**
             * @author:杠上花
             * @date:2017-09-01
             * @param:
             * @return:
             * @description:初始化事件
             */
            function initEvent() {
                me.vueElement = new Vue({
                    el: "#element",
                    data: {
                        resData: me.resData,
                        levels: []
                    },
                    methods: {
                        click: function (e) {
                            if (!me.vuePlay.play) {//暂停
                                $("#play").click();
                            }
                            var target = e.toElement;
                            me.curElementID = target.id;
                            this.resData.forEach(item => {
                                if (item.id === target.id) {
                                    item.isActive = true;
                                    this.levels = item.level;
                                    me.curElementName = item.name;
                                }
                                else {
                                    item.isActive = false;
                                }
                            });
                            $("#day").click();
                        },
                        levelClick: function (obj) {
                            this.levels.forEach(item => {
                                item.isActive = false;
                            });
                            obj.isActive = true;
                        }
                    }
                });
                me.vueHourspan = new Vue({
                    el: "#hourspan_div",
                    data: {
                        resHourspan: me.resHourspan
                    },
                    methods: {
                        getUnit: async function (e) {
                            var id = e.toElement.id;
                            if (!me.vuePlay.play) {//暂停
                                $("#play").click();
                            }
                            me.curUnit = id;
                            if (id === "day") {
                                $(e.toElement).next().removeClass("headActive");
                            } else {
                                $(e.toElement).prev().removeClass("headActive");
                            }
                            $(e.toElement).addClass("headActive");
                            var path = Physics_Config.modelResDir + "/" + me.curElementID + "/" + id + "/";
                            me.newHS = await me.getDateTime(path, id, me.ssee);
                            this.resHourspan = [];
                            me.newHS.forEach(item => {
                                this.resHourspan.push(item);
                            });
                            this.$nextTick(function () {
                                $(".hs_item:first").click();
                            });
                        },
                        mouseMove: function (e) {
                            //console.log(e);
                        },
                        hourspanClick: function (e) {
                            var cur = e.toElement;
                            $(cur).siblings().removeClass("active");
                            $(cur).prevAll().addClass("active");
                            $(cur).addClass("active");
                            me.getData();
                        }
                    }
                });
                me.vuePlay = new Vue({
                    el: "#play",
                    data: {
                        play: true,
                        pause: false
                    },
                    methods: {
                        click: function (e) {
                            if (this.play) {
                                this.pause = true;
                                this.play = false;
                                me.play();
                            }
                            else {
                                this.pause = false;
                                this.play = true;
                                clearInterval(me.playInterval);
                            }
                        }
                    }
                });
                me.vueValType = new Vue({
                    el: "#vueType",
                    data: {
                        tools: [{
                            id: "zhi",
                            name: "值",
                            class: "iconfont icon-zhi",
                            isActive: true
                        }, {
                            id: "juping",
                            name: "距平",
                            class: "iconfont icon-juping",
                            isActive: false
                        }]
                    },
                    methods: {
                        click: function (obj) {
                            this.tools.forEach(item => {
                                item.isActive = false;
                            });
                            obj.isActive = true;
                            me.getData();
                        }
                    }
                });
                me.vueGridClip = new Vue({
                    el: "#coverTool",
                    data: {
                        status: false,
                        title: "遮罩"
                    },
                    methods: {
                        click: function () {
                            this.status = !this.status;
                            common.addCover(me.geo, this.status);
                        }
                    }
                });
                me.elementChoice = new Vue({
                    el: "#timeticker",
                    data: {
                    },
                    methods: {
                        change: async function(){
                            var str = $("#ip")[0].value;
                            me.ssee=str.replace("-", "").substr(0, 6);
                            console.log(me.ssee);
                            console.log(me.newHS);
                            var path = Physics_Config.modelResDir + "/" + me.curElementID + "/" + me.curUnit + "/";
                            me.newHS = await me.getDateTime(path, me.curUnit, me.ssee);
                            console.log(me.newHS);
                            $("#day").click();
                        },
                    },
                });
                document.onkeydown = function (event) {
                    if (event.keyCode === 13) {//回车
                        $("#play").click();
                    }
                    else if (event.keyCode === 37) {//左
                        if (!me.vuePlay.play) {
                            return;
                        }
                        $(".hs_item.active:last").prev().click();
                    }
                    else if (event.keyCode === 39) {//右
                        if (!me.vuePlay.play) {
                            return;
                        }
                        $(".hs_item.active:last").next().click();
                    }
                    else if (event.keyCode === 38) {//上
                        if (!me.vuePlay.play) {
                            return;
                        }
                        $("#element_div div.active:first").prev().click();
                    }
                    else if (event.keyCode === 40) {//下
                        if (!me.vuePlay.play) {
                            return;
                        }
                        $("#element_div div.active:first").next().click();
                    }
                }
                $("#element_div div:first").click();
            }
        },
        /**
         * @author:杠上花
         * @date:2017-03-09
         * @param:path-路径,unit-单位
         * @return:
         * @description:获取数据时间
         */
        getDateTime: function (path, unit, slet) {
            var me = this;
            var param = {
                path: path,
                unit: unit,
                slet: slet,
            };
            param = JSON.stringify(param);
            var url = Url_Config.gridServiceUrl + "services/FileInfo/getResDate";
            var pro = new Promise(function (resolve, reject) {
                com.AJAX(url, param, reject, resolve);
            });
            return pro;
        },
        /**
         * @author:杠上花
         * @date:2017-09-11
         * @param:hourspan-时效
         * @return:
         * @description:获取数据时间
         */
        getData: async function () {
            var me = this;
            var hourspan = $("#hourspan div.active:last").text();
            var url = Url_Config.gridServiceUrl + "services/FileData/getGrid";
            var strDateTime = me.vueHourspan.resHourspan[0];
            var level = me.getLevel() + "";
            var strLevel = level.PadLeft(4, "0");
            var strFileName = me.getFileNameFormat();
            strFileName = strFileName.replace("level", strLevel);//替换层次
            var strCurDate = "";
            if (me.curUnit === "day") {
                strCurDate = me.newHS[0];
                // var strJPFileName = parseInt(strCurDate.substring(4, 6)) + "_" + hourspan + ".tif";
                var date =new Date($("#ip")[0].value+"-01");

                var newDate = date.addDays(hourspan-1);
                strJPFileName = newDate.format("MMdd")+".tif";
            }
            else {
                strCurDate = me.newHS[0] + "01";
                var monthint=parseInt(strCurDate.substring(4, 6))+parseInt(hourspan)-1;
                var monthinty=monthint%12;
                if(monthinty==0)
                {
                   monthint=12
                }
                else(
                    monthint=monthinty
                )
                strJPFileName=monthint+".tif";
            }
            var strDateStartEnd = me.newHS[0] + "-" + me.newHS[me.newHS.length - 1];
            strFileName = strFileName.replace("CurDate", strCurDate);//替换开始时间
            strFileName = strFileName.replace("DateStartEnd", strDateStartEnd);//替换时间段
            var strFile = Physics_Config.modelResDir + me.curElementID + "/" + me.curUnit + "/" + strFileName;
            var valTypeID = me.getValueType();//获取值类型
            //获取距平文件

            // var strJPFileName = parseInt(strCurDate.substring(4, 6)) + "_" + hourspan + ".tif";
            var strAvgFile = "";
            if(me.curElementID == "height"){
                strAvgFile = Physics_Config.modeJPResDir + me.curElementID + "Avg/" + me.curUnit +"/"+ level + "/" + strJPFileName;
            }
            else{
                strAvgFile = Physics_Config.modeJPResDir + me.curElementID + "Avg/" + me.curUnit + "/" + strJPFileName;
            }
            var param = {
                file: strFile,
                avgFile: strAvgFile,
                hourspan: hourspan,
                valTypeID: valTypeID,
                elementID: me.curElementID
            }
            param = JSON.stringify(param);
            var datasetgrid = await gridUtil.downFileGrid(me.curUnit, url, param, me.curElementID, hourspan, strDateTime, level);
            if (datasetgrid == null) {
                me.layerFillColor.setDatasetGrid(null);
                processControl.hide("无数据!", 1000);
                return;
            }
            if(valTypeID == "zhi"){
                com.datasetGridDeal(datasetgrid, me.curElementID, valTypeID);
            }
            var style = me.getStyle();
            me.layerFillColor.items = style;
            vlegend.setStyle(style);
            me.layerFillColor.setDatasetGrid(datasetgrid);
            me.layerFillColor.refresh();
            me.showLine(datasetgrid,style);
            vueTitle.name = me.curElementName;
            var date = com.convertStringToDate(datasetgrid.nwpmodelTime);
            if (me.curUnit === "day") {
                date = date.addDays(hourspan - 1);
            }
            else {
                date = date.addMonths(hourspan - 1);
            }
            var strDate = date.format("yyyy-MM-dd");
            vueTitle.datetime = strDate;
            if(valTypeID=="zhi"){
                vueTitle.type="值"
            }
            else{
                vueTitle.type="距平"
            }
            
            me.vueGridClip.status = false;
            me.vueGridClip.click();
        },
        /**
         * @author:杠上花
         * @date:2017-09-11
         * @param:
         * @return:
         * @description:播放
         */
        play: async function () {
            var me = this;
            console.log("开始播放!");
            var hourspans = me.vueHourspan.resHourspan;
            var level = me.getLevel() + "";
            var strLevel = level.PadLeft(4, "0");
            var strFileName = me.getFileNameFormat();
            strFileName = strFileName.replace("level", strLevel);//替换层次
            var strCurDate = "";
            if (me.curUnit === "day") {
                strCurDate = me.newHS[0];
            }
            else {
                strCurDate = me.newHS[0] + "01";
            }
            var strDateStartEnd = me.newHS[0] + "-" + me.newHS[me.newHS.length - 1];
            strFileName = strFileName.replace("CurDate", strCurDate);//替换开始时间
            strFileName = strFileName.replace("DateStartEnd", strDateStartEnd);//替换时间段
            var strFile = Physics_Config.modelResDir + "/" + me.curElementID + "/" + me.curUnit + "/" + strFileName;
            var valTypeID = me.getValueType();//获取值类型
            var strJPFileName = parseInt(strCurDate.substring(4, 6)) + "_hourspan.tif";
            var strJPFile = Physics_Config.modeJPResDir + me.curElementID + "Avg/" + me.curUnit + "/" + strJPFileName;
            var param = {
                file: strFile,
                jpFile: strJPFile,
                hourspan: "",
                valTypeID: valTypeID,
                elementID: me.curElementID
            }
            var url = Url_Config.gridServiceUrl + "services/FileData/getGrid";
            await gridUtil.downGrids(me.curUnit, url, param, me.curElementID, hourspans, hourspans[0]);
            me.playInterval = setInterval(function () {
                if ($(".hs_item.active:last").next().length > 0) {
                    $(".hs_item.active:last").next().click();
                }
                else {
                    $(".hs_item:first").click();
                }
            }, 2000);
        },
        /**
         * @author:杠上花
         * @date:2017-12-25
         * @modifydate:
         * @param:
         * @return:
         * @description:获取层次
         */
        getLevel: function () {
            var me = this;
            var level = -1;
            me.vueElement.levels.forEach(item => {
                if (item.isActive) {
                    level = item.val;
                }
            });
            return level;
        },
        /**
         * @author:杠上花
         * @date:2017-12-25
         * @modifydate:
         * @param:
         * @return:
         * @description:获取文件名模版
         */
        getFileNameFormat: function () {
            var me = this;
            var strFileName = "";
            me.vueElement.resData.forEach(item => {
                if (item.isActive) {
                    strFileName = item.fileFormat;
                    return;
                }
            });
            return strFileName;
        },
        /**
         * @author:杠上花
         * @date:2017-12-25
         * @modifydate:
         * @param:
         * @return:
         * @description:获取值类型(值或距平)
         */
        getValueType: function () {
            var me = this;
            var valTypeID = "";
            me.vueValType.tools.forEach(item => {
                if (item.isActive) {
                    valTypeID = item.id;
                    return;
                }
            });
            return valTypeID;
        },
        /**
         * @author:杠上花
         * @date:2017-12-26
         * @modifydate:
         * @param:
         * @return:
         * @description:获取样式
         */
        getStyle: function () {
            var me = this;
            var style = null;
            var valType = me.getValueType();
            if (me.curUnit === "day") {//天
                if (valType === "zhi") {
                    if (me.curElementID === "prec") {
                        style = heatMap_Rain24Styles;
                    }
                    else if (me.curElementID === "temp") {
                        style = heatMap_TempStyles;
                    }
                    else if (me.curElementID === "height") {
                        style = heatMap_500hPaHightStyles;
                    }
                }
                else if (valType === "juping") {
                    if (me.curElementID === "prec") {
                        style = month_jp_rain;
                    }
                    else if (me.curElementID === "temp") {
                        style = temp_jp;
                    }
                    else if (me.curElementID === "height") {
                        style = heatMap_HeighJPStyles;
                    }
                }
            }
            else {//月
                if (valType === "zhi") {
                    if (me.curElementID === "prec") {
                        style = month_prec;
                    }
                    else if (me.curElementID === "temp") {
                        style = heatMap_TempStyles_month;
                    }
                    else if (me.curElementID === "height") {
                        style = heatMap_500hPaHightStyles;
                    }
                }
                else if (valType === "juping") {
                    if (me.curElementID === "prec") {
                        style = month_jp_rain;
                    }
                    else if (me.curElementID === "temp") {
                        style = temp_jp_month;
                    }
                    else if (me.curElementID === "height") {
                        style = heatMap_HeighJPStyles;
                    }
                }
            }
            return style;
        },
        /**
         * @author:杠上花
         * @date:2017-1-31
         * @modifydate:
         * @param:
         * @return:
         * @description:显示等值线
         */
        showLine:function(dg,style){
            var me = this;
            var name = "等值线";
            var dZValues = [];
			let styleSize=style.length;
			for(let i=0;i<styleSize;i++){
				let val=style[i].end;
				dZValues.push(val);
            }
            var contour = new WeatherMap.Analysis.Contour();
			var result = contour.analysis(dg, dZValues);
			var features = [];
			var resultSize=result.length;
			if(resultSize > 0){
				for(var i=0;i<resultSize;i++){
					var geoline = result[i].geoline;
					var dZValue = result[i].dZValue;
					var feature = new WeatherMap.Feature.Vector(geoline);
					feature.attributes.dZValue = dZValue.toString();
					features.push(feature);
				}
			}
			var layer = lmu.addLayer(name, "vector", null,"line","Contour");
			layer.renderer.labelField = "dZValue";
			layer.style = {
                fontFamily:"Arial",
                fontColor:"red",
                fontSize:"16px",
                fontWeight:"bold",
                strokeColor: "#c47a55",
                strokeWidth: 1.0
            };
            layer.removeAllFeatures();
			layer.addFeatures(features);
			layer.redraw();
        }
    }
});