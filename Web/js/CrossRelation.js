/**
 * Created by zouwei on 2016/2/26.
 * 交叉关系
 */
var CrossRelation = [
    {src:"r12",target:"r3",reasonable:true},
    {src:"tmax",target:"2t",reasonable:true},
    {src:"tmin",target:"2t",reasonable:true},
    {src:"wmax",target:"10uv",reasonable:true},
    {src:"10uv",target:"wmax",reasonable:true},
    {src:"r3",target:"r12",reasonable:true},
    {src:"r3",target:"tcc",reasonable:true},
    {src:"2t",target:"tmax",reasonable:true},
    {src:"2t",target:"tmin",reasonable:true},
    {src:"tcc",target:"w",reasonable:true},
    {src:"r12",target:"w",reasonable:true}
];