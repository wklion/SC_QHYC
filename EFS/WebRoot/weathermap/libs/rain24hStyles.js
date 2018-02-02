/**
 * Created by allen_000 on 2015/7/15.
 * 24小时降水填色风格
 * 最小值，最大值，样式
 * 同时包含最大值和最小值，也即左开右闭
 */
    var rain24hStyles = [
    [0,0.1,{
        stroke: false,
        fill: false
}],
    [0.1,10.0,{
    strokeColor: "#ff0000",
    strokeWidth: 0.5,
    stroke:false,
    fillColor: "#A6F38D",
    fillOpacity: "0.5"
}],
    [10.0,25.0,{
    strokeColor: "#ff0000",
    strokeWidth: 0.5,
    stroke:false,
    fillColor: "#38A700",
    fillOpacity: "0.5"
}],
    [25.0,50.0,{
    strokeColor: "#ff0000",
    strokeWidth: 0.5,
    stroke:false,
    fillColor: "#61B8FF",
    fillOpacity: "0.5"
}],
    [50.0,100.0, {
    strokeColor: "#ff0000",
    strokeWidth: 0.5,
    stroke:false,
    fillColor: "#0000FE",
    fillOpacity: "0.5"
}],
    [100.0,250.0,{
    strokeColor: "#ff0000",
    strokeWidth: 0.5,
    stroke:false,
    fillColor: "#FA00FA",
    fillOpacity: "0.5"
}],
    [250.0, 1000, {
    strokeColor: "#ff0000",
    strokeWidth: 0.5,
    stroke:false,
    fillColor: "#720000",
    fillOpacity: "0.5"
}]];