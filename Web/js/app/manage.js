/**
 * @author: wangkun
 * @date:   2017/4/18.
 * @description 管理页面
 */
function Manage(){
    this._init_();
}
Manage.prototype={
    constructor:Manage,
    _init_:function(){
        this.name="管理页面";
    },
    /**
     * @author:wangkun
     * @date:2017-03-09
     * @return:
     * @description:加载地图页面
     */
    loadMapPage:function(){
        $("#content").html(`
            <h2>显示区域</h2>
            <div class="h-big-row" id="areactive"><span id="xn">西南区域</span><span id="sc" class="active">四川</span></div>
            <div class="h-big-row"><button id="saveData" class="btn btn-default">保存配置</button><span style="color:red;display:none;">保存成功</span></div>
        `);
        initMapPage();
        function initMapPage(){
            //加载激活区域
            var activeAreaID=window.localStorage.getItem("activearea");
            if(activeAreaID!=undefined&&activeAreaID!=null&&activeAreaID!=""){
                $("#areactive span").removeClass("active");
                $("#"+activeAreaID).addClass("active");
            }
            $("#areactive span").on("click",function(){
                $(this).siblings().removeClass("active");
                $(this).addClass("active");
            });
            $("#saveData").on("click",function(){
                var activeAreaID=$("#areactive span.active")[0].id;
                window.localStorage.setItem("activearea",activeAreaID);
                $(this).next().css("display","block");
            });
        }
    }
}