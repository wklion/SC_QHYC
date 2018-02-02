/**
 * @author: wangkun
 * @date:   2016/5/17.
 * @description LSW指数
 */
function LSWPageClass() {
    var t = this;
    t.level = 850;
    t.data = {};
    t.layerWindFillRangeColor = {};
    t.layerOLRFillRangeColor = {};
    t.datasetgrid = {};
    t.stationlayer = {};
    t.isDeal = true; //是否资料处理
    t.layerfactor = {}; //因子图层
    t.drawFactorLuoqu = {}; //画落区
    t.selectFeature = null; //可先图层
    t.layerforcast = {}; //预报区域图层
    t.drawForcastLuoqu = {}; //预报区域落区
    t.map = {};
    t.playindex = 0; //播放index
    t.datacount = 0; //资料个数
    t.timerid = 0; //定时器id
    t.datasetgridofwind = {}; //风场数据集
    t.datasetgridofolr = {}; //风场数据集
    t.currentfeature = {}; //当前绘制对象
    t.pname = "因子区域";
    t.huanhang = "&#13;&#10;"; //换行
    t.startDate = null; //开始日期
    t.endDate = null; //结束日期
    t.layerPredictorName = null; //因子名称图层
    t.layerPredictandName = null; //预报区域名称图层
    t.chartUtil = null;
    this.renderMenu = function () {
        this.CreateResourceDealPanel();
        this.CreateModal();
        t.startDate = new DateSelecter(2, 2, "yyyy-mm-dd");
        t.startDate.intervalMinutes = 60 * 24; //24小时
        var sdate = new Date(new Date().setDate(1)).format("yyyy-MM-dd");
        t.startDate.setCurrentTime(sdate);
        $("#sdatepicker").append(t.startDate.div);
        t.endDate = new DateSelecter(2, 2, "yyyy-mm-dd");
        t.endDate.intervalMinutes = 60 * 24; //24小时
        var edate = new Date(new Date().setDate(1)).addMonths(2).addDays(-1).format("yyyy-MM-dd");
        t.endDate.setCurrentTime(edate);
        $("#edatepicker").append(t.endDate.div);
        t.InitiLayer();
        //绑定事件
        $("#processdata").bind("click", this.GetHasDataDate);
        $("#element button").bind("click", this.ElementClick);
        $("#level button").bind("click", this.LevelClick);
        $("#period button").bind("click", this.PeriodClick);
        $("#areaforcast").bind("click", this.Forcast);
        $("#stationforcast").bind("click", this.Forcast);
        $("#play").bind("click", this.Play);
        $("#test").bind("click", this.Test);
        $("#modal_ok").bind("click", this.SaveGeoToLocal);
        $(".dropdown-menu a").bind("click", this.ManageArea);
        $("#status").bind("click", function () {
            t.SingleViewChange();
        });
        $("#lswforcast").hide();

        //默认加载
        this.LoadArea("因子区域");
        this.LoadArea("预报区域");
        t.chartUtil = new ChartUtil();
    }
    /**
     * @author:wangkun
     * @date:2017-04-06
     * @param:
     * @return:
     * @description:
     */
    this.CreateResourceDealPanel = function () {
        $("#menu_bd").html(`
                <div id="resourcedeal" class="indexForecastMenu">
                    <h6>要素</h6>
                    <div class="text-center"><div class="btn-group" id="element"><button class="btn btn-default active w70" id="uv">UV</button><button class="btn btn-default w70" id="olr">OLR</button></div></div>
                    <h6>层次</h6>
                    <div class="text-center"><div class="btn-group" id="level"><button class="btn btn-default w70" id="1000">1000</button><button class="btn btn-default active w70" id="850">850</button><button class="btn btn-default w70" id="700">700</button></div></div>
                    <h6>滤波周期</h6>
                    <div class="text-center"><div class="btn-group" id="period"><button class="btn btn-default w70" id="10-20">10-20</button><button class="btn btn-default active w70" id="30-60">30-60</button></div></div>
                    <h3>资料日期</h3>
                    <div id="resDate_div" class="normal_div_row">
                        <input id="resDate" type="month" value='2017-09'>
                    </div>
                    <h6>操作</h6>
                    <button class="btn btn-default" id="processdata">资料处理</button>
                        <div class="btn-group">
                            <button class="btn btn-default dropdown-toggle" id="factorset" data-toggle="dropdown">因子区域<span class="caret"></span></button>
                            <ul class="dropdown-menu" role="menu">
                                <li><a pn="因子区域" href="#">添加</a></li>
                                <li><a pn="因子区域" href="#">删除</a></li>
                                <li><a pn="因子区域" href="#">刷新</a></li>
                            </ul>
                        </div>
                        <div class="btn-group">
                            <button class="btn btn-default" data-toggle="dropdown">预报区域<span class="caret"></span></button>
                            <ul class="dropdown-menu" role="menu">
                                <li><a pn="预报区域" href="#">添加</a></li>
                                <li><a pn="预报区域" href="#">删除</a></li>
                                <li><a pn="预报区域" href="#">刷新</a></li>
                            </ul>
                        </div>
                        <button class="btn btn-default" id="areaforcast">区域预报</button>
                        <button class="btn btn-default" id="stationforcast">站点预报</button>
                    <h6>风场</h6>
                    <div id="listwindow" class="timeList_div"></div>
                    <div class="text-right"><button class="btn btn-default" id="play">播放</button><button class="btn btn-default soff" id="status" status="off">显示单点</button></div>
                </div>
            `);
        $("#map_div").append(`
            <div id="forcastdata" class="delete panel panel-default" style="display:none;">
                    <div class="paneltitle panel-heading">
                        <span>预测结果</span><span class="close">&times;</span>
                    </div>
                    <div class="panel-body">
                        <div id="forcastchart" style="display:none;"></div>
                        <table id="tb_station" style="display:none;"></table>
                    </div>
            </div>
         `);
        InitEvent();
        /**
         * @author:wangkun
         * @date:2017-04-06
         * @param:
         * @return:
         * @description:初始化事件
         */
        function InitEvent() {
            $(".close").bind("click", Close);
        }
        /**
         * @author:wangkun
         * @date:2017-03-27
         * @param:
         * @return:
         * @description:关闭事件
         */
        function Close() {
            var parent = $(this).parent();
            if (parent[0].id != "") {
                parent.css("display", "none");
            } else {
                parent.parent().css("display", "none");
            }
        }
    }
    //获取时间列表
    this.GetHasDataDate = function () {
        $("#listwindow").empty();
        ShowProgress("正在处理数据!");
        var level = $("#level button.active").attr("id");
        var period = $("#period button.active").attr("id");
        t.HandleData(level, period);
        t.DisplayDateList();
        /*var url=Url_Config.gridServiceUrl + "services/EFSService/GetValidDate";
        var data='{"elements":"'+elements+'","startdate":"'+strStartDate+'","enddate":"'+strEndDate+'"}';
        AJAX(url,data,"获取有效日期出错!",function(data){
            for(var pro in data)
            {
                var status=data[pro];
                if(isNaN(status))
                {
                    var errortxt=pro+'资料日期为：'+status;
                    $("#msg textarea").append(errortxt+t.huanhang);
                    HideProgress(errortxt);
                    return;
                }
            }
            //$("#msg textarea").append('获取日期成功!'+t.huanhang);
            HideProgress('获取日期成功!');
            t.DisplayDateList();
            t.HandleData(elements,level,period);
        });*/
    }
    //显示结果序列
    this.DisplayDateList = function () {
        var dateControl = document.getElementById("resDate");
        var strDate = dateControl.value+"-01";
        var strDates = strDate.split("-");
        var year = parseInt(strDates[0]);
        var month = parseInt(strDates[1]) - 1;
        var startDate = new MyDate(year,month,1);
        var endDate = new MyDate(year,month,1);
        var endDate = endDate.addMonths(2);
        var strHtml = '<ul class="list-unstyled">';
        while (endDate > startDate) {
            var strDate = startDate.format("yyyy-MM-dd");
            strHtml += '<li>' + strDate + '</li>';
            startDate = startDate.addDays(1);
        }
        strHtml += '</ul>';
        $("#listwindow").empty();
        $("#listwindow").html(strHtml);
        $("#listwindow li").bind("click", t.ListClick);
        HideProgress('处理数据完成!');
    }
    this.ListClick = function () {
        $("#listwindow li").removeClass("active");
        $(this).addClass("active");
        var viewdate = $(this)[0].innerHTML;
        t.DisplayData(viewdate);
    }
    //处理数据
    this.HandleData = function (level, period) {
        ShowProgress('正在处理数据!');
        var dateControl = document.getElementById("resDate");
        var strDate = dateControl.value+"-01";
        var url = Url_Config.gridServiceUrl + "services/EFSService/UVProcess";
        var param = {
            level:level,
            period:period,
            resDate:strDate,
            tempDir:Physics_Config.tempDir,
            derfUVDir:Physics_Config.derfDir,
            uvDir:Physics_Config.uvDir
        };
        param = JSON.stringify(param);
        AJAX(url, param, function(data){
            HideProgress(data);
        }, function () {
            HideProgress("数据处理完成!");
        });
    }
    this.DisplayData = function (viewdate) {
        var startdate = $("#listwindow li:first")[0].innerHTML;
        var enddate = $("#listwindow li:last")[0].innerHTML;
        var period = $("#period button.active")[0].innerHTML;
        var level = $("#level button.active")[0].id;
        var element = $("#element button.active")[0].id;
        var param = {
            element:element,
            level:level,
            period:period,
            startdate:startdate,
            enddate:enddate,
            viewdate:viewdate,
            tempDir:Physics_Config.tempDir
        };
        param = JSON.stringify(param);
        $.ajax({
            type: 'post',
            url: Url_Config.gridServiceUrl + "services/EFSService/DisplayProcessedData",
            data: {
                'para': param
            },
            dataType: 'json',
            error: function () {
                HideProgress("获取数据失败!");
                //$("#msg textarea").append('获取数据失败!' + t.huanhang);
            },
            success: function (data) {
                if (data == undefined || data.length == 0) {
                    //$("#msg textarea").append('未找到数据,请重新处理数据!' + t.huanhang);
                    HideProgress("未找到数据,请重新处理数据!");
                    return;
                } else if (data.length == 1) {
                    t.UpdateNormalEle(data);
                } else if (data.length == 2) {
                    t.UpdateWind(data);
                }
            }
        });
    }
    //更新常规要素
    this.UpdateNormalEle = function (data) {
        var datasetgrid = data[0];
        var rows = datasetgrid.rows;
        var cols = datasetgrid.cols;
        t.datasetgridofolr = new WeatherMap.DatasetGrid(datasetgrid.left, datasetgrid.top, datasetgrid.right, datasetgrid.bottom, rows, cols, 1);
        t.datasetgridofolr.grid = datasetgrid.dvalues;
        t.datasetgridofolr.rows = rows;
        t.datasetgridofolr.cols = cols;
        t.layerOLRFillRangeColor.items = heatMap_OLRStyles;
        GDYB.Legend.update(heatMap_OLRStyles);
        t.layerOLRFillRangeColor.setDatasetGrid(t.datasetgridofolr);
    }
    //更新风场
    this.UpdateWind = function (data) {
        var datasetgridU = data[0];
        var datasetgridV = data[1];
        var uGrids = datasetgridU.dvalues;
        var vGrids = datasetgridV.dvalues;
        var rows = datasetgridU.rows;
        var cols = datasetgridU.cols;
        t.datasetgridofwind = new WeatherMap.DatasetGrid(datasetgridU.left, datasetgridU.top, datasetgridU.right, datasetgridU.bottom, rows, cols, 2);
        var values = [];
        var size = uGrids.length;
        for (var i = 0; i < size; i++) {
            var uVal = uGrids[i];
            var vVal = vGrids[i];
            var speed = Math.sqrt(uVal * uVal + vVal * vVal);
            var angle = 270-Math.atan2(vVal, uVal) * 180 / Math.PI;
            values.push(speed);
            values.push(angle);
        }
        t.datasetgridofwind.grid = values;
        t.datasetgridofwind.rows = rows;
        t.datasetgridofwind.cols = cols;
        t.layerWindFillRangeColor.items = heatMap_10uvStyles;
        t.layerWindFillRangeColor.setDatasetGrid(t.datasetgridofwind);
    }
    //元素点击
    this.ElementClick = function () {
        $("#element button").removeClass("active");
        $(this).addClass("active");
    }
    //层次点击
    this.LevelClick = function () {
        $("#level button").removeClass("active");
        $(this).addClass("active");
        t.level = $(this).attr("id");
        //$("#listwindow").empty();
    }
    //周期点击
    this.PeriodClick = function () {
        $("#period button").removeClass("active");
        $(this).addClass("active");
    }
    //初始化图层
    this.InitiLayer = function () {
        t.map = GDYB.Page.curPage.map;
        t.map.addControl(new WeatherMap.Control.LayerSwitcher());
        t.layerWindFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer("风场", {
            "radius": 40,
            "featureWeight": "value",
            "featureRadius": "geoRadius"
        });
        t.layerWindFillRangeColor.isWind = true;
        t.layerWindFillRangeColor.isAlwaySmooth = false;
        t.layerWindFillRangeColor.isSmooth = false;
        t.map.addLayers([t.layerWindFillRangeColor]);
        t.layerOLRFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer("OLR", {
            "radius": 40,
            "featureWeight": "value",
            "featureRadius": "geoRadius"
        });
        t.layerOLRFillRangeColor.isWind = true;
        t.layerOLRFillRangeColor.isAlwaySmooth = false;
        t.layerOLRFillRangeColor.isSmooth = false;
        t.map.addLayers([t.layerOLRFillRangeColor]);
        t.layerfactor = new WeatherMap.Layer.Vector("因子区域", {
            renderers: ["Canvas2"]
        });
        t.layerfactor.style = {
            strokeColor: "#ff0000",
            strokeWidth: 2.0,
            fillColor: "#ff0000",
            fillOpacity: "0.4"
        };
        t.map.addLayers([t.layerfactor]);
        t.layerforcast = new WeatherMap.Layer.Vector("预报区域", {
            renderers: ["Canvas2"]
        });
        t.layerforcast.style = {
            strokeColor: "#00ff00",
            strokeWidth: 2.0,
            fillColor: "#00ff00",
            fillOpacity: "0.4"
        };
        t.map.addLayers([t.layerforcast]);
        t.drawFactorLuoqu = new WeatherMap.Control.DrawFeature(t.layerfactor, WeatherMap.Handler.PolygonFree);
        t.drawFactorLuoqu.events.on({
            "featureadded": t.DrawFeatureCompleted
        });
        t.map.addControl(t.drawFactorLuoqu);
        t.drawForcastLuoqu = new WeatherMap.Control.DrawFeature(t.layerforcast, WeatherMap.Handler.PolygonFree);
        t.drawForcastLuoqu.events.on({
            "featureadded": t.DrawFeatureCompleted
        });
        t.map.addControl(t.drawForcastLuoqu);
        t.layerPredictorName = new WeatherMap.Layer.Vector("预报因子名称", { renderers: ["Canvas"] });
        t.layerPredictorName.style = {
            strokeColor: "#ff0000",
            strokeWidth: 2.0,
            fillColor: "#ff0000",
            fillOpacity: "0.4"
        };
        t.map.addLayer(t.layerPredictorName);
        t.layerPredictandName = new WeatherMap.Layer.Vector("预报结果名称", { renderers: ["Canvas"] });
        t.layerPredictandName.style = {
            strokeColor: "#ff0000",
            strokeWidth: 2.0,
            fillColor: "#ff0000",
            fillOpacity: "0.4"
        };
        t.map.addLayer(t.layerPredictandName);
    }
    this.StopDragMap = function () {
        for (var i = 0; i < t.map.events.listeners.mousemove.length; i++) {
            var handler = t.map.events.listeners.mousemove[i];
            if (handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag") {
                handler.obj.active = false;
            }
        }
    }
    this.StartDragMap = function () {
        for (var i = 0; i < t.map.events.listeners.mousemove.length; i++) {
            var handler = t.map.events.listeners.mousemove[i];
            if (handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag") {
                handler.obj.active = true;
            }
        }
    }
    /**
     * @author:wangkun
     * @date:2017-03-27
     * @param:
     * @return:
     * @description:获取预报日期
     */
    this.GetForcastDate = function () {
        var endDate = t.endDate.getCurrentTimeReal();
        var strEndDate = endDate.format("yyyy-MM-dd");
    }
    //预报
    this.Forcast = function () {
        ShowProgress("正在生成预报!");
        var forcastType = this.id;
        $("#forcastdata").css("display", "block");
        if (forcastType === "areaforcast") {
            $("#forcastchart").css("display", "block");
            $("#tb_station").css("display", "none");
            //初始化echart
            if (t.chartUtil.echart == null) {
                t.chartUtil.initChart("forcastchart");
            }
            t.CreateXLabel();
        }
        else {
            $("#forcastchart").css("display", "none");
            $("#tb_station").css("display", "block");
            t.initTable();
        }
        var forcastlines = [];
        //获取预报区域--start
        var forcastGeometrys = t.layerforcast.features;
        var forcastGeoSize = forcastGeometrys.length;
        if (forcastGeoSize < 1) {
            HideProgress("");
            layer.alert("没有预报区域，请增加!");
            //$("#msg textarea").append("没有预报区域，请增加!" + t.huanhang);
            return;
        }
        var kong = -1; //分隔
        for (var i = 0; i < forcastGeoSize; i++) {
            var line = [];
            var geoPoint = forcastGeometrys[i].geometry.components[0].components;
            var geoPointSize = geoPoint.length;
            for (var c = 0; c < geoPointSize; c++) {
                var x = geoPoint[c].x;
                var y = geoPoint[c].y;
                line.push(x);
                line.push(y);
            }
            forcastlines.push(line);
            if (i != forcastGeoSize - 1)
                forcastlines.push(kong); //以便分隔
        }
        //获取预报区域--end
        //获取区域
        var factorlines = [];
        var factorGeometrys = t.layerfactor.features;
        var factorGeoSize = factorGeometrys.length;
        if (factorGeoSize < 1) {
            ShowProgress("没有因子区域，请增加!");
            //$("#msg textarea").append("没有因子区域，请增加!" + t.huanhang);
            return;
        }
        for (var i = 0; i < factorGeoSize; i++) {
            var line = [];
            var geoPoint = factorGeometrys[i].geometry.components[0].components;
            var geoPointSize = geoPoint.length;
            for (var c = 0; c < geoPointSize; c++) {
                var x = geoPoint[c].x;
                var y = geoPoint[c].y;
                line.push(x);
                line.push(y);
            }
            factorlines.push(line);
            if (i != factorGeoSize - 1)
                factorlines.push(kong); //以便分隔
        }
        var startDate = t.startDate.getCurrentTimeReal();
        var strStartDate = startDate.format("yyyy-MM-dd");
        var endDate = t.endDate.getCurrentTimeReal();
        var strEndDate = endDate.format("yyyy-MM-dd");
        var level = $("#level button.active").attr("id");
        var period = $("#period button.active").attr("id");
        //发送到服务端
        var url = Url_Config.gridServiceUrl + "services/EFSService/MakeForcast";
        var param = {
            forcastlines: forcastlines,
            factorlines: factorlines,
            startdate: strStartDate,
            enddate: strEndDate,
            level: level,
            period: period,
            forcasttype: forcastType,
            tempDir:Physics_Config.tempDir
        };
        param = JSON.stringify(param);
        // var data = '{"forcastlines":"' + forcastlines + '","factorlines":"' + factorlines + '","startdate":"' + strStartDate + '","enddate":"' + strEndDate + '","level":"' + level + '","period":"' + period + '"}';
        AJAX(url, param, "制作预报失败!", function (data) {
            HideProgress("预报生成成功!");
            if(forcastType === "areaforcast"){
                t.UpdateEChart(data);
            }
            else{
                t.updateTable(data);
            }
        });
    }
    /**
     * @author:wangkun
     * @date:2017-04-25
     * @param:
     * @return:
     * @description:更新表格
     */
    this.updateTable=function(data){
        if(typeof(data)=="undefined"){
            return;
        }
        var strHtml="";
        data.forEach(item=>{
            var name=item.name;
            var itemData=item.lsData;
            strHtml+="<tr>";
            strHtml+="<td>"+name+"</td>";
            var size=itemData.length;
            for(var i=0;i<size;i++){
                var val=itemData[i];
                strHtml+="<td>"+val+"</td>";
            }
            strHtml+="</tr>";
        });
        $("#tb_station tbody").append(strHtml);
    }
    /**
     * @author:wangkun
     * @date:2017-04-06
     * @param:
     * @return:
     * @description:更新图表
     */
    this.UpdateEChart = function (data) {
        if (typeof(data) == 'undefined') {
            HideProgress("返回数据为空!");
            return;
        }
        var subnames = localStorage.getItem("预报区域");
        var subs = subnames.split(",");
        var size = subs.length;
        var option = t.chartUtil.echart.getOption();
        var legends = [];
        for (var i = 0; i < size; i++) {
            legends.push(subs[i]);
        }
        option.legend[0].data = legends;
        option.series.length = 0;
        for (var i = 0; i < size; i++) {
            option.series.push({
                type: 'bar',
                data: data[i].lsData,
                name: subs[i]
            });
        }
        t.chartUtil.echart.setOption(option);
        HideProgress("预报生成完成!");
        //$("#msg textarea").append("预报生成完成!" + t.huanhang);
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:时间序列
     */
    this.CreateXLabel = function () {
        var startDate = t.startDate.getCurrentTimeReal();
        var endDate = t.endDate.getCurrentTimeReal();
        var labels = [];
        while (startDate < endDate) {
            var strDate = startDate.format("MM-dd");
            labels.push(strDate);
            startDate = startDate.addDays(1);
        }
        var option = t.chartUtil.echart.getOption();
        option.xAxis[0].data = labels;
        t.chartUtil.echart.setOption(option);
    }
    //显示预报结果对话框
    this.CreateForcastDLG = function () {
        var strHtml = '<div id="forcastdlg" style="width:800px;height:600px;border:1px solid;background:white;"></div>';
        $("#map_div").append(strHtml);
        strHtml = '<div id="dlgtop" class="div_title"></div></div><div id="dlgcontent" style="height:538px"></div>';
        $("#forcastdlg").html(strHtml);
        strHtml = '<span style="font-size:16px;font-weight:bold;margin:5px 0px 0px 5px;">预报结果</span><button class="close" id="dlgclose" style="line-height:21px;">&times;</button>';
        $("#dlgtop").html(strHtml);
        $("#dlgclose").bind("click", CloseDLG);

        function CloseDLG() {
            $("#forcastdlg").remove();
        }
        $("#forcastdlg").draggable();
    }
    //播放
    this.Play = function () {
        datacount = $("#listwindow li").length;
        var txt = $(this)[0].innerHTML;
        var index = 0;
        if (txt == "播放") {
            if (datacount == 0)
                return;
            t.timerid = setInterval(OrderPlay, 1000);
            $(this)[0].innerHTML = "暂停";
        } else {
            clearInterval(t.timerid);
            $(this)[0].innerHTML = "播放";
        }

        function OrderPlay() {
            if (t.playindex == datacount) {
                t.playindex = 0;
            }
            $("#listwindow li:eq(" + t.playindex + ")").click();
            t.playindex++;
        }
    }
    this.Test = function () {
        //$("#msg textarea").append("追加"+t.huanhang);
        var tt = $("#sdatepicker").datepicker("getDate");
        var yy = tt.format("yyyy-MM-dd");
        alert(yy);
    }
    //绘制完成，添加名称
    this.DrawFeatureCompleted = function (eventArgs) {
        t.currentfeature = eventArgs.feature;
        $("#modal_input_name").modal('show');
        if (t.pname == "因子区域") {
            t.drawFactorLuoqu.deactivate();
            t.StartDragMap();
        } else if (t.pname == "预报区域") {
            t.drawForcastLuoqu.deactivate();
            t.StartDragMap();
        }
    }
    //创建模态对话框
    this.CreateModal = function () {
        var strHtml = '<div class="modal fade" id="modal_input_name" tabindex="-1" role="dialog">' + '<div class="modal-dialog">' + '<div class="modal-content">' + '<div class="modal-header">' + '<button class="close" data-dismiss="modal">&times;</button>' + '<h4 class="modal-title">请输入名称</h4>' + '</div>' + '<div class="modal-body"><div class="form-group has-feedback"><input type="text" class="form-control" id="input_name" placeholder="请输名称："><span class="glyphicon form-control-feedback"></span></div></div>' + '<div class="modal-footer">' + '<button class="btn btn-primary" id="modal_ok">确定</button>' + '</div>' + '</div>' + '</div>' + '</div>';
        $("body").append(strHtml);
    }
    //保存对象到缓存
    this.SaveGeoToLocal = function () {
        //取出对象
        var name = $("#input_name").val();
        t.currentfeature.attributes["name"] = name;
        var val = localStorage.getItem(name);
        if (val != undefined) { //已有值，重新输入
            $(".modal-body div").addClass("has-error");
            $(".modal-body span").addClass("glyphicon-remove");
            return;
        }
        $(".modal-body div").removeClass("has-error");
        $(".modal-body span").removeClass("glyphicon-remove");
        var geometry = t.currentfeature.geometry;
        var points = geometry.components[0].components;
        var size = points.length;
        var strPt = "";
        for (var i = 0; i < size; i++) {
            var x = points[i].x;
            var y = points[i].y;
            strPt += x + "," + y + ",";
        }
        size = strPt.length;
        strPt = strPt.substring(0, size - 1);
        localStorage.setItem(name, strPt);
        var parentname = localStorage.getItem(t.pname);
        parentname = parentname == null ? name : parentname + "," + name;
        localStorage.setItem(t.pname, parentname);
        $("#modal_input_name").modal('hide');
        t.LoadArea(t.pname);
    }
    //区域管理
    this.ManageArea = function () {
        t.pname = $(this).attr("pn");
        var txt = $(this).text();
        if (txt == "添加") {
            t.AddArea();
        } else if (txt == "删除") {
            t.DeleteArea();
        } else if (txt == "刷新") {
            t.LoadArea(t.pname);
        }
    }
    //添加区域
    this.AddArea = function (pname) {
        if (t.pname == "因子区域") {
            t.drawFactorLuoqu.activate();
        } else if (t.pname == "预报区域") {
            t.drawForcastLuoqu.activate();
        }
        t.StopDragMap();
    }
    //删除区域
    this.DeleteArea = function () {
        if (t.selectFeature == null) {
            t.selectFeature = new WeatherMap.Control.SelectFeature(t.layerfactor, {
                onSelect: DeleteFeature
            });
            t.map.addControl(t.selectFeature);
        }
        if (t.pname == "因子区域") {
            t.selectFeature.setLayer(t.layerfactor);
        } else if (t.pname == "预报区域") {
            t.selectFeature.setLayer(t.layerforcast);
        }
        t.selectFeature.activate();

        function DeleteFeature(feature) {
            var name = feature.attributes["name"];
            $("#div_modal_txt_content").html("是否删除<s>" + name + "</s>");
            $("#div_modal_txt").modal();
            $("#div_modal_txt").find("a").unbind();
            $("#div_modal_txt").find("a").click(function () {
                if (typeof (this.id) != "undefined") {
                    if (this.id == "btn_ok") {
                        feature.layer.removeFeatures(feature);
                        var subnames = localStorage.getItem(t.pname);
                        var subs = subnames.split(",");
                        var size = subs.length;
                        var newsubnames = "";
                        for (var i = 0; i < size; i++) {
                            var subname = subs[i];
                            if (subname != name) {
                                newsubnames += subname + ",";
                            }
                        }
                        newsubnames = newsubnames.substring(0, newsubnames.length - 1);
                        localStorage.setItem(t.pname, newsubnames);
                        localStorage.removeItem(name);
                        t.LoadArea(t.pname);
                    }
                    t.selectFeature.deactivate();
                }
            });
        }
    }
    //加载区域
    this.LoadArea = function (pname) {
        var thisLayer = {};
        if (pname == "因子区域") {
            thisLayer = t.layerfactor;
        } else if (pname == "预报区域") {
            thisLayer = t.layerforcast;
        }
        thisLayer.removeAllFeatures();
        var parentname = localStorage.getItem(pname);
        if (parentname == null)
            return;
        var items = parentname.split(",");
        var size = items.length;
        var features = [];
        var txtFeatures = [];
        var style = {
            strokeColor: "#339933",
            strokeOpacity: 1,
            strokeWidth: 3,
            pointRadius: 6
        }
        for (var i = 0; i < size; i++) {
            var subname = items[i];
            var strpts = localStorage.getItem(subname);
            if (strpts == null)
                continue;
            var pts = strpts.split(",");
            var ptsSize = pts.length / 2;
            var points = [];
            for (var j = 0; j < ptsSize; j++) {
                var x = pts[j * 2];
                var y = pts[j * 2 + 1];
                var temppt = new WeatherMap.Geometry.Point(x, y);
                points.push(temppt);
            }
            var linearRings = new WeatherMap.Geometry.LinearRing(points);
            var region = new WeatherMap.Geometry.Polygon([linearRings]);
            var feature = new WeatherMap.Feature.Vector(region, null);
            feature.attributes["name"] = subname;
            features.push(feature);
            var center = region.getCentroid();
            var centerX = center.x;
            var centerY = center.y;
            var geoText = new WeatherMap.Geometry.Point(centerX, centerY);
            var txtFeature = new WeatherMap.Feature.Vector(geoText);
            var style = {
                label: subname,
                fontColor: "#0000ff",
                fontOpacity: "0.5",
                fontFamily: "隶书",
                fontSize: "2em",
                fontWeight: "bold",
                fontStyle: "italic",
                labelSelect: "true",
            }
            txtFeature.style = style;
            txtFeatures.push(txtFeature);
        }
        if (pname == "因子区域") {
            t.layerPredictorName.removeAllFeatures();//先清空所有
            t.layerPredictorName.addFeatures(txtFeatures);
        } else if (pname == "预报区域") {
            t.layerPredictandName.removeAllFeatures();//先清空所有
            t.layerPredictandName.addFeatures(txtFeatures);
        }
        thisLayer.addFeatures(features);
    }
    //查看单点数据
    this.ShowPointVal = function () {
        //判断有无数据
        if ($("#listwindow li").length == 0) {
            return;
        }
        var state = $(this).is(':checked');
        if (state == true) {
            t.map.events.on({
                "click": t.DisplayVal
            });
        } else {
            t.map.events.un({
                "click": t.DisplayVal
            });
        }
    }
    //显示单点的值
    this.DisplayVal = function (event) {
        var currentPosition = this.getLonLatFromPixel(event.xy);
        var pt = currentPosition.lon + "," + currentPosition.lat;
        var element = $("#element button.active").attr("id");
        var level = $("#level button.active").attr("id");
        var period = $("#period button.active").attr("id");
        var startDate = $("#listwindow li:first")[0].innerHTML;
        var endDate = $("#listwindow li:last")[0].innerHTML;
        var url = Url_Config.gridServiceUrl + "services/EFSService/DisplayPointData";
        var param = {
            element:element,
            level:level,
            period:period,
            startdate:startDate,
            enddate:endDate,
            tempDir:Physics_Config.tempDir,
            point:pt
        };
        param = JSON.stringify(param);
        AJAX(url, param, "获取数据失败!", function (data) {
            if ($("#forcastdlg").length == 0) {
                t.CreateForcastDLG();
            }
            var labels = CreateLabelByDate(startDate, endDate);
            var datas = [];
            var size = data.length;
            if (size == 1) {
                var olr = {
                    name: "OLR",
                    data: data[0]
                };
                datas.push(olr);
            } else if (size == 2) {
                var u = {
                    name: "U",
                    data: data[0]
                };
                var v = {
                    name: "V",
                    data: data[1]
                };
                datas.push(u);
                datas.push(v);
            }
            t.UpdateChart(labels, datas);
        });
    }
    //清空消息
    /*this.ClearMsg = function() {
            $("#msg textarea").empty();
        }*/
    //单点显示
    this.SingleViewChange = function () {
        var sta = $("#status").attr("status");
        if (sta == "off") {
            $("#status").attr("status", "on");
            $("#status").removeClass("soff");
            $("#status").addClass("son");
            t.map.events.on({
                "click": t.DisplayVal
            });
        } else if (sta == "on") {
            $("#status").attr("status", "off");
            $("#status").removeClass("son");
            $("#status").addClass("soff");
            t.map.events.un({
                "click": t.DisplayVal
            });
        }
    }
    /**
     * @author:wangkun
     * @date:2017-04-25
     * @param:
     * @return:
     * @description:初始化站点预报显示
     */
    this.initTable = function () {
        $("#tb_station").empty();
        var startDate = t.startDate.getCurrentTimeReal();
        var endDate = t.endDate.getCurrentTimeReal();
        var strDates = [];
        while (startDate <= endDate) {
            var strDate = startDate.format("MM-dd");
            strDates.push(strDate);
            startDate = startDate.addDays(1);
        }
        var days = strDates.length;
        var strHtml = "<thead>";
        strHtml += "<tr>";
        strHtml += "<th>站名</th>";
        for (var i = 0; i < days; i++) {
            var strDate = strDates[i];
            strHtml += "<th>" + strDate + "</th>";
        }
        strHtml += "</tr>";
        strHtml += "</thead>";
        strHtml += "<tbody>";
        strHtml += "</tbody>";
        $("#tb_station").append(strHtml);
    }
}
LSWPageClass.prototype = new PageBase();