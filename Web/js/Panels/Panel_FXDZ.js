/*
 * 风向订正
 * by zouwei 2015-05-10
 * */
function Panel_FXDZ(div){
    this.div = div;
    this.createPanelDom = function(){
        this.panel = $("<div id=\"Panel_FXDZ\" class=\"dragPanel\">"
            +"<div class=\"title\"><span>风向订正</span><a class=\"closeBtn\">×</a></div>"
            +"<div class=\"body\">"
            +"<div id='div_windTools' class=\"row1\" style=\"text-align: center;\"><img id='img_fx' src=\"imgs/img_uv_fx.png\" style=\"margin-right: 10px;\"/><img id='img_luoqu' src=\"imgs/img_uv_luoqu.png\" style=\"margin-right: 10px;\"/><img id='img_typhoon' src=\"imgs/img_uv_typhoon.png\"/></div>"
            +"<div class=\'row1\' style=\"text-align: center;\"><span>影响半径:</span><input style='width: 80px;padding-left: 5px;' type='text' value='20'/>KM</div></div>"
            +"</div>")
            .appendTo(this.div);

        //风向订正
        $("#img_fx").click(function(){
            if(GDYB.GridProductClass.datasetGrid == null)
                alert("请先打开风场数据");
            else {
                if(GDYB.GridProductClass.currentElement != "10uv"){
                    alert("当前要素不是风场");
                    return;
                }

                if($(this).hasClass("active"))
                {
                    $(this).removeClass("active");
                    GDYB.GridProductClass.drawFreePath.deactivate();
                    startDragMap();
                }
                else {
                    $("#div_windTools").find("img").removeClass("active");
                    $(this).addClass("active");
                    GDYB.GridProductClass.drawFreePath.activate();
                    stopDragMap();
                }
            }
            });

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

        //落区订正
        $("#img_luoqu").click(function(){
            if(GDYB.GridProductClass.datasetGrid == null)
                alert("请先打开风场数据");
            else {
                if(GDYB.GridProductClass.currentElement != "10uv"){
                    alert("当前要素不是风场");
                    return;
                }

                if($(this).hasClass("active"))
                {
                    $(this).removeClass("active");
                    GDYB.GridProductClass.layerLuoqu.removeAllFeatures();
                    GDYB.GridProductClass.layerLuoquCenter.removeAllFeatures();
                    GDYB.GridProductClass.drawLuoqu.deactivate();
                    GDYB.GridProductClass.drawFreePath.deactivate();
                    startDragMap();
                }
                else {
                    $("#div_windTools").find("img").removeClass("active");
                    $(this).addClass("active");

                    GDYB.GridProductClass.layerLuoqu.removeAllFeatures();
                    GDYB.GridProductClass.layerLuoquCenter.removeAllFeatures();
                    GDYB.GridProductClass.currentGridValueDown = 10.8;
                    GDYB.GridProductClass.currentGridValueUp = 13.8;
                    GDYB.GridProductClass.drawLuoqu.activate();
                    stopDragMap();
                }
            }
        });

        //台风
        $("#img_typhoon").click(function(){
            if(GDYB.GridProductClass.datasetGrid == null)
                alert("请先打开风场数据");
            else {
                if(GDYB.GridProductClass.currentElement != "10uv"){
                    alert("当前要素不是风场");
                    return;
                }

                if($(this).hasClass("active"))
                {
                    $(this).removeClass("active");
                }
                else {
                    $("#div_windTools").find("img").removeClass("active");
                    $(this).addClass("active");
                }
            }
        });
    }

    this.init();
    this.panel.css({
        "top":"5px",
        "right":"470px"
    });
}
Panel_FXDZ.prototype = new DragPanelBase();