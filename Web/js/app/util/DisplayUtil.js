define(['layerManageUtil','gridUtil'], function(layerManageUtil,gridUtil) {
    'use strict';
    return {
        /**
         * @author:wangkun
         * @date:2017-11-24
         * @modifydate:
         * @param:
         * @return:
         * @description:显示
         */
        displayStationData:function(name,stationData){
            var layer = layerManageUtil.addLayer(name, "vector", null,"dot");
            layer.removeAllFeatures();
            var features = [];
            stationData.forEach(item=>{
                let lon=item.longitude==undefined?item.lon:item.longitude;
				let lat=item.latitude==undefined?item.lat:item.latitude;
                var val = item.value;
                val = parseInt(val);
                var geometry = new WeatherMap.Geometry.Point(lon,lat);
                var color = "blue";
                if(val<0){
                    color = "red";
                }
                var style = {
                    label:val+"",
                    fontColor:color,
                    fontSize:"1em",
                    strokeColor:"#339933", 
                    strokeOpacity:1,
                    strokeWidth:1,
                    pointRadius:2 
                }
                var pointFeature = new WeatherMap.Feature.Vector(geometry,null,style);
                features.push(pointFeature);
            });
            layer.addFeatures(features);
        },
        displayFillColor:function(name,stationData,style){
            var layer = layerManageUtil.addLayer(name, null, null,"grid");
            layer.visibility = true;
            layer.items = style;
            var dg = gridUtil.interpolate(stationData);
            layer.setDatasetGrid(dg);
            layer.refresh();
        },
        /**
         * @author:wangkun
         * @date:2017-12-10
         * @modifydate:
         * @param:
         * @return:
         * @description:显示站点图层
         */
        displayStationLayer:function(stationData){
            var layer = layerManageUtil.addLayer("站点", "vector", null,"dot");
            stationData.forEach(item=>{
                let lon = item.lon;
                let lat = item.lat;
                let name = item.station_Name;
                var geo = new WeatherMap.Geometry.Point(lon,lat);
                let style = {
                    label:name,
                    fontColor:"purple",
                    strokeColor:"#339933", 
                    strokeOpacity:0.5, 
                    strokeWidth:1, 
                    pointRadius:2 
                }
                var pointFeature = new WeatherMap.Feature.Vector(geo,null,style);
                layer.addFeatures(pointFeature);
            });
        },
        /**
         * @author:wangkun
         * @date:2017-12-10
         * @modifydate:
         * @param:
         * @return:
         * @description:隐藏站点图层
         */
        hideLayer:function(name){
            layerManageUtil.Remove(name);
        }
    }
});