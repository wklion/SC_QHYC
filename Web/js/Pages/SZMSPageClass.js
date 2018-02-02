/*
 * 数值模式页面
 * by zouwei, 2015-05-10
 * */
function SZMSPageClass(){
    this.className = "SZMSPageClass";
    this.yubaoshixiaoTools = null;
    this.renderMenu = function(){
        var htmlStr = "<div id='div_type'><div class='title1'>模式</div>"
            +"<div class='text-center'><button id='cfs' class='btn btn-default w70'>CFS</button><button id='derf' class='btn btn-default w70'>Derf</button></div>"
            +"</div>"
            +"<div id='div_element'><div class='title1'>要素</div>"
            +"<div class='text-center'><button id='r24' class='btn btn-default w70'>降水量</button><button id='2t' class='btn btn-default w70'>气温</button></div>"
            +"<div class='btn_line'></div></div>"
            +"<div id='div_datetime' class=''>"
            +"<div class='title1'>起报时间</div>"
            +"<div id='dateSelect'></div>"
            +"</div>"
            +"<div id='yubaoshixiao' class=''></div>"
            +"<div id='div_display' class=''><div class='title1'>显示</div>"
            +"<div class='text-center'><button id='buttonDisplayPlot' class='btn btn-default w70'>填值</button><button id='buttonDisplayFill' class='btn btn-default w70'>填色</button><button id='buttonDisplayContour' class='btn btn-default w70'>等值线</button><button id='buttonDisplayIsoSurface' class='btn btn-default w70'>色斑图</button></div></div>";
        $("#menu_bd").html(htmlStr);
        htmlStr="<div id='div_Depart'>四川省气候中心</div>";
        $("#menu_bd").append(htmlStr);
        var myDateSelecter = new DateSelecter(2,2);
        //myDateSelecter.intervalMinutes = 60*12; //12小时间隔
        $("#dateSelect").append(myDateSelecter.div);
        //预报时效
        this.yubaoshixiaoTools = new YuBaoshixiaoTools($("#yubaoshixiao"), myDateSelecter.getCurrentTimeReal());
        var me = this;

        var isWaitingForResponse = false; //是否正在等待服务端响应

        //重置界面
        function resetUI(type)
        {
            GDYB.GridProductClass.getLastGridInfo(function(datetime, elements, hourspans, levels, datetimeSerial){
                //使按钮无效
                if(datetime != null && datetime != "null" && datetime != "")
                {
                    myDateSelecter.setCurrentTime(datetime);
                    myDateSelecter.setDatetimeSerial(datetimeSerial);
                }

                var activeElement = "";
                $("#div_element").find("button").removeClass("active");
                $("#div_element").find("button").addClass("disabled");
                if(elements != null && elements != "null" && elements != "")
                {
                    var elementArray = elements.split(",");
                    for(var i=0; i<elementArray.length; i++)
                    {
                        $("#div_element").find("#"+elementArray[i]).removeClass("disabled");
                        if(elementArray[i] == "r12")
                            activeElement = "r12";
                    }
                    if(activeElement == "")
                        activeElement = elementArray[0];
                    $("#div_element").find("#"+activeElement).addClass("active"); //激活第一个要素
                }

                $("#div_level").find("button").addClass("disabled");
                if(levels != null && levels != "null" && levels != "")
                {
                    var levelArray = levels.split(",");
                    for(var i=0; i<levelArray.length; i++)
                        $("#div_level").find("#"+levelArray[i]).removeClass("disabled");
                }

                checkHourSpan(displayGridProduct, type, activeElement, myDateSelecter.getCurrentTime(false));
            },type);
        }

        //点击类型
        $("#div_type").find("button").click(function(){
            if($(this).hasClass("disabled")) //无效按钮，直接返回
                return;
            var btnTypeActive = $("#div_type").find("button.active");
            if(btnTypeActive.attr("id") == this.id) //激活按钮，直接返回
                return;

            btnTypeActive.removeClass("active");
            $(this).addClass("active");
            if(this.id=="vwnd"){
                GDYB.GridProductClass.currentLevel="850";
            }
            resetUI(this.id);
        });

        //点击要素
        $("#div_element").find("button").click(function(){
            if($(this).hasClass("disabled")) //无效按钮，直接返回
                return;
            var btnElementActive = $("#div_element").find("button.active");
            if(btnElementActive.attr("id") == this.id)
                return;

            btnElementActive.removeClass("active");
            $(this).addClass("active");

            var element = this.id;
            openElement(element);
        });

        function openElement(element){
            //切换时效面板
            /*if(true){
                me.yubaoshixiaoTools.numbers = [24,48,72,96,120,144,168,192,216,240,264,288,312,336,360,384,408,432,456,480,504,528,552,576,600,624,648,672,696,720];
            }*/
            me.yubaoshixiaoTools.createDom(myDateSelecter.getCurrentTimeReal());
            regesterYuBaoShiXiaoEvent();

            var btnTypeActive = $("#div_type").find("button.active");
            if(btnTypeActive != null)
                checkHourSpan(displayGridProduct, btnTypeActive.attr("id"), element, myDateSelecter.getCurrentTime(false));
        }

        //键盘按键事件，实现上翻、下翻
        $(document).keydown(function (event) {
            if(typeof(GDYB.Page.curPage.className) == "undefined" || GDYB.Page.curPage.className != me.className)
                return;
            if(isWaitingForResponse)
                return;
            if(document.activeElement.id == "table_yubaoshixiao"){  //时效上下翻
                var offset = 0;
                if(event.keyCode == 37 || event.keyCode == 38)  //左上
                    offset = -1;
                else if(event.keyCode == 39 || event.keyCode == 40) //右下
                    offset = 1;

                if(offset != 0){
                    var hourspans = me.yubaoshixiaoTools.numbers;
                    var hourSpan = me.yubaoshixiaoTools.hourSpan;
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
                    me.yubaoshixiaoTools.hourSpan = hourSpan.toString();
                    $("#table_yubaoshixiao").find("#"+hourSpan+"h").addClass("active");
                    displayGridProduct();
                }
            }
            else if(document.activeElement.parentNode.parentNode.id == "div_element"){ //要素上下翻
                var offset = 0;
                if(event.keyCode == 37 || event.keyCode == 38)  //左上
                    offset = -1;
                else if(event.keyCode == 39 || event.keyCode == 40) //右下
                    offset = 1;
                if(offset != 0){
                    var nIndex = -1;
                    var btnElementActive = $("#div_element").find("button.active");
                    var id = btnElementActive.attr("id");
                    var btns = $("#div_element").find("button");
                    for(var i=0; i<btns.length; i++)
                        if(id == btns[i].id){
                            nIndex = i;
                            break;
                        }
                    nIndex += offset;
                    if(nIndex >= btns.length)
                        nIndex = 0;
                    else if(nIndex < 0)
                        nIndex = btns.length - 1;
                    var btn = btns[nIndex];
                    btnElementActive.removeClass("active");
                    $("#"+btn.id).addClass("active");
                    openElement(btn.id);
                }
            }
        });

        //注册时效点击事件
        function regesterYuBaoShiXiaoEvent(){
            $("#yubaoshixiao").find("td").click(function(){
                var btnTypeActive = $("#div_type").find("button.active");
                var btnElementActive = $("#div_element").find("button.active");
                if(btnTypeActive != null && btnElementActive!= null)
                    checkHourSpan(displayGridProduct, btnTypeActive.attr("id"), btnElementActive.attr("id"), myDateSelecter.getCurrentTime(false));
            });
        };
        regesterYuBaoShiXiaoEvent();


        //点击时次
        myDateSelecter.input.change(function(){
            me.yubaoshixiaoTools.createDom(myDateSelecter.getCurrentTimeReal());
            regesterYuBaoShiXiaoEvent();
            var btnTypeActive = $("#div_type").find("button.active");
            var btnElementActive = $("#div_element").find("button.active");
            if(btnTypeActive != null && btnElementActive!= null)
                checkHourSpan(displayGridProduct, btnTypeActive.attr("id"), btnElementActive.attr("id"), myDateSelecter.getCurrentTime(false));
        });

	    var bfirst = true; //是否第一次进入数值模式页面
        function displayGridProduct(){
            var btnType = $("#div_type").find("button.active");
            var btnLevel = $("#div_level").find("button.active");
            if(btnType != null){
                var btnElement = $("#div_element").find("button.active");
                var type = btnType.attr("id");
                var level = btnLevel.attr("id");
                var element = btnElement.attr("id");
                var elementName = btnElement[0].innerHTML;
                var datetime = myDateSelecter.getCurrentTime(false);

                var hourspan = me.yubaoshixiaoTools.hourSpan;
                if(typeof(hourspan) == "undefined")
                    return;

                //获取上一次效
                var i=0;
                for(i; i<me.yubaoshixiaoTools.numbers.length; i++){
                    if(me.yubaoshixiaoTools.numbers[i] == hourspan)
                        break;
                }
                var hourspanLast = 0;
                if(i>0)
                    hourspanLast = me.yubaoshixiaoTools.numbers[i-1];

                var fromModel;

                $("#buttonDisplayPlot").addClass("disabled");//填值
                $("#buttonDisplayFill").addClass("disabled");//填色
                $("#buttonDisplayContour").addClass("disabled");//等值线
                $("#buttonDisplayIsoSurface").addClass("disabled");//色斑图
                isWaitingForResponse = true;
                GDYB.GridProductClass.displayGridProduct(function(){
                    isWaitingForResponse = false;
                    if(bfirst) //如果是第一次，（数值模式）默认不显示填值，默认显示填值 和 等值线
                    {
                        bfirst = false;
                        GDYB.GridProductClass.layerFillRangeColor.isShowLabel = false;
                        GDYB.GridProductClass.layerFillRangeColor.isShowGridline = false;
                        GDYB.GridProductClass.layerFillRangeColor.refresh();
                    }

                    GDYB.GridProductClass.layerContour.setVisibility(true);
                    GDYB.GridProductClass.addContour(function () {
                        if (GDYB.GridProductClass.layerContour.visibility) {
                            if (GDYB.GridProductClass.layerContour.features.length == 0) {
                                $("#buttonDisplayContour").removeClass("active");
                                $("#buttonDisplayContour").addClass("disabled");
                            }
                            else {
                                $("#buttonDisplayContour").removeClass("disabled");
                                $("#buttonDisplayContour").addClass("active");
                            }
                        }
                        else {
                            $("#buttonDisplayContour").removeClass("disabled");
                        }
                    }, GDYB.Page.curPage.map);

                    if(GDYB.GridProductClass.layerFillRangeColor.visibility)
                    {
                        if(GDYB.GridProductClass.layerFillRangeColor.grid != null &&
                            GDYB.GridProductClass.layerFillRangeColor.grid.length == 0)
                        {
                            $("#buttonDisplayPlot").removeClass("active");
                            $("#buttonDisplayPlot").addClass("disabled");
                            $("#buttonDisplayFill").removeClass("active");
                            $("#buttonDisplayFill").addClass("disabled");
                        }
                        else
                        {
                            $("#buttonDisplayPlot").removeClass("disabled");
                            if(GDYB.GridProductClass.layerFillRangeColor.isShowLabel)
                                $("#buttonDisplayPlot").addClass("active");
                            $("#buttonDisplayFill").removeClass("disabled");
                            $("#buttonDisplayFill").addClass("active");
                        }
                    }
                    else
                    {
                        $("#buttonDisplayPlot").removeClass("disabled");
                        $("#buttonDisplayFill").removeClass("disabled");
                    }
                    if(GDYB.GridProductClass.layerFillRangeColor != null){
                        if(GDYB.GridProductClass.layerFillRangeColor.visibility){
                            if (GDYB.GridProductClass.layerFillRangeColor.datasetGrid == null || GDYB.GridProductClass.layerFillRangeColor.datasetGrid.grid == null) {
                                $("#buttonDisplayIsoSurface").removeClass("active");
                                $("#buttonDisplayIsoSurface").addClass("disabled");
                            }
                            else {
                                if(GDYB.GridProductClass.layerFillRangeColor.isSmooth){
                                    $("#buttonDisplayIsoSurface").removeClass("disabled");
                                    $("#buttonDisplayIsoSurface").removeClass("active");
                                }
                                else {
                                    $("#buttonDisplayIsoSurface").removeClass("disabled");
                                    $("#buttonDisplayIsoSurface").addClass("active");
                                }
                            }
                        }
                        else{
                            $("#buttonDisplayIsoSurface").removeClass("disabled");
                        }
                    }
                    $("#buttonDisplayIsoSurface").addClass("active");
                    GDYB.GridProductClass.layerFillRangeColor.isSmooth = false;
                    GDYB.GridProductClass.layerFillRangeColor.refresh();
                },type, level, element, datetime, "p", datetime, hourspan, fromModel, elementName, hourspanLast);
            }
        }

        //检查时效
        function checkHourSpan(recall, type, element, datetime){
            GDYB.GridProductClass.getHourSpanWithElement(function(hourspans){
                $("#yubaoshixiao").find("td").removeClass("active");
                $("#yubaoshixiao").find("td").addClass("disabled");
                if(hourspans != null && hourspans.length > 0)
                {
                    var bContain = false;
                    for(var i=0; i<hourspans.length; i++)
                    {
                        $("#yubaoshixiao").find("#"+hourspans[i]+"h").removeClass("disabled");
                        if(hourspans[i] == me.yubaoshixiaoTools.hourSpan)
                            bContain = true;
                    }
                    if(!bContain)
                    {
                        me.yubaoshixiaoTools.hourSpan = hourspans[0];
                    }
                    $("#yubaoshixiao").find("#"+me.yubaoshixiaoTools.hourSpan+"h").addClass("active");
                }
                recall&&recall();
            },type, element, datetime, "p", datetime);
        };

        //填值显隐
        $("#buttonDisplayPlot").click(function(){
            if($("#buttonDisplayPlot").hasClass("active")){
                $("#buttonDisplayPlot").removeClass("active");
                GDYB.GridProductClass.layerFillRangeColor.isShowLabel = false;
            }
            else{
                $("#buttonDisplayPlot").addClass("active");
                GDYB.GridProductClass.layerFillRangeColor.isShowLabel = true;
            }
            GDYB.GridProductClass.layerFillRangeColor.refresh();
        });
        //填色显隐
        $("#buttonDisplayFill").click(function(){
            if($("#buttonDisplayFill").hasClass("disabled"))
                return;
            if(GDYB.GridProductClass.layerFillRangeColor == null || GDYB.GridProductClass.layerFillRangeColor.visibility)
                $("#buttonDisplayFill").removeClass("active");
            else
                $("#buttonDisplayFill").addClass("active");
            GDYB.GridProductClass.layerFillRangeColor.setVisibility(!GDYB.GridProductClass.layerFillRangeColor.visibility);
            if(GDYB.GridProductClass.layerFillRangeColor.visibility && GDYB.GridProductClass.layerFillRangeColor.gridS.length == 0) //可见，但无要素，请求一下
                GDYB.GridProductClass.addGrid(null, GDYB.Page.curPage.map);
        });
        //等值线
        $("#buttonDisplayContour").click(function(){
            if($("#buttonDisplayContour").hasClass("disabled"))
                return;
            if(GDYB.GridProductClass.layerContour == null || GDYB.GridProductClass.layerContour.visibility)
                $("#buttonDisplayContour").removeClass("active");
            else
                $("#buttonDisplayContour").addClass("active");
            GDYB.GridProductClass.layerContour.setVisibility(!GDYB.GridProductClass.layerContour.visibility);
            if(GDYB.GridProductClass.layerContour.visibility && GDYB.GridProductClass.layerContour.features.length == 0) //可见，但无要素，请求一下
                GDYB.GridProductClass.addContour(null, GDYB.Page.curPage.map);
        });
        //色斑图
        $("#buttonDisplayIsoSurface").click(function(){
            if($("#buttonDisplayIsoSurface").hasClass("disabled"))
                return;

            if($("#buttonDisplayIsoSurface").hasClass("active")){
                $("#buttonDisplayIsoSurface").removeClass("active");
                GDYB.GridProductClass.layerFillRangeColor.isSmooth = true;
                GDYB.GridProductClass.layerFillRangeColor.refresh();
            }
            else{
                $("#buttonDisplayIsoSurface").addClass("active");
                GDYB.GridProductClass.layerFillRangeColor.isSmooth = false;
                GDYB.GridProductClass.layerFillRangeColor.refresh();
            }
        });
        //初始化默认值
        resetUI("cfs");
        $("#1000").addClass("active");
        $("#div_type #cfs").addClass("active");
        //$("#yubaoshixiao").find("#24h").addClass("active");
        //me.yubaoshixiaoTools.hourSpan = 24;
        $("#buttonDisplayPlot").addClass("disabled");
        $("#buttonDisplayFill").addClass("disabled");
        $("#buttonDisplayContour").addClass("disabled");
        $("#buttonDisplayIsoSurface").addClass("disabled");

		GDYB.GridProductClass.currentVersion = "p";
    };
}
SZMSPageClass.prototype = new PageBase();