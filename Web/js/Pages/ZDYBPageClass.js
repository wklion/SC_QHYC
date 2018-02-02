/**
 * Created by wangkun on 2016/4/12.
 */
function ZDYBPageClass(){
    var t = this;
    var hourspans = [24,48,72,96,120,144,168,192,216,240,264,288,312,336,360,384,408,432,456,480,504,528,552,576,600,624,648,672,696,720,744,768,792,816,840,864,888,912,936,960];//时效
    var elements=["r24","2t"];
    var grids=new Array();
    var dicGrids=new Array();
    var stations;
    var areaName="";
    var elementID=0;//0表示逐日预报，1表示强降水强降温预报
    var isDownData=false;//下载完数据
    var isInitTable=false;//初始化表格
    var isInitStation=false;//下载完站点
    var aArea;//亚区
    var aCity;//城市
    var aStations;
    var strZhuRiTemp="fcst_stn_temp_date.txt";//date比如20151211
    var strZhuRiPrec="fcst_stn_prec_date.txt";
    var strQiangJiangTemp="Z_SEVP_C_BCCD_date_P_CLFC-ATCTF.txt";//date比如20151211140000
    var strQiangJiangPrec="Z_SEVP_C_BCCD_date_P_CLFC-TCTF.txt";
    this.renderMenu = function(){
        //左侧菜单
        var htmlStr = "<div id='div_datetime' style='padding-left:10px;height: 40px;'>"
            +"<div id='dateSelect' style='margin: 10px 0px 0px 0px;float: left;width: 140px;height: 26px;'></div></div>"
            +"<div id='zdybProductTypePanel' class=''></div>";
        $("#menu_bd").html(htmlStr);
        //获取所有产品
        GDYB.GridProductClass.init(function(){
            t.areaName = "qutai";
            getZDYBPublishTime();
        });
        t.myDateSelecter = new DateSelecter(2,2,"yyyy-mm-dd");
        t.myDateSelecter.intervalMinutes = 60*24; //24小时
        $("#dateSelect").html(t.myDateSelecter.div);
        $("#dateSelect").find("input").bind("change",function(){
            isDownData=false;
            changeTimeSerie();
            GetGrid();//日期改变下载数据
            getAllProductNum();//更新产品状态
        });
        $("#dateSelect").find("img").bind("click",function(){
            isDownData=false;
            changeTimeSerie();
            GetGrid();//日期改变下载数据
            getAllProductNum();//更新产品状态
        });
        //$("#dateSelect").find("input").val("2016-01-10");
        
        if($("#workspace_div").find("#ZDYBDiv").length == 0){
            //主面板
            $("#workspace_div").append("<div id='ZDYBDiv' style='background-color: rgb(255, 255, 255);height: 100%;right: 0px;left:406px;position: absolute;'><div id='ZDYBAllContent'>" +
            //标题
               "<div id='FXYBDivTitle' style='height: 30px;line-height: 30px;background-color: #4C9ED9;'>"+
                    "<span id='FXYBView'>" +
                        "<button id='ShowBaoWen'>上传报文</button>"+
                    "</span>"+
                    "<span id='FXYBSpanTitle' style='font-weight: bold;position: absolute;left: 50%;margin-left: -140px;'></span>"+
                    "<span id='FXYBSpanTimeCountdown' style='color: red;float: right'>00小时26分钟10秒</span><span id='FXYBCountdownTitle' style='float: right;font-size: 12;'>注意：距上网截止时间还剩</span>"+
                "</div>"+
                //数据
                   "<div id='FXYBMainDiv'>"+
                        "<div id='FXYBTopDiv'>"+
                            "<table id='FXYBTopTable'></table>"+
                        "</div>"+
                        "<div id='FXYBLeftDiv'>"+
                            "<table id='FXYBLeftTable'></table>"+
                        "</div>"+
                        "<div id='FXYBDataDiv'>"+
                            "<table id='FXYBDataTable'>"+
                            "</table>"+
                        "</div>"+
                    "</div>"+
             "</div>"
            );
        }
        $("#map_div").css("display","none");
        //计算表格宽度
        var width = document.body.offsetWidth;
        t.windowWidth = width;
        $("#FXYBDivTitle").css("width",parseInt(width)-415);
        $("#FXYBDivControl").css("width",parseInt(width)-415);
        $("#FXYBData").css("width",parseInt(width)-415);
        $("#FXYBMainDiv").css("height",parseInt($("#ZDYBDiv").css("height"))-30);
        $("#MainData").height($("#FXYBData").height() - 20);//上标
        var mainDiv=document.getElementById("FXYBMainDiv");
        mainDiv.onscroll=function(){
            var FXYBTopDiv=document.getElementById("FXYBTopDiv");
            var FXYBLeftDiv=document.getElementById("FXYBLeftDiv");
            FXYBTopDiv.style.top = this.scrollTop+"px";
            FXYBLeftDiv.style.left = this.scrollLeft+"px";
        }
        $("#ShowBaoWen").click(function(){
            ShowBaoWen();
        });
        getStation();//初始化站点
        setTimeout(function(){
            GDYB.GridProductClass.getLastGridInfo(t.displayLastTime,"prvn");
        }, 1000); //显示格点产品
        //GetGrid();
    };
    this.displayLastTime=function(datetime, elements, hourspans, levels, datetimeSerial){//显示最新时间
        if(datetime != null && datetime != "null" && datetime != "")
        {
            t.myDateSelecter.setCurrentTime(datetime);
            t.myDateSelecter.setDatetimeSerial(datetimeSerial);
            changeTimeSerie();
            GetGrid();
        }
    };
    function changeTimeSerie(){
        var strDate=$("#dateSelect").find("input").val();//日期
        var dtForcastTime=new Date(strDate);
        var firstR=$("#FXYBTopTable tr:first");
        for(var d=0;d<hourspans.length;d++){
            var tempDate=dtForcastTime.format("yyyy-MM-dd");
            firstR[0].cells[d+3].innerHTML=tempDate;
            dtForcastTime.addDays(1);
        }
    };
    function getStation(){
        isInitStation=false;
        $.ajax({
            type: 'post',
            url: Url_Config.gridServiceUrl + "services/ForecastfineService/getStation",
            data: {'para': ''},
            dataType: 'json',
            error: function () {
                alert('获取站点出错!');
            },
            success: function (data) {
                stations=data;
                DealStation();
            }
        });
    }
    function DealStation(){
        aCitys=new Array();
        aArea=new Array();
        aStations=new Array();
        //初始化亚区
        for(var s=0;s<stations.length;s++){
            var station=stations[s];
            var area=station["areaname"];
            var isContain=false;
            for(var a=0;a<aArea.length;a++){
                if(area==aArea[a]){
                    isContain=true;
                    break;
                }
            }
            if(!isContain){
                aArea.push(area);
            }
        }
        //初始化城市，和亚区对应
        for(var a=0;a<aArea.length;a++){
            var areaName=aArea[a];
            var aCity=new Array();
            for(var s=0;s<stations.length;s++){
                var station=stations[s];
                var area=station["areaname"];
                    if(areaName==area){
                        var cityName=station["cityname"];
                        isContain=false;
                        for(var ac=0;ac<aCity.length;ac++){
                            if(cityName==aCity[ac]){
                                isContain=true;
                                break;
                            }
                        }
                        if(!isContain){
                            aCity.push(cityName);
                        }
                    }
                }
            aStation=new Array();
            for(var c=0;c<aCity.length;c++){
                var cityName=aCity[c];
                var aChildStations=new Array();
                for(var s=0;s<stations.length;s++){
                    var station=stations[s];
                    if(cityName==station["cityname"]){
                        aChildStations.push(station);
                    }
                }
                aStation.push(aChildStations);
            }
            aCitys.push(aCity);
            aStations.push(aStation);
        }
        isInitStation=true;
        InitTable();
        }
    //初始化强降温强降水表格
    function InitTable(){
        isInitTable=false;
        if(!isInitStation){
            return;
        }
        var strForcastTime=$("#dateSelect").find("input").val();
        var dtForcastTime=new Date(strForcastTime);
        if(elementID==0||elementID==1){
            <!--头标题-->
            //标题
            var headTitleHtml="";
            headTitleHtml+="<tr>"
            for(var c=0;c<hourspans.length+3;c++){//天数+3列
                if(c==0){
                    headTitleHtml+="<th>区域</th>";
                }
                else if(c==1){
                    headTitleHtml+="<th>市/区</th>";
                }
                else if(c==2){
                    headTitleHtml+="<th>站点</th>";
                }
                else{
                    var tempDate=dtForcastTime.format("yyyy-MM-dd");
                    headTitleHtml+="<th>"+tempDate+"</th>";
                    dtForcastTime.addDays(1);
                }
            }
            headTitleHtml+="</tr>";
            $("#FXYBTopTable").html(headTitleHtml);
            //左侧
            var leftHtml="";
            for(var ass=0;ass<aStations.length;ass++){//区域
                var citys=aStations[ass];
                var areaName=citys[0][0]["areaname"];
                var areaStaionCount=0;
                for(var c=0;c<citys.length;c++) {//区站点数
                    var cityStations=citys[c];
                    areaStaionCount+=cityStations.length;
                }
                for(var c=0;c<citys.length;c++){//市、区
                    var cityStations=citys[c];
                    var cityName=cityStations[0]["cityname"];
                    for(var s=0;s<cityStations.length;s++){//站
                        var stationNum=cityStations[s]["stationnum"];
                        var stationName=cityStations[s]["stationname"];
                        leftHtml+="<tr>";//气温
                        if(c==0){
                            if(s==0){
                                leftHtml+="<td rowspan='"+areaStaionCount+"'>"+areaName+"</td><td rowspan='"+cityStations.length+"'>"+cityName+"</td><td>"+stationName+stationNum+"</td>"
                            }
                            else{
                                leftHtml+="<td>"+stationName+stationNum+"</td>";
                            }
                        }
                        else{
                            if(s==0){
                                leftHtml+="<td rowspan='"+cityStations.length+"'>"+cityName+"</td><td>"+stationName+stationNum+"</td>";
                            }
                            else{
                                leftHtml+="<td>"+stationName+stationNum+"</td>";
                             }
                        }
                    }
                }
            }
            $("#FXYBLeftTable").html(leftHtml);
            //内容
            var dataHtml="";
            for(var r=0;r<stations.length;r++){
                dataHtml+="<tr>";
                for(var d=0;d<hourspans.length;d++){
                    dataHtml+="<td></td>";
                }
                dataHtml+="</tr>";
            }
            $("#FXYBDataTable").html(dataHtml);
            $("#FXYBDataDiv").css("margin-left","255px");
        }
        else if(elementID==2||elementID==3){
            //标题
            var headTitleHtml="";
            headTitleHtml+="<tr><th>区域</th><th>市/区</th><th>站点</th></tr>";
            $("#FXYBTopTable").html(headTitleHtml);
            //左侧
            var leftHtml="";
            for(var ass=0;ass<aStations.length;ass++){//区域
                var citys=aStations[ass];
                var areaName=citys[0][0]["areaname"];
                var areaStaionCount=0;
                for(var c=0;c<citys.length;c++) {//区站点数
                    var cityStations=citys[c];
                    areaStaionCount+=cityStations.length;
                }
                for(var c=0;c<citys.length;c++){//市、区
                    var cityStations=citys[c];
                    var cityName=cityStations[0]["cityname"];
                    for(var s=0;s<cityStations.length;s++){//站
                        var stationNum=cityStations[s]["stationnum"];
                        var stationName=cityStations[s]["stationname"];
                        leftHtml+="<tr>";//气温
                        if(c==0){
                            if(s==0){
                                leftHtml+="<td rowspan='"+areaStaionCount+"'>"+areaName+"</td><td rowspan='"+(cityStations.length)+"'>"+cityName+"</td><td>"+stationName+stationNum+"</td>"
                            }
                            else{
                                leftHtml+="<td>"+stationName+stationNum+"</td>";
                            }
                        }
                        else{
                            if(s==0){
                                leftHtml+="<td rowspan='"+cityStations.length+"'>"+cityName+"</td><td>"+stationName+stationNum+"</td>";
                            }
                            else{
                                leftHtml+="<td>"+stationName+stationNum+"</td>";
                            }
                        }
                    }
                }
            }
            $("#FXYBLeftTable").html(leftHtml);
            //内容
            var dataHtml="";
            for(var r=0;r<stations.length;r++){
                dataHtml+="<tr>";
                for(var d=0;d<hourspans.length;d++){
                    dataHtml+="<td></td>";
                }
                dataHtml+="</tr>";
                dataHtml+="<tr>";
                for(var d=0;d<hourspans.length;d++){
                    dataHtml+="<td></td>";
                }
                dataHtml+="</tr>";
            }
            $("#FXYBDataTable").html(dataHtml);

            var contentHtml="";
            for(var s=0;s<stations.length;s++){
                contentHtml+="<tr></tr>";
            }
            $("#FXYBDataTable").html(contentHtml);
            $("#FXYBDataDiv").css("margin-left","255px");
        }
        isInitTable=true;
        displayData();
        t.tableEdit();//初始化表格
    }
    //显示数据
    function displayData(){
        if(isDownData&&isInitTable){
            if(elementID==0||elementID==1){
                viewZRData();
            }
            else{
                CalQJAndDisplay();
            }
        }
    }
    function getZDYBPublishTime(){
        $.ajax({
            type: 'post',
            url: Url_Config.gridServiceUrl+"services/ForecastfineService/getZDYBPublishTime",
            data: {'para': '{"depart":"%'+ t.areaName+'%","areaCode":"%'+GDYB.GridProductClass.currentUserDepart.departCode+',%"}'},
            dataType: 'json',
            error: function () {
                alert('获取制作时间出错!');
            },
            success: function (data) {
                t.elementData = data;
                for(var i=0;i< t.elementData.length;i++){
                    if(GDYB.GridProductClass.currentUserDepart.departCode.length == 2){
                        t.elementData[i].gdybPublishTime = t.elementData[i].gdybPublishTime.split(",")[0];
                        t.elementData[i].endTime = t.elementData[i].endTime.split(",")[0];
                        t.elementData[i].gdybType = t.elementData[i].gdybType.split(",")[0];
                    }
                    else{
                        t.elementData[i].gdybPublishTime = t.elementData[i].gdybPublishTime.split(",")[1];
                        t.elementData[i].endTime = t.elementData[i].endTime.split(",")[1];
                        t.elementData[i].gdybType = t.elementData[i].gdybType.split(",")[1];
                    }
                }
                var contentProduct ="";
                var productName = "";
                for(var i=0;i<data.length;i++){
                    if(productName != data[i].type){
                        productName = data[i].type;
                        if(i!=0){
                            contentProduct += '</div>';
                        }
                        contentProduct += '<div class="dis_menu_head" >'+data[i].productName+'</div>' +
                            '<div class="dis_menu_body">';
                    }
                    contentProduct += '<div class="dis_menu_body_item" href="#" elementId="'+i+'">'+data[i].name+ '<span title="未上网" class="zdybProductIsNotSubmit"></div>';
                    //contentProduct += '<div class="dis_menu_body_item" href="#" elementId="'+i+'">' +'<p style="float: left;margin-top: 8px;">'+data[i].name+'</p>'+'<p id="InitState"></p>'+'</div>';
                }
                contentProduct += '</div>';
                $("#zdybProductTypePanel").html(contentProduct);
                $("#zdybProductTypePanel").find("div.dis_menu_body_item").click(function(){
                    $("#zdybProductTypePanel").find("div.dis_menu_body_item").css("background-color","");
                    $("#zdybProductTypePanel").find("div.productActive").removeClass("productActive");
                    $(this).css("background-color","rgb(158,195,255)");
                    $(this).addClass("productActive");
                    $("#ZDYBAllContent").css("display","block");
                    $("#gdybProduct").css("display","none");
                    elementID=$(this).attr("elementid");
                    isInitTable=false;
                    InitTable();
                });
                $("#zdybProductTypePanel div.dis_menu_head").click(function() {
                    if ($(this).hasClass("dis_current")) {
                        $(this).removeClass("dis_current").next("div.dis_menu_body").slideToggle(300);
                    } else {
                        $(this).addClass("dis_current").next("div.dis_menu_body").slideToggle(300).slideUp("slow");
                    }
                });
                getAllProductNum();
                $($("#zdybProductTypePanel").find("div.dis_menu_body_item")[0]).click();
                getServiceTime();
            }
        });
    }
    function getAllProductNum(){
        var data = t.elementData;
        if(data==undefined){
            return;
        }
        $.ajax({
            type: 'post',
            url: Url_Config.gridServiceUrl + "services/ForecastfineService/getZDYBOutType",
            data: null,
            dataType: 'json',
            error: function () {
                alert('获取输出类型错误!');
            },
            success: function (list) {
                var outTypeObj = {};
                for(var i=0;i<list.length;i++){
                    outTypeObj[list[i].type] = list[i].name;
                }
                var nameList = [];
                for(var i=0;i<data.length;i++){
                    var outlist = data[i].outType.split(",");
                    var list = [];
                    var nowTime = $("#dateSelect").find("input").val();
                    var nowDate = new Date(nowTime.substr(5,2)+" "+nowTime.substr(8,2)+","+nowTime.substr(0,4)+" "+data[i].makeTime);
                    nowDate.setHours(nowDate.getHours()-8);
                    for(var j=0;j<outlist.length;j++){
                        if(outlist[j]=="1"){
                            var makeTime=nowTime.replace(/-/g,"");
                            //var time = nowTime.substr(0,4)+nowTime.substr(5,2)+nowTime.substr(8,2)+data[i].forecastTime+"00-"+(Array(2).join(0)+data[i].hourSpanTotal).slice(-3)+ data[i].hourSpan;
                            var obj = {};
                            obj.productName = data[i].productName+"/"+outTypeObj[outlist[j]];
                            obj.name=strZhuRiTemp.replace("date",makeTime);
                            list.push(obj);
                        }
                        else if(outlist[j]=="2"){
                            var makeTime=nowTime.replace(/-/g,"");
                            //var time = nowTime.substr(0,4)+nowTime.substr(5,2)+nowTime.substr(8,2)+data[i].forecastTime+"00-"+(Array(2).join(0)+data[i].hourSpanTotal).slice(-3)+ data[i].hourSpan;
                            var obj = {};
                            obj.productName = data[i].productName+"/"+outTypeObj[outlist[j]];
                            obj.name=strZhuRiPrec.replace("date",makeTime);
                            list.push(obj);
                        }
                        else if(outlist[j]=="3"){
                            var makeTime =nowTime.replace(/-/g,"")+data[i].zdybHour+"0000";
                            var obj = {};
                            obj.productName = data[i].productName+"/"+outTypeObj[outlist[j]];
                            obj.name=strQiangJiangTemp.replace("date",makeTime);
                            list.push(obj);
                        }
                        else if(outlist[j]=="4"){
                            var makeTime =makeTime.replace(/-/g,"")+data[i].zdybHour+"0000";;
                            var obj = {};
                            obj.productName = data[i].productName+"/"+outTypeObj[outlist[j]];
                            obj.name=strQiangJiangPrec.replace("date",makeTime);
                            list.push(obj);
                        }
                        else {
                            var obj = {};
                            obj.productName = data[i].productName+"/"+outTypeObj[outlist[j]];
                            obj.name = "test.TXT";
                            list.push(obj);
                        }
                    }
                    nameList.push(list);
                }
                var nameListStr = JSON.stringify(nameList)
                var param = '{"areaName":"'+ t.areaName+'","nameList":'+nameListStr+'}';
                $.ajax({
                    type: 'post',
                    url: Url_Config.gridServiceUrl + "services/ForecastfineService/getAllProductNum",
                    data: {'para': param},
                    dataType: 'json',
                    error: function () {
                        alert('获取输出类型错误!');
                    },
                    success: function (data) {
                        var list = $("#zdybProductTypePanel").find("div.dis_menu_body_item");
                        for(var i=0;i<data.length;i++){
                            $(list[i]).find("span").attr("class","hh");
                            if(data[i]==1){
                                $(list[i]).find("span").attr("class","zdybProductIsSubmit");
                            }
                            else{
                                $(list[i]).find("span").attr("class","zdybProductIsNotSubmit");
                            }
                        }
                    }
                });
            }
        });
    }
    //获取服务器时间
    function getServiceTime(){
        $.ajax({
            type: 'post',
            url: Url_Config.gridServiceUrl+"services/ForecastfineService/getServiceTime",
            data: null,
            dataType: 'text',
            error: function () {
                alert('获取服务器时间错误!');
            },
            success: function (data) {
                var nowTime = $("#dateSelect").find("input").val();
                var nowDate =  new Date(data.substr(5,2)+" "+data.substr(8,2)+","+data.substr(0,4)+data.substr(10,9));
                var endDate;
                var time = data.substr(11,8);
                var list = $($("#zdybProductTypePanel").find("div.dis_menu_body")[1]).find("div.dis_menu_body_item");
                var allPast = true;
                var endtime = "";

                for(var i=0;i<list.length;i++){
                    if(GDYB.GridProductClass.currentUserDepart.departCode.length == 2){
                        endtime = t.elementData[parseInt($(list[i]).attr("elementId"))].endTime.toString().split(",")[0];
                    }
                    else{
                        endtime = t.elementData[parseInt($(list[i]).attr("elementId"))].endTime.toString().split(",")[1];
                    }
                    if(time< endtime){
                        endDate = new Date(nowTime.substr(5,2)+" "+nowTime.substr(8,2)+","+nowTime.substr(0,4)+" "+endtime);
                        //$(list[i]).click();
                        allPast = false;
                        break;
                    }
                }
                if(allPast&&list.length!=0){
                    if(GDYB.GridProductClass.currentUserDepart.departCode.length == 2){
                        endtime = t.elementData[parseInt($(list[list.length-1]).attr("elementId"))].endTime.toString().split(",")[0];
                    }
                    else{
                        endtime = t.elementData[parseInt($(list[list.length-1]).attr("elementId"))].endTime.toString().split(",")[1];
                    }
                    endDate = new Date(nowTime.substr(5,2)+" "+nowTime.substr(8,2)+","+nowTime.substr(0,4)+" "+endtime);
                    //$(list[list.length-1]).click();
                }
                t.endTime = (endDate.getTime()-nowDate.getTime())/1000;
                if(!t.refreshTime){
                    refreshEndTime();
                }
                t.refreshTime = true;
            }
        });
    }
    //时间倒计时
    function refreshEndTime(){
        if(t.endTime>0){
            var hours = parseInt(t.endTime/60/60);
            var minutes = parseInt(t.endTime/60)%60;
            var seconds = t.endTime%60;
            $("#FXYBSpanTimeCountdown").html(hours+"小时"+minutes+"分钟"+seconds+"秒");
            $("#FXYBCountdownTitle").html("注意：距上网截止时间还剩");
            t.endTime--;
        }
        else{
            $("#FXYBSpanTimeCountdown").html("注意：该时次预报已过规定的上网截止时间");
            $("#FXYBCountdownTitle").html("");
        }
        setTimeout(function(){
            if(t.refreshTime == true){
                refreshEndTime();
            }
        },1000);
    }
    //初始化产品template
    function initZDYBProduct(obj){
        var elementId = parseInt($(obj).attr("elementId"));
        t.nowElement = t.elementData[elementId];
        getServiceTime();
        var nowTime = $("#dateSelect").find("input").val();
        var time = nowTime.substr(0,4)+"年"+nowTime.substr(5,2)+"月"+nowTime.substr(8,2)+"日";
        $("#zdybTitle").html(GDYB.GridProductClass.currentUserDepart.departName+"气象台"+time+t.nowElement.name+"制作");//标题
    }
    //获取格点
    function GetGrid(){
        var strHourSpans="";
        for(var h=0;h<hourspans.length;h++){
            strHourSpans+=hourspans[h]+",";
        }
        strHourSpans=strHourSpans.substring(0,strHourSpans.length-1);
        DownGrid(0,strHourSpans);
    }
    //下载逐日数据
    function DownGrid(index,strHourSpans){
        if(index<elements.length){
            var elementName=index==0?"降水":"气温";
            $("#div_progress_title").html("正在下载"+elementName+"格点数据!");
            $("#div_progress").css("display", "block");
            var strDate=$("#dateSelect").find("input").val();//日期
            grids=new Array();
            var url=Url_Config.gridServiceUrl+"services/GridService/getGrids";
            var type="prvn";
            var strMakeTime=strDate+" "+"00:00:00";
            var strDateTime=strDate+" "+"00:00:00";
            $.ajax({
                data:{"para":"{element:'"+ elements[index] + "',type:'"+ type + "',level:'"+ 1000 + "',hourspans:'"+ strHourSpans + "',maketime:'" + strMakeTime + "',version:'" + "p" + "',datetime:'"+ strDateTime + "'}"},
                url:url,
                dataType:"json",
                success:function(datas){
                    if(datas==undefined||datas.length==0||datas[0].rows==0){
                        $("#div_progress_title").html(strDate+"日"+elementName+"无数据!");
                        $("#div_progress").css("display", "none");
                        dicGrids[elements[index]]=undefined;
                        DownGrid(index+1,strHourSpans);
                        return;
                    }
                    for (var k = 0; k < datas.length; k++) {
                        var data = datas[k];
                        var datasetGrid = null;
                        var dvalues = data.dvalues;
                        var nwpModelTime = null;
                        if (dvalues != null && dvalues.length > 0) {
                            var bWind = false; //是否为风场，风场具有两个字段（风向、风速），在dvalues中交叉表示
                            var hasTag = (!bWind)&&(dvalues.length==data.rows*data.cols*2);
                            var dimensions = (bWind||hasTag) ? 2 : 1; //维度，风场有两维；带有Tag属性也是两维
                            var dMin = 9999;
                            var dMax = -9999;
                            datasetGrid = new WeatherMap.DatasetGrid(data.left, data.top, data.right, data.bottom, data.rows, data.cols);
                            datasetGrid.noDataValue = data.noDataValue;
                            if (data.nwpmodelTime != null)
                                nwpModelTime = data.nwpmodelTime;
                            var grid = [];
                            var tag = [];
                            for (var i = 0; i < data.rows; i++) {
                                var tagLine = [];
                                var nIndexLine = data.cols * i * dimensions;
                                for (var j = 0; j < data.cols; j++) {
                                    var nIndex = nIndexLine + j * dimensions;
                                    var x = data.left + j * datasetGrid.deltaX;
                                    var y = data.top - i * datasetGrid.deltaY;
                                    var z;
                                    if (bWind) {
                                        z = dvalues[nIndex + 1];
                                        grid.push(Math.round(dvalues[nIndex+1])); //风速在前
                                        grid.push(Math.round(dvalues[nIndex]));   //风向在后
                                    }
                                    else {
                                        z = dvalues[nIndex];
                                        grid.push(Math.round(dvalues[nIndex] * 10) / 10);
                                        if(hasTag)
                                            tagLine.push(dvalues[nIndex+1]);
                                    }
                                    if (z != 9999 && z != -9999) {
                                        if (z < dMin)
                                            dMin = z;
                                        if (z > dMax)
                                            dMax = z;
                                    }
                                }
                                if(hasTag)
                                    tag.push(tagLine);
                            }
                            datasetGrid.grid = grid;
                            datasetGrid.dMin = dMin;
                            datasetGrid.dMax = dMax;
                            if(hasTag){
                                datasetGrid.tag = tag;
                                datasetGrid.defaultTag = 0;
                            }
                        }
                        grids[hourspans[k]]=datasetGrid;
                    }
                    if(grids.length>0){
                        dicGrids[elements[index]]=grids;
                    }
                    DownGrid(index+1,strHourSpans);
                },
                error:function(ex){
                    $("#div_progress").css("display", "none");
                    alert("下载"+elements[index]+"失败!");
                },
                type:"POST"
            });
        }
        else{
            $("#div_progress").css("display", "none");
            isDownData=true;
            displayData();
        }
    }
    //逐日数据显示
    function viewZRData() {
        for (var elementKey in dicGrids) {
            if (elementID==0&&elementKey == "2t") {//给偶数行赋值
                var tGrids = dicGrids[elementKey];
                if (tGrids == undefined) {
                    for (var s = 0; s < stations.length; s++) {//循环行
                        for (var d = 0; d < hourspans.length; d++) {
                            $("#FXYBDataTable tr:eq(" + s + ") td:eq(" + d + ")").html("");
                        }
                    }
                }
                else {
                    var colIndex = 0;
                    var rowIndex=0;
                    for (var key in tGrids) {
                        if(isNaN(key)){
                            break;
                        }
                        var datasetGrid = tGrids[key];
                        for (var as = 0; as < aStations.length; as++) {
                            var tempArea = aStations[as];
                            for (var aa = 0; aa < tempArea.length; aa++) {
                                var tempCity = tempArea[aa];
                                for (var s = 0; s < tempCity.length; s++) {
                                    var tempStation = tempCity[s];
                                    var lon = parseFloat(tempStation["longitude"]);
                                    var lat = parseFloat(tempStation["latitude"]);
                                    var grid = datasetGrid.xyToGrid(lon, lat);
                                    var valIndex=datasetGrid.cols*grid.y+grid.x;
                                    var val = datasetGrid.grid[valIndex];
                                    $("#FXYBDataTable tr:eq(" + rowIndex + ") td:eq(" + colIndex + ")").html(val);
                                    rowIndex++;
                                }
                            }
                        }
                        colIndex++;
                        rowIndex=0;
                    }
                }
            }
            else if (elementID==1&&elementKey == "r24") {//给基数行赋值
                var rGrids = dicGrids[elementKey];
                if (rGrids == undefined) {
                    for (var s = 0; s < stations.length; s++) {//循环站点
                        for (var d = 0; d < hourspans.length; d++) {
                            $("#FXYBDataTable tr:eq(" + s + ") td:eq(" + d + ")").html("");
                        }
                    }
                }
                else {
                    var colIndex = 0;
                    var rowIndex=0;
                    for (var key in rGrids) {
                        if(isNaN(key)){
                            break;
                        }
                        var datasetGrid = rGrids[key];
                        for (var as = 0; as < aStations.length; as++) {
                            var tempArea = aStations[as];
                            for (var aa = 0; aa < tempArea.length; aa++) {
                                var tempCity = tempArea[aa];
                                for (var s = 0; s < tempCity.length; s++) {
                                    var tempStation = tempCity[s];
                                    var lon = parseFloat(tempStation["longitude"]);
                                    var lat = parseFloat(tempStation["latitude"]);
                                    var grid = datasetGrid.xyToGrid(lon, lat);
                                    var valIndex=datasetGrid.cols*grid.y+grid.x;
                                    var val = datasetGrid.grid[valIndex];
                                    $("#FXYBDataTable tr:eq(" + rowIndex + ") td:eq(" + colIndex + ")").html(val);
                                    rowIndex++;
                                }
                            }
                        }
                        colIndex++;
                        rowIndex=0;
                    }
                }
            }
        }
    }
    //强降水强降温
    function CalQJAndDisplay(){
        $("#FXYBDataTable tr").html("");
        var strForcastTime=$("#dateSelect").find("input").val();
        var forcastTime=new Date(strForcastTime);
        var maxCount=0;//总行数的最大值
        for(var elementKey in dicGrids){
            var rowIndex=0;
            if(elementID==2&&elementKey=="2t"){//温度
                var grids=dicGrids[elementKey];
                if(grids==undefined){
                    break;
                }
                for (var as = 0; as < aStations.length; as++) {
                    var tempArea = aStations[as];
                    for (var aa = 0; aa < tempArea.length; aa++) {
                        var tempCity = tempArea[aa];
                        for (var s = 0; s < tempCity.length; s++) {
                            var firstDay=new Date(strForcastTime);
                            var tempStation = tempCity[s];
                            var lon = parseFloat(tempStation["longitude"]);
                            var lat = parseFloat(tempStation["latitude"]);
                            var stationName=tempStation["stationname"];
                            var stationMaxCount=0;
                            var preVal=0;
                            var sum=0;//累积
                            var strStartTime;
                            var strEndTime;
                            var bFirst=true;//第一个
							var countinuDay=0;
                            for(var key in grids){
                                if(isNaN(key)){
                                    break;
                                }
                                var dayIndex=parseInt(key)/24;
                                var datasetGrid=grids[key];
                                var grid=datasetGrid.xyToGrid(lon,lat);
                                var valIndex=datasetGrid.cols*grid.y+grid.x;
                                var val = datasetGrid.grid[valIndex];
                                if(bFirst){//第一个
                                    preVal=val;
                                    strStartTime=firstDay.addDays(1).format("yyyy-MM-dd");//第一天没法做比较，所以开始时间就从第二天
                                    bFirst=false;
                                }
                                else{
                                    var cha=val-preVal;
                                    preVal=val;
                                    if(cha<-0.5){//暂时设置成0.5
										if(countinuDay==3&&sum<-4){
											stationMaxCount++;
											countinuDay=0;
											var endTime=firstDay.addDays(dayIndex-1);//昨天
											var strEndTime=endTime.format("yyyy-MM-dd");
											var col="<td disabled='disabled'>"+strStartTime+"</td><td disabled='disabled'>"+strEndTime+"</td><td>"+Math.abs(sum.toFixed(0))+"</td>";
											$("#FXYBDataTable tr:eq("+rowIndex+")").append(col);
											countinuDay=0;
											sum=0;
                                            firstDay=new Date(strForcastTime);
											strStartTime=firstDay.addDays(dayIndex).format("yyyy-MM-dd");
										}
                                        sum+=cha;
										countinuDay++;
                                        strEndTime=firstDay.addDays(dayIndex-1).format("yyyy-MM-dd");//当天
                                        if(dayIndex==hourspans.length&&sum<-4){//最后一天
                                            var col="<td disabled='disabled'>"+strStartTime+"</td><td disabled='disabled'>"+strEndTime+"</td><td>"+Math.abs(sum.toFixed(0))+"</td>";
                                            $("#FXYBDataTable tr:eq("+rowIndex+")").append(col);
                                            stationMaxCount++;
                                        }
                                    }
                                    else{
                                        if(sum<-4){//小于-4
                                            stationMaxCount++;
                                            var endTime=firstDay.addDays(dayIndex-1);//昨天
                                            var strEndTime=endTime.format("yyyy-MM-dd");
                                            var col="<td disabled='disabled'>"+strStartTime+"</td><td disabled='disabled'>"+strEndTime+"</td><td>"+Math.abs(sum.toFixed(0))+"</td>";
                                            $("#FXYBDataTable tr:eq("+rowIndex+")").append(col);
                                        }
                                        countinuDay=0;
                                        sum=0;
                                        firstDay=new Date(strForcastTime);
                                        strStartTime=firstDay.addDays(dayIndex).format("yyyy-MM-dd");
                                    }
                                    firstDay=new Date(strForcastTime);
                                }
                            }
                            if(stationMaxCount>maxCount){
                                maxCount=stationMaxCount;
                            }
                            rowIndex++;
                        }
                    }
                }
            }
            else if(elementID==3&&elementKey=="r24"){//降水
                var rowIndex=0;
                var grids=dicGrids[elementKey];
                if(grids==undefined){
                    break;
                }
                for (var as = 0; as < aStations.length; as++) {
                    var tempArea = aStations[as];
                    for (var aa = 0; aa < tempArea.length; aa++) {
                        var tempCity = tempArea[aa];
                        for (var s = 0; s < tempCity.length; s++) {
                            var firstDay=new Date(strForcastTime);
                            var tempStation = tempCity[s];
                            var lon = parseFloat(tempStation["longitude"]);
                            var lat = parseFloat(tempStation["latitude"]);
                            var stationName=tempStation["stationname"];
                            var stationMaxCount=0;
                            var sum=0;//累积
                            var strStartTime;
                            var strEndTime;
							var countinuDay=0;
                            for(var key in grids){
                                if(isNaN(key)){
                                    break;
                                }
                                var dayIndex=parseInt(key)/24;
                                var datasetGrid=grids[key];
                                var grid=datasetGrid.xyToGrid(lon,lat);
                                var valIndex=datasetGrid.cols*grid.y+grid.x;
                                var val = datasetGrid.grid[valIndex];
                                if(dayIndex==1){//第一天
                                    strStartTime=firstDay.format("yyyy-MM-dd");
                                }
                                if(val>1){//每天应该大于1mm
                                    if(countinuDay==3&&sum>10){
											if(sum>10){//大于10
											var col="<td disabled='disabled'>"+strStartTime+"</td><td disabled='disabled'>"+strEndTime+"</td><td>"+sum.toFixed(0)+"</td>";
											$("#FXYBDataTable tr:eq("+rowIndex+")").append(col);
											stationMaxCount++;
											}
                                            firstDay=new Date(strForcastTime);
											strStartTime=firstDay.addDays(dayIndex).format("yyyy-MM-dd");//每次都认为第二天是开始时间
											sum=0;
											countinuDay=0;
										}
										sum+=val;
										countinuDay++;
										strEndTime=firstDay.addDays(dayIndex-1).format("yyyy-MM-dd");//当天
										if(dayIndex==hourspans.length&&sum>10){//最后一天
											var col="<td disabled='disabled'>"+strStartTime+"</td><td disabled='disabled'>"+strEndTime+"</td><td>"+sum.toFixed(0)+"</td>";
											$("#FXYBDataTable tr:eq("+rowIndex+")").append(col);
											stationMaxCount++;
										}
                                }
                                else{
                                    if(sum>10){//大于10
                                        var col="<td disabled='disabled'>"+strStartTime+"</td><td disabled='disabled'>"+strEndTime+"</td><td>"+sum.toFixed(0)+"</td>";
                                        $("#FXYBDataTable tr:eq("+rowIndex+")").append(col);
                                        stationMaxCount++;
                                    }
                                    firstDay=new Date(strForcastTime);
                                    strStartTime=firstDay.addDays(dayIndex).format("yyyy-MM-dd");//每次都认为第二天是开始时间
                                    sum=0;
									countinuDay=0;
                                }
                                firstDay=new Date(strForcastTime);
                            }
                            if(stationMaxCount>maxCount){
                                maxCount=stationMaxCount;
                            }
                            rowIndex++;
                        }
                    }
                }
            }
        }
        var headHtml="";
        for(var c=0;c<maxCount;c++){
            headHtml+="<th></th><th>第"+(c+1)+"过程</th><th></th>";
        }
        $("#FXYBTopTable tr:eq(0)").append(headHtml);
    }
    //查看报文
    function ShowBaoWen(){
        var data= t.elementData;
        var strForcastTime=$("#dateSelect").find("input").val();
        var startDay=new Date(strForcastTime);
        var fileName="";
        if(elementID==0){
            var makeTime=startDay.format("yyyyMMdd");
            fileName=strZhuRiTemp.replace("date",makeTime);
        }
        else if(elementID==1){
            var makeTime=startDay.format("yyyyMMdd");
            fileName=strZhuRiPrec.replace("date",makeTime);
        }
        else if(elementID==2){
            var makeTime = startDay.format("yyyyMMdd")+data[elementID].zdybHour+"0000";
            fileName=strQiangJiangTemp.replace("date",makeTime);
        }
        else if(elementID==3){
            var makeTime = startDay.format("yyyyMMdd")+data[elementID].zdybHour+"0000";
            fileName=strQiangJiangPrec.replace("date",makeTime);
        }
       //取ProductName,由本节点和父结点组成
        var productName=data[elementID].productName+"/"+data[elementID].name;
        var content="";
        if(elementID==0||elementID==1){
            content+=padRight("station",10," ");
            var firstDay=new Date(strForcastTime);
            for(var d=0;d<35;d++){//日期
                var strDay=firstDay.format("yyyyMMdd");
                content+=padRight(strDay,10," ");
                firstDay.addDays(1);
            }
            content+="\n";//第一行
            var drLength=$("#FXYBLeftTable tr").length;//取站点
            for(var r=0;r<drLength;r++){
                var strStationNum=$("#FXYBLeftTable tr:eq("+r+") td:last")[0].innerHTML;
                var stationNum=strStationNum.substr(strStationNum.length-5,5);//最后6位
                content+=padRight(stationNum,10," ");
                for(var d=0;d<hourspans.length;d++){
                    var val=$("#FXYBDataTable tr:eq("+r+") td:eq("+d+")")[0].innerHTML;
                    content+=padRight(val,10," ");
                }
                content+="\n";
            }
        }
        else if(elementID==2||elementID==3){
            var drLength=$("#FXYBLeftTable tr").length;//取站点
            for(var r=0;r<drLength;r++){
                var strStationNum=$("#FXYBLeftTable tr:eq("+r+") td:last")[0].innerHTML;
                var stationNum=strStationNum.substr(strStationNum.length-5,5);//最后6位
                content+=stationNum+" ";//6位
                content+=startDay.format("yyyyMMdd")+" ";//预报时间
                content+="010 ";//预报时间
                var tdLength=$("#FXYBDataTable tr:eq("+r+") td").length;
                var count=Math.floor(tdLength/3);
                var strCount="";
                if(count<10){
                    strCount+="0"+count;
                }
                else{
                    strCount=count;
                }
                content+=strCount+" ";
                for(var c=0;c<count;c++){
                    var strSTime=$("#FXYBDataTable tr:eq("+r+") td:eq("+(c*3+0)+")")[0].innerHTML;//开始时间
                    var strETime=$("#FXYBDataTable tr:eq("+r+") td:eq("+(c*3+1)+")")[0].innerHTML;//结束时间
                    var strVal=$("#FXYBDataTable tr:eq("+r+") td:eq("+(c*3+2)+")")[0].innerHTML;//值
                    content+=strSTime+" ";
                    content+=strETime+" ";
                    content+=strVal+" ";
                }
                content+="\n";
            }
        }
        content=content.replace(/-/g,"");//替换时间的-
        FXYBUpload(productName,fileName,content);
    }
    function FXYBUpload(productName,name,content){
        var param = '{"areaName":"'+ t.areaName+'","productName":"'+productName+'","name":"'+name+'","data":"'+content+'"}';
        $.ajax({
            type: 'post',
            url: Url_Config.gridServiceUrl+"services/ForecastfineService/ForecastToTXT",
            data: {'para': param},
            dataType: 'text',
            error: function () {
                alert('上传'+productName+'错误!');
            },
            success: function (data) {
                getAllProductNum();
                alert('上传'+productName+'成功');
            }
        });
    }
    //向右补齐
    function padRight(str,lenght,placeHolder){
        var result="";
        result+=str;
        var cha=lenght-str.length;
        for(var c=0;c<cha;c++){
            result+=placeHolder;
        }
        return result;
    }
    this.tableEdit=function(){
        $("#FXYBDataTable td").bind("click",function(){
            var cell = $(this);
            if(cell.attr("disabled")=="disabled"){
                return false;
            }
            if (cell.children("input").length > 0) {
                return false;
            }
            var input = $("<input/>");
            var originalValue = cell.html();
            input.attr("value", originalValue);
            cell.html("");
            input.width(cell.width());
            input.css({"border-width":"0","color":"blue","font-size":"16px"});
            input.appendTo(cell);
            input.trigger("focus").trigger("select");
            input.click(function(){
                return false;
            });
            input.keyup(function(event){
                var keycode = event.which;
                if (13 == keycode) {
                    var str = $(this).val();
                    cell.html(str);
                }
                if (27 == keycode) {
                    cell.html(originalValue);
                }
            });
            input.blur(function(){
                var newVal=$(this).val();
                if(newVal==""){//如果是无效值就变成原来的值
                    cell.html(originalValue);
                }
                else{
                    cell.html(newVal);
                }
            });
        });
    };
}
ZDYBPageClass.prototype = new PageBase();