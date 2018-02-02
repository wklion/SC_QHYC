define(['bootpick'],function(bp){
	cdid="";//尺度
	Date.prototype.format = function(fmt){  
		let o = {
                "M+" : this.getMonth()+1,                 //月份   
                "d+" : this.getDate(),                    //日   
                "h+" : this.getHours(),                   //小时   
                "m+" : this.getMinutes(),                 //分   
                "s+" : this.getSeconds(),                 //秒   
                "q+" : Math.floor((this.getMonth()+3)/3), //季度   
                "S"  : this.getMilliseconds()             //毫秒   
            };   
        if(/(y+)/.test(fmt))   
        	fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
        for(var k in o)   
        	if(new RegExp("("+ k +")").test(fmt))   
        		fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
        	return fmt;   
    };
    Date.prototype.add = function(milliseconds){
    	var m = this.getTime() + milliseconds;
    	return new Date(m);
    };
    Date.prototype.addSeconds = function(second){
    	return this.add(second * 1000);
    };
    Date.prototype.addMinutes = function(minute){
    	return this.addSeconds(minute*60);
    };
    Date.prototype.addHours = function(hour){
    	return this.addMinutes(60*hour);
    };
    Date.prototype.addDays = function(day){
    	return this.addHours(day * 24);
    };
    Date.prototype.subMonth = function(){
    	let m = this.getMonth();
    	if(m == 0){
    		return new Date(this.getFullYear() -1,11,this.getDate(),this.getHours(),this.getMinutes(),this.getSeconds());
    	}
    	var day = this.getDate();
    	var daysInPreviousMonth = Date.daysInMonth(this.getFullYear(),this.getMonth());
    	if(day > daysInPreviousMonth){
    		day = daysInPreviousMonth;
    	}
    	return new Date(this.getFullYear(),this.getMonth() - 1,day,this.getHours(),this.getMinutes(),this.getSeconds());
    };
    Date.prototype.addMonths = function(value){
    	var month = this.getMonth();
		this.setMonth(month + value);
		return this;
    };
    Date.prototype.addYears = function(year){
    	return new Date(this.getFullYear() + year,this.getMonth(),this.getDate(),this.getHours(),this.getMinutes(),this.getSeconds());
    };
	return {
		//尺度点击
		CD_Click:function(){
			cdid=this.id;
			var dataformat="";
			var minViewMode=1;
			var startView=1;
			if(cdid=="month"){
				minViewMode=minViewMode;
				startView=startView;
				dataformat="yyyy-mm";
			}
			else if(cdid=="season"){
				minViewMode=minViewMode;
				startView=startView;
				dataformat="yyyy-mm";
			}
			else if(cdid=="year"){
				dataformat="yyyy";
				minViewMode=2;
				startView=2;
			}
			var date=new Date();
			$("#datepicker").datepicker();
			var dp=$("#datepicker").data('datepicker');
			dp._process_options({
				minViewMode:minViewMode,
				format:dataformat,
				startView:startView
			});
			dp._setDate(date);
		}
	}
});