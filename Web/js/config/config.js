var MAP_CONFIG={
    SC:{Code:51,
        RectangleBound:[96.5,25.5,109.0,34.5]
    },
    XN:{Code:51,
        RectangleBound:[78.0,20.5,110.5,37.0]
    },
};
var debug = true;//部署的时候关闭
var host = "http://"+window.location.host;;
var Url_Config={
    gridServiceUrl:host+"/WMGridService/",
    userServiceUrl:host+"/WMUser/",
    dataSericeUrl:host+"/WMDataService/"
}
var Physics_Config={
    modelResDir:"E:/SC/Data/",
    modeJPResDir:"E:/SC/Data/Mode/",
    modeHgtResOfMonthDir:"E:/SC/Data/Height/month/",
    tempDir:"C:/Users/wklion/Desktop/temp/efs/",
    uvDir:"E:/SC/Data/UV/",
    derfDir:"E:/SC/Data/Derf/"
}