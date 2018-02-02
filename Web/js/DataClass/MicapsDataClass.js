/*
 * Micaps数据类
 * by zouwei, 2015-08-25
 * */
function MicapsDataClass() {
    this.isInitialized = false;         //是否已初始化
    this.layerPlot = null;              //填图图层
    this.currentElement = null;         //当前要素
    this.currentLevel = null;           //当前层次
    this.currentDateTime = null;        //当前时次
    this.currentHourSpan = null;        //当前时效

    this.datasetGrid = null;            //格点数据集
    this.layerFillRangeColor = null;    //填色图层

    this.layerContour = null;           //等值线图层

    this.displayMicapsData=function(recall, element, level, datetime, hourspan){
        this.currentElement = element;
        this.currentLevel = level;
        this.currentDateTime = datetime;
        this.currentHourSpan = hourspan;

        var map = GDYB.Page.curPage.map;
//        map.setCenter(new WeatherMap.LonLat(110, 35), 3); //设置可视范围
        this.addLayer(map);
        this.addData(recall, map);
        if(!(this.currentElement == "surface_plot" || this.currentElement == "high_plot" ||
            this.currentElement == "fy2_ir1" || this.currentElement == "fy2_ir2" || this.currentElement == "fy2_ir3"
                || this.currentElement == "fy2_ir4" || this.currentElement == "fy2_vis"))
                this.addContour(recall, map);
    };

    this.addLayer = function(pMap){
        //填图图层
        if(this.layerPlot == null) {
            this.layerPlot = new WeatherMap.Layer.Vector("layerMicapsPlot", {renderers: ["Plot"]});
            pMap.addLayers([this.layerPlot]);

            //对象选中事件
            var t = this;
            var callbacks = {
                renderer: null,

                over: function (currentFeature) {

                },

                click: function (currentFeature) {
                    var plotRenderer = this.layer.renderer; //this是SelectFeature
                    if (plotRenderer.listDrawnID.indexOf(currentFeature.id) >= 0)
                        return;
                    plotRenderer.drawGeometry(currentFeature.geometry, plotRenderer.features[currentFeature.id][1], currentFeature.id);
                },

                out: function (currentFeature) {
                }
            };

            var selectFeature = new WeatherMap.Control.SelectFeature(this.layerPlot,
                {
                    callbacks: callbacks
                });
            pMap.addControl(selectFeature);
            selectFeature.activate();
        }

        //填色图层
        if(this.layerFillRangeColor == null)
        {
                this.layerFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer(
                    "layerMicapsGrid",
                    {
                        "radius": 40,
                        "featureWeight": "value",
                        "featureRadius": "geoRadius"
                    }
                );
                this.layerFillRangeColor.isSmooth = true;
                this.layerFillRangeColor.isShowGridline = false;
                this.layerFillRangeColor.isShowLabel = false;
                pMap.addLayers([this.layerFillRangeColor]);

                //等值线图层
                this.layerContour = new WeatherMap.Layer.Vector("layerContour", {renderers: ["Contour"]});
                this.layerContour.renderer.labelField = "值";
                pMap.addLayers([this.layerContour]);
        }

        //图层风格设置
        if(this.currentElement == "surface_plot" || this.currentElement == "surface")
        {
            this.layerPlot.renderer.styles = plotStyles_surface;
            this.layerPlot.renderer.plotWidth = 90;
            this.layerPlot.renderer.plotHeight = 80;
        }
        else if(this.currentElement == "high_plot" || this.currentElement == "high")
        {
            this.layerPlot.renderer.styles = plotStyles_hight;
            this.layerPlot.renderer.plotWidth = 80;
            this.layerPlot.renderer.plotHeight = 70;
        }

        if(this.currentElement == "fy2_ir1")
            this.layerFillRangeColor.items = heatMap_IR1Styles;
        else if(this.currentElement == "fy2_ir2")
            this.layerFillRangeColor.items = heatMap_IR2Styles;
        else if(this.currentElement == "fy2_ir3")
            this.layerFillRangeColor.items = heatMap_IR3Styles;
        else if(this.currentElement == "fy2_ir4")
            this.layerFillRangeColor.items = heatMap_IR4Styles;
        else if(this.currentElement == "fy2_vis")
            this.layerFillRangeColor.items = heatMap_FYVISStyles;
        else
            this.layerFillRangeColor.items = heatMap_TempStyles;

        //查看栅格值
        if(!this.isInitialized) {
            this.isInitialized = true;
            var t = this;
            var bDrag = false;
            pMap.events.register("mousemove", pMap, function (event) {
                if (!bDrag && t.datasetGrid != null) {
                    if(t.currentElement == "fy2_ir1" || t.currentElement == "fy2_ir2" || t.currentElement == "fy2_ir3"
                        || t.currentElement == "fy2_ir4" || t.currentElement == "fy2_vis")
                        return;
                    $("#div_showGridValue").css("display", "block");
                    $("#div_showGridValue").css("left", event.xy.x + 20);
                    $("#div_showGridValue").css("top", event.xy.y + 20);
                    var lonlat = this.getLonLatFromPixel(event.xy);
                    var dValue = "-";
                    var pt = t.datasetGrid.xyToGrid(lonlat.lon, lonlat.lat);
                    dValue = t.datasetGrid.grid[pt.y][pt.x].z;
                    $("#div_showGridValue").html("经度：" + Math.round(lonlat.lon * 10000) / 10000 + "<br/>" +
                        "纬度：" + Math.round(lonlat.lat * 10000) / 10000 + "<br/>" +
                        "格点：" + Math.round(dValue * 10) / 10);
                }
                else {
                    $("#div_showGridValue").css("display", "none");
                }
            });

            pMap.events.register("movestart", pMap, function (event) {
                bDrag = true;
                $("#div_showGridValue").css("display", "none");
            });

            pMap.events.register("moveend", pMap, function (event) {
                bDrag = false;
            });
        }
    };

    /*
     * 添加数据
     */
    this.addData = function(recall, pMap){
        var t = this;

        $("#div_progress_title").html("正在下载数据...");
        $("#div_progress").css("display", "block");

        getData(function(features, datasetGrid){
            if(features != null && features.length > 0)
            {
                t.layerPlot.removeAllFeatures();
                t.layerPlot.addFeatures(features);
            }
            else{
                t.layerPlot.removeAllFeatures();
            }
            //else if(datasetGrid != null && datasetGrid.grid.length > 0) //为空也要赋值，清空数据
            {
                t.datasetGrid = datasetGrid;
                if(t.layerFillRangeColor != null) {
                    //根据值域，重组风格
                    if (typeof(datasetGrid) != "undefined" && datasetGrid != null && t.currentElement.substring(0, 6) == "physic") {
                        var dMin = Math.floor(datasetGrid.dMin * 10) / 10;
                        var dMax = Math.floor(datasetGrid.dMax * 10 + 1.0) / 10; //向上取十分位整
                        var items = t.layerFillRangeColor.items;
                        var dStep = (dMax - dMin) / items.length;
                        for (var i = 0; i < items.length; i++) {
                            items[i].start = dMin + dStep * i;
                            items[i].end = dMin + dStep * (i + 1);
                        }
                    }
                    t.layerFillRangeColor.setDatasetGrid(datasetGrid);
                    t.layerFillRangeColor.refresh();
                }
            }
            recall&&recall();
        },null);

        function getData(recall){
            var t1 = new Date().getTime();
            var url=dataSericeUrl+"services/MicapsService/getData";
            $.ajax({
                data:{"para":"{element:'"+ t.currentElement + "',level:'"+ t.currentLevel + "',hourspan:"+ t.currentHourSpan + ",datetime:'"+ t.currentDateTime + "'}"},
                url:url,
                dataType:"json",
                success:function(data){
                    var features = null;
                    var datasetGrid = null;
                    try
                    {
                        if(typeof(data) != "undefined")
                        {
                            if(data.hasOwnProperty("featureUriList")) //矢量数据
                            {
                                var result = GDYB.FeatureUtilityClass.getRecordsetFromJson(data);
                                features = result.features;
                            }
                            else if(data.hasOwnProperty("dvalues")) //格点数据
                            {
                                var dvalues = data.dvalues;
                                if(dvalues != null && dvalues.length > 0)
                                {
                                    var dMin=9999;
                                    var dMax=-9999;
                                    datasetGrid = new WeatherMap.DatasetGrid(data.left, data.top, data.right, data.bottom, data.rows, data.cols);
                                    datasetGrid.noDataValue = data.noDataValue;
                                    var grid = [];
                                    for(var i=0;i<data.rows;i++){
                                        var gridLine = [];
                                        var nIndexLine = data.cols*i;
                                        for(var j=0;j<data.cols;j++){
                                            var nIndex = nIndexLine + j;
                                            var z = dvalues[nIndex];
                                            var x = data.left + j * datasetGrid.deltaX;
                                            var y = data.top - i * datasetGrid.deltaY;
                                            gridLine.push({
                                                "z":z,
                                                "x":x,
                                                "y":y
                                            });
                                            if(z != 9999 && z != -9999)
                                            {
                                                if(z < dMin)
                                                    dMin = z;
                                                if(z > dMax)
                                                    dMax = z;
                                             }
                                        }
                                        grid.push(gridLine);
                                    }
                                    datasetGrid.grid = grid;
                                    datasetGrid.dMin = dMin;
                                    datasetGrid.dMax = dMax;
                                }
                            }
                        }
                        else
                        {
                            alertFuc("无数据");
                        }
                    }
                    catch (err)
                    {
                        alert(err.description);
                    }
                    $("#div_progress").css("display", "none");
                    recall&&recall(features, datasetGrid);
                },
                error: function (e) {
                    $("#div_progress").css("display", "none");
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
        var url = dataSericeUrl + "services/MicapsService/getContour";
        var element = t.currentElement;
        $.ajax({
            url: url,
            data:{"para":"{element:'"+ t.currentElement + "',level:'"+ t.currentLevel + "',hourspan:"+ t.currentHourSpan + ",datetime:'"+ t.currentDateTime + "'}"},
            dataType: "json",
            success: function (data) {
                t.layerContour.removeAllFeatures();

				if(typeof(data) != "undefined"){
					//初始化数据
					var result = GDYB.FeatureUtilityClass.getRecordsetFromJson(data);
					var features = [];
					var len = result.features.length;
					for (var i = 0; i < len; i++) {
						var feature = result.features[i];
						features.push(feature);
					}
					t.layerContour.addFeatures(features);
				}
                recall&&recall();
            },
            error: function(e) {
                recall&&recall();
            },
            type: "POST"
        });
    };
}