/**
 * Class: WeatherMap.Layer.MappingLayer
 * 绘图制图（辅助）图层。
 * 绘图编辑时的辅助性图层，例如显示画刷等。
 * by zouwei
 * 2015-11-24
 */
WeatherMap.Layer.MappingLayer = WeatherMap.Class(WeatherMap.Layer, {

    /**
     * Proterty: rootCanvas
     * {Canvas} 主绘制面板。
     */
    rootCanvas: null,

    /**
     * Proterty: rootCanvas
     * {Canvas} 主绘制对象。
     */
    canvasContext: null,

    maxWidth: null,

    maxHeight: null,

    /**
     * Proterty: features
     * {Feature} 对象。
     */
    features: null,

    initialize: function(name, options) {
        WeatherMap.Layer.prototype.initialize.apply(this, arguments);

        //构建绘图面板
        this.rootCanvas = document.createElement("canvas");
        if (!this.rootCanvas.getContext) {
            return;
        }
        this.supported = true;
        this.rootCanvas.id = "Canvas_" + this.id;
        this.rootCanvas.style.position = "absolute";
        this.div.appendChild(this.rootCanvas);
        this.canvasContext = this.rootCanvas.getContext('2d');
    },

    setMap:function(map){
        WeatherMap.Layer.prototype.setMap.apply(this, arguments);
        if(!this.supported){
            this.map.removeLayer(this);
        }else{
            this.redraw();
        }

        //注册事件
        var t = this;
        this.map.events.register("mousemove", this.map, function(event){
            t.refresh(event.xy);
        });
    },

    addFeatures: function(features){//addHeatPoints
        if (!(WeatherMap.Util.isArray(features))) {
            features = [features];
        }
        this.features = this.features || [];
        if(0 == this.features.length){
            this.features = features;
        }else{
            this.features.concat(features);
        }
        this.events.triggerEvent("featuresadded", {features: features, succeed: true});
        this.refresh();
    },

    removeAllFeatures: function(){
        if(this.features && this.features.length > 0){
            for(var i=0, len= this.features.length; i < len; i++){
                this.features[i].destroy();
                this.features[i] = null;
            }
        }
        this.features = [];
        //this.refresh();
        this.canvasContext.clearRect(0, 0, this.maxWidth, this.maxHeight);
    },

    moveTo: function(bounds, zoomChanged, dragging) {
        WeatherMap.Layer.prototype.moveTo.apply(this, arguments);
        if(!this.supported){
            return;
        }
        this.zoomChanged = zoomChanged;
        if(!dragging){
            this.div.style.visibility = "hidden";
            this.div.style.left = -parseInt(this.map.layerContainerDiv.style.left) + "px";
            this.div.style.top = -parseInt(this.map.layerContainerDiv.style.top) + "px";
            var size = this.map.getSize();
            this.rootCanvas.width = parseInt(size.w);
            this.rootCanvas.height = parseInt(size.h);
            this.maxWidth = size.w;
            this.maxHeight = size.h;
            this.div.style.visibility = "visible";
            if(!zoomChanged){
                this.refresh();
            }
        }

        if(zoomChanged){
            this.refresh();
        }
    },

    refresh: function(xy){
        if(this.features == null || this.features.length == 0)
            return;

        var lonlat = null;
        if(typeof(xy) != "undefined")
            lonlat = this.map.getLonLatFromPixel(xy);

        this.canvasContext.clearRect(0, 0, this.maxWidth, this.maxHeight);
        this.canvasContext.save();
        var resolution = this.map.getResolution();
        var bounds = this.map.getExtent();
        for(var key in this.features) {
            var feature = this.features[key];
            var geometry = feature.geometry;
            var style = feature.style;

            if(lonlat != null) {
                geometry.x = lonlat.lon - geometry.width/2;
                geometry.y = lonlat.lat + geometry.height/2;
            }

            if(geometry.CLASS_NAME == "WeatherMap.Geometry.Rectangle"){
                var ptLeftBottomLocal = this.getPixelXY(geometry.x, geometry.y, bounds, resolution);
                var ptRightTopLocal = this.getPixelXY(geometry.x + geometry.width, geometry.y + geometry.height, bounds, resolution);
                //绘制矩形填充
                if (style.fill !== false) {
                    this.setCanvasStyle("fill", style);
                    this.canvasContext.fillRect(ptLeftBottomLocal.x, ptLeftBottomLocal.y,
                            ptRightTopLocal.x - ptLeftBottomLocal.x,
                            ptLeftBottomLocal.y - ptRightTopLocal.y);
                    this.setCanvasStyle("reset");
                }

                //绘制矩形边线
                if (style.stroke !== false) {
                    this.setCanvasStyle("stroke", style);
                    this.canvasContext.strokeRect(ptLeftBottomLocal.x, ptLeftBottomLocal.y,
                            ptRightTopLocal.x - ptLeftBottomLocal.x,
                            ptLeftBottomLocal.y - ptRightTopLocal.y);
                    this.setCanvasStyle("reset");
                }
            }
        }
    },

    /**
     * Method: getPixelXY
     * 转换地理坐标为相对于当前窗口左上角的像素坐标
     *
     * Parameters:
     * x - {int} 热点的像素 x 坐标。
     * y - {int} 热点的像素 y 坐标。
     * bounds - {WeatherMap.Bounds} 当前地图显示范围。
     * resolution - {Number} 当前地图分辨率。
     */
    getPixelXY: function(x, y, bounds, resolution) {
        var x = (x / resolution + (-bounds.left / resolution));
        var y = ((bounds.top / resolution) - y / resolution);
        return {x: parseInt(x), y: parseInt(y)};
    },

    setCanvasStyle: function(type, style) {
        if (type === "fill") {
            this.canvasContext.globalAlpha = style['fillOpacity'];
            this.canvasContext.fillStyle = style['fillColor'];
        } else if (type === "stroke") {
            this.canvasContext.globalAlpha = style['strokeOpacity'];
            this.canvasContext.strokeStyle = style['strokeColor'];
            this.canvasContext.lineWidth = style['strokeWidth'];
        } else {
            this.canvasContext.globalAlpha = 0;
            this.canvasContext.lineWidth = 1;
        }
    },

    /**
     * APIMethod: destroy
     * 销毁图层，释放资源。
     */
    destroy: function() {
        if(this.features && this.features.length > 0){
            for(var i=0, len= this.features.length; i < len; i++){
                this.features[i].destroy();
                this.features[i] = null;
            }
        }
        this.colors = null;
        this.features = null;
        this.radius = null;
        this.supported = null;
        this.canvasContext = null;
        this.pixelHeatPoints = null;
        this.rootCanvas = null;
        this.alphaValues = null;
        this.colorValues = null;
        this.imgData = null;
        this.maxWeight = null;
        this.minWeight = null;
        this.maxWidth = null;
        this.maxHeight = null;
        this.featureRadius = null;
        WeatherMap.Layer.prototype.destroy.apply(this, arguments);
    },

    CLASS_NAME: "WeatherMap.Layer.MappingLayer"
});