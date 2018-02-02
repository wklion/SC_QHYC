/*
* 图表类
* by zouwei, 2015-05-10
* */
function ChartClass() {}
ChartClass.prototype.lineChartData = {
    labels : ["1h","2h","3h","4h","5h","6h","7h","8h","9h","10h","11h","12h","13h","14h","15h","16h","17h","18h","19h","20h","21h","22h","23h","24h","36h","48h","60h","72h","84h","96h","108h","120h","132h","144h","156h","168h"],
    datasets : [
        {
            fillColor : "rgba(151,187,205,0.5)",
            strokeColor : "rgba(151,187,205,1)",
            pointColor : "rgba(151,187,205,1)",
            pointStrokeColor : "#fff",
            data : [18,28,20,17,22,30,24,25,18,28,20,17,22,30,24,25,18,28,20,17,22,30,24,25,18,28,20,17,22,30,24,25,18,28,20,17,22,30,24,25,18,28,20,17,22,30,24,25,18,28,20,17,22,30,24,25,18,28,20,17,22,30,24,25]
        }
    ]

};
ChartClass.prototype.myLineChart = null;
//显示图表
ChartClass.prototype.displayChart = function(labels,dataset){
    var t  = this;
    var ctx=document.getElementById("canvas").getContext("2d");
    var config = {
        type: 'line',
        data: {
            labels:labels,
            datasets: [{
                label: "",
                data: dataset,
                fill: false,
                borderColor: "rgba(151,187,205,0.5)"
            }]
        }
    };
    t.myLineChart = new Chart(ctx, config);

//    function OnValueChange(lineIndex, nodeIndex, newval, oldval) {
//        document.getElementById(nodeIndex).value = t.lineChartData.datasets[lineIndex].data[nodeIndex];
//    }

//    t.myLineChart.registerValueChangedHandler(OnValueChange);
};
//刷新图表
ChartClass.prototype.refreshChart = function(id, datas){
    this.lineChartData.datasets[0].data[id] = datas;
    this.myLineChart.Refresh();
};