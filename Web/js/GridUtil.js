/**
 * @author: wangkun
 * @date:   2017/4/19.
 * @description 格点相关
 */
function GridUtil() {
    this._init_();
}
GridUtil.prototype = {
    constructor: GridUtil,
    _init_: function () {
    },
    /**
     * @author:wangkun
     * @date:2017-04-19
     * @param:原始格点
     * @return:新的格点数据
     * @description:格点裁剪
     */
    clip: function (srcGrid) {
        var result = jQuery.extend({}, srcGrid);
        var oldLeft = srcGrid.left;
        var oldRight = srcGrid.right;
        var oldTop = srcGrid.top;
        var oldBottom = srcGrid.bottom;
        var rows = srcGrid.rows;
        var cols = srcGrid.cols;
        var step = rows / (oldTop - oldBottom);
        //获取当前区域
        var activeAreaID = window.localStorage.getItem("activearea");
        var bounds = null;
        if (activeAreaID === "sc") {//四川
            bounds = MAP_CONFIG.SC.RectangleBound;
        }
        else{
            bounds = MAP_CONFIG.XN.RectangleBound;
        }
        var leftOff = (bounds[0] - oldLeft) * step;//左偏
        var bottomOff = (bounds[1] - oldBottom) * step;//下偏
        var rightOff = (oldRight - bounds[2]) * step;//右偏
        var topOff = (oldTop - bounds[3]) * step;//上偏
        var newCols=cols - leftOff - rightOff;//新的列
        var newRows=rows - topOff - bottomOff;//新的行
        var values=[];
        for(var i = topOff;i < rows - bottomOff;i++){
            for(var j = leftOff;j < cols - rightOff;j++){
                var index = i*cols+j;
                var val = srcGrid.dvalues[index];
                values.push(val);
            }
        }
        result.left = bounds[0];
        result.bottom = bounds[1];
        result.right = bounds[2];
        result.top = bounds[3];
        result.rows = newRows;
        result.cols = newCols;
        result.dvalues = values;
        return result;
    }
}