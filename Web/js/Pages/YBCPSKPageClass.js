/**
 * @author wangkun
 * 2016-05-10
 * @description 系统测评
 */

function YBCPSKPageClass(){
    var t=this;
    t.ElementID=null;
    t.stations=[];
    t.strDateTime="";
    t.stationNames=[];
    t.isForcastF=false;
    t.isRealF=false;
    t.forcastData={};
    t.realData={};
	 //渲染左侧菜单区域里的按钮
    this.renderMenu = function(){
        t.Register();
        t.InitiPage();
        t.CreateDom();
        var control=new CDateAndArea("ybcpsk_div");
        $("#daArea").change(function(){
            /*GetData();*/
            GetCurrentAreaStation();
        });
        $('#datepicker').change(function(){
            GetData();
        });
        GetCurrentAreaStation();
    }// rendMenu end
    //获取数据
    function GetData(){
        $(".cpSation div").empty();
        t.isForcastF=false;
        t.isRealF=false;
        t.forcastData={};
        t.realData={};
        var datetime=$("#datepicker").val();
        var areaname=$("#daArea option:selected").val();
        if(datetime==undefined||areaname==undefined||datetime==""||areaname==""){
            alert("日期或区域为空!");
            return;
        }
        GetBaoWenData();
        GetRealData();
    }
    //获取报文数据
    function GetBaoWenData(){
        var datetime=$("#datepicker").val();
        $.ajax({
           type: 'post',
           url: Url_Config.gridServiceUrl + "services/GridService/getZhuRiBaoWen",
           data: {'para': '{"datetime":"'+ datetime+'"}'},
           dataType: 'json',
           error: function () {
                $("#div_progress").css("display", "none");
               alert('获取报文数据出错!');
           },
           success: function (data) {
              if(data==undefined||data.length==0){
                $("#div_progress").css("display", "block");
                $("#div_progress_title").html("预报无数据!");
                $("#div_progress").fadeOut(2000,function(){});
                return;
            }
            t.isForcastF=true;
            t.forcastData=data;
            DataCompleted();
        }
       });
    }
    function GetRealData(){
        var datetime=$("#datepicker").val();
        $.ajax({
           type: 'post',
           url: Url_Config.gridServiceUrl + "services/DBService/getRealVal",
           data: {'para': '{"datetime":"'+ datetime+'"}'},
           dataType: 'json',
           error: function () {
                $("#div_progress").css("display", "none");
               alert('获取实况数据出错!');
           },
           success: function (data){
              if(data==undefined||data.length==0){
                $("#div_progress").css("display", "block");
                $("#div_progress_title").html("实况无数据!");
                $("#div_progress").fadeOut(2000,function(){});
                return;
              }
                t.isRealF=true;
                t.realData=data;
                DataCompleted();
           }
       });
    }
    function DataCompleted(){
        if(t.isRealF&&t.isForcastF){//数据都请求完成
            t.isRealF=false;
            t.isForcastF=false;
            //labels
            var labels=[];
            var datetime=$("#datepicker").val();
            var date=new Date(datetime);
            date=date.addDays(1);
            for(var d=0;d<30;d++){
                var tempDate=date.format("MM-dd");
                labels.push(tempDate);
                date=date.addDays(1);
            }
            for(var s=0;s<t.stations.length;s++){
                var stationnum=t.stations[s];
                var count=0;
                var tempDataF=[];
                var precDataF=[];
                var tempDataR=[];
                var precDataR=[];
                for(var i=0;i<t.forcastData.length;i++){
                    var tempNum=t.forcastData[i][0];
                    if(tempNum==stationnum){
                        var id=t.forcastData[i][t.forcastData[i].length-1];
                        if(id=="temp"){
                            for(var index=1;index<t.forcastData[i].length-1;index++){
                                tempDataF.push(Number(t.forcastData[i][index]));
                            }
                        }
                        else{
                            for(var index=1;index<t.forcastData[i].length-1;index++){
                                precDataF.push(Number(t.forcastData[i][index]));
                            }
                        }
                    }
                }
                if(precDataF.length==0&&tempDataR.length==0)
                    continue;
                for(var i=0;i<t.realData.length;i++){
                    var tempNum=t.realData[i][0];
                    if(tempNum==stationnum){
                        var id=t.realData[i][t.realData[i].length-1];
                        if(id=="temp"){
                            for(var index=1;index<t.realData[i].length-1;index++){
                                tempDataR.push(Number(t.realData[i][index]));
                            }
                        }
                        else{
                            for(var index=1;index<t.realData[i].length-1;index++){
                                precDataR.push(Number(t.realData[i][index]));
                            }
                        }
                        count++;
                        if(count==2){
                            addCharData(s,labels,tempDataF,precDataF,tempDataR,precDataR);
                        }
                    }
                }
            }
        }
    }
    //获取当前区域的所有站点
    function GetCurrentAreaStation(){
        $("#div_progress_title").html("正在请求数据!");
        $("#div_progress").css("display", "block");
        $("#ybcpskda_div").empty();
        t.stations=[];
        t.stationNames=[];
       var areaname=$("#daArea option:selected").val();
       if(areaname==undefined||areaname=="")
        return;
       $.ajax({
           type: 'post',
           url: Url_Config.gridServiceUrl + "services/AreaService/getStationByAreaName",
           data: {'para': '{"areaname":"'+ areaname+'"}'},
           dataType: 'json',
           error: function () {
                $("#div_progress").css("display", "none");
               alert('获取区域出错!');
           },
           success: function (data) {
               StationGetCompleted(data);
           }
       });
    }
    //站点请求完成
    function StationGetCompleted(data){
        for(var i=0;i<data.length;i++){
            var areaname=data[i][0];
            var stationnum=data[i][1];
            t.stations.push(stationnum);
            t.stationNames.push(stationnum+"("+areaname+")");
            var strHtml="<div class='cpSation'><div id='"+stationnum+"' class='ycContent'></div></div>";
            $("#ybcpskda_div").append(strHtml);
        }
        GetData();
    }
    function addCharData(s,labels,ybtemp,ybprec,sktemp,skprec){
        $("#"+t.stations[s]).highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: t.stationNames[s]
            },
            xAxis: {
                categories: labels
            },
            /*yAxis: {
                max: 1,
                title:{
                    text:''
                }
            },*/
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0"></td>' +
                '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    groupPadding:0
                }
            },
            credits: {
                enabled: false
            },
            series: [{
                name: "气温(预报)",
                data:ybtemp
            },{
                name: "气温(实况)",
                data:sktemp
            },{
                name: "降水(预报)",
                data:ybprec
            },{
                name: "降水(实况)",
                data:skprec
            }]
        });
        $("#div_progress_title").html("数据请求完成!");
        $("#div_progress").fadeOut(2000,function(){});
    }
    //消除
    this.ClearPage=function(){
        $("#ybcp_div .cpSation").remove();
    }
    //初始化页面
    this.InitiPage=function(){
        /*--日期start--*/
        var date=new Date();
        date.addDays(-30);
        t.strDateTime=date.format("yyyy-MM-dd");
        /*--日期end--*/
        $("#div_legend").css("display","none");//隐藏lengend
        $("#menu").css("display","none");//隐藏menu
        $("#mapSwitch_div").css("display","none");//mapSwitch
        $("#YBCPDiv").remove();
    }
    //创建dom
    this.CreateDom=function(){
        $("#ybcpsk_div").remove();
        var strHtml="<div id='ybcpsk_div'><div id='ybcpskda_div'></div></div>";
        $("#workspace_div").append(strHtml);
    }
    //注册事件
    this.Register=function(){
        var isVaild=true;
        $(".navigation button").bind("click",function(){
            if(isVaild){
                $("#div_legend").css("display","block");
                $("#menu").css("display","block");
                $("#mapSwitch_div").css("display","block");
                t.ClearPage();
            }
            isVaild=false;
        });
    }
}
YBCPSKPageClass.prototype = new PageBase();
