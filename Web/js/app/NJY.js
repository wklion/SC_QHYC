/**
 * @author:wangkun
 * @date:2017-09-01
 * @param:
 * @return:
 * @description:年季月入口
 */
define(['mapUtil','ModelResView','dongli',"CorrReg",'fillrangecolor','vue','processControl','IntegrationForecast','ForecastTest'], function(mapUtil,modelResView,dongli,corrReg,frc,Vue,processControl,integrationForecast,forecastTest) {
    return {
        /**
		 * @author:wangkun
		 * @date:2017-09-01
		 * @param:
		 * @return:
		 * @description:初始化
		 */
        Init:function(){
            var me = this;
            initRes();//初始化资源
            initEvent();//注册事件
			function initEvent(){
				$(".navigator button").on("click",function(){
					$(this).siblings().removeClass("active");
                    $(this).addClass("active");
                    clearDiv();
                    clearTitle();
                    clearDatepicker();
                    var id = this.id;
                    mapUtil.clearMap();
                    if(id==="data_view_btn"){
                        modelResView.Init();
                        //dongli.Init();
                    }
                    else if(id==="dynamical_downscaling_btn"){
                        dongli.Init();
                    }
                    else if(id === "corr_reg_btn"){
                        corrReg.Init();
                    }
                    else if(id === "integration_forecast_btn"){
                        integrationForecast.Init();
                    }
                    else if(id === "prediction_detection_btn"){
                        forecastTest.Init();
                    }
				});
            }
            function initRes(){
                mapUtil.initWeatherMap();
                window.vueTitle = new Vue({
                    el:"#title",
                    data:{
                        datetime:"",
                        name:"",
                        type:""
                    }
                });
                processControl.init();
            }
            //清除要移除的div
            function clearDiv(){
                $(".delete").remove();
            }
            function clearTitle(){
                //$("#title").css("display","none")
                vueTitle.datetime = "";
                vueTitle.name = "";
                vueTitle.type = "";
            }
            function clearDatepicker(){
                $("#timeticker").remove();
            }
            $(".navigator button:first").click();
        }
    }
});