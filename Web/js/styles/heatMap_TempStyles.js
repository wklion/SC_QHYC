/**
 * Created by allen_000 on 2015/7/15.
 * 气温填色风格
 * 开始值，结束值，开始颜色，结束颜色
 * 包含最小值，不包含最大值，也即左闭右开
 */
    var heatMap_TempStyles = [
    {start:-20,end:-16,caption:"-20~-16",startColor:{red:59,green:126,blue:219},endColor:{red:0,green:200,blue:255}},
    {start:-16,end:-12,caption:"-16~-12",startColor:{red:97,green:150,blue:224},endColor:{red:0,green:255,blue:247}},
    {start:-12,end:-8,caption:"-12~-8",startColor:{red:135,green:175,blue:229},endColor:{red:0,green:255,blue:179}},
    {start:-8,end:-4,caption:"-8~-4",startColor:{red:154,green:196,blue:220},endColor:{red:0,green:255,blue:115}},
    {start:-4,end:0,caption:"-4~0",startColor:{red:152,green:214,blue:196},endColor:{red:0,green:255,blue:47}},
    {start:0,end:4,caption:"0~4",startColor:{red:215,green:222,blue:126},endColor:{red:17,green:255,blue:0}},
    {start:4,end:8,caption:"4~8",startColor:{red:244,green:217,blue:99},endColor:{red:85,green:255,blue:0}},
    {start:8,end:12,caption:"8~12",startColor:{red:247,green:180,blue:45},endColor:{red:149,green:255,blue:0}},
    {start:12,end:16,caption:"12~16",startColor:{red:241,green:147,blue:3},endColor:{red:217,green:255,blue:0}},
    {start:16,end:20,caption:"16~20",startColor:{red:239,green:117,blue:17},endColor:{red:255,green:229,blue:0}},
    {start:20,end:24,caption:"20~24",startColor:{red:238,green:88,blue:31},endColor:{red:255,green:162,blue:0}},
    {start:24,end:28,caption:"24~28",startColor:{red:224,green:63,blue:22},endColor:{red:255,green:98,blue:0}},
    {start:28,end:32,caption:"28~32",startColor:{red:208,green:36,blue:14},endColor:{red:255,green:45,blue:0}},
    {start:32,end:34,caption:"32~34",startColor:{red:181,green:1,blue:9},endColor:{red:255,green:20,blue:0}},
    {start:34,end:36,caption:"34~36",startColor:{red:169,green:2,blue:16},endColor:{red:255,green:0,blue:0}},
    {start:36,end:38,caption:"36~38",startColor:{red:138,green:5,blue:25},endColor:{red:255,green:0,blue:0}},
    {start:38,end:45,caption:">38",startColor:{red:111,green:0,blue:21},endColor:{red:255,green:0,blue:0}}
].reverse();
var heatMap_TempStyles_month = [
    {start:32,end:45,caption:">32",startColor:{red:90,green:10,blue:10},endColor:{red:90,green:10,blue:10}},
    {start:28,end:32,caption:"28~32",startColor:{red:130,green:15,blue:15},endColor:{red:130,green:15,blue:15}},
    {start:24,end:28,caption:"24~28",startColor:{red:200,green:17,blue:17},endColor:{red:200,green:17,blue:17}},
    {start:20,end:24,caption:"20~24",startColor:{red:225,green:135,blue:70},endColor:{red:225,green:135,blue:70}},
    {start:16,end:20,caption:"16~20",startColor:{red:230,green:150,blue:60},endColor:{red:230,green:150,blue:60}},
    {start:12,end:16,caption:"12~16",startColor:{red:240,green:150,blue:110},endColor:{red:240,green:150,blue:110}},
    {start:8,end:12,caption:"8~12",startColor:{red:240,green:200,blue:50},endColor:{red:240,green:200,blue:50}},
    {start:4,end:8,caption:"4~8",startColor:{red:250,green:250,blue:80},endColor:{red:250,green:250,blue:80}},
    {start:0,end:4,caption:"0~4",startColor:{red:255,green:255,blue:180},endColor:{red:255,green:255,blue:180}},
    {start:-4,end:0,caption:"-4~0",startColor:{red:200,green:250,blue:250},endColor:{red:200,green:250,blue:250}},
    {start:-8,end:-4,caption:"-8~-4",startColor:{red:170,green:250,blue:250},endColor:{red:170,green:250,blue:250}},
    {start:-12,end:-8,caption:"-12~-8",startColor:{red:70,green:250,blue:250},endColor:{red:70,green:250,blue:250}},
    {start:-16,end:-12,caption:"-16~-12",startColor:{red:30,green:90,blue:230},endColor:{red:30,green:90,blue:230}},
    {start:-20,end:-16,caption:"-20~-16",startColor:{red:20,green:140,blue:230},endColor:{red:20,green:140,blue:230}},
    {start:-24,end:-20,caption:"-24~-20",startColor:{red:20,green:80,blue:220},endColor:{red:20,green:80,blue:220}},
    {start:-28,end:-24,caption:"-28~-24",startColor:{red:20,green:20,blue:200},endColor:{red:20,green:20,blue:200}},
    {start:-32,end:-28,caption:"-32~-28",startColor:{red:20,green:20,blue:130},endColor:{red:20,green:20,blue:130}},
    {start:-32,end:-40,caption:">-32",startColor:{red:20,green:20,blue:40},endColor:{red:20,green:20,blue:40}}
];