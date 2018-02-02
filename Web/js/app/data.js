define([],function(){
	data=null;//站点数据
	return {
		//获取站点数据
		GetData:function(){
			return this.data;
		},
		SetData:function(data){
			this.data=data;
		},
		//高度场资料处理
		HResDeal:function(cidu){
			let datetime=$("#datepicker").datepicker('getDate');
			datetime=datetime.format("yyyy-MM-dd");
			let id=$("#date button.active")[0].id;
			var cf=require('commonfun');
			let url=cf.GetGridURL();
			url = url+"services/EFSService/HResDeal";
            let paramdata="{cidu:'" + id +"',datetime:'"+datetime +"'}";
            let errortext="资料处理失败!";
            cf.AJAX(url,paramdata,errortext,function(){
            	console.log('处理成功!');
            });
		},
		//只要四川站的数据
		Filter:function(data,scstation){
			var result=[];
			data.forEach((x,i)=>{
				let stationNum=x["stationNum"];
				var find=scstation.find((n)=>n==stationNum);
				if(find!=undefined)
					result.push(x);
			});
			return result;
		}
	}
});