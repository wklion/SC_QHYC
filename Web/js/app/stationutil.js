define([],function(){
	return {
		getscstation:function(id,recall){//获取数据站点
			var cf=require('commonfun');
			let url=cf.GetGridURL();
			if(id=="disscstation"){
				url = url+"services/StationService/GetSCStation";
			}
			else{
				url = url+"services/StationService/GetXNStation";
			}
            var paramdata="";
            var errortext="站点请求失败!";
            cf.AJAX(url,paramdata,errortext,recall);
		},
		displayonmap:function(){
			let id=this.id;
			var val=$(this).attr("val");
			var mu=require('maputil');
			var stationLayer=mu.GetStationLayer();
			if(val=="on"){
				stationLayer.display(false);
				$(this).attr("val","off");
				return;
			}
			else{
				stationLayer.display(true);
				$(this).attr("val","on");
			}
			var self=require('stationutil');
			self.getscstation(id,function(data){
				if(data==null||data.length==0){
					console.log("站点无数据!");
					return;
				}
				data.forEach((x,i)=>{
					let stationName=x["stationName"];
					let stationNum=x["stationNum"];
					let lon=x["longitude"];
					let lat=x["latitude"];
					let pt = new WeatherMap.Geometry.Point(lon,lat);
					var style = {
						strokeColor:"#339933", 
						strokeOpacity:0.5, 
						strokeWidth:2, 
						pointRadius:2,
						labelXOffset:10,
						labelYOffset:10,
						label:stationName, 
						fontColor:"#0000ff", 
						fontOpacity:"0.5", 
						fontFamily:"隶书", 
						fontSize:"0.8em", 
						fontWeight:"bold", 
						fontStyle:"italic", 
						labelSelect:"true", 
					};
					var pointFeature = new WeatherMap.Feature.Vector(pt,null,style);
					stationLayer.removeFeatures();
					stationLayer.addFeatures(pointFeature);
				});
			});
		}
	}
});