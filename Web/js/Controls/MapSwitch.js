//"地图切换"控件事件处理

//点击天地图
function tiandituOnclick(obj){
    var map = GDYB.Page.curPage.map;
    var baseLayer = GDYB.Page.curPage.baseLayer;
    var baseLayerLabel = GDYB.Page.curPage.baseLayerLabel;
    var clickImg = $(obj).find('.mapImg');
    var curImg = $("#curImg");
    var curSrc = curImg.attr("src");
    var clickSrc = clickImg.attr("src");
    curImg.attr("src",clickSrc);
    var curFlag = curImg.attr("flag");
    var clickFlag = clickImg.attr("flag");
    curImg.attr("flag",clickFlag);
    if(baseLayer != null)
        map.removeLayer(baseLayer);
    if(baseLayerLabel != null && map.getLayerIndex(baseLayerLabel) != -1){
        map.removeLayer(baseLayerLabel);
    }

    var layer = null;
    var layerLabel = null;
    if(clickFlag == "day") //白板图
    {
        layer = new WeatherMap.Layer.LocalTiledCacheLayerWhiteMap();
        map.addLayers([layer]);
        GDYB.Page.curPage.baseLayer = layer;
    }
    else if(clickFlag == "night") //黑板图
    {
        layer = new WeatherMap.Layer.LocalTiledCacheLayerBlackMap();
        map.addLayers([layer]);
        GDYB.Page.curPage.baseLayer = layer;
    }
    else if(clickFlag == "vec_local") //本地天地图-地图
    {
        layer = new WeatherMap.Layer.LocalTiledCacheLayerTDTMAP();
        layer.setIsBaseLayer(true);
        layerLabel = new WeatherMap.Layer.LocalTiledCacheLayerTDTMAP();
        layerLabel.dir="tianditu/map/maplabel/";
        layerLabel.setIsBaseLayer(false);
        map.addLayers([layerLabel, layer]);
    }
    else if(clickFlag == "ter_local") //本地天地图-地形
    {
        layer = new WeatherMap.Layer.LocalTiledCacheLayerTDTTER();
        layer.setIsBaseLayer(true);
        layerLabel = new WeatherMap.Layer.LocalTiledCacheLayerTDTTER();
        layerLabel.dir="tianditu/Terrain/Terrainlabel/";
        layerLabel.format="png";
        layerLabel.setIsBaseLayer(false);
        map.addLayers([layerLabel, layer]);
    }
    else if(clickFlag == "img_local") //本地天地图-影像
    {
        layer = new WeatherMap.Layer.LocalTiledCacheLayerTDTIMG();
        layer.setIsBaseLayer(true);
        layerLabel = new WeatherMap.Layer.LocalTiledCacheLayerTDTIMG();
        layerLabel.dir="tianditu/Sattelite/Sattelitelabel/";
        layerLabel.format="png";
        layerLabel.setIsBaseLayer(false);
        map.addLayers([layerLabel, layer]);
    }
    else if(clickFlag == "vec") //天地图矢量
    {
        layer = new WeatherMap.Layer.TianDiTuLayer();
        layer.setFormat("vec");
        layer.setName("tianDiTuLayer_vec");
        layer.setIsBaseLayer(true);
        layerLabel = new WeatherMap.Layer.TianDiTuLayer();
        layerLabel.setFormat("cva");
        layerLabel.setName("tianDiTuLayer_cva");
        layerLabel.setIsBaseLayer(false);
        map.addLayers([layerLabel, layer]);
    }
    else if(clickFlag == "ter") //天地图地形
    {
        var layer = new WeatherMap.Layer.TianDiTuLayer();
        layer.setFormat("ter");
        layer.setName("tianDiTuLayer_ter");
        var layerLabel = new WeatherMap.Layer.TianDiTuLayer();
        layerLabel.setFormat("cta");
        layerLabel.setName("tianDiTuLayer_cta");
        layerLabel.setIsBaseLayer(false);
        map.addLayers([layerLabel, layer]);
    }
    else if(clickFlag == "img") //天地图影像
    {

    }
    GDYB.Page.curPage.baseLayer = layer;
    GDYB.Page.curPage.baseLayerLabel  = layerLabel;
}