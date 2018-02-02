/*区域*/
define(['commonfun'],function(cf){
	return {
		GetCity:function(recall){//获取市
			let url=cf.GetGridURL();
			url = url+"services/AreaService/GetCity";
			var paramdata="";
            var errortext="区域请求失败!";
            cf.AJAX(url,paramdata,errortext,recall);
		},
		GetArea:function(recall){//获取区域
			let url=cf.GetGridURL();
			url = url+"services/AreaService/GetArea";
			var paramdata="";
            var errortext="区域请求失败!";
            cf.AJAX(url,paramdata,errortext,recall);
		}
	}
});