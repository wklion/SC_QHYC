/**
 * SideWrapper.js 左侧竖条响应代码
 * Created by zouwei on 2015-10-3.
 */
function SideWrapper(){
    this.activeButton = "";
    var t = this;

    //设置当前按钮，设置模式来源及模式时间
    this.setActive = function(activeButton, nwpModelTime){
        t.activeButton = activeButton;
        if(typeof(activeButton) == "undefined" || activeButton == "" || activeButton == "undefined")
            $("#nav_bg_slider").css("display","none");
        else{
            var btn = $("#"+t.activeButton)[0];
            if(typeof(btn) != "undefined") {
                $("#nav_bg_slider").css("top", btn.offsetTop);
                $("#nav_bg_slider").css("display", "block");
                $("#" + activeButton).attr("title", nwpModelTime);
            }
        }
    };

    //注册事件
    this.register = function(){
        $("#sideWrapper").find("li").click(function(){
            if(this.activeButton == this.id)
                return;
            if(this.id == "InitialField"){
                $("#div_modal_content").html("是否重新调入初始场");
                $("#div_modal").modal();
                $("#div_modal").find("a").unbind();
                $("#div_modal").find("a").click(function(){
                    if(typeof(this.id) != "undefined"){
                        if(this.id == "btn_ok")
                        {
                            var url=Url_Config.gridServiceUrl+"services/GridService/getGridDefaultSchemes";
                            $.ajax({
                                data:{"para":"{}"},
                                url:url,
                                dataType:"json",
                                success:function(data){
                                    var defaultSchemes = data;
                                    var modelType = null;
                                    if($("#inputCurrentElementCurrentHourspan")[0].checked) {
                                        modelType = getModelByElement();
                                        if(modelType == null)
                                            alert("初始场默认方案中没有该要素的配置");
                                        else
                                            GDYB.GridProductClass.callModel(modelType, GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion);
                                    }
                                    else if($("#inputCurrentElementAllHourspan")[0].checked)
                                    {
                                        modelType = getModelByElement();
                                        if(modelType == null)
                                            alert("初始场默认方案中没有该要素的配置");
                                        else
                                            GDYB.GDYBPage.callModels(modelType, GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion);
                                    }
                                    else if($("#inputAllElementAllHourspan")[0].checked)
                                        GDYB.GDYBPage.callModelsAll(modelType, defaultSchemes, GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion);

                                    //匹配要素-模式方案
                                    function getModelByElement(){
                                        var modelType = null;
                                        if(typeof(defaultSchemes) != "undefined" && defaultSchemes.length > 0){
                                            var makeTime = GDYB.GridProductClass.currentMakeTime.replace(/\d*-\d*-\d* (\d*):\d*:\d*/,"$1")+":"+GDYB.GridProductClass.currentMakeTime.replace(/\d*-\d*-\d* \d*:(\d*):\d*/,"$1");
                                            for(var key in defaultSchemes){
                                                var scheme = defaultSchemes[key];
                                                if(scheme.type == GDYB.GridProductClass.currentType && scheme.makeTime == makeTime && scheme.element == GDYB.GridProductClass.currentElement){
                                                    modelType = scheme.model;
                                                    break;
                                                }
                                            }
                                        }
                                        return modelType;
                                    }
                                },
                                error: function (e) {
                                    alert("获取初始场默认方案错误");
                                },
                                type:"POST"
                            });
                        }
                    }
                });
            }
            else if(this.id == "prvn" || this.id == "cty") //调入格点产品，与调入模式不同的是，格点产品制作时间与预报时间不同
            {
                var id = this.id;
                var typeModel = GDYB.GridProductClass.currentType;
                var element = GDYB.GridProductClass.currentElement;
                var forecastTime = GDYB.GridProductClass.currentDateTime;
                var url=Url_Config.gridServiceUrl+"services/GridService/getGridProductLastDate";
                $.ajax({
                    data:{"para":"{element:'"+ element + "',type:'" + typeModel + "',forecastTime:'" + forecastTime + "'}"},
                    url:url,
                    dataType:"text",
                    success:function(data){
                        if(typeof(data) == "undefined" || data == null || data == ""){
                            alert("没有找到上一期");
                            return;
                        }
                        $("#div_modal_content").html("是否调入"+(id == "prvn"?"区台指导":"市台订正")+"预报：" + data);
                        $("#div_modal").modal();
                        $("#div_modal").find("a").unbind();
                        $("#div_modal").find("a").click(function(){
                            if(typeof(this.id) != "undefined"){
                                if(this.id == "btn_ok")
                                {
                                    if($("#inputCurrentElementCurrentHourspan")[0].checked)
                                        GDYB.GridProductClass.callModel(typeModel, data, "p");
                                    else if($("#inputCurrentElementAllHourspan")[0].checked)
                                        GDYB.GDYBPage.callModels(typeModel, data, "p");
                                    else if($("#inputAllElementAllHourspan")[0].checked)
                                        GDYB.GDYBPage.callModelsAll(typeModel, null, data, "p");
                                }
                            }
                        });
                    },
                    error: function (e) {
                        alert("没有找到上一期");
                    },
                    type:"POST"
                });
            }
            else if(this.id == "ec" || this.id == "gp" || this.id == "japan" || this.id == "t639" || this.id == "bj" || this.id == "cfs" || this.id == "derf") //调入模式
            {
                var typeModel = this.id;
                var datetime = GDYB.GridProductClass.currentDateTime;
                $("#div_modal_content").html("是否调入模式数据（" + datetime + "）");
                $("#div_modal").modal();
                $("#div_modal").find("a").unbind();
                $("#div_modal").find("a").click(function(){
                    if(typeof(this.id) != "undefined"){
                        if(this.id == "btn_ok")
                        {
                            if($("#inputCurrentElementCurrentHourspan")[0].checked)
                                GDYB.GridProductClass.callModel(typeModel, datetime, "p");
                            else if($("#inputCurrentElementAllHourspan")[0].checked)
                                GDYB.GDYBPage.callModels(typeModel, datetime, "p");
                            else if($("#inputAllElementAllHourspan")[0].checked)
                                GDYB.GDYBPage.callModelsAll(typeModel, datetime, "p");
                        }
                    }
                });

              /*  var element = GDYB.GridProductClass.currentElement;
                var url=Url_Config.gridServiceUrl+"services/GridService/getNWPModelLastDate";
                $.ajax({
                    data:{"para":"{element:'"+ element + "',type:'"+typeModel + "'}"},
                    url:url,
                    dataType:"text",
                    success:function(data){

                    },
                    error: function (e) {
                        alert("没有找到数值模式");
                    },
                    type:"POST"
                });*/
            }

            else if(this.id=="zdyb"){
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                GDYB.Page.curPage = GDYB.ZDYBSZPageClass;
                GDYB.ZDYBSZPageClass.active();
            }

            else if(this.id=="qygl"){
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                GDYB.Page.curPage = GDYB.XTGLPage;
                GDYB.XTGLPage.active();
            }
            else if(this.id == "zddr"){
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                GDYB.Page.curPage = GDYB.ZDGLPage;
                GDYB.ZDGLPage.active();
            }
            else if(this.id == "dz"){
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                GDYB.Page.curPage = GDYB.YBCPPage;
                GDYB.YBCPPage.active();
            }
            else if(this.id == "qy"){
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                GDYB.Page.curPage = GDYB.QYCPPage;
                GDYB.QYCPPage.active();
            }
            else if(this.id == "dzsk"){
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                GDYB.Page.curPage = GDYB.YBCPSKPage;
                GDYB.YBCPSKPage.active();
            }
            else if(this.id == "lsw"){
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                GDYB.Page.curPage = GDYB.LSWPage;
                GDYB.LSWPage.active();
            }
            else if(this.id == "mjo"){
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                GDYB.Page.curPage = GDYB.MJOPage;
                GDYB.MJOPage.active();
            }
            else //数据浏览
            {
                $("#"+t.activeButton).removeClass("active");
                $(this).addClass("active");
                t.activeButton = this.id;
                if(t.activeButton == "menu_skzl")
                {
                    GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                    GDYB.Page.curPage = GDYB.SKZLPage;
                    GDYB.SKZLPage.active();
                }
                else if(t.activeButton == "menu_wxld")
                {
                    GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                    GDYB.Page.curPage = GDYB.WXLDPage;
                    GDYB.WXLDPage.active();
                }
                else if(t.activeButton == "menu_szms")
                {
                    GDYB.Page.curPage&&GDYB.Page.curPage.destroy();
                    GDYB.Page.curPage = GDYB.SZMSPage;
                    GDYB.SZMSPage.active();
                }
            }
        });

        $("#sideWrapper").find("li").hover(function(){
            $("#nav_bg_slider").css("display","block");
            $("#nav_bg_slider").css("top", this.offsetTop);
        });

        $("#sideWrapper").mouseleave(function(){
            if(t.activeButton == "")
                $("#nav_bg_slider").css("display","none");
            else
                $("#nav_bg_slider").css("top", $("#"+t.activeButton)[0].offsetTop);
        });
    };
}