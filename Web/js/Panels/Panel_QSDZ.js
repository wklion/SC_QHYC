/*
 * 趋势订正
 * by zouwei 2015-05-10
 * */
function Panel_QSDZ(div){
    var stationX = 0.0; //代表站经度
    var stationY = 0.0; //代表站纬度
    var hourSpans = []; //时效数组
    var gridValues = []; //格点值数组
    var datasource=0;
    var t=this;

    this.div = div;
    this.createPanelDom = function(){
        this.panel = $("<div id=\"Panel_QSDZ\" class=\"dragPanel\">"
            +"<div class=\"title\"><span>趋势订正</span><a class=\"closeBtn\">×</a></div>"
            +"<div class=\"body\">"
            +"<div id='divLeft' style='float:left'>"
            +"<div id='divElement_QS' style='display: none'><span>选择要素：</span><select id='selectElement_QS' style='width:100px;height:25px;line-height:25px;margin-top:5px;'><option>风速</option><option>风向</option></select></div>"
            +"<div><span>区划类型：</span><select id='selectClimaticRegionType_QS' style='width:100px;height:25px;line-height:25px;margin-top:5px;'><option>无</option></select></div>"
            +"<div><span>选择区域：</span><select id='selectClimaticRegionItem_QS' style='width:100px;height:25px;line-height:25px;margin-top:5px;'><option>无</option></select></div>"
            +"<div id='divStation'><span>站&nbsp;&nbsp;&nbsp;&nbsp;点：</span><select  id='selectStation' style='width:100px;height:25px;line-height:25px;margin-top:5px;'/></div>"
            //<input  id='inputStation' type='text' style='width:100px;margin-top:5px;' value='南宁（59134）'/>
            +"<div id='divTool' style='float:right;margin-right: 0px;margin-top: 10px;'>"
            +"<button id='btnDrawLuoqu' style='width: 80px'>绘制区域</button><button id='btnApply_QS' style='width: 80px;margin-left: 10px'>应用</button>"
            +"</div>"
            +"</div>"
            +"<div id='divRight' style='float: left'>"
//            +"<div id='divTable_QS'  style='margin-left: 20px;'>"
//            +"</div>"
            +"<div id='divChart' style='width:100%'>"
            /*+"<canvas id='canvas'></canvas>"*/
            +"</div>"
            +"<div id='divOption' style='width:200px;height: 20px;margin-left: 15px;'>"
            +"<span style=''>单站</span>"
            +"<input id='cSingle' name='datatype' type='radio' value='single' checked='checked'>"
            +"<span style='width: 100px'>区域</span>"
            +"<input id='cArea' name='datatype' type='radio' value='area'>"
            +"</div>"
            +"</div>"
            +"</div>"
            +"</div>")
            .appendTo(this.div);
        //var canvas = document.getElementById("canvas");
        //cavas.width = $("#divChart").width;
        //GDYB.ChartClass.displayChart();
    }
    this.init();
    this.panel.css({
        "width":"100%",
        "bottom":"0px",
        "left":"0px"
    });

    var widthParent = parseInt($("#Panel_QSDZ").css("width"));
    var widthLeft = parseInt($("#divLeft").css("width"));
    $("#divRight").css("width", widthParent-widthLeft - 25);

    //refreshChart();
    //$("#inputStation").change(this.refreshChart);

    if(GDYB.GridProductClass.currentElement == "wmax" || GDYB.GridProductClass.currentElement == "10uv"){
        $("#divElement_QS").css("display", "block");
    }
    else{
        $("#divElement_QS").css("display", "none");
    }

    initType();
    function initType(){
        $("#selectClimaticRegionType_QS").empty();
        var url=Url_Config.gridServiceUrl+"services/ClimaticRegionService/getClimaticRegionTypes";
        $.ajax({
            data: {"para": "{}"},
            url: url,
            dataType: "json",
            success: function (data) {
                if(data.length > 0)
                {
                    for(var i=0; i<data.length; i++)
                    {
                        $("#selectClimaticRegionType_QS").append("<option value='" + data[i].datasetName + "'>" + data[i].typeName + "</option>");
                    }
                    fillClimaticRegionItem(data[0].datasetName);
                }
            },
            type: "POST"
        });
    }

    $("#selectClimaticRegionType_QS").change(function(){
        fillClimaticRegionItem($(this).val());
    });
    //单站选中
    $("#cSingle").click(function(){
        datasource=0;
        var btn=document.getElementById("btnApply_QS");
        btn.disabled=false;
        refreshChart();
    });
    //区域选中
    $("#cArea").click(function () {
        datasource=1;
        var btn=document.getElementById("btnApply_QS");
        btn.disabled=true;
        refreshChart();
    });
    function fillClimaticRegionItem(datasetName){
        $("#selectClimaticRegionItem_QS").empty();
        var url=Url_Config.gridServiceUrl+"services/ClimaticRegionService/getClimaticRegionItemNames";
        $.ajax({
            data: {"para": "{datasetname:'" + datasetName + "'}"},
            url: url,
            dataType: "json",
            success: function (data) {
                for(var i=0; i<data.length; i++)
                {
                    $("#selectClimaticRegionItem_QS").append("<option value='" + data[i].regionId + "'>" + data[i].regionName + "</option>");
                }
                if(data.length > 0)
                {
                    refreshClimaticRegionItem($("#selectClimaticRegionType_QS").val(),data[0].regionId,data[0].regionName);
                }
            },
            type: "POST"
        });
    }

    $("#selectClimaticRegionItem_QS").change(function(){
            refreshClimaticRegionItem($("#selectClimaticRegionType_QS").val(), $(this).val(),$("#selectClimaticRegionItem_QS").find("option:selected").text());
    });

    $("#selectStation").change(function(){
        var str=$("#selectStation option:selected")[0].innerHTML;
        var strs=str.split("(");
        str=strs[1].substring(0,strs[1].length-1);
        strs=str.split(",");
        if(strs.length==2){
            stationX=strs[0];
            stationY=strs[1];
            refreshChart();
            ReflushPos();
        }
    });
    //刷新定位
    function ReflushPos(){
        GDYB.GridProductClass.layerMarkers.clearMarkers();
        var size = new WeatherMap.Size(25,30);
        var offset = new WeatherMap.Pixel(-(size.w/2), -size.h);
        var icon = new WeatherMap.Icon('imgs/marker.png', size, offset);
        GDYB.GridProductClass.layerMarkers.addMarker(new WeatherMap.Marker(new WeatherMap.LonLat(stationX,stationY),icon));
    }
    //刷新数据
    function refreshClimaticRegionItem(datasetName, regionId,name)
    {
        showClimaticRegionItem(datasetName, regionId,name);
    }
    function showClimaticRegionItem(datasetName, regionId,name)
    {
        var url=Url_Config.gridServiceUrl+"services/ClimaticRegionService/getClimaticRegionItem";
        $.ajax({
            data: {"para": "{datasetName:'" + datasetName+ "',regionId:" + regionId + "}"},
            url: url,
            dataType: "json",
            success: function (data) {
                var feature = GDYB.FeatureUtilityClass.getFeatureFromJson(data);
                var fAttributes = feature.attributes;
                fAttributes["FEATUREID"] = regionId;//fAttributes["SMID"];
                var url=Url_Config.gridServiceUrl+"services/ClimaticRegionService/getStationsByArea";
                $.ajax({
                    data: {"para": "{name:'" + name + "'}"},
                    url: url,
                    dataType: "json",
                    success: function (data) {
                        //显示站点
                        $("#selectStation").empty();
                        for (var i = 0; i < data.length; i++) {
                            var strs = data[i].split(",");
                            $("#selectStation").append("<option value='" + strs[1] + "'>" + strs[0]+"("+strs[2]+","+strs[3]+")" + "</option>");
                            if(i==0){
                                stationX=strs[2];
                                stationY=strs[3];
                            }
                        }
                        //气候区划
                        GDYB.GridProductClass.layerClimaticRegion.removeAllFeatures();
                        feature.style = {
                            strokeColor: "#a548ca",
                            strokeWidth: 2.0,
                            fillColor: "#FF0000",
                            fillOpacity: "0.3",
                            fill:false
                        };
                        var features = [];
                        features.push(feature);
                        GDYB.GridProductClass.layerClimaticRegion.addFeatures(features);

                        refreshChart(); //必须要让该线程结束，也即stationX、stationY变化后，才能刷新地图，否则stationX、stationY会匹配错误

                        if(GDYB.GridProductClass.layerLuoqu != null)
                            GDYB.GridProductClass.layerLuoqu.removeAllFeatures(); //移除落区
                        if(GDYB.GridProductClass.layerLuoquCenter != null)
                            GDYB.GridProductClass.layerLuoquCenter.removeAllFeatures(); //移除落区中心点
                        refreshChart();
                        ReflushPos();
                    },
                    type: "POST"
                });
            },
            type: "POST"
        });
    }

    //初始化（刷新）图表
    function refreshChart(){
        hourSpans = [];
        gridValues = [];
        var isWindDirection = (GDYB.GridProductClass.currentElement == "wmax" || GDYB.GridProductClass.currentElement == "10uv") && $("#selectElement_QS").val() == "风向";

        var x = stationX;
        var y = stationY;
        var strLabels = [];
        var dValues = [];
        var dataCache = GDYB.GridProductClass.dataCache;
        var elementData = dataCache.getData(GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion, GDYB.GridProductClass.currentDateTime, GDYB.GridProductClass.currentElement);
        var myDate=new Date(GDYB.GridProductClass.currentDateTime);
        for(var key in elementData)
        {
            var val=0;
            var vailStationCount=0;//有效站
            var hourspanData = elementData[key];
            if(hourspanData != null && hourspanData.data != null) {
                strLabels.push(myDate.format("MM-dd"));
                myDate.addDays(1);
                hourSpans.push(key);
                var datasetGrid = hourspanData.data;
                if(datasource==0){//单站
                    var cell = datasetGrid.xyToGrid(x, y);
                    var cols=datasetGrid.cols;
                    var index=cell.y*cols+cell.x;
                    val = isWindDirection?datasetGrid.grid[index].direction:datasetGrid.grid[index];
                    if(val == datasetGrid.noDataValue) //如果是无效值，暂时用0表示。
                        val = 0;
                }
                else{//区域
                    $('#selectStation option').each(function () {
                    var str=$(this).text();
                    var strH=$(this).outerHTML;
                    var startPos= str.indexOf("(");
                    var endPos= str.indexOf(")");
                    if(startPos!=-1||endPos!=-1){
                        str=str.substring(startPos+1,endPos);
                        var strs=str.split(",");
                        var x=strs[0];
                        var y=strs[1];
                        var cell = datasetGrid.xyToGrid(x, y);
                        var cols=datasetGrid.cols;
                        var index=cell.y*cols+cell.x;
                        var tempVal = isWindDirection?datasetGrid.grid[index].direction:datasetGrid.grid[index];
                        if(tempVal != datasetGrid.noDataValue) //如果是无效值，暂时用0表示。
                            val=val+tempVal;
                        vailStationCount++;
                    }
                    });
                }
                if(datasource==1&&vailStationCount!=0){
                    val=Math.round(val/vailStationCount);
                }
                else{
                    val=Math.round(val);
                }
                dValues.push(val);
            }
        }
        var danwei="";
        if(GDYB.GridProductClass.currentElement=="2t"){
            danwei="℃";
        }
        else if(GDYB.GridProductClass.currentElement=="r24"){
            danwei="mm";
        }
        ShowData(strLabels,dValues,danwei);
    }
    function ShowData(strLabels,dValues,danwei){//展示数据
        t.chart = new Highcharts.Chart({
            chart: {
                height: 150,
                type: 'column',
                renderTo:'divChart'
            },
            title: {
                text: ''
            },
            legend: {
                enabled:false
            },
            xAxis: {
                categories: strLabels
            },
            yAxis: {
                min: null,
                title: {
                    text: danwei
                }
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0"></td>' +
                '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            credits: {
                enabled: false
            },
            series: [{
                name: "",
                data:dValues,
                draggableY:true
            }]
        })
    }
    //绘制区域
    $("#btnDrawLuoqu").click(function(){
        GDYB.GridProductClass.currentGridValueDown = GDYB.GridProductClass.datasetGrid.noDataValue;
        GDYB.GridProductClass.currentGridValueUp = GDYB.GridProductClass.datasetGrid.noDataValue;
        startDrawLuoqu();
        isDrawing = true;

        function startDrawLuoqu(){
            GDYB.GridProductClass.layerLuoqu.removeAllFeatures();
            GDYB.GridProductClass.layerLuoquCenter.removeAllFeatures();
            GDYB.GridProductClass.drawLuoqu.activate();
            GDYB.GridProductClass.drawFreePath.deactivate();
            stopDragMap();

            function stopDragMap()
            {
                var map = GDYB.Page.curPage.map;
                for(var i =0; i < map.events.listeners.mousemove.length; i++) {
                    var handler = map.events.listeners.mousemove[i];
                    if(handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag")
                    {
                        handler.obj.active = false;
                    }
                }
            }
        }
    });

    $("#div_element").find("button").click(function(){
        if(GDYB.GridProductClass.currentElement == "wmax" || GDYB.GridProductClass.currentElement == "10uv"){
            $("#divElement_QS").css("display", "block");
        }
        else{
            $("#divElement_QS").css("display", "none");
        }
        //refreshChart();
    });

    $("#selectElement_QS").change(function(){
        //refreshChart();
    });

    //落区绘制完成
    var isDrawing = false;
    GDYB.GridProductClass.drawLuoqu.events.on({"featureadded": drawCompleted});
    function drawCompleted() {
        if(isDrawing){
            isDrawing = false;
            addLuoquCenter();
            stopDrawLuoqu();

            //添加落区中心点
            function addLuoquCenter(){
                var feature = GDYB.GridProductClass.layerLuoqu.features[0];
                var bounds = feature.geometry.bounds;
                var centerLonLat = {x:bounds.left + (bounds.right - bounds.left)/2, y:bounds.bottom+(bounds.top - bounds.bottom)/2};
                var pointCenter = new WeatherMap.Geometry.Point(centerLonLat.x, centerLonLat.y);
                var featureCenter = new WeatherMap.Feature.Vector(pointCenter);
                GDYB.GridProductClass.layerLuoquCenter.addFeatures([featureCenter]);

                GDYB.GridProductClass.layerClimaticRegion.removeAllFeatures(); //移除气候区划
                //stationX = centerLonLat.x;
                //stationY = centerLonLat.y;
                //refreshChart();
            }

            function stopDrawLuoqu(){
                startDragMap();
                if(GDYB.GridProductClass.drawLuoqu != null)
                    GDYB.GridProductClass.drawLuoqu.deactivate();

                function startDragMap()
                {
                    var map = GDYB.Page.curPage.map;
                    for(var i =0; i < map.events.listeners.mousemove.length; i++) {
                        var handler = map.events.listeners.mousemove[i];
                        if(handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag")
                        {
                            handler.obj.active = true;
                        }
                    }
                }
            }
        }
    };

    //落区中心点移动
    GDYB.GridProductClass.dragFeature.onComplete = function(feature, pixel){
        if(feature != null && feature.geometry.CLASS_NAME == "WeatherMap.Geometry.Point") {
            stationX = feature.geometry.x;
            stationY = feature.geometry.y;
            //refreshChart();
        }
    };

    //点击应用
    $("#btnApply_QS").click(function(){
        if(t.chart == null)
            return;

        var geo = null;
        if(GDYB.GridProductClass.layerLuoqu != null && GDYB.GridProductClass.layerLuoqu.features.length != 0)
            geo = GDYB.GridProductClass.layerLuoqu.features[0].geometry;
        else if(GDYB.GridProductClass.layerClimaticRegion != null && GDYB.GridProductClass.layerClimaticRegion.features.length != 0)
            geo = GDYB.GridProductClass.layerClimaticRegion.features[0].geometry;
        if(geo == null)
            return;

        var isWindDirection = (GDYB.GridProductClass.currentElement == "wmax" || GDYB.GridProductClass.currentElement == "10uv") && $("#selectElement_QS").val() == "风向";

        //var method = 2; //固定增量方式订正，无法解决基准格点值为0的情况，无法除以0，比如降水为0，订正为10，无法知道增加的百分比为多少
        var method = isWindDirection?0:1; //由于上面的问题，只能统一加减值
        var datas = t.chart.series[0].data;
        for(var i=0; i<datas.length; i++)
        {
            var hourSpan = hourSpans[i];
            var hourSpanData = GDYB.GridProductClass.dataCache.getData(GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion, GDYB.GridProductClass.currentDateTime, GDYB.GridProductClass.currentElement, hourSpan);
            if(hourSpanData != null && hourSpanData.data != null){
                var datasetGrid = hourSpanData.data;
               var x = stationX;
                var y = stationY;
                var cell = datasetGrid.xyToGrid(x, y);
                var cols=datasetGrid.cols;
                var index=cell.y*cols+cell.x;
                var valSrc = isWindDirection?datasetGrid.grid[index].direction : datasetGrid.grid[index];
                var increment = datas[i].y-valSrc;
                if(increment != 0) {
                    GDYB.GridProductClass.fillRegion(datasetGrid, geo, isWindDirection ? datas[i] : increment, method, GDYB.GridProductClass.currentElement, isWindDirection);
                    GDYB.GridProductClass.dataCache.setDataStatus(GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion, GDYB.GridProductClass.currentDateTime, GDYB.GridProductClass.currentElement, hourSpan, 1); //更新已修改状态
                }
            }
        }
        GDYB.GridProductClass.layerFillRangeColor.refresh();
    });
}

Panel_QSDZ.prototype = new DragPanelBase();