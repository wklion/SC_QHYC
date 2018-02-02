/*
* 格点产品类
* by zouwei, 2015-05-10
*
* */
function GridProductClass() {}

GridProductClass.prototype.defaultscheme = null; //初始场默认方案
GridProductClass.prototype.currentUserName = null; //当前用户
GridProductClass.prototype.currentUserDepart = null; //当前用户所在部门
GridProductClass.prototype.currentUserArea = null; //当前用户所在地区
GridProductClass.prototype.currentForecaster = ""; //当前预报员
GridProductClass.prototype.forcusAreaName = null; //保存用户添加的关注区域名称
GridProductClass.prototype.areaType = null; // 保存区域类型
GridProductClass.prototype.currentPost = null; //当前岗位（关键岗：前3天0-72，值班岗：后四天72-168，全部：0-168，首席岗：0-168）

GridProductClass.prototype.currentElement = null; //当前要素
GridProductClass.prototype.currentElementName = null; //当前要素名称
GridProductClass.prototype.currentLevel = null; //当前层次
GridProductClass.prototype.currentFromModel = null; //调入模式
GridProductClass.prototype.currentType = null; //当前类型
GridProductClass.prototype.currentVersion = null; //当前版本
GridProductClass.prototype.currentHourSpan = null; //当前时效
GridProductClass.prototype.currentHourSpanTotal = 168; //当前总时效
GridProductClass.prototype.currentDateTime = null; //当前预报时间
GridProductClass.prototype.currentMakeTime = null; //当前制作时间
GridProductClass.prototype.timeoutid = null;
GridProductClass.prototype.layerLabel = null;   //填值图层
GridProductClass.prototype.layerPlot = null;   //填图图层
GridProductClass.prototype.layerLabelOld = null;   //填值图层
GridProductClass.prototype.layerContour = null; //等值线图层
GridProductClass.prototype.layerPolygon = null; //填色图层
GridProductClass.prototype.layerFillRangeColor = null; //热点图层
GridProductClass.prototype.layerLuoqu = null;   //落区图层
GridProductClass.prototype.layerLuoquCenter = null;   //落区中心图层，用于拖拽中心点，订正暴雨/大风区/高温中心等。
GridProductClass.prototype.layerFreePath = null;   //自由曲线图层，用于风向订正
GridProductClass.prototype.layerClimaticRegion = null; //气候区划图层
GridProductClass.prototype.layerMarkers = null; //（综合）标记图层，用于显示（气候区划）代表站等
GridProductClass.prototype.layerMagic = null; //魔术图层
GridProductClass.prototype.layerStreamLine = null; //流线图层
GridProductClass.prototype.layerMapping = null; //绘图辅助图层

GridProductClass.prototype.layerWindDirection = null; //用于风向订正的线图层
GridProductClass.prototype.drawWindDirection = null; //画线，风向

GridProductClass.prototype.drawRectangle = null; //画矩形
GridProductClass.prototype.drawPolygon = null;   //画多边形
GridProductClass.prototype.drawLuoqu = null;     //画落区
GridProductClass.prototype.drawCircle = null;    //画圆形
GridProductClass.prototype.drawEllipse = null;   //画椭圆
GridProductClass.prototype.drawFreePath = null;     //画自由曲线，用于风向订正

GridProductClass.prototype.dragFeature = null; //拖拽要素

GridProductClass.prototype.currentGridValueDown = null; //落区订正格点值下限
GridProductClass.prototype.currentGridValueUp = null; //落区订正格点值上限
GridProductClass.prototype.currentGridTag = null; //落区订正格点标识，用于标示雨雪等属性
GridProductClass.prototype.currentGridValuePicked = null; //当前拾取的格点值
GridProductClass.prototype.currentWindDirection = null; //当前风向
GridProductClass.prototype.luoquCorrectType = 0; //落区订正方式。0-保持空间分布趋势，1-距离反比权重插值
GridProductClass.prototype.luoquCorrectJustOnStation = false; //落区订正仅赋值到站点

GridProductClass.prototype.action = -1; //订正动作，CorrectAction枚举类型
GridProductClass.prototype.fillColorType = "国标"; //填色类型：国标、值域

//GridProductClass.prototype.grid = null; //格点（数组）：每个格点是{x,y,z}
GridProductClass.prototype.datasetGrid = null; //格点数据集
GridProductClass.prototype.datasetGridInfos = []; //格点产品信息
GridProductClass.prototype.dataCache = null; //数据缓存类，其中的datasetGrid与当前编辑的datasetGrid必须是关联关系
GridProductClass.prototype.dataStack = null; //数据堆栈类

GridProductClass.prototype.addValue = 1.0;  //加一
GridProductClass.prototype.subValue = -1.0; //减一

GridProductClass.prototype.stations = null;
GridProductClass.prototype.stationsForecast = null;
GridProductClass.prototype.layerPlotNationStation = null;   //国家站（填值）图层
GridProductClass.prototype.layerPlotLocalStation = null;    //区域站（填值）图层
GridProductClass.prototype.layerPlotTown = null;    //区域站（填值）图层
GridProductClass.prototype.layerPlotHighStaion = null;    //高山站（填值）图层

GridProductClass.prototype.layerFocusArea = null;   //关注区域图层
GridProductClass.prototype.drawFocusArea = null;    //绘制关注区域
GridProductClass.prototype.layerFocusAreaAnimator = null;   //关注区域动画图层

GridProductClass.prototype.isBrowseMode = false; //是否浏览模式

//GridProductClass.prototype.arrayTag = null; //标记数组，用于标记格点。主要用于魔术棒，自动拾取落区
/*
 * 初始化 
 *  */
GridProductClass.prototype.init = async function(recall){
    var t = this;
    var map = GDYB.Page.curPage.map;
    if(t.layerFocusArea == null)
    {
        t.layerFocusArea = new WeatherMap.Layer.Vector("layerFocusArea");
        t.layerFocusArea.style = {
            strokeColor: "#a548ca",
            strokeWidth: 2.0,
            fillColor: "#ff0000",
            fillOpacity: "0"
        };
        map.addLayers([t.layerFocusArea]);
    }

    if(t.layerFocusAreaAnimator == null)
    {
        t.layerFocusAreaAnimator = new WeatherMap.Layer.AnimatorVector("layerFocusAreaAnimator", {rendererType:"NeonLine"},{
            repeat:true,
            //设置速度为每帧播放的数据
            speed:1,
            //开始时间为0
            startTime:1,
            //每秒渲染10帧
            frameRate:6,
            //结束时间设置为10
            endTime:1
        });
        t.layerFocusAreaAnimator.animator.events.on({"framestart": layerFocusAreaAnimatorFramestart});
        t.layerFocusAreaAnimator.style = {
            strokeWidth: 2.0
        };
        map.addLayers([t.layerFocusAreaAnimator]);
    }

    //获取用户所在地区及部门
    if (t.currentUserName != "" && t.currentUserName != null) {
        var url=Url_Config.gridServiceUrl+"services/AreaService/getDepartByUser";
        $.ajax({
            data: {"para": "{userName:'" + t.currentUserName + "'}"},
            url: url,
            dataType: "json",
            success: function (data) {
                var depart = data;
                t.currentUserDepart = depart;
                t.currentUserArea = depart.departCode;

                //初始化产品类型
                if(t.currentUserDepart.departCode.length == 2) //省台产品
                    GDYB.GDYBPage.productType = "prvn";
                else if(t.currentUserDepart.departCode.length == 4)    //市台产品
                    GDYB.GDYBPage.productType = "cty";
                else if(t.currentUserDepart.departCode.length == 6)    //县台产品
                    GDYB.GDYBPage.productType = "cnty";
                else
                    GDYB.GDYBPage.productType = "prvn";
                t.currentType = GDYB.GDYBPage.productType;
                if(t.currentType == "prvn"){
                    //t.currentVersion = "r"; //r-修订版，p-发布版（首席岗） //由用户选择
                }
                else if(t.currentType == "cty" || t.currentType == "cnty"){
                    t.currentVersion == "p";   //市县就直接是发布版
                }

                recall&&recall();
            },
            error:function(data){
                recall&&recall();
            },
            type: "POST"
        });
    }
    var tempScheme = await getScheme();
    if(tempScheme === "err"){
        console.log("获取初始场默认方案失败!");
    }
    else{
        t.defaultscheme = tempScheme;
    }
    function getDepart(){
        var pro = new Promise(function(resolve, reject){
            var url=Url_Config.gridServiceUrl+"services/AreaService/getDepartByUser";
            $.ajax({
                data: {"para": "{userName:'" + t.currentUserName + "'}"},
                url: url,
                dataType: "json",
                success: function (data) {
                    var depart = data;
                    t.currentUserDepart = depart;
                    t.currentUserArea = depart.departCode;

                    //初始化产品类型
                    if(t.currentUserDepart.departCode.length == 2) //省台产品
                        GDYB.GDYBPage.productType = "prvn";
                    else if(t.currentUserDepart.departCode.length == 4)    //市台产品
                        GDYB.GDYBPage.productType = "cty";
                    else if(t.currentUserDepart.departCode.length == 6)    //县台产品
                        GDYB.GDYBPage.productType = "cnty";
                    else
                        GDYB.GDYBPage.productType = "prvn";
                    t.currentType = GDYB.GDYBPage.productType;
                    if(t.currentType == "prvn"){
                        //t.currentVersion = "r"; //r-修订版，p-发布版（首席岗） //由用户选择
                    }
                    else if(t.currentType == "cty" || t.currentType == "cnty"){
                        t.currentVersion == "p";   //市县就直接是发布版
                    }
                    recall&&recall();
                },
                error:function(data){
                    recall&&recall();
                },
                type: "POST"
            });
        });
    }
    /**
     * @author:wangkun
     * @date:2017-04-06,2017-09-21
     * @param:
     * @return:
     * @description:获取初始场默认方案
     */
    function getScheme(){
        var pro = new Promise(function(resolve, reject){
            var url=Url_Config.gridServiceUrl+"services/GridService/getGridDefaultScheme";
            $.ajax({
                data: {"para": "{}"},
                url: url,
                dataType: "json",
                success: function (data) {
                    resolve(data);
                    //t.defaultscheme = data;
                },
                error:function(data){
                    resolve("err");
                },
                type: "POST"
            });
        });
        return pro;
    }
    //获取初始场默认方案
    // if (true) {
    //     var url=Url_Config.gridServiceUrl+"services/GridService/getGridDefaultScheme";
    //     $.ajax({
    //         data: {"para": "{}"},
    //         url: url,
    //         dataType: "json",
    //         success: function (data) {
    //             t.defaultscheme = data;
    //         },
    //         error:function(data){
    //         },
    //         type: "POST"
    //     });
    // }
    function layerFocusAreaAnimatorFramestart()
    {
        if(t.layerFocusAreaAnimator != null)
            t.layerFocusAreaAnimator.renderer.frameCount++;
    }
};

GridProductClass.prototype.getLastGridInfo = function(func, type){
    var url=Url_Config.gridServiceUrl+"services/GridService/getLastGridInfo";
    $.ajax({
        data: {"para": "{type:'" + type + "'}"},
        url: url,
        dataType: "json",
        success: function (data) {
            var datetime = data.dateTime;
            var elements = data.elements;
            var hourspans = data.hourSpans;
            var levels = data.levels;
            var datetimeSerial = data.datetimeSerial;
            func&&func(datetime, elements, hourspans, levels, datetimeSerial);
        },
        type: "POST"
    });
};

//根据要素获取时效
GridProductClass.prototype.getHourSpanWithElement = function(recall, type, element, maketime, version, datetime){
    var url=Url_Config.gridServiceUrl+"services/GridService/getHourSpanWithElement";
    $.ajax({
        data: {"para": "{element:'"+ element + "',type:'"+ type+ "',maketime:'"+ maketime+ "',version:'"+ version + "',datetime:'"+ datetime + "'}"},
        url: url,
        dataType: "json",
        success: function (data) {
            recall&&recall(data);
        },
	    error: function (e) {
            recall&&recall(e);
        },
        type: "POST"
    });
};

GridProductClass.prototype.displayGridProduct = function(recall, type, level, element, maketime, version, datetime, hourspan, fromModel, elementName, hourspanLast){
    var t = this;

    t.dataStack = new DataStack();

    if(t.datasetGrid != null && t.datasetGrid.isDirty)
    {
        var result = confirm("格点已修改，是否保存？");
//        if (result == true)
//            t.saveGridProduct(t.currentType); //当前类型有误
    }

    //var map = GDYB.GDYBPage.map;
    var map = GDYB.Page.curPage.map;

    //map2_title_div

    var elementLast = GDYB.GridProductClass.currentElement;
    GDYB.GridProductClass.currentElement = element;
    GDYB.GridProductClass.currentElementName = elementName;
    GDYB.GridProductClass.currentLevel = level;
    GDYB.GridProductClass.currentType = type;
    GDYB.GridProductClass.currentMakeTime = maketime;
//    GDYB.GridProductClass.currentVersion = version;
    GDYB.GridProductClass.currentDateTime = datetime;
    GDYB.GridProductClass.currentHourSpan = hourspan;
    GDYB.GridProductClass.currentFromModel = fromModel;


    if(element == "trwt")
    {
        GDYB.GDYBPage.is4Screen = true;
        GDYB.GDYBPage.screen4Map();
        $("#map_title_div").html("");
//        $("#map_title_div").css("padding", "0px");

        t.currentElement = "2t";
        map =GDYB.GDYBPage.map1;
        map.events.register("moveend", map, function(){
            var pMap = this;
            if(t.timeoutid!=null)window.clearTimeout(t.timeoutid);
            t.timeoutid = window.setTimeout(function(){
                t.currentElement = "2t";
                refresh(pMap);
                t.timeoutid = null;
            },100)
        });
        displayGrid();
        //var elementName = getElementName(t.currentElement);
        $("#map1_title_div").html(datetime+ "+" + hourspan +"小时 "+elementName);

        t.currentElement = "RH";
        map =GDYB.GDYBPage.map2;
        map.events.register("moveend", map, function(){
            var pMap = this;
            if(t.timeoutid!=null)window.clearTimeout(t.timeoutid);
            t.timeoutid = window.setTimeout(function(){
                t.currentElement = "rh";
                refresh(pMap);
                t.timeoutid = null;
            },100)
        });
        displayGrid();
        //var elementName = getElementName(t.currentElement);
        $("#map2_title_div").html(datetime+ "+" + hourspan +"小时 "+elementName);

        t.currentElement = "TCC";
        map =GDYB.GDYBPage.map3;
        map.events.register("moveend", map, function(){
            var pMap = this;
            if(t.timeoutid!=null)window.clearTimeout(t.timeoutid);
            t.timeoutid = window.setTimeout(function(){
                t.currentElement = "tcc";
                refresh(pMap);
                t.timeoutid = null;
            },100)
        });
        displayGrid();
        //var elementName = getElementName(t.currentElement);
        $("#map3_title_div").html(datetime+ "+" + hourspan +"小时 "+elementName);

        t.currentElement = "10uv";
        map =GDYB.GDYBPage.map4;
        map.events.register("moveend", map, function(){
            var pMap = this;
            if(t.timeoutid!=null)window.clearTimeout(t.timeoutid);
            t.timeoutid = window.setTimeout(function(){
                t.currentElement = "10uv";
                refresh(pMap);
                t.timeoutid = null;
            },100)
        });
        displayUV();
        //var elementName = getElementName(t.currentElement);
        $("#map4_title_div").html(datetime+ "+" + hourspan +"小时 "+elementName);
    }
//    else if(element == "10uv")
//    {
//        //显示流场动画
//        displayUV();
//        var elementName = getElementName(t.currentElement);
//        $("#map_title_div").html("2012年12月16日20时+3小时 "+elementName);
//
//        //显示风向风速
//        //displayWind();
//    }
    else
    {
        map.events.register("moveend", map, function() {
            var pMap = this;
            if (t.timeoutid != null)window.clearTimeout(t.timeoutid);
            t.timeoutid = window.setTimeout(function () {
                refresh(pMap);
                t.timeoutid = null;
            }, 100)
        });

        displayGrid(recall);
        //如果不是天气现象、空气污染气象条件等级预报要素，切换其他要素时，默认填色平滑显示。因为天气现象、空气污染气象条件等级预报要素会自动设置为不平滑，需要还原。
        if(element != "w" && element != "air" && elementLast != element){
            t.layerFillRangeColor.isSmooth = true;
        }


        //var elementName = getElementName(t.currentElement);
        var typeName = getTypeName(type);
        var levelName = level == null || level == "1000" ?"" : level + "百帕";
        var titleDate = "";
        if(typeof(hourspanLast) == "undefined")
            titleDate = typeName + " " + datetime+ "+" + hourspan +"小时";
        else
        {
            var year = parseInt(datetime.replace(/(\d*)-\d*-\d* \d*:\d*:\d*/,"$1"));
            var month = parseInt(datetime.replace(/\d*-(\d*)-\d* \d*:\d*:\d*/,"$1"));
            var day = parseInt(datetime.replace(/\d*-\d*-(\d*) \d*:\d*:\d*/,"$1"));
            var hour = parseInt(datetime.replace(/\d*-\d*-\d* (\d*):\d*:\d*/,"$1"));
            var minutes = 0;
            var seconds = 0;
            var date1 = new Date();
            date1.setFullYear(year,month - 1,day);
            date1.setHours(hour, minutes, seconds, 0);
            var time1 = date1.getTime();
            time1 += Number(hourspanLast)*60*60*1000; //hourspan小时为单位
            date1.setTime(time1);

            var date2 = new Date();
            date2.setFullYear(year,month - 1,day);
            date2.setHours(hour, minutes, seconds, 0);
            var time2 = date2.getTime();
            time2 += Number(hourspan)*60*60*1000; //hourspan小时为单位
            date2.setTime(time2);

            titleDate = date1.getFullYear() +"年" + (date1.getMonth()+1) + "月" + date1.getDate() + "日";
        }

        var forecastor = "";//"<br>预报员：曾小团，签发：曾小团";
        var unit = "";
        if(type == "prvn" || type == "cty")
        {
            unit = "四川";
            if(t.currentUserDepart != null && t.currentUserDepart.departCode.length > 2){
                unit = t.currentUserDepart.departName;
            }
            var gridInfo = t.getGridInfoFromCache(t.currentType, t.currentElement, t.currentDateTime, t.currentHourSpan);
            if(gridInfo != null && typeof(gridInfo.forecaster) != "undefined" && typeof(gridInfo.issuer) != "undefined")
                forecastor = "<br>预报员：" + gridInfo.forecaster + "，签发：" + gridInfo.issuer;
        }
        else if(type == "ec")
            unit = "欧洲中心";
        else if(type == "gp")
            unit = "GRAPES";
        else if(type == "t639")
            unit = "T639";
        else if(type == "japan")
            unit = "日本";
        else if(type == "bj")
            unit = "中央台";
        else
            unit = type;
        var title = "<p style='line-height: 18px; font-size: 18px;padding: 10px 0px 0px 0px;font-weight: bold;color: red;'>" + unit + levelName + elementName + "预报</p>" + titleDate + forecastor;
        $("#map_title_div").css("display", "block");
        $("#map_title_div").html("<div style=\"line-height: 20px\">" + title +"</div>");
    }

    function getTypeName(type)
    {
        var typeName = type;
        if(type == "BJ")
            typeName = "中央台产品";
        else if(type == "OBJ")
            typeName = "客观产品";
        else if(type == "PRVN")
            typeName = "区级产品";
        else if(type == "CTY")
            typeName = "市级产品";

        return typeName;
    }

    /*
    * 调入模式
    * */
    GridProductClass.prototype.callModel = function(type, maketime) {
        var t = this;
        t.currentFromModel = type;
        var map = GDYB.Page.curPage.map;

        var bCallModel = true;
        var element = t.currentElement;
        var version = "p";
        var datetime = t.currentDateTime;
        var hourspan = t.currentHourSpan;
        var level = t.currentLevel;


        if(t.layerLabel != null)
            t.layerLabel.removeAllFeatures();

        this.getGrid(function(datasetGrid){
            if(datasetGrid != null && datasetGrid.grid.length > 0)
            {
                t.currentGridValuePicked = datasetGrid.noDataValue;

                t.datasetGrid = datasetGrid;
                if(t.layerFillRangeColor != null)
                {
                    if(t.fillColorType == "值域" && !(element == "w" || element == "air"))
                        t.layerFillRangeColor.items = t.getFillColorItemsByRange(element, datasetGrid.dMin, datasetGrid.dMax);

                    t.layerFillRangeColor.isWind = (element == "10uv" || element == "wmax");
                    t.layerFillRangeColor.setDatasetGrid(datasetGrid);
                    GDYB.Legend.update(t.layerFillRangeColor.items);
                    t.updateStationLayer();
                }
                recall&&recall();
            }
            else
            {
                if(!bCallModel) {
                    t.datasetGrid = datasetGrid;
                    t.layerFillRangeColor.setDatasetGrid(null);
                    t.layerLabel.removeAllFeatures();
                    if (t.layerContour != null)
                        t.layerContour.removeAllFeatures();
                    if (t.layerPolygon != null)
                        t.layerPolygon.removeAllFeatures();
                }
            }
            if(datasetGrid == null){
                alertFuc(bCallModel?"调入失败，无对应时效数据":"无数据");
            }
            else if(t.layerLabel.visibility && (element != "10uv" && element != "wmax"))
            {
//                t.addLabel(recall, pMap);
            }
        }, element, type, level, hourspan, maketime, version, datetime, bCallModel);
    };

    /*
     * 调入（要素所有时效）数值模式集合
     * */
    GridProductClass.prototype.callModels = function(recalls, type, maketime, version, date, element, elementName, level, hourspans, strCallModel, version) {
        var t = this;
        t.currentFromModel = type;
        t.getGrids(recalls, element, elementName, type, level, hourspans, maketime, version, date, strCallModel);
    };

    var strategy;
    function displayGrid(recall){
        //先移除已有图层
//        if(t.layerLabel != null)
//            map.removeLayer(t.layerLabel, false);
//        var layerFillRangeColorVisible = true;
//        if(t.layerFillRangeColor != null)
//        {
//            layerFillRangeColorVisible = t.layerFillRangeColor.visibility;
//            map.removeLayer(t.layerFillRangeColor, false);
//        }
        var layerPolygonVisibel = true; //默认显示色斑图
        if(t.layerPolygon != null)
        {
            layerPolygonVisibel = t.layerPolygon.visibility;
            map.removeLayer(t.layerPolygon, false);
        }

        //新建一个策略
        strategy = new WeatherMap.Strategy.GeoText();
        //设置标签的样式
        strategy.style = {
            fontFamily:"Arial",
            fontColor:"#333",
            fontSize:"14px",
            fill: false,
            stroke: false
        };

        //填色图
        if(t.layerFillRangeColor == null)
        {
            t.layerFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer(
                "heatMap",
                {
                    "radius":40,
                    "featureWeight":"value",
                    "featureRadius":"geoRadius"
                }
            );
            //t.layerFillRangeColor.visibility = layerFillRangeColorVisible;
            map.addLayers([t.layerFillRangeColor]);
            t.layerFillRangeColor.isAlwaySmooth = false;
            t.layerFillRangeColor.isSmooth = true;
            //t.layerFillRangeColor.alpha=50;//200

            /*var layerCache=map.getBy("layers","name","LocalTiledCacheLayerWhiteMap");
            var layerFRCIndex=Number($(t.layerFillRangeColor.div).css("z-index"));
            $(layerCache[0].div).css("z-index",layerFRCIndex+1);*/
        }
        //填色风格
        t.layerFillRangeColor.items = t.getFillColorItems(element);
        if(element == "air")
        {
            t.layerFillRangeColor.isSmooth = false;
            t.layerFillRangeColor.isAlwaySmooth = false;
        }
        else if(element == "w")
        {
            t.layerFillRangeColor.isSmooth = false;
            t.layerFillRangeColor.isAlwaySmooth = false;
        }

//        //色斑图
//        t.layerPolygon = new WeatherMap.Layer.Vector("Polygon", {renderers: ["Canvas2"]});
//        t.layerPolygon.visibility = layerPolygonVisibel;
//        map.addLayers([t.layerPolygon]); //先添加，就在下面

        //等值线
        if(t.layerContour == null)
        {
            t.layerContour = new WeatherMap.Layer.Vector("Contour", {renderers: ["Contour"]});
            t.layerContour.renderer.labelField = "dZValue";
            t.layerContour.visibility = false;
            t.layerContour.style = {
                fontFamily:"Arial",
                fontColor:"#333",
                fontSize:"16px",
                fontWeight:"bold",
                //strokeColor: "#ff0000",
                strokeColor: "#c47a55",
                strokeWidth: 1.0
            };
            map.addLayers([t.layerContour]);
        }
        else
        {
            //if(!t.layerContour.visibility)
                t.layerContour.removeAllFeatures();
        }

        //填值
        if(t.layerLabel == null)
        {
            t.layerLabel = new WeatherMap.Layer.Vector("Label",{strategies: [strategy],renderers:["Canvas"]});
            map.addLayers([t.layerLabel]);
        }

        //添加自由曲线图层
        if(t.layerFreePath == null)
        {
            t.layerFreePath = new WeatherMap.Layer.Vector("FreePath");
            t.layerFreePath.style = {
                strokeColor: "#ff0000",
                strokeWidth: 2.0
            };
            map.addLayers([t.layerFreePath]);
            t.drawFreePath = new WeatherMap.Control.DrawFeature(t.layerFreePath, WeatherMap.Handler.PathFree);
            map.addControl(t.drawFreePath);
            t.drawFreePath.events.on({"featureadded": drawFreePathCompleted});
        }

        //添加绘制风向的线图层
        if(t.layerWindDirection == null){
            t.layerWindDirection = new WeatherMap.Layer.Vector("layerWindDirection");
            map.addLayers([t.layerWindDirection]);
            t.drawWindDirection = new WeatherMap.Control.DrawFeature(t.layerWindDirection, WeatherMap.Handler.Path, { multi: false, maxVertices:2});
            t.drawWindDirection.handler.maxVertices = 2; //上面设置不进去，就直接赋值吧
            map.addControl(t.drawWindDirection);
            t.drawWindDirection.events.on({"featureadded": drawWindDirectionCompleted});
        }

        if(t.layerMagic == null)
        {
//            t.layerMagic = new WeatherMap.Layer.Vector("Magic");
//            map.addLayers([t.layerMagic]);

            t.layerMagic = new WeatherMap.Layer.AnimatorVector("NeonLine", {rendererType:"NeonLine"},{
                repeat:true,
                //设置速度为每帧播放的数据
                speed:1,
                //开始时间为0
                startTime:1,
                //每秒渲染10帧
                frameRate:6,
                //结束时间设置为10
                endTime:1
            });
            t.layerMagic.animator.events.on({"framestart": layerMagicFramestart});
            t.layerMagic.style = {
                strokeWidth: 2.0
            };
            map.addLayers([t.layerMagic]);
        }

        if(t.layerMapping == null){
            t.layerMapping = new WeatherMap.Layer.MappingLayer("LayerMapping");
            map.addLayers([t.layerMapping]);
        }

        //创建一个气候区划图层
        if(t.layerClimaticRegion == null)
        {
            t.createClimaticRegionLayer(map);
        }

        //创建气候区划代表站图层
        if(t.layerMarkers == null)
        {
            t.layerMarkers = new WeatherMap.Layer.Markers("layerMarkers");
            map.addLayers([t.layerMarkers]);
        }

        //添加落区图层
        if(t.layerLuoqu == null)
        {
            t.layerLuoqu = new WeatherMap.Layer.Vector("Luoqu");
            t.layerLuoqu.style = {
                strokeColor: "#ff0000",
                strokeWidth: 2.0,
                fillColor: "#ff0000",
                fillOpacity: "0"
            };
            map.addLayers([t.layerLuoqu]);
            //t.drawRectangle = new WeatherMap.Control.DrawFeature(t.layerLuoqu, WeatherMap.Handler.Box);
            t.drawLuoqu = new WeatherMap.Control.DrawFeature(t.layerLuoqu, WeatherMap.Handler.PolygonFree);
            map.addControl(t.drawLuoqu);
            t.drawLuoqu.events.on({"featureadded": drawCompleted});
        }

        //添加落区重心图层
        if(t.layerLuoquCenter == null){
            t.layerLuoquCenter = new WeatherMap.Layer.Vector("layerLuoquCenter");
            t.layerLuoquCenter.style = {
                fillColor: "#00cc00",
                fillOpacity: 0.75,
                pointRadius: 10,
                strokeColor: "#cc0000",
                strokeWidth: 1.0,
                strokeOpacity:1.0,
                stroke: true
            };
            map.addLayers([t.layerLuoquCenter]);
            t.dragFeature = new WeatherMap.Control.DragFeature(t.layerLuoquCenter);
            map.addControl(t.dragFeature);
            //完成拖拽
            t.dragFeature.onComplete = function(feature, pixel){
                if(feature != null && feature.geometry.CLASS_NAME == "WeatherMap.Geometry.Point" && t.currentGridValueDown != t.datasetGrid.noDataValue) {
                    var featureRegion = t.layerLuoqu.features[0];
                    GDYB.GridProductClass.updateGridByIDW(featureRegion.geometry);
                }
            };
            t.dragFeature.activate();

            map.events.register("mouseup", map, function(event){
                if(event.button == 2){ //右键取值
                    //反距离权重
                    if(t.layerLuoquCenter != null && t.layerLuoquCenter.features.length > 0) {
                        var lonlat = this.getLonLatFromPixel(event.xy);
                        var pt = t.datasetGrid.xyToGrid(lonlat.lon, lonlat.lat);
                        var dValue = t.datasetGrid.grid[pt.y][pt.x].z;
                        $("#popup_text").val(dValue);
                        setTimeout(function () {
                            $("#popup_div").css("display", "block")
                        }, 100);
                    }
                }
                else if(event.button == 0){ //左键加中心
                    if(t.currentGridValuePicked != null && t.currentGridValuePicked != t.datasetGrid.noDataValue && t.currentGridValueDown != t.datasetGrid.noDataValue){
                        if(t.layerLuoqu.features.length > 0 && GDYB.GridProductClass.layerLuoquCenter.features.length > 0) {
                            var feature = t.layerLuoqu.features[0];
                            var lonlat = this.getLonLatFromPixel(event.xy);
                            var point = new WeatherMap.Geometry.Point(lonlat.lon, lonlat.lat);
                            var pointVector = new WeatherMap.Feature.Vector(point);
                            pointVector.attributes.z = t.currentGridValuePicked;
                            GDYB.GridProductClass.layerLuoquCenter.addFeatures([pointVector]);
                            GDYB.GridProductClass.dragFeature.activate();

                            var featureRegion = t.layerLuoqu.features[0];
                            GDYB.GridProductClass.updateGridByIDW(featureRegion.geometry);
                        }
                    }
                    t.currentGridValuePicked = null;
                }
            });
        }

        t.addGrid(recall, map);

        if(t.currentElement != "10uv" && t.currentElement != "wmax") {
            //由于存在异步处理，等值线色斑图不应该写在这里，应该在格点请求完成后
//            //显示色斑图
//            if (t.layerPolygon.visibility)
//                t.drawDengzhimian(recall, map);
//
//            //显示等值线
//            if (t.layerContour.visibility)
//                t.addContour(recall, map);
        }

        //清空所有订正痕迹
        if(t.layerLuoqu != null)
            t.layerLuoqu.removeAllFeatures();
        if(t.layerLuoquCenter != null)
            t.layerLuoquCenter.removeAllFeatures();
        if(t.layerMagic != null)
            t.layerMagic.removeAllFeatures();
        if(t.layerFreePath != null)
            t.layerFreePath.removeAllFeatures();

        if(GDYB.GDYBPage.myPanel_Tools != null)
            GDYB.GDYBPage.myPanel_Tools.updateUI(element, elementName);
    }

    function refresh(pMap){
        var layerLabels = pMap.getBy("layers","name","Label");
        var layerLabel = layerLabels[0];
        if(layerLabel != null && layerLabel.visibility && t.currentElement != "10uv" && t.currentElement != "wmax")
        {
            layerLabel.removeAllFeatures();
            var bounds = pMap.getExtent();
            //var boundsStr = "{element:'"+ t.currentElement +"', left:"+bounds.left+", bottom:"+bounds.bottom+",right:"+bounds.right+", top:"+bounds.top+"}";
            var para = "{element:'"+ t.currentElement + "',type:'"+ t.currentType + "',level:'"+ t.currentLevel + "',hourspan:"+ t.currentHourSpan + ",datetime:'"+ t.currentDateTime  +"',left:"+bounds.left+", bottom:"+bounds.bottom+",right:"+bounds.right+", top:"+bounds.top+"}";
            t.addLabel(null, pMap, para);
        }

//        if(t.layerPlot != null)
//            t.addPlot(null, pMap, null);
    }

    function refreshAll(){
        //refresh();
    }

    function drawCompleted(){
        //$("#popup_div").css("display","block");
        //落区保留最新那个
        if(t.layerLuoqu.features.length > 1)
        {
            var count = t.layerLuoqu.features.length;
            for(var i=count-2;i>=0;i--)
                t.layerLuoqu.removeFeatures(t.layerLuoqu.features[i]);
        }
        //落区中心全部删除
        if(t.layerLuoquCenter.features.length > 0)
        {
            var count = t.layerLuoquCenter.features.length;
            for(var i=count-1;i>=0;i--)
                t.layerLuoquCenter.removeFeatures(t.layerLuoquCenter.features[i]);
        }

        //GDYB.GridProductClass.updateGridProduct(GDYB.GridProductClass.currentGridValue); //服务端订正

        if(t.currentElement == "10uv"){
            //GDYB.GridProductClass.drawFreePath.activate(); //注释原因：落区画完后不自动进入风向订正，通过面板底部的风向进行订正。
            //停止落区绘制，线和面的绘制决不能同时进行
            //GDYB.GridProductClass.drawLuoqu.deactivate();
        }
//        else
        {
            if(GDYB.GridProductClass.luoquCorrectType == 0) //保持空间分布趋势
            {
                if(GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null)
                    GDYB.GridProductClass.updateGridBySpatial(GDYB.GridProductClass.currentGridValueDown, GDYB.GridProductClass.currentGridValueUp);  //客户端订正
            }
            else if(GDYB.GridProductClass.luoquCorrectType == 1 &&
                GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null &&
                GDYB.GridProductClass.currentGridValueDown != t.datasetGrid.noDataValue &&
                GDYB.GridProductClass.currentGridValueUp != t.datasetGrid.noDataValue)//如果不保持空间趋势，而是距离反比插值
            {
                //添加落区中心点，作为极大值点
                var feature = t.layerLuoqu.features[0];
                var bounds = feature.geometry.bounds;
                var centerLonLat = {x:bounds.left + (bounds.right - bounds.left)/2, y:bounds.bottom+(bounds.top - bounds.bottom)/2};
                var pointMax = new WeatherMap.Geometry.Point(centerLonLat.x, centerLonLat.y);
                var pointVectorMax = new WeatherMap.Feature.Vector(pointMax);
                pointVectorMax.attributes.z = GDYB.GridProductClass.currentGridValueUp;
                GDYB.GridProductClass.layerLuoquCenter.addFeatures([pointVectorMax]);

                GDYB.GridProductClass.dragFeature.activate();

                //距离反比权重订正
                t.layerLuoquCenter.borderValue = t.currentGridValueDown;
                //if(GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null)
                GDYB.GridProductClass.updateGridByIDW(feature.geometry);

                //停止落区绘制
                GDYB.GridProductClass.drawLuoqu.deactivate();
//                //恢复地图拖拽
//                var map = GDYB.Page.curPage.map;
//                for(var i =0; i < map.events.listeners.mousemove.length; i++) {
//                    var handler = map.events.listeners.mousemove[i];
//                    if (handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag") {
//                        handler.obj.active = true;
//                    }
//                }
            }
        }

        GDYB.GDYBPage.myPanel_Tools.showDivSettingAssignment();
    }

    function drawFreePathCompleted(){
        if(t.layerFreePath.features.length > 0) {
            var feature = t.layerFreePath.features[0];
            var geoLine = feature.geometry;
            t.layerFreePath.removeAllFeatures();
            if(t.action == GDYB.CorrectAction.modifyLuoqu){ //修改落区，局部修改
                var lineLuoqu = GDYB.MagicTool.geoline;
                if(lineLuoqu == null)
                    return;
                var lineCorrect = geoLine;
                //1.获取两线交点
                var intersections = []
                for(var i=1; i<lineLuoqu.components.length; i++){
                    var pt1 = lineLuoqu.components[i-1];
                    var pt2 = lineLuoqu.components[i];
                    for(var j=1; j<lineCorrect.components.length; j++){
                        var pt3 = lineCorrect.components[j-1];
                        var pt4 = lineCorrect.components[j];
                        var intersection = t.calcIntersection(pt1, pt2, pt3, pt4);
                        if(intersection == null)
                            continue;
                        intersections.push({pt:intersection, indexLuoquLine:i, indexCorrectLine:j});
                    }
                }
                //2.构面
                if(intersections.length < 2){
                    alert("与落区不封闭，交点少于2个");
                    return;
                }

                var geoRegion = toGeoRegion(lineLuoqu, lineCorrect, intersections, true);
                if(geoRegion.containsPoint(lineLuoqu.components[intersections[0].indexLuoquLine - 1]) &&
                    geoRegion.containsPoint(lineLuoqu.components[intersections[0].indexLuoquLine])) //多边形错误，落区节点取反了
                {
                    geoRegion = toGeoRegion(lineLuoqu, lineCorrect, intersections, false);
                }

                //6.获取落区上中点坐标，作为待修改区域的中心
                var indexMiddle = Math.floor(intersections[0].indexLuoquLine + (intersections[1].indexLuoquLine - intersections[0].indexLuoquLine)/2); //由于落区线的线段是等距的，故可以这样
                var ptLineMiddle = lineLuoqu.components[indexMiddle];
                var ptGrid = t.datasetGrid.xyToGrid(ptLineMiddle.x, ptLineMiddle.y);
                if(!GDYB.MagicTool.arrayTag[ptGrid.y][ptGrid.x]){
                    for(var i=-1; i<2; i++){
                        for(var j=-1; j<2; j++){
                            if(GDYB.MagicTool.arrayTag[ptGrid.y + i][ptGrid.x+j]){
                                ptGrid = {x:ptGrid.x+j, y:ptGrid.y + i};
                                break;
                            }
                        }
                    }
                }
                var ptMiddle = t.datasetGrid.gridToXY(ptGrid.x, ptGrid.y);
                var valueUp = t.datasetGrid.grid[ptGrid.y][ptGrid.x].z;
                var valueDown = t.currentGridValueDown;

                //如果没有指定下限值，则用最小值
                if(t.currentGridValueDown == t.datasetGrid.noDataValue){
                    var minValue = valueUp;
                    for (var i = 0; i < t.datasetGrid.rows; i++) {
                        for (var j = 0; j < t.datasetGrid.cols; j++){
                            if(GDYB.MagicTool.arrayTag[i][j]){
                                if(t.datasetGrid.grid[i][j].z < minValue)
                                    minValue = t.datasetGrid.grid[i][j].z;
                            }
                        }
                    }
                    valueDown = minValue;
                }

                //7.更新格点（采用一维线性最好）
                t.fillRegionByLinear(t.datasetGrid, geoRegion, ptMiddle, valueDown, valueUp, t.currentElement);
                //8.刷新填色图层
                t.layerFillRangeColor.refresh();
                //9.更新落区
                if(t.currentGridValueDown == t.datasetGrid.noDataValue){
                    GDYB.MagicTool.getLine(t.datasetGrid);
                }
                else {
                    var lonlat = {lon:ptLineMiddle.x, lat:ptLineMiddle.y};
                    GDYB.MagicTool.pick(lonlat);
                }
                t.dataStack.push(t.datasetGrid); //压入堆栈
            }
            else if(t.currentElement == "10uv" || t.currentElement == "wmax") { //风场订正
                if (t.layerLuoqu.features.length > 0){ //按绘制的落区订正
                    var geoRegion = null;
                    geoRegion = t.layerLuoqu.features[0].geometry;
                    GDYB.GridProductClass.updateGridWind(geoLine, geoRegion);
                }
                else if(t.layerMagic.features.length > 0){ //按拾取的落区订正
                    GDYB.MagicTool.updateGridWind(geoLine);
                }
                else{ //按影响半径订正
                    GDYB.GridProductClass.updateGridWind(geoLine, null);
                }

                t.layerFillRangeColor.refresh();
            }
            else{ //画刷
                t.updateGridbyBrush(geoLine, t.currentGridValueDown);
                t.layerFillRangeColor.refresh();
            }
        }

        /*
        * 构面
        * lineLuoqu：落区边界线
        * lineCorrect：订正线
        * intersections：交点
        * bForward：是否（按节点索引）顺向取落区节点
        * */
        function toGeoRegion(lineLuoqu, lineCorrect, intersections, bForward){
            var pts = [];
            //2.1 添加第一个交点
            pts.push(new WeatherMap.Geometry.Point(intersections[0].pt.x, intersections[0].pt.y));
            //2.2 添加落区节点
            if(bForward) {
                for (var i = intersections[0].indexLuoquLine; i < intersections[1].indexLuoquLine; i++) {
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
            }
            else{
                for(var i=intersections[0].indexLuoquLine - 1; i>=0; i--){
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
                for(var i=lineLuoqu.components.length - 1; i>=intersections[1].indexLuoquLine; i--){
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
            }
            //2.3 添加第二个交点
            pts.push(new WeatherMap.Geometry.Point(intersections[1].pt.x, intersections[1].pt.y));
            //2.4 添加订正线节点
            var count = Math.abs(intersections[1].indexCorrectLine - intersections[0].indexCorrectLine);
            var delta = (intersections[1].indexCorrectLine - intersections[0].indexCorrectLine)/count;
            var startIndex = intersections[1].indexCorrectLine; //必须从这个位置开始，否则节点顺序就乱了
            for(var i = 0; i<count; i++){
                var pt = lineCorrect.components[startIndex-i*delta];
                pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
            }

            //3.构面
            var line =  new WeatherMap.Geometry.LinearRing(pts);
            var lines  = [];
            lines.push(line);
            var geoRegion = new WeatherMap.Geometry.Polygon(lines);
            //geoRegion.calculateBounds(); //这个怎么不靠谱呢
            //4.计算Bounds
            var bounds = new WeatherMap.Bounds(180, 90, -180, -90);
            for(var i=0; i<pts.length; i++){
                var pt = pts[i];
                if(pt.x < bounds.left)
                    bounds.left = pt.x;
                if(pt.x > bounds.right)
                    bounds.right = pt.x;
                if(pt.y < bounds.bottom)
                    bounds.bottom = pt.y;
                if(pt.y > bounds.top)
                    bounds.top = pt.y;
            }
            geoRegion.bounds = bounds;
            return geoRegion;
        }
    }

    function drawWindDirectionCompleted(){
        if(t.layerWindDirection.features.length > 0) {
            var feature = t.layerWindDirection.features[0];
            var geoLine = feature.geometry;
            t.layerWindDirection.removeAllFeatures();

            GDYB.GridProductClass.updateGridWind(geoLine, null);
            GDYB.GridProductClass.layerFillRangeColor.refresh();
        }
    }

    //显示风向风速
    function displayUV()
    {
        //初始化动画矢量图层
        t.layerStreamLine = new WeatherMap.Layer.AnimatorVector("StreamLine", {rendererType:"StreamLine"},{
            repeat:true,
            //设置速度为每帧播放的数据
            speed:1,
            //开始时间为0
            startTime:1,
            //每秒渲染10帧
            frameRate:12,
            //结束时间设置为10
            endTime:1
        });
        t.layerStreamLine.animator.events.on({"framestart": layerStreamLineFramestart});
        var layerGrid = new WeatherMap.Layer.Vector("LayerGrid", {renderers: ["Canvas"]});
        map.addLayers([layerGrid,t.layerStreamLine]);

        //流线
        addStreamLine(t.layerStreamLine);

        //流场
        var maxValue = 6.0;
        var colorStart = [35, 40, 146];
        var colorEnd = [25, 130, 66];
        var cols = 37;
        var rows = 24;
        var dx = 0.25;
        var dy = 0.25;
        addGrid(layerGrid, uv, maxValue, rows, cols, dx, dy, colorStart, colorEnd);

        t.layerStreamLine.animator.start();
    }

//    //显示风向风速
//    function displayWind()
//    {
//        //先移除已有图层
//        if(t.layerLabel != null)
//            map.removeLayer(t.layerLabel, false);
//        if(t.layerPolygon != null)
//            map.removeLayer(t.layerPolygon, false);
//
//        if(t.layerPlot == null)
//        {
//            t.layerPlot = new WeatherMap.Layer.Vector("Plot");
//            map.addLayer(t.layerPlot);
//        }
//
//        t.addPlot(null, map, null);
//    }

    function layerMagicFramestart()
    {
        if(t.layerMagic != null)
            t.layerMagic.renderer.frameCount++;
    }

    function layerStreamLineFramestart()
    {
        if(t.layerStreamLine != null)
            t.layerStreamLine.renderer.frameCount++;
    }

    //添加数据
    function addStreamLine(animatorVector)
    {
        var style =
        {
            fillColor: "#cc0000",
            pointRadius: 3,
            strokeColor: "#7AFF168A8",
            strokeWidth: 1
        };
        var lineFeatures = [];
        for(var i = 0,len = lines.length;i<len;i++)
        {
            var line = lines[i];
            var pts = line[1]; //全部节点
            var arrP = []; //定义节点数组
            for(var j=0;j<pts.length;j++)
            {
                var point = new WeatherMap.Geometry.Point(pts[j][0],pts[j][1]); //节点
                arrP.push(point); //节点数组赋值
            }
            var lineString = new WeatherMap.Geometry.LineString(arrP); //通过点串构造几何对象线串类LineString

            var lineFeature = new WeatherMap.Feature.Vector(lineString,{
                FEATUREID:line[0],
                TIME:1
            },style); //实例化Feature，参数：geometry，attributes，style
            lineFeatures.push(lineFeature);
        }
        animatorVector.addFeatures(lineFeatures); //需要添加的要素数组。（也可以是单个feature）
    }

    //Point转Rectangle
    function addGrid(layerGrid, grid, maxValue, rows, cols, dx, dy, colorStart, colorEnd){
        var features = [];
        var count = 4;
        var w = dx / count;
        var h = dx / count;
        var rx = w / 2.0;
        var ry = h / 2.0;
        for (var i = 0; i < grid.length; i++) {
            var pt = grid[i][4];
            {
                for(var j=0; j<count; j++)
                {
                    if(grid[i][2] == cols - 1 && j > 0) //最后一列
                        break;

                    var ptXTemp = [pt[0]+w*j, pt[1]];

                    var vX1;
                    if(grid[i][2] == cols - 1)
                        vX1 = (count - j)/count*grid[i][3] + j/count*grid[i - 1][3];
                    else
                        vX1 = (count - j)/count*grid[i][3] + j/count*grid[i + 1][3];

                    if(grid[i][1] < rows - 1) {
                        var vX2;
                        if(grid[i][2] == cols - 1)
                            vX2 = (count - j)/count*grid[i + cols][3] + j/count*grid[i + cols - 1][3];
                        else
                            vX2 = (count - j)/count*grid[i + cols][3] + j/count*grid[i + cols + 1][3];

                        for (var k = 0; k < count; k++) {
                            var ptYTemp = [ptXTemp[0], ptXTemp[1] - h * k];
                            var v = (count - k) / count * vX1 + k / count * vX2;
                            dv = v/maxValue;
                            var color = "rgba(" +Math.round(colorStart[0] - dv*(colorStart[0] - colorEnd[0])) + "," + Math.round(colorStart[1] + dv*(colorEnd[1] - colorStart[1])) +"," + Math.round(colorStart[2] - dv*(colorStart[2]-colorEnd[2])) +",255)";
                            //contextMap.fillStyle = color;
                            var style = {
                                fillColor: "#cc0000",
                                fillOpacity: 0.75,
                                //fillOpacity: 1.0,
                                pointRadius: 3,
                                strokeColor: "#000000",
                                strokeWidth: 0.15,
                                strokeOpacity:0.75,
                                stroke: true
                            };
                            style.fillColor = color;
                            style.strokeColor = color;
                            var rectangle = new WeatherMap.Geometry.Rectangle(ptYTemp[0] - rx, ptYTemp[1] - ry, w, h);
                            var rectFeature = new WeatherMap.Feature.Vector(rectangle, null, style); //实例化Feature，参数：geometry，attributes，style
                            features.push(rectFeature);
                        }
                    }
                }
            }
        }
        layerGrid.addFeatures(features);
    }
};
GridProductClass.prototype.displayGridProduct10to30JP = function(recall, type, level, element, maketime, version, datetime, hourspan, fromModel, elementName, hourspanLast,hosData){
    var t = this;
    t.dataStack = new DataStack();
    var map = GDYB.Page.curPage.map;
    var elementLast = GDYB.GridProductClass.currentElement;
    GDYB.GridProductClass.currentElement = element;
    GDYB.GridProductClass.currentElementName = elementName;
    GDYB.GridProductClass.currentLevel = level;
    GDYB.GridProductClass.currentType = type;
    GDYB.GridProductClass.currentMakeTime = maketime;
    GDYB.GridProductClass.currentDateTime = datetime;
    GDYB.GridProductClass.currentHourSpan = hourspan;
    GDYB.GridProductClass.currentFromModel = fromModel;
    map.events.register("moveend", map, function() {
        var pMap = this;
        if (t.timeoutid != null)window.clearTimeout(t.timeoutid);
        t.timeoutid = window.setTimeout(function () {
            refresh(pMap);
            t.timeoutid = null;
        }, 100)
    });

    displayGridJuPing(recall,hosData);

    var typeName = getTypeName(type);
    var levelName = level == null || level == "1000" ?"" : level + "百帕";
    var titleDate = "";
    if(typeof(hourspanLast) == "undefined")
        titleDate = typeName + " " + datetime+ "+" + hourspan +"小时";
    else
    {
        var year = parseInt(datetime.replace(/(\d*)-\d*-\d* \d*:\d*:\d*/,"$1"));
        var month = parseInt(datetime.replace(/\d*-(\d*)-\d* \d*:\d*:\d*/,"$1"));
        var day = parseInt(datetime.replace(/\d*-\d*-(\d*) \d*:\d*:\d*/,"$1"));
        var hour = parseInt(datetime.replace(/\d*-\d*-\d* (\d*):\d*:\d*/,"$1"));
        var minutes = 0;
        var seconds = 0;

        var date1 = new Date();
        date1.setFullYear(year,month - 1,day);
        date1.setHours(hour, minutes, seconds, 0);
        var time1 = date1.getTime();
        time1 += Number(hourspanLast)*60*60*1000; //hourspan小时为单位
        date1.setTime(time1);

        var date2 = new Date();
        date2.setFullYear(year,month - 1,day);
        date2.setHours(hour, minutes, seconds, 0);
        var time2 = date2.getTime();
        time2 += Number(hourspan)*60*60*1000; //hourspan小时为单位
        date2.setTime(time2);

        titleDate = date1.getFullYear() +"年" + (date1.getMonth()+1) + "月" + date1.getDate() + "日";
    }

    var forecastor = "";
    var unit = "";
    if(type == "prvn" || type == "cty")
    {
        unit = "四川";
        if(t.currentUserDepart != null && t.currentUserDepart.departCode.length > 2){
            unit = t.currentUserDepart.departName;
        }
        var gridInfo = t.getGridInfoFromCache(t.currentType, t.currentElement, t.currentDateTime, t.currentHourSpan);
        if(gridInfo != null && typeof(gridInfo.forecaster) != "undefined" && typeof(gridInfo.issuer) != "undefined")
            forecastor = "<br>预报员：" + gridInfo.forecaster + "，签发：" + gridInfo.issuer;
    }
    else
        unit = type;
    var title = "<p style='line-height: 18px; font-size: 18px;padding: 10px 0px 0px 0px;font-weight: bold;color: red;'>" + unit + levelName + elementName + "预报</p>" + titleDate + forecastor;
    $("#map_title_div").css("display", "block");
    $("#map_title_div").html("<div style=\"line-height: 20px\">" + title +"</div>");

    function getTypeName(type)
    {
        var typeName = type;
        if(type == "BJ")
            typeName = "中央台产品";
        else if(type == "OBJ")
            typeName = "客观产品";
        else if(type == "PRVN")
            typeName = "区级产品";
        else if(type == "CTY")
            typeName = "市级产品";

        return typeName;
    }

    /*
     * 调入模式
     * */
    GridProductClass.prototype.callModel = function(type, maketime) {
        var t = this;
        t.currentFromModel = type;
        var map = GDYB.Page.curPage.map;

        var bCallModel = true;
        var element = t.currentElement;
        var version = "p";
        var datetime = t.currentDateTime;
        var hourspan = t.currentHourSpan;
        var level = t.currentLevel;


        if(t.layerLabel != null)
            t.layerLabel.removeAllFeatures();

        this.getGrid(function(datasetGrid){
            if(datasetGrid != null && datasetGrid.grid.length > 0)
            {
                t.currentGridValuePicked = datasetGrid.noDataValue;

                t.datasetGrid = datasetGrid;
                if(t.layerFillRangeColor != null)
                {
                    if(t.fillColorType == "值域" && !(element == "w" || element == "air"))
                        t.layerFillRangeColor.items = t.getFillColorItemsByRange(element, datasetGrid.dMin, datasetGrid.dMax);

                    t.layerFillRangeColor.isWind = (element == "10uv" || element == "wmax");
                    t.layerFillRangeColor.setDatasetGrid(datasetGrid);
                    GDYB.Legend.update(t.layerFillRangeColor.items);
                    t.updateStationLayer();
                }
                recall&&recall();
            }
            else
            {
                if(!bCallModel) {
                    t.datasetGrid = datasetGrid;
                    t.layerFillRangeColor.setDatasetGrid(null);
                    t.layerLabel.removeAllFeatures();
                    if (t.layerContour != null)
                        t.layerContour.removeAllFeatures();
                    if (t.layerPolygon != null)
                        t.layerPolygon.removeAllFeatures();
                }
            }
            if(datasetGrid == null){
                alertFuc(bCallModel?"调入失败，无对应时效数据":"无数据");
            }
            else if(t.layerLabel.visibility && (element != "10uv" && element != "wmax"))
            {
//                t.addLabel(recall, pMap);
            }
        }, element, type, level, hourspan, maketime, version, datetime, bCallModel);
    };

    /*
     * 调入（要素所有时效）数值模式集合
     * */
    GridProductClass.prototype.callModels = function(recalls, type, maketime, version, date, element, elementName, level, hourspans, strCallModel, version) {
        var t = this;
        t.currentFromModel = type;
        t.getGrids(recalls, element, elementName, type, level, hourspans, maketime, version, date, strCallModel);
    };

    var strategy;
    function displayGridJuPing(recall,hosData){
        var layerPolygonVisibel = true; //默认显示色斑图
        if(t.layerPolygon != null)
        {
            layerPolygonVisibel = t.layerPolygon.visibility;
            map.removeLayer(t.layerPolygon, false);
        }

        //新建一个策略
        strategy = new WeatherMap.Strategy.GeoText();
        //设置标签的样式
        strategy.style = {
            fontFamily:"Arial",
            fontColor:"#333",
            fontSize:"14px",
            fill: false,
            stroke: false
        };

        //填色图
        if(t.layerFillRangeColor == null)
        {
            t.layerFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer(
                "heatMap",
                {
                    "radius":40,
                    "featureWeight":"value",
                    "featureRadius":"geoRadius"
                }
            );
            //t.layerFillRangeColor.visibility = layerFillRangeColorVisible;
            map.addLayers([t.layerFillRangeColor]);
            t.layerFillRangeColor.isAlwaySmooth = false;
            t.layerFillRangeColor.isSmooth = true;
        }
        //填色风格
        t.layerFillRangeColor.items = t.getFillColorItems(element+"jp");
        if(element == "air")
        {
            t.layerFillRangeColor.isSmooth = false;
            t.layerFillRangeColor.isAlwaySmooth = false;
        }
        else if(element == "w")
        {
            t.layerFillRangeColor.isSmooth = false;
            t.layerFillRangeColor.isAlwaySmooth = false;
        }

        //等值线
        if(t.layerContour == null)
        {
            t.layerContour = new WeatherMap.Layer.Vector("Contour", {renderers: ["Contour"]});
            t.layerContour.renderer.labelField = "dZValue";
            t.layerContour.visibility = false;
            t.layerContour.style = {
                fontFamily:"Arial",
                fontColor:"#333",
                fontSize:"16px",
                fontWeight:"bold",
                //strokeColor: "#ff0000",
                strokeColor: "#c47a55",
                strokeWidth: 1.0
            };
            map.addLayers([t.layerContour]);
        }
        else
        {
            //if(!t.layerContour.visibility)
            t.layerContour.removeAllFeatures();
        }

        //填值
        if(t.layerLabel == null)
        {
            t.layerLabel = new WeatherMap.Layer.Vector("Label",{strategies: [strategy],renderers:["Canvas"]});
            map.addLayers([t.layerLabel]);
        }

        //添加自由曲线图层
        if(t.layerFreePath == null)
        {
            t.layerFreePath = new WeatherMap.Layer.Vector("FreePath");
            t.layerFreePath.style = {
                strokeColor: "#ff0000",
                strokeWidth: 2.0
            };
            map.addLayers([t.layerFreePath]);
            t.drawFreePath = new WeatherMap.Control.DrawFeature(t.layerFreePath, WeatherMap.Handler.PathFree);
            map.addControl(t.drawFreePath);
            t.drawFreePath.events.on({"featureadded": drawFreePathCompleted});
        }

        if(t.layerMagic == null)
        {
//            t.layerMagic = new WeatherMap.Layer.Vector("Magic");
//            map.addLayers([t.layerMagic]);

            t.layerMagic = new WeatherMap.Layer.AnimatorVector("NeonLine", {rendererType:"NeonLine"},{
                repeat:true,
                //设置速度为每帧播放的数据
                speed:1,
                //开始时间为0
                startTime:1,
                //每秒渲染10帧
                frameRate:6,
                //结束时间设置为10
                endTime:1
            });
            t.layerMagic.animator.events.on({"framestart": layerMagicFramestart});
            t.layerMagic.style = {
                strokeWidth: 2.0
            };
            map.addLayers([t.layerMagic]);
        }

        if(t.layerMapping == null){
            t.layerMapping = new WeatherMap.Layer.MappingLayer("LayerMapping");
            map.addLayers([t.layerMapping]);
        }

        //创建一个气候区划图层
        if(t.layerClimaticRegion == null)
        {
            t.createClimaticRegionLayer(map);
        }

        //创建气候区划代表站图层
        if(t.layerMarkers == null)
        {
            t.layerMarkers = new WeatherMap.Layer.Markers("layerMarkers");
            map.addLayers([t.layerMarkers]);
        }

        //添加落区图层
        if(t.layerLuoqu == null)
        {
            t.layerLuoqu = new WeatherMap.Layer.Vector("Luoqu");
            t.layerLuoqu.style = {
                strokeColor: "#ff0000",
                strokeWidth: 2.0,
                fillColor: "#ff0000",
                fillOpacity: "0"
            };
            map.addLayers([t.layerLuoqu]);
            //t.drawRectangle = new WeatherMap.Control.DrawFeature(t.layerLuoqu, WeatherMap.Handler.Box);
            t.drawLuoqu = new WeatherMap.Control.DrawFeature(t.layerLuoqu, WeatherMap.Handler.PolygonFree);
            map.addControl(t.drawLuoqu);
            t.drawLuoqu.events.on({"featureadded": drawCompleted});
        }

        //添加落区重心图层
        if(t.layerLuoquCenter == null){
            t.layerLuoquCenter = new WeatherMap.Layer.Vector("layerLuoquCenter");
            t.layerLuoquCenter.style = {
                fillColor: "#00cc00",
                fillOpacity: 0.75,
                pointRadius: 10,
                strokeColor: "#cc0000",
                strokeWidth: 1.0,
                strokeOpacity:1.0,
                stroke: true
            };
            map.addLayers([t.layerLuoquCenter]);
            t.dragFeature = new WeatherMap.Control.DragFeature(t.layerLuoquCenter);
            map.addControl(t.dragFeature);
            //完成拖拽
            t.dragFeature.onComplete = function(feature, pixel){
                if(feature != null && feature.geometry.CLASS_NAME == "WeatherMap.Geometry.Point" && t.currentGridValueDown != t.datasetGrid.noDataValue) {
                    var featureRegion = t.layerLuoqu.features[0];
                    GDYB.GridProductClass.updateGridByIDW(featureRegion.geometry);
                }
            };
            t.dragFeature.activate();

            map.events.register("mouseup", map, function(event){
                if(event.button == 2){ //右键取值
                    //反距离权重
                    if(t.layerLuoquCenter != null && t.layerLuoquCenter.features.length > 0) {
                        var lonlat = this.getLonLatFromPixel(event.xy);
                        var pt = t.datasetGrid.xyToGrid(lonlat.lon, lonlat.lat);
                        var dValue = t.datasetGrid.grid[pt.y][pt.x].z;
                        $("#popup_text").val(dValue);
                        setTimeout(function () {
                            $("#popup_div").css("display", "block")
                        }, 100);
                    }
                }
                else if(event.button == 0){ //左键加中心
                    if(t.currentGridValuePicked != null && t.currentGridValuePicked != t.datasetGrid.noDataValue && t.currentGridValueDown != t.datasetGrid.noDataValue){
                        if(t.layerLuoqu.features.length > 0 && GDYB.GridProductClass.layerLuoquCenter.features.length > 0) {
                            var feature = t.layerLuoqu.features[0];
                            var lonlat = this.getLonLatFromPixel(event.xy);
                            var point = new WeatherMap.Geometry.Point(lonlat.lon, lonlat.lat);
                            var pointVector = new WeatherMap.Feature.Vector(point);
                            pointVector.attributes.z = t.currentGridValuePicked;
                            GDYB.GridProductClass.layerLuoquCenter.addFeatures([pointVector]);
                            GDYB.GridProductClass.dragFeature.activate();

                            var featureRegion = t.layerLuoqu.features[0];
                            GDYB.GridProductClass.updateGridByIDW(featureRegion.geometry);
                        }
                    }
                    t.currentGridValuePicked = null;
                }
            });
        }

        t.addGrid10to30JP(recall,hosData);

        if(t.currentElement != "10uv" && t.currentElement != "wmax") {
            //由于存在异步处理，等值线色斑图不应该写在这里，应该在格点请求完成后
//            //显示色斑图
//            if (t.layerPolygon.visibility)
//                t.drawDengzhimian(recall, map);
//
//            //显示等值线
//            if (t.layerContour.visibility)
//                t.addContour(recall, map);
        }

        //清空所有订正痕迹
        if(t.layerLuoqu != null)
            t.layerLuoqu.removeAllFeatures();
        if(t.layerLuoquCenter != null)
            t.layerLuoquCenter.removeAllFeatures();
        if(t.layerMagic != null)
            t.layerMagic.removeAllFeatures();
        if(t.layerFreePath != null)
            t.layerFreePath.removeAllFeatures();

        if(GDYB.GDYBPage.myPanel_Tools != null)
            GDYB.GDYBPage.myPanel_Tools.updateUI(element, elementName);
    }

    function refresh(pMap){
        var layerLabels = pMap.getBy("layers","name","Label");
        var layerLabel = layerLabels[0];
        if(layerLabel != null && layerLabel.visibility && t.currentElement != "10uv" && t.currentElement != "wmax")
        {
            layerLabel.removeAllFeatures();
            var bounds = pMap.getExtent();
            //var boundsStr = "{element:'"+ t.currentElement +"', left:"+bounds.left+", bottom:"+bounds.bottom+",right:"+bounds.right+", top:"+bounds.top+"}";
            var para = "{element:'"+ t.currentElement + "',type:'"+ t.currentType + "',level:'"+ t.currentLevel + "',hourspan:"+ t.currentHourSpan + ",datetime:'"+ t.currentDateTime  +"',left:"+bounds.left+", bottom:"+bounds.bottom+",right:"+bounds.right+", top:"+bounds.top+"}";
            t.addLabel(null, pMap, para);
        }

//        if(t.layerPlot != null)
//            t.addPlot(null, pMap, null);
    }

    function drawCompleted(){
        //$("#popup_div").css("display","block");
        //落区保留最新那个
        if(t.layerLuoqu.features.length > 1)
        {
            var count = t.layerLuoqu.features.length;
            for(var i=count-2;i>=0;i--)
                t.layerLuoqu.removeFeatures(t.layerLuoqu.features[i]);
        }
        //落区中心全部删除
        if(t.layerLuoquCenter.features.length > 0)
        {
            var count = t.layerLuoquCenter.features.length;
            for(var i=count-1;i>=0;i--)
                t.layerLuoquCenter.removeFeatures(t.layerLuoquCenter.features[i]);
        }

        //GDYB.GridProductClass.updateGridProduct(GDYB.GridProductClass.currentGridValue); //服务端订正

        if(t.currentElement == "10uv"){
            //GDYB.GridProductClass.drawFreePath.activate(); //注释原因：落区画完后不自动进入风向订正，通过面板底部的风向进行订正。
            //停止落区绘制，线和面的绘制决不能同时进行
            //GDYB.GridProductClass.drawLuoqu.deactivate();
        }
//        else
        {
            if(GDYB.GridProductClass.luoquCorrectType == 0) //保持空间分布趋势
            {
                if(GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null)
                    GDYB.GridProductClass.updateGridBySpatial(GDYB.GridProductClass.currentGridValueDown, GDYB.GridProductClass.currentGridValueUp);  //客户端订正
            }
            else if(GDYB.GridProductClass.luoquCorrectType == 1 &&
                GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null &&
                GDYB.GridProductClass.currentGridValueDown != t.datasetGrid.noDataValue &&
                GDYB.GridProductClass.currentGridValueUp != t.datasetGrid.noDataValue)//如果不保持空间趋势，而是距离反比插值
            {
                //添加落区中心点，作为极大值点
                var feature = t.layerLuoqu.features[0];
                var bounds = feature.geometry.bounds;
                var centerLonLat = {x:bounds.left + (bounds.right - bounds.left)/2, y:bounds.bottom+(bounds.top - bounds.bottom)/2};
                var pointMax = new WeatherMap.Geometry.Point(centerLonLat.x, centerLonLat.y);
                var pointVectorMax = new WeatherMap.Feature.Vector(pointMax);
                pointVectorMax.attributes.z = GDYB.GridProductClass.currentGridValueUp;
                GDYB.GridProductClass.layerLuoquCenter.addFeatures([pointVectorMax]);

                GDYB.GridProductClass.dragFeature.activate();

                //距离反比权重订正
                t.layerLuoquCenter.borderValue = t.currentGridValueDown;
                //if(GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null)
                GDYB.GridProductClass.updateGridByIDW(feature.geometry);

                //停止落区绘制
                GDYB.GridProductClass.drawLuoqu.deactivate();
//                //恢复地图拖拽
//                var map = GDYB.Page.curPage.map;
//                for(var i =0; i < map.events.listeners.mousemove.length; i++) {
//                    var handler = map.events.listeners.mousemove[i];
//                    if (handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag") {
//                        handler.obj.active = true;
//                    }
//                }
            }
        }

        GDYB.GDYBPage.myPanel_Tools.showDivSettingAssignment();
    }

    function drawFreePathCompleted(){
        if(t.layerFreePath.features.length > 0) {
            var feature = t.layerFreePath.features[0];
            var geoLine = feature.geometry;
            t.layerFreePath.removeAllFeatures();
            if(t.action == GDYB.CorrectAction.modifyLuoqu){ //修改落区，局部修改
                var lineLuoqu = GDYB.MagicTool.geoline;
                if(lineLuoqu == null)
                    return;
                var lineCorrect = geoLine;
                //1.获取两线交点
                var intersections = []
                for(var i=1; i<lineLuoqu.components.length; i++){
                    var pt1 = lineLuoqu.components[i-1];
                    var pt2 = lineLuoqu.components[i];
                    for(var j=1; j<lineCorrect.components.length; j++){
                        var pt3 = lineCorrect.components[j-1];
                        var pt4 = lineCorrect.components[j];
                        var intersection = t.calcIntersection(pt1, pt2, pt3, pt4);
                        if(intersection == null)
                            continue;
                        intersections.push({pt:intersection, indexLuoquLine:i, indexCorrectLine:j});
                    }
                }
                //2.构面
                if(intersections.length < 2){
                    alert("与落区不封闭，交点少于2个");
                    return;
                }

                var geoRegion = toGeoRegion(lineLuoqu, lineCorrect, intersections, true);
                if(geoRegion.containsPoint(lineLuoqu.components[intersections[0].indexLuoquLine - 1]) &&
                    geoRegion.containsPoint(lineLuoqu.components[intersections[0].indexLuoquLine])) //多边形错误，落区节点取反了
                {
                    geoRegion = toGeoRegion(lineLuoqu, lineCorrect, intersections, false);
                }

                //6.获取落区上中点坐标，作为待修改区域的中心
                var indexMiddle = Math.floor(intersections[0].indexLuoquLine + (intersections[1].indexLuoquLine - intersections[0].indexLuoquLine)/2); //由于落区线的线段是等距的，故可以这样
                var ptLineMiddle = lineLuoqu.components[indexMiddle];
                var ptGrid = t.datasetGrid.xyToGrid(ptLineMiddle.x, ptLineMiddle.y);
                if(!GDYB.MagicTool.arrayTag[ptGrid.y][ptGrid.x]){
                    for(var i=-1; i<2; i++){
                        for(var j=-1; j<2; j++){
                            if(GDYB.MagicTool.arrayTag[ptGrid.y + i][ptGrid.x+j]){
                                ptGrid = {x:ptGrid.x+j, y:ptGrid.y + i};
                                break;
                            }
                        }
                    }
                }
                var ptMiddle = t.datasetGrid.gridToXY(ptGrid.x, ptGrid.y);
                var valueUp = t.datasetGrid.grid[ptGrid.y][ptGrid.x].z;
                var valueDown = t.currentGridValueDown;

                //如果没有指定下限值，则用最小值
                if(t.currentGridValueDown == t.datasetGrid.noDataValue){
                    var minValue = valueUp;
                    for (var i = 0; i < t.datasetGrid.rows; i++) {
                        for (var j = 0; j < t.datasetGrid.cols; j++){
                            if(GDYB.MagicTool.arrayTag[i][j]){
                                if(t.datasetGrid.grid[i][j].z < minValue)
                                    minValue = t.datasetGrid.grid[i][j].z;
                            }
                        }
                    }
                    valueDown = minValue;
                }

                //7.更新格点（采用一维线性最好）
                t.fillRegionByLinear(t.datasetGrid, geoRegion, ptMiddle, valueDown, valueUp, t.currentElement);
                //8.刷新填色图层
                t.layerFillRangeColor.refresh();
                //9.更新落区
                if(t.currentGridValueDown == t.datasetGrid.noDataValue){
                    GDYB.MagicTool.getLine(t.datasetGrid);
                }
                else {
                    var lonlat = {lon:ptLineMiddle.x, lat:ptLineMiddle.y};
                    GDYB.MagicTool.pick(lonlat);
                }
                t.dataStack.push(t.datasetGrid); //压入堆栈
            }
            else if(t.currentElement == "10uv" || t.currentElement == "wmax") { //风场订正
                if (t.layerLuoqu.features.length > 0){ //按绘制的落区订正
                    var geoRegion = null;
                    geoRegion = t.layerLuoqu.features[0].geometry;
                    GDYB.GridProductClass.updateGridWind(geoLine, geoRegion);
                }
                else if(t.layerMagic.features.length > 0){ //按拾取的落区订正
                    GDYB.MagicTool.updateGridWind(geoLine);
                }
                else{ //按影响半径订正
                    GDYB.GridProductClass.updateGridWind(geoLine, null);
                }

                t.layerFillRangeColor.refresh();
            }
            else{ //画刷
                t.updateGridbyBrush(geoLine, t.currentGridValueDown);
                t.layerFillRangeColor.refresh();
            }
        }

        /*
         * 构面
         * lineLuoqu：落区边界线
         * lineCorrect：订正线
         * intersections：交点
         * bForward：是否（按节点索引）顺向取落区节点
         * */
        function toGeoRegion(lineLuoqu, lineCorrect, intersections, bForward){
            var pts = [];
            //2.1 添加第一个交点
            pts.push(new WeatherMap.Geometry.Point(intersections[0].pt.x, intersections[0].pt.y));
            //2.2 添加落区节点
            if(bForward) {
                for (var i = intersections[0].indexLuoquLine; i < intersections[1].indexLuoquLine; i++) {
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
            }
            else{
                for(var i=intersections[0].indexLuoquLine - 1; i>=0; i--){
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
                for(var i=lineLuoqu.components.length - 1; i>=intersections[1].indexLuoquLine; i--){
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
            }
            //2.3 添加第二个交点
            pts.push(new WeatherMap.Geometry.Point(intersections[1].pt.x, intersections[1].pt.y));
            //2.4 添加订正线节点
            var count = Math.abs(intersections[1].indexCorrectLine - intersections[0].indexCorrectLine);
            var delta = (intersections[1].indexCorrectLine - intersections[0].indexCorrectLine)/count;
            var startIndex = intersections[1].indexCorrectLine; //必须从这个位置开始，否则节点顺序就乱了
            for(var i = 0; i<count; i++){
                var pt = lineCorrect.components[startIndex-i*delta];
                pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
            }

            //3.构面
            var line =  new WeatherMap.Geometry.LinearRing(pts);
            var lines  = [];
            lines.push(line);
            var geoRegion = new WeatherMap.Geometry.Polygon(lines);
            //geoRegion.calculateBounds(); //这个怎么不靠谱呢
            //4.计算Bounds
            var bounds = new WeatherMap.Bounds(180, 90, -180, -90);
            for(var i=0; i<pts.length; i++){
                var pt = pts[i];
                if(pt.x < bounds.left)
                    bounds.left = pt.x;
                if(pt.x > bounds.right)
                    bounds.right = pt.x;
                if(pt.y < bounds.bottom)
                    bounds.bottom = pt.y;
                if(pt.y > bounds.top)
                    bounds.top = pt.y;
            }
            geoRegion.bounds = bounds;
            return geoRegion;
        }
    }

    function layerMagicFramestart()
    {
        if(t.layerMagic != null)
            t.layerMagic.renderer.frameCount++;
    }

    function layerStreamLineFramestart()
    {
        if(t.layerStreamLine != null)
            t.layerStreamLine.renderer.frameCount++;
    }

    //添加数据
    function addStreamLine(animatorVector)
    {
        var style =
        {
            fillColor: "#cc0000",
            pointRadius: 3,
            strokeColor: "#7AFF168A8",
            strokeWidth: 1
        };
        var lineFeatures = [];
        for(var i = 0,len = lines.length;i<len;i++)
        {
            var line = lines[i];
            var pts = line[1]; //全部节点
            var arrP = []; //定义节点数组
            for(var j=0;j<pts.length;j++)
            {
                var point = new WeatherMap.Geometry.Point(pts[j][0],pts[j][1]); //节点
                arrP.push(point); //节点数组赋值
            }
            var lineString = new WeatherMap.Geometry.LineString(arrP); //通过点串构造几何对象线串类LineString

            var lineFeature = new WeatherMap.Feature.Vector(lineString,{
                FEATUREID:line[0],
                TIME:1
            },style); //实例化Feature，参数：geometry，attributes，style
            lineFeatures.push(lineFeature);
        }
        animatorVector.addFeatures(lineFeatures); //需要添加的要素数组。（也可以是单个feature）
    }
};
GridProductClass.prototype.displayGridProductWithTime = function(recall, type, level, element, maketime, version, datetime, hourspan, fromModel, elementName, houOrXun){
    var t = this;

    t.dataStack = new DataStack();

    if(t.datasetGrid != null && t.datasetGrid.isDirty)
    {
        var result = confirm("格点已修改，是否保存？");
//        if (result == true)
//            t.saveGridProduct(t.currentType); //当前类型有误
    }
    var map = GDYB.Page.curPage.map;
    var elementLast = GDYB.GridProductClass.currentElement;
    GDYB.GridProductClass.currentElement = element;
    GDYB.GridProductClass.currentElementName = elementName;
    GDYB.GridProductClass.currentLevel = level;
    GDYB.GridProductClass.currentType = type;
    GDYB.GridProductClass.currentMakeTime = maketime;
//    GDYB.GridProductClass.currentVersion = version;
    GDYB.GridProductClass.currentDateTime = datetime;
    GDYB.GridProductClass.currentHourSpan = hourspan;
    GDYB.GridProductClass.currentFromModel = fromModel;

    map.events.register("moveend", map, function() {
        var pMap = this;
        if (t.timeoutid != null)window.clearTimeout(t.timeoutid);
        t.timeoutid = window.setTimeout(function () {
            refresh(pMap);
            t.timeoutid = null;
        }, 100)
    });

    displayGrid(recall);

    //如果不是天气现象、空气污染气象条件等级预报要素，切换其他要素时，默认填色平滑显示。因为天气现象、空气污染气象条件等级预报要素会自动设置为不平滑，需要还原。
    if(element != "w" && element != "air" && elementLast != element){
        t.layerFillRangeColor.isSmooth = true;
    }


    var typeName = getTypeName(type);
    var levelName = level == null || level == "1000" ?"" : level + "百帕";
    var titleDate = "";
    var year = parseInt(datetime.replace(/(\d*)-\d*-\d* \d*:\d*:\d*/,"$1"));
    var month = parseInt(datetime.replace(/\d*-(\d*)-\d* \d*:\d*:\d*/,"$1"));
    var day = parseInt(datetime.replace(/\d*-\d*-(\d*) \d*:\d*:\d*/,"$1"));
    var hour = parseInt(datetime.replace(/\d*-\d*-\d* (\d*):\d*:\d*/,"$1"));
    var minutes = 0;
    var seconds = 0;

    var date1 = new Date();
    date1.setFullYear(year,month - 1,day);
    date1.setHours(hour, minutes, seconds, 0);
    var time1 = date1.getTime();
    if(houOrXun=="候"){
        time1+=(hourspan/24-1)*5*24*60*60*1000;
    }
    else if(houOrXun=="旬"){
        time1+=(hourspan/24-1)*10*24*60*60*1000;
    }
    date1.setTime(time1);

    var date2 = new Date();
    date2.setFullYear(year,month - 1,day);
    date2.setHours(hour, minutes, seconds, 0);
    var time2 = date2.getTime();
    if(houOrXun=="候"){
        time2=time1+5*24*60*60*1000;
    }
    else if(houOrXun=="旬"){
        time2=time1+10*24*60*60*1000;
    }
    date2.setTime(time2);

    titleDate = date1.getFullYear() +"年" + (date1.getMonth()+1) + "月" + date1.getDate() + "日- " + date2.getDate() + "日";

    var forecastor = "";//"<br>预报员：曾小团，签发：曾小团";
    var unit = "";
    if(type == "prvn" || type == "cty")
    {
        unit = "四川";
        if(t.currentUserDepart != null && t.currentUserDepart.departCode.length > 2){
            unit = t.currentUserDepart.departName;
        }
        var gridInfo = t.getGridInfoFromCache(t.currentType, t.currentElement, t.currentDateTime, t.currentHourSpan);
        if(gridInfo != null && typeof(gridInfo.forecaster) != "undefined" && typeof(gridInfo.issuer) != "undefined")
            forecastor = "<br>预报员：" + gridInfo.forecaster + "，签发：" + gridInfo.issuer;
    }
    else
        unit = type;
    var title = "<p style='line-height: 20px; font-size: 24px;padding: 10px 0px 0px 0px;font-weight: bold;color: red;'>" + unit + levelName + elementName + "预报</p>" + titleDate + forecastor;
    $("#map_title_div").css("display", "block");
    $("#map_title_div").html("<div style=\"line-height: 24px\">" + title +"</div>");

    function getTypeName(type)
    {
        var typeName = type;
        if(type == "BJ")
            typeName = "中央台产品";
        else if(type == "OBJ")
            typeName = "客观产品";
        else if(type == "PRVN")
            typeName = "区级产品";
        else if(type == "CTY")
            typeName = "市级产品";

        return typeName;
    }

    /*
     * 调入模式
     * */
    GridProductClass.prototype.callModel = function(type, maketime) {
        var t = this;
        t.currentFromModel = type;
        var map = GDYB.Page.curPage.map;

        var bCallModel = true;
        var element = t.currentElement;
        var version = "p";
        var datetime = t.currentDateTime;
        var hourspan = t.currentHourSpan;
        var level = t.currentLevel;


        if(t.layerLabel != null)
            t.layerLabel.removeAllFeatures();

        this.getGrid(function(datasetGrid){
            if(datasetGrid != null && datasetGrid.grid.length > 0)
            {
                t.currentGridValuePicked = datasetGrid.noDataValue;

                t.datasetGrid = datasetGrid;
                if(t.layerFillRangeColor != null)
                {
                    if(t.fillColorType == "值域" && !(element == "w" || element == "air"))
                        t.layerFillRangeColor.items = t.getFillColorItemsByRange(element, datasetGrid.dMin, datasetGrid.dMax);

                    t.layerFillRangeColor.isWind = (element == "10uv" || element == "wmax");
                    t.layerFillRangeColor.setDatasetGrid(datasetGrid);
                    GDYB.Legend.update(t.layerFillRangeColor.items);
                    t.updateStationLayer();
                }
                recall&&recall();
            }
            else
            {
                if(!bCallModel) {
                    t.datasetGrid = datasetGrid;
                    t.layerFillRangeColor.setDatasetGrid(null);
                    t.layerLabel.removeAllFeatures();
                    if (t.layerContour != null)
                        t.layerContour.removeAllFeatures();
                    if (t.layerPolygon != null)
                        t.layerPolygon.removeAllFeatures();
                }
            }
            if(datasetGrid == null){
                alertFuc(bCallModel?"调入失败，无对应时效数据":"无数据");
            }
            else if(t.layerLabel.visibility && (element != "10uv" && element != "wmax"))
            {
//                t.addLabel(recall, pMap);
            }
        }, element, type, level, hourspan, maketime, version, datetime, bCallModel);
    };

    /*
     * 调入（要素所有时效）数值模式集合
     * */
    GridProductClass.prototype.callModels = function(recalls, type, maketime, version, date, element, elementName, level, hourspans, strCallModel, version) {
        var t = this;
        t.currentFromModel = type;
        t.getGrids(recalls, element, elementName, type, level, hourspans, maketime, version, date, strCallModel);
    };

    var strategy;
    function displayGrid(recall){
        var layerPolygonVisibel = true; //默认显示色斑图
        if(t.layerPolygon != null)
        {
            layerPolygonVisibel = t.layerPolygon.visibility;
            map.removeLayer(t.layerPolygon, false);
        }

        //新建一个策略
        strategy = new WeatherMap.Strategy.GeoText();
        //设置标签的样式
        strategy.style = {
            fontFamily:"Arial",
            fontColor:"#333",
            fontSize:"14px",
            fill: false,
            stroke: false
        };

        //填色图
        if(t.layerFillRangeColor == null)
        {
            t.layerFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer(
                "heatMap",
                {
                    "radius":40,
                    "featureWeight":"value",
                    "featureRadius":"geoRadius"
                }
            );
            //t.layerFillRangeColor.visibility = layerFillRangeColorVisible;
            map.addLayers([t.layerFillRangeColor]);
            t.layerFillRangeColor.isAlwaySmooth = false;
            t.layerFillRangeColor.isSmooth = true;
        }
        //填色风格
        t.layerFillRangeColor.items = t.getFillColorItems(element);
        if(element == "air")
        {
            t.layerFillRangeColor.isSmooth = false;
            t.layerFillRangeColor.isAlwaySmooth = false;
        }
        else if(element == "w")
        {
            t.layerFillRangeColor.isSmooth = false;
            t.layerFillRangeColor.isAlwaySmooth = false;
        }

//        //色斑图
//        t.layerPolygon = new WeatherMap.Layer.Vector("Polygon", {renderers: ["Canvas2"]});
//        t.layerPolygon.visibility = layerPolygonVisibel;
//        map.addLayers([t.layerPolygon]); //先添加，就在下面

        //等值线
        if(t.layerContour == null)
        {
            t.layerContour = new WeatherMap.Layer.Vector("Contour", {renderers: ["Contour"]});
            t.layerContour.renderer.labelField = "dZValue";
            t.layerContour.visibility = false;
            t.layerContour.style = {
                fontFamily:"Arial",
                fontColor:"#333",
                fontSize:"16px",
                fontWeight:"bold",
                //strokeColor: "#ff0000",
                strokeColor: "#c47a55",
                strokeWidth: 1.0
            };
            map.addLayers([t.layerContour]);
        }
        else
        {
            //if(!t.layerContour.visibility)
            t.layerContour.removeAllFeatures();
        }

        //填值
        if(t.layerLabel == null)
        {
            t.layerLabel = new WeatherMap.Layer.Vector("Label",{strategies: [strategy],renderers:["Canvas"]});
            map.addLayers([t.layerLabel]);
        }

        //添加自由曲线图层
        if(t.layerFreePath == null)
        {
            t.layerFreePath = new WeatherMap.Layer.Vector("FreePath");
            t.layerFreePath.style = {
                strokeColor: "#ff0000",
                strokeWidth: 2.0
            };
            map.addLayers([t.layerFreePath]);
            t.drawFreePath = new WeatherMap.Control.DrawFeature(t.layerFreePath, WeatherMap.Handler.PathFree);
            map.addControl(t.drawFreePath);
            t.drawFreePath.events.on({"featureadded": drawFreePathCompleted});
        }

        //添加绘制风向的线图层
        if(t.layerWindDirection == null){
            t.layerWindDirection = new WeatherMap.Layer.Vector("layerWindDirection");
            map.addLayers([t.layerWindDirection]);
            t.drawWindDirection = new WeatherMap.Control.DrawFeature(t.layerWindDirection, WeatherMap.Handler.Path, { multi: false, maxVertices:2});
            t.drawWindDirection.handler.maxVertices = 2; //上面设置不进去，就直接赋值吧
            map.addControl(t.drawWindDirection);
            t.drawWindDirection.events.on({"featureadded": drawWindDirectionCompleted});
        }

        if(t.layerMagic == null)
        {
//            t.layerMagic = new WeatherMap.Layer.Vector("Magic");
//            map.addLayers([t.layerMagic]);

            t.layerMagic = new WeatherMap.Layer.AnimatorVector("NeonLine", {rendererType:"NeonLine"},{
                repeat:true,
                //设置速度为每帧播放的数据
                speed:1,
                //开始时间为0
                startTime:1,
                //每秒渲染10帧
                frameRate:6,
                //结束时间设置为10
                endTime:1
            });
            t.layerMagic.animator.events.on({"framestart": layerMagicFramestart});
            t.layerMagic.style = {
                strokeWidth: 2.0
            };
            map.addLayers([t.layerMagic]);
        }

        if(t.layerMapping == null){
            t.layerMapping = new WeatherMap.Layer.MappingLayer("LayerMapping");
            map.addLayers([t.layerMapping]);
        }

        //创建一个气候区划图层
        if(t.layerClimaticRegion == null)
        {
            t.createClimaticRegionLayer(map);
        }

        //创建气候区划代表站图层
        if(t.layerMarkers == null)
        {
            t.layerMarkers = new WeatherMap.Layer.Markers("layerMarkers");
            map.addLayers([t.layerMarkers]);
        }

        //添加落区图层
        if(t.layerLuoqu == null)
        {
            t.layerLuoqu = new WeatherMap.Layer.Vector("Luoqu");
            t.layerLuoqu.style = {
                strokeColor: "#ff0000",
                strokeWidth: 2.0,
                fillColor: "#ff0000",
                fillOpacity: "0"
            };
            map.addLayers([t.layerLuoqu]);
            //t.drawRectangle = new WeatherMap.Control.DrawFeature(t.layerLuoqu, WeatherMap.Handler.Box);
            t.drawLuoqu = new WeatherMap.Control.DrawFeature(t.layerLuoqu, WeatherMap.Handler.PolygonFree);
            map.addControl(t.drawLuoqu);
            t.drawLuoqu.events.on({"featureadded": drawCompleted});
        }

        //添加落区重心图层
        if(t.layerLuoquCenter == null){
            t.layerLuoquCenter = new WeatherMap.Layer.Vector("layerLuoquCenter");
            t.layerLuoquCenter.style = {
                fillColor: "#00cc00",
                fillOpacity: 0.75,
                pointRadius: 10,
                strokeColor: "#cc0000",
                strokeWidth: 1.0,
                strokeOpacity:1.0,
                stroke: true
            };
            map.addLayers([t.layerLuoquCenter]);
            t.dragFeature = new WeatherMap.Control.DragFeature(t.layerLuoquCenter);
            map.addControl(t.dragFeature);
            //完成拖拽
            t.dragFeature.onComplete = function(feature, pixel){
                if(feature != null && feature.geometry.CLASS_NAME == "WeatherMap.Geometry.Point" && t.currentGridValueDown != t.datasetGrid.noDataValue) {
                    var featureRegion = t.layerLuoqu.features[0];
                    GDYB.GridProductClass.updateGridByIDW(featureRegion.geometry);
                }
            };
            t.dragFeature.activate();

            map.events.register("mouseup", map, function(event){
                if(event.button == 2){ //右键取值
                    //反距离权重
                    if(t.layerLuoquCenter != null && t.layerLuoquCenter.features.length > 0) {
                        var lonlat = this.getLonLatFromPixel(event.xy);
                        var pt = t.datasetGrid.xyToGrid(lonlat.lon, lonlat.lat);
                        var dValue = t.datasetGrid.grid[pt.y][pt.x].z;
                        $("#popup_text").val(dValue);
                        setTimeout(function () {
                            $("#popup_div").css("display", "block")
                        }, 100);
                    }
                }
                else if(event.button == 0){ //左键加中心
                    if(t.currentGridValuePicked != null && t.currentGridValuePicked != t.datasetGrid.noDataValue && t.currentGridValueDown != t.datasetGrid.noDataValue){
                        if(t.layerLuoqu.features.length > 0 && GDYB.GridProductClass.layerLuoquCenter.features.length > 0) {
                            var feature = t.layerLuoqu.features[0];
                            var lonlat = this.getLonLatFromPixel(event.xy);
                            var point = new WeatherMap.Geometry.Point(lonlat.lon, lonlat.lat);
                            var pointVector = new WeatherMap.Feature.Vector(point);
                            pointVector.attributes.z = t.currentGridValuePicked;
                            GDYB.GridProductClass.layerLuoquCenter.addFeatures([pointVector]);
                            GDYB.GridProductClass.dragFeature.activate();

                            var featureRegion = t.layerLuoqu.features[0];
                            GDYB.GridProductClass.updateGridByIDW(featureRegion.geometry);
                        }
                    }
                    t.currentGridValuePicked = null;
                }
            });
        }
        t.addGridWithhouXun(recall, map);

        if(t.currentElement != "10uv" && t.currentElement != "wmax") {
            //由于存在异步处理，等值线色斑图不应该写在这里，应该在格点请求完成后
//            //显示色斑图
//            if (t.layerPolygon.visibility)
//                t.drawDengzhimian(recall, map);
//
//            //显示等值线
//            if (t.layerContour.visibility)
//                t.addContour(recall, map);
        }

        //清空所有订正痕迹
        if(t.layerLuoqu != null)
            t.layerLuoqu.removeAllFeatures();
        if(t.layerLuoquCenter != null)
            t.layerLuoquCenter.removeAllFeatures();
        if(t.layerMagic != null)
            t.layerMagic.removeAllFeatures();
        if(t.layerFreePath != null)
            t.layerFreePath.removeAllFeatures();

        if(GDYB.GDYBPage.myPanel_Tools != null)
            GDYB.GDYBPage.myPanel_Tools.updateUI(element, elementName);
    }

    function refresh(pMap){
        var layerLabels = pMap.getBy("layers","name","Label");
        var layerLabel = layerLabels[0];
        if(layerLabel != null && layerLabel.visibility && t.currentElement != "10uv" && t.currentElement != "wmax")
        {
            layerLabel.removeAllFeatures();
            var bounds = pMap.getExtent();
            //var boundsStr = "{element:'"+ t.currentElement +"', left:"+bounds.left+", bottom:"+bounds.bottom+",right:"+bounds.right+", top:"+bounds.top+"}";
            var para = "{element:'"+ t.currentElement + "',type:'"+ t.currentType + "',level:'"+ t.currentLevel + "',hourspan:"+ t.currentHourSpan + ",datetime:'"+ t.currentDateTime  +"',left:"+bounds.left+", bottom:"+bounds.bottom+",right:"+bounds.right+", top:"+bounds.top+"}";
            t.addLabel(null, pMap, para);
        }

//        if(t.layerPlot != null)
//            t.addPlot(null, pMap, null);
    }

    function refreshAll(){
        //refresh();
    }

    function drawCompleted(){
        //$("#popup_div").css("display","block");
        //落区保留最新那个
        if(t.layerLuoqu.features.length > 1)
        {
            var count = t.layerLuoqu.features.length;
            for(var i=count-2;i>=0;i--)
                t.layerLuoqu.removeFeatures(t.layerLuoqu.features[i]);
        }
        //落区中心全部删除
        if(t.layerLuoquCenter.features.length > 0)
        {
            var count = t.layerLuoquCenter.features.length;
            for(var i=count-1;i>=0;i--)
                t.layerLuoquCenter.removeFeatures(t.layerLuoquCenter.features[i]);
        }

        //GDYB.GridProductClass.updateGridProduct(GDYB.GridProductClass.currentGridValue); //服务端订正

        if(t.currentElement == "10uv"){
            //GDYB.GridProductClass.drawFreePath.activate(); //注释原因：落区画完后不自动进入风向订正，通过面板底部的风向进行订正。
            //停止落区绘制，线和面的绘制决不能同时进行
            //GDYB.GridProductClass.drawLuoqu.deactivate();
        }
//        else
        {
            if(GDYB.GridProductClass.luoquCorrectType == 0) //保持空间分布趋势
            {
                if(GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null)
                    GDYB.GridProductClass.updateGridBySpatial(GDYB.GridProductClass.currentGridValueDown, GDYB.GridProductClass.currentGridValueUp);  //客户端订正
            }
            else if(GDYB.GridProductClass.luoquCorrectType == 1 &&
                GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null &&
                GDYB.GridProductClass.currentGridValueDown != t.datasetGrid.noDataValue &&
                GDYB.GridProductClass.currentGridValueUp != t.datasetGrid.noDataValue)//如果不保持空间趋势，而是距离反比插值
            {
                //添加落区中心点，作为极大值点
                var feature = t.layerLuoqu.features[0];
                var bounds = feature.geometry.bounds;
                var centerLonLat = {x:bounds.left + (bounds.right - bounds.left)/2, y:bounds.bottom+(bounds.top - bounds.bottom)/2};
                var pointMax = new WeatherMap.Geometry.Point(centerLonLat.x, centerLonLat.y);
                var pointVectorMax = new WeatherMap.Feature.Vector(pointMax);
                pointVectorMax.attributes.z = GDYB.GridProductClass.currentGridValueUp;
                GDYB.GridProductClass.layerLuoquCenter.addFeatures([pointVectorMax]);

                GDYB.GridProductClass.dragFeature.activate();

                //距离反比权重订正
                t.layerLuoquCenter.borderValue = t.currentGridValueDown;
                //if(GDYB.GridProductClass.currentGridValueDown != null && GDYB.GridProductClass.currentGridValueUp != null)
                GDYB.GridProductClass.updateGridByIDW(feature.geometry);

                //停止落区绘制
                GDYB.GridProductClass.drawLuoqu.deactivate();
//                //恢复地图拖拽
//                var map = GDYB.Page.curPage.map;
//                for(var i =0; i < map.events.listeners.mousemove.length; i++) {
//                    var handler = map.events.listeners.mousemove[i];
//                    if (handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag") {
//                        handler.obj.active = true;
//                    }
//                }
            }
        }

        GDYB.GDYBPage.myPanel_Tools.showDivSettingAssignment();
    }

    function drawFreePathCompleted(){
        if(t.layerFreePath.features.length > 0) {
            var feature = t.layerFreePath.features[0];
            var geoLine = feature.geometry;
            t.layerFreePath.removeAllFeatures();
            if(t.action == GDYB.CorrectAction.modifyLuoqu){ //修改落区，局部修改
                var lineLuoqu = GDYB.MagicTool.geoline;
                if(lineLuoqu == null)
                    return;
                var lineCorrect = geoLine;
                //1.获取两线交点
                var intersections = []
                for(var i=1; i<lineLuoqu.components.length; i++){
                    var pt1 = lineLuoqu.components[i-1];
                    var pt2 = lineLuoqu.components[i];
                    for(var j=1; j<lineCorrect.components.length; j++){
                        var pt3 = lineCorrect.components[j-1];
                        var pt4 = lineCorrect.components[j];
                        var intersection = t.calcIntersection(pt1, pt2, pt3, pt4);
                        if(intersection == null)
                            continue;
                        intersections.push({pt:intersection, indexLuoquLine:i, indexCorrectLine:j});
                    }
                }
                //2.构面
                if(intersections.length < 2){
                    alert("与落区不封闭，交点少于2个");
                    return;
                }

                var geoRegion = toGeoRegion(lineLuoqu, lineCorrect, intersections, true);
                if(geoRegion.containsPoint(lineLuoqu.components[intersections[0].indexLuoquLine - 1]) &&
                    geoRegion.containsPoint(lineLuoqu.components[intersections[0].indexLuoquLine])) //多边形错误，落区节点取反了
                {
                    geoRegion = toGeoRegion(lineLuoqu, lineCorrect, intersections, false);
                }

                //6.获取落区上中点坐标，作为待修改区域的中心
                var indexMiddle = Math.floor(intersections[0].indexLuoquLine + (intersections[1].indexLuoquLine - intersections[0].indexLuoquLine)/2); //由于落区线的线段是等距的，故可以这样
                var ptLineMiddle = lineLuoqu.components[indexMiddle];
                var ptGrid = t.datasetGrid.xyToGrid(ptLineMiddle.x, ptLineMiddle.y);
                if(!GDYB.MagicTool.arrayTag[ptGrid.y][ptGrid.x]){
                    for(var i=-1; i<2; i++){
                        for(var j=-1; j<2; j++){
                            if(GDYB.MagicTool.arrayTag[ptGrid.y + i][ptGrid.x+j]){
                                ptGrid = {x:ptGrid.x+j, y:ptGrid.y + i};
                                break;
                            }
                        }
                    }
                }
                var ptMiddle = t.datasetGrid.gridToXY(ptGrid.x, ptGrid.y);
                var valueUp = t.datasetGrid.grid[ptGrid.y][ptGrid.x].z;
                var valueDown = t.currentGridValueDown;

                //如果没有指定下限值，则用最小值
                if(t.currentGridValueDown == t.datasetGrid.noDataValue){
                    var minValue = valueUp;
                    for (var i = 0; i < t.datasetGrid.rows; i++) {
                        for (var j = 0; j < t.datasetGrid.cols; j++){
                            if(GDYB.MagicTool.arrayTag[i][j]){
                                if(t.datasetGrid.grid[i][j].z < minValue)
                                    minValue = t.datasetGrid.grid[i][j].z;
                            }
                        }
                    }
                    valueDown = minValue;
                }

                //7.更新格点（采用一维线性最好）
                t.fillRegionByLinear(t.datasetGrid, geoRegion, ptMiddle, valueDown, valueUp, t.currentElement);
                //8.刷新填色图层
                t.layerFillRangeColor.refresh();
                //9.更新落区
                if(t.currentGridValueDown == t.datasetGrid.noDataValue){
                    GDYB.MagicTool.getLine(t.datasetGrid);
                }
                else {
                    var lonlat = {lon:ptLineMiddle.x, lat:ptLineMiddle.y};
                    GDYB.MagicTool.pick(lonlat);
                }
                t.dataStack.push(t.datasetGrid); //压入堆栈
            }
            else if(t.currentElement == "10uv" || t.currentElement == "wmax") { //风场订正
                if (t.layerLuoqu.features.length > 0){ //按绘制的落区订正
                    var geoRegion = null;
                    geoRegion = t.layerLuoqu.features[0].geometry;
                    GDYB.GridProductClass.updateGridWind(geoLine, geoRegion);
                }
                else if(t.layerMagic.features.length > 0){ //按拾取的落区订正
                    GDYB.MagicTool.updateGridWind(geoLine);
                }
                else{ //按影响半径订正
                    GDYB.GridProductClass.updateGridWind(geoLine, null);
                }

                t.layerFillRangeColor.refresh();
            }
            else{ //画刷
                t.updateGridbyBrush(geoLine, t.currentGridValueDown);
                t.layerFillRangeColor.refresh();
            }
        }

        /*
         * 构面
         * lineLuoqu：落区边界线
         * lineCorrect：订正线
         * intersections：交点
         * bForward：是否（按节点索引）顺向取落区节点
         * */
        function toGeoRegion(lineLuoqu, lineCorrect, intersections, bForward){
            var pts = [];
            //2.1 添加第一个交点
            pts.push(new WeatherMap.Geometry.Point(intersections[0].pt.x, intersections[0].pt.y));
            //2.2 添加落区节点
            if(bForward) {
                for (var i = intersections[0].indexLuoquLine; i < intersections[1].indexLuoquLine; i++) {
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
            }
            else{
                for(var i=intersections[0].indexLuoquLine - 1; i>=0; i--){
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
                for(var i=lineLuoqu.components.length - 1; i>=intersections[1].indexLuoquLine; i--){
                    var pt = lineLuoqu.components[i];
                    pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
                }
            }
            //2.3 添加第二个交点
            pts.push(new WeatherMap.Geometry.Point(intersections[1].pt.x, intersections[1].pt.y));
            //2.4 添加订正线节点
            var count = Math.abs(intersections[1].indexCorrectLine - intersections[0].indexCorrectLine);
            var delta = (intersections[1].indexCorrectLine - intersections[0].indexCorrectLine)/count;
            var startIndex = intersections[1].indexCorrectLine; //必须从这个位置开始，否则节点顺序就乱了
            for(var i = 0; i<count; i++){
                var pt = lineCorrect.components[startIndex-i*delta];
                pts.push(new WeatherMap.Geometry.Point(pt.x, pt.y));
            }

            //3.构面
            var line =  new WeatherMap.Geometry.LinearRing(pts);
            var lines  = [];
            lines.push(line);
            var geoRegion = new WeatherMap.Geometry.Polygon(lines);
            //geoRegion.calculateBounds(); //这个怎么不靠谱呢
            //4.计算Bounds
            var bounds = new WeatherMap.Bounds(180, 90, -180, -90);
            for(var i=0; i<pts.length; i++){
                var pt = pts[i];
                if(pt.x < bounds.left)
                    bounds.left = pt.x;
                if(pt.x > bounds.right)
                    bounds.right = pt.x;
                if(pt.y < bounds.bottom)
                    bounds.bottom = pt.y;
                if(pt.y > bounds.top)
                    bounds.top = pt.y;
            }
            geoRegion.bounds = bounds;
            return geoRegion;
        }
    }

    function drawWindDirectionCompleted(){
        if(t.layerWindDirection.features.length > 0) {
            var feature = t.layerWindDirection.features[0];
            var geoLine = feature.geometry;
            t.layerWindDirection.removeAllFeatures();

            GDYB.GridProductClass.updateGridWind(geoLine, null);
            GDYB.GridProductClass.layerFillRangeColor.refresh();
        }
    }

    //显示风向风速
    function displayUV()
    {
        //初始化动画矢量图层
        t.layerStreamLine = new WeatherMap.Layer.AnimatorVector("StreamLine", {rendererType:"StreamLine"},{
            repeat:true,
            //设置速度为每帧播放的数据
            speed:1,
            //开始时间为0
            startTime:1,
            //每秒渲染10帧
            frameRate:12,
            //结束时间设置为10
            endTime:1
        });
        t.layerStreamLine.animator.events.on({"framestart": layerStreamLineFramestart});
        var layerGrid = new WeatherMap.Layer.Vector("LayerGrid", {renderers: ["Canvas"]});
        map.addLayers([layerGrid,t.layerStreamLine]);

        //流线
        addStreamLine(t.layerStreamLine);

        //流场
        var maxValue = 6.0;
        var colorStart = [35, 40, 146];
        var colorEnd = [25, 130, 66];
        var cols = 37;
        var rows = 24;
        var dx = 0.25;
        var dy = 0.25;
        addGrid(layerGrid, uv, maxValue, rows, cols, dx, dy, colorStart, colorEnd);

        t.layerStreamLine.animator.start();
    }

//    //显示风向风速
//    function displayWind()
//    {
//        //先移除已有图层
//        if(t.layerLabel != null)
//            map.removeLayer(t.layerLabel, false);
//        if(t.layerPolygon != null)
//            map.removeLayer(t.layerPolygon, false);
//
//        if(t.layerPlot == null)
//        {
//            t.layerPlot = new WeatherMap.Layer.Vector("Plot");
//            map.addLayer(t.layerPlot);
//        }
//
//        t.addPlot(null, map, null);
//    }

    function layerMagicFramestart()
    {
        if(t.layerMagic != null)
            t.layerMagic.renderer.frameCount++;
    }

    function layerStreamLineFramestart()
    {
        if(t.layerStreamLine != null)
            t.layerStreamLine.renderer.frameCount++;
    }

    //添加数据
    function addStreamLine(animatorVector)
    {
        var style =
        {
            fillColor: "#cc0000",
            pointRadius: 3,
            strokeColor: "#7AFF168A8",
            strokeWidth: 1
        };
        var lineFeatures = [];
        for(var i = 0,len = lines.length;i<len;i++)
        {
            var line = lines[i];
            var pts = line[1]; //全部节点
            var arrP = []; //定义节点数组
            for(var j=0;j<pts.length;j++)
            {
                var point = new WeatherMap.Geometry.Point(pts[j][0],pts[j][1]); //节点
                arrP.push(point); //节点数组赋值
            }
            var lineString = new WeatherMap.Geometry.LineString(arrP); //通过点串构造几何对象线串类LineString

            var lineFeature = new WeatherMap.Feature.Vector(lineString,{
                FEATUREID:line[0],
                TIME:1
            },style); //实例化Feature，参数：geometry，attributes，style
            lineFeatures.push(lineFeature);
        }
        animatorVector.addFeatures(lineFeatures); //需要添加的要素数组。（也可以是单个feature）
    }

    //Point转Rectangle
    function addGrid(layerGrid, grid, maxValue, rows, cols, dx, dy, colorStart, colorEnd){
        var features = [];
        var count = 4;
        var w = dx / count;
        var h = dx / count;
        var rx = w / 2.0;
        var ry = h / 2.0;
        for (var i = 0; i < grid.length; i++) {
            var pt = grid[i][4];
            {
                for(var j=0; j<count; j++)
                {
                    if(grid[i][2] == cols - 1 && j > 0) //最后一列
                        break;

                    var ptXTemp = [pt[0]+w*j, pt[1]];

                    var vX1;
                    if(grid[i][2] == cols - 1)
                        vX1 = (count - j)/count*grid[i][3] + j/count*grid[i - 1][3];
                    else
                        vX1 = (count - j)/count*grid[i][3] + j/count*grid[i + 1][3];

                    if(grid[i][1] < rows - 1) {
                        var vX2;
                        if(grid[i][2] == cols - 1)
                            vX2 = (count - j)/count*grid[i + cols][3] + j/count*grid[i + cols - 1][3];
                        else
                            vX2 = (count - j)/count*grid[i + cols][3] + j/count*grid[i + cols + 1][3];

                        for (var k = 0; k < count; k++) {
                            var ptYTemp = [ptXTemp[0], ptXTemp[1] - h * k];
                            var v = (count - k) / count * vX1 + k / count * vX2;
                            dv = v/maxValue;
                            var color = "rgba(" +Math.round(colorStart[0] - dv*(colorStart[0] - colorEnd[0])) + "," + Math.round(colorStart[1] + dv*(colorEnd[1] - colorStart[1])) +"," + Math.round(colorStart[2] - dv*(colorStart[2]-colorEnd[2])) +",255)";
                            //contextMap.fillStyle = color;
                            var style = {
                                fillColor: "#cc0000",
                                fillOpacity: 0.75,
                                //fillOpacity: 1.0,
                                pointRadius: 3,
                                strokeColor: "#000000",
                                strokeWidth: 0.15,
                                strokeOpacity:0.75,
                                stroke: true
                            };
                            style.fillColor = color;
                            style.strokeColor = color;
                            var rectangle = new WeatherMap.Geometry.Rectangle(ptYTemp[0] - rx, ptYTemp[1] - ry, w, h);
                            var rectFeature = new WeatherMap.Feature.Vector(rectangle, null, style); //实例化Feature，参数：geometry，attributes，style
                            features.push(rectFeature);
                        }
                    }
                }
            }
        }
        layerGrid.addFeatures(features);
    }
};

//这个方法最好写到里面去，或者新增并包入方法UpdateLayer
GridProductClass.prototype.addLabel = function(recall, pMap, para){
    return;
    var t = this;
    var layerLabels = pMap.getBy("layers","name","Label");
    var layerLabel = layerLabels[0];
    if(layerLabel == null || !layerLabel.visibility)
        return;
    layerLabel.removeAllFeatures();

//    var t1=new Date().getTime(); //test
    if(this.datasetGrid == null || this.datasetGrid.grid.length == 0)
        return;

    var bounds = pMap.getExtent();
    var nCountX = 30;
    var nCountY = 20;
//    var rows = this.grid.length;
//    var cols = this.grid[0].length;
//    var gridLeft = this.grid[0][0].x;
//    var gridTop = this.grid[0][0].y;
//    var gridRight = this.grid[this.grid.length - 1][this.grid[0].length - 1].x;
//    var gridBottom = this.grid[this.grid.length - 1][this.grid[0].length - 1].y;
//    var deltaX = Math.abs(gridRight - gridLeft) / (cols - 1);
//    var deltaY =  Math.abs(gridTop - gridBottom) / (rows - 1);

    var nLeft = (bounds.left - this.datasetGrid.left) / this.datasetGrid.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= this.datasetGrid.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (this.datasetGrid.top - bounds.top) / this.datasetGrid.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= this.datasetGrid.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - this.datasetGrid.left) / this.datasetGrid.deltaX;
    if(nRight <= 0)
        return;
    else if(nRight >= this.datasetGrid.cols)
        nRight = this.datasetGrid.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (this.datasetGrid.top - bounds.bottom) / this.datasetGrid.deltaX;
    if(nBottom < 0)
        return;
    else if(nBottom >= this.datasetGrid.rows)
        nBottom = this.datasetGrid.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    var width = nRight - nLeft;
    var height = nBottom - nTop;
    var nDetaX = 1;
    var nDetaY = 1;
    if(width <= nCountX)
        nDetaX = 1;
    else
        nDetaX = width%nCountX == 0 ? Math.floor(width/nCountX) : Math.floor(width/nCountX+1);
    if(height <= nCountY)
        nDetaY = 1;
    else
        nDetaY = height/nCountY == 0 ? Math.floor(height/nCountY) : Math.floor(height/nCountY+1);


    var labelFeatures = [];
    for(var i = nTop; i<= nBottom; i+=nDetaY)
    {
        var gridRow = this.datasetGrid.grid[i];
        for(var j = nLeft; j<=nRight; j+=nDetaX)
        {
            if(gridRow[j].z == -9999 || gridRow[j].z == 9999)
                continue;
            var dvalue = null;
            if(gridRow[j].z < 0.1 && gridRow[j].z>0)
                dvalue = "T";
            else
                dvalue = Math.floor(gridRow[j].z*10) / 10;
            var label = new WeatherMap.Geometry.GeoText(gridRow[j].x, gridRow[j].y, dvalue);
            var feature = new WeatherMap.Feature.Vector(label);
            labelFeatures.push(feature);
        }
    }
    layerLabel.addFeatures(labelFeatures);

    recall&&recall();
};
GridProductClass.prototype.addGrid = function(recall, pMap, strCallModel){
    var t = this;
    var bCallModel = typeof(strCallModel) != "undefined";
    var type = bCallModel? strCallModel : t.currentType;
    var element = t.currentElement;
    var maketime = t.currentMakeTime;
    var version = t.currentVersion;
    var datetime = t.currentDateTime;
    var hourspan = t.currentHourSpan;
    var level = t.currentLevel;


    if(t.layerLabel != null)
        t.layerLabel.removeAllFeatures();

    this.getGrid(function(datasetGrid) {
        if (datasetGrid != null && datasetGrid.grid.length > 0) {
            t.currentGridValuePicked = datasetGrid.noDataValue;

            t.datasetGrid = datasetGrid;
            if (t.layerFillRangeColor != null) {
                if (t.fillColorType == "值域" && !(element == "w" || element == "air"))
                    t.layerFillRangeColor.items = t.getFillColorItemsByRange(element, datasetGrid.dMin, datasetGrid.dMax);

                t.layerFillRangeColor.isWind = (element == "10uv" || element == "wmax"||element=="uv");
                t.layerFillRangeColor.setDatasetGrid(datasetGrid);
                GDYB.Legend.update(t.layerFillRangeColor.items);
                t.updateStationLayer();
            }
            recall && recall();
        }
        else {
            if (!bCallModel) {
                t.datasetGrid = datasetGrid;
                t.layerFillRangeColor.setDatasetGrid(null);
                t.layerLabel.removeAllFeatures();
                if (t.layerContour != null)
                    t.layerContour.removeAllFeatures();
                if (t.layerPolygon != null)
                    t.layerPolygon.removeAllFeatures();
            }
        }
        if (datasetGrid == null) {
            alertFuc(bCallModel ? "调入失败，无对应时效数据" : "无数据");
        }
        else if (t.layerLabel.visibility && (element != "10uv" && element != "wmax")) {
        }
    }, element, type, level, hourspan, maketime, version, datetime, bCallModel);
};
GridProductClass.prototype.addGridJuPing = function(recall, pMap, strCallModel,hosData){
    var t = this;
    var bCallModel = typeof(strCallModel) != "undefined";
    var type = bCallModel? strCallModel : t.currentType;
    var element = t.currentElement;
    var maketime = t.currentMakeTime;
    var version = t.currentVersion;
    var datetime = t.currentDateTime;
    var hourspan = t.currentHourSpan;
    var level = t.currentLevel;


    if(t.layerLabel != null)
        t.layerLabel.removeAllFeatures();

    this.getGrid(function(datasetGrid) {
        //处理历史数据
        var hosD=[];
        for(var r=0;r<hosData.rows;r++){
            var hosDLie=[];
            for(var c=0;c<hosData.cols;c++){
                var val=hosData.dvalues[r*hosData.cols+c];
                hosDLie.push(val);
            }
            hosD.push(hosDLie);
        }
        //更新DatasetGrid.grid数据
        var oldGrid=datasetGrid.grid;
        for(var h=0;h<oldGrid.length;h++){
            for(var w=0;w<oldGrid[h].length;w++){
                var oldVal=oldGrid[h][w].z;
                var hosVal=hosD[h][w];
                if(element=="2t")//距平
                {
                    var newVal=oldVal-hosVal;
                    oldGrid[h][w].z=newVal.toFixed(1);
            }
                else//距平率
                {
                    var newVal=100*(oldVal-hosVal)/hosVal;
                    oldGrid[h][w].z=newVal.toFixed(0);
                }
            }
        }
        datasetGrid.grid=oldGrid;
        if (datasetGrid != null && datasetGrid.grid.length > 0) {
            t.currentGridValuePicked = datasetGrid.noDataValue;

            t.datasetGrid = datasetGrid;
            if (t.layerFillRangeColor != null) {
                if (t.fillColorType == "值域" && !(element == "w" || element == "air"))
                    t.layerFillRangeColor.items = t.getFillColorItemsByRange(element, datasetGrid.dMin, datasetGrid.dMax);

                t.layerFillRangeColor.isWind = (element == "10uv" || element == "wmax");
                t.layerFillRangeColor.setDatasetGrid(datasetGrid);
                GDYB.Legend.update(t.layerFillRangeColor.items);
                t.updateStationLayer();
            }
            recall && recall();
        }
        else {
            if (!bCallModel) {
                t.datasetGrid = datasetGrid;
                t.layerFillRangeColor.setDatasetGrid(null);
                t.layerLabel.removeAllFeatures();
                if (t.layerContour != null)
                    t.layerContour.removeAllFeatures();
                if (t.layerPolygon != null)
                    t.layerPolygon.removeAllFeatures();
            }
        }
        if (datasetGrid == null) {
            alertFuc(bCallModel ? "调入失败，无对应时效数据" : "无数据");
        }

    }, element, type, level, hourspan, maketime, version, datetime, bCallModel);
};
GridProductClass.prototype.addGrid10to30JP = function(recall,hosData){
    var t = this;
    if(t.layerLabel != null)
        t.layerLabel.removeAllFeatures();
    var result= t.datasetGrid.grid;
    var min=9999;
    var max=-9999;
    var rows=hosData.rows;
    var cols=hosData.cols;
    var lastdata=0;
    for(var r=0;r<rows;r++){
        var hosDLie=[];
        for(var c=0;c<cols;c++){
            var index=r*cols+c
            var val=hosData.dvalues[index];
            if(val=="Infinity"||val=="-Infinity"){
                val=lastdata;
            }
            lastdata=val;
            if(val<min){
                min=val;
            }
            if(val>max){
                max=val;
            }
            val=val.toFixed(0);
            result[index]=val;
        }
    }
    //更新DatasetGrid.grid数据
    t.datasetGrid.grid=result;
    t.datasetGrid.dMin=min;
    t.datasetGrid.dMax=max;
    if (t.datasetGrid != null && t.datasetGrid.grid.length > 0) {
        if (t.layerFillRangeColor != null) {
            if (t.fillColorType == "值域" && !(element == "w" || element == "air"))
                t.layerFillRangeColor.items = t.getFillColorItemsByRange(element, t.datasetGrid.dMin, t. datasetGrid.dMax);

            t.layerFillRangeColor.isWind = false;
            t.layerFillRangeColor.setDatasetGrid(t.datasetGrid);
            GDYB.Legend.update(t.layerFillRangeColor.items);
            t.updateStationLayer();
            GDYB.GridProductClass.layerFillRangeColor.isSmooth = false;
            GDYB.GridProductClass.layerFillRangeColor.refresh();
        }
        recall && recall();
    }
};
GridProductClass.prototype.addGridWithhouXun = function(recall, pMap, strCallModel){
    var t = this;
    var bCallModel = typeof(strCallModel) != "undefined";
    var type = bCallModel? strCallModel : t.currentType;
    var element = t.currentElement;
    var maketime = t.currentMakeTime;
    var version = t.currentVersion;
    var datetime = t.currentDateTime;
    var hourspan = t.currentHourSpan;
    var level = t.currentLevel;


    if(t.layerLabel != null)
        t.layerLabel.removeAllFeatures();

    var houOrXun=$("#selectTimes option:selected")[0].innerHTML;
    var numTh=hourspan/24;//第几候或旬
    //得到所有时效的产品
    var count=houOrXun=="候"?5:10;
    for(var c=1;c<count+1;c++){
        var hs=(numTh-1)*count*24+c*24;
        this.getGrid(calDatasetGrid , element, type, level, hs, maketime, version, datetime, bCallModel);
    }
    var aDG=new Array();
    function calDatasetGrid(datasetGrid){
        if(datasetGrid!=null){
            aDG.push(datasetGrid);
        }
        if(count==aDG.length){
            var method=1;//1是平均，2是求和
            if(element=="2t"){
                method=1;
            }
            else if(element=="r24"){
                method=2;
            }
            var newDatasetGrid = new WeatherMap.DatasetGrid(aDG[0].left, aDG[0].top, aDG[0].right, aDG[0].bottom, aDG[0].rows, aDG[0].cols);
            newDatasetGrid.noDataValue = aDG[0].noDataValue;
            for(var a=0;a<aDG.length;a++){
                if(a==0){//第一个数据拷贝
                    var grid=aDG[a].grid;
                  newDatasetGrid.grid=grid;
                }
                else{
                    var srcGrid=aDG[a].grid;
                    var newGrid=newDatasetGrid.grid;
                    for(var i=0;i<newGrid.length;i++){
                        var tempNew=Number(newGrid[i]);
                        var tempSrc=Number(srcGrid[i]);
                        if(method==1){
                                newGrid[i]=((tempNew+tempSrc)/2).toFixed(1);
                        }
                        else if(method==2){
                                newGrid[i]=(tempNew+tempSrc).toFixed(1);
                        }
                    }
                    newDatasetGrid.grid=newGrid;
                }
            }
            //新格点计算完毕
            t.currentGridValuePicked = newDatasetGrid.noDataValue;

            t.datasetGrid = newDatasetGrid;
            if (t.layerFillRangeColor != null) {
                if (t.fillColorType == "值域" && !(element == "w" || element == "air"))
                    t.layerFillRangeColor.items = t.getFillColorItemsByRange(element, newDatasetGrid.dMin, newDatasetGrid.dMax);

                t.layerFillRangeColor.isWind = (element == "10uv" || element == "wmax");
                t.layerFillRangeColor.setDatasetGrid(newDatasetGrid);
                GDYB.Legend.update(t.layerFillRangeColor.items);
                t.updateStationLayer();
            }
            t.layerLabel.visibility && (element != "10uv" && element != "wmax")
        }
    }
};

GridProductClass.prototype.getGrid = function(recall, element, type, level, hourspan, maketime, version, datetime, bCallModel){
    //优先从缓存中取值。如果是调入模式，需要更新缓存
    var t = this;
    var dataCache = null;
    if(t.dataCache != null) {
        dataCache = t.dataCache.getData(maketime, version, datetime, element, hourspan);
        if (!bCallModel) {
            if (dataCache != null && dataCache.data != null) {
                var datasetGrid = dataCache.data;

                //显示模式来源
                var nhourSpan = Number(hourspan);
                var gridInfos = GDYB.GridProductClass.datasetGridInfos;
                if(gridInfos != null){
                    for(var i=0; i<gridInfos.length; i++){
                        if(gridInfos[i].type == t.currentType && gridInfos[i].element == element && gridInfos[i].makeTime == maketime && gridInfos[i].version == version && gridInfos[i].forecastTime == datetime && gridInfos[i].hourSpan == nhourSpan){
                            GDYB.SideWrapper.setActive(gridInfos[i].nwpmodel, gridInfos[i].nwpmodelTime);
                            break;
                        }
                    }
                }

                recall && recall(datasetGrid);
                return;
            }
        }
    }
    var url={};
    if(element=="uv"){
        url=Url_Config.gridServiceUrl+"services/GridService/getUVGrids";
    }
    else{
        url= bCallModel? Url_Config.gridServiceUrl+"services/GridService/callModel" : Url_Config.gridServiceUrl+"services/GridService/getGrid";
    }
    $.ajax({
        data:{"para":"{element:'"+ element + "',type:'"+ type + "',level:'"+ level + "',hourspan:"+ hourspan + ",maketime:'" + maketime + "',version:'" + version + "',datetime:'"+ datetime + "'}"},
        url:url,
        dataType:"json",
        success:function(data){
            var datasetGrid = null;
            var nwpModelTime = null;
            try
            {
                if(data.dvalues == null)
                {
                    if(!t.isBrowseMode && (type == "prvn" || type == "cty" || type == "cnty")){
                        datasetGrid = t.createDatasetGrid(maketime, version, datetime, element, hourspan); //如果数据集为空，则创建
                        t.dataCache.setDataStatus(maketime, version, datetime, element, hourspan, 0); //更新状态
                    }
                    else
                    {
                        recall&&recall(datasetGrid);
                        return;
                    }
                }
                else {
                    var gridUtil = new GridUtil();
                    var newData = gridUtil.clip(data);
                    var dvalues = newData.dvalues;
                    if (dvalues != null && dvalues.length > 0) {
                        var bWind = (element == "10uv" || element == "wmax"||element=="uv"); //是否为风场，风场具有两个字段（风向、风速），在dvalues中交叉表示
                        var hasTag = (!bWind)&&(dvalues.length==data.rows*data.cols*2);
                        var dimensions = (bWind||hasTag) ? 2 : 1; //维度，风场有两维；带有Tag属性也是两维
                        var dMin = 9999;
                        var dMax = -9999;
                        
                        datasetGrid = new WeatherMap.DatasetGrid(newData.left, newData.top, newData.right, newData.bottom, newData.rows, newData.cols);
                        datasetGrid.noDataValue = newData.noDataValue;
                        if (newData.nwpmodelTime != null)
                            nwpModelTime = newData.nwpmodelTime;
                        var grid = [];
                        var tag = [];
                        var rows=newData.rows;
                        var cols=newData.cols;
                        for (var i = 0; i < rows; i++) {
                            var nIndexLine = newData.cols * i * dimensions;
                            for (var j = 0; j < newData.cols; j++) {
                                var nIndex = nIndexLine + j * dimensions;
                                var z;
                                if (bWind) {
                                    z = dvalues[nIndex + 1];
                                    grid.push(Math.round(dvalues[nIndex+1])); //风速在前
                                    grid.push(Math.round(dvalues[nIndex]));   //风向在后
                                }
                                else {
                                    z = dvalues[nIndex];
                                    grid.push(Math.round(dvalues[nIndex] * 10) / 10);
                                    if(hasTag)
                                        tagLine.push(dvalues[nIndex+1]);
                                }
                                if (z != 9999 && z != -9999) {
                                    if (z < dMin)
                                        dMin = z;
                                    if (z > dMax)
                                        dMax = z;
                                }
                            }
                            if(hasTag)
                                tag.push(tagLine);
                        }
                        datasetGrid.grid = grid;
                        datasetGrid.dMin = dMin;
                        datasetGrid.dMax = dMax;
                        if(hasTag){
                            datasetGrid.tag = tag;
                            datasetGrid.defaultTag = 0;
                        }
                    }
                }
            }
            catch (err)
            {
                alert(err.message);
            }

            //更新缓存
            if(bCallModel){
                t.dataCache.addData(t.currentMakeTime, t.currentVersion, t.currentDateTime, element, t.currentHourSpan, datasetGrid, 1); //标记为已修改
                $("#yubaoshixiao").find("#"+hourspan+"h").removeClass("disabled");
                $("#yubaoshixiao").find("#"+hourspan+"h").addClass("modified");
            }

            //显示模式来源
            var nhourSpan = Number(hourspan);
            var gridInfos = GDYB.GridProductClass.datasetGridInfos;
            if(gridInfos != null){
                for(var i=0; i<gridInfos.length; i++){
                    if(gridInfos[i].type == t.currentType && gridInfos[i].element == element && gridInfos[i].makeTime == t.currentMakeTime && gridInfos[i].version == t.currentVersion && gridInfos[i].forecastTime == t.currentDateTime && gridInfos[i].hourSpan == t.currentHourSpan){
                        if(bCallModel)
                        {
                            gridInfos[i].nwpmodel = type; //更新数值模式来源
                            gridInfos[i].nwpmodelTime = nwpModelTime; //更新数值模式时间
                        }
                        GDYB.SideWrapper.setActive(gridInfos[i].nwpmodel, gridInfos[i].nwpmodelTime);
                        break;
                    }
                }
            }
            recall&&recall(datasetGrid);
        },
        type:"POST"
    });
};

/*
 * 获取格点产品信息
 */
GridProductClass.prototype.getGridInfo = function(recall, departCode, type, element, datetime, version){
    var t = this;

    //优先从缓存中取
    var gridinfos = t.getGridInfosFromCache(type, element, datetime);
    var hourspans = GDYB.GDYBPage.getHourSpan(element);
    var hourspansNoData = [];

    //有就直接返回，不再请求
    if(gridinfos.length > 0){
        recall&&recall(gridinfos);
        return;
    }


    var maketime = t.currentMakeTime;
    if(typeof(version) == "undefined")
        version = t.currentVersion;
    var url=Url_Config.gridServiceUrl+"services/GridService/getGridInfo";
    $.ajax({
        data:{"para":"{element:'"+ element + "',departCode:'" + departCode + "',type:'" + type+ "',maketime:'" + maketime + "',version:'" + version  + "',datetime:'"+ datetime + "'}"},
        url:url,
        dataType:"json",
        success:function(data){

            for (var i = 0; i < data.length; i++) {
                t.datasetGridInfos.push(data[i]);
            }

            //获取产品状态（判断是否已提交）
            try {
                if (data != null) {
                    if(t.dataCache!=null){
                        var dataCaches = t.dataCache.getData(maketime, version, datetime, element);
                        if (dataCaches != null) {
                            for (var i = 0; i < data.length; i++) {
                                var gridInfo = data[i];
                                var dataCache = t.dataCache.getData(maketime, version, datetime, element, gridInfo.hourSpan);
                                if (dataCache != null && dataCache.status == 0) {
                                    var status = 0;
                                    if (typeof(gridInfo.userName) != "undefined" && gridInfo.userName != "") //已提交
                                    {
                                        status = 2;
                                        if (typeof(gridInfo.subjective) != "undefined" && gridInfo.subjective == 1) //已主观订正
                                            status = 4;
                                    }
                                    dataCache.status = status;
                                }
                            }
                        }
                    }
                }
            }
            catch(err)
            {
                alert(err.message);
            }

            recall&&recall(data);
        },
        type:"POST"
    });
};

/*
 * 获取（全部要素）格点产品信息
 */
GridProductClass.prototype.getGridInfos = function(recall, departCode, type, datetime, version, elements){
    var t = this;
    var maketime = t.currentMakeTime;
    if(typeof(version) == "undefined")
        version = t.currentVersion;
    var url=Url_Config.gridServiceUrl+"services/GridService/getGridInfos";
    $.ajax({
        data:{"para":"{departCode:'" + departCode + "',type:'" + type+ "',maketime:'" + maketime + "',version:'" + version  + "',datetime:'"+ datetime + "'}"},
        url:url,
        dataType:"json",
        success:function(data){
            for (var i = 0; i < data.length; i++) {
//                if(version == "r" && t.currentPost != null && t.currentPost.name == "首席岗") //首席调取预报结果，把预报产品信息作为首席审核产品信息//callR已经不用了，这里就不用了
//                {
//                    for(var hKey in hourspansNoData){
//                        if(data[i].hourSpan == hourspansNoData[hKey]){
//                            data[i].version = "p";
//                            break;
//                        }
//                    }
//                }
                t.datasetGridInfos.push(data[i]);
            }

            //获取产品状态（判断是否已提交）
            try {
                if (data != null && typeof(elements) != "undefined") {
                    for (var i = 0; i < elements.length; i++) {
                        var element = elements[i];
                        var hourSpans = GDYB.GDYBPage.getHourSpan(element);
                        if (hourSpans != null) {
                            for (var j = 0; j < hourSpans.length; j++) {
                                var hourSpan = hourSpans[j];
                                var dataCache = t.dataCache.getData(maketime, version, datetime, element, hourSpan);
                                if (dataCache != null && dataCache.status == 0) {
                                    var status = dataCache.status;
                                    var gridInfo = t.getGridInfoFromCache(type, element, datetime, hourSpan);
                                    if (gridInfo != null && typeof(gridInfo.userName) != "undefined" && gridInfo.userName != "") //已提交
                                    {
                                        status = 2;
                                        if (typeof(gridInfo.subjective) != "undefined" && gridInfo.subjective == 1) //已主观订正
                                            status = 4;
                                    }
                                    dataCache.status = status;
                                }
                            }
                        }
                    }
                }
            }
            catch(err)
            {
                alert(err.message);
            }

            recall&&recall(data);
        },
        type:"POST"
    });
};

/*
 *从缓存中获取产品信息
 * */
GridProductClass.prototype.getGridInfoFromCache = function(type, element, forecastTime, hourSpan){
    var result = null;
    var gridInfos = GDYB.GridProductClass.datasetGridInfos;
    var maketime = this.currentMakeTime;
    var version = this.currentVersion;
    if(gridInfos != null){
        for(var i=0; i<gridInfos.length; i++){
            if(gridInfos[i].type == type && gridInfos[i].element == element && gridInfos[i].makeTime == maketime && gridInfos[i].version == version && gridInfos[i].forecastTime == forecastTime && gridInfos[i].hourSpan == Number(hourSpan)){
                result = gridInfos[i];
                break;
            }
        }
    }
    return result;
}

/*
 *从缓存中（按要素）获取产品信息
 * */
GridProductClass.prototype.getGridInfosFromCache = function(type, element, forecastTime){
    var result = [];
    var gridInfos = GDYB.GridProductClass.datasetGridInfos;
    var maketime = this.currentMakeTime;
    var version = this.currentVersion;
    if(gridInfos != null){
        for(var i=0; i<gridInfos.length; i++){
            if(gridInfos[i].type == type && gridInfos[i].element == element && gridInfos[i].makeTime == maketime && gridInfos[i].version == version && gridInfos[i].forecastTime == forecastTime){
                result.push(gridInfos[i]);
            }
        }
    }
    return result;
}

//这个方法最好写到里面去，或者新增并包入方法UpdateLayer
GridProductClass.prototype.drawDengzhimian = function (recall, pMap) {
    var layerPolygons = pMap.getBy("layers","name","Polygon");
    var layerPolygon = layerPolygons[0];

    var t = this;
    var url = Url_Config.gridServiceUrl + "services/GridService/getIsoRegion";
    var element = t.currentElement;
    $.ajax({
        url: url,
        data:{"para":"{element:'"+ t.currentElement + "',type:'"+ (typeof(t.currentFromModel) == "undefined"?t.currentType:t.currentFromModel) + "',level:'"+ t.currentLevel + "',hourspan:"+ t.currentHourSpan + ",datetime:'"+ t.currentDateTime + "'}"},
        dataType: "json",
        success: function (data) {
            layerPolygon.removeAllFeatures();

            //获取填色风格
            var styles = null;
            if(element == "2t")
                styles = tempStyles;
            else if(element == "rh")
                styles = rhStyles;
            else if(element == "tcc")
                styles = tccStyles;
            else if(element == "r1" || element == "r3" || element == "r6" || element == "r12" || element == "r24")
                styles = rain24hStyles;

            //初始化数据
            //var result = WeatherMap.REST.Recordset.fromJson(data);
            var result = GDYB.FeatureUtilityClass.getRecordsetFromJson(data);
            var features = [];
            var len = result.features.length;
            for (var i = 0; i < len; i++) {
                var feature = result.features[i];
                var fAttributes = feature.attributes;
                fAttributes['FEATUREID'] = i;
                //var value = fAttributes['DMINVALUE'];
                var value = fAttributes['最小值'];
                if (value== undefined)
                    value = fAttributes['最小值'];
                //value = fAttributes['dMinZValue']; //如果服务端提取等值线采用setInterval，字段是DMINVALUE；反之，如果是setExpectedZValues，字段是dMinZValue
                if(styles != null)
                {
                    for(var j = 0;j < styles.length; j++)
                    {
                        var minValue = styles[j][0];
                        var maxValue = styles[j][1];
                        if(value >= minValue && value < maxValue) //因为value是取下限，所以这里是>=minValue
                        {
                            feature.style = styles[j][2];
                            break;
                        }
                    }
                }
                features.push(feature);
            }
            layerPolygon.addFeatures(features);
//            //移除图层
//            if(t.layerPolygonOld != null)
//                pMap.removeLayer(t.layerPolygonOld, false);
            recall&&recall();
        },
        error: function(e) {
            //t.nLastTime = 0;
            recall&&recall();
        },
        type: "POST"
    });
};

/*
* 显示等值线
* */
GridProductClass.prototype.addContour = function (recall, pMap) {
    var t = this;
    var dZValues = [];
    var dStep = 2.0;
    var element = t.currentElement;
    var type = t.currentType;
    var dStart = 0;
    var styles=null;
    if(element == "2t"){
        styles = heatMap_TempStyles;
        dStep = 2.0;
        dStart = Math.floor(t.datasetGrid.dMin);
    }
    else if(element == "r24") {
        dZValues = [0.1, 10.0, 25.0, 50.0, 100.0, 250.0];
    }
    else if(element == "uv"){
        styles = heatMap_10uvStyles;
    }
    if(styles!=null){
        for(var s=0;s<styles.length;s++){
            dZValues.push(styles[s].start);
        }
    }
    t.layerContour.removeAllFeatures();
    var timeStart=new Date().getTime();
    var contour = new WeatherMap.Analysis.Contour();
    var result = contour.analysis(t.datasetGrid, dZValues);
    var features = [];
    var resultSize=result.length;
    if(resultSize > 0){
        for(var i=0;i<resultSize;i++)
        {
            var geoline = result[i].geoline;
            var dZValue = result[i].dZValue;
            var feature = new WeatherMap.Feature.Vector(geoline);
            feature.attributes.dZValue = dZValue.toString();
            features.push(feature);
        }
    }
    t.layerContour.addFeatures(features);
    var timeEnd=new Date().getTime();
    console.log(timeEnd-timeStart);
    recall&&recall();
};

GridProductClass.prototype.updateGridProduct = function(value){
    var t = this;
    var feature = t.layerLuoqu.features[0];
//    feature.style = {
//        strokeColor: "#ff0000",
//        strokeWidth: 1.0,
//        fillColor: "#ff0000",
//        fillOpacity: "0"
//    };
//    t.layerLuoqu.refresh();
    var geometry = feature.geometry;
    var parts = geometry.components[0].components;
    var xy1 = [];
    for(var i=0;i<parts.length;i++){
        var point = parts[i];
        var x = point.x;
        var y = point.y;
        var xyStr = x+","+y;
        xy1.push(xyStr);
    }
    var xy2 = xy1.join(" ");
    var para = "{element:'"+ t.currentElement + "',type:'"+ t.currentType + "',hourspan:"+ t.currentHourSpan + ",datetime:'"+ t.currentDateTime +"',value:"+value+",coordinates:'"+xy2+"'}";
    //t.layerLuoqu.removeAllFeatures();
    var url=Url_Config.gridServiceUrl+"services/GridService/updateGridByRegion";
    $.ajax({
        data:{"para":para},
        url:url,
        dataType:"json",
        success:function(data){
//            //map.removeLayer(vectorLayer);
//            t.layerLabel.removeAllFeatures();
//            var bounds = GDYB.GDYBPage.map.getExtent();
//            var para = "{element:'"+ t.currentElement + "',type:'"+ t.currentType + "',hourspan:"+ t.currentHourSpan + ",datetime:'"+ t.currentDateTime  +"',left:"+bounds.left+", bottom:"+bounds.bottom+",right:"+bounds.right+", top:"+bounds.top+"}";
//            t.addLabel(null, GDYB.GDYBPage.map, para);
//            //if($("#checkbox1")[0].checked == true){
//            if(true){
//                t.drawDengzhimian(null, GDYB.GDYBPage.map);
//            }
            t.addGrid(null, GDYB.GDYBPage.map);
        },
        type:"POST"
    });
};

/*
 * 描述：通过画刷订正格点
 * 参数
 *    geoline：线条几何对象，描述画刷路径
 *    value：格点值
 * */
GridProductClass.prototype.updateGridbyBrush = function(geoline, value){
    var t = this;
    var dg = t.datasetGrid;

    var bounds = geoline.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= dg.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= dg.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if(nRight <= 0)
        return;
    else if(nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if(nBottom < 0)
        return;
    else if(nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    {
        var distance = 20; //影响距离20公里
        var count = Math.round(distance/111.1/dg.deltaX); //影响格点数
        //逐行扫描
        for (var i = nTop; i <= nBottom; i++) {
            var gridRow = dg.grid[i];
            var pt1 = gridRow[0];
            var pt2 = gridRow[gridRow.length - 1];
            var pts = this.getIntersections(pt1, pt2, geoline); //其实这个是（有宽度的）网格中位线（横向）与geoline的交点。
            //if (pts.length == 0) //理论上在geoline的bounds范围内一定会有交点。如果返回0个交点，则求网格中线（纵向）与与geoline的交点。//无论当前行与线条是否有交点，都要求纵向交点
            {
                var rowTop = dg.grid[dg.grid.length - 1];
                var rowBottom = dg.grid[0];
                for(var j=nLeft; j<=nRight; j++) {
                    pt1 = rowTop[j];
                    pt2 = rowBottom[j];
                    var ptsVertical = this.getIntersections(pt1, pt2, geoline);
                    if(ptsVertical.length > 0){
                        for (var key in ptsVertical) {
                            if(Math.abs(ptsVertical[key].y - gridRow[0].y) > dg.deltaY/2) //如果不在当前行
                                continue;
                            pts.push(ptsVertical[key]);
                        }
                    }
                }
            }
            if(pts.length == 0)
                continue;

            var arrayX = []; //所在列数
            var lineString = geoline;
            for (var j = 0; j < pts.length; j++) {
                //计算所在列数
                arrayX.push(Math.floor((pts[j].x - dg.left + dg.deltaX/2) / dg.deltaX));
            }

            //遍历本行的交点
            for (var j = 0; j < arrayX.length; j++) {
                var x0 = arrayX[j];
                var y0 = i;

                //以交点为中心，count为半径，遍历待计算格点
                for (var y = y0 - count; y <= y0 + count; y++) {
                    for (var x = x0 - count; x <= x0 + count; x++) {
                        if (y < 0 || y >= dg.rows || x < 0 || x >= dg.cols)
                            continue;
                        dg.grid[y][x].z = value;
                    }
                }
            }
        }
    }

    t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, t.currentHourSpan, 1); //更新已修改状态
    t.dataStack.push(t.datasetGrid); //压入堆栈
    t.updateStationLayer();
};

/*
 * 描述：风场订正
 * 参数
 *    geoline:线条几何对象，描述风向
 *    geoRegion：落区几何对象，影响范围
* */
GridProductClass.prototype.updateGridWind = function(geoline, geoRegion){
    var t = this;
    var dg = t.datasetGrid;

    var bounds = geoRegion == null?geoline.bounds : geoRegion.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= dg.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= dg.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if(nRight <= 0)
        return;
    else if(nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if(nBottom < 0)
        return;
    else if(nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    if(geoRegion != null) //按落区订正
    {
        function sortNumber(a,b)
        {
            return a - b;
        }
        for(var i = nTop; i<= nBottom; i++) {
            var gridRow = dg.grid[i];
            var pt1 = gridRow[0];
            var pt2 = gridRow[gridRow.length - 1];
            var pts = this.getIntersections(pt1, pt2, geoRegion);
            var arrayX = [];
            for (var j = 0; j < pts.length; j++) {
                arrayX.push((pts[j].x - dg.left) / dg.deltaX);
            }
            if (arrayX.length == 0)
                continue;
            var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

            for (var j = nLeft; j <= nRight; j++) {
                var pt = gridRow[j];
                if (j < arrayXSort[0])
                    continue;
                var k = arrayXSort.length - 1;
                for (k; k >= 0; k--) {
                    if (j >= arrayXSort[k]) {
                        break;
                    }
                }
                if (k % 2 != 0)
                    continue;

                //计算格点到直线的最小距离，及对应线段
                var x0 = gridRow[j].x;
                var y0 = gridRow[j].y;
                var ptMin1;
                var ptMin2;
                var dMin = dg.width;
                var lineString = geoline;
                for (var kk = 1; kk < lineString.components.length; kk++) {
                    var pt1 = lineString.components[kk - 1];
                    var pt2 = lineString.components[kk];
                    //代入直线方程两点式，得一般式（Ax0+By0+C=0）的A B C
                    var a = pt1.y - pt2.y;
                    var b = pt2.x - pt1.x;
                    var c = pt1.x*pt2.y - pt1.y*pt2.x;
                    //根据点到直线距离公式d=Math.abs(A*x+B*y+C)/Math.sqrt(A*A+B*B)，得
                    var d=Math.abs(a*x0+b*y0+c)/Math.sqrt(a*a+b*b);
                    if(d < dMin){
                        dMin = d;
                        ptMin1 = pt1;
                        ptMin2 = pt2;
                    }
                }
                var direction = Math.atan2(ptMin2.y - ptMin1.y, ptMin2.x - ptMin1.x);
                direction = 270.0 - direction / Math.PI * 180;
                gridRow[j].direction = direction;
            }
        }
    }
    else {
        var brushSize = GDYB.GDYBPage.myPanel_Tools.brushSize;
        //if(geoline.components.length == 2){ //如果是两点
        if(brushSize <= 0){
            var deltax = geoline.components[1].x - geoline.components[0].x;
            var deltay = geoline.components[1].y - geoline.components[0].y;
            var len = Math.sqrt(deltax*deltax + deltay*deltay);
            brushSize = Math.round(len/0.05/2);
        }
        var radius = Math.floor((brushSize-1)/2);
        //逐行扫描
        for (var i = nTop; i <= nBottom; i++) {
            var gridRow = dg.grid[i];
            var pt1 = gridRow[0];
            var pt2 = gridRow[gridRow.length - 1];
            var pts = this.getIntersections(pt1, pt2, geoline); //与横向水平线交点

            //与纵线交点
            var rowTop = dg.grid[dg.grid.length - 1];
            var rowBottom = dg.grid[0];
            for(var j=nLeft; j<=nRight; j++) {
                pt1 = rowTop[j];
                pt2 = rowBottom[j];
                var ptsVertical = this.getIntersections(pt1, pt2, geoline);
                if(ptsVertical.length > 0){
                    for (var key in ptsVertical) {
                        if(Math.abs(ptsVertical[key].y - gridRow[0].y) > dg.deltaY/2) //如果不在当前行
                            continue;
                        pts.push(ptsVertical[key]);
                    }
                }
            }

            if (pts.length == 0)
                continue;
            var arrayX = []; //所在列数
            var arrayD = []; //对应风向，角度
            var arrayA = []; //对应风向，弧度
            var lineString = geoline;
            for (var j = 0; j < pts.length; j++) {
                //计算所在列数
                arrayX.push(Math.floor((pts[j].x - dg.left) / dg.deltaX));
                //计算对应风向
                var arc = dg.noDataValue;
                var direction = dg.noDataValue;
                for (var k = 1; k < lineString.components.length; k++) {
                    var pt1 = lineString.components[k - 1];
                    var pt2 = lineString.components[k];
                    if ((pts[j].x - pt1.x) * (pts[j].x - pt2.x) < 0 && (pts[j].y - pt1.y) * (pts[j].y - pt2.y) < 0) {
                        direction = Math.atan2(pt2.y - pt1.y, pt2.x - pt1.x);
                        direction = 270.0 - direction / Math.PI * 180;
                        break;
                    }
                }
                arrayA.push(arc);
                arrayD.push(direction);
            }

            //遍历本行的交点
            for (var j = 0; j < arrayX.length; j++) {
                var x0 = arrayX[j];
                var y0 = i;
                var arc = arrayA[j];
                var direction = arrayD[j];
                if (direction == dg.noDataValue)
                    continue;

                var left = x0 - radius;
                var right = x0 + (brushSize-radius-1);
                var top = y0 - radius;
                var bottom = y0 + (brushSize-radius-1);
                for (var y = top; y <= bottom; y++) {
                    for (var x = left; x <= right; x++) {
                        if (y < 0 || y >= dg.rows || x < 0 || x >= dg.cols)
                            continue;
                        dg.grid[y][x].direction = direction;
                    }
                }
            }
        }
    }

    t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, t.currentHourSpan, 1); //更新已修改状态
    t.dataStack.push(t.datasetGrid); //压入堆栈
    t.updateStationLayer();
};

/*
 * 描述：更新格点值，落区中心点值也要随之变化
 * 参数：
 *   dvalue:值
 *   method：订正方法，0-统一赋值，1-统一加减值，2-统一增量
 * */
GridProductClass.prototype.updateGrid = function(value, method){
    var bMinIsZero = this.currentElement == "10uv" || this.currentElement == "wmax" || this.currentElement == "r1" || this.currentElement == "r3" || this.currentElement == "r6" || this.currentElement == "r12" || this.currentElement == "24";
    if(this.layerLuoqu != null && this.layerLuoqu.features.length > 0) { //按图上绘制的落区订正
        this.updateGridByRegion(value, method);

        //落区中心点（已知点）的值也要随之增减，便于进一步拖拽中心时，按照增减后的值进行插值
        if(this.layerLuoquCenter != null && this.layerLuoquCenter.features.length){
            this.updateLuoquCenter(value, method);
        }
    }
    else if(this.layerMagic != null && this.layerMagic.features.length > 0 && GDYB.MagicTool.arrayTag != null){ //按魔术棒拾取的落区订正
        GDYB.MagicTool.updateGrid(value, method);
        this.layerFillRangeColor.refresh();
    }
    else if(this.layerClimaticRegion != null && this.layerClimaticRegion.features.length > 0){ //按气候区划订正
        this.updateGridByClimaticRegion(value, method);
    }

    //this.dataCache.setDataStatus(this.currentDateTime, this.currentElement, this.currentHourSpan, 1); //更新已修改状态
};

/*
 * 描述：更新落区中心
 * 参数：
 *   dvalue:值
 *   method:订正方法
 *  */
GridProductClass.prototype.updateLuoquCenter = function(dvalue, method){
    var bMinIsZero = this.currentElement == "10uv" || this.currentElement == "wmax" || this.currentElement == "r1" || this.currentElement == "r3" || this.currentElement == "r6" || this.currentElement == "r12" || this.currentElement == "24";
    if(this.layerLuoquCenter.hasOwnProperty("borderValue"))
    {
        var targetValue = this.layerLuoquCenter.borderValue;
        if(method == 0) //统一赋值，value=x
        {
            targetValue = dvalue;
        }
        else if(method == 1) //统一加减值，value+=x
        {
            targetValue += dvalue;
        }
        else if(method == 2) //统一增量（百分比），value*=(1+x)
        {
            targetValue*=(1+dvalue);
        }
        if(bMinIsZero && targetValue < 0)
            targetValue = 0.0;
        targetValue = Math.floor(targetValue*10)/10;
        if(bMinIsZero && targetValue < 0)
            targetValue = 0;
        this.layerLuoquCenter.borderValue = targetValue;
    }
    for (var i = 0; i < this.layerLuoquCenter.features.length; i++) {
        var feature = this.layerLuoquCenter.features[i];
        var targetValue = feature.attributes.z;
        if(method == 0) //统一赋值，value=x
        {
            targetValue = dvalue;
        }
        else if(method == 1) //统一加减值，value+=x
        {
            targetValue += dvalue;
        }
        else if(method == 2) //统一增量（百分比），value*=(1+x)
        {
            targetValue*=(1+dvalue);
        }
        if(bMinIsZero && targetValue < 0)
            targetValue = 0.0;
        targetValue = Math.floor(targetValue*10)/10;
        if(bMinIsZero && targetValue < 0)
            targetValue = 0;
        feature.attributes.z = targetValue;
    }
};

/*
 * 描述：根据区域范围（落区）更新格点
 * 参数：
 *   dg：格点数据集
 *   gr:多边形几何对象
 *   dvalue:值
 *   method:订正方法
 *   element:要素名称
 * 返回：是否成功
 * */
GridProductClass.prototype.updateGridByRegion = function(value, method){
    var t = this;

    var feature = t.layerLuoqu.features[0];
    var geoRegion = feature.geometry;
    if(t.luoquCorrectJustOnStation)
        t.fillStation(t.datasetGrid, geoRegion, value, method, t.currentElement);
    else
        t.fillRegion(t.datasetGrid, geoRegion, value, method, t.currentElement);
    t.layerFillRangeColor.refresh();
//    t.addLabel(null, GDYB.Page.curPage.map, null);

    t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, t.currentHourSpan, 1);  //更新已修改状态
    t.dataStack.push(t.datasetGrid); //压入堆栈
    t.updateStationLayer();
};

/*
 * 描述：根据区域范围（落区）+重心，采用距离反比权重插值，更新格点
 * 参数：
 *   geoRegion：落区多边形
 *   geoPoint：重心
 *   valueDown：格点值下限
 *   valueUp:格点值上限
 * 返回：是否成功
 * */
GridProductClass.prototype.updateGridByIDW = function(geoRegion){
    var t = this;
    if(t.layerLuoquCenter.features.length > 0) {
        var xyz = [];
        //获取已知点
        for(var i=0; i<t.layerLuoquCenter.features.length; i++)
            xyz.push({x:t.layerLuoquCenter.features[i].geometry.x,
                y:t.layerLuoquCenter.features[i].geometry.y,
                z:t.layerLuoquCenter.features[i].attributes.z});

        var borderValue = t.layerLuoquCenter.borderValue;

        if(t.luoquCorrectJustOnStation)
            t.fillStationByIDW(t.datasetGrid, geoRegion, xyz, borderValue, t.currentElement);
        else
            t.fillRegionByIDW(t.datasetGrid, geoRegion, xyz, borderValue, t.currentElement);
        t.layerFillRangeColor.refresh();
//        t.addLabel(null, GDYB.Page.curPage.map, null);

        t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, t.currentHourSpan, 1); //更新已修改状态
        t.dataStack.push(t.datasetGrid); //压入堆栈
        t.updateStationLayer();
    }
};

/*
 * 描述：根据区域范围（落区）更新格点，保持空间分布
 * 参数：
 *   dg：格点数据集
 *   gr:多边形几何对象
 *   dvalue:值
 *   method:订正方法
 *   element:要素名称
 * 返回：是否成功
 * */
GridProductClass.prototype.updateGridBySpatial = function(valueDown, valueUp){
    var t = this;
    if(t.datasetGrid == null)
        return;
    if(valueDown == t.datasetGrid.noDataValue || valueUp == t.datasetGrid.noDataValue)
        return;
    var feature = t.layerLuoqu.features[0];
    var geoRegion = feature.geometry;
    if(t.luoquCorrectJustOnStation)
        t.fillStationSpatial(t.datasetGrid, geoRegion, valueDown, valueUp, t.currentElement);
    else
        t.fillRegionSpatial(t.datasetGrid, geoRegion, valueDown, valueUp, t.currentElement);
    //t.layerLuoqu.removeAllFeatures();
    t.layerFillRangeColor.refresh();
//    t.addLabel(null, GDYB.Page.curPage.map, null);

    t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, t.currentHourSpan, 1); //更新已修改状态
    t.dataStack.push(t.datasetGrid); //压入堆栈
    t.updateStationLayer();
};

/*
 * 描述：根据气候区划更新格点
 * 参数：
 *   dvalue:值
 *   method:订正方法
 * 返回：是否成功
 * */
GridProductClass.prototype.updateGridByClimaticRegion = function(value, method){
    var t = this;
    var feature = t.layerClimaticRegion.features[0];
    var geoRegion = feature.geometry;
    t.fillRegion(t.datasetGrid, geoRegion, value, method, t.currentElement);
    t.layerFillRangeColor.refresh();
//    t.addLabel(null, GDYB.Page.curPage.map, null);

    t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, t.currentHourSpan, 1); //更新已修改状态
    t.dataStack.push(t.datasetGrid); //压入堆栈
    t.updateStationLayer();
};

/*
 * 描述：根据区域订正格点
 * 参数：
 *   dg：格点数据集
 *   gr:多边形几何对象
 *   dvalue:值
 *   method:订正方法，0-统一赋值，1-统一加减值，2-统一增量
 *   element:要素名称
 *   isWindDirection：是否订正风向（仅用于趋势订正）
 * 返回：是否成功
 * */
GridProductClass.prototype.fillRegion = function(dg, gr, dvalue, method, element, isWindDirection){
        var bMinIsZero = element == "10uv" || element == "wmax" || element == "r1" || element == "r3" || element == "r6" || element == "r12" || element == "r24"; //风、降水不能为负
    if(method == 0 && dvalue<0 && bMinIsZero)
        return false;

    var result = false;
    if (dg == null || gr == null)
        return result;

    var bounds = gr.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= dg.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= dg.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if(nRight <= 0)
        return;
    else if(nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if(nBottom < 0)
        return;
    else if(nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    var isWD = (element=="wmax" || element == "10uv") && typeof(isWindDirection)!="undefined"&&isWindDirection;

    //方案二，逐行计算，判断与落区边界交点
    /**add by wangkun**/
    var items=this.layerFillRangeColor.items;
    var dicItems = new Array();
    for(var i=0;i<items.length;i++){
        var caption=items[i].caption;
        var start=items[i].start;
        dicItems[caption]=start;
    }
    function sortNumber(a,b)
    {
        return a - b;
    }
    for(var i = nTop; i<= nBottom; i++)
    {
        var pt1 = dg.gridToXY(0, i);
        var pt2 = dg.gridToXY(dg.cols - 1, i);
        var pts = this.getIntersections(pt1, pt2, gr);
        var arrayX = [];
        for(var j = 0; j<pts.length; j++){
            var x = (pts[j].x - dg.left) / dg.deltaX;
            if(arrayX.indexOf(x)>=0)
                continue;
            arrayX.push(x);
        }
        if(arrayX.length == 0)
            continue;
        var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

        for(var j = nLeft; j<=nRight; j++)
        {
            if((j+0.5) < arrayXSort[0])
                continue;
            var k = arrayXSort.length - 1;
            for(k; k>=0; k--){
                if((j+0.5)>=arrayXSort[k]){
                    break;
                }
            }
            if(k%2 != 0)
                continue;

            var targetValue = dg.getValue(0, j, i);
            if(method == 0) //统一赋值，value=x
            {
                targetValue = dvalue;
            }
            else if(method == 1) //统一加减值，value+=x
            {
                targetValue += dvalue;
            }
            else if(method == 2) //统一增量（百分比），value*=(1+x)
            {
                targetValue*=(1+dvalue);
            }
            else if(method==3){//值叠加,add by wangkun,2016.07.15
                if(dvalue==undefined){
                    var tt=dvalue;
                }
                if(targetValue==0&&dvalue!=0){//原值为0，新值不为0，则把新值赋给原值
                    targetValue=dvalue;
                }
                else if(targetValue!=0&&dvalue!=0&&dvalue!=dg.noDataValue){//原值各新值都不为零，则组合
                    var oldCaption=oldCaption=items[targetValue].caption;
                    var aOldCaptions=oldCaption.split("、");
                    var newCaption=items[dvalue].caption;
                    if(ArrayIsContain(aOldCaptions,newCaption))
                        continue;
                    aOldCaptions.push(newCaption);
                    aOldCaptions.sort();
                    var caption="";
                    for(var a=0;a<aOldCaptions.length;a++){
                        if(a==aOldCaptions.length-1){
                            caption+=aOldCaptions[a];
                        }
                        else{
                            caption+=aOldCaptions[a]+"、";
                        }
                    }
                    newVal=dicItems[caption];
                    targetValue=newVal;
                }
            }
            if(bMinIsZero && targetValue < 0)
                targetValue = 0.0;
            targetValue = Math.floor(targetValue*10)/10;
            if(bMinIsZero && targetValue < 0)
                targetValue = 0;
            if(isWD)
                dg.setValue(1, j, i, targetValue);
            else
                dg.setValue(0, j, i, targetValue);
        }
    }
};

/*
 * 描述：根据区域订正（站点所在）格点
 * 参数：
 *   dg：格点数据集
 *   gr:多边形几何对象
 *   dvalue:值
 *   method:订正方法，0-统一赋值，1-统一加减值，2-统一增量
 *   element:要素名称
 *   isWindDirection：是否订正风向（仅用于趋势订正）
 * 返回：是否成功
 * */
GridProductClass.prototype.fillStation = function(dg, gr, dvalue, method, element, isWindDirection){
    var bMinIsZero = element == "10uv" || element == "wmax" || element == "r1" || element == "r3" || element == "r6" || element == "r12" || element == "r24"; //风、降水不能为负
    if(method == 0 && dvalue<0 && bMinIsZero)
        return false;

    var result = false;
    if (dg == null || gr == null)
        return result;

    var features  = this.getCurrentVisibleStations();
    if(features == null || features.length == 0)
        return result;

    var isWD = (element=="wmax" || element == "10uv") && typeof(isWindDirection)!="undefined"&&isWindDirection;

    for(var key in features) {
        var geometry = features[key].geometry;
        var geoPoint = new WeatherMap.Geometry.Point(geometry.x, geometry.y);
        if (!gr.containsPoint(geoPoint))
            continue;
        var cell = dg.xyToGrid(geoPoint.x, geoPoint.y);
        var i = cell.y;
        var j = cell.x;

        var targetValue = dg.grid[i][j].z;
        if(method == 0) //统一赋值，value=x
        {
            targetValue = dvalue;
        }
        else if(method == 1) //统一加减值，value+=x
        {
            targetValue += dvalue;
        }
        else if(method == 2) //统一增量（百分比），value*=(1+x)
        {
            targetValue*=(1+dvalue);
        }
        if(bMinIsZero && targetValue < 0)
            targetValue = 0.0;
        targetValue = Math.floor(targetValue*10)/10;
        if(bMinIsZero && targetValue < 0)
            targetValue = 0;
        if(isWD)
            dg.grid[i][j].direction = targetValue;
        else
            dg.grid[i][j].z = targetValue;
    }
};

/*
 * 描述：根据反距离法订正格点
 * 参数：
 *   dg：格点数据集
 *   gr:多边形落区几何对象
 *   xyz:已知点的值
 *   borderValue：边界值
 *   element:要素名称
 * 返回：是否成功
 * */
GridProductClass.prototype.fillRegionByIDW = function(dg, gr, xyz, borderValue, element){
    if(dg == null)
        return;
    if(xyz.length < 1)
    {
        alert("已知个数不能小于1");
        return;
    }
    var dMinValue = Math.abs(dg.noDataValue); //最小值
    var dMaxValue = Math.abs(dg.noDataValue) * -1; //最大值
    var bExistNoDataValue = false; //存在无效值
    for(var k=0; k<xyz.length; k++){
        if(xyz[k].z == dg.noDataValue) {
            bExistNoDataValue = true;
            break;
        }
        if(xyz[k].z < dMinValue)
            dMinValue = xyz[k].z;
        if(xyz[k].z > dMaxValue)
            dMaxValue = xyz[k].z;
    }
    if(bExistNoDataValue)
    {
        alert("请设置正确的值域");
        return false;
    }
    if(dMinValue < 0 && (element == "R1" || element == "R3" || element == "R6" || element == "R12" || element == "R24"))
    {
        alert("降水不能为负");
        return false;
    }

    var isWind = element=="10uv"||element=="wmax";

    var result = false;
    if (dg == null || gr == null)
        return result;

    var bounds = gr.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= dg.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= dg.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if(nRight <= 0)
        return;
    else if(nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if(nBottom < 0)
        return;
    else if(nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    function sortNumber(a,b)
    {
        return a - b;
    }

    //获取所有边界点，其值设置为最小值borderValue，作为已知点（但取最近的一个边界点作为已知点代入）
    var xyzBorder = [];
    if(borderValue != dg.noDataValue) {
        for (var i = nTop; i <= nBottom; i++) {
            var gridRow = dg.grid[i];
            var pt1 = gridRow[0];
            var pt2 = gridRow[gridRow.length - 1];
            var pts = this.getIntersections(pt1, pt2, gr);
            for (var j = 0; j < pts.length; j++) {
                xyzBorder.push({x:pts[j].x, y:pts[j].y, z:borderValue});
            }
        }
    }

    //添加tag标识
    var tag = null;
    if(typeof(this.currentGridTag)!="undefined" && typeof(dg.tag)=="undefined")
        this.addTag(dg);
    if(typeof(dg.tag)!="undefined"){
        if(typeof(this.currentGridTag)!="undefined")
            tag = Number(this.currentGridTag);
        else
            tag = dg.defaultTag;
    }

    var deltaValue = dMaxValue - dMinValue;
    for(var i = nTop; i<= nBottom; i++)
    {
        var gridRow = dg.grid[i];
        var pt1 = gridRow[0];
        var pt2 = gridRow[gridRow.length - 1];
        var pts = this.getIntersections(pt1, pt2, gr);
        var arrayX = [];
        for(var j = 0; j<pts.length; j++){
            arrayX.push((pts[j].x - dg.left) / dg.deltaX);
        }
        if(arrayX.length == 0)
            continue;
        var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

        for(var j = nLeft; j<=nRight; j++)
        {
            var pt = gridRow[j];
            //var geoPoint = new WeatherMap.Geometry.Point(pt.x, pt.y);
            if(j < arrayXSort[0])
                continue;
            var k = arrayXSort.length - 1;
            for(k; k>=0; k--){
                if(j>=arrayXSort[k]){
                    break;
                }
            }
            if(k%2 != 0)
                continue;

            //1.获取未知点
            var pt = dg.gridToXY(j, i);
            //2.计算未知点到所有点的距离（的倒数）
            var arryV = [];
            var arrayD = []; //距离倒数数组
            var totalD = 0.0; //距离倒数之和
            for(var k=0; k<xyz.length; k++){
                var pt0 = xyz[k];
                var distance = Math.sqrt((pt0.y - pt.y)*(pt0.y - pt.y)+(pt0.x - pt.x)*(pt0.x - pt.x));
                arrayD.push(1/distance);
                totalD+=(1/distance);
                arryV.push(xyz[k].z);
            }

            //3.取最近的一个边界点作为已知点代入（注意不能全部边界点代入计算，因为量很大，其权重势必很高，导致大部分格点解决边界值）
            var xyzNearest  = null;
            var minDistance = dg.width;
            for(var k=0; k<xyzBorder.length; k++){
                var pt0 = xyzBorder[k];
                var distance = Math.sqrt((pt0.y - pt.y)*(pt0.y - pt.y)+(pt0.x - pt.x)*(pt0.x - pt.x));
                if(distance < minDistance)
                {
                    xyzNearest = xyzBorder[k];
                    minDistance = distance;
                }
            }
            arrayD.push(1/minDistance);
            totalD+=(1/minDistance);
            arryV.push(xyzNearest.z);

            //4.计算权重，并计算值
            var v = 0.0;
            for(var k=0; k<arrayD.length; k++){
                var weight = arrayD[k]/totalD;  //计算每个点的权重：权重是距离的倒数的函数
                v+=weight*arryV[k]; //格点值=权重*值再求和
            }
            gridRow[j].z = Math.floor(v*10)/10;
            if(isWind && GDYB.GridProductClass.currentWindDirection != null)
                gridRow[j].direction = GDYB.GridProductClass.currentWindDirection;
            if(tag != null)
                dg.tag[i][j]= tag;
        }
    }
};

/*
 * 描述：根据反距离法订正（站点所在）格点
 * 参数：
 *   dg：格点数据集
 *   gr:多边形落区几何对象
 *   xyz:已知点的值
 *   borderValue：边界值
 *   element:要素名称
 * 返回：是否成功
 * */
GridProductClass.prototype.fillStationByIDW = function(dg, gr, xyz, borderValue, element){
    if(dg == null)
        return;
    if(xyz.length < 1)
    {
        alert("已知个数不能小于1");
        return;
    }
    var dMinValue = Math.abs(dg.noDataValue); //最小值
    var dMaxValue = Math.abs(dg.noDataValue) * -1; //最大值
    var bExistNoDataValue = false; //存在无效值
    for(var k=0; k<xyz.length; k++){
        if(xyz[k].z == dg.noDataValue) {
            bExistNoDataValue = true;
            break;
        }
        if(xyz[k].z < dMinValue)
            dMinValue = xyz[k].z;
        if(xyz[k].z > dMaxValue)
            dMaxValue = xyz[k].z;
    }
    if(bExistNoDataValue)
    {
        alert("请设置正确的值域");
        return false;
    }
    if(dMinValue < 0 && (element == "R1" || element == "R3" || element == "R6" || element == "R12" || element == "R24"))
    {
        alert("降水不能为负");
        return false;
    }

    var isWind = element=="10uv"||element=="wmax";

    var result = false;
    if (dg == null || gr == null)
        return result;

    var features  = this.getCurrentVisibleStations();
    if(features == null || features.length == 0)
        return result;

    var bounds = gr.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= dg.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= dg.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if(nRight <= 0)
        return;
    else if(nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if(nBottom < 0)
        return;
    else if(nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    function sortNumber(a,b)
    {
        return a - b;
    }

    //获取所有边界点，其值设置为最小值borderValue，作为已知点（但取最近的一个边界点作为已知点代入）
    var xyzBorder = [];
    if(borderValue != dg.noDataValue) {
        for (var i = nTop; i <= nBottom; i++) {
            var gridRow = dg.grid[i];
            var pt1 = gridRow[0];
            var pt2 = gridRow[gridRow.length - 1];
            var pts = this.getIntersections(pt1, pt2, gr);
            for (var j = 0; j < pts.length; j++) {
                xyzBorder.push({x:pts[j].x, y:pts[j].y, z:borderValue});
            }
        }
    }

    //添加tag标识
    var tag = null;
    if(typeof(this.currentGridTag)!="undefined" && typeof(dg.tag)=="undefined")
        this.addTag(dg);
    if(typeof(dg.tag)!="undefined"){
        if(typeof(this.currentGridTag)!="undefined")
            tag = Number(this.currentGridTag);
        else
            tag = dg.defaultTag;
    }

    var deltaValue = dMaxValue - dMinValue;
    for(var key in features) {
        var geometry = features[key].geometry;
        var geoPoint = new WeatherMap.Geometry.Point(geometry.x, geometry.y);
        if (!gr.containsPoint(geoPoint))
            continue;
        var cell = dg.xyToGrid(geoPoint.x, geoPoint.y);
        var i = cell.y;
        var j = cell.x;

        //1.获取未知点
        var pt = dg.gridToXY(j, i);
        //2.计算未知点到所有点的距离（的倒数）
        var arryV = [];
        var arrayD = []; //距离倒数数组
        var totalD = 0.0; //距离倒数之和
        for(var k=0; k<xyz.length; k++){
            var pt0 = xyz[k];
            var distance = Math.sqrt((pt0.y - pt.y)*(pt0.y - pt.y)+(pt0.x - pt.x)*(pt0.x - pt.x));
            arrayD.push(1/distance);
            totalD+=(1/distance);
            arryV.push(xyz[k].z);
        }

        //3.取最近的一个边界点作为已知点代入（注意不能全部边界点代入计算，因为量很大，其权重势必很高，导致大部分格点解决边界值）
        var xyzNearest  = null;
        var minDistance = dg.width;
        for(var k=0; k<xyzBorder.length; k++){
            var pt0 = xyzBorder[k];
            var distance = Math.sqrt((pt0.y - pt.y)*(pt0.y - pt.y)+(pt0.x - pt.x)*(pt0.x - pt.x));
            if(distance < minDistance)
            {
                xyzNearest = xyzBorder[k];
                minDistance = distance;
            }
        }
        arrayD.push(1/minDistance);
        totalD+=(1/minDistance);
        arryV.push(xyzNearest.z);

        //4.计算权重，并计算值
        var v = 0.0;
        for(var k=0; k<arrayD.length; k++){
            var weight = arrayD[k]/totalD;  //计算每个点的权重：权重是距离的倒数的函数
            v+=weight*arryV[k]; //格点值=权重*值再求和
        }
        dg.grid[i][j].z = Math.floor(v*10)/10;
        if(isWind && GDYB.GridProductClass.currentWindDirection != null)
            dg.grid[i][j].direction = GDYB.GridProductClass.currentWindDirection;
        if(tag != null)
            dg.tag[i][j]= tag;
    }
};

/*
 * 描述：空间分布正比法订正格点，维持空间分布趋势
 * 参数：
 *   dg：格点数据集
 *   gr:多边形几何对象
 *   dvalueDown:目标格点值下限
 *   dvalueUp:目标格点值上限
 *   element:要素名称
 * 返回：是否成功
 * */
GridProductClass.prototype.fillRegionSpatial = function(dg, gr, dvalueDown, dvalueUp, element){
        if((element == "R1" || element == "R3" || element == "R6" || element == "R12" || element == "R24") && dvalueDown<0) //降水不能为负
        return false;

    var result = false;
    if (dg == null || gr == null)
        return result;

    var bounds = gr.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= dg.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= dg.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if(nRight <= 0)
        return;
    else if(nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if(nBottom < 0)
        return;
    else if(nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    function sortNumber(a,b)
    {
        return a - b;
    }

    //求落区内格点最大、最小值
    var extreme = this.getExtremebyRegion(dg, gr);
    var minValue = extreme[0].z;
    var maxValue = extreme[1].z;

    var isWind = element=="10uv"||element=="wmax";

    //添加tag标识
    var tag = null;
    if(typeof(this.currentGridTag)!="undefined" && typeof(dg.tag)=="undefined")
        this.addTag(dg);
    if(typeof(dg.tag)!="undefined"){
        if(typeof(this.currentGridTag)!="undefined")
            tag = Number(this.currentGridTag);
        else
            tag = dg.defaultTag;
    }

    //赋值
    for(var i = nTop; i<= nBottom; i++)
    {
        var pt1 = dg.gridToXY(0, i);
        var pt2 = dg.gridToXY(dg.cols - 1, i);
        var pts = this.getIntersections(pt1, pt2, gr);
        var arrayX = [];
        for(var j = 0; j<pts.length; j++){
            var x = (pts[j].x - dg.left) / dg.deltaX;
            if(arrayX.indexOf(x)>=0)
                continue;
            arrayX.push(x);
        }
        if(arrayX.length == 0)
            continue;
        var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

        for(var j = nLeft; j<=nRight; j++)
        {
//            var pt = gridRow[j];
            //var geoPoint = new WeatherMap.Geometry.Point(pt.x, pt.y);
            //if(!gr.containsPoint(geoPoint))
            //    continue;
            if((j+0.5) < arrayXSort[0])
                continue;
            var k = arrayXSort.length - 1;
            for(k; k>=0; k--){
                if((j+0.5)>=arrayXSort[k]){
                    break;
                }
            }
            if(k%2 != 0)
                continue;

            if(maxValue == minValue){
                dg.setValue(0, j, i, dvalueDown);
                if(isWind && GDYB.GridProductClass.currentWindDirection != null)
                    dg.setValue(1, j, i, GDYB.GridProductClass.currentWindDirection);
                if(tag != null)
                    dg.tag[i][j]= tag;
            }
            else{
                var v = (dg.getValue(0, j, i)-minValue)/(maxValue-minValue)*(dvalueUp-dvalueDown)+dvalueDown;
                dg.setValue(0, j, i, Math.floor(v*10)/10);
                if(isWind && GDYB.GridProductClass.currentWindDirection != null)
                    dg.setValue(1, j, i, GDYB.GridProductClass.currentWindDirection);
                if(tag != null)
                    dg.tag[i][j]= tag;
            }
        }
    }
};

/*
 * 描述：空间分布正比法订正（站点所在）格点，维持空间分布趋势
 * 参数：
 *   dg：格点数据集
 *   gr:多边形几何对象
 *   dvalueDown:目标格点值下限
 *   dvalueUp:目标格点值上限
 *   element:要素名称
 * 返回：是否成功
 * */
GridProductClass.prototype.fillStationSpatial = function(dg, gr, dvalueDown, dvalueUp, element) {
    if ((element == "R1" || element == "R3" || element == "R6" || element == "R12" || element == "R24") && dvalueDown < 0) //降水不能为负
        return false;

    var result = false;
    if (dg == null || gr == null)
        return result;

    var features  = this.getCurrentVisibleStations();
    if(features == null || features.length == 0)
        return result;

    //求落区内格点最大、最小值
    var extreme = this.getExtremebyRegion(dg, gr);
    var minValue = extreme[0].z;
    var maxValue = extreme[1].z;

    var isWind = element=="10uv"||element=="wmax";

    //添加tag标识
    var tag = null;
    if(typeof(this.currentGridTag)!="undefined" && typeof(dg.tag)=="undefined")
        this.addTag(dg);
    if(typeof(dg.tag)!="undefined"){
        if(typeof(this.currentGridTag)!="undefined")
            tag = Number(this.currentGridTag);
        else
            tag = dg.defaultTag;
    }

    for(var key in features){
        var geometry = features[key].geometry;
        var geoPoint = new WeatherMap.Geometry.Point(geometry.x, geometry.y);
        if(!gr.containsPoint(geoPoint))
            continue;
        var cell = dg.xyToGrid(geoPoint.x, geoPoint.y);
        if(maxValue == minValue){
            dg.grid[cell.y][cell.x].z = dvalueDown;
            if(isWind && GDYB.GridProductClass.currentWindDirection != null)
                dg.grid[cell.y][cell.x].direction = GDYB.GridProductClass.currentWindDirection;
            if(tag != null)
                dg.tag[cell.y][cell.x]= tag;
        }
        else{
            var v = (dg.grid[cell.y][cell.x].z-minValue)/(maxValue-minValue)*(dvalueUp-dvalueDown)+dvalueDown;
            dg.grid[cell.y][cell.x].z = Math.floor(v*10)/10;
            if(isWind && GDYB.GridProductClass.currentWindDirection != null)
                dg.grid[cell.y][cell.x].direction = GDYB.GridProductClass.currentWindDirection;
            if(tag != null)
                dg.tag[cell.y][cell.x]= tag;
        }
    }
};

/*
 * 描述：采用线性关系订正格点（已知点-->最远的点之间，根据）
 * 参数：
 *   dg：格点数据集
 *   gr：多边形几何对象，描述落区
 *   gp：中心点几何对象
 *   valueDown:最小值
 *   valueUp： 最大值
 *   element:要素名称
 * 返回：是否成功
 * */
GridProductClass.prototype.fillRegionByLinear = function(dg, gr, gp, valueDown, valueUp, element){
        if(valueDown<0 && (element == "R1" || element == "R3" || element == "R6" || element == "R12" || element == "R24"))
    {
        alert("降水不能为负");
        return false;
    }
    if(valueDown == dg.noDataValue || valueUp == dg.noDataValue)
    {
        alert("请设置正确的值域");
        return false;
    }

    var result = false;
    if (dg == null || gr == null)
        return result;

    var bounds = gr.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= dg.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= dg.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if(nRight <= 0)
        return;
    else if(nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if(nBottom < 0)
        return;
    else if(nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    function sortNumber(a,b)
    {
        return a - b;
    }

    //计算离重心最远距离，该格点一定在落区边界上
    var maxDistance = 0.0;
    for(var i = nTop; i<= nBottom; i++)
    {
        var pt1 = dg.gridToXY(0, i);
        var pt2 = dg.gridToXY(dg.cols - 1, i);
        var pts = this.getIntersections(pt1, pt2, gr);
        var arrayX = [];
        for(var j = 0; j<pts.length; j++){
            var x = (pts[j].x - dg.left) / dg.deltaX;
            if(arrayX.indexOf(x)>=0)
                continue;
            arrayX.push(x);
        }
        if(arrayX.length == 0)
            continue;
        var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

        for(var j=0; j<arrayXSort.length; j++)
        {
            var pt = dg.gridToXY(arrayXSort[j], i);
            var distance = Math.sqrt((gp.y - pt.y)*(gp.y - pt.y)+(gp.x - pt.x)*(gp.x - pt.x));
            if(distance>maxDistance)
                maxDistance=distance;
        }
    }

    if(maxDistance == 0.0)
        return;

    var deltaValue = valueUp - valueDown;
    for(var i = nTop; i<= nBottom; i++)
    {
        var pt1 = dg.gridToXY(0, i);
        var pt2 = dg.gridToXY(dg.cols - 1, i);
        var pts = this.getIntersections(pt1, pt2, gr);
        var arrayX = [];
        for(var j = 0; j<pts.length; j++){
            var x = (pts[j].x - dg.left) / dg.deltaX;
            if(arrayX.indexOf(x)>=0)
                continue;
            arrayX.push(x);
        }
        if(arrayX.length == 0)
            continue;
        var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

        for(var j = nLeft; j<=nRight; j++)
        {
//            var pt = gridRow[j];
//            var geoPoint = new WeatherMap.Geometry.Point(pt.x, pt.y);
            if((j+0.5) < arrayXSort[0])
                continue;
            var k = arrayXSort.length - 1;
            for(k; k>=0; k--){
                if((j+0.5)>=arrayXSort[k]){
                    break;
                }
            }
            if(k%2 != 0)
                continue;

            var pt = dg.gridToXY(j, i);
            var distance = Math.sqrt((gp.y - pt.y)*(gp.y - pt.y)+(gp.x - pt.x)*(gp.x - pt.x));
            var v  = valueDown + deltaValue*(1-distance/maxDistance);
            dg.setValue(0, j, i, Math.floor(v*10)/10);

            if(GDYB.MagicTool.arrayTag != null)
                GDYB.MagicTool.arrayTag[i][j] = true;
        }
    }
};

/*
 * 描述：获取落区内格点最大、最小值，及其行列
 * 参数：
 *   dg：格点数据集
 *   gr:多边形几何对象
 * 返回：数组，第一个元素为最小值，第二个元素为最大值
 * */
GridProductClass.prototype.getExtremebyRegion = function(dg, gr){
        var result = [];
    if (dg == null || gr == null)
        return result;

    var bounds = gr.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if(nLeft < 0)
        nLeft = 0;
    else if(nLeft >= dg.cols)
        return result;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if(nTop < 0)
        nTop = 0;
    else if(nTop >= dg.rows)
        return result;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if(nRight <= 0)
        return result;
    else if(nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if(nBottom < 0)
        return result;
    else if(nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    function sortNumber(a,b)
    {
        return a - b;
    }

    var minValue = Math.abs(dg.noDataValue);
    var maxValue = -Math.abs(dg.noDataValue);
    var minValueX = 0;
    var minValueY = 0;
    var maxValueX = 0;
    var maxValueY = 0;
    for(var i = nTop; i<= nBottom; i++)
    {
        var pt1 = dg.gridToXY(0, i);
        var pt2 = dg.gridToXY(dg.cols - 1, i);
        var pts = this.getIntersections(pt1, pt2, gr);
        var arrayX = [];
        for(var j = 0; j<pts.length; j++){
            var x = (pts[j].x - dg.left) / dg.deltaX;
            if(arrayX.indexOf(x)>=0)
                continue;
            arrayX.push(x);
        }
        if(arrayX.length == 0)
            continue;
        var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

        for(var j = nLeft; j<=nRight; j++)
        {
//            var pt = gridRow[j];
            //var geoPoint = new WeatherMap.Geometry.Point(pt.x, pt.y);
            if((j+0.5) < arrayXSort[0])
                continue;
            var k = arrayXSort.length - 1;
            for(k; k>=0; k--){
                if((j+0.5)>=arrayXSort[k]){
                    break;
                }
            }
            if(k%2 != 0)
                continue;
            var value = dg.getValue(0, j, i);
            if(value == dg.noDataValue)
                continue;
            if(value < minValue)
            {
                minValue = value;
                minValueX = j;
                minValueY = i;
            }
            if(value > maxValue)
            {
                maxValue = value;
                maxValueX = j;
                maxValueY = i;
            }
        }
    }
    result.push({x:minValueX, y:minValueY, z:minValue});
    result.push({x:maxValueX, y:maxValueY, z:maxValue});
    return result;
};

/*
 * 描述：获取落区内离已知点最远（距离的）格点
 * 参数：
 *   dg：格点数据集
 *   gr:多边形落区几何对象
 *   pt:已知点
 * 返回：最远格点及其距离
 * */
GridProductClass.prototype.getFurthestGridInRegion = function(dg, gr, pt0) {
    if (dg == null || gr == null)
        return;

    var bounds = gr.bounds;
    var nLeft = (bounds.left - dg.left) / dg.deltaX;
    if (nLeft < 0)
        nLeft = 0;
    else if (nLeft >= dg.cols)
        return;
    else
        nLeft = Math.floor(nLeft);

    var nTop = (dg.top - bounds.top) / dg.deltaX;
    if (nTop < 0)
        nTop = 0;
    else if (nTop >= dg.rows)
        return;
    else
        nTop = Math.floor(nTop);

    var nRight = (bounds.right - dg.left) / dg.deltaX;
    if (nRight <= 0)
        return;
    else if (nRight >= dg.cols)
        nRight = dg.cols - 1;
    else
        nRight = Math.floor(nRight);

    var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
    if (nBottom < 0)
        return;
    else if (nBottom >= dg.rows)
        nBottom = dg.rows - 1;
    else
        nBottom = Math.floor(nBottom);

    function sortNumber(a, b) {
        return a - b;
    }

    var result = {x:0,y:0,distance:0.0};
    for (var i = nTop; i <= nBottom; i++) {
        var gridRow = dg.grid[i];
        var pt1 = gridRow[0];
        var pt2 = gridRow[gridRow.length - 1];
        var pts = this.getIntersections(pt1, pt2, gr);
        var arrayX = [];
        for (var j = 0; j < pts.length; j++) {
            arrayX.push((pts[j].x - dg.left) / dg.deltaX);
        }
        if (arrayX.length == 0)
            continue;
        var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

        for (var j = 0; j < arrayXSort.length; j++) {
            var pt = dg.gridToXY(arrayXSort[j], i);
            var distance = Math.sqrt((pt0.y - pt.y) * (pt0.y - pt.y) + (pt0.x - pt.x) * (pt0.x - pt.x));
            if (distance > result.distance)
            {
                result.distance = distance;
                result.x = arrayXSort[j];
                result.y = i;
            }
        }
    }
    return result;
};

//提交（一个时效的）产品
GridProductClass.prototype.saveGridProduct = function(type, userName, forecaster, issuer){
    if(this.datasetGrid == null)
        return;
    if(this.currentUserName == null)
        return;
    var t = this;

    var hourSpanRange = t.currentPost;
    if(hourSpanRange == null){
        alert("请选择值班类型。");
        return;
    }
    var currentHourSpan = t.currentHourSpan; //为了避免异步提交时，用户操作，改变t.currentHourSpan
    if(Number(currentHourSpan) <= hourSpanRange.min || Number(currentHourSpan) > hourSpanRange.max){
        alert(currentHourSpan + "小时，不是您的预报的范围。");
        return;
    }

    var values = [];
    if(t.currentElement == "10uv" || t.currentElement == "wmax")
    {
        for(var i=0; i< t.datasetGrid.rows; i++)
        {
            for(var j=0; j< t.datasetGrid.cols; j++)
            {
                var dir = t.datasetGrid.grid[i][j].direction;
                var spd = t.datasetGrid.grid[i][j].z;
                var tmp = (270.0-dir) * Math.PI /180.0;
                var u = spd * Math.cos(tmp);
                var v = spd * Math.sin(tmp);
                u=Math.round(u*10)/10;
                v=Math.round(v*10)/10;
                values.push(u);
                values.push(v);
            }
        }
    }
    else
    {
        var hasTag = typeof(t.datasetGrid.tag) != "undefined";
        for(var i=0; i< t.datasetGrid.rows; i++)
        {
            for(var j=0; j< t.datasetGrid.cols; j++)
            {
                values.push(t.datasetGrid.grid[i][j].z);
                if(hasTag)
                    values.push(t.datasetGrid.tag[i][j]);
            }
        }
    }

    var id = -1;
    var subjective = 0;
    var fromModel = "";
    var fromModelTime = "";
    var gridInfo = t.getGridInfoFromCache(type, t.currentElement, t.currentDateTime, currentHourSpan);
    if(gridInfo != null){
        id = gridInfo.id;
        fromModel = gridInfo.nwpmodel;
        fromModelTime = gridInfo.nwpmodelTime;
        subjective = gridInfo.subjective;

        //对预报员和签发人的处理
        if(t.currentType == "prvn") {
            if(t.currentPost.name == "首席岗"){
                gridInfo.issuer = issuer;   //只能更新签发人
                forecaster = gridInfo.forecaster; //保留预报员
            }
            else{
                issuer = "未签发";
                gridInfo.forecaster = forecaster; //更新预报员
                gridInfo.issuer = issuer;
            }
        }
        else{
            gridInfo.forecaster = forecaster;
            gridInfo.issuer = issuer;
        }
    }

    if(subjective == 0) {
        var dataCache = GDYB.GridProductClass.dataCache.getData(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, currentHourSpan);
        if (dataCache != null && dataCache.status == 1 || dataCache.status == 4)
            subjective = 1;
    }

    $("#div_progress_title").html("正在提交"+t.currentHourSpan+"小时"+t.getElementNameByElement(t.currentElement)+"...");
    $("#div_progress").css("display", "block");

    var t1 = new Date().getTime();
    var url=Url_Config.gridServiceUrl+"services/GridService/saveGrid";
    $.ajax({
        data:{"para":"{id:" + id + ",departCode:'"+t.currentUserDepart.departCode+"',element:'"+ t.currentElement + "',type:'"+ type + "',level:'"+ t.currentLevel
            + "',hourspan:"+ currentHourSpan + ",hourspanTotal:"+ t.currentHourSpanTotal + ",maketime:'"+ t.currentMakeTime + "',version:'"+ t.currentVersion + "',datetime:'"+ t.currentDateTime + "',fromModel:'" + fromModel + "',fromModelTime:'" + fromModelTime
            + "',userName:'" + userName + "',forecaster:'" + forecaster + "',issuer:'" + issuer + "',subjective:" + subjective + ",cols:" + t.datasetGrid.cols
            + ",rows:" + t.datasetGrid.rows + ",left:" + t.datasetGrid.left + ",bottom:" + t.datasetGrid.bottom
            + ",width:" + t.datasetGrid.width+ ",height:" + t.datasetGrid.height + ",noDataValue:" + t.datasetGrid.noDataValue + ",values:'" + values + "'}"},
        url:url,
        dataType:"json",
        success:function(data){
            $("#div_progress").css("display", "none");
            if(data>=0)
            {
                if(gridInfo == null)
                    gridInfo = t.createGridInfo(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, currentHourSpan);
                if(gridInfo.id < 0) //更新id
                    gridInfo.id = data;
                if(gridInfo.userName == "") //更新userName
                    gridInfo.userName = GDYB.GridProductClass.currentUserName;
                if(t.dataCache != null)
                    t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, currentHourSpan, subjective==0?2:4); //更新已提交状态
                alert("格点保存成功");
            }
            else
                alert("格点保存失败");
            GDYB.GDYBPage.updateElementStatus();
        },
        error:function(){
            $("#div_progress").css("display", "none");
            alert("格点保存失败");
        },
        type:"POST"
    });
};

//提交（所有时效的）产品
GridProductClass.prototype.saveGridProducts = function(recall, type, userName, forecaster, issuer, element, hourSpans){
    if(this.datasetGrid == null)
        return;
    if(this.currentUserName == null)
        return;
    var t = this;

    var hourSpanRange = t.currentPost;
    if(hourSpanRange == null){
        alert("请选择值班类型。");
        return;
    }

    if(t.defaultscheme == null) {
        alert("默认方案为空！");
        return;
    }

    var gridinfos="";
    var hourSpanSubjective = {};
    for(var key in hourSpans){
        var hourSpan = hourSpans[key];

        if(Number(hourSpan) <= hourSpanRange.min || Number(hourSpan) > hourSpanRange.max)
            continue;

        var dataCache = t.dataCache.getData(t.currentMakeTime, t.currentVersion, t.currentDateTime, element, hourSpan);
        if(dataCache == null || dataCache.data == null){
            recall&&recall("失败：" + t.getElementNameByElement(element) + "要素时效不全不能提交");
            return;
        }
        var datasetGrid = dataCache.data;
        var values = [];
        if(element == "10uv" || element == "wmax"){
            for(var i=0; i< datasetGrid.rows; i++)
            {
                for(var j=0; j< datasetGrid.cols; j++)
                {
                    var dir = datasetGrid.grid[i][j].direction;
                    var spd = datasetGrid.grid[i][j].z;
                    var tmp = (270.0-dir) * Math.PI /180.0;
                    var u = spd * Math.cos(tmp);
                    var v = spd * Math.sin(tmp);
                    u=Math.round(u*10)/10;
                    v=Math.round(v*10)/10;
                    values.push(u);
                    values.push(v);
                }
            }
        }
        else {
            var hasTag = typeof(datasetGrid.tag) != "undefined";
            for (var i = 0; i < datasetGrid.rows; i++) {
                for (var j = 0; j < datasetGrid.cols; j++) {
                    values.push(datasetGrid.grid[i][j].z);
                    if(hasTag)
                        values.push(datasetGrid.tag[i][j]);
                }
            }
        }

        var id = -1;
        var subjective = 0;
        var fromModel = "";
        var fromModelTime = "";
        var gridInfo = t.getGridInfoFromCache(type, element, t.currentDateTime, hourSpan);
        if(gridInfo != null){
            id = gridInfo.id;
            fromModel = gridInfo.nwpmodel;
            fromModelTime = gridInfo.nwpmodelTime;
            subjective = gridInfo.subjective;

            //对预报员和签发人的处理
            if(t.currentType == "prvn") {
                if(t.currentPost.name == "首席岗"){
                    gridInfo.issuer = issuer;   //只能更新签发人
                    forecaster = gridInfo.forecaster; //保留预报员
                }
                else{
                    issuer = "未签发";
                    gridInfo.forecaster = forecaster; //更新预报员
                    gridInfo.issuer = issuer;
                }
            }
            else{
                gridInfo.forecaster = forecaster;
                gridInfo.issuer = issuer;
            }
        }

        if(subjective == 0) {
            var dataCache = GDYB.GridProductClass.dataCache.getData(t.currentMakeTime, t.currentVersion, t.currentDateTime, element, hourSpan);
            if (dataCache != null && dataCache.status == 1 || dataCache.status == 4)
                subjective = 1;
        }

        gridinfos += "{id:" + id + ", departCode:'"+t.currentUserDepart.departCode+"',element:'"+ element + "',type:'"+ type + "',level:'"+ t.currentLevel
        + "',hourspan:"+ hourSpan + ",hourspanTotal:"+ t.currentHourSpanTotal + ",maketime:'"+ t.currentMakeTime + "',version:'"+ t.currentVersion + "',datetime:'"+ t.currentDateTime + "',fromModel:'" + fromModel + "',fromModelTime:'" + fromModelTime
        + "',userName:'" + userName + "',forecaster:'" + forecaster + "',issuer:'" + issuer + "',subjective:"+ subjective + ",cols:" + datasetGrid.cols
        + ",rows:" + datasetGrid.rows + ",left:" + datasetGrid.left + ",bottom:" + datasetGrid.bottom
        + ",width:" + datasetGrid.width+ ",height:" + datasetGrid.height + ",noDataValue:" + datasetGrid.noDataValue + ",values:'" + values + "'},";

        hourSpanSubjective[hourSpan]=subjective;
    }
    gridinfos = gridinfos.substr(0, gridinfos.length - 1);

    var elementName = t.getElementNameByElement(element);
	$("#div_progress_title").html("正在提交"+elementName+t.currentPost.des+"预报...");
    $("#div_progress").css("display", "block");

    var t1 = new Date().getTime();
    var url=Url_Config.gridServiceUrl+"services/GridService/saveGrids";
    $.ajax({
        data:{"para":"{gridinfos:[" + gridinfos + "]}"},
        url:url,
        dataType:"json",
        success:function(data){
            $("#div_progress").css("display", "none");
            if(data)
            {
                var falseHourSpans = "";
                for(var key in hourSpans) {
                    var hourSpan = hourSpans[key];
                    if(Number(hourSpan) <= hourSpanRange.min || Number(hourSpan) > hourSpanRange.max)
                        continue;
                    var id = data[key];
                    if(id < 0)
                        falseHourSpans+=hourSpan+"、";
                    var gridInfo = t.getGridInfoFromCache(type, element, t.currentDateTime, hourSpan);
                    if(gridInfo == null)
                        gridInfo = t.createGridInfo(t.currentMakeTime, t.currentVersion, t.currentDateTime, element, hourSpan);
                    if(gridInfo.id < 0) //更新id
                        gridInfo.id = id;
                    if(gridInfo.userName == "") //更新userName
                        gridInfo.userName = GDYB.GridProductClass.currentUserName;
                    var subjective = hourSpanSubjective[hourSpan];
                    if (t.dataCache != null)
                        t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, element, hourSpan, subjective==0?2:4); //更新已提交状态
                }

                var result = "";
                if(falseHourSpans.length == 0)
                    result = "成功："+elementName;
                else
                    result = "失败："+elementName+"；时效："+falseHourSpans.substr(0, falseHourSpans.length-1);
                GDYB.GDYBPage.updateElementStatus();
                recall&&recall(result);
            }
            else{
                //alert("格点保存失败");
                recall&&recall("失败："+elementName);
            }
        },
        error:function(){
            $("#div_progress").css("display", "none");
            //alert("格点保存失败");
            recall&&recall("失败："+elementName);
        },
        type:"POST"
    });
};

/*
 * 描述：计算直线与几何对象（折线、多边形）的交点
 * 参数：
 *   pt1：直线的第一个点，{x, y, z}
 *   pt2：直线的第二个点，{x, y, z}
 *   gr：简单几何对象，WeatherMap.Geomtry
 * 返回：交点数组
 * */
GridProductClass.prototype.getIntersections = function(pt1, pt2, geo){
    var result = [];
    var lineString = null;
    if(geo.CLASS_NAME == "WeatherMap.Geometry.LineString" || geo.CLASS_NAME === "WeatherMap.Geometry.LinearRing")
        lineString = geo;
    else if(geo.CLASS_NAME == "WeatherMap.Geometry.Polygon")
        lineString = geo.components[0];
    if(lineString == null)
        return null;

    var pts = lineString.components;
    for(var i=1; i<pts.length; i++)
    {
        var pt3 = pts[i-1];
        var pt4 = pts[i];
        //var intersection = this.calcIntersection(pt1, pt2, pt3, pt4);
        var intersection = null;
        if(pt1.y == pt2.y)
            intersection = this.calcIntersectionWithHorizontalSegment(pt1.y, pt3, pt4);
        else if(pt1.x == pt2.x)
            intersection = this.calcIntersectionWithVerticalSegment(pt1.x, pt3, pt4);
        else
            intersection = this.calcIntersection(pt1, pt2, pt3, pt4);
        if(intersection == null)
            continue;
        result.push(intersection);
    }
    return result;
}

//计算线段交点
GridProductClass.prototype.calcIntersection = function(a, b, c, d) {
    var result = null;
    // 三角形abc 面积的2倍
    var area_abc = (a.x - c.x) * (b.y - c.y) - (a.y - c.y) * (b.x - c.x);

    // 三角形abd 面积的2倍
    var area_abd = (a.x - d.x) * (b.y - d.y) - (a.y - d.y) * (b.x - d.x);

    // 面积符号相同则两点在线段同侧,不相交 (对点在线段上的情况,本例当作不相交处理);
    if ( area_abc*area_abd>=0 ) {
        return result;
    }

    // 三角形cda 面积的2倍
    var area_cda = (c.x - a.x) * (d.y - a.y) - (c.y - a.y) * (d.x - a.x);
    // 三角形cdb 面积的2倍
    // 注意: 这里有一个小优化.不需要再用公式计算面积,而是通过已知的三个面积加减得出.
    var area_cdb = area_cda + area_abc - area_abd ;
    if (  area_cda * area_cdb >= 0 ) {
        return result;
    }

    //计算交点坐标
    var t = area_cda / ( area_abd- area_abc );
    var dx= t*(b.x - a.x),
        dy= t*(b.y - a.y);
    result = { x: a.x + dx , y: a.y + dy };
    return result;
}

/*
 * 计算线段与水平横线(含水平横延长线)交点
 *  y: 水平横线y坐标；
 *  pt1: 线段端点1；
 *  pt2: 线段端点2；
 * */
GridProductClass.prototype.calcIntersectionWithHorizontalSegment = function(y, pt1, pt2) {
    var result = null;
    //if((pt1.y - y) * (pt2.y - y) > 0)
    if((pt1.y > y && pt2.y > y) || (pt1.y < y && pt2.y < y))
        return result;
    return {x:pt2.x - (pt2.y-y)/(pt2.y-pt1.y)*(pt2.x-pt1.x), y:y};
}

/*
 * 计算线段与纵线(含纵线延长线)交点
 *  x: 纵线x坐标；
 *  pt1: 线段端点1；
 *  pt2: 线段端点2；
 * */
GridProductClass.prototype.calcIntersectionWithVerticalSegment = function(x, pt1, pt2) {
    var result = null;
    //if((pt1.y - y) * (pt2.y - y) > 0)
    if((pt1.x > x && pt2.x > x) || (pt1.x < x && pt2.x < x))
        return result;
    return {x:x, y:(x-pt1.x)/(pt2.x-pt1.x)*(pt2.y-pt1.y)+pt1.y};
};

GridProductClass.prototype.createClimaticRegionLayer = function(){
    if(this.layerClimaticRegion == null)
    {
        var map = GDYB.Page.curPage.map;
        this.layerClimaticRegion = new WeatherMap.Layer.Vector("ClimaticRegion");
        this.layerClimaticRegion.style = {
            strokeColor: "#a548ca",
            strokeWidth: 2.0,
            fillColor: "#ff0000",
            fillOpacity: "0"
        };
        map.addLayers([this.layerClimaticRegion]);
    }
};

// /*
//  * 生成缓存，逐时递归请求，缺点：次数太多导致整个过程耗时长
//  * */
//GridProductClass.prototype.cache = function(recall, type, date, element, level, hourspans){
//    var t = this;
//    if(t.dataCache == null)
//        t.dataCache = new DataCache();
//
//    if(t.dataCache.getData(date, element) == null){
//        var nIndex = 0;
//        getGridRecursion(nIndex);
//    }
//    else{
//        recall&&recall();
//    }
//
//    //递归请求
//    function getGridRecursion(nIndex){
//        if(nIndex >= hourspans.length)
//        {
//            recall&&recall();
//            return;
//        }
//        var hourspan = hourspans[nIndex];
//        t.getGrid(function(datasetGrid){
//            t.dataCache.addData(date, element, hourspan, datasetGrid);
//            getGridRecursion(++nIndex);
//        }, element, type, level, hourspan, date, false);
//    }
//};

/*
 * 生成缓存，一次性批量请求
 * */
GridProductClass.prototype.cache = function(recalls, type, maketime, date, element, elementName, level, hourspans, version){
    var t = this;
    if(t.dataCache == null)
        t.dataCache = new DataCache();

//    var maketime = t.currentMakeTime;
    if(typeof(version) == "undefined")
        version = t.currentVersion;
    var dataCache = t.dataCache.getData(maketime, version, date, element);
    var hourSpans = GDYB.GDYBPage.getHourSpan(element);
    if(dataCache == null || type != t.currentType) {
        t.getGrids(recalls, element, elementName, type, level, hourspans, maketime, version, date);
    }
    else{
        if(recalls != null && typeof(recalls) != "undefined" && recalls.length > 0){
            for(var key in recalls)
                recalls[key]&&recalls[key]();
        }
    }
};

GridProductClass.prototype.getGrids = function(recalls, element, elementName, type, level, hourspans, maketime, version, datetime, strCallModel){
    var t = this;

    var bCallModel = typeof(strCallModel) != "undefined";
    var type = bCallModel? strCallModel : type;

    var strHourSpans = "";
    var hSize=hourspans.length;
    for(var i=0;i<hSize;i++){
        strHourSpans += hourspans[i] + ",";
    }
    strHourSpans = strHourSpans.substr(0, strHourSpans.length - 1);
    $("#div_progress_title").html("正在下载["+ elementName +"]要素...");
    $("#div_progress").css("display", "block");

    var url= bCallModel?Url_Config.gridServiceUrl+"services/GridService/callModels" : Url_Config.gridServiceUrl+"services/GridService/getGrids";
    $.ajax({
        data:{"para":"{element:'"+ element + "',type:'"+ type + "',level:'"+ level + "',hourspans:'"+ strHourSpans + "',maketime:'" + maketime + "',version:'" + version + "',datetime:'"+ datetime + "'}"},
        url:url,
        dataType:"json",
        success:function(datas){
            try
            {
                var isAllNull = datas == null;
                if(!isAllNull) {
                    isAllNull = true;
                    for (var k = 0; k < datas.length; k++) {
                        var data = datas[k];
                        var dvalues = data.dvalues;
                        if (dvalues != null && dvalues.length > 0) {
                            isAllNull = false;
                            break;
                        }
                    }
                }
                if(isAllNull){
                    if(bCallModel)
                        alert("模式调入失败："+elementName+"无数据");
                }
                else {
                    for (var k = 0; k < datas.length; k++) {
                        var data = datas[k];
                        var datasetGrid = null;
                        var dvalues = data.dvalues;
                        var nwpModelTime = null;
                        if (dvalues != null && dvalues.length > 0) {
                            var bWind = (element == "10uv" || element == "wmax"); //是否为风场，风场具有两个字段（风向、风速），在dvalues中交叉表示
                            var hasTag = (!bWind)&&(dvalues.length==data.rows*data.cols*2);
                            var dimensions = (bWind||hasTag) ? 2 : 1; //维度，风场有两维；带有Tag属性也是两维
                            var dMin = 9999;
                            var dMax = -9999;
                            datasetGrid = new WeatherMap.DatasetGrid(data.left, data.top, data.right, data.bottom, data.rows, data.cols);
                            datasetGrid.noDataValue = data.noDataValue;
                            if (data.nwpmodelTime != null)
                                nwpModelTime = data.nwpmodelTime;
                            var grid = [];
                            var tag = [];
                            for (var i = 0; i < data.rows; i++) {
                                var tagLine = [];
                                var nIndexLine = data.cols * i * dimensions;
                                for (var j = 0; j < data.cols; j++) {
                                    var nIndex = nIndexLine + j * dimensions;
                                    var z;
                                    if (bWind) {
                                        z = dvalues[nIndex + 1];
                                        grid.push(Math.round(dvalues[nIndex+1])); //风速在前
                                        grid.push(Math.round(dvalues[nIndex]));   //风向在后
                                    }
                                    else {
                                        z = dvalues[nIndex];
                                        grid.push(Math.round(dvalues[nIndex] * 10) / 10);
                                        if(hasTag)
                                            tagLine.push(dvalues[nIndex+1]);
                                    }
                                    if (z != 9999 && z != -9999) {
                                        if (z < dMin)
                                            dMin = z;
                                        if (z > dMax)
                                            dMax = z;
                                    }
                                }
                                if(hasTag)
                                    tag.push(tagLine);
                            }
                            datasetGrid.grid = grid;
                            datasetGrid.dMin = dMin;
                            datasetGrid.dMax = dMax;
                            if(hasTag){
                                datasetGrid.tag = tag;
                                datasetGrid.defaultTag = 0;
                            }
                        }

                        //获取产品状态（判断是否已提交）
                        var status = 0;
                        var gridInfo = t.getGridInfoFromCache(type, element, datetime, hourspans[k]);
                        if(gridInfo != null)
                        {
                            if (typeof(gridInfo.userName) != "undefined" && gridInfo.userName != "") //已提交
                            {
                                status = 2;
                                if(typeof(gridInfo.subjective) != "undefined" && gridInfo.subjective == 1) //已主观订正
                                    status = 4;
                            }
                        }

//                        if(version == "r" && t.currentPost != null && t.currentPost.name == "首席岗") //callR已经不用了，这里就不用了
//                            version = "p";

                        var currentMakeTime = t.currentMakeTime; //之所以这里要用当前制作时间，是为了实现调取上一期。因为调取上一期时，请求的制作时间和当前制作时间不一致，故在此替换为当前时间。
                        var currentVersion = t.currentVersion; //之所以这里要用当前版本，是为了实现调取上一期。因为调取上一期时，请求的版本和当前版本可能不一致，故在此替换为当前版本。

                        //添加格点到缓存
                        t.dataCache.addData(currentMakeTime, currentVersion, datetime, element, hourspans[k], datasetGrid, status);

                        //更新缓存
                        if (bCallModel) {
                            var dataCache = t.dataCache;
                            var hourspan = hourspans[k];
                            if (dataCache != null) {
                                dataCache.data = datasetGrid;
                                t.dataCache.addData(currentMakeTime, currentVersion, datetime, element, hourspan, datasetGrid, 1); //标记为已修改
                            }

                            //更新模式来源
                            var nhourSpan = Number(hourspan);
                            var gridInfos = GDYB.GridProductClass.datasetGridInfos;
                            if (gridInfos != null) {
                                var find = false;
                                for (var i = 0; i < gridInfos.length; i++) {
                                    if (gridInfos[i].type == t.currentType && gridInfos[i].element == element && gridInfos[i].makeTime == currentMakeTime && gridInfos[i].version == currentVersion && gridInfos[i].forecastTime == datetime && gridInfos[i].hourSpan == nhourSpan) {
                                        find = true;
                                        gridInfos[i].nwpmodel = type; //更新数值模式来源
                                        gridInfos[i].nwpmodelTime = nwpModelTime; //更新数值模式时间
                                        GDYB.SideWrapper.setActive(gridInfos[i].nwpmodel, gridInfos[i].nwpmodelTime);
                                        break;
                                    }
                                }
                                if (!find) {
                                    var modelType = type;
                                    if (strCallModel == "prvn" || strCallModel == "cty" || strCallModel == "cnty") //如果调入省、市、县产品，模式来源为上一期吧
                                        modelType = "last";
                                    var gridInfo = {"type": type, "element": element, "forecastTime": datetime, "hourSpan": nhourSpan, "nwpmodel": modelType, "nwpmodelTime": nwpModelTime};
                                    GDYB.SideWrapper.setActive(gridInfo.nwpmodel, gridInfo.nwpmodelTime);
                                    gridInfos.push(gridInfo);
                                }
                            }
                        }
                    }
                }

//                //如果无数据，则创建一个空的数据集
//                if(!bCallModel){
//                    for (var k = 0; k < datas.length; k++) {
//                        var data = datas[k];
//                        var dvalues = data.dvalues;
//                        if (dvalues == null) {
//                            t.createDatasetGrid(maketime, version, datetime, element, hourspans[k]);
//                        }
//                    }
//                }
            }
            catch (err)
            {
                alert(err.message);
            }

            $("#div_progress").css("display", "none");

            if(recalls != null && typeof(recalls) != "undefined" && recalls.length > 0){
                for(var key in recalls)
                    recalls[key]&&recalls[key]();
            }
        },
        error: function (e) {
            $("#div_progress").css("display", "none");
        },
        type:"POST"
    });
};

/*
 * 显示站点值
 */
GridProductClass.prototype.showStation = function(recall, stationType, height){
    var t = this;
    if(t.stations == null){
        var areaCode = "51";
        var url=Url_Config.gridServiceUrl+"services/GridService/getStations";
        $.ajax({
            data:{"para":"{areaCode:'"+ areaCode + "'}"},
            url:url,
            dataType:"json",
            success:function(data){
                t.stations = data;
                t.gridToStation(stationType, height);
                recall&&recall();
            },
            error: function (e) {
                alert("获取站点数据错误");
            },
            type:"POST"
        });
    }
    else{
        t.gridToStation(stationType, height);
        recall&&recall();
    }
};

/*
 * 显示预报站点值
 */
GridProductClass.prototype.showStationForecast = function(recall, stationType, height){
    var t = this;
    if(t.stationsForecast == null){
        var areaCode = "51";
        var url=Url_Config.gridServiceUrl+"services/GridService/getStationsForecast";
        $.ajax({
            data:{"para":"{areaCode:'"+ areaCode + "'}"},
            url:url,
            dataType:"json",
            success:function(data){
                t.stationsForecast = data;
                t.gridToStationForecast(stationType, height);
                recall&&recall();
            },
            error: function (e) {
                alert("获取站点数据错误");
            },
            type:"POST"
        });
    }
    else{
        t.gridToStationForecast(stationType, height);
        recall&&recall();
    }
};
/*
 * 格点转站点
 * stationType：1-国家站，2-区域站
 */
GridProductClass.prototype.gridToStation = function(stationType, height){
    var t = this;
    var map = GDYB.Page.curPage.map;
    if(typeof(height) != "undefined") {
        if (t.layerPlotHighStaion == null) {
            var strategy = new WeatherMap.Strategy.GeoText();
            strategy.style = {
                fontFamily: "Arial",
                fontColor: "#333",
                fontSize: "14px",
                fontWeight: 900,
                fill: false,
                stroke: false
            };
            t.layerPlotHighStaion = new WeatherMap.Layer.Vector("layerPlotHighStaion", {strategies: [strategy], renderers: ["Canvas"]});
            map.addLayer(t.layerPlotHighStaion);
        }
        else {
            t.layerPlotHighStaion.removeAllFeatures();
        }
        t.layerPlotHighStaion.visibility = true;
    }
    else {
        if (stationType == 1) {
            var strategy = new WeatherMap.Strategy.GeoText();
            strategy.style = {
                fontFamily: "Arial",
                fontColor: "#333",
                fontSize: "16px",
                fontWeight: 900,
                fill: false,
                stroke: false
            };
            if (t.layerPlotNationStation == null) {
                t.layerPlotNationStation = new WeatherMap.Layer.Vector("layerPlotNationStation", {strategies: [strategy], renderers: ["Canvas"]});
                map.addLayer(t.layerPlotNationStation);
            }
            else {
                t.layerPlotNationStation.removeAllFeatures();
            }
            t.layerPlotNationStation.visibility = true;
        }
        else if (stationType == 2) {
            var strategy = new WeatherMap.Strategy.GeoText();
            strategy.style = {
                fontFamily: "Arial",
                fontColor: "#333",
                fontSize: "14px",
                fontWeight: 900,
                fill: false,
                stroke: false
            };
            if (t.layerPlotLocalStation == null) {
                t.layerPlotLocalStation = new WeatherMap.Layer.Vector("layerPlotLocalStation", {strategies: [strategy], renderers: ["Canvas"]});
                map.addLayer(t.layerPlotLocalStation);
            }
            else {
                t.layerPlotLocalStation.removeAllFeatures();
            }
            t.layerPlotLocalStation.visibility = true;
        }
    }

    if(t.datasetGrid == null || t.stations == null)
        return;
    var labelFeatures = [];
    for(var key in t.stations){
        var station = t.stations[key];
        if(typeof(height) != "undefined" && (station.height < height || station.height >= 10000) || typeof(height) == "undefined" && station.type != stationType)
            continue;
        var rowcol = t.datasetGrid.xyToGrid(station.longitude, station.latitude);
        if(rowcol.y >= 0 && rowcol.y < t.datasetGrid.rows && rowcol.x >=0  && rowcol.x < t.datasetGrid.cols) {
            var index=rowcol.y*t.datasetGrid.cols+rowcol.x;
            var v = t.datasetGrid.grid[index];

            if(v == t.datasetGrid.noDataValue)
                continue;
            var dvalue = null;
            if(v < 0.1 && v>0)
                dvalue = "T";
            else
                dvalue = Math.floor(v*10) / 10;
            var label = new WeatherMap.Geometry.GeoText(station.longitude+0.05, station.latitude+0.05, dvalue);
            var feature = new WeatherMap.Feature.Vector(label);
            labelFeatures.push(feature);
        }
    }

    if(typeof(height) != "undefined"){
            t.layerPlotHighStaion.addFeatures(labelFeatures);
            t.layerPlotHighStaion.redraw();
    }
    else{
        if(stationType == 1){
            t.layerPlotNationStation.addFeatures(labelFeatures);
            t.layerPlotNationStation.redraw();
        }
        else if(stationType == 2){
            t.layerPlotLocalStation.addFeatures(labelFeatures);
            t.layerPlotLocalStation.redraw();
        }
    }
};

/*
 * 格点转（预报）站点
 * stationType：1-城镇，4-乡镇
 */
GridProductClass.prototype.gridToStationForecast = function(stationType, height){
    var t = this;
    var map = GDYB.Page.curPage.map;
    if(stationType == 1) {

    }
    else if(stationType == 4) {
        if(typeof(height) == "undefined") {
            if (t.layerPlotTown == null) {
                var strategy = new WeatherMap.Strategy.GeoText();
                strategy.style = {
                    fontFamily: "Arial",
                    fontColor: "#333",
                    fontSize: "14px",
                    fontWeight: 900,
                    fill: false,
                    stroke: false
                };
                t.layerPlotTown = new WeatherMap.Layer.Vector("layerPlotTown", {strategies: [strategy], renderers: ["Canvas"]});
                map.addLayer(t.layerPlotTown);
            }
            else {
                t.layerPlotTown.removeAllFeatures();
            }
            t.layerPlotTown.visibility = true;
        }
        else {
            //高山站图层
            if (t.layerPlotHighStaion == null) {
                var strategy = new WeatherMap.Strategy.GeoText();
                strategy.style = {
                    fontFamily: "Arial",
                    fontColor: "#333",
                    fontSize: "14px",
                    fontWeight: 900,
                    fill: false,
                    stroke: false
                };
                t.layerPlotHighStaion = new WeatherMap.Layer.Vector("layerPlotHighStaion", {strategies: [strategy], renderers: ["Canvas"]});
                map.addLayer(t.layerPlotHighStaion);
            }
            else {
                t.layerPlotHighStaion.removeAllFeatures();
            }
            t.layerPlotHighStaion.visibility = true;
        }
    }

    if(t.datasetGrid == null || t.stationsForecast == null)
        return;
    var labelFeatures = [];
    for(var key in t.stationsForecast){
        var station = t.stationsForecast[key];
        if(station.type != stationType)
            continue;
        if(typeof(height) != "undefined" && (station.height < height || station.height == 999999))
            continue;
        var rowcol = t.datasetGrid.xyToGrid(station.longitude, station.latitude);
        if(rowcol.y >= 0 && rowcol.y < t.datasetGrid.rows && rowcol.x >=0  &&rowcol.x < t.datasetGrid.cols) {
            var v = t.datasetGrid.grid[rowcol.y][rowcol.x].z;

            if(v == t.datasetGrid.noDataValue)
                continue;
            var dvalue = null;
            if(v < 0.1 && v>0)
                dvalue = "T";
            else
                dvalue = Math.floor(v*10) / 10;
            var label = new WeatherMap.Geometry.GeoText(station.longitude, station.latitude, dvalue);
            var feature = new WeatherMap.Feature.Vector(label);
            labelFeatures.push(feature);
        }
    }
    if(stationType == 1){

    }
    else if(stationType == 4){
        if(typeof(height) == "undefined"){
            t.layerPlotTown.addFeatures(labelFeatures);
            t.layerPlotTown.redraw();
        }
        else{
            t.layerPlotHighStaion.addFeatures(labelFeatures);
            t.layerPlotHighStaion.redraw();
        }
    }
};

GridProductClass.prototype.updateStationLayer = function(){
    if(this.layerPlotNationStation != null && this.layerPlotNationStation.visibility)
        this.gridToStation(1);
    if(this.layerPlotLocalStation != null && this.layerPlotLocalStation.visibility)
        this.gridToStation(2);
    if(this.layerPlotTown != null && this.layerPlotTown.visibility)
        this.gridToStationForecast(4);
    if(this.layerPlotHighStaion != null && this.layerPlotHighStaion.visibility)
        this.gridToStation(-1, 800);
};

GridProductClass.prototype.showFocusArea = function(recall){
	//t.layerFocusArea 
    var t = this;
    var departCode =  t.currentUserArea;
    var areaType;
    var url=Url_Config.gridServiceUrl+"services/AreaService/getAreas";
    $.ajax({
        data:{"para":"{departCode:'"+ departCode + "'}"},
        url:url,
        dataType:"json",
        success:function(data){
            if(t.layerFocusArea != null) 
                t.layerFocusArea.removeAllFeatures();
            if(data != null && data.length > 0) {
                var features = [];
                for (var i in data) {
                    var area = data[i];
                    var strCoordinates = area["coordinates"];
                    var arrayCoordinates = strCoordinates.split(";");
                    var pointArray = new Array();
                    var bounds = new WeatherMap.Bounds(180, 90, -180, -90);
                    for(var j in arrayCoordinates){
                        var xy = arrayCoordinates[j];
                        var arrayXY = xy.split(",");
                        var pt = new WeatherMap.Geometry.Point(Number(arrayXY[0]), Number(arrayXY[1]));
                        pointArray.push((pt));
                        if(pt.x < bounds.left)
                            bounds.left = pt.x;
                        if(pt.x > bounds.right)
                            bounds.right = pt.x;
                        if(pt.y < bounds.bottom)
                            bounds.bottom = pt.y;
                        if(pt.y > bounds.top)
                            bounds.top = pt.y;
                    }
                    var line = new WeatherMap.Geometry.LinearRing(pointArray);
                    var lines  = [];
                    lines.push(line);
                    var geoRegion = new WeatherMap.Geometry.Polygon(lines);
                    geoRegion.bounds = bounds;
                    var regionFeature = new WeatherMap.Feature.Vector(geoRegion);
                    features.push(regionFeature);
                }
                t.layerFocusArea.addFeatures(features);
            }
            recall&&recall();
        },
        error: function (e) {
            alert("获取关注区域错误");
        },
        type:"POST"
    });
};



/**
 * param type 
 * @param {Object} recall
 */
GridProductClass.prototype.showFocusAreaByType = function(recall){
	//t.layerFocusArea 
    var t = this;
    var type =  t.areaType;
    var url=Url_Config.gridServiceUrl+"services/AreaService/getAreasByType";
    $.ajax({
        data:{"para":"{type:'"+ type + "'}"},
        url:url,
        dataType:"json",
        success:function(data){
            if(t.layerFocusArea != null) 
                t.layerFocusArea.removeAllFeatures();
            if(data != null && data.length > 0) {
                var features = [];
                for (var i in data) {
                    var area = data[i];
                    var strCoordinates = area["coordinates"];
                    var arrayCoordinates = strCoordinates.split(";");
                    var pointArray = new Array();
                    var bounds = new WeatherMap.Bounds(180, 90, -180, -90);
                    for(var j in arrayCoordinates){
                        var xy = arrayCoordinates[j];
                        var arrayXY = xy.split(",");
                        var pt = new WeatherMap.Geometry.Point(Number(arrayXY[0]), Number(arrayXY[1]));
                        pointArray.push((pt));
                        if(pt.x < bounds.left)
                            bounds.left = pt.x;
                        if(pt.x > bounds.right)
                            bounds.right = pt.x;
                        if(pt.y < bounds.bottom)
                            bounds.bottom = pt.y;
                        if(pt.y > bounds.top)
                            bounds.top = pt.y;
                    }
                    var line = new WeatherMap.Geometry.LinearRing(pointArray);
                    var lines  = [];
                    lines.push(line);
                    var geoRegion = new WeatherMap.Geometry.Polygon(lines);
                    geoRegion.bounds = bounds;
                    var regionFeature = new WeatherMap.Feature.Vector(geoRegion);
                    features.push(regionFeature);
                }
                t.layerFocusArea.addFeatures(features);
            }
            recall&&recall();
        },
        error: function (e) {
            alert("获取关注区域错误");
        },
        type:"POST"
    });
};

GridProductClass.prototype.setFillColorType = function(type){
    this.fillColorType = type;
    if(type == "值域"){
        if(!(this.currentElement == "w" || this.currentElement == "air"))
            this.layerFillRangeColor.items = this.getFillColorItemsByRange(this.currentElement, this.datasetGrid.dMin, this.datasetGrid.dMax);
        else
            alert("天气现象和空气污染等级不支持按值域填色");
    }
    else{
        this.layerFillRangeColor.items = this.getFillColorItems(this.currentElement);
    }
    this.layerFillRangeColor.refresh();
    GDYB.Legend.update(this.layerFillRangeColor.items);
    GDYB.GDYBPage.myPanel_Tools.updateUI(this.currentElement, this.currentElementName, false);
};

GridProductClass.prototype.getFillColorItems = function(element){
    var items = null;
    if(element == "2t")
        items = heatMap_TempStyles;
    else if(element == "2tjp"||element == "10to302tjpjp")
        items = heatMap_TempJuPingStyles;
    else if(element == "tcc")
        items = heatMap_TCCStyles;
    else if(element == "r1" || element == "r3" || element == "r6" || element == "r12" || element == "r24")
        items = heatMap_Rain24Styles;
    else if(element == "10uv" || element == "wmax"||element=="uv")
        items = heatMap_10uvStyles;
    else if(element == "r24jp"||element=="10to30r24jpjp")
        items = heatMap_Rain24JuPingStyles;
    else if(element == "air")
        items = heatMap_AirStyles;
    else if(element == "w")
        items = heatMap_WStyles;
    else
        items = heatMap_TempStyles;
    return items;
};

//根据值域，重组风格，风和降水除外
GridProductClass.prototype.getFillColorItemsByRange = function(element, dMin, dMax){
    var itemsTarget = [];
    if(this.fillColorType != "值域")
        return itemsTarget;

    var dMin = Math.floor(dMin*10)/10;
    var dMax = Math.round(dMax*10)/10; //向上取（十分位）整

    var items = this.layerFillRangeColor.items;
    var dStep = Math.round((dMax - dMin) / items.length*10)/10;
    for (var i = 0; i < items.length; i++) {
        var item = {};
        item.start = Math.round((dMin + dStep * i)*10)/10;
        item.end = Math.round((dMin + dStep * (i + 1))*10)/10;
        if(i == (items.length - 1)) //因为四舍五入dStep可能变小了一点点，计算出来的end小于dMax一点，故此干脆直接把最大值赋给end
            item.end = dMax;
        item.startColor = items[i].startColor;
        item.image = items[i].image;
        itemsTarget.push(item);
    }
    return itemsTarget;
};

//先暂时这样写吧
GridProductClass.prototype.getElementNameByElement = function(element){
	var result = element;
	if(element == "r24")
		result = "降水量";
	else if(element == "2t")
		result = "气温";
	return result;		
};

GridProductClass.prototype.createDatasetGrid = function(maketime, version, datetime, element, hourspan){
    var t = this;
    var bWind = (element == "10uv" || element == "wmax");
    var left = 96.5;
    var top = 34.5;
    var right = 109;
    var bottom = 25.5;
    var rows = 180;
    var cols = 250;
    var scheme = t.getCurrentGridScheme(element);
    var defaultValue = scheme == null?0:scheme.defaultDataValue;
    var datasetGrid = new WeatherMap.DatasetGrid(left, top, right, bottom, rows, cols);
    datasetGrid.noDataValue = -9999;
    var grid = [];
    for(var i=0;i<rows;i++){
        var gridLine = [];
        for(var j=0;j<cols;j++){
            var x = left + j * datasetGrid.deltaX;
            var y = top - i * datasetGrid.deltaY;
            if(bWind) {
                gridLine.push({
                    "direction": 0,
                    "z": defaultValue,       //依旧用z表示风速，便于渲染
                    "x": x,
                    "y": y
                });
            }
            else {
                gridLine.push({
                    "z": defaultValue,
                    "x": x,
                    "y": y
                });
            }
        }
        grid.push(gridLine);
    }
    datasetGrid.grid = grid;
    datasetGrid.dMin = defaultValue;
    datasetGrid.dMax = defaultValue;

    //更新缓存
    t.dataCache.addData(maketime, version, datetime, element, hourspan, datasetGrid, 0);
    t.createGridInfo(maketime, version, datetime, element, hourspan);
    return datasetGrid;
};

GridProductClass.prototype.createGridInfo = function(maketime, version, datetime, element, hourspan){
    var gi = {};
    gi.id = -1;
    gi.departCode = this.currentUserDepart;
    gi.type = this.currentType;
    gi.version = version;
    gi.element = element;
    gi.forecastTime = datetime;
    gi.hourSpan = hourspan;
    gi.totalHourSpan = this.currentHourSpanTotal;
    gi.level = 1000;
    gi.tabelName = "";
    gi.nwpmodel = "";
    gi.nwpmodelTime = "";
    gi.userName = this.currentUserName;
    gi.forecaster = "";
    gi.makeTime = maketime;
    gi.lastModifyTime = "";
    gi.subjective = 0;
    gi.remark = "";
    this.datasetGridInfos.push(gi);
    return gi;
};

GridProductClass.prototype.getCurrentGridScheme = function(element){
    var t = this;
    var result = null;
    if(t.defaultscheme != null && t.defaultscheme.length > 0){
        var makeTime = t.currentMakeTime.replace(/\d*-\d*-\d* (\d*):\d*:\d*/,"$1")+":"+t.currentMakeTime.replace(/\d*-\d*-\d* \d*:(\d*):\d*/,"$1");
        for(var key in t.defaultscheme){
            var scheme = t.defaultscheme[key];
            if(scheme.type == t.currentType && scheme.makeTime == makeTime && scheme.element == element){
                result = scheme;
                break;
            }
        }
    }
    return result;
};

//往datasetGrid中添加空的Tag属性，默认值为0。主要用于表示雨雪
GridProductClass.prototype.addTag = function(datasetGrid){
    var tag = [];
    var defaultTag = 0;
    var rows = datasetGrid.rows;
    var cols = datasetGrid.cols;
    for(var i=0;i<rows;i++){
        var tagLine = [];
        for(var j=0;j<cols;j++){
            tagLine.push(defaultTag);
        }
        tag.push(tagLine);
    }
    datasetGrid.tag = tag;
    datasetGrid.defaultTag = defaultTag;
};

//获取当前可见的站点
GridProductClass.prototype.getCurrentVisibleStations = function(){
    var t = this;
    var features = [];
    if(t.layerPlotNationStation != null && t.layerPlotNationStation.visibility && t.layerPlotNationStation.features.length > 0){
        for(var key in t.layerPlotNationStation.features)
            features.push(t.layerPlotNationStation.features[key]);
    }
    if(t.layerPlotLocalStation != null && t.layerPlotLocalStation.visibility && t.layerPlotLocalStation.features.length > 0){
        for(var key in t.layerPlotLocalStation.features)
            features.push(t.layerPlotLocalStation.features[key]);
    }
    if(t.layerPlotTown != null && t.layerPlotTown.visibility && t.layerPlotTown.features.length > 0){
        for(var key in t.layerPlotTown.features)
            features.push(t.layerPlotTown.features[key]);
    }
    if(t.layerPlotHighStaion != null && t.layerPlotHighStaion.visibility && t.layerPlotHighStaion.features.length > 0){
        for(var key in t.layerPlotHighStaion.features)
            features.push(t.layerPlotHighStaion.features[key]);
    }
    return features;
};
/**
 * @author:杠上花
 * @date:2017-1-31
 * @modifydate:
 * @param:
 * @return:
 * @description:等值线
 */
GridProductClass.prototype.getGridWithTimes = function(element,type,level,maketime,version,datetime,hourspans){
    var me = this;
    var param = {
        element:element,
        type:type,
        level:level,
        maketime:maketime,
        version:version,
        datetime:datetime,
        hourspans:hourspans
    };
    param = JSON.stringify(param);
    var url = Url_Config.gridServiceUrl + "services/GridService/getGridWithTimes";
    $.ajax({
        data: {"para":param},
        url: url,
        dataType: "json",
        type: "POST",
        success: function (data) {
            if(data.suc){
                var dg = convertGridToDatasetGrid(element,data.suc);
                me.displayDatasetGrid(element,dg);
            }
        },
        error:function(data){
            console.log(data);
        }
    });
}
/**
 * @author:杠上花
 * @date:2017-1-31
 * @modifydate:
 * @param:
 * @return:
 * @description:显示格点
 */
GridProductClass.prototype.displayDatasetGrid = function(element,dg){
    var me = this;
    var style = me.getFillColorItems(element);
    me.layerFillRangeColor.items = style;
    me.layerFillRangeColor.setDatasetGrid(dg);
    me.layerFillRangeColor.refresh();
}
/**
 * @author:杠上花
 * @date:2017-1-31
 * @modifydate:
 * @param:
 * @return:
 * @description:等值线
 */
GridProductClass.prototype.getGrid10To30DayDeparture = function(element,type,level,maketime,version,datetime){
    var me = this;
    var param = {
        element:element,
        type:type,
        level:level,
        maketime:maketime,
        version:version,
        datetime:datetime
    };
    param = JSON.stringify(param);
    var url = Url_Config.gridServiceUrl + "services/GridService/get10To30DayDeparture";
    $.ajax({
        data: {"para":param},
        url: url,
        dataType: "json",
        type: "POST",
        success: function (data) {
            if(data.suc){
                var dg = convertGridToDatasetGrid(element,data.suc);
                me.displayDatasetGrid(element,dg);
            }
        },
        error:function(data){
            console.log(data);
        }
    });
}