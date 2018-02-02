define([],function(){
	return {
		//预测模块单位获取
		GetForcastDanWei:function(){
            let typeid=$("#type button.active")[0].id;//获取显示方式
            let elementid=$("#element button.active")[0].id;//取元素
            if((typeid=="live"||typeid=="hos"||typeid=="jp"||typeid=="nh")&&elementid=="temp"){
            	return "℃";
            }
            else if((typeid=="live"||typeid=="hos")&&elementid=="rain"){
            	return "mm";
            }
            else if((typeid=="jp"||typeid=="nh")&&elementid=="rain"){
            	return "%";
            }
		}
	}
});