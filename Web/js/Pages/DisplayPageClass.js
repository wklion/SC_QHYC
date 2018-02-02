/**
 * Created by zouwei on 2016/3/4.
 */
function DisplayPageClass(){
    this.myDateSelecter = null;
    this.yubaoshixiaoTools = null;
    this.currentPosition = {lon:104,lat:30.6}; //成都
    var t = this;
    this.renderMenu = function(){
        //初始化布局
        var width = $("#content_div").css("width");
        var height = $("#content_div").css("height");
        var widthChart = $("#latticeForcast").css("width");
        var heightTable = $("#latticePrecipitation").css("height");
        var heightMapContainer = $("#mapContainer_div").css("height");
        var heightTopDiv = parseInt(height)-parseInt(heightTable) - 30;
        $("#top_div").css("height",heightTopDiv+"px");
        $("#bottom_div").css("height",(parseInt(heightTable))+"px");
        $("#mapContainer_div").css("width",(parseInt(width)-parseInt(widthChart) - 10)+"px");
        $("#divHourSpan").css("width",(parseInt(width)-parseInt(widthChart) - 10)+"px");

        t.myDateSelecter = new DateSelecter(2, 2); //最小视图为天
        t.myDateSelecter.intervalMinutes = 60*24; //24小时
        $("#dateSelect").append(this.myDateSelecter.div);

        t.yubaoshixiaoTools = new YuBaoshixiaoTools($("#divHourSpan"), t.myDateSelecter.getCurrentTimeReal(), 1);
        t.yubaoshixiaoTools.hourSpan = t.yubaoshixiaoTools.numbers[0];
        regesterYuBaoShiXiaoEvent(); //由于createDom重构了页面，需要重新注册事件，否则无法响应事件
        $("#divHourSpan td:first").addClass("active");
        var heightMapTool = parseInt($("#divTool").css("height")) + parseInt($("#divHourSpan").css("height"))+2;
        $("#mapTool_div").css("height", heightMapTool);
        $("#map_div").css("height", (heightTopDiv-parseInt(heightMapTool))+"px");
        GDYB.Page.curPage.map.updateSize();

        //初始化参数
        initProductType();
        GDYB.GridProductClass.isBrowseMode = true;

        //初始化制作时间和预报时间
        var dateNow = new Date();
        if(GDYB.GridProductClass.currentMakeTime == null) {
            dateNow.setMinutes(0);
            dateNow.setSeconds(0);
        }
        else{
            var curTimeStr = GDYB.GridProductClass.currentMakeTime;
            var year = parseInt(curTimeStr.replace(/(\d*)-\d*-\d* \d*:\d*:\d*/,"$1"));
            var month = parseInt(curTimeStr.replace(/\d*-(\d*)-\d* \d*:\d*:\d*/,"$1"));
            var day = parseInt(curTimeStr.replace(/\d*-\d*-(\d*) \d*:\d*:\d*/,"$1"));
            var hour = parseInt(curTimeStr.replace(/\d*-\d*-\d* (\d*):\d*:\d*/,"$1"));
            var minutes = 0;
            var seconds = 0;
            dateNow.setFullYear(year,month - 1,day);
            dateNow.setHours(hour, minutes, seconds, 0);
        }
        var makeTimeHour = "00";
        setForecastTime(dateNow, makeTimeHour);

        //改变制作时间
        this.myDateSelecter.input.change(function(){
            var datetime = t.myDateSelecter.getCurrentTimeReal();
            var makeTimeHour = $("#selectMakeTime").val();
            setForecastTime(datetime, makeTimeHour);
            onChangeDateTime();
        });

        //点击上翻
        t.myDateSelecter.leftBtn.click(function(){
            onChangeDateTime();
        });

        //点击下翻
        t.myDateSelecter.rightBtn.click(function(){
            onChangeDateTime();
        });

        //改变制作时次
        $("#selectMakeTime").change(function() {
            var datetime = t.myDateSelecter.getCurrentTimeReal();
            var makeTimeHour = $("#selectMakeTime").val();
            setForecastTime(datetime, makeTimeHour);
            onChangeDateTime();
        });

        //改变要素
        $("#selectElement").change(function() {
            var element = $("#selectElement").val();
            t.yubaoshixiaoTools.numbers = GDYB.GDYBPage.getHourSpan(element);
            t.yubaoshixiaoTools.createDom(t.myDateSelecter.getCurrentTimeReal());
            t.yubaoshixiaoTools.hourSpan = t.yubaoshixiaoTools.numbers[0];
            $("#divHourSpan td:first").addClass("active");
            regesterYuBaoShiXiaoEvent(); //由于createDom重构了页面，需要重新注册事件，否则无法响应事件
            var heightMapTool = parseInt($("#divTool").css("height")) + parseInt($("#divHourSpan").css("height"))+2;
            $("#mapTool_div").css("height", heightMapTool);
            $("#map_div").css("height", (heightTopDiv-parseInt(heightMapTool))+"px");
            GDYB.Page.curPage.map.updateSize();

            t.displayGridProduct();
        });
        //长时效更改
        $("#selectTimes").change(function() {
            onChangeDateTime();
        });

        //注册时效点击事件
        function regesterYuBaoShiXiaoEvent(){
            $("#divHourSpan").find("td").click(function () {
                if(typeof(this.id) != "undefined" && this.id != "")
                    t.displayGridProduct();
            });
        };

        function initProductType(){
            var strProductType = "prvn_p";
            var arrayProductType = strProductType.split('_');
            GDYB.GridProductClass.currentType = arrayProductType[0];
            GDYB.GridProductClass.currentVersion = arrayProductType[1];
        }

        function onChangeDateTime(){
            t.displayGridProduct();
            t.displayGridValueSerial(); //时间改变，同时更新图表c
            t.displayGridValueSerialJuPing(); //时间改变，同时更新图表
        }

        //根据制作时间，设置预报时间
        function setForecastTime(datetime, makeTimeHour){
            if(typeof(datetime) == "undefined")
                datetime = t.myDateSelecter.getCurrentTimeReal();
            if(typeof(makeTimeHour) == "undefined")
                makeTimeHour = $("#selectMakeTime").val();
            if(GDYB.GridProductClass.currentType == "prvn"){
                datetime.setHours(0);
            }
            else if(GDYB.GridProductClass.currentType == "cty" || GDYB.GridProductClass.currentType == "cnty"){
                if(makeTimeHour == 5 || makeTimeHour == 10)
                    datetime.setHours(0);
                else
                    datetime.setHours(20);
            }
            t.myDateSelecter.setCurrentTime(datetime.format("yyyy-MM-dd hh:mm:ss"));

            datetime.setHours(makeTimeHour);
            GDYB.GridProductClass.currentMakeTime = datetime.format("yyyy-MM-dd hh:mm:ss");
            GDYB.GridProductClass.currentDateTime = t.myDateSelecter.getCurrentTime(false);
        }

        //鼠标点击事件
        var map= t.map;
        map.events.register("click", map, function(event){
            t.currentPosition = this.getLonLatFromPixel(event.xy);
            if(GDYB.GridProductClass.layerMarkers == null){
                GDYB.GridProductClass.layerMarkers = new WeatherMap.Layer.Markers("layerMarkers");
                GDYB.Page.curPage.map.addLayers([GDYB.GridProductClass.layerMarkers]);
            }
            GDYB.GridProductClass.layerMarkers.clearMarkers();
            var size = new WeatherMap.Size(25,30);
            var offset = new WeatherMap.Pixel(-(size.w/2), -size.h);
            var icon = new WeatherMap.Icon('imgs/marker.png', size, offset);
            GDYB.GridProductClass.layerMarkers.addMarker(new WeatherMap.Marker(new WeatherMap.LonLat(t.currentPosition.lon,t.currentPosition.lat),icon));

            t.displayGridLocation();
            t.displayGridValueSerial();
            t.displayGridValueSerialJuPing();
            //t.displayWeatherDescription(); //天气概况
        });

        //键盘按键事件，实现上翻、下翻
        $(document).keydown(function (event) {
            if(document.activeElement.id == "table_yubaoshixiao"){  //时效上下翻
                var offset = 0;
                if(event.keyCode == 37 || event.keyCode == 38)  //左上
                    offset = -1;
                else if(event.keyCode == 39 || event.keyCode == 40) //右下
                    offset = 1;

                if(offset != 0){
                    var hourspans = t.yubaoshixiaoTools.numbers;
                    var hourSpan = t.yubaoshixiaoTools.hourSpan;
                    var nIndex = -1;
                    for(var hKey in hourspans){
                        if(hourspans[hKey] == hourSpan){
                            nIndex = Number(hKey);
                            break;
                        }
                    }
                    nIndex += offset
                    if(nIndex >= hourspans.length)
                        nIndex = 0;
                    else if(nIndex < 0)
                        nIndex = hourspans.length - 1;
                    hourSpan = hourspans[nIndex];
                    $("#table_yubaoshixiao").find("td").removeClass("active");
                    t.yubaoshixiaoTools.hourSpan = hourSpan;
                    $("#table_yubaoshixiao").find("#"+hourSpan+"h").addClass("active");
                    t.displayGridProduct();
                }
            }
        });

        initChartTable(); //初始化图表
        t.displayGridValueSerial(); //更新图表
        t.displayGridValueSerialJuPing(); //更新图表
        this.displayGridLocation(); //显示定位信息
        /*setTimeout(function(){
            t.displayGridProduct(t);
        }, 1000); //显示格点产品*/
        setTimeout(function(){
            GDYB.GridProductClass.getLastGridInfo(t.displayLastTime,"prvn");
        }, 1000); //显示格点产品
        //t.displayWeatherDescription(); //天气概况
        //setTimeout(t.BaiHua,1000);//白化
        new MapTool("map");
    };
    this.BaiHua=function(){
        var testLayer = GDYB.Page.curPage.map.getLayer("mapCoverLayer");
        //testLayer.removeFeatures([GDYB.GDYBPage.lineVector]);
        testLayer.addFeatures([GDYB.GDYBPage.polygonVector]);
        //testLayer.addFeatures([GDYB.GDYBPage.lineVector]);
    };
    this.displayLastTime=function(datetime, elements, hourspans, levels, datetimeSerial){//显示最新时间
        if(datetime != null && datetime != "null" && datetime != "")
        {
            t.myDateSelecter.setCurrentTime(datetime);
            t.myDateSelecter.setDatetimeSerial(datetimeSerial);
            t.displayGridProduct();
            t.displayGridValueSerial(); //更新图表
            t.displayGridValueSerialJuPing(); //更新图表
            t.displayGridLocation(); //显示定位信息
        }
    }
    //展示格点产品
    this.displayGridProduct = function(){
        var type = GDYB.GridProductClass.currentType;
        var element = $("#selectElement").val();
        var elementName = $("#selectElement").find("option:selected")[0].innerHTML;
        var maketime = GDYB.GridProductClass.currentMakeTime;
        var version = GDYB.GridProductClass.currentVersion;
        var datetime = t.myDateSelecter.getCurrentTime(false);
        var hourspan = t.yubaoshixiaoTools.hourSpan;
        var fromModel;
        var level = 1000;
        if(type == null || element == null)
            return;

        //获取上一次效
        var i=0;
        var hourspans = GDYB.GDYBPage.getHourSpan(element);
        for(i; i<hourspans.length; i++){
            if(hourspans[i] == hourspan)
                break;
        }
        var hourspanLast = 0;
        if(i>0)
            hourspanLast = hourspans[i-1];

        if(GDYB.GridProductClass.datasetGridInfos == null && GDYB.GridProductClass.datasetGridInfos.length > 0)
            GDYB.GridProductClass.getGridInfo(null, type, element, datetime);
        var displayTime=$("#selectTimes option:selected")[0].innerHTML;
        if(element=="2t"||element=="r24")
        {
            if(displayTime=="天"){
                GDYB.GridProductClass.displayGridProduct(function(){
                }, type, level, element, datetime, version, datetime, hourspan, fromModel, elementName, hourspanLast);
            }
            else{
                let hourspans = [];
                let index = hourspan/24;
                var len = displayTime == "侯"?5:10;
                let startHourspan = (index-1)*len*24;
                for(var i=1;i<=len;i++){
                    var hr = startHourspan + i*24;
                    hourspans.push(hr);
                }
                GDYB.GridProductClass.getGridWithTimes(element,type,level,maketime,version,datetime,hourspans);
                //GDYB.GridProductClass.displayGridProductWithTime(function(){
                //}, type, level, element, datetime, version, datetime, hourspan, fromModel, elementName, displayTime);
            }
        }
        else if(element=="10to302tjp"||element=="10to30r24jp")//10-30天距平
        {
            element = element=="10to302tjp"?"2t":"r24";
            GDYB.GridProductClass.getGrid10To30DayDeparture(element,type,level,maketime,version,datetime);
        }
        GDYB.GridProductClass.layerFillRangeColor.isSmooth = false;
        GDYB.GridProductClass.layerFillRangeColor.refresh();
    };

    //显示当前位置
    this.displayGridLocation = function(){
        var t = this;
        var url=Url_Config.gridServiceUrl+"services/AdminDivisionService/getLocationInfo"; //格点转任意点
        $.ajax({
            data: {"para": "{x:"+ t.currentPosition.lon + ",y:" + t.currentPosition.lat +"}"},
            url: url,
            dataType: "json",
            success: function (data) {
                if(data != null)
                {
                    updateLocationInfo(t.currentPosition.lon, t.currentPosition.lat, data.province_name, data.city_name, data.county_name);
                }
            },
            error:function(e){

            },
            type: "POST"
        });
    };

    //展示趋势图
    this.displayGridValueSerial = function(){
        var t = this;
        var arrayPoint = [];
        arrayPoint.push({x:t.currentPosition.lon,y:t.currentPosition.lat});
        var strPoints = JSON.stringify(arrayPoint);

        var arrayElement = [];
        var hourspans=t.yubaoshixiaoTools.numbers;
        arrayElement.push({name:"2t",hourSpans:JSON.stringify(hourspans)});
        arrayElement.push({name:"r24",hourSpans:JSON.stringify(hourspans)});
        var strElements = JSON.stringify(arrayElement);

        var url=Url_Config.gridServiceUrl+"services/GridService/grid2points"; //格点转任意点
        $.ajax({
            data: {"para": "{type:'"+ GDYB.GridProductClass.currentType + "',makeTime:'" + GDYB.GridProductClass.currentMakeTime
                + "',version:'" + GDYB.GridProductClass.currentVersion + "',elements:"+ strElements + ",points:" + strPoints +"}"},
            url: url,
            dataType: "json",
            success: function (data) {
                if(data != null && data.items.length > 0)
                {
                    updateChartTable(data.items, t.myDateSelecter.getCurrentTimeReal());
                }
            },
            error:function(e){

            },
            type: "POST"
        });
    };
    //展示趋势图(距平)
    this.displayGridValueSerialJuPing = function(){
        var t = this;
        var arrayPoint = [];
        arrayPoint.push({x:t.currentPosition.lon,y:t.currentPosition.lat});
        var strPoints = JSON.stringify(arrayPoint);

        var arrayElement = [];
        var hourspans=t.yubaoshixiaoTools.numbers;
        arrayElement.push({name:"2t",hourSpans:JSON.stringify(hourspans)});
        arrayElement.push({name:"r24",hourSpans:JSON.stringify(hourspans)});
        var strElements = JSON.stringify(arrayElement);

        var url=Url_Config.gridServiceUrl+"services/GridService/grid2pointsJuPing"; //格点转任意点
        $.ajax({
            data: {"para": "{type:'"+ GDYB.GridProductClass.currentType + "',makeTime:'" + GDYB.GridProductClass.currentMakeTime
            + "',version:'" + GDYB.GridProductClass.currentVersion + "',elements:"+ strElements + ",points:" + strPoints +"}"},
            url: url,
            dataType: "json",
            success: function (data) {
                if(data != null && data.items.length > 0)
                {
                    updateChartTableJuPing(data.items, t.myDateSelecter.getCurrentTimeReal());
                }
            },
            error:function(e){

            },
            type: "POST"
        });
    };

    //展示天气概况
    this.displayWeatherDescription = function(){
        var t = this;
        var arrayPoint = [];
        arrayPoint.push({x:t.currentPosition.lon,y:t.currentPosition.lat});
        var strPoints = JSON.stringify(arrayPoint);

        var dateNow = new Date();
        var timeNow = dateNow.getTime();
        var dateStartForecast = t.myDateSelecter.getCurrentTimeReal();
        var timeStartForecast = dateStartForecast.getTime();
        if(timeNow > timeStartForecast) {
            var hourOffset = (timeNow - timeStartForecast) / 1000 / 3600;
            if(hourOffset < 72) { //超过三天的预报，看未来3小时预报已没啥意义了
                var hourSpan3 = 3 + Math.floor(hourOffset/3)*3;
                var hourSpan12 = 12 + Math.floor(hourOffset/12)*12;
                var hourSpan24 = 24 + Math.floor(hourOffset/24)*24;

                var arrayElement = [];
                arrayElement.push({name: "2t", hourSpans: [hourSpan3]});
                arrayElement.push({name: "r24", hourSpans: [hourSpan3]});
//                arrayElement.push({name: "wd3", hourSpans: [hourSpan3]});
//                arrayElement.push({name: "ws3", hourSpans: [hourSpan3]});
//                arrayElement.push({name: "rh", hourSpans: [hourSpan3]});
//                arrayElement.push({name: "tcc", hourSpans: [hourSpan3]});
//                arrayElement.push({name: "vis", hourSpans: [hourSpan3]});
//                arrayElement.push({name: "w", hourSpans: [hourSpan12, hourSpan24]});
//                arrayElement.push({name: "r12", hourSpans: [hourSpan12, hourSpan24]});
//                arrayElement.push({name: "wd", hourSpans: [hourSpan24]});
//                arrayElement.push({name: "ws", hourSpans: [hourSpan24]});
//                arrayElement.push({name: "tmin", hourSpans: [hourSpan24]});
//                arrayElement.push({name: "tmax", hourSpans: [hourSpan24]});
//                arrayElement.push({name: "air", hourSpans: [hourSpan24]});
                var strElements = JSON.stringify(arrayElement);

                var url = Url_Config.gridServiceUrl + "services/GridService/grid2points"; //格点转任意点
                $.ajax({
                    data: {"para": "{type:'" + GDYB.GridProductClass.currentType + "',makeTime:'" + GDYB.GridProductClass.currentMakeTime
                        + "',version:'" + GDYB.GridProductClass.currentVersion + "',elements:" + strElements + ",points:" + strPoints + "}"},
                    url: url,
                    dataType: "json",
                    success: function (data) {
                        if (data != null && data.items.length > 0) {
                            updateWeatherDescription(data.items, hourSpan3, hourSpan12, hourSpan24);
                        }
                    },
                    error: function (e) {

                    },
                    type: "POST"
                });
            }
            else{
                $("#weatherDescription").html("无");
            }
        }
        else{
            $("#weatherDescription").html("无");
        }
    };
}

DisplayPageClass.prototype = new PageBase();