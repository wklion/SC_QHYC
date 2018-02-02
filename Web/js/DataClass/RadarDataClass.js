/*
* SWAN雷达数据类
* by zouwei, 2015-08-25
* */
function RadarDataClass() {
    this.datasetGrid = null;            //格点数据集
    this.layerFillRangeColor = null;    //填色图层
    this.currentElement = null;         //当前要素
    this.currentLevel = null;           //当前仰角
    this.currentDateTime = null;        //当前时间

    this.displayRadarData=function(recall, element, level, datetime){
        this.currentElement = element;
        this.currentLevel = level;
        this.currentDateTime = datetime;
        var map = GDYB.Page.curPage.map;
        this.addLayer(map);
        this.addGrid(recall, map);
    };

    this.addLayer = function(pMap){
        if(this.layerFillRangeColor == null) {
            this.layerFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer(
                "RadarMap",
                {
                    "radius": 40,
                    "featureWeight": "value",
                    "featureRadius": "geoRadius"
                }
            );
            var items = heatMap_RadarStyles;
            this.layerFillRangeColor.items = items;
            this.layerFillRangeColor.isSmooth = true;
            this.layerFillRangeColor.isAlwaySmooth = true;
            this.layerFillRangeColor.isShowGridline = false;
            this.layerFillRangeColor.isShowLabel = false;
            pMap.addLayers([this.layerFillRangeColor]);
        }
    };

    this.addGrid = function(recall, pMap){
        var t = this;

        $("#div_progress_title").html("正在下载数据...");
        $("#div_progress").css("display", "block");

        getGrid(function(datasetGrid){
            //if(datasetGrid != null && datasetGrid.grid.length > 0)  //为空也要赋值，清空数据
            {
                t.datasetGrid = datasetGrid;
                if(t.layerFillRangeColor != null)
                    t.layerFillRangeColor.setDatasetGrid(datasetGrid);
                recall&&recall();
            }
        },null);

        function getGrid(recall){
            var t1 = new Date().getTime();
            var url=dataSericeUrl+"services/SwanRadarService/getGrid";
            $.ajax({
                data:{"para":"{element:'"+ t.currentElement + "',level:'"+ t.currentLevel + "',datetime:'"+ t.currentDateTime + "'}"},
                url:url,
                dataType:"json",
                success:function(data){
                    var datasetGrid = null;
                    try
                    {
                        if(typeof(data) != "undefined")
                        {
                            var dvalues = data.dvalues;
                            if(dvalues != null && dvalues.length > 0)
                            {
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
                                    }
                                    grid.push(gridLine);
                                }
                                datasetGrid.grid = grid;
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
                    recall&&recall(datasetGrid);
                },
                error: function (e) {
                    $("#div_progress").css("display", "none");
                },
                type:"POST"
            });
        }
    };
}