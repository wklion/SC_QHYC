/**
 * 滤镜工具，根据给定的格点值范围，高亮或闪烁显示过滤的格点，并能对其进行订正。
 * 是对魔术棒功能的补充，它针对整个图层范围
 * Created by zouwei on 2016/3/30.
 */
function FilterTool(){
    this.datasetGrid = null;
    this.layerFillRangeColor = null;
    this.animationId = null;

    this.refresh = function(minValue, maxValue) {
        var t = this;

        //停止前一次动画
        t.stopAnimation();

        //创建数据集
        var dgCurrent = GDYB.GridProductClass.datasetGrid;
        var left = dgCurrent.left;
        var top = dgCurrent.top;
        var right = dgCurrent.right;
        var bottom = dgCurrent.bottom;
        var rows = dgCurrent.rows;
        var cols = dgCurrent.cols;
        var noDataValue = dgCurrent.noDataValue;
        t.datasetGrid = new WeatherMap.DatasetGrid(left, top, right, bottom, rows, cols);
        t.datasetGrid.noDataValue = noDataValue;
        var grid = [];
        for(var i=0;i<rows;i++){
            for(var j=0;j<cols;j++) {
                var srcValue = dgCurrent.getValue(0, j, i);
                if(minValue <= srcValue && srcValue<maxValue)
                    grid.push(1);
                else
                    grid.push(noDataValue);
            }
        }
        t.datasetGrid.grid = grid;
        t.datasetGrid.dMin = 1;
        t.datasetGrid.dMax = 1;

        //创建图层
        var map = GDYB.Page.curPage.map;
        if(t.layerFillRangeColor == null)
        {
            t.layerFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer(
                "FilterToolLayer",
                {
                    "radius":40,
                    "featureWeight":"value",
                    "featureRadius":"geoRadius"
                }
            );
            t.layerFillRangeColor.isShowGridline = false;
            t.layerFillRangeColor.isShowLabel = false;
            t.layerFillRangeColor.isAlwaySmooth = false;
            t.layerFillRangeColor.isSmooth = false;
            t.layerFillRangeColor.items = [
                {start:1,end:1,startColor:{red:255,green:255,blue:0},endColor:{red:255,green:255,blue:0}}
            ];
            map.addLayers([t.layerFillRangeColor]);
        }
        t.layerFillRangeColor.visibility = true;

        //关联数据集
        t.layerFillRangeColor.setDatasetGrid(t.datasetGrid);

        //开启新的动画
        t.animationId = setInterval(function(){
            t.layerFillRangeColor.visibility = !t.layerFillRangeColor.visibility;
            t.layerFillRangeColor.refresh();
        }, 300);

    };

    this.clear = function(){
        var t = this;
        t.stopAnimation();
        if(t.layerFillRangeColor != null){
            t.layerFillRangeColor.visibility = false;
            t.layerFillRangeColor.refresh();
        }
        t.datasetGrid = null;
    };

    this.updateGrid = function(newValue, method){
        var t = this;
        if(t.datasetGrid == null)
            return;
        var dg = GDYB.GridProductClass.datasetGrid;
        if(dg == null)
            return;
        var maxThanZero = GDYB.GridProductClass.currentElement == "r3" ||
            GDYB.GridProductClass.currentElement == "r12" ||
            GDYB.GridProductClass.currentElement == "rh" ||
            GDYB.GridProductClass.currentElement == "tcc" ||
            GDYB.GridProductClass.currentElement == "vis";
        var rows = dg.rows;
        var cols = dg.cols;
        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < cols; j++) {
                var val = dg.getValue(0, j, i);
                if(t.datasetGrid.getValue(0, j, i) == 1) {
                    if(method == 0)
                        val = newValue;
                    else if(method == 1)
                        val += newValue;
                    else if(method == 2)
                        val *= (1+newValue);
                    if (val < 0 && maxThanZero)
                        val = 0;
                    dg.setValue(0, j, i, Math.round(val*10)/10);
                }
            }
        }
        GDYB.GridProductClass.layerFillRangeColor.refresh();

        GDYB.GridProductClass.dataCache.setDataStatus(GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion, GDYB.GridProductClass.currentDateTime, GDYB.GridProductClass.currentElement, GDYB.GridProductClass.currentHourSpan, 1); //更新已修改状态
        GDYB.GridProductClass.dataStack.push(GDYB.GridProductClass.datasetGrid); //压入堆栈
        GDYB.GridProductClass.updateStationLayer();

        t.refresh(GDYB.GridProductClass.currentGridValueDown, GDYB.GridProductClass.currentGridValueUp);
    };

    this.stopAnimation = function(){
        var t = this;
        if(t.animationId == null)
            return;
        clearInterval(t.animationId);
        t.animationId = null;
    };
}