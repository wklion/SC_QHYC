/**
 * @author:wangkun
 * @date:2017-03-12
 * @description:格点服务
 */
define(['Common', 'fileCacheUtil','layerManageUtil'], function (com, fcu,lmu) {
    var gridService = {
        dataCache: null,
		/**
		 * @author:wangkun
		 * @date:2017-03-30
		 * @param:elementid-要素id;elementname-要素名称
		 * @return:
		 * @description:获取格点
		 */
        GetGrid: function (elementid, elementname, maketime, hourspan, level, forcasttime, datatype) {
            var url = "";
            if (level == undefined || level === "") {
                level = 1000;
            }
            if (datatype === "dl") {
                url = gridServiceUrl + "services/TxtGridService/Get2HRH";
            }
            else {
                url = gridServiceUrl + "services/GridService/callModel";
            }
            var param = {
                element: elementid,
                level: level,
                hourspan: hourspan,
                maketime: maketime,
                datetime: forcasttime
            };
            var paramJson = JSON.stringify(param);
            var errText = "获取Grid失败!";
            com.AJAX(url, param, true, errText, function (data) {
                console.log(data);
            });
        },
        /**
		 * @author:wangkun
		 * @date:2017-03-30
		 * @param:elementid-要素id;elementname-要素名称;hourspans-时效(数组);dic-目录;fileformat-文件格式;datetime-时间;islast-是否最新
		 * @return:
		 * @description:获取文件格点
		 */
        downFileGrid: async function (pDic, url, param, elementID, hourspan, datetime,level) {
            var me = this;
            var content = await fcu.Get(pDic, datetime, elementID, hourspan,level);
            var data = null;
            var isLocal = false;
            if (content == null) {//下载数据
                var result = await me.downGrid(url, param);
             
                if(result.err == null){
                    data = result.suc;
                }
            }
            else {//解析数据
                data = JSON.parse(content);
                isLocal = true;
            }
            if(data==undefined){
                return null;
            }
            var datasetGrid = me.convertGridToDatasetGrid(elementID, data);
            if (!isLocal) {
                await fcu.AddFileAsync(pDic, datetime, elementID, hourspan, datasetGrid);
            }
            return datasetGrid;
        },
        downGrid: function (url, param) {
            return new Promise(function (resolve, reject) {
                com.AJAX(url, param, false, function (data) {
                    reject("err");
                }, function (data) {
                    resolve(data);
                });
            });
        },
        /**
		 * @author:wangkun
		 * @date:2017-03-30
		 * @param:elementid-要素id;elementname-要素名称;hourspans-时效(数组);dic-目录;fileformat-文件格式;datetime-时间;islast-是否最新
		 * @return:
		 * @description:获取文件格点
		 */
        convertGridToDatasetGrid: function (elementid, data) {
            var bWind = false;
            if (elementid === "10uv") {
                bWind = true;
            }
            var rows = data.rows;
            var cols = data.cols;
            var dvalues = null;
            if (data.dvalues == undefined) {
                dvalues = data.grid;
            }
            else {
                dvalues = data.dvalues;
            }
            var hasTag = (!bWind) && (dvalues.length == rows * cols * 2);
            var dimensions = (bWind || hasTag) ? 2 : 1; //维度，风场有两维；带有Tag属性也是两维
            var dMin = 9999;
            var dMax = -9999;
            var datasetGrid = new WeatherMap.DatasetGrid(data.left, data.top, data.right, data.bottom, rows, cols, bWind ? 2 : 1); //只有风是两要素
            datasetGrid.noDataValue = data.noDataValue;
            datasetGrid.nwpmodelTime = data.nwpmodelTime;
            var grid = [];
            var tag = [];
            for (var i = 0; i < rows; i++) {
                var tagLine = [];
                var nIndexLine = cols * i * dimensions;
                for (var j = 0; j < cols; j++) {
                    var nIndex = nIndexLine + j * dimensions;
                    var z;
                    if (bWind) {
                        z = dvalues[nIndex + 1];
                        grid.push(Math.round(dvalues[nIndex + 1])); //风速在前
                        grid.push(Math.round(dvalues[nIndex]));   //风向在后
                    }
                    else {
                        z = dvalues[nIndex];
                        grid.push(z);
                        if (hasTag)
                            tagLine.push(dvalues[nIndex + 1]);
                    }
                    if (z != 9999 && z != -9999) {
                        if (z < dMin)
                            dMin = z;
                        if (z > dMax)
                            dMax = z;
                    }
                    if (hasTag)
                        tag.push(tagLine);
                }
            }
            datasetGrid.grid = grid;
            datasetGrid.dMin = dMin;
            datasetGrid.dMax = dMax;
            if (hasTag) {
                datasetGrid.tag = tag;
                datasetGrid.defaultTag = 0;
            }
            return datasetGrid;
        },
        /**
		 * @author:wangkun
		 * @date:2017-03-30
		 * @param:elementid-要素id;elementname-要素名称;hourspans-时效(数组);dic-目录;fileformat-文件格式;datetime-时间;islast-是否最新
		 * @return:
		 * @description:创建格点数据
		 */
        failDown: async function (parantDir, elementid, elementname, hourspan, datetime) {
            var datasetgrid = com.createDatasetGrid(false);
            await fcu.AddFileAsync(parantDir, datetime, elementid, hourspan, datasetgrid, null);
        },
        /**
		 * @author:wangkun
		 * @date:2017-09-11
		 * @param:pDic-父目录;url-请求的url;param-参数;elementID-要素ID,hourspans-时效(数组);datetime-时间
		 * @return:
		 * @description:批量下载
		 */
        downGrids: async function (pDic, url, param, elementID, hourspans, datetime) {
            var me = this;
            $('.loading').css("display", "block");
            $('.loading').shCircleLoader({
                dots: 24,
                dotsRadius: 5,
                color: "red"
            });
            var jpFileFormat = param.jpFile;
            for (var i = 0, size = hourspans.length; i < size; i++) {
                var content = await fcu.Get(pDic, datetime, elementID, hourspans[i]);
                var data = null;
                if (content == null) {//下载数据
                    param.hourspan = i + 1;
                    param.jpFile = jpFileFormat.replace("hourspan",param.hourspan);
                    var strParam = JSON.stringify(param);
                    var result = await me.downGrid(url, strParam);
                    if(result.err == null){
                        data = result.suc;
                    }
                    var datasetGrid = me.convertGridToDatasetGrid(elementID, data);
                    await fcu.AddFileAsync(pDic, datetime, elementID, hourspans[i], datasetGrid);
                }
                var process = parseInt(((i + 1) / size) * 100);
                $('.loading').shCircleLoader('progress', process + "%");
            }
            $('.loading').shCircleLoader('progress', "提交完成!");
            $('.loading').hide("slow");
        },
        /**
		 * @author:wangkun
		 * @date:2017-09-18
		 * @param:data-点数据
		 * @return:
		 * @description:点插值显示
		 */
        displayByIDW:function(elementID,elementName,data,style){
            var features=[];
            data.forEach((item,i)=>{
				let lon=item.longitude==undefined?item.lon:item.longitude;
				let lat=item.latitude==undefined?item.lat:item.latitude;
				let sna=item.stationName;
				let snu=item.stationNum;
				let val=item.value;
				let pt=new WeatherMap.Geometry.Point(lon,lat);
				let attr={stationname:sna,stationnum:snu,value:val};
				var feature = new WeatherMap.Feature.Vector(pt,attr,null);
				features.push(feature);
            });
            let interpolate = new Interpolate();
            let datasetGrid = interpolate.run(features, "value", {left:78,bottom:21,right:110,top:36},0.25,0.25);
            var layer = lmu.addLayer(elementName,null,null,"grid");
            layer.items = style;
            layer.setDatasetGrid(datasetGrid);
        	layer.refresh();
        },
        CalContour:function(datasetGrid,features,style){

        },
        /**
         * @author:wangkun
         * @date:2017-12-5
         * @modifydate:
         * @param:
         * @return:
         * @description:插值
         */
        interpolate:function(stationData){
            var features=[];
            stationData.forEach(item=>{
                let lon=item.longitude==undefined?item.lon:item.longitude;
				let lat=item.latitude==undefined?item.lat:item.latitude;
                let val=item.value;
                let pt=new WeatherMap.Geometry.Point(lon,lat);
                let attr={value:val};
                let feature = new WeatherMap.Feature.Vector(pt,attr,null);
                features.push(feature);
            });
            let interpolate = new Interpolate();
            let datasetGrid = interpolate.run(features, "value", {left:78,bottom:21,right:110,top:36},0.5,0.5);
            return datasetGrid;
        }
    };
    return gridService;
});