/**
 * Created by allen_000 on 2015/7/15.
 * 降水填色风格
 * 开始值，结束值，开始颜色，结束颜色
 * 包含最小值，不包含最大值，也即左闭右开
 */
    var heatMap_WStyles = [
    {start:0.0,end:0.0,caption:"晴",startColor:{red:255,green:255,blue:255},endColor:{red:0,green:85,blue:255}},
    {start:1.0,end:1.0,caption:"多云",startColor:{red:245,green:245,blue:245},endColor:{red:85,green:255,blue:0}},
    {start:2.0,end:2.0,caption:"阴",startColor:{red:235,green:235,blue:235},endColor:{red:255,green:85,blue:0}},
    {start:3.0,end:3.0,caption:"阵雨",startColor:{red:200,green:255,blue:200},endColor:{red:152,green:251,blue:152}},
    {start:4.0,end:4.0,caption:"雷阵雨",startColor:{red:170,green:253,blue:170},endColor:{red:152,green:251,blue:152}},
    {start:5.0,end:5.0,caption:"冰雹",startColor:{red:255,green:255,blue:0},endColor:{red:152,green:251,blue:152}},
    {start:7.0,end:7.0,caption:"小雨",startColor:{red:152,green:251,blue:152},endColor:{red:152,green:251,blue:152}},
    {start:8.0,end:8.0,caption:"中雨",startColor:{red:34,green:139,blue:34},endColor:{red:34,green:139,blue:34}},
    {start:9.0,end:9.0,caption:"大雨",startColor:{red:92,green:172,blue:238},endColor:{red:92,green:172,blue:238}},
    {start:10.0,end:10.0,caption:"暴雨",startColor:{red:0,green:0,blue:205},endColor:{red:0,green:0,blue:205}},
    {start:11.0,end:11.0,caption:"大暴雨",startColor:{red:238,green:0,blue:238},endColor:{red:238,green:0,blue:238}},
    {start:12.0,end:12.0,caption:"特大暴雨",startColor:{red:139,green:0,blue:0},endColor:{red:139,green:0,blue:0}},
    {start:6.0,end:6.0,caption:"雨夹雪",startColor:{red:255,green:190,blue:239},endColor:{red:152,green:251,blue:152}},
    {start:13.0,end:13.0,caption:"阵雪",startColor:{red:0,green:85,blue:255},endColor:{red:0,green:85,blue:255}},
    {start:14.0,end:14.0,caption:"小雪",startColor:{red:206,green:206,blue:206},endColor:{red:0,green:85,blue:255}},
    {start:15.0,end:15.0,caption:"中雪",startColor:{red:165,green:165,blue:165},endColor:{red:0,green:85,blue:255}},
    {start:16.0,end:16.0,caption:"大雪",startColor:{red:115,green:115,blue:115},endColor:{red:0,green:85,blue:255}},
    {start:17.0,end:17.0,caption:"暴雪",startColor:{red:74,green:74,blue:74},endColor:{red:0,green:85,blue:255}},
    {start:18.0,end:18.0,caption:"雾",startColor:{red:240,green:130,blue:40},endColor:{red:0,green:85,blue:255}},
    {start:53.0,end:53.0,caption:"霾",startColor:{red:60,green:0,blue:0},endColor:{red:0,green:85,blue:255}},
    {start:19.0,end:19.0,caption:"冻雨",startColor:{red:200,green:255,blue:255},endColor:{red:0,green:85,blue:255}},
    {start:20.0,end:20.0,caption:"沙尘暴",startColor:{red:255,green:100,blue:10},endColor:{red:0,green:85,blue:255}},
    {start:29.0,end:29.0,caption:"浮尘",startColor:{red:150,green:140,blue:20},endColor:{red:0,green:85,blue:255}},
    {start:30.0,end:30.0,caption:"扬沙",startColor:{red:240,green:180,blue:30},endColor:{red:0,green:85,blue:255}},
    {start:31.0,end:31.0,caption:"强沙尘暴",startColor:{red:220,green:10,blue:0},endColor:{red:0,green:85,blue:255}},
    {start:21.0,end:21.0,caption:"小到中雨",startColor:{red:90,green:180,blue:90},endColor:{red:0,green:85,blue:255}},
    {start:22.0,end:22.0,caption:"中到大雨",startColor:{red:60,green:150,blue:130},endColor:{red:0,green:85,blue:255}},
    {start:23.0,end:23.0,caption:"大到暴雨",startColor:{red:46,green:85,blue:220},endColor:{red:0,green:85,blue:255}},
    {start:24.0,end:24.0,caption:"暴雨到大暴雨",startColor:{red:170,green:0,blue:220},endColor:{red:0,green:85,blue:255}},
    {start:25.0,end:25.0,caption:"大暴雨到特大暴雨",startColor:{red:180,green:0,blue:120},endColor:{red:0,green:85,blue:255}},
    {start:26.0,end:26.0,caption:"小到中雪",startColor:{red:186,green:186,blue:186},endColor:{red:0,green:85,blue:255}},
    {start:27.0,end:27.0,caption:"中到大雪",startColor:{red:140,green:140,blue:140},endColor:{red:0,green:85,blue:255}},
    {start:28.0,end:28.0,caption:"大到暴雪",startColor:{red:95,green:95,blue:95},endColor:{red:0,green:85,blue:255}}
];