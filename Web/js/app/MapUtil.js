define(['Legend','commonfun','layerManageUtil'],function(legend,cf,lmu){
	return {
		map:null,//地图
		baseLayer:null,//基础图层
		stationLayer:null,//站点图层
		layerFillRangeColor:null,//热点图层
		layerContour:null,//等势面图层
		layerPlot:null,//等势线值图层
		layerCover:null,//遮罩图层
		topLayer:null,//顶层
		initTDT:function(){//初始化天地图
			var me = this;
			require('tdtmap');
			require('tdtmapconn');
			me.map = new TMap("map",{projection: "EPSG:4326"});//初始化天地图的map
			me.map.centerAndZoom(new TLngLat(100,40),7);//定位
			me.map.enableHandleMouseScroll();//增加鼠标对地图的控制
			me.map.enableDoubleClickZoom();
			me.map.addControl(new TMapTypeControl());//增加切换叠图类型控件
			var config={
				type:"TMAP_NAVIGATION_CONTROL_LARGE",   //缩放平移的显示类型
				anchor:"TMAP_ANCHOR_TOP_LEFT",          //缩放平移控件显示的位置
				offset:[0,0],                           //缩放平移控件的偏移值
				showZoomInfo:true                       //是否显示级别提示信息，true表示显示，false表示隐藏。
			};
			me.map.addControl(new TNavigationControl(config));
			TEvent.addListener(me.map,"mousemove",this.ShowPosition);
		},
		initWeatherMap:function(){//初始化Weathermap
			var me = this;
			var navigatnion = new WeatherMap.Control.Navigation();
			var layerSwitcher = new WeatherMap.Control.LayerSwitcher();
    		navigatnion.handleRightClicks = true; //响应右键双击缩小
			me.map = new WeatherMap.Map("map",{controls:[
        		navigatnion,layerSwitcher,new WeatherMap.Control.Zoom()],projection: "EPSG:4326"});
			me.map.addControl(new WeatherMap.Control.MousePosition());
			var layer = new WeatherMap.Layer.LocalTiledCacheLayerWhiteMap();
			me.map.addLayers([layer]);
    		me.baseLayer = layer;
			me.map.setCenter(new WeatherMap.LonLat(98, 30), 2); //格点数据范围中心点

			me.topLayer = new WeatherMap.Layer.LocalTiledCacheLayerWhiteMap();
            me.topLayer.noDelete = true;
            me.topLayer.setIsBaseLayer(false);
            me.topLayer.name = "顶层";
            me.topLayer.layerType = "top";
			me.map.addLayer(me.topLayer);
			
			lmu.map = me.map;
    		me.map.events.register("addlayer",null,function(e){
                lmu.sortLayer();
			});
		},
		InitlayerCover:function(){//初始化遮罩图层
			if(layerCover==null){
				layerCover = new WeatherMap.Layer.Vector("layerCover");
				me.map.addLayer(layerCover);
				me.map.setLayerIndex(layerCover,99);//置于最上层
			}
			layerCover.removeAllFeatures();
			let gxPointList = new Array();
			gxPointList.push(new WeatherMap.Geometry.Point(-180, -90));
            gxPointList.push(new WeatherMap.Geometry.Point(180, -90));
            gxPointList.push(new WeatherMap.Geometry.Point(180, 90));
            gxPointList.push(new WeatherMap.Geometry.Point(-180, 90));
            //请求边界
            let url=cf.GetGridURL();
            url = url+"services/AdminDivisionService/getDivisionInfo";
            let paramdata="{areaCode:'"+51+"'}";
            let errortext="请求边界数据失败!";
            cf.AJAX(url,paramdata,errortext,function(data){
            	if(data==undefined){
            		console.log("边界数据请求失败!");
            		return;
            	}
            	var pointArray = new Array();
            	let pointList = data.geometry.points
            	for(let i=0;i<pointList.length;i++){
            		let lon = pointList[i].x;
            		let lat = pointList[i].y;
            		let point = new WeatherMap.Geometry.Point(lon, lat);
            		pointArray.push(point);
            	}
            	let linearRings = new WeatherMap.Geometry.LinearRing(pointArray);
            	let linearRings1 = new WeatherMap.Geometry.LinearRing(gxPointList);
            	let polygon = new WeatherMap.Geometry.Polygon([linearRings,linearRings1]);
            	let polygonVector = new WeatherMap.Feature.Vector(polygon);
            	polygonVector.style = {
            		strokeColor: "#ffffff",
            		fillColor: "#ffffff",
            		strokeWidth: 1,
            		fillOpacity: 1,
            		strokeOpacity: 0.4
            	};
            	layerCover.addFeatures([polygonVector]);
            });
          
		},
		ShowPosition:function(p){//显示坐标
			var lnglat = me.map.fromContainerPixelToLngLat(p);
			$("#posdiv").html(lnglat.getLng()+","+lnglat.getLat());
		},
		CalContour:function(datasetGrid,features,style){
			layerPlot.removeAllFeatures();
			layerPlot.addFeatures(features);
			var dZValues = [];
			let styleSize=style.length;
			for(let i=0;i<styleSize;i++){
				let val=style[i].end;
				dZValues.push(val);
			}
			var contour = new WeatherMap.Analysis.Contour();
			var result = contour.analysis(datasetGrid, dZValues, 6); //6为平滑度
			var contours = [];
			layerContour.renderer.labelField = "dZValue";
            layerContour.removeAllFeatures();
			result.forEach((item,i)=>{
				let geoline = item.geoline;
                let dZValue = item.dZValue;
                let feature = new WeatherMap.Feature.Vector(geoline);
                feature.attributes.dZValue = dZValue.toString();
                contours.push(feature);
			});
			layerContour.addFeatures(contours);
		},
		/**
		 * @author:wangkun`
		 * @date:2017-04-01
		 * @param:
		 * @return:
		 * @description:清除图层
		 */
		clearMap: function () {
			var me = this;
			var layerCount = me.map.getNumLayers();
			for (var i = layerCount - 1; i >= 0; i--) {
				var layer = me.map.layers[i];
				if (layer.isBaseLayer || layer.noDelete ) continue;
				me.map.removeLayer(layer);
			}
		}
	}
});