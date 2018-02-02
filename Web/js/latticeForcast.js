/**
 * Created by Administrator on 2015/10/9.
 */
function initChartTable(){
    window.myControl=[];
    if($("#latticeForcast").html() == ""){
        var contentForcast = '<div>'+
            '<div class="latticeInfoTitle" style="margin-top: -1px;"><div class="fl"><span style="width: 30px;">地区</span><span id="latticeInfoBase"></span></div><div class="fr"><select id="selectCity" class="tm">请选择一个城市</select><select id="selectStation" class="tm"><option>选择一个站点</option></select></div>'+
            '</div>'+
            '<div>'+
            '<div class="latticeInfoTitle"><img src="imgs/lattice/latticeTem.png"/><span>温度</span></div>'+
            '<div id="temperature" height="150" width="765" style="margin-top: 5px;"></div>'+
            '</div>'+
            '<div>'+
            '<div class="latticeInfoTitle"><img src="imgs/lattice/latticePre.png"/><span>降水</span></div>'+
            '<div id="precipitation" height="150" width="765" style="margin-top: 5px;"></div>'+
            '</div>'+
            '<div class="latticeInfoTitle"><img src="imgs/lattice/latticeTem.png"/><span>温度距平</span></div>'+
            '<div id="temperatureJuPing" height="150" width="765" style="margin-top: 5px;"></div>'+
            '</div>'+
            '<div class="latticeInfoTitle"><img src="imgs/lattice/latticePre.png"/><span>降水距平百分率</span></div>'+
            '<div id="precipitationJuPingLu" height="150" width="765" style="margin-top: 5px;"></div>'+
            '</div>'+
            '<div>';
            var contentPrecipitation ='<div class="latticeInfoTitle"><span>要素</span></div>'+
            '<div id="latticeTable" style="width: 100%;height: 200px;"></div>';
        $("#latticeForcast").html(contentForcast);
        $("#latticePrecipitation").html(contentPrecipitation);
    }
    if(true)
    {
        $("#latticeForcast").css("display", "block");
        $("#latticePrecipitation").css("display", "block");
    }
    else
    {
        $("#latticeForcast").css("display", "none");
        $("#latticePrecipitation").css("display", "none");
    }
    $("#selectCity").bind("change",selectStation);
    $("#selectStation").bind("change",showStationVal);
    LoadCity();
}
function showStationVal(){//显示站点
    var lon=$("#selectStation option:selected").attr("lon");
    var lat=$("#selectStation option:selected").attr("lat");
    GDYB.DisplayPage.currentPosition.lon=lon;
    GDYB.DisplayPage.currentPosition.lat=lat;
    GDYB.DisplayPage.displayGridValueSerial();
    GDYB.DisplayPage.displayGridValueSerialJuPing();
    var cname=$("#selectCity option:selected").val();
    var sname=$("#selectStation option:selected").val();
    updateLocationInfo("","","四川省",cname,sname);
}
function  LoadCity(){
    var url=Url_Config.gridServiceUrl+"services/AreaService/getAreaAndStation"; //格点转任意点
    $.ajax({
        data: {},
        url: url,
        dataType: "json",
        success: function (data) {
            var htmlContent="";
            var city="";
            for(var d=0;d<data.length;d++){
                var cname=data[d][0];//区域名称
                if(city==""){//第一次
                    city=cname;
                    htmlContent+="<option>"+cname+"</option>";
                }
                else if(city!=cname){//新的城市
                    city=cname;
                    htmlContent+="<option>"+cname+"</option>";
                }
            }
            $("#selectCity").html(htmlContent);
        },
        error:function(e){
            alert("获取区域信息出错!");
        },
        type: "POST"
    });
}
function selectStation(){
    var cityName=$("#selectCity option:selected").val();
    loadStation(cityName);
}
function loadStation(cityName){
    var url=Url_Config.gridServiceUrl+"services/AreaService/getAreaAndStation";
    $.ajax({
        data: {},
        url: url,
        dataType: "json",
        success: function (data) {
            var htmlContent="";
           for(var d=0;d<data.length;d++){
               var cname=data[d][0];//区域名称
               var sname=data[d][1];//站名
               var lon=data[d][2];//经度
               var lat=data[d][3];//纬度
               if(sname==null)
               continue;
               if(cname==cityName){
                   htmlContent+="<option lon="+lon+" lat="+lat+">"+sname+"</option>";
               }
           }
            $("#selectStation").html(htmlContent);
            showStationVal();
        },
        error:function(e){
            alert("获取区域信息出错!");
        },
        type: "POST"
    });
}
//更新图表
function updateChartTable(items, dateTime){
    if(items == null || items.length == 0)
        return;
    updateChart("temperature", "bar", items, dateTime, "2t");
    updateChart("precipitation", "bar", items, dateTime, "r24");
    updateTable(items, dateTime);
}
function updateChartTableJuPing(items, dateTime){
    if(items == null || items.length == 0)
        return;
    updateChart("temperatureJuPing", "bar", items, dateTime, "2t");
   updateChart("precipitationJuPingLu", "bar", items, dateTime, "r24");
}

//更新定位信息
function updateLocationInfo(lon, lat, province, city, county){
    //格点位置信息
    var latticeInfoBaseStr = province+','+city+','+county;
    $("#latticeInfoBase").html(latticeInfoBaseStr);
}


//更新统计图
function updateChart(chartId, chartType, items, dateTime, element){
    var strTime=$("#selectTimes option:selected")[0].innerHTML;//天，候，旬
    var chartData = getChartDataFromForecastData(items, element, dateTime);
    var fengDuan=strTime=="候"?5:10;
    //处理标题
    function dealLabel(chartData){
        if(chartData==undefined||chartData==null){
            return;
        }
        var maxCount=Math.ceil(chartData.labels.length/fengDuan);
        chartData.labels=[];
        for(var c=0;c<maxCount;c++){
            chartData.labels.push("第"+(c+1)+strTime);
        }
    }
    //处理数据
    function dealData(chartData){
        if(chartData==undefined||chartData==null){
            return;
        }
        var tempA=new Array();
        chartData.data.reverse();
        var flag=true;
        while(flag){
            var sum=0;
            var valid=0;
            for(var f=0;f<fengDuan;f++){
                var val=chartData.data.pop();
                if(val!=undefined){
                    sum+=val;
                    valid++;
                }
                else{
                    flag=false;
                }
            }
            if(valid!=0){
                if(element=="2t"){
                    tempA.push(Number((sum/valid).toFixed(1)));
                }
                else if(element=="r24"){
                    tempA.push(Number(sum.toFixed(1)));
                }
            }
        }
        //把值，赋给charData
        for(var c=0;c<tempA.length;c++){
            chartData.data.push(tempA[c]);
        }
    }
    if(strTime!="天"){
        dealLabel(chartData);
        dealData(chartData);
    }
    if(chartData != null){
        //Chart赋值
        var yDanWei="";
        var zones={};
        if(chartId=="temperature"||chartId=="temperatureJuPing"){
            yDanWei="度(℃)";
            Highcharts.setOptions({
                colors: ['#ff0000']
            });
            zones=[{//分段颜色
                    value: 0,
                    color: '#0000FF'
                },{
                    color: '#FF0000'
                }]
        }
        else if(chartId=="precipitation"){
            yDanWei="毫米(mm)";
            Highcharts.setOptions({
                colors: ['#0a67fb']
            });
            zones=[{//分段颜色
                    value: 0,
                    color: '#FF0000'
                },{
                    color: '#0000FF'
                }]
        }
        else if(chartId=="precipitationJuPingLu"){
            yDanWei="百分率(%)";
            Highcharts.setOptions({
                colors: ['#0a67fb']
            });
            zones=[{//分段颜色
                    value: 0,
                    color: '#FF0000'
                },{
                    color: '#0000FF'
                }]
        }
        $("#"+chartId).highcharts({
            chart: {
                height: 150,
                type: 'column',

            },
            title: {
                text: ''
            },
            legend: {
                enabled:false
            },
            xAxis: {
                categories: chartData.labels,
            },
            yAxis: {
                title: {
                    text: yDanWei
                }
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0"></td>' +
                '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    groupPadding:0
                },
                line:{
                    marker:{
                        enabled:false
                    }
                }
            },
            credits: {
                enabled: false
            },
            series: [{
                type: 'column',
                name: chartId,
                data:chartData.data,
                zones:zones
            },{                                                              
            type: 'line',                                               
            name: chartId,                                              
            data: chartData.data,
            color:'#0AF334'                                                         
        }]
        });
    }
}

//预报统计图日期
function updateChartTime(dateTime, hourSpans){
    var time = dateTime.getTime();
    var widthTotal = 708;
    var delta = widthTotal/hourSpans.length;
    var content = "";
    var last = 0;
    for(var i=0;i<hourSpans.length;i++)
    {
        var timeCurrent = time + hourSpans[i] * 60 * 60 * 1000; //value以小时为单位
        var dateCurrent = new Date();
        dateCurrent.setTime(timeCurrent);
        var day = dateCurrent.getDate();

        if(i == hourSpans.length - 1){
            var month = dateCurrent.getMonth()+1;
            var str = (Array(2).join(0)+month).slice(-2) + "-" + (Array(2).join(0)+day).slice(-2);
            content += '<div style="width: '+delta*(i-last)+'px;">'+str+'</div>';
        }
        else{
            var timeNext = time + hourSpans[i+1] * 60 * 60 * 1000; //value以小时为单位
            var dateNext = new Date();
            dateNext.setTime(timeNext);
            var dayNext = dateNext.getDate();
            if(day != dayNext){
                var month = dateCurrent.getMonth()+1;
                var str = (Array(2).join(0)+month).slice(-2) + "-" + (Array(2).join(0)+day).slice(-2);
                content += '<div style="width: '+delta*(i-last)+'px;word-break: break-all">'+str+'</div>';
                last = i;
            }
        }
    }
    return content;
}

//更新要素表格
function updateTable(items, dateTime){
    var hourSpan = 24; //3小时降水为最小时效间隔
    var hourSpanTotal = 960; //最长40天，这里写死基本上没问题
    var hourSpanGroup = 24; //
    var hourSpanCount = hourSpanTotal/hourSpan;
    var days = hourSpanTotal/24;
    var time = dateTime.getTime();
    var startHour = dateTime.getHours();

    var contentHtml = "";
    contentHtml += '<table class="latticeTable"><tr style="background-color: rgb(246,246,246);"><td rowspan="1" style="width: 80px">预报要素</td>';
    for(var i=0;i<days;i++){
        var dayTemp = new Date();
        var timeTemp = time+(i+(startHour>=20?1:0))*1000*3600*24;
        dayTemp.setTime(timeTemp);
        var year = dayTemp.getFullYear();
        var month = dayTemp.getMonth()+1;
        var day = dayTemp.getDate();
        var strDate = year + "-" + (Array(2).join(0) + month).slice(-2) + "-" + (Array(2).join(0) + day).slice(-2);
        contentHtml += '<td colspan="8">'+strDate+'</td>';
    }

//    contentHtml += '</tr><tr style="background-image: linear-gradient(to top,rgb(239,239,239) 0,rgb(246,246,246) 100%);">';
//    for(var i=0;i<days;i++){
//        var count = hourSpanGroup/hourSpan;
//        for(var j=0;j<count; j++)
//        {
//            var dayTemp = new Date();
//            var timeTemp = time+(i*count+j)*hourSpan*1000*3600;
//            dayTemp.setTime(timeTemp);
//            var hour = dayTemp.getHours();
//            var minute = dayTemp.getMinutes();
//            var strDate = (Array(2).join(0) + hour).slice(-2) + ":" + (Array(2).join(0) + minute).slice(-2);
//            contentHtml += '<td colspan="2">'+strDate+'</td>';
//        }
//    }

//    contentHtml += '</tr><tr><td>3小时降水</td>';
//    for(var i=0;i<days;i++){
//        var statisticHours = 3;//统计时长
//        var count = hourSpanGroup/statisticHours;
//        for(var j=0;j<count; j++){
//            var hourSpan = (i*count+j+1)*statisticHours;
//            if(hourSpan<=72) {
//                var v = getValueFromForecastData(items, "r3", hourSpan);
//                contentHtml += '<td>' + v + '</td>';
//            }
//            else{
//                if(hourSpan%6==0) {
//                    var v = getValueFromForecastData(items, "r3", hourSpan);
//                    contentHtml += '<td colspan="2">' + v + '</td>';
//                }
//            }
//        }
//    }
//
//    contentHtml += '</tr><tr><td>6小时降水</td>';
//    for(var i=0;i<days;i++){
//        var statisticHours = 6;//统计时长
//        var count = hourSpanGroup/statisticHours;
//        for(var j=0;j<count; j++){
//            var v = getStatisticValue(items, "r3", (i*count+j)*statisticHours, (i*count+j+1)*statisticHours, 0);
//            contentHtml += '<td colspan="2">'+v+'</td>';
//        }
//    }
//
//    contentHtml += '</tr><tr><td>12小时降水</td>';
//    for(var i=0;i<days;i++){
//        var statisticHours = 12;//统计时长
//        var count = hourSpanGroup/statisticHours;
//        for(var j=0;j<count; j++){
//            var v = getStatisticValue(items, "r3", (i*count+j)*statisticHours, (i*count+j+1)*statisticHours, 0);
//            contentHtml += '<td colspan="4">'+v+'</td>';
//        }
//    }

    contentHtml += '</tr><tr><td>24小时降水</td>';
    for(var i=0;i<days;i++){
        var statisticHours = 24;//统计时长
        var count = hourSpanGroup/statisticHours;
        for(var j=0;j<count; j++){
            var v = getStatisticValue(items, "r24", (i*count+j)*statisticHours, (i*count+j+1)*statisticHours, 0);
            contentHtml += '<td colspan="8">'+v+'</td>';
        }
    }

    contentHtml += '</tr><tr><td>气温</td>';
    for(var i=0;i<days;i++){
        var statisticHours = 24;//统计时长
        var count = hourSpanGroup/statisticHours;
        for(var j=0;j<count; j++){
            var v = getStatisticValue(items, "2t", (i*count+j)*statisticHours, (i*count+j+1)*statisticHours, 1);
            contentHtml += '<td colspan="8">'+v+'</td>';
        }
    }

   /* contentHtml += '</tr><tr><td>日最低温</td>';
    for(var i=0;i<days;i++){
        var statisticHours = 24;//统计时长
        var count = hourSpanGroup/statisticHours;
        for(var j=0;j<count; j++){
            var v = getStatisticValue(items, "2t", (i*count+j)*statisticHours, (i*count+j+1)*statisticHours, 2);
            contentHtml += '<td colspan="8">'+v+'</td>';
        }
    }*/

    contentHtml += '</tr></table>';
    $("#latticeTable").html(contentHtml);

    //获取统计值
    function getStatisticValue(items, element, startHourSpan, endHourSpan, statisticMethod){
        var result = null;
        for(var key in items){
            var item = items[key];
            if(item.element != element)
                continue;
            if(startHourSpan<item.hourSpan && item.hourSpan<=endHourSpan){
                var v = item.datas[0];
                if(result == null)
                    result = v;
                else {
                    if (statisticMethod == 0) //累加
                        result += v;
                    else if (statisticMethod == 1) //最大
                    {
                        if(v>result)
                            result=v;
                    }
                    else if (statisticMethod == 2) //最小
                        if(v<result)
                            result=v;
                }
            }
        }
        if(result != null)
            result = Math.round(result*10.0)/10.0;
        return result;
    }
}

//从预报数据中获取图表数据
function getValueFromForecastData(items, element, hourSpan){
    var result = null;
    for(var key in items) {
        var item = items[key];
        if (item.element == element && item.hourSpan == hourSpan) {
            result = item.datas[0];
        }
    }
    return result;
}

//从预报数据中获取图表数据
function getChartDataFromForecastData(items, element, dateTime){
    var chartData = null;
    var labels = [];
    var datas = [];
    for(var key in items){
        var newTime=new Date(dateTime);
        var item = items[key];
        if(item.element == element){
            datas.push(item.datas[0]);
            if(key%5!=0){
                labels.push("");
            } //间隔5个显示时间
            else{
                newTime.addDays((item.hourSpan/24-1));
                labels.push(newTime.format("MM-dd"));
            }
        }
    }
    if(datas.length > 0){
        if(element=="2t"){
            chartData = {labels:labels, data:datas};
        }
        else if(element=="r24"){
            chartData = {labels:labels, data:datas};
        }
        else{
            chartData = {labels:labels, data:datas};
        }
    }

    return chartData;
}
function getAvgData(datas,jiange){
    var newResult=new Array();
    var fengduan=0;//分段
    if(datas.length%jiange==0){
        fengduan=datas.length/jiange;
    }
    else{
        fengduan=datas.length/jiange+1;
    }
    for(var f=0;f<fengduan;f++){
        var avg=0;
        for(var j=0;j<jiange;j++){
            if((f*jiange+j)==datas.length){
                break;
            }
            if(j==0){
                avg=datas[f*jiange+j];
            }
            else{
                avg=(avg+datas[f*jiange+j])/2;
            }
        }
        newResult.push(avg.toFixed(1));
    }
    return newResult;
}
function getTotalData(datas,jiange){
    var newResult=new Array();
    var fengduan=0;//分段
    if(datas.length%jiange==0){
        fengduan=datas.length/jiange;
    }
    else{
        fengduan=datas.length/jiange+1;
    }
    for(var f=0;f<fengduan;f++){
        var sum=0;
        for(var j=0;j<jiange;j++){
            sum+=datas[f*jiange+j];
        }
        newResult.push(sum);
    }
    return newResult;
}
function getHourSpansFromForecastData(items, element){
    var hourSpans = [];
    for(var key in items){
        var item = items[key];
        if(item.element == element){
            hourSpans.push(item.hourSpan);
        }
    }
    return hourSpans;
}
