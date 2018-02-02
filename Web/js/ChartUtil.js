/**
 * @author: wangkun
 * @date:   2017/4/19.
 * @description 绘图相关
 */
function ChartUtil() {
    this._init_();
}
ChartUtil.prototype = {
    constructor: ChartUtil,
    _init_: function () {
    },
    echart:null,
    /**
     * @author:wangkun
     * @date:2017-04-19
     * @param:divid
     * @return:
     * @description:初始化echart
     */
    initChart: function (divid) {
        this.echart = echarts.init(document.getElementById(divid));
        var option = {
            color: ['blue', 'green', 'pink', 'red'],
            title: {
                text: ''
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['逐日数据']
            },
            toolbox: {
                show: true,
                color: ['#1e90ff', '#22bb22', '#4b0082', '#d2691e'],
                feature: {
                    mark: {
                        show: true
                    },
                    dataView: {
                        show: true,
                        readOnly: false
                    },
                    magicType: {
                        show: true,
                        type: ['line', 'bar']
                    },
                    restore: {
                        show: true
                    },
                    saveAsImage: {
                        show: true
                    }
                }
            },
            calculable: true,
            xAxis: [{
                type: 'category',
                boundaryGap: false,
                data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
            }],
            yAxis: [{
                type: 'value',
                axisLabel: {
                    formatter: '{value} mm'
                }
            }],
            series: [{
                name: '逐日数据',
                type: 'bar',
                data: []
            }]
        };
        this.echart.setOption(option);
    }
}