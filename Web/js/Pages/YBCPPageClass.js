/**
 * @author wangkun
 * 2016-05-10
 * @description 系统测评
 */

function YBCPPageClass() {
    var t = this;
    t.ElementID = null;
    t.stations = null;
    t.echarts = null;
    t.stationData_Vue = null,//站点Vue
        //渲染左侧菜单区域里的按钮
        this.renderMenu = function () {
            $("#div_legend").css("display", "none");//隐藏lengend
            var htmlStr = "<div id='dateControl' style='height: 30px;width: 100%;margin-left: 0px;'><p style='float: left;width: 60px;height: 30px;text-align: center;padding-top: 15px;'>日期</p><div id='dateSelect' style='margin: 10px 3px 0px 0px;float: left;width: 140px;height: 26px;'></div></div>";

            $("#menu_bd").html(htmlStr);
            GDYB.GridProductClass.init();
            GDYB.GridProductClass.currentUserArea = $.cookie("departCode");
            GDYB.GridProductClass.layerFocusArea.visibility = true;
            GDYB.GridProductClass.showFocusArea();
            t.myDateSelecter = new DateSelecter(2, 2, "yyyy-mm-dd");
            var myDate = new Date();
            myDate.addDays(-31);
            t.myDateSelecter.setCurrentTime(myDate.format("yyyy-MM-dd"));
            t.myDateSelecter.intervalMinutes = 60 * 24; //24小时
            $("#dateSelect").html(t.myDateSelecter.div);
            $("#dateSelect").find("input").css("width", "90px");
            ClearPage();//先清除
            var htmlYBCP = "<div id='YBCPDiv' style='background-color: rgb(255, 255, 255);height: 100%;right: 0px;left:400px;position: absolute;'></div>";
            $("#workspace_div").append(htmlYBCP);
            //区域
            var htmlArea = "<div id='AreaDiv'></div>"
            $("#YBCPDiv").append(htmlArea);
            htmlArea = "<ul><li>盆地西北部</li><li>盆地南部</li><li>攀西地区</li><li>盆地中部</li><li>盆地西南部</li><li>盆地东北部</li><li>川西高原</li></ul>";
            $("#AreaDiv").append(htmlArea);
            //内容
            var htmlContent = "<div id='ContentDiv'></div>";
            $("#YBCPDiv").append(htmlContent);
            $("#ContentDiv").css("height", parseInt($("#YBCPDiv").css("height")) - 40);
            $("#AreaDiv li").click(function () {
                $("#AreaDiv li.active").removeClass();
                $(this).addClass("active");
                t.liClick(this);
            });
            var isVaild = true;
            $(".navigation button").bind("click", function () {
                if (isVaild) {
                    $("#div_legend").css("display", "block");
                    ClearPage();
                }
                isVaild = false;
            })
            //时间控件更新
            $("#dateSelect").find("input").bind("change", function () {
                getData();
            });
            $("#dateSelect").find("img").bind("click", function () {
                getData();
            });

            //初始化图表位置
            $("#ContentDiv").html(`
                <div v-for="item in stationData" class="chartItem" :id="item.stationNum"></div>
            `);

            initVue();
            function initVue() {
                t.stationData_Vue = new Vue({
                    el: '#ContentDiv',
                    data: {
                        stationData: [{ stationName: "测试" }]
                    },
                    methods:{
                        reflash:async function(){
                            console.log("tt");
                            await this.$nextTick();
                        }
                    }
                });
            }
            initEvent();
            function initEvent() {
                $("#AreaDiv li:first").click();
            }
            //initChart();//初始化图表(echart)

            function initChart() {
                t.echarts = echarts.init(document.getElementById('ContentDiv'));
                // 指定图表的配置项和数据
                var option = {
                    title: {
                        text: 'ZsCz检验'
                    },
                    tooltip: {},
                    legend: {
                        data: ['ZsCz']
                    },
                    xAxis: {
                        data: []
                    },
                    yAxis: {},
                    series: [{ name: 'zs(气温)', type: 'bar', data: [] },
                    { name: 'cs(气温)', type: 'bar', data: [] },
                    { name: 'zs(降水)', type: 'bar', data: [] },
                    { name: 'zs(降水)', type: 'bar', data: [] }
                    ]
                };
                t.echarts.setOption(option);
            }

        }// rendMenu end
    this.liClick = function (object) {
        var blockName = $(object).text();
        var stationData = updateStation(blockName);
        t.stationData_Vue.stationData = stationData;
        t.stationData_Vue.$nextTick(function(){
            t.initChart();
            //获取数据
            var zscsData = getData();
            t.updateChart(zscsData);
        });
    }
    this.initChart = function () {
        var option = {
            title: {
                text: ''
            },
            tooltip: {},
            legend: {
                data: ['zs(气温)','cs(气温)','zs(降水)','cs(降水)']
            },
            xAxis: {
                data: []
            },
            yAxis: {},
            series: [{ name: 'zs(气温)', type: 'bar', data: [] },
            { name: 'cs(气温)', type: 'bar', data: [] },
            { name: 'zs(降水)', type: 'bar', data: [] },
            { name: 'cs(降水)', type: 'bar', data: [] }
            ]
        };
        var data = t.stationData_Vue.stationData;
        var size = data.length;
        for (var i = 0; i < size; i++) {
            var item = data[i];
            var chart = echarts.init(document.getElementById(item.stationNum));
            option.title.text = item.stationName+"("+item.stationNum+")";
            chart.setOption(option);
        }
    }
    function getData() {
        var areaname = $("#AreaDiv li.active")[0].innerHTML;
        var strEndTime = $("#dateSelect").find("input").val();//日期
        strEndTime = strEndTime.replace(/-/g, "");
        var url = Url_Config.gridServiceUrl + "services/GridService/getZSCS";
        var param = {
            areaname: areaname,
            endtime: strEndTime
        };
        param = JSON.stringify(param);
        var data = AJAXSync(url, param);
        return data;
    }
    this.updateChart = function(data){
        //数据处理
        var stationData = new Map();
        var size = data.length;
        for(var i=0;i<size;i++){
            var item = data[i];
            var stationNum = item.stationnum;
            var vals = stationData.get(stationNum);
            if(vals==undefined||vals==null){//空创建
                vals = {};
                var zsTVals = [];
                var csTVals = [];
                var zsRVals = [];
                var csRVals = [];
                var date = [];
                zsTVals.push(item.tzs);
                csTVals.push(item.tcs);
                zsRVals.push(item.rzs);
                csRVals.push(item.rcs);
                date.push(item.publictime);
                vals.zsTVals = zsTVals;
                vals.csTVals = csTVals;
                vals.zsRVals = zsRVals;
                vals.csRVals = csRVals;
                vals.date = date;
                stationData.set(stationNum,vals);
            }   
            else{//增加
                vals.zsTVals.push(item.tzs);
                vals.csTVals.push(item.tcs);
                vals.zsRVals.push(item.rzs);
                vals.csRVals.push(item.rcs);
                vals.date.push(item.publictime);
            }
        }
        //刷新
        for(var key of stationData.keys()){
            var chart = echarts.getInstanceByDom(document.getElementById(key));
            var option = chart.getOption();
            var vals = stationData.get(key);
            option.xAxis[0].data =  vals.date;
            option.series[0].data = vals.zsTVals;
            option.series[1].data = vals.csTVals;
            option.series[2].data = vals.zsRVals;
            option.series[3].data = vals.csRVals;
            chart.setOption(option);
        }
    }
    function addCharData(stationnum, labels, tzss, tcss, rzss, rcss) {
        $("#" + stationnum).highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: stationnum
            },
            xAxis: {
                categories: labels
            },
            yAxis: {
                max: 1,
                title: {
                    text: ''
                }
            },
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
                    groupPadding: 0
                }
            },
            credits: {
                enabled: false
            },
            series: [{
                name: "zs",
                data: tzss
            }, {
                name: "cs",
                data: tcss
            }, {
                name: "zs",
                data: rzss
            }, {
                name: "cs",
                data: rcss
            }]
        });
    }
    function ClearPage() {
        $("#YBCPDiv").remove();
    }
    function updateStation(blockName) {
        var url = Url_Config.gridServiceUrl + "services/StationService/getStationByBlock";
        var param = {
            blockname: blockName
        };
        param = JSON.stringify(param);
        var data = AJAXSync(url, param);
        return data;
    }
}
YBCPPageClass.prototype = new PageBase();
