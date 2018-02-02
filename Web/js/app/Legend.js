define([],function(){
	return {
		Update:function(styles){
			$("#div_legend").css("display","block");
			var strLegendItem = "";
		    var strLegendItemText = "";
		    var items={}; //把原始的颜色存起来
		    var size=styles.length;
		    for(let i=0;i<size;i++){
		    	let style = styles[i];
		    	let value = Math.floor(style.end*10)/10;
		    	var visible = "true";
		    	var rgb = "rgb("+ style.startColor.red + "," + style.startColor.green + "," + style.startColor.blue + ")";
		    	items[value] = rgb;
		    	if(typeof(style.visible) != "undefined" && !style.visible)
		    	{
		    		rgb = "rgb(255, 255, 255)";
		    		visible = "false";
		    	}
		    	var strvalue = value;
		    	if(typeof(style.legend) != "undefined")
		    		strvalue = style.legend;
		    	strLegendItem+="<div class='text-center' style='width:50px;line-height:30px;cursor:pointer;background-color:" + rgb + "' visible='"+ visible +"' tag='"+value+"'>"+strvalue+"</div>";
		    }
		    $("#div_legend_items").html(strLegendItem);
        	$("#div_legend_itemTexts").html(strLegendItemText);
        	//注册点击事件
        	$("#div_legend_items").find("div").click(function(){
        		var legenItemValue = Number($(this).attr("tag"));
        		var bvisible = typeof(this.attributes["visible"]) == "undefined" || this.attributes["visible"].value == "true";
        		if(bvisible){
        			$(this).css("background-color", "rgb(255, 255, 255)");
        			$(this).attr("visible", "false");
        		}
        		else{
        			var rgb = items[legenItemValue];
        			$(this).css("background-color", rgb);
        			$(this).attr("visible", "true");
        		}
        		for(var key in styles) {
        			var style = styles[key];
        			var value = Math.floor(style.end * 10) / 10;
        			if(value == legenItemValue){
        				style["visible"] = !bvisible;
        				break;
        			}
        		}
        		//GDYB.GridProductClass.layerFillRangeColor.refresh();
        	});
		},
		Hidden:function(){
			$("#div_legend").css("display","none");
		}
	}
});