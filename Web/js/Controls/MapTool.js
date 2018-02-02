/*
* 地图工具
* by wangkun 2016-08-08
* */
function MapTool(divid) {
	var t=this;
	strHtml="<div id='div_maptool'></div>";
	$("#"+divid).append(strHtml);
	var strHtml="<a id='a_maptool'><span id='s_maptool'></span></a>";
	$("#"+divid).append(strHtml);
	$("#div_maptool").bind("click",function(){
		t.downPic();
	});
	this.downPic=function(){
		var map = GDYB.Page.curPage.map;
		var size = map.getCurrentSize();
	   	var memCanvas = document.createElement("canvas");
	  	memCanvas.width = size.w;
	   	memCanvas.height = size.h;
	   	memCanvas.style.width = size.w+"px";
	   	memCanvas.style.height = size.h+"px";
	   var memContext = memCanvas.getContext("2d");
	   for(var i = 0; i<map.layers.length; i++){
	   		if(typeof(map.layers[i].canvasContext) != "undefined") {
		   			var layerCanvas = map.layers[i].canvasContext.canvas;
		   			memContext.drawImage(layerCanvas, 0, 0, layerCanvas.width, layerCanvas.height);
		    }
	       	else if(typeof(map.layers[i].renderer) != "undefined" && typeof(map.layers[i].renderer.canvas) != "undefined"){
	       		if(typeof(map.layers[i].renderer.canvas) == "undefined")
	       			continue;
	       		var layerCanvas = map.layers[i].renderer.canvas.canvas;
	       		memContext.drawImage(layerCanvas, 0, 0, layerCanvas.width, layerCanvas.height);
		    }
	    }
		var img = new Image();
		img.src = memCanvas.toDataURL("image/png");
	    $("#a_maptool").attr("href",img.src);
	    $("#a_maptool").attr("download",'map.png');
	    $("#s_maptool").click();
	}
}