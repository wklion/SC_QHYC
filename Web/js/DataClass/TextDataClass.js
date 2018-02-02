/*
 * Micaps数据类
 * by zouwei, 2015-08-25
 * */
function TextDataClass() {
    this.isInitialized = false;         //是否已初始化
    this.layerPlot = null;              //填图图层
    this.currentElement = null;         //当前要素
    this.currentDateTime = null;        //当前时次
    this.layerLabel = null;             //填值图层
    this.layerPlot = null;              //填图图层
    this.layerContour = null;           //等值线图层
    this.layerPolygon = null;           //色斑图

    this.displayTextData=function(recall, element, datetime){
        this.currentElement = element;
        this.currentDateTime = datetime;

        var map = GDYB.Page.curPage.map;
        this.addLayer(map);
        this.addData(recall, map);
    };

    this.addLayer = function(pMap){
        if(this.layerPolygon == null)
        {
            this.layerPolygon = new WeatherMap.Layer.Vector("layerIsoSurfaceTextData", {renderers: ["Canvas"]});
            pMap.addLayers([this.layerPolygon]);
        }

        //等值线图层
        if(this.layerContour == null)
        {
            this.layerContour = new WeatherMap.Layer.Vector("layerContour", {renderers: ["Contour"]});
            this.layerContour.renderer.labelField = "值";
            this.layerContour.style = {
            fontFamily:"Arial",
            fontColor:"#333",
            fontSize:"14px",
            fontWeight:"bold",
            strokeColor: "#ff0000",
            strokeWidth: 1.0
            };
            pMap.addLayers([this.layerContour]);
        }

        //填值/填图
        if(this.layerPlot == null) {
            this.layerPlot = new WeatherMap.Layer.Vector("layerPlotTextData", {renderers: ["Plot"]});
            this.layerPlot.renderer.plotWidth = 36;
            this.layerPlot.renderer.plotHeight = 20;
            pMap.addLayers([this.layerPlot]);
        }
        //设置统一风格
        this.layerPlot.style = {
            pointRadius: 1.0
        };
        //设置子项风格
        this.layerPlot.renderer.styles = [
            {
                field:this.currentElement,
                type:"label",
                visible:"true",
                offsetX: 0,
                offsetY: 0,
                rotationField:null,
                decimal:1,
                noDataValue:0.0,
                style: {
                    labelAlign:"rb",
                    fontFamily:"Arial",
                    fontColor:"rgb(0, 0, 128)",
                    fontSize:"12px",
                    fill: false,
                    stroke: false
                },
                symbols:null
            }
        ];
    };

    /*
     * 添加数据
     */
    this.addData = function(recall, pMap){
        var t = this;

        getData(function(features){
            t.layerPlot.removeAllFeatures();
            t.layerContour.removeAllFeatures();
            t.layerPolygon.removeAllFeatures();
            t.layerPlot.addFeatures(features);
            if(features == null || features.length == 0)
                t.layerPlot.redraw();
            else
                t.addContour(recall, pMap);
            recall&recall();
        },null);

        function getData(recall){
            var t1 = new Date().getTime();
            var url=dataSericeUrl+"services/TextDataService/getData";
            $.ajax({
                data:{"para":"{element:'"+ t.currentElement + "',datetime:'"+ t.currentDateTime + "'}"},
                url:url,
                dataType:"json",
                success:function(data){
                    var labelFeatures = [];
                    try
                    {
                        if(typeof(data) != "undefined")
                        {
                            if(data.hasOwnProperty("featureUriList")) //矢量数据
                            {
                                var result = GDYB.FeatureUtilityClass.getRecordsetFromJson(data);
                                labelFeatures = result.features;
                            }
                        }
                        else
                        {
                            //alert("无数据");
                        }
                    }
                    catch (err)
                    {
                        alert(err.description);
                    }
                    recall&&recall(labelFeatures);
                },
                type:"POST"
            });
        }
    };

    /*
     * 添加等值线
     */
    this.addContour = function(recall, pMap) {
        var t = this;
        //获取填色风格
        var element = t.currentElement;
        var styles = null;
        if(element == "temp_1h" || element == "maxTemp_24h" || element == "minTemp_24h")
            styles = tempStyles;
        else if(element == "rh_1h")
            styles = rhStyles;
        else if(element == "maxWS_1h")
            styles = windStyles;
        if(element == "rain_24h" || element == "rain_20hToNow" || element == "rain_08hToNow")
            styles = rain24hStyles;
        if(element == "rain_1h")
            styles = rain1hStyles;
        if(element == "rain_3h")
            styles = rain3hStyles;
        if(element == "rain_6h")
            styles = rain6hStyles;
        if(element == "rain_12h")
            styles = rain12hStyles;
        var strContourValues = "";
        if(t.currentElement == "rain_1h" || t.currentElement == "rain_3h" || t.currentElement == "rain_6h" || t.currentElement == "rain_12h" || t.currentElement == "rain_24h"
            || t.currentElement == "rain_20hToNow" || t.currentElement == "rain_08hToNow") //测试客户端传等值线值
        {
            for(var i=0; i<styles.length; i++)
            {
                if(i != 0)
                    strContourValues+=styles[i][0]+" ";
            }
            if(strContourValues.length > 0)
                strContourValues = strContourValues.substring(0, strContourValues.length-1);
        }
        else if(t.currentElement == "rh_1h")
            strContourValues = "10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0";
        var url = dataSericeUrl + "services/TextDataService/getContour";
        var element = t.currentElement;
        $.ajax({
            url: url,
            data:{"para":"{element:'"+ t.currentElement + "',datetime:'"+ t.currentDateTime + "',contourValues:'" + strContourValues + "'}"},
            dataType: "json",
            success: function (data) {
                t.layerContour.removeAllFeatures();
                t.layerPolygon.removeAllFeatures();
                var result = GDYB.FeatureUtilityClass.getRecordsetFromJson(data);
                var features = [];
                var len = result.features.length;
                for (var i = 0; i < len; i++) {
                    var feature = result.features[i];
                    features.push(feature);
                }
                t.layerContour.addFeatures(features);
                //recall&&recall();
                t.addIsoSurface(recall, pMap);
            },
            error: function(e) {
                recall&&recall();
            },
            type: "POST"
        });
    };

    /*
    * 添加色斑图
    * */
    this.addIsoSurface = function (recall, pMap) {
        var t = this;
        //获取填色风格
        var element = t.currentElement;
        var styles = null;
        if(element == "temp_1h" || element == "maxTemp_24h" || element == "minTemp_24h")
            styles = tempStyles;
        else if(element == "rh_1h")
            styles = rhStyles;
        else if(element == "maxWS_1h")
            styles = windStyles;
        if(element == "rain_24h" || element == "rain_20hToNow" || element == "rain_08hToNow")
            styles = rain24hStyles;
        if(element == "rain_1h")
            styles = rain1hStyles;
        if(element == "rain_3h")
            styles = rain3hStyles;
        if(element == "rain_6h")
            styles = rain6hStyles;
        if(element == "rain_12h")
            styles = rain12hStyles;
        var strContourValues = "";
        if(t.currentElement == "rain_1h" || t.currentElement == "rain_3h" || t.currentElement == "rain_6h" || t.currentElement == "rain_12h" || t.currentElement == "rain_24h"
            || t.currentElement == "rain_20hToNow" || t.currentElement == "rain_08hToNow") //测试客户端传等值线值
        {
            for(var i=0; i<styles.length; i++)
            {
                if(i != 0)
                    strContourValues+=styles[i][0]+" ";
            }
            if(strContourValues.length > 0)
                strContourValues = strContourValues.substring(0, strContourValues.length-1);
        }
        else if(t.currentElement == "rh_1h")
            strContourValues = "10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0";
        var url = dataSericeUrl + "services/TextDataService/getIsoRegion";
        $.ajax({
            url: url,
            data:{"para":"{element:'"+ t.currentElement + "',datetime:'"+ t.currentDateTime + "',contourValues:'" + strContourValues + "'}"},
            dataType: "json",
            success: function (data) {
                t.layerPolygon.removeAllFeatures();

                //初始化数据
                var result = GDYB.FeatureUtilityClass.getRecordsetFromJson(data);
                var features = [];
                var len = result.features.length;
                for (var i = 0; i < len; i++) {
                    var feature = result.features[i];
                    var fAttributes = feature.attributes;
                    fAttributes['FEATUREID'] = i;
                    var fromMinValue = true;
                    var value = fAttributes['最小值'];
                    if (value== undefined || value==-9999)
                    {
                        value = fAttributes['最大值'];
                        fromMinValue = false;
                    }
                    //value = fAttributes['dMinZValue']; //如果服务端提取等值线采用setInterval，字段是DMINVALUE；反之，如果是setExpectedZValues，字段是dMinZValue
                    if(styles != null)
                    {
                        for(var j = 0;j < styles.length; j++)
                        {
                            var minValue = styles[j][0];
                            var maxValue = styles[j][1];
                            if(fromMinValue && value >= minValue && value < maxValue       //因为value是取下限，所以这里是>=minValue
                                || !fromMinValue && value > minValue && value <= maxValue) //因为value是取上限，所以这里是<=maxValue
                            {
                                feature.style = styles[j][2];
                                break;
                            }
                        }
                    }
                    features.push(feature);
                }
                t.layerPolygon.addFeatures(features);
                recall&&recall();
            },
            error: function(e) {
                recall&&recall();
            },
            type: "POST"
        });
    };

    this.clearData = function()
    {
        if(this.layerPlot != null)
            this.layerPlot.removeAllFeatures();
        if(this.layerLabel != null)
            this.layerLabel.removeAllFeatures();
        if(this.layerContour != null)
            this.layerContour.removeAllFeatures();
        if(this.layerPolygon != null)
            this.layerPolygon.removeAllFeatures();
    };
}