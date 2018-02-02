/**
 * Created by allen_000 on 2015/7/15.
 * 降水填色风格
 * 开始值，结束值，开始颜色，结束颜色
 * 包含最小值，不包含最大值，也即左闭右开
 */
    var heatMap_Rain24Styles = [
    {start:0.1,end:10.0,caption:"小雨",startColor:{red:166,green:242,blue:142},endColor:{red:166,green:242,blue:142}},
    {start:10.0,end:25.0,caption:"中雨",startColor:{red:60,green:186,blue:60},endColor:{red:60,green:186,blue:60}},
    {start:25.0,end:50.0,caption:"大雨",startColor:{red:97,green:184,blue:255},endColor:{red:97,green:184,blue:255}},
    {start:50.0,end:100.0,caption:"暴雨",startColor:{red:0,green:2,blue:226},endColor:{red:0,green:2,blue:226}},
    {start:100.0,end:250.0,caption:"大暴雨",startColor:{red:250,green:0,blue:250},endColor:{red:250,green:0,blue:25}},
    {start:250.0,end:500.0,caption:"特大暴雨",startColor:{red:127,green:1,blue:64},endColor:{red:127,green:1,blue:64}}
].reverse();