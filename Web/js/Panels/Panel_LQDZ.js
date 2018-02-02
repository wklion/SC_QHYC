/*
* 落区订正
* by zouwei 2015-05-10
* */
function Panel_LQDZ(div){
    this.div = div;
    this.createPanelDom = function(){
        this.panel = $("<div id=\"Panel_LQDZ\" class=\"dragPanel\">"
            +"<div class=\"title\"><span>落区订正</span><a class=\"closeBtn\">×</a></div>"
            +"<div class=\"body\">"
            +"<div id='div_tools' class=\"row1\" style=\"text-align: center;\"><button id='buttonDrawLuoqu' style='outline:none'>绘制落区</button><button id='buttonClearLuoqu' style='outline:none'>清除落区</button><button id='buttonPickLuoqu' style='outline:none'>拾取落区</button><button id='buttonModifyLuoqu' style='outline:none'>修改落区</button></div>"
            +"<div class=\"row1\" style=\"text-align: center;\"><span>格点值:</span><input id='gridValueDown' style='width: 40px;height: 25px;padding-left: 5px;' type='text' value=''/><span> - </span><input id='gridValueUp' style='width: 40px;height: 25px;padding-left: 5px;' type='text' value=''/><button id='buttonAdd' style='outline:none'> +1 </button><button id='buttonSub' style='outline:none'>-1</button></div>"
            +"<div style='margin: 5px;border:1px solid #999999;background: #fff;'>"
            +"<div id='rain' style='height: 30px;line-height:30px;text-align:center;background: #70a5ce;border-bottom:1px solid #999999;color:#fff;font-size: 14px;font-weight: bold;'>降水（mm）</div>"
            +"<div><table id='tablePrecip' style='width: 100%;text-align: center;text-align: left;margin-left: 10px'>"
            +"<tr><td style='padding:5px 0px 5px 0px;'><span style='display:inline-block;border:1px solid #000;width:26px;height:26px;'><span id='precip_0.1_9.9' class='luoquButton' style=\"display:inline-block;width:24px;height:24px;background:#A6F38D;border:3px solid #fff;\"></span></span><span style='vertical-align: top;line-height: 26px;'>小雨</span></td><td style='padding:5px 0px 5px 0px;'><span style='display:inline-block;border:1px solid #000;width:26px;height:26px;'><span id='precip_10_24.9' class='luoquButton' style=\"display:inline-block;width:24px;height:24px;background:#38A700;border:3px solid #fff;\"></span></span><span style='vertical-align: top;line-height: 26px;'>中雨</span></td></tr>"
            +"<tr><td style='padding:5px 0px 5px 0px;'><span style='display:inline-block;border:1px solid #000;width:26px;height:26px;'><span id='precip_25_49.9' class='luoquButton' style=\"display:inline-block;width:24px;height:24px;background:#61B8FF;border:3px solid #fff;\"></span></span><span style='vertical-align: top;line-height: 26px;'>大雨</span></td><td style='padding:5px 0px 5px 0px;'><span style='display:inline-block;border:1px solid #000;width:26px;height:26px;'><span id='precip_50_99.9' class='luoquButton' style=\"display:inline-block;width:24px;height:24px;background:#0000FE;border:3px solid #fff;\"></span></span><span style='vertical-align: top;line-height: 26px;'>暴雨</span></td></tr>"
            +"<tr><td style='padding:5px 0px 5px 0px;'><span style='display:inline-block;border:1px solid #000;width:26px;height:26px;'><span id='precip_100_249.9' class='luoquButton' style=\"display:inline-block;width:24px;height:24px;background:#FA00FA;border:3px solid #fff;\"></span></span><span style='vertical-align: top;line-height: 26px;'>大暴雨</span></td><td style='padding:5px 0px 5px 0px;'><span style='display:inline-block;border:1px solid #000;width:26px;height:26px;'><span id='precip_250_499.9' class='luoquButton' style=\"display:inline-block;width:24px;height:24px;background:#720000;border:3px solid #fff;\"></span></span><span style='vertical-align: top;line-height: 26px;'>特大暴雨</span></td></tr>"
            +"</table></div>"
            +"</div>"
            +"<div style='margin: 5px;border:1px solid #999999;background: #fff;'>"
            +"<div style='height: 30px;line-height:30px;text-align:center;background: #70a5ce;border-bottom:1px solid #999999;color:#fff;font-size: 14px;font-weight: bold;'>气温（℃）</div>"
            +"<div id='temp'><table style='width: 100%;text-align: center;'>"
            +"<tr><td style='padding:5px 0px 5px 0px;'><span span id='20'  style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#FF0000'>20</span></td>"
            +"<td><span id='21' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#00FF00'>21</span></td>"
            +"<td><span id='22' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#0000FF'>22</span></td>"
            +"<td><span id='23' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#FF00FF'>23</span></td>"
            +"<td><span id='24' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#00FFFF'>24</span></td>"
            +"</tr><tr>"
            +"<td style='padding:5px 0px 5px 0px;'><span id='25'  style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#FFFF00'>25</span></td>"
            +"<td><span id='26' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#70DB93'>26</span></td>"
            +"<td><span id='27' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#5C3317'>27</span></td>"
            +"<td><span id='28' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#9F5F9F'>28</span></td>"
            +"<td><span id='29' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#B5A642'>29</span></td>"
            +"</tr><tr>"
            +"<td style='padding:5px 0px 5px 0px;'><span id='30'  style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#D9D919'>30</span></td>"
            +"<td><span id='31' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#A67D3D'>31</span></td>"
            +"<td><span id='32' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#8C7853'>32</span></td>"
            +"<td><span id='33' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#5F9F9F'>33</span></td>"
            +"<td><span id='34' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#FF7F00'>34</span></td>"
            +"</tr><tr>"
            +"<td style='padding:5px 0px 5px 0px;'><span id='35' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#42426F'>35</span></td>"
            +"<td><span id='36' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#5C4033'>36</span></td>"
            +"<td><span id='37' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#4F4F2F'>37</span></td>"
            +"<td><span id='38' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#9932CD'>38</span></td>"
            +"<td><span id='39' style='display:inline-block;width:32px;height:32px;border:1px solid #adadad;text-align:center;line-height:32px;background:#FF2400'>39</span></td>"
            +"</tr>"
            +"</table></div>"
            +"</div>"
            +"<div class='smallTitle'><span>空间分布正比法</span>"
            +"<div class='checkboxCircle'>"
            +"<label id='labelKeepSpatial' style='background:rgb(52,152,219)' value='1'></label>"
            +"</div></div>"
            +"<div class='smallTitle'><span>反距离权重法</span>"
            +"<div class='checkboxCircle'>"
            +"<label id='labelIDW' value='0'></label>"
            +"</div></div>"
            +"</div>")
            .appendTo(this.div);

        $("#labelKeepSpatial").click(function(){
            $("#labelKeepSpatial").attr("value", 1);;
            $("#labelKeepSpatial").css("background","rgb(52,152,219)");
            $("#labelIDW").attr("value", 0);
            $("#labelIDW").css("background","");
            GDYB.GridProductClass.keepSpatial = $("#labelKeepSpatial").attr("value") == 1;
        });

        $("#labelIDW").click(function(){
            $("#labelKeepSpatial").attr("value", 0);
            $("#labelKeepSpatial").css("background","");
            $("#labelIDW").attr("value", 1);
            $("#labelIDW").css("background","rgb(52,152,219)");
            GDYB.GridProductClass.keepSpatial = $("#labelKeepSpatial").attr("value") == 1;
        });

        //绘制落区
        $("#buttonDrawLuoqu").click(function(){
            if(GDYB.GridProductClass.drawLuoqu == null)
                alert("请先打开格点数据");
            else
            {
                $("#div_tools").find("button").removeClass("active");
                if($(this).hasClass("active"))
                {
                    $(this).removeClass("active");
                    GDYB.GridProductClass.drawLuoqu.deactivate();
                    startDragMap();
                }
                else
                {
                    startDrawLuoqu();
                }
            }
        });

        function startDrawLuoqu(){
            GDYB.GridProductClass.layerLuoqu.removeAllFeatures();
            GDYB.GridProductClass.layerLuoquCenter.removeAllFeatures();
            //$(this).addClass("active"); //只能画一次，就不激活了。如果要支持连续画落区，就放开。另外还有GridProductClass中的drawCompleted事件中停止绘制的代码
            GDYB.GridProductClass.drawLuoqu.activate();
            var gridValueDown = $("#gridValueDown").val();
            var gridValueUp = $("#gridValueUp").val();
            if(gridValueDown != "")
                GDYB.GridProductClass.currentGridValueDown = Number(gridValueDown);
            else
                GDYB.GridProductClass.currentGridValueDown = null;
            if(gridValueUp)
                GDYB.GridProductClass.currentGridValueUp = Number(gridValueUp);
            else
                GDYB.GridProductClass.currentGridValueUp = null;
            GDYB.GridProductClass.keepSpatial = $("#labelKeepSpatial").attr("value") == 1;

            stopDragMap();

            //关闭拾取落区
            if($("#buttonPickLuoqu").hasClass("active")){
                $("#buttonPickLuoqu").removeClass("active");
                GDYB.GridProductClass.action = GDYB.CorrectAction.none;
            }
            //关闭拾取落区
            if($("#buttonModifyLuoqu").hasClass("active")){
                $("#buttonModifyLuoqu").removeClass("active");
                GDYB.GridProductClass.action = GDYB.CorrectAction.none;
            }
        }

        function stopDragMap()
        {
            var map = GDYB.Page.curPage.map;
            for(var i =0; i < map.events.listeners.mousemove.length; i++) {
                var handler = map.events.listeners.mousemove[i];
                if(handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag")
                {
                    handler.obj.active = false;
                }
            }
        }

        function startDragMap()
        {
            var map = GDYB.Page.curPage.map;
            for(var i =0; i < map.events.listeners.mousemove.length; i++) {
                var handler = map.events.listeners.mousemove[i];
                if(handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag")
                {
                    handler.obj.active = true;
                }
            }
        }

        //清除落区
        $("#buttonClearLuoqu").click(function(){
            if(GDYB.GridProductClass.layerLuoqu != null)
                GDYB.GridProductClass.layerLuoqu.removeAllFeatures();
            if(GDYB.GridProductClass.layerLuoquCenter != null)
                GDYB.GridProductClass.layerLuoquCenter.removeAllFeatures();
        });

        //移动落区，魔术棒
        $("#buttonPickLuoqu").click(function(){
            if($(this).hasClass("active"))
            {
                $(this).removeClass("active");
                GDYB.GridProductClass.action = GDYB.CorrectAction.none;
            }
            else
            {
                $("#div_tools").find("button").removeClass("active");
                $(this).addClass("active");
                GDYB.GridProductClass.action = GDYB.CorrectAction.moveLuoqu;
                GDYB.GridProductClass.drawLuoqu.deactivate(); //关闭绘制落区
                startDragMap();
            }
        });

        //修改落区，先拾取落区，然后绘制曲线
        $("#buttonModifyLuoqu").click(function(){
            if($(this).hasClass("active")){
                $(this).removeClass("active");
                GDYB.GridProductClass.action = GDYB.CorrectAction.none;
                GDYB.GridProductClass.drawFreePath.deactivate();
                GDYB.GridProductClass.layerMagic.removeAllFeatures();
            }
            else{
                $("#div_tools").find("button").removeClass("active");
                $(this).addClass("active");
                GDYB.GridProductClass.drawLuoqu.deactivate();  //关闭绘制落区
                //启动修改落区
                GDYB.GridProductClass.action = GDYB.CorrectAction.modifyLuoqu;
            }
            startDragMap();
        });

        //点击格点值
        $("#temp").find("span").click(function(){
            $("#gridValueDown").val(this.id);
            $("#gridValueUp").val(this.id);
            GDYB.GridProductClass.currentGridValueDown = Number($("#gridValueDown").val());
            GDYB.GridProductClass.currentGridValueUp = Number($("#gridValueUp").val());
            startDrawLuoqu();
        });

        $("#tablePrecip").find("span").click(function(){
            if(this.id == null || this.id == "")
                return;
            $("#gridValueDown").val(this.id.split("_")[1]);
            $("#gridValueUp").val(this.id.split("_")[2]);
            GDYB.GridProductClass.currentGridValueDown = Number($("#gridValueDown").val());
            GDYB.GridProductClass.currentGridValueUp = Number($("#gridValueUp").val());
            startDrawLuoqu();
        });

        $("#gridValueDown").change(function(){
            var valueDown = Number($(this).val());
            var valueUp = Number($("#gridValueUp").val());
            if(valueUp < valueDown)
            {
                alert("下限值不能大于上限值");
                $("#gridValueDown").val(valueUp);
            }
            GDYB.GridProductClass.currentGridValueDown = valueDown;
        });
        $("#gridValueUp").change(function(){
            var valueDown = Number($("#gridValueDown").val());
            var valueUp = Number($(this).val());
            if(valueUp < valueDown)
            {
                alert("上限值不能小于下限值");
                $("#gridValueUp").val(valueDown);
            }
            GDYB.GridProductClass.currentGridValueUp = valueUp;
        });

        //+1
        $("#buttonAdd").click(function(){
            GDYB.GridProductClass.addGridByRegion(1);
        });

        //-1
        $("#buttonSub").click(function(){
            GDYB.GridProductClass.addGridByRegion(-1);
        });

//        if(GDYB.GridProductClass.drawLuoqu != null) {
//            GDYB.GridProductClass.drawLuoqu.events.on({"featureadded": drawCompleted1});
//            function drawCompleted1() {
//                if (!GDYB.GridProductClass.keepSpatial) {
//                    $("#buttonDrawLuoqu").removeClass("active");
//                    GDYB.GridProductClass.drawLuoqu.deactivate();
//                    startDragMap();
//                }
//            };
//        }
    }
    this.init();
    this.panel.css({
        "top":"5px",
        "right":"10px"
    });
}
Panel_LQDZ.prototype = new DragPanelBase();