/**
 * Class: WeatherMap.Layer.Tianditu
 * 天地图服务图层类。
 *     用于显示天地图的地图服务，使用<WeatherMap.Layer.Tianditu>的
 *     构造函数可以创建天地图图层，更多信息查看：
 *
 * Inherits from:
 *  - <WeatherMap.Layer.CanvasLayer>
 */
WeatherMap.Layer.LocalTiledCacheLayerTDTIMG = WeatherMap.Class(WeatherMap.CanvasLayer, {
    dir:"tianditu/Sattelite/Sattelite/",
    format:"jpg",
    scales_1:[1/591658710, 1/295829355, 1/147914678,1/73957339, 1/36978669, 1/18489335,1/9244667,1/4622334,1/2311167,1/1155583,1/577792,1/288896,1/144448,1/72224,1/36112],
    url:"http://172.22.96.140:8080/TiledCacheService/TiledCacheServlet?dir=${dir}&scale=${scale}&row=${row}&col=${col}&format=${format}",

    initialize: function (options) {
        var me = this;
        me.name = "LocalTiledCache";
        var resolutions = [];
        for(var i=0;i<me.scales_1.length;i++){
            resolutions.push(WeatherMap.Util.getResolutionFromScaleDpi(1/me.scales_1[i],96,"degree"));
        }
        options = WeatherMap.Util.extend({
            maxExtent: new WeatherMap.Bounds(
                -180,
                -90,
                180,
                90
            ),
            tileOrigin:new WeatherMap.LonLat(-180,90),
            resolutions:resolutions
        }, options);
        WeatherMap.CanvasLayer.prototype.initialize.apply(me, [me.name, me.url, null, options]);
    },

    getTileUrl:function(xyz){
        var me = this;
        url = me.url;
        var x = xyz.x;
        var y = xyz.y;
        var z = xyz.z;
        var scale = 1/this.scales_1[z];

        url = WeatherMap.String.format(url, {
            dir:this.dir,
            col: x,
            row: y,
            scale: Math.round(scale),
            format:this.format
        });
        return url;
    },

    setMap: function(map) {
        WeatherMap.CanvasLayer.prototype.setMap.apply(this, [map]);
    },

    CLASS_NAME: 'WeatherMap.Layer.LocalTiledCacheLayerTDTIMG'
});