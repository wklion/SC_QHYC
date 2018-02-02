/**
 * Created by wangkun on 2016/5/17.
 * @description 区域评分
 */
function QYCPPageClass(){
    var t=this;
    this.renderMenu= async function () {
        //初始化时间
        var htmlStr ="<div id='dateControl' style='height: 30px;width: 100%;margin-left: 0px;'><p style='float: left;width: 60px;height: 30px;text-align: center;padding-top: 15px;'>日期</p><div id='dateSelect' style='margin: 10px 3px 0px 0px;float: left;width: 140px;height: 26px;'></div></div>";

        $("#menu_bd").html(htmlStr);
        GDYB.GridProductClass.init();
        GDYB.GridProductClass.currentUserArea = $.cookie("departCode");
        GDYB.GridProductClass.layerFocusArea.visibility = true;
        GDYB.GridProductClass.showFocusArea();
        t.myDateSelecter = new DateSelecter(2,2,"yyyy-mm-dd");
        t.myDateSelecter.intervalMinutes = 60*24; //24小时
        $("#dateSelect").html(t.myDateSelecter.div);
        $("#dateSelect").find("input").css("width","90px");

        //获取最新时间
        var lastDateObj = await t.getLastDate();
        if(lastDateObj.suc){
            var fullDate = lastDateObj.suc.split("-");
            var date = new Date(fullDate[0], fullDate[1]-1, fullDate[2], 0, 0, 0);
            t.myDateSelecter.setCurrentTime(date.format("yyyy-MM-dd"));
        }

        ClearPage();
        var htmlYBCP="<div id='YBCPDiv' style='background-color: rgb(255, 255, 255);height: 100%;right: 0px;left:400px;position: absolute;'></div>";
        $("#workspace_div").append(htmlYBCP);
        var htmlTable="<table id='dtData' class='dtData'></table>";
        $("#YBCPDiv").append(htmlTable);
        //请求数据
        GetData();
        //时间控件更新
       $("#dateSelect").find("input").bind("change",function(){
            GetData();
        });
        $("#dateSelect").find("span").bind("click",function(){
            GetData();
        });
    }
    function GetData(){
        var strTime=$("#dateSelect").find("input").val();//日期
        strTime=strTime.replace(/-/g,"");
        $.ajax({
            type: 'post',
            url: Url_Config.gridServiceUrl + "services/GridService/getAreaZSCS",
            data: {'para': '{"curtime":"'+strTime+'"}'},
            dataType: 'json',
            error: function () {
                alert('获取区域ZSCS出错!');
            },
            success: function (data)
            {
                FillData(data);
            }
        });
    }
    function FillData(data){
        $("#dtData").html("");//清空
        var htmlContent="";//5列
        //标题
        var strTime=$("#dateSelect").find("input").val();//日期
        htmlContent+="<tr><td colspan='5' style='font-size: 22px;'>"+strTime+"区域检验</td></tr>";//字体要大一些
        htmlContent+="<tr><th>区域</th><th>温度ZS检验值</th><th>温度CS检验值</th><th>降水ZS检验值</th><th>降水CS检验值</th></tr>"
        for(var d=0;d<data.length;d++)
        {
            var areaname=data[d][0];//区域
            var tzs=data[d][1].toFixed(2);//tzs
            var tcs=data[d][2].toFixed(2);//tcs
            var rzs=data[d][3].toFixed(2);//rzs
            var rcs=data[d][4].toFixed(2);//rcs
            htmlContent+="<tr><td>"+areaname+"</td><td>"+tzs+"</td><td>"+tcs+"</td><td>"+rzs+"</td><td>"+rcs+"</td></tr>"
        }
        $("#dtData").append(htmlContent);
    }
    function ClearPage(){
        $("#YBCPDiv").remove();
    }
    /**
     * @author:wangkun
     * @date:2018-1-14
     * @modifydate:
     * @param:
     * @return:
     * @description:获取最新时间
     */
    this.getLastDate = function(){
        var pro = new Promise(function (resolve, reject) {
            $.ajax({
                type: 'post',
                url: Url_Config.gridServiceUrl + "services/TestService/getZsCsLastDate",
                data: {'para': '{}'},
                dataType: 'json',
                error:resolve,
                success:resolve
            });
        });
        return pro;
    }
}
QYCPPageClass.prototype = new PageBase();