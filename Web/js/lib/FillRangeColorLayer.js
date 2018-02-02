﻿/* COPYRIGHT 2012 WeatherMap
 * 本程序只能在有效的授权许可下使用。
 * 未经许可，不得以任何手段擅自使用或传播。*/

/**
 * @requires WeatherMap/BaseTypes/Class.js
 * @requires WeatherMap/Util.js
 * @requires WeatherMap/Layer.js
 */

/**
 * Class: WeatherMap.Layer.FillRangeColorLayer
 *    热点填色图。
 *
 * Inherits from:
 *  - <WeatherMap.Layer.FillRangeColorLayer>
 */
WeatherMap.Layer.FillRangeColorLayer = WeatherMap.Class(WeatherMap.Layer.FillColorLayer, {

    /**
     * APIProperty: items
     * {Array} 权重数组
     *
     * (start code)
     * 用于表示数据权重的渐变，此参数不设置的情况颜色由绿（低权重）到红（高权重）。
     * 如需要设置可以为如下两种方式：
     *
     * //例1：
     * //feature.attributes中表示权重的字段为height,则在HeatMapWeight的featureWeight参数赋值为"height"
     * feature1.attributes.height = 4.0;
     * feature2.attributes.height = 1.0;
     * var heatMapLayer = new WeatherMap.Layer.HeatMapWeight("heatmaplayer",{"featureWeight":"height"});
     * //items数组形如：
     * var items = [
     *                     {
     *                       start:0,
     *                       end:2,
     *                       startColor:new  WeatherMap.REST.ServerColor(255,0,0),
     *                       endColor:new  WeatherMap.REST.ServerColor(200,0,0)
     *                      },
     *                     {
     *                        start:2,
     *                        end:4,
     *                        startColor:new  WeatherMap.REST.ServerColor(200,0,0),
     *                        endColor:new  WeatherMap.REST.ServerColor(100,0,0)
     *                      }
     *                 ];
     *
     * heatMapLayer.items = items;
     * heatMapLayer.addFeatures([feature1,feature2]);
     *
     * //例2：
     * //feature.attributes中表示权重的字段为 temperature ,则在HeatMapWeight的featureWeight参数赋值为"temperature"
     * feature1.attributes.temperature = 3.0;
     * feature2.attributes.temperature = 0.0;
     * var heatMapLayer = new WeatherMap.Layer.HeatMapWeight("heatmaplayer",{"featureWeight":"temperature"});
     * //startColor和endColor可以为任意的object对象，但是必须有red、green、blue三个属性。
     * var items = [
     *                     {
     *                       start:0,
     *                       end:2,
     *                       startColor:{red:0,green:0,blue:0},
     *                       endColor:{red:100,green:0,blue:0}
     *                      },
     *                     {
     *                        start:2,
     *                        end:4,
     *                        startColor:{red:100,green:0,blue:0},
     *                        endColor:{red:200,green:0,blue:0}
     *                      }
     *                 ];
     *
     * heatMapLayer.items = items;
     * heatMapLayer.addFeatures([feature1,feature2]);
     * (end)
     */
    items:null,
    /**
     * Property: itemsC
     * {Array} items经过计算后的权重数组，权重数据都在0-1之间，
     */
    itemsC:null,

    //是否渐变
    isGradient:false,

    /**
     * Constructor: WeatherMap.Layer.HeatMapWeight
     * 创建一个热点权重图层。
     *
     * (start  code)
     * var layer = new WeatherMap.Layer.HeatMapWeight();
     * var items=[
     * {
     *        start:0,
     *        end:2,
     *        startColor:{red:0,green:0,blue:0} ,
     *        endColor:{red:100,green:0,blue:0}
     * }  ,{
     *     start:2,
     *     end:4,
     *     startColor:{red:100,green:0,blue:0},
     *     endColor:{red:200,green:0,blue:0}
     * }
     *  ];
     *  layer.items=items
     *  (end)
     *
     * Parameters:
     * name - 此图层的图层名 {String} 
     * options - {Object} 设置此类上没有默认值的属性。
     *
     * Returns:
     * {<WeatherMap.Layer.HeatMapWeight>} 新的热点权重图层。
     */
    initialize: function(name, options) {
        WeatherMap.Layer.FillColorLayer.prototype.initialize.apply(this, arguments);
    },



    /**
     * APIMethod: destroy
     * 销毁图层，释放资源。
     */
    destroy: function() {
        this.items = null;
        this.itemsC = null;
        WeatherMap.Layer.FillColorLayer.prototype.destroy.apply(this, arguments);
    },
    
    /**
     * Method: drawHeatPoints
     * 重写父类方法，完成绘制热点图的初始工作，逐一完成热点的渲染
     *
     * Parameters:
     * bounds - {<WeatherMap.Bounds>} 当前显示范围
     */
    drawHeatPoints:function(){
        var len,me = this;
        //计算itemsC，此处将items里面的权重计算到0-1范围内
        if(this.items && WeatherMap.Util.isArray(this.items) && this.items.length>1)
        {
            len = this.items.length;
            this.itemsC = [];
            var min = this.items[0].start;
            var max = this.items[len-1].end;
            //如果items里面的权重范围大，则以items里面的为准
            this.minWeight = this.minWeight<min? this.minWeight : min;
            this.maxWeight = this.maxWeight>max ? this.maxWeight : max;
            this.tempValue = this.maxWeight - this.minWeight;

//            for(var i = 0;i<len;i++)
//            {
//                var start = (this.items[i].start - this.minWeight)/this.tempValue;
//                var end = (this.items[i].end - this.minWeight)/this.tempValue;
//                var item = {
//                    start:start,
//                    end:end,
//                    startColor:this.items[i].startColor,
//                    endColor:this.items[i].endColor
//                }
//                this.itemsC.push(item);
//            }
        }
        WeatherMap.Layer.FillColorLayer.prototype.drawHeatPoints.apply(me, arguments);
    },

    convertValueToColor:function(value){
        var r = 255, g = 255, b = 255,a = 0,me = this;
        if(me.items)
        {
            var len = me.items.length;
            for(var i = 0;i<len;i++)
            {
                if(value >= me.items[i].start && value < me.items[i].end ||
                    i==(len-1) && value >= me.items[i].start && value <= me.items[i].end ||
                    me.items[i].start == me.items[i].end && value == me.items[i].start)
                {
                    var startC = me.items[i].startColor;
                    if(typeof(me.items[i].visible) == "undefined" || me.items[i].visible) { //可见，typeof会不会影响效率
                        if (i == len - 1) //最大值
                        {
                            r = startC.red;
                            g = startC.green;
                            b = startC.blue;
                            a = me.alpha;
                        }
                        else {
                            if (this.isSmooth && me.items[i].start != me.items[i].end) {
                                var endC = me.items[i + 1].startColor;
                                if(me.isGradient) {
                                    r = startC.red + (endC.red - startC.red) / (me.items[i + 1].start - me.items[i].start) * (value - me.items[i].start);
                                    g = startC.green + (endC.green - startC.green) / (me.items[i + 1].start - me.items[i].start) * (value - me.items[i].start);
                                    b = startC.blue + (endC.blue - startC.blue) / (me.items[i + 1].start - me.items[i].start) * (value - me.items[i].start);
                                }
                                else {
                                    r = startC.red;
                                    g = startC.green;
                                    b = startC.blue;
                                }
                                a = me.alpha;
                            }
                            else {
                                r = startC.red;
                                g = startC.green;
                                b = startC.blue;
                                a = me.alpha;
                            }
                        }
                    }
                    else //隐藏
                    {
                        r = startC.red;
                        g = startC.green;
                        b = startC.blue;
                        a = 0;
                    }
                    break;
                }
            }
        }
        else
        {

        }
        return {"r": r, "g": g, "b": b, "a": a};
    },

    //带有tag属性的方法。之所以单独弄一个方法，是为了保证效率，不用频繁判断tag是否undefined
    convertValueToColorWithTag:function(value, tag){
        var r = 255, g = 255, b = 255,a = 0,me = this;
        if(me.items)
        {
            var len = me.items.length;
            for(var i = 0;i<len;i++)
            {
                if(value >= me.items[i].start && value < me.items[i].end ||
                    i==(len-1) && value >= me.items[i].start && value <= me.items[i].end ||
                    me.items[i].start == me.items[i].end && value == me.items[i].start)
                {
                    if(me.items[i].tag == tag){
                        var startC = me.items[i].startColor;
                        if(typeof(me.items[i].visible) == "undefined" || me.items[i].visible) { //可见，typeof会不会影响效率
                            if (i == len - 1) //最大值
                            {
                                r = startC.red;
                                g = startC.green;
                                b = startC.blue;
                                a = me.alpha;
                            }
                            else {
                                if (this.isSmooth && me.items[i].start != me.items[i].end) {
                                    var endC = me.items[i + 1].startColor;
                                    if(me.items[i + 1].start > me.items[i].start) {
                                        r = startC.red + (endC.red - startC.red) / (me.items[i + 1].start - me.items[i].start) * (value - me.items[i].start);
                                        g = startC.green + (endC.green - startC.green) / (me.items[i + 1].start - me.items[i].start) * (value - me.items[i].start);
                                        b = startC.blue + (endC.blue - startC.blue) / (me.items[i + 1].start - me.items[i].start) * (value - me.items[i].start);
                                    }
                                    else{
                                        endC = me.items[i].endColor;
                                        r = startC.red + (endC.red - startC.red) / (me.items[i].end - me.items[i].start) * (value - me.items[i].start);
                                        g = startC.green + (endC.green - startC.green) / (me.items[i].end - me.items[i].start) * (value - me.items[i].start);
                                        b = startC.blue + (endC.blue - startC.blue) / (me.items[i].end - me.items[i].start) * (value - me.items[i].start);
                                    }
                                    a = me.alpha;
                                }
                                else {
                                    r = startC.red;
                                    g = startC.green;
                                    b = startC.blue;
                                    a = me.alpha;
                                }
                            }
                        }
                        else //隐藏
                        {
                            r = startC.red;
                            g = startC.green;
                            b = startC.blue;
                            a = 0;
                        }
                        break;
                    }
                }
            }
        }
        else
        {

        }
        return {"r": r, "g": g, "b": b, "a": a};
    },

    isShowAllLabel:function(){
        var me = this;
        var len = me.items.length;
        var result = true;
        for(var i = 0;i<len;i++){
            if(typeof(me.items[i].visible) != "undefined" && !me.items[i].visible){
                result = false;
                break;
            }
        }
        return result;
    },

    isShowThisLabel:function(value){
        var me = this;
        var len = me.items.length;
        var result = true;
        for(var i = 0;i<len;i++)
        {
            if(value >= me.items[i].start && value < me.items[i].end ||
                i==(len-1) && value >= me.items[i].start && value <= me.items[i].end ||
                me.items[i].start == me.items[i].end && value == me.items[i].start)
            {
                if(typeof(me.items[i].visible) != "undefined" && !me.items[i].visible)
                    result = false;
                break;
            }
        }
        return result;
    },

    isShowThisLabelWithTag:function(value, tag){
        var me = this;
        var len = me.items.length;
        var result = true;
        for(var i = 0;i<len;i++)
        {
            if(me.items[i].tag == tag &&
                value >= me.items[i].start && value < me.items[i].end ||
                i==(len-1) && value >= me.items[i].start && value <= me.items[i].end ||
                me.items[i].start == me.items[i].end && value == me.items[i].start)
            {
                if(typeof(me.items[i].visible) != "undefined" && !me.items[i].visible)
                    result = false;
                break;
            }
        }
        return result;
    },

    /**
     * Method: convertWeightToColor
     * 重写父类方法
     * 将颜色权重转成具体的颜色。
     *
     * Parameters:
     * value - {Number} 颜色权重，0-1之间。
     */
    convertWeightToColor:function(value){
        //设一个初始值为白色，避免不在权重内无法设置颜色
        var r = 255, g = 255, b = 255,me = this;
        if(me.itemsC)
        {
            var len = me.itemsC.length;
            for(var i = 0;i<len;i++)
            {
                if(value >= me.itemsC[i].start && value <= me.itemsC[i].end)
                {
                    //计算value在当前一段权重中的百分比
                    var scale = (value - me.itemsC[i].start)/(me.itemsC[i].end - me.itemsC[i].start);
                    var startC = me.itemsC[i].startColor;
                    var endC = me.itemsC[i].endColor;
                    r = startC.red + (endC.red - startC.red)*scale;
                    g = startC.green + (endC.green - startC.green)*scale;
                    b = startC.blue + (endC.blue - startC.blue)*scale;
                    break;
                }
            }
        }
        else
        {
            //转换颜色,这里是用颜色权重的三次方作为依据，已达到中心到边缘过渡的更迅速，重点突出（三次方效果）
            value = value*value*value;
            if(value < 0.65)
            {
                g = 240;
                r = 370 * value;
            }
            else
            {
                r = 240;
                g = 50+(636 - 636*value);
            }
            b = 0;
        }

        return {"r": r, "g": g,"b":b};
    },
    
    CLASS_NAME: "WeatherMap.Layer.FillRangeColorLayer"
});