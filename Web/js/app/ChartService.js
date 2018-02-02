define(['jquery','echarts','datatool'],function($,ec,dt){
    var chart=null;
    var colors=["red","blue","green","black"];
    return {
        init:function(){
            if(chart!=null)
                return;
            var chartcontrol=document.getElementById("chart");
            if(chartcontrol==undefined)
                return;
            chart = ec.init(chartcontrol);
            chart.setOption({
                title: {
                    text: 'MGDemo'
                },
                legend:{
                    data:[]
                },
                toolbox: {
                    show : true,
                    feature: {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        magicType: {show: true, type: ['line', 'bar']},
                        restore : {show: true},
                        saveAsImage: {show: true}
                    }
                },
                tooltip: {},
                legend: {
                    data:[]
                },
                xAxis: [],
                yAxis: [],
                series: []
            });
        },
        initmap:function(){
            var chartcontrol=document.getElementById("chart");
            if(chartcontrol==undefined)
                return;
            $.get('js/map/china.json',function(mapjson){
                ec.registerMap('china', mapjson);
                chart = ec.init(chartcontrol);
                chart.setOption({
                    series: [{
                        type: 'map',
                        map: 'china'
                    }]
                });
            })
        },
        //增加数据
        adddata:function(name,data,label,danwei){
            var option=chart.getOption();
            var dataSize=option.series.length;
            option.series.push({
                type:'bar',
                name:name,
                data:data,
                itemStyle:{
                    normal:{
                        color:colors[dataSize]
                    }
                }
            });
            var xAxisLen=option.xAxis.length;
            option.xAxis[xAxisLen-1].data=label;
            option.legend[0].data.push({
                name:name
            });
            let yAxis={
                type:'value',
                axisLabel:{
                    formatter:'{value}'+danwei
                }
            };
            option.yAxis[0]=yAxis;
            chart.setOption(option);
        },
        //name：数据名称
        //data:数据
        //label:x轴值
        //danwei：单位
        updatedata:function(name,data,label,danwei,title){//覆盖数据
            var option=chart.getOption();
            option.title[0].text=title;
            option.series.length=0;
            chart.clear();
            let xAxis={data:label};
            option.xAxis[0]=xAxis;
            let yAxis={
                type:'value',
                axisLabel:{
                    formatter:'{value}'+danwei
                }
            };
            option.yAxis[0]=yAxis;
            option.series.push({
                type:'bar',
                name:name,
                data:data,
                itemStyle:{
                    normal:{
                        color:colors[0]
                    }
                }
            });
            option.legend[0].data[0]={
                name:name
            };
            chart.setOption(option);
        },
        viewmap:function(option){
            chart.setOption(option);
        }
    }
})