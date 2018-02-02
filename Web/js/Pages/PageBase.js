/*
 * 页面基类
 * by zouwei, 2015-05-10
 * */
function PageBase() { }
//地图对象
PageBase.prototype.map = null;
PageBase.prototype.baseLayer = null;
PageBase.prototype.baseLayerLabel = null;

//PageBase.prototype.nTickCount = 0;
//当前页面是否全屏模式
PageBase.prototype.isFullScreen = false;
//渲染左侧菜单区域里的按钮
PageBase.prototype.renderMenu = function () { };
//创建地图
PageBase.prototype.initMap = function (options) {
    if (!options) options = {};
    $("#map_div").css("display", "");
    var navigatnion = new WeatherMap.Control.Navigation();
    var layerSwitcher = new WeatherMap.Control.LayerSwitcher();
    navigatnion.handleRightClicks = true; //响应右键双击缩小
    var map = new WeatherMap.Map(options.id || "map", {
        controls: [
            navigatnion,
            layerSwitcher,
            new WeatherMap.Control.Zoom()], projection: "EPSG:4326"
    });
    map.addControl(new WeatherMap.Control.MousePosition());

    //    //本地缓存地图
    //    var layer = new WeatherMap.Layer.LocalTiledCacheLayer();
    //    //layer.name = "BaseLayer";
    //    map.addLayers([layer]);
    //    this.baseLayer = layer;

    //白板图
    var layer = new WeatherMap.Layer.LocalTiledCacheLayerWhiteMap();
    layer.name = "白板图";
    map.addLayers([layer]);
    this.baseLayer = layer;

    //构建全球图层
    /*var gloabLayer = new WeatherMap.Layer.Vector("vectorLine",{renderers: ["Canvas2"]});
    gloabLayer.id = "gloabLayer";
    map.addLayer(gloabLayer);
    this.baseLayer = gloabLayer;*/


    //    var layerLabel = new WeatherMap.Layer.LocalTiledCacheLayer();
    //    map.addLayers([layerLabel]);
    //    layerLabel.dir = "tianditu/map/label/";

    //    //天地图-地图
    //    var layer = new WeatherMap.Layer.TianDiTuLayer();
    //    layer.setFormat("vec");
    //    layer.setName("tianDiTuLayer_vec");
    //    var layerLabel = new WeatherMap.Layer.TianDiTuLayer();
    //    layerLabel.setFormat("cva");
    //    layerLabel.setName("tianDiTuLayer_cva");
    //    //只能有一个为baseLayer
    //    layerLabel.setIsBaseLayer(false);
    //    map.addLayers([layer,layerLabel]);

    //天地图-地形
    //    var layer = new WeatherMap.Layer.TianDiTuLayer();
    //    layer.setFormat("ter");
    //    layer.setName("tianDiTuLayer_ter");
    //    var layerLabel = new WeatherMap.Layer.TianDiTuLayer();
    //    layerLabel.setFormat("cta");
    //    layerLabel.setName("tianDiTuLayer_cta");
    //    //只能有一个为baseLayer
    //    layerLabel.setIsBaseLayer(false);
    //    map.addLayers([layer,layerLabel]);

    //map.setCenter(new WeatherMap.LonLat(options.x||11339634.286396, options.y||4588716.5813769), options.z||4);

    //魔术棒工具
    GDYB.MagicTool.init(map);
    map.events.register("movestart", map, function (event) {
        bDrag = true;
        $("#div_showGridValue").css("display", "none");
    });

    map.events.register("moveend", map, function (event) {
        bDrag = false;
    });

    map.events.register("keydown", map, function (event) {
        alert(event);
    });
    map.events.register("addlayer", map, function (event) {
        moveLabelToTop();
    })

    //图层被添加，将标签显示到最上面。这样也不好看
    //map.events.register("addlayer", map, function(event){
    // var layerCache= this.getBy("layers","name","LocalTiledCacheLayerWhiteMap");
    //this.setLayerIndex(layerLabel, 98);
    //map.raiseLayer(layerCache,1);
    //});
    var userName = $.cookie("userName");
    var password = $.cookie("password");
    viewMap();
    /**
     * @author:wangkun
     * @date:2017-03-10
     * @param:
     * @return:
     * @description:显示地图
     */
    function viewMap() {
        var activeArea = window.localStorage.getItem("activearea");
        if (activeArea == null || activeArea === "sc") {//默认加载四川
            map.setCenter(new WeatherMap.LonLat(102.8, 30.6), 6);
        }
        else if (activeArea === "xn") {
            map.setCenter(new WeatherMap.LonLat(94.2, 29.3), 5);
        }
        else if (activeArea === "cq") {
            map.setCenter(new WeatherMap.LonLat(107.8, 30.0), 7);
        }
        else if (activeArea === "yn") {
            map.setCenter(new WeatherMap.LonLat(102.5, 24.8), 6);
        }
        else if (activeArea === "gz") {
            map.setCenter(new WeatherMap.LonLat(106.5, 26.6), 6);
        }
        else if (activeArea === "xz") {
            map.setCenter(new WeatherMap.LonLat(88.0, 31.2), 6);
        }
    }
    /**
     * @author:wangkun
     * @date:2017-03-10
     * @param:
     * @return:
     * @description:标签置顶
     */
    function moveLabelToTop() {
        var layerCount = map.getNumLayers();
        var baseLayer= map.baseLayer;
        var baseIndex=map.getLayerIndex(baseLayer);
        var max = 0;
        var maxLayer=null;
        for (var i = layerCount - 1; i >= 0; i--) {
            var layer = map.layers[i];
            var index = map.getLayerIndex(layer);
            if (index > max) {
                max = index;
                maxLayer=layer;
            }
        }
        map.setLayerIndex(baseLayer,max+1);
        map.setLayerIndex(maxLayer,baseIndex);
        map.setLayerIndex(baseLayer,max);
    }
    // if(userName != null && password != null){
    //     var url=Url_Config.gridServiceUrl+"services/AreaService/getDepartByUser";
    //     $.ajax({
    //         data: {"para": "{userName:'"+userName+"'}"},
    //         url: url,
    //         dataType: "json",
    //         type: "POST",
    //         success: function (data) {
    //             if(typeof(data) != "undefined")
    //             {
    //                 $.cookie('departCode', data.departCode, { expires: 60 });
    //                 var depart = data;
    //                 url=Url_Config.gridServiceUrl+"services/AdminDivisionService/getDivisionInfo";
    //                 $.ajax({
    //                     data: {"para": "{areaCode:'"+data.departCode+"'}"},
    //                     url: url,
    //                     dataType: "json",
    //                     type: "POST",
    //                     success: function (data) {
    //                         if(typeof(data) != "undefined")
    //                         {
    //                             //var areaData = JSON.parse(data);
    //                             var areaData = data;
    //                             if(depart.parentID == 0){
    //                                 map.setCenter(new WeatherMap.LonLat(areaData.geometry.center.x, areaData.geometry.center.y), 6);
    //                             }
    //                             else if(depart.parentID == 1) {
    //                                 map.setCenter(new WeatherMap.LonLat(areaData.geometry.center.x, areaData.geometry.center.y), 8);
    //                             }
    //                             else{
    //                                 map.setCenter(new WeatherMap.LonLat(areaData.geometry.center.x, areaData.geometry.center.y), 9);
    //                             }
    //                             var testLayer = new WeatherMap.Layer.Vector("vectorLine",{renderers: ["Canvas2"]});
    //                             testLayer.id = "mapCoverLayer";
    //                             testLayer.setIsBaseLayer(true);
    //                             map.addLayer(testLayer);
    //                             map.events.register("addlayer", map, function(event){
    //                                 map.setLayerIndex(testLayer,98);
    //                                 if(GDYB.GridProductClass.layerLuoquCenter != null)
    //                                     map.setLayerIndex(GDYB.GridProductClass.layerLuoquCenter,99); //这个落区中心一定要放到最上层，否则无法移动
    //                             });
    //                             var pointArray = new Array();
    //                             var pointList = areaData.geometry.points
    //                             for(var i=0;i<pointList.length;i++){
    //                                 var lon = pointList[i].x;
    //                                 var lat = pointList[i].y;
    //                                 var point = new WeatherMap.Geometry.Point(lon, lat);
    //                                 pointArray.push(point);
    //                             }
    //                             var gxPointList = new Array();
    //                             gxPointList.push(new WeatherMap.Geometry.Point(-180, -90));
    //                             gxPointList.push(new WeatherMap.Geometry.Point(180, -90));
    //                             gxPointList.push(new WeatherMap.Geometry.Point(180, 90));
    //                             gxPointList.push(new WeatherMap.Geometry.Point(-180, 90));
    //                             var linearRings = new WeatherMap.Geometry.LinearRing(pointArray);
    //                             var linearRings1 = new WeatherMap.Geometry.LinearRing(gxPointList);
    //                             var polygon = new WeatherMap.Geometry.Polygon([linearRings,linearRings1]);
    //                             var polygonVector = new WeatherMap.Feature.Vector(polygon);
    //                             polygonVector.style = {
    //                                 strokeColor: "#ffffff",
    //                                 fillColor: "#ffffff",
    //                                 strokeWidth: 1,
    //                                 fillOpacity: 1,
    //                                 strokeOpacity: 0.4
    //                             };
    //                             //testLayer.addFeatures([polygonVector]);
    //                             GDYB.GDYBPage.polygonVector = polygonVector;
    //                             var line = new WeatherMap.Geometry.LineString(pointArray);
    //                             var lineVector = new WeatherMap.Feature.Vector(line);
    //                             lineVector.style = {
    //                                 strokeColor: "red",
    //                                 strokeWidth: 2
    //                             };
    //                             testLayer.addFeatures([lineVector]);
    //                             GDYB.GDYBPage.lineVector = lineVector;
    //                         }
    //                     },
    //                     error: function(e){
    //                         alert("获取用户所在地区失败："+ e.statusText);
    //                     }
    //                 });
    //             }
    //         },
    //         error: function(e){
    //             alert("获取用户所在部门失败："+ e.statusText);
    //         }
    //     });
    // }
    // else{
    //     setTimeout(function(){
    //         map.setCenter(new WeatherMap.LonLat(108.2, 23), 3); //格点数据范围中心点
    //     }, 300);
    // }
    return map;
};
//创建单一屏幕地图
PageBase.prototype.screen1Map = function () {
    $("#map").html("").css("display", "block");
    $(".mapd").css("display", "none");
    this.map = this.initMap({ id: "map" });
};
//进入全屏
PageBase.prototype.launchFullScreen = function (element) {
    if (element.requestFullscreen) {
        element.requestFullscreen();
    } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
    } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
    } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen();
    }

    //    //测试地图输出图片
    //    var img = GDYB.Page.curPage.map.getImage();
    //    $("#map_title_div").html(img);

    //    //测试地图输出图片
    //     var map = GDYB.Page.curPage.map;
    //    var size = map.getCurrentSize();
    //    var memCanvas = document.createElement("canvas");
    //    memCanvas.width = size.w;
    //    memCanvas.height = size.h;
    //    memCanvas.style.width = size.w+"px";
    //    memCanvas.style.height = size.h+"px";
    //    var memContext = memCanvas.getContext("2d");
    //    for(var i = 0; i<map.layers.length; i++){
    //        if(typeof(map.layers[i].canvasContext) != "undefined") {
    //            var layerCanvas = map.layers[i].canvasContext.canvas;
    //            memContext.drawImage(layerCanvas, 0, 0, layerCanvas.width, layerCanvas.height);
    //        }
    //        else if(typeof(map.layers[i].renderer.canvas) != "undefined"){
    //            var layerCanvas = map.layers[i].renderer.canvas.canvas;
    //            memContext.drawImage(layerCanvas, 0, 0, layerCanvas.width, layerCanvas.height);
    //        }
    //    }
    //    var img = new Image();
    //    img.src = memCanvas.toDataURL("image/png");
    //    $("#map_title_div").html(img);
};
//退出全屏
PageBase.prototype.exitFullScreen = function () {
    if (document.exitFullscreen) {
        document.exitFullscreen();
    } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
    } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
    }
};

//激活
PageBase.prototype.active = function () {
    var t = this;
    t.screen1Map();
    this.renderMenu(); //这个要放到创建地图之后,否则存在无法访问地图的问题
    $("#ScreenFull").unbind("click");
    $("#ScreenFull").click(function () {
        if (!t.isFullScreen) {
            t.launchFullScreen(document.documentElement);
            t.isFullScreen = true;
            this.innerHTML = "<img src=\"imgs/img_exitfullscreen.png\"/>";
        }
        else {
            t.exitFullScreen();
            t.isFullScreen = false;
            this.innerHTML = "<img src=\"imgs/img_launchfullscreen.png\"/>";
        }
    });
    this.bindBtnEvents();
};
//销毁
PageBase.prototype.destroy = function () {
    this.map = null;
    /* this.map1 = null;
     this.map2 = null;
     this.map3 = null;
     this.map4 = null;*/
    $("#map").html("");
    $("#menu_bd").html("");
    $(".datetimepicker").remove();
    $("#ZDYBDiv").remove();
    $("#ZDYBSet").remove();
    $("#ybcpsk_div").remove();
    $("#MJOView").remove();
    $("#menu").css("display", "block");//显示menu
    $(".delete").remove();
    //图层置为空
    GDYB.GridProductClass.layerLuoqu = null;
    GDYB.GridProductClass.layerLuoquCenter = null;
    GDYB.GridProductClass.layerLabel = null;
    GDYB.GridProductClass.layerFillRangeColor = null;
    GDYB.GridProductClass.layerPlot = null;
    GDYB.GridProductClass.layerPolygon = null;
    GDYB.GridProductClass.layerContour = null;
    GDYB.GridProductClass.layerClimaticRegion = null;
    GDYB.GridProductClass.layerMarkers = null;
    GDYB.GridProductClass.layerMagic = null;
    GDYB.GridProductClass.layerFocusArea = null;

    GDYB.TextDataClass.layerPlot = null;
    GDYB.TextDataClass.layerLabel = null;
    GDYB.TextDataClass.layerContour = null;
    GDYB.TextDataClass.layerPolygon = null;

    GDYB.MicapsDataClass.layerPlot = null;
    GDYB.MicapsDataClass.layerFillRangeColor = null;
    GDYB.MicapsDataClass.layerContour = null;

    GDYB.RadarDataClass.layerFillRangeColor = null;


    $("#map_div").find(".dragPanel").remove();
    GDYB.GDYBPage.myPanel_LQDZ = null;
    GDYB.GDYBPage.myPanel_QHDZ = null;
    GDYB.GDYBPage.myPanel_QSDZ = null;
    GDYB.GDYBPage.myPanel_FXDZ = null;
    GDYB.GDYBPage.myPanel_Tools = null;

    $("#map_title_div").html("");
    $("#map1_title_div").html("");
    $("#map2_title_div").html("");
    $("#map3_title_div").html("");
    $("#map4_title_div").html("");
};
PageBase.prototype.bindBtnEvents = function () { };