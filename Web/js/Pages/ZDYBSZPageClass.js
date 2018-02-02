/**
 * Created by Administrator on 2016/3/4.
 */
/**
 * @author zj
 * 2016-2-23
 * @description 站点预报设置页面
 */

function ZDYBSZPageClass(){
    this.elementData = null;
    this.nowElement = null;
    this.addProduct = false;
    var t= this;
    this.renderMenu = function(){
        if(GDYB.GridProductClass.currentUserDepart.departCode.length != 2){
            alert("仅区台用户可以设置");
            return;
        }

        /*if(GDYB.GridProductClass.currentUserName != "admin"){
         alert("仅管理员可以设置");
         return;
         }*/

        $("#workspace_div").append("<div id='ZDYBSet' style='height: 100%;right: 0px;position: absolute;font-size: 12px;background-color: #FFFFFF;overflow: auto;'></div>");

        if($.cookie("departCode").length == 2){
            t.areaName = "qutai";
        }
        else{
            t.areaName = "shitai";
        }
        var width = document.body.offsetWidth;
        $("#ZDYBSet").css("width",parseInt(width)-415);//左侧面板宽度415px

        //左侧面板设置
        var contentPanel = "<div id='zdybPanelTop' class='zdybPanelTop'></div>";
        $("#menu_bd").html(contentPanel);
        $("#map_div").css("display","none");
        initZDYBProductType();
    }

    function initZDYBProductType(){
        $.ajax({
            type: 'post',
            url: gridServiceUrl+"services/ForecastfineService/getZDYBType",
            data: null,
            dataType: 'text',
            error: function () {
                alert('获取产品类型错误!');
            },
            success: function (data) {
                var list = JSON.parse(data);
                var contentProduct = "";
                var productName = "";
                for (var i = 0; i < list.length; i++) {
                    contentProduct += '<div class="dis_menu_head" flag="show">' + list[i].name + '</div>' +
                        '<div  class="dis_menu_body">';
                    contentProduct += '<div id="zdybSetProductAdd'+list[i].id+'" stationType="'+list[i].stationType+'" flagId="'+list[i].id+'" class="dis_menu_body_item zdybAddForecastTimeBT" style="font-size: 28px;"  href="#">＋</div></div>';
                }
                contentProduct += '<div class="dis_menu_head" flag="add">＋添加预报类型</div>';
                $("#zdybPanelTop").html(contentProduct);
                initZDYBPublishTime();
                $("#zdybPanelTop div.dis_menu_head").click(function() {
                    if(t.addProduct){
                        return;
                    }
                    if ($(this).hasClass("dis_current")) {
                        $(this).removeClass("dis_current").next("div.dis_menu_body").slideToggle(300);
                    } else {
                        $(this).addClass("dis_current").next("div.dis_menu_body").slideToggle(300).slideUp("slow");
                    }
                    if($(this).attr("flag")=="add"){
                        t.addProduct = true;
                        $("#zdybPanelTop").find("div.dis_menu_body_item").css("background-color","")
                        $(this).html('<input id="zdybAddProductType" style="width: 180px;height: 25px;margin: 0px;" type="text">');
                        $("#zdybAddProductType")[0].focus();
                        var contentHtml = "";
                        $.ajax({
                            type: 'post',
                            url: gridServiceUrl + "services/ForecastfineService/getZDYBStationType",
                            data: null,
                            dataType: 'text',
                            error: function () {
                                alert('获取站点类型错误!');
                            },
                            success: function (data) {
                                var list = JSON.parse(data);
                                contentHtml += "<div style='border-bottom: 1px solid rgb(200,200,200);margin-top: 10px;float: left;width: 100%;'>" +
                                    "<div style='font-weight: bold;'>显示表格</div>" +
                                    "<div style='margin-left: 60px;height: 40px;'>" +
                                    "<div id='zdybSetShowTable' class='zdybSetArea'>显示表格</div></div></div>";
                                contentHtml += "<div style='border-bottom: 1px solid rgb(200,200,200);margin-top: 10px;float: left;width: 100%;'>" +
                                    "<div style='font-weight: bold;'>站点类型</div>" +
                                    "<div style='margin-left: 60px;height: 40px;'>" +
                                    "<select id='zdybSetStationType' style='width: 130px;'>";
                                for(var i=0;i<list.length;i++){
                                    contentHtml += "<option flag='"+list[i].type+"'>"+list[i].name+"</option>";
                                }
                                contentHtml +="</select></div></div>";
                                contentHtml +="<div style='height: 40px;margin-top: 15px;'><button class='zdybSetSave'>取消</button><button class='zdybSetSave'>保存</button></div>";
                                $("#ZDYBSet").html(contentHtml);
                                $(".zdybSetArea").click(function(){
                                    zdybSetElementChoice(this);
                                });
                                //保存取消
                                $(".zdybSetSave").click(function(){
                                    if($(this).html()=="保存"){
                                        zdybAddProductType();
                                    }
                                    else{
                                        initZDYBProductType();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    //产品选择
    function initZDYBPublishTime(){
        $.ajax({
            type: 'post',
            url: gridServiceUrl + "services/ForecastfineService/getZDYBPublishTime",
            data: {'para': '{"depart":"%%","areaCode":"%%"}'},
            dataType: 'json',
            error: function () {
                alert('获取制作时间出错!');
            },
            success: function (data) {
                t.elementData = data;
                for (var i = 0; i < data.length; i++) {
                    var contentProduct = '<div class="dis_menu_body_item" href="#" elementId="' + data[i].id + '">' + data[i].name + '<span title="删除" class="zdybProductTimeDelete">×</span></div>';
                    $("#zdybSetProductAdd"+data[i].type).before(contentProduct);
                }
                if(data.length!=0){
                    t.nowElement = data[0];
                    $($("#zdybPanelTop").find("div.dis_menu_body_item")[0]).css("background-color","rgb(158,195,255)");
                    initZDYBSetPage(t.nowElement);
                }

                $("#zdybPanelTop").find("div.dis_menu_body_item").click(function(){
                    if(t.addProduct){
                        alert("请完成添加");
                        return;
                    }
                    $("#zdybPanelTop").find("div.dis_menu_body_item").css("background-color","");
                    $(this).css("background-color","rgb(158,195,255)");
                    if($(this).hasClass("zdybAddForecastTimeBT")){
                        t.addProduct = true;
                        $(this).before('<div style="padding-left: 38px;"><input id="zdybAddProductTime" style="width: 180px;height: 25px;margin: 0px;" type="text"></div>');
                        $("#zdybAddProductTime")[0].focus();
                        $(this).css("display","none");
                        var obj = {id:-1,stationType:$(this).attr("stationType")};
                        initZDYBSetPage(obj);
                    }
                    else{
                        var id = $(this).attr("elementId");
                        for(var i=0;i< t.elementData.length;i++){
                            if(id == t.elementData[i].id){
                                t.nowElement = t.elementData[i];
                            }
                        }
                        initZDYBSetPage(t.nowElement);
                    }
                });

                //预报时次删除
                $(".zdybProductTimeDelete").click(function(){
                    var obj = this;
                    var result = confirm("是否删除");
                    if(result == false){
                        return;
                    }
                    zdybDeleteProductTime(obj);
                });
            }
        });
    }

    //设置页面右侧
    function initZDYBSetPage(obj){
        var hourSpanTotal = 168;
        var hourSpanInerval = 12;
        var elements = [];
        var groupList;
        if(obj.id!=-1){
            if(obj.ui!=""){
                groupList = obj.ui.split(";");
            }
            hourSpanTotal = parseInt(obj.hourSpanTotal);
            hourSpanInerval = parseInt(obj.hourSpan);
        }
        for(var groupKey in groupList){
            var rowList = groupList[groupKey].split(",");
            for (var rowKey in rowList){
                var row = rowList[rowKey].split(" ");
                elements.push({element:row[0],hourSpan:row[1],elementName:row[2]});
            }
        }
        var type = obj.stationType;

        var contentHtml = "";
        contentHtml += "<div class='ZDYBSetModular'>"+
            "<div style='border-bottom: 1px solid rgb(200,200,200);margin-top: 10px;'>" +
            "<div style='font-weight: bold;'>时间设置</div>" +
            "<div style='margin-left: 60px;'>";
        contentHtml +="<span >制作时次：</span><select id='zdybSetPublishTime' class='zdybSetPublishTime'>";
        for(var i=0;i<24;i++){
            if(obj.id!=-1&&parseInt(obj.publishTime)==i){
                contentHtml += "<option selected='true'>"+mosaicTime(i)+"</option>";
            }
            else{
                contentHtml += "<option>"+mosaicTime(i)+"</option>";
            }
        }
        contentHtml +="</select>";
        contentHtml +="<span style='margin-left: 24px;'>起报时次：</span><select id='zdybSetforecastTime' class='zdybSetPublishTime'>";
        for(var i=0;i<24;i++){
            if(obj.id!=-1&&(parseInt(obj.forecastTime)+8)==i){
                contentHtml += "<option selected='true'>"+mosaicTime(i)+"</option>";
            }
            else{
                contentHtml += "<option>"+mosaicTime(i)+"</option>";
            }
        }
        contentHtml +="</select>";
        contentHtml +="<span style='margin-left: 24px;'>发报时间：</span>" +
            "<select id='zdybSetMakeTime1' class='zdybSetPublishTime'>";
        for(var i=0;i<24;i++){
            if(obj.id!=-1&&parseInt(obj.makeTime.substr(0,2))==i){
                contentHtml += "<option selected='true'>"+mosaicTime(i)+"</option>";
            }
            else{
                contentHtml += "<option>"+mosaicTime(i)+"</option>";
            }
        }
        contentHtml +="</select>时" +
            "<select id='zdybSetMakeTime2' class='zdybSetPublishTime'>";
        for(var i=0;i<60;i++){
            if(obj.id!=-1&&parseInt(obj.makeTime.substr(3,2))==i){
                contentHtml += "<option selected='true'>"+mosaicTime(i)+"</option>";
            }
            else{
                contentHtml += "<option>"+mosaicTime(i)+"</option>";
            }
        }
        contentHtml +="</select>分</div></div>";
        contentHtml +="<div style='border-bottom: 1px solid rgb(200,200,200);margin-top: 10px;float: left;width: 100%;'>" +
            "<div style='font-weight: bold;'>部门设置</div>" +
            "<div id='zdybSetAreaDiv' style='margin-left: 60px;height: 40px;'><div id='zdybSetAreaQutai' style='float: left;width: 100%;height: 39px;'>";
        var departList = [];
        if(obj.hasOwnProperty("depart")){
            departList = obj.depart.split(",");
        }
        var display = "";
        if($.inArray("qutai", departList)!=-1){
            contentHtml += "<div class='zdybSetArea zdybSetActive'style='background-color: rgb(158, 195, 255);' flag='qutai'>区台</div>";
        }
        else{
            contentHtml += "<div class='zdybSetArea' flag='qutai'>区台</div>";
            display = "none";
        }
        contentHtml +="<div class='zdybSetAreaTime' style='display: "+display+"'><span style='margin-left: 24px;'>截止时间：</span>" +
            "<select class='zdybSetPublishTime'>";
        for(var i=0;i<24;i++){
            if(obj.id!=-1&&obj.endTime.toString().split(",")[0]!=-1&&parseInt(obj.endTime.toString().split(",")[0].substr(0,2))==i){
                contentHtml += "<option selected='true'>"+mosaicTime(i)+"</option>";
            }
            else{
                contentHtml += "<option>"+mosaicTime(i)+"</option>";
            }
        }
        contentHtml +="</select><span>时</span>" +
            "<select class='zdybSetPublishTime'>";
        for(var i=0;i<60;i++){
            if(obj.id!=-1&&obj.endTime.toString().split(",")[0]!=-1&&parseInt(obj.endTime.toString().split(",")[0].substr(3,2))==i){
                contentHtml += "<option selected='true'>"+mosaicTime(i)+"</option>";
            }
            else{
                contentHtml += "<option>"+mosaicTime(i)+"</option>";
            }
        }
        contentHtml +="</select><span>分</span>" +
            "<span style='margin-left: 24px;'>格点来源：</span><select class='zdybSetPublishTime' style='padding: 2px;'>";
        if(obj.id==-1){
            contentHtml += "<option selected='true' value='prvn'>区台</option>";
            contentHtml += "<option value='cty'>市台</option>";
        }
        else if(obj.gdybType.split(",")[0]!="cty"){
            contentHtml += "<option selected='true' value='prvn'>区台</option>";
            contentHtml += "<option value='cty'>市台</option>";
        }
        else{
            contentHtml += "<option value='prvn'>区台</option>";
            contentHtml += "<option selected='true' value='cty'>市台</option>";
        }
        contentHtml +="</select></div>";
        contentHtml += "</div><div id='zdybSetAreaShitai' style='float: left;width: 100%;height: 39px;'>";
        display = "";
        if($.inArray("shitai", departList)!=-1){
            contentHtml += "<div class='zdybSetArea zdybSetActive'style='background-color: rgb(158, 195, 255);' flag='shitai'>市台</div>";
        }
        else{
            contentHtml += "<div class='zdybSetArea' flag='shitai'>市台</div>";
            display = "none";
        }
        contentHtml +="<div class='zdybSetAreaTime' style='display: "+display+"'><span style='margin-left: 24px;'>截止时间：</span>" +
            "<select class='zdybSetPublishTime'>";
        for(var i=0;i<24;i++){
            if(obj.id!=-1&&obj.endTime.toString().split(",")[1]!=-1&&parseInt(obj.endTime.toString().split(",")[1].substr(0,2))==i){
                contentHtml += "<option selected='true'>"+mosaicTime(i)+"</option>";
            }
            else{
                contentHtml += "<option>"+mosaicTime(i)+"</option>";
            }
        }
        contentHtml +="</select><span>时</span>" +
            "<select class='zdybSetPublishTime'>";
        for(var i=0;i<60;i++){
            if(obj.id!=-1&&obj.endTime.toString().split(",")[1]!=-1&&parseInt(obj.endTime.toString().split(",")[1].substr(3,2))==i){
                contentHtml += "<option selected='true'>"+mosaicTime(i)+"</option>";
            }
            else{
                contentHtml += "<option>"+mosaicTime(i)+"</option>";
            }
        }
        contentHtml +="</select><span>分</span>" +
            "<span style='margin-left: 24px;'>格点来源：</span><select class='zdybSetPublishTime' style='padding: 2px;'>";
        if(obj.id==-1){
            contentHtml += "<option value='prvn'>区台</option>";
            contentHtml += "<option selected='true' value='cty'>市台</option>";
        }
        else if(obj.gdybType.split(",")[1]!="cty"){
            contentHtml += "<option selected='true' value='prvn'>区台</option>";
            contentHtml += "<option value='cty'>市台</option>";
            }
        else{
            contentHtml += "<option value='prvn'>区台</option>";
            contentHtml += "<option selected='true' value='cty'>市台</option>";
            }
        contentHtml +="</select></div>";
        contentHtml += "</div></div></div>";
        contentHtml +="<div style='border-bottom: 1px solid rgb(200,200,200);margin-top: 10px;float: left;'>" +
            "<div style='font-weight: bold;'>要素时效</div>" +
            "<div style='margin-left: 60px;'>" +
            "<span>总预报时效：</span><select id='zdybSetHourTotal' class='zdybSetPublishTime'>";
        for(var i=1;i<8;i++){
            if(parseInt(hourSpanTotal)==(i*24)){
                contentHtml +="<option selected='true'>"+i*24+"</option>";
            }
            else{
                contentHtml +="<option>"+i*24+"</option>";
            }
        }
        var timeList = [3,4,6,12,24];
        contentHtml +="</select><span>小时</span>" +
            "<span style='margin-left: 30px;'>时效间隔：</span><select id='zdybSetHourInerval' class='zdybSetPublishTime'>";
        for(var i=0;i<timeList.length;i++){
            if(parseInt(hourSpanInerval)==(timeList[i])){
                contentHtml +="<option selected='true'>"+timeList[i]+"</option>";
            }
            else{
                contentHtml +="<option>"+timeList[i]+"</option>";
            }
        }
        contentHtml +="</select><span>小时</span><span style='margin-left: 30px;'>添加要素：</span><div class='zdybAddElement' flag='false' title='添加预报要素'>＋</div>" +
            "<div id='zdybSetElementDiv'></div></div></div>";
        contentHtml +="<div style='margin-top: 10px;border-bottom: 1px solid rgb(200,200,200);float: left;width: 100%;'>" +
            "<div style='font-weight: bold;height: 24px;line-height: 24px;'>站点设置";
        contentHtml +="<button class='zdybSetStationAll' >全选</button></div>" +
            "<div id='zdybSetStationDiv' style='margin-left: 50px;float: left;margin-top: 10px;'></div></div>";

        contentHtml += "<div style='border-bottom: 1px solid rgb(200,200,200);margin-top: 10px;float: left;width: 100%;'>" +
            "<div style='font-weight: bold;'>输出类型</div>" +
            "<div id='zdybSetOutDiv' style='margin-left: 60px;height: 40px;'></div></div>" +
            "<div style='height: 40px;margin-top: 15px;'><button class='zdybSetSave'>取消</button><button class='zdybSetSave'>保存</button></div></div>";
        $("#ZDYBSet").html(contentHtml);

        //查询要素信息
        $.ajax({
            type: 'post',
            url: gridServiceUrl + "services/ForecastfineService/getZDYBElement",
            data: null,
            dataType: 'text',
            error: function () {
                alert('获取要素错误!');
            },
            success: function (data) {
                var elementObj = JSON.parse(data);
                getZDYBElements(elementObj,elements,hourSpanInerval,hourSpanTotal);
                $("#zdybSetHourInerval").change(function(){
                    getZDYBElements(elementObj,elements,parseInt($("#zdybSetHourInerval").val()),parseInt($("#zdybSetHourTotal").val()));
                });
                $("#zdybSetHourTotal").change(function(){
                    getZDYBElements(elementObj,elements,parseInt($("#zdybSetHourInerval").val()),parseInt($("#zdybSetHourTotal").val()));
                });
                //添加要素
                var addNum = 0;
                $(".zdybAddElement").click(function(){
                    addNum++;
                    var content = "";
                    content += "<div class='zdybSetElementRow'>" +
                        "<span class='zdybElementTitle' >要素：</span><select class='zdybSetElementSelect'>";
                    var list = $(".zdybElementName");
                    var elementList = [];
                    for(var i=0;i<list.length;i++){
                        elementList.push($(list[i]).html());
                    }
                    for(var i=0;i<elementObj.length;i++){
                        if($.inArray(elementObj[i].name, elementList)==-1){
                            content += "<option flag='"+elementObj[i].abbreviation+"'>"+elementObj[i].name+"</option>";
                        }
                    }

                    content +="</select><span class='zdybElementTitle' >时效：</span>";
                    for(var i=parseInt($("#zdybSetHourInerval").val());i<=parseInt($("#zdybSetHourTotal").val());){
                        content += "<div class='zdybSetElement zdybAddElement"+addNum+"'>"+i+"</div>";
                        i += parseInt($("#zdybSetHourInerval").val());
                    }
                    content += "<div class='zdybSetElementDelete zdybAddDelete"+addNum+"' title='删除要素'>×</div></div>";
                    $("#zdybSetElementDiv").append(content);
                    $(".zdybAddElement"+addNum).click(function(){
                        zdybSetElementChoice(this);
                    });
                    $(".zdybAddDelete"+addNum).click(function(){
                        var result = confirm("是否删除要素");
                        if(result == false){
                            return;
                        }
                        $(this).parent().remove();
                    });
                });
            }
        });
        //获取站点信息
        var param = '{"departCode":"'+$.cookie("departCode")+'%","type":'+type+'}';
        $.ajax({
            type: 'post',
            url: gridServiceUrl + "services/ForecastfineService/getUserStation",
            data: {'para': param},
            dataType: 'text',
            error: function () {
                alert('获取站点错误!');
            },
            success: function (data) {
                var stationList = JSON.parse(data);
                var contentHtml = "";
                var city;
                if(stationList.length!=0){
                    city = stationList[0].AreaName;
                    contentHtml += "<div class='zdybStationRow'><span class='zdybStationName' flag='false' areaCode='"+stationList[0].CityAreaCode+"'>"+city+"：</span>";
                }
                for(var j=0;j<stationList.length;j++){
                    if(stationList[j].AreaName!=city){
                        city = stationList[j].AreaName;
                        contentHtml += "</div><div class='zdybStationRow'><span class='zdybStationName' flag='false' areaCode='"+stationList[j].CityAreaCode+"'>"+city+"：</span>";
                    }
                    if(obj.hasOwnProperty("stationNums")&&obj.stationNums.indexOf(stationList[j].StationNum)!=-1){
                        contentHtml += "<div class='zdybSetStation zdybSetActive' style='background-color: rgb(158, 195, 255);' flag='"+stationList[j].StationNum+"'>"+stationList[j].StationName+"</div>";
                    }
                    else{
                        contentHtml += "<div class='zdybSetStation' flag='"+stationList[j].StationNum+"'>"+stationList[j].StationName+"</div>";
                    }
                }
                if(stationList.length!=0){
                    contentHtml += "</div>"
                }
                $("#zdybSetStationDiv").html(contentHtml);
                $(".zdybSetStation").click(function(){
                    zdybSetElementChoice(this);
                });
                //全选取消
                $(".zdybSetStationAll").click(function(){
                    if($(this).html()=="全选"){
                        $(this).html("取消");
                        $("#zdybSetStationDiv").find(".zdybSetStation").addClass("zdybSetActive");
                        $("#zdybSetStationDiv").find(".zdybSetStation").css("background-color","rgb(158, 195, 255)");
                        $(".zdybStationName").attr("flag","true");
                    }
                    else{
                        $(this).html("全选");
                        $("#zdybSetStationDiv").find(".zdybSetStation").removeClass("zdybSetActive");
                        $("#zdybSetStationDiv").find(".zdybSetStation").css("background-color","");
                        $(".zdybStationName").attr("flag","false");
                    }
                });
                //单个城市全选
                $(".zdybStationName").click(function(){
                    if($(this).attr("flag")=="false"){
                        $(this).parent().find(".zdybSetStation").addClass("zdybSetActive");
                        $(this).parent().find(".zdybSetStation").css("background-color","rgb(158, 195, 255)");
                        $(this).attr("flag","true");
                    }
                    else{
                        $(this).parent().find(".zdybSetStation").removeClass("zdybSetActive");
                        $(this).parent().find(".zdybSetStation").css("background-color","");
                        $(this).attr("flag","false");
                    }
                });
            }
        });
        //获取输出类型
        $.ajax({
            type: 'post',
            url: gridServiceUrl + "services/ForecastfineService/getZDYBOutType",
            data: null,
            dataType: 'text',
            error: function () {
                alert('获取要素错误!');
            },
            success: function (data) {
                var outTypeList = JSON.parse(data);
                var nowOutType = [];
                var contentHtml = "";
                if(obj.hasOwnProperty("outType")){
                    nowOutType = obj.outType.split(",");
                }
                for(var i=0;i<outTypeList.length;i++){
                    if($.inArray(outTypeList[i].type, nowOutType)!=-1){
                        contentHtml += "<div class='zdybSetOutType zdybSetActive'style='background-color: rgb(158, 195, 255);' flag='"+outTypeList[i].type+"'>"+outTypeList[i].name+"</div>";
                    }
                    else{
                        contentHtml += "<div class='zdybSetOutType' flag='"+outTypeList[i].type+"'>"+outTypeList[i].name+"</div>";
                    }
                }
                $("#zdybSetOutDiv").html(contentHtml);
                $(".zdybSetOutType").click(function(){
                    zdybSetElementChoice(this);
                });
            }
        });
        //获取格点预报时次
        $.ajax({
            type: 'post',
            url: gridServiceUrl + "services/ForecastfineService/getGDYBPublishTime",
            data: null,
            dataType: 'text',
            error: function () {
                alert('获取要素错误!');
            },
            success: function (data) {
                var list = JSON.parse(data);
                var contentHtml = "<span style='margin-left: 24px;'>格点预报时次：</span><select class='zdybSetPublishTime'>";
                for(var i=0;i<list.length;i++){
                    if(obj.id!=-1&&obj.gdybPublishTime.split(",")[0]==list[i].publishTime){
                        contentHtml += "<option selected='true'>"+mosaicTime(list[i].publishTime)+"</option>";
                    }
                    else{
                        contentHtml += "<option>"+mosaicTime(list[i].publishTime)+"</option>";
                    }
                }
                contentHtml += "</select>";
                $("#zdybSetAreaQutai").find(".zdybSetAreaTime").append(contentHtml);
                var contentHtml = "<span style='margin-left: 24px;'>格点预报时次：</span><select class='zdybSetPublishTime' >";
                for(var i=0;i<list.length;i++){
                    if(obj.id!=-1&&obj.gdybPublishTime.split(",")[1]==list[i].publishTime){
                        contentHtml += "<option selected='true'>"+mosaicTime(list[i].publishTime)+"</option>";
                    }
                    else{
                        contentHtml += "<option>"+mosaicTime(list[i].publishTime)+"</option>";
                    }
                }
                contentHtml += "</select>";
                $("#zdybSetAreaShitai").find(".zdybSetAreaTime").append(contentHtml);

                $("#zdybSetAreaDiv").find(".zdybSetArea").click(function(){
                    if($(this).hasClass("zdybSetActive")){
                        $(this).removeClass("zdybSetActive");
                        $(this).css("background-color","");
                        $(this).parent().find(".zdybSetAreaTime").css("display","none");
                    }
                    else{
                        $(this).addClass("zdybSetActive");
                        $(this).css("background-color","rgb(158, 195, 255)");
                        $(this).parent().find(".zdybSetAreaTime").css("display","");
                    }
                });
            }
        });

        //保存取消
        $(".zdybSetSave").click(function(){
            if($(this).html()=="保存"){
                zdybSetSave(obj);
            }
            else{
                if(obj.id!=-1){
                    initZDYBSetPage(obj);
                }
                else{
                    initZDYBProductType();
                    t.addProduct = false;
                }
            }
        });
    }

    function getZDYBElements(elementObj,elements,hourSpanInerval,hourSpanTotal){
        var elementNameObj = {};
        var contentHtml = "";
        for (var i = 0; i < elementObj.length; i++) {
            elementNameObj[elementObj[i].abbreviation] = elementObj[i].name;
        }
        var elementList = {};
        for(var i=0;i< elements.length;i++){
            if(!elementList.hasOwnProperty(elements[i].element)){
                elementList[elements[i].element] = new Array();
            }
            elementList[elements[i].element].push(elements[i].hourSpan);
        }
        for(var element in elementList){
            contentHtml += "<div class='zdybSetElementRow'><span class='zdybElementTitle' >要素：</span><span class='zdybElementName' flag='"+element+"'>"+elementNameObj[element]+"</span><span class='zdybElementTitle' >时效：</span>";
            for(var i=hourSpanInerval;i<=hourSpanTotal;){
                if($.inArray(i.toString(), elementList[element])!=-1){
                    contentHtml += "<div class='zdybSetElement zdybSetActive'style='background-color: rgb(158, 195, 255);'>"+i+"</div>";
                }
                else{
                    contentHtml += "<div class='zdybSetElement'>"+i+"</div>";
                }
                i += hourSpanInerval;
            }
            contentHtml += "<div class='zdybSetElementDelete' title='删除要素'>×</div></div>";
        }
        $("#zdybSetElementDiv").html(contentHtml);
        $(".zdybSetElement").click(function(){
            zdybSetElementChoice(this);
        });

        $(".zdybSetElementDelete").click(function(){
            var result = confirm("是否删除要素");
            if(result == false){
                return;
            }
            $(this).parent().remove();
        });
    }

    //站点预报设置保存
    function zdybSetSave(obj){
        //判断是否有重复要素
        var elelist = $("#zdybSetElementDiv").find("select");
        var eleNameList = [];
        for(var i=0;i<elelist.length;i++){
            eleNameList.push($(elelist[i]).val());
        }
        var s = eleNameList.join(",")+",";
        for(var i=0;i<eleNameList.length;i++) {
            if(s.replace(eleNameList[i]+",","").indexOf(eleNameList[i]+",")>-1) {
                alert("有重复元素：" + eleNameList[i]);
                return;
            }
        }
        if(obj.id!=-1){
            var gdybPublishTime = "";
            var endTime = "";
            var gdybType = "";
            var list = $("#zdybSetAreaDiv").find(".zdybSetArea");
            for(var i=0;i<list.length;i++){
                if($(list[i]).hasClass("zdybSetActive")){
                    gdybPublishTime += parseInt($(list[i]).parent().find("select")[3].value).toString()+",";
                    endTime += $(list[i]).parent().find("select")[0].value+":"+$(list[i]).parent().find("select")[1].value+":00,";
                }
                else{
                    gdybPublishTime += "-1,";
                    endTime += "-1,";
                }
                gdybType += $($(list[i]).parent().find("select")[2]).find("option:selected").attr("value")+",";
            }
            gdybPublishTime = gdybPublishTime.substr(0,gdybPublishTime.length-1);
            endTime = endTime.substr(0,endTime.length-1);
            gdybType = gdybType.substr(0,gdybType.length-1);
            var publishTime = parseInt($("#zdybSetPublishTime").val())
            var forecastTime = mosaicTime((parseInt($("#zdybSetforecastTime").val())+24-8)%24);
            var makeTime = $("#zdybSetMakeTime1").val()+":"+$("#zdybSetMakeTime2").val()+":00";
            var stations = $("#zdybSetStationDiv").find(".zdybSetActive");
            var stationNums = "";
            var areaCodeList = [];
            for(var i= 0;i<stations.length;i++){
                stationNums += $(stations[i]).attr("flag")+",";
                var areaCode = $(stations[i]).parent().find(".zdybStationName").attr("areaCode");
                if($.inArray(areaCode, areaCodeList)==-1){
                    areaCodeList.push(areaCode);
                }
            }
            if(stationNums.length!=0){
                stationNums = stationNums.substr(0,stationNums.length-1);
            }
            var areaCodeStr = "45,";
            for(var i=0;i<areaCodeList.length;i++){
                areaCodeStr += areaCodeList[i]+",";
            }
            var depart = "";
            var list = $("#zdybSetAreaDiv").find(".zdybSetActive");
            for(var i=0;i<list.length;i++){
                if(i!=0){
                    depart += ","+$(list[i]).attr("flag");
                }
                else{
                    depart += $(list[i]).attr("flag");
                }
            }
            var outType = "";
            var list = $("#zdybSetOutDiv").find(".zdybSetActive");
            for(var i=0;i<list.length;i++){
                if(i!=0){
                    outType += ","+$(list[i]).attr("flag");
                }
                else{
                    outType += $(list[i]).attr("flag");
                }
            }
            var ui = "";
            var hourSpan = parseInt($("#zdybSetHourInerval").val());
            var hourSpanTotal = parseInt($("#zdybSetHourTotal").val());
            var totalNum = hourSpanTotal/24;
            var timeNum = 24/hourSpan;
            var start = 0;
            var list = $(".zdybSetElementRow");
            for(var i=0;i<totalNum;i++){
                for(var j=0;j<list.length;j++){
                    var timeList = $(list[j]).find(".zdybSetElement");
                    var nowTimeList = timeList.slice(start,start+timeNum);
                    var activeTimeList = [];
                    for(var k=0;k<nowTimeList.length;k++){
                        if($(nowTimeList[k]).hasClass("zdybSetActive")){
                            activeTimeList.push(nowTimeList[k]);
                        }
                    }
                    if(activeTimeList.length==1&&($(activeTimeList[0]).html()%24==0)){
                        if($(list[j]).find(".zdybElementName").length!=0){
                            ui += $(list[j]).find(".zdybElementName").attr("flag")+" "+$(activeTimeList[0]).html()+" "+$(list[j]).find(".zdybElementName").html()+",";
                        }
                        else{
                            ui += $(list[j]).find("option:selected").attr("flag")+" "+$(activeTimeList[0]).html()+" "+$(list[j]).find("select").val()+",";
                        }
                    }
                    else{
                        for(var m=0;m<activeTimeList.length;m++){
                            var timeName = (parseInt($(activeTimeList[m]).html())%24)==0?24:(parseInt($(activeTimeList[m]).html())%24);
                            if($(list[j]).find(".zdybElementName").length!=0){
                                ui += $(list[j]).find(".zdybElementName").attr("flag")+" "+$(activeTimeList[m]).html()+" "+timeName+"小时"+$(list[j]).find(".zdybElementName").html()+",";
                            }
                            else{
                                ui += $(list[j]).find("option:selected").attr("flag")+" "+$(activeTimeList[m]).html()+" "+timeName+"小时"+$(list[j]).find("select").val()+",";
                            }
                        }
                    }
                }
                ui = ui.substr(0,ui.length-1);
                ui += ";";
                start += timeNum;
            }
            if(ui!=""){
                ui = ui.substr(0,ui.length-1);
            }

            var param = '{"id":'+obj.id+',"publishTime":"'+publishTime+'","gdybPublishTime":"'+gdybPublishTime+'","makeTime":"'+makeTime+'","forecastTime":"'+forecastTime+'","gdybType":"'+gdybType+'","endTime":"'+endTime+'","stationNums":"'+stationNums+'","depart":"'+depart+'","areaCodes":"'+areaCodeStr+'","outType":"'+outType+'","hourSpan":"'+obj.hourSpan+'","hourSpanTotal":"'+obj.hourSpanTotal+'","ui":"'+ui+'"}';
            $.ajax({
                type: 'post',
                url: gridServiceUrl + "services/ForecastfineService/updateZDYBSet",
                data: {'para': param},
                dataType: 'text',
                error: function () {
                    alert('保存失败!');
                },
                success: function (data) {
                    alert("保存成功");
                    initZDYBProductType();
                }
            });
        }
        else{
            if($("#zdybAddProductTime").val()==""){
                alert("请输入预报名称");
                return;
            }
            var gdybPublishTime = "";
            var endTime = "";
            var gdybType = "";
            var list = $("#zdybSetAreaDiv").find(".zdybSetArea");
            for(var i=0;i<list.length;i++){
                if($(list[i]).hasClass("zdybSetActive")){
                    gdybPublishTime += parseInt($(list[i]).parent().find("select")[3].value).toString()+",";
                    endTime += $(list[i]).parent().find("select")[0].value+":"+$(list[i]).parent().find("select")[1].value+":00,";
                }
                else{
                    gdybPublishTime += "-1,";
                    endTime += "-1,";
                }
                gdybType += $($(list[i]).parent().find("select")[2]).find("option:selected").attr("value")+",";
            }
            gdybPublishTime = gdybPublishTime.substr(0,gdybPublishTime.length-1);
            endTime = endTime.substr(0,endTime.length-1);
            gdybType = gdybType.substr(0,gdybType.length-1);
            var publishTime = parseInt($("#zdybSetPublishTime").val())
            var makeTime = $("#zdybSetMakeTime1").val()+":"+$("#zdybSetMakeTime2").val()+":00";
            var forecastTime = mosaicTime((parseInt($("#zdybSetforecastTime").val())+24-8)%24);
            var stations = $("#zdybSetStationDiv").find(".zdybSetActive");
            var stationNums = "";
            var areaCodeList = [];
            var name = $("#zdybAddProductTime").val();
            var depart = "";
            var list = $("#zdybSetAreaDiv").find(".zdybSetActive");
            for(var i=0;i<list.length;i++){
                if(i!=0){
                    depart += ","+$(list[i]).attr("flag");
                }
                else{
                    depart += $(list[i]).attr("flag");
                }

            }
            var type = $("#zdybAddProductTime").parent().next().attr("flagId");
            var outType = "";
            var list = $("#zdybSetOutDiv").find(".zdybSetActive");
            for(var i=0;i<list.length;i++){
                if(i!=0){
                    outType += ","+$(list[i]).attr("flag");
                }
                else{
                    outType += $(list[i]).attr("flag");
                }
            }
            var context = "";
            var hourSpan = $("#zdybSetHourInerval").val();
            var hourSpanTotal = $("#zdybSetHourTotal").val();
            var zdybHour = "0";
            for(var i= 0;i<stations.length;i++){
                stationNums += $(stations[i]).attr("flag")+",";
                var areaCode = $(stations[i]).parent().find(".zdybStationName").attr("areaCode");
                if($.inArray(areaCode, areaCodeList)==-1){
                    areaCodeList.push(areaCode);
                }
            }
            if(stationNums.length!=0){
                stationNums = stationNums.substr(0,stationNums.length-1);
            }
            var areaCodeStr = "45,";
            for(var i=0;i<areaCodeList.length;i++){
                areaCodeStr += areaCodeList[i]+",";
            }
            var ui = "";
            var hourSpan = parseInt($("#zdybSetHourInerval").val());
            var hourSpanTotal = parseInt($("#zdybSetHourTotal").val());
            var totalNum = hourSpanTotal/24;
            var timeNum = 24/hourSpan;
            var start = 0;
            var list = $(".zdybSetElementRow");
            for(var i=0;i<totalNum;i++){
                for(var j=0;j<list.length;j++){
                    var timeList = $(list[j]).find(".zdybSetElement");
                    var nowTimeList = timeList.slice(start,start+timeNum);
                    var activeTimeList = [];
                    for(var k=0;k<nowTimeList.length;k++){
                        if($(nowTimeList[k]).hasClass("zdybSetActive")){
                            activeTimeList.push(nowTimeList[k]);
                        }
                    }
                    if(activeTimeList.length==1&&($(activeTimeList[0]).html()%24==0)){
                        if($(list[j]).find(".zdybElementName").length!=0){
                            ui += $(list[j]).find(".zdybElementName").attr("flag")+" "+$(activeTimeList[0]).html()+" "+$(list[j]).find(".zdybElementName").html()+",";
                        }
                        else{
                            ui += $(list[j]).find("option:selected").attr("flag")+" "+$(activeTimeList[0]).html()+" "+$(list[j]).find("select").val()+",";
                        }
                    }
                    else{
                        for(var m=0;m<activeTimeList.length;m++){
                            var timeName = (parseInt($(activeTimeList[m]).html())%24)==0?24:(parseInt($(activeTimeList[m]).html())%24);
                            if($(list[j]).find(".zdybElementName").length!=0){
                                ui += $(list[j]).find(".zdybElementName").attr("flag")+" "+$(activeTimeList[m]).html()+" "+timeName+"小时"+$(list[j]).find(".zdybElementName").html()+",";
                            }
                            else{
                                ui += $(list[j]).find("option:selected").attr("flag")+" "+$(activeTimeList[m]).html()+" "+timeName+"小时"+$(list[j]).find("select").val()+",";
                            }
                        }
                    }
                }
                ui = ui.substr(0,ui.length-1);
                ui += ";";
                start += timeNum;
            }
            if(ui!=""){
                ui = ui.substr(0,ui.length-1);
            }
            var param = '{"publishTime":'+publishTime+',"gdybPublishTime":"'+gdybPublishTime+'","makeTime":"'+makeTime+'","forecastTime":"'+forecastTime+'","gdybType":"'+gdybType+'","endTime":"'+endTime+'","stationNums":"'+stationNums+'","name":"'+name+'","depart":"'+depart+'","areaCodes":"'+areaCodeStr+'","type":"'+type+'","outType":"'+outType+'","context":"'+context+'","hourSpan":"'+hourSpan+'","hourSpanTotal":"'+hourSpanTotal+'","zdybHour":"'+zdybHour+'","ui":"'+ui+'"}';
            $.ajax({
                type: 'post',
                url: gridServiceUrl + "services/ForecastfineService/insertZDYBSet",
                data: {'para': param},
                dataType: 'text',
                error: function () {
                    alert('保存失败!');
                },
                success: function (data) {
                    alert("添加成功");
                    t.addProduct = false;
                    initZDYBProductType();
                }
            });
        }
    }

    //时效，站点选择取消事件
    function zdybSetElementChoice(obj){
        if($(obj).hasClass("zdybSetActive")){
            $(obj).removeClass("zdybSetActive");
            $(obj).css("background-color","");
        }
        else{
            $(obj).addClass("zdybSetActive");
            $(obj).css("background-color","rgb(158, 195, 255)");
        }
    }

    function mosaicTime(time){
        if(time.toString().length == 1){
            time = "0"+time;
        }
        return time;
    }

    function zdybDeleteProductTime(obj){
        $.ajax({
            type: 'post',
            url: gridServiceUrl + "services/ForecastfineService/deleteProductTime",
            data: {'para': '{"id":'+parseInt($(obj).parent().attr("elementId"))+'}'},
            dataType: 'text',
            error: function () {
                alert('删除失败!');
            },
            success: function (data) {
                alert("删除成功");
                initZDYBProductType();
            }
        });
    }

    function zdybAddProductType(){
        if($("#zdybAddProductType").val()==""){
            alert("请输入名称");
            return;
        }
        var name = $("#zdybAddProductType").val();
        var stationType = $("#zdybSetStationType").find("option:selected").attr("flag");
        var showTable = $("#zdybSetShowTable").hasClass("zdybSetActive")?1:0;
        $.ajax({
            type: 'post',
            url: gridServiceUrl + "services/ForecastfineService/addProductType",
            data: {'para': '{"name":"'+name+'","stationType":"'+stationType+'","showTable":'+showTable+'}'},
            dataType: 'text',
            error: function () {
                alert('添加失败!');
            },
            success: function (data) {
                alert("添加成功");
                t.addProduct = false;
                initZDYBProductType();
            }
        });
    }
}


ZDYBSZPageClass.prototype = new PageBase();
