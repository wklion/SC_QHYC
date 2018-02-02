/**
 * Created by wangkun on 2016/9/14.
 */
function alertFuc(content){
    $("#map_alert_div").html(content);

    if($("#map_alert_div").css("display") == "none") {
        $("#map_alert_div").css("display", "block");

        var int = self.setInterval(moveAlert, 100);
        var tickcount = 0;
        function moveAlert() {
            tickcount++;
            var left = 45 + tickcount;
            $("#map_alert_div").css("left", left + "%");
            if (left >= 55) {
                clearInterval(int);
                $("#map_alert_div").css("display", "none");
            }
        }
    }
}
/*Array扩展 start*/
function ArrayIsContain(srcA,targetA){
    var i = srcA.length;
    while (i--) {
        if (srcA[i] === targetA) {
            return true;
        }
    }
    return false;
}
//下载站点
function DownStation(recall){
    $.ajax({
        type: 'post',
        url: Url_Config.gridServiceUrl + "services/ForecastfineService/getStation",
        data: {'para': ''},
        dataType: 'json',
        error: function () {
            alert('获取站点出错!');
        },
        success: function (data) {
            recall&&recall(data);
        }
    });
}
function ArrayMax(arr){
    if(arr==undefined){
        return;
    }
    var size=arr.length;
    if(size==0){
        return;
    }
    var max=arr[0];
    for(var i=1;i<size;i++){
        if (arr[i] > max) { 
            max = arr[i]; 
        } 
    }
    return max;
}
function ArrayMin(arr){
    if(arr==undefined){
        return;
    }
    var size=arr.length;
    if(size==0){
        return;
    }
    var min=arr[0];
    for(var i=1;i<size;i++){
        if (arr[i] < min) { 
            min = arr[i]; 
        } 
    }
    return min;
}
//显示进度动画
function ShowProgress(txt){
    $("#div_progress").css("display", "block");
    $("#div_progress_title").html(txt);
}
//隐藏进度动画
function HideProgress(txt){
    $("#div_progress").css("display", "block");
    $("#div_progress_title").html(txt);
    $("#div_progress").fadeOut(2000,function(){});
}
function AJAX(url,data,errortxt,recall,ft){
    $.ajax({
        type: 'post',
        url: url,
        async: ft,
        data: {'para':data},
        dataType: 'json',
        error: function () {
            HideProgress(errortxt);
            $("#msg textarea").append(errortxt+"&#13;&#10;");
        },
        success: function(data){
            if(data==undefined){
                HideProgress("获取数据为空!");
                $("#msg textarea").append("获取数据为空!"+"&#13;&#10;");
                return;
            }
            recall(data);
        }
    });
}
function AJAXSync(url,data){
    var result = "";
    $.ajax({
        type: 'post',
        url: url,
        async: false,
        data: {'para':data},
        dataType: 'json',
        error: function () {
            result = "err";
        },
        success: function(data){
            result = data;
        }
    });
    return result;
}
//根据日期创建label
function CreateLabelByDate(startDate,endDate){
    if(typeof(startDate) == "string"){
        var startDates = startDate.split("-");
        startDate = new Date(startDates[0],startDates[1],startDates[2]);
    }
    if(typeof(endDate) == "string"){
        var endDates = endDate.split("-");
        endDate = new Date(endDates[0],endDates[1],endDates[2]);
    }
    var currentDate = startDate.clone(true);
    var labels=[];
    while(currentDate<endDate)
    {
        var strmmdd=currentDate.format("MM-dd");
        labels.push(strmmdd);
        currentDate.addDays(1);
    }
    var strendmmdd=endDate.format("MM-dd");
    labels.push(strendmmdd);
    return labels;
}
/**
 * @author:杠上花
 * @date:2017-1-31
 * @modifydate:
 * @param:
 * @return:
 * @description:格点数据转换
 */
function convertGridToDatasetGrid(elementid, data){
    var bWind = false;
    if (elementid === "10uv") {
        bWind = true;
    }
    var rows = data.rows;
    var cols = data.cols;
    var dvalues = null;
    if (data.dvalues == undefined) {
        dvalues = data.grid;
    }
    else {
        dvalues = data.dvalues;
    }
    var hasTag = (!bWind) && (dvalues.length == rows * cols * 2);
    var dimensions = (bWind || hasTag) ? 2 : 1; //维度，风场有两维；带有Tag属性也是两维
    var dMin = 9999;
    var dMax = -9999;
    var datasetGrid = new WeatherMap.DatasetGrid(data.left, data.top, data.right, data.bottom, rows, cols, bWind ? 2 : 1); //只有风是两要素
    datasetGrid.noDataValue = data.noDataValue;
    datasetGrid.nwpmodelTime = data.nwpmodelTime;
    var grid = [];
    var tag = [];
    for (var i = 0; i < rows; i++) {
        var tagLine = [];
        var nIndexLine = cols * i * dimensions;
        for (var j = 0; j < cols; j++) {
            var nIndex = nIndexLine + j * dimensions;
            var z;
            if (bWind) {
                z = dvalues[nIndex + 1];
                grid.push(Math.round(dvalues[nIndex + 1])); //风速在前
                grid.push(Math.round(dvalues[nIndex]));   //风向在后
            }
            else {
                z = dvalues[nIndex];
                grid.push(z);
                if (hasTag)
                    tagLine.push(dvalues[nIndex + 1]);
            }
            if (z != 9999 && z != -9999) {
                if (z < dMin)
                    dMin = z;
                if (z > dMax)
                    dMax = z;
            }
            if (hasTag)
                tag.push(tagLine);
        }
    }
    datasetGrid.grid = grid;
    datasetGrid.dMin = dMin;
    datasetGrid.dMax = dMax;
    if (hasTag) {
        datasetGrid.tag = tag;
        datasetGrid.defaultTag = 0;
    }
    return datasetGrid;
}
