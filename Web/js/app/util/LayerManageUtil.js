/**
 * @author:wangkun
 * @date:2017-03-10
 * @param:
 * @return:
 * @description:图层管理类
 */
define(['mapUtil'], function (mapUtil) {
	return {
		radarGridLayer:[],//失量图层开始索引
		radarVectorLayer:[],
		preImgLayerName:"",//此类图层只显示一个
		map:null,
        layerType:['top','zhezhao','dot','line','region','grid','image'], //图层的几种类型,点、线、面、图片，区分图层叠加顺序
		/**
		 * @author:wangkun
		 * @date:2017-03-30
		 * @param:layername-图层名;layertype-图层类型,url(可选)-地址，bounds(可选)-边界,options(可选)-选项
		 * @return:图层
		 * @description:增加图层
		 */
		addLayer: function (layername, layertype, options,type,render) {
			var me = this;
			var layer = null;
			var layers = me.getLayer(layername);
			if (layers.length > 0) {
				layer=layers[0];
			}
			else {
				if (layertype === "vector") {
					layer = new WeatherMap.Layer.Vector(layername,{renderers: [render||"Canvas2"]});
				}
				else if (layertype === "image") {
					me.Remove(me.preImgLayerName);
					layer = new WeatherMap.Layer.Image(layername, url, bounds, { useCanvas: true, isBaseLayer: false});
					me.preImgLayerName = layername;
				}
				else if (layertype === "makers") {
					layer = new WeatherMap.Layer.Markers(layername, {});
				}
				else if(layertype === "lable"){
					layer = new WeatherMap.Layer.Vector(layername,options);
				}
				else {
					layer = new WeatherMap.Layer.FillRangeColorLayer(
						layername, {
							"radius": 40,
							"featureWeight": "value",
							"featureRadius": "geoRadius"
						}
                    );
                    layer.alpha = 255;
                }
				layer.layerType = type;//用于排序
				var mu = require('mapUtil');
                mu.map.addLayer(layer);
            }
			return layer;
		},
		/**
		 * @author:wangkun
		 * @date:2017-03-30
		 * @param:layername-图层名
		 * @return:图层
		 * @description:获取图层
		 */
		getLayer: function (layername) {
			var me = this;
			return me.map.getLayersByName(layername);
		},
		/**
		 * @author:wangkun
		 * @date:2017-03-30
		 * @param:
		 * @return:
		 * @description:移除图层
		 */
		Remove: function (layername) {
			var me = this;
			var map = me.map;
			var layer = me.getLayer(layername);
			if(layername.toLowerCase()==="titan"){
				var layerLabel = me.getLayer("TITANLable");//
				if (layerLabel.length > 0) {
					map.removeLayer(layerLabel[0]);
				}
			}
			if (layer.length > 0) {
				map.removeLayer(layer[0]);
			}
		},
        /**
         * @author:POPE
         * @date:2017-06-29
         * @description:填色图层
         */
        addLayerFillRangeColor:function () {
            var map = me.map;
            var layerFillRangeColor = new WeatherMap.Layer.FillRangeColorLayer(
                "layerMicapsGrid", {
                    "radius": 40,
                    "featureWeight": "value",
                    "featureRadius": "geoRadius"
                }
            );
            layerFillRangeColor.isSmooth = true;
            layerFillRangeColor.isAlwaySmooth = true;
            layerFillRangeColor.isShowGridline = false;
            layerFillRangeColor.isShowLabel = false;
            layerFillRangeColor.items = heatMap_TempStyles;
            map.addLayer(layerFillRangeColor);
            return layerFillRangeColor;
        },
        /**
         * @author:POPE
         * @date:2017-03-30
         * @param: id,fieldName,type
         * @return:
         * @description:添加等值线图层
         */
        addLayerContour: function (id,layerType) {
            var self =this;
            var map = me.map;
            var layerContour = new WeatherMap.Layer.Vector(id, {renderers: ["Contour"]});
            layerContour.renderer.labelField = "值";
            layerContour.style = {
                fontFamily:"Arial",
                fontColor:"#333",
                fontSize:"16px",
                fontWeight:"bold",
                strokeColor: "#ff0000",
                strokeWidth: 1.0
            };
            map.addLayer(layerContour);
            switch (layerType){
                case 'dot':
                    self.layerIndex[0] += 1;
                    map.setLayerIndex(layerContour,self.layerIndex[0]);
                    break;
                case 'line':
                    self.layerIndex[1] += 1;
                    map.setLayerIndex(layerContour,self.layerIndex[1]);
                    break;
                case 'region':
                    self.layerIndex[2] += 1;
                    map.setLayerIndex(layerContour,self.layerIndex[2]);
                    break;
                case 'image':
                    self.layerIndex[3] += 1;
                    map.setLayerIndex(layerContour,self.layerIndex[3]);
                    break;
            }
            return layerContour;
        },
        /**
         * @author:POPE
         * @date:2017-06-14
         * @param: {string} id - 图层标识.
         * @returns: {Object} alarmLayer
         * @description:创建闪烁图层
         */
        addAlarmLayer: function (id) {
            var map = me.map;
            var alarmLayer = new WeatherMap.Layer.AnimatorVector(id,{rendererType: "GlintAnimator"}, {
                speed:0.05, //设置速度为每帧播放0.05的数据
                startTime:1, //开始时间为0
                frameRate:10, //每秒渲染12帧
                endTime:1 //结束时间设置为10
            });
            map.addLayer(alarmLayer);
            return alarmLayer;
        },
		/**
		 * @author:wangkun
		 * @date:2017-04-04
		 * @param:
		 * @return:
		 * @description:移动图层至顶部
		 */
		moveToTop: function (targetlayer) {
			var map = me.map;
			var targetIndex = map.getLayerIndex(targetlayer);
			var layerCount = map.getNumLayers();
			var max = 0;
			var maxLayer = null;
			for (var i = layerCount - 1; i >= 0; i--) {
				var layer = map.layers[i];
				var index = map.getLayerIndex(layer);
				if (index > max) {
					max = index;
					maxLayer = layer;
				}
			}
			map.setLayerIndex(targetlayer, max + 1);
			map.setLayerIndex(maxLayer, targetIndex);
			map.setLayerIndex(targetlayer, max);
		},
		/**
		 * @author:wangkun
		 * @date:2017-08-19
		 * @param:
		 * @return:
		 * @description:移除指定图层
		 */
		removeLayersByName:function(names){
			var me = this;
			var map = me.map;
			var size = names.length;
			for(var i=size-1;i>=0;i--){
				var layername = names[i];
				var layer = me.getLayer(layername);
				if (layer.length > 0) {
					map.removeLayer(layer[0]);
				}
			}
        },
        /**
		 * @author:wangkun
		 * @date:2017-09-05
		 * @param:
		 * @return:
		 * @description:图层排序
		 */
        sortLayer:function(){
            var me = this;
            var map = me.map;
            var layers = map.layers;
            var layerCount = layers.length;
            var typeCount = me.layerType.length;
            var layerIndex = 1;
            for(var i=typeCount-1;i>=0;i--){
                var curType = me.layerType[i];
                for(var j=0;j<layerCount;j++){
                    var curLayer = layers[j];
                    if(curLayer.isBaseLayer){
                        continue;
                    }
                    if(curLayer.layerType===curType){
                        map.setLayerIndex(curLayer,layerIndex);
                        layerIndex++;
                    }
                }
            }
        }
	}
});