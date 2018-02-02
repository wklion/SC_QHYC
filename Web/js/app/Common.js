/**
 * @author:杠上花
 * @date:2017-03-09
 * @param:
 * @return:
 * @description:公共模块
 */
define(['mapUtil', 'layerManageUtil'], function (mu, lmu) {
    return {
        cDateTime: null,
		/**
		 * @author:杠上花
		 * @date:2017-03-09
		 * @param:url-url,paramdata-参数，ft-(true:异步)
		 * @return:
		 * @description:AJAX
		 */
        AJAX: function (url, paramdata, ft, errorCallback, successCallback) {
            $.ajax({
                type: 'post',
                url: url,
                async: ft,
                data: {
                    'para': paramdata
                },
                dataType: 'json',
                error: function () {
                    $.isFunction(errorCallback) && errorCallback.call(null); //alter by pope on 20170724
                },
                success: function (data) {
                    $.isFunction(successCallback) && successCallback.call(null, data); //alter by pope on 20170724
                }
            });
        },
		/**
		 * @author:杠上花
		 * @date:2017-03-12
		 * @param:
		 * @return:
		 * @description:初始化时间
		 */
        InitDateTime: function (id, addMinite) {
            var me = this;
            if (addMinite == undefined || !addMinite) {
                me.cDateTime = new DateSelecter(0, 0);
            }
            else {
                me.cDateTime = new DateSelecter(0, 0, undefined, addMinite); //最小视图为天
            }
            me.cDateTime.setIntervalMinutes(6);
            $("#" + id).append(me.cDateTime.div);
            return me.cDateTime;
        },
		/**
		 * @author:杠上花
		 * @date:2017-04-04
		 * @param:
		 * @return:
		 * @description:关闭窗口
		 */
        closeWindow: function () {
            var parent = $(this).parent();
            if (parent[0].id != "") {
                parent.css("display", "none");
            } else {
                parent.parent().css("display", "none");
            }
        },
		/**
		 * @author:杠上花
		 * @date:2017-04-20
		 * @param:id-id
		 * @return:
		 * @description:显示图例
		 */
        displayLegend: function (id, strDateTime) {
            var me = this;
            require(['mullegend'], function (ml) {
                var style = me.getStyleByElement(id);
                if (style == null) {
                    return;
                }
                var name = me.getNameByElement(id);
                if (name == null) {
                    return;
                }
                ml.add(name, style, strDateTime);
            });
        },
        drawRing: function (id, w, h, val) {
            //先创建一个canvas画布对象，设置宽高  
            var c = document.getElementById(id);
            var ctx = c.getContext('2d');
            ctx.canvas.width = w;
            ctx.canvas.height = h;
            ctx.clearRect(0, 0, w, h);//清除
            //圆环有两部分组成，底部灰色完整的环，根据百分比变化的环  
            //先绘制底部完整的环  
            //起始一条路径  
            ctx.beginPath();
            //设置当前线条的宽度  
            ctx.lineWidth = 10;//10px  
            //设置笔触的颜色  
            ctx.strokeStyle = '#CCCCCC';
            //arc()方法创建弧/曲线（用于创建圆或部分圆）arc(圆心x,圆心y,半径,开始角度,结束角度)  
            ctx.arc(w / 2, h / 2, w / 2 - 10, 0, 2 * Math.PI);
            //绘制已定义的路径  
            ctx.stroke();

            //绘制根据百分比变动的环  
            ctx.beginPath();
            ctx.lineWidth = 10;
            ctx.strokeStyle = '#d54540';
            //设置开始处为0点钟方向（-90*Math.PI/180）  
            //x为百分比值（0-100）
            ctx.arc(w / 2, h / 2, w / 2 - 10, -90 * Math.PI / 180, (val * 3.6 - 90) * Math.PI / 180);
            ctx.stroke();
            //绘制中间的文字  
            ctx.font = '20px Arial';
            ctx.fillStyle = '#747474';
            ctx.textBaseline = 'middle';
            ctx.textAlign = 'center';
            ctx.fillText(val + '%', w / 2, h / 2);
        },
        /**
		 * @author:杠上花
		 * @date:2017-08-16
		 * @param:isTemp-是否临时的
		 * @return:datasetGrid
		 * @description:创建临时缓存文件
		 */
        createDatasetGrid: function () {
            var bounds = sysConfig.gridBound;//配置文件中
            var datasetGrid = new WeatherMap.DatasetGrid(bounds[0], bounds[3], bounds[2], bounds[1], bounds[4], bounds[5], 1);
            var noVal = -9999;
            datasetGrid.noDataValue = noVal;
            var grid = [];
            for (var i = 0; i < bounds[4]; i++) {
                for (var j = 0; j < bounds[5]; j++) {
                    grid.push(noVal);
                }
            }
            datasetGrid.grid = grid;
            datasetGrid.dMin = noVal;
            datasetGrid.dMax = noVal;
            return datasetGrid;
        },
        /**
		 * @author:杠上花
		 * @date:2017-07-31
		 * @param:datetime-日期(yyyy-MM-dd hh:mm:ss);signaltype-类型(lightning)
		 * @return:
		 * @description:更新datasetgrid
		 */
        updateDatasetGrid: function (dg, gr, value) {
            var me = this;
            var bounds = gr.bounds;
            var nLeft = (bounds.left - dg.left) / dg.deltaX;
            if (nLeft < 0)
                nLeft = 0;
            else if (nLeft >= dg.cols)
                return;
            else
                nLeft = Math.floor(nLeft);
            var nTop = (dg.top - bounds.top) / dg.deltaX;
            if (nTop < 0)
                nTop = 0;
            else if (nTop >= dg.rows)
                return;
            else
                nTop = Math.floor(nTop);

            var nRight = (bounds.right - dg.left) / dg.deltaX;
            if (nRight <= 0)
                return;
            else if (nRight >= dg.cols)
                nRight = dg.cols - 1;
            else
                nRight = Math.floor(nRight);

            var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
            if (nBottom < 0)
                return;
            else if (nBottom >= dg.rows)
                nBottom = dg.rows - 1;
            else
                nBottom = Math.floor(nBottom);

            for (var i = nTop; i <= nBottom; i++) {
                var pt1 = dg.gridToXY(0, i);
                var pt2 = dg.gridToXY(dg.cols - 1, i);
                var pts = me.getIntersections(pt1, pt2, gr);
                var arrayX = [];
                for (var j = 0; j < pts.length; j++) {
                    var x = (pts[j].x - dg.left) / dg.deltaX;
                    if (arrayX.indexOf(x) >= 0)
                        continue;
                    arrayX.push(x);
                }
                if (arrayX.length == 0)
                    continue;
                var arrayXSort = arrayX.sort(sortNumber); //从小到大排序
                for (var j = nLeft; j <= nRight; j++) {
                    if ((j + 0.5) < arrayXSort[0])
                        continue;
                    var k = arrayXSort.length - 1;
                    for (k; k >= 0; k--) {
                        if ((j + 0.5) >= arrayXSort[k]) {
                            break;
                        }
                    }
                    if (k % 2 != 0)
                        continue;
                    dg.setValue(1, j, i, value);
                }
            }
            function sortNumber(a, b) {
                return a - b;
            }
        },
        getIntersections: function (pt1, pt2, geo) {
            var me = this;
            var result = [];
            var lineString = null;
            if (geo.CLASS_NAME == "WeatherMap.Geometry.LineString" || geo.CLASS_NAME === "WeatherMap.Geometry.LinearRing")
                lineString = geo;
            else if (geo.CLASS_NAME == "WeatherMap.Geometry.Polygon")
                lineString = geo.components[0];
            if (lineString == null)
                return null;

            var pts = lineString.components;
            for (var i = 1; i < pts.length; i++) {
                var pt3 = pts[i - 1];
                var pt4 = pts[i];
                var intersection = null;
                if (pt1.y == pt2.y)
                    intersection = me.calcIntersectionWithHorizontalSegment(pt1.y, pt3, pt4);
                else if (pt1.x == pt2.x)
                    intersection = me.calcIntersectionWithVerticalSegment(pt1.x, pt3, pt4);
                else
                    intersection = me.calcIntersection(pt1, pt2, pt3, pt4);
                if (intersection == null)
                    continue;
                result.push(intersection);
            }
            return result;
        },
        /**
		 * @author:杠上花
		 * @date:2017-08-16
		 * @param:y:纵线y坐标;pt1:线段端点1；pt2:线段端点2;
		 * @return:
		 * @description:交点数组
		 */
        calcIntersectionWithHorizontalSegment: function (y, pt1, pt2) {
            var result = null;
            if ((pt1.y > y && pt2.y > y) || (pt1.y < y && pt2.y < y))
                return result;
            return { x: pt2.x - (pt2.y - y) / (pt2.y - pt1.y) * (pt2.x - pt1.x), y: y };
        },
        /**
		 * @author:杠上花
		 * @date:2017-08-16
		 * @param:x:纵线x坐标;pt1:线段端点1；pt2:线段端点2;
		 * @return:
		 * @description:交点数组
		 */
        calcIntersectionWithVerticalSegment: function (x, pt1, pt2) {
            var result = null;
            if ((pt1.x > x && pt2.x > x) || (pt1.x < x && pt2.x < x))
                return result;
            return { x: x, y: (x - pt1.x) / (pt2.x - pt1.x) * (pt2.y - pt1.y) + pt1.y };
        },
        /**
		 * @author:杠上花
		 * @date:2017-08-16
		 * @param:
		 * @return:
		 * @description:计算线段交点
		 */
        calcIntersection: function (a, b, c, d) {
            var result = null;
            // 三角形abc 面积的2倍
            var area_abc = (a.x - c.x) * (b.y - c.y) - (a.y - c.y) * (b.x - c.x);
            // 三角形abd 面积的2倍
            var area_abd = (a.x - d.x) * (b.y - d.y) - (a.y - d.y) * (b.x - d.x);
            // 面积符号相同则两点在线段同侧,不相交 (对点在线段上的情况,本例当作不相交处理);
            if (area_abc * area_abd >= 0) {
                return result;
            }
            // 三角形cda 面积的2倍
            var area_cda = (c.x - a.x) * (d.y - a.y) - (c.y - a.y) * (d.x - a.x);
            // 三角形cdb 面积的2倍
            // 注意: 这里有一个小优化.不需要再用公式计算面积,而是通过已知的三个面积加减得出.
            var area_cdb = area_cda + area_abc - area_abd;
            if (area_cda * area_cdb >= 0) {
                return result;
            }
            //计算交点坐标
            var t = area_cda / (area_abd - area_abc);
            var dx = t * (b.x - a.x),
                dy = t * (b.y - a.y);
            result = { x: a.x + dx, y: a.y + dy };
            return result;
        },
        /**
		 * @author:杠上花
		 * @date:2017-08-16
		 * @param:
		 * @return:
		 * @description:计算极值
		 */
        calExtreme: function (datasetGrid) {
            var rows = datasetGrid.rows;
            var cols = datasetGrid.cols;
            var grid = datasetGrid.grid;
            var max = grid[0];
            var min = grid[0];
            for (var r = 0; r < rows; r++) {
                for (var c = 0; c < cols; c++) {
                    var index = r * cols + c;
                    var val = grid[index];
                    if (val > max) {
                        max = val;
                    }
                    if (val < min) {
                        min = val;
                    }
                }
            }
            datasetGrid.dMax = max;
            datasetGrid.dMin = min;
        },
        /**
		 * @author:杠上花
		 * @date:2017-08-17
		 * @param:
		 * @return:
		 * @description:datasetgrid清除
		 */
        clearDatasetGrid: function (datasetGrid) {
            var rows = datasetGrid.rows;
            var cols = datasetGrid.cols;
            var noVal = datasetGrid.noDataValue;
            var grid = [];
            for (var i = 0; i < rows; i++) {
                for (var j = 0; j < cols; j++) {
                    grid.push(noVal);
                }
            }
            datasetGrid.grid = grid;
            datasetGrid.dMin = noVal;
            datasetGrid.dMax = noVal;
        },
        /**
		 * @author:杠上花
		 * @date:2017-09-04
		 * @param:strDateTime-日期,name-名称
		 * @return:
		 * @description:添加标题
		 */
        addTitle: function (strDateTime, name) {
            var obj = {
                datetime: strDateTime,
                name: name
            };
            vueTitle.titles.push(obj);
        },
        /**
		 * @author:杠上花
		 * @date:2017-09-04
		 * @param:name-名称
		 * @return:
		 * @description:移除标题
		 */
        removeTitle: function (name) {
            var me = this;
            var titles = vueTitle.titles;
            for (var i = 0, size = titles.length; i < size; i++) {
                var item = titles[i];
                if (item.name === name) {
                    titles.removeByIndex(i);
                }
                size = titles.length;
            }
        },
        contain: function (dg, gr, dvalue) {
            var result = false;
            if (dg == null || gr == null)
                return result;
            var bounds = gr.bounds;
            var nLeft = (bounds.left - dg.left) / dg.deltaX;
            if (nLeft < 0)
                nLeft = 0;
            else if (nLeft >= dg.cols)
                return;
            else
                nLeft = Math.floor(nLeft);
            var nTop = (dg.top - bounds.top) / dg.deltaX;
            if (nTop < 0)
                nTop = 0;
            else if (nTop >= dg.rows)
                return;
            else
                nTop = Math.floor(nTop);
            var nRight = (bounds.right - dg.left) / dg.deltaX;
            if (nRight <= 0)
                return;
            else if (nRight >= dg.cols)
                nRight = dg.cols - 1;
            else
                nRight = Math.floor(nRight);

            var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
            if (nBottom < 0)
                return;
            else if (nBottom >= dg.rows)
                nBottom = dg.rows - 1;
            else
                nBottom = Math.floor(nBottom);

            function sortNumber(a, b) {
                return a - b;
            }
            for (var i = nTop; i <= nBottom; i++) {
                var pt1 = dg.gridToXY(0, i);
                var pt2 = dg.gridToXY(dg.cols - 1, i);
                var pts = this.getIntersections(pt1, pt2, gr);
                var arrayX = [];
                for (var j = 0; j < pts.length; j++) {
                    arrayX.push((pts[j].x - dg.left) / dg.deltaX);
                }
                if (arrayX.length == 0)
                    continue;
                var arrayXSort = arrayX.sort(sortNumber); //从小到大排序
                for (var j = nLeft; j <= nRight; j++) {
                    if (j < arrayXSort[0])
                        continue;
                    var k = arrayXSort.length - 1;
                    for (k; k >= 0; k--) {
                        if (j >= arrayXSort[k]) {
                            break;
                        }
                    }
                    if (k % 2 != 0)
                        continue;

                    if (dg.getValue(0, j, i) == dvalue) {
                        result = true;
                        break;
                    }
                }
                if (result)
                    break;
            }
            return result;
        },
        /**
         * @author:杠上花
         * @date:2017-11-12
         * @modifydate:
         * @param:
         * @return:
         * @description:获取所有站点数据
         */
        getAllStation:function(suc,err){
            var me = this;
            var url = Url_Config.gridServiceUrl+"services/StationService/GetXNStation";
            me.AJAX(url,"",true,err,suc);
        },
        /**
         * @author:杠上花
         * @date:2017-12-23
         * @modifydate:
         * @param:
         * @return:
         * @description:获取所有站点数据
         */
        getAllStationAsync:function(){
            var me = this;
            var url = Url_Config.gridServiceUrl+"services/StationService/GetXNStation";
            var pro = new Promise(function(resolve,reject){
                me.AJAX(url,"",true,resolve,resolve);
            });
            return pro;
        },
        /**
         * @author:杠上花
         * @date:2017-12-11
         * @modifydate:
         * @param:
         * @return:
         * @description:获取月距平数据
         */
        getJPMonthData:function(flag){
            var me = this;
            var url = Url_Config.gridServiceUrl+"services/ForcastService/getMonthAvg";
            var param = {
                flag:flag
            };
            param = JSON.stringify(param);
            var pro = new Promise(function (resolve, reject) {
                me.AJAX(url, param, false, function(){
                    resolve("err");
                }, function(data){
                    resolve(data);
                });
            });
            return pro;
        },
        /**
         * @author:杠上花
         * @date:2017-12-11
         * @modifydate:
         * @param:
         * @return:
         * @description:站点数据转Map
         */
        convertStationDataToMap:function(data){
            var map = new Map();
            data.forEach(item=>{
                var sn = item.stationnum;
                map.set(sn,item);
            });
            return map;
        },
        getMonthStationData:function(data,month){
            var val = 0;
            switch(month){
                case 1:
                    val = data.m1;
                    break;
                case 2:
                    val = data.m2;
                    break;
                case 3:
                    val = data.m3;
                    break;
                case 4:
                    val = data.m4;
                    break;
                case 5:
                    val = data.m5;
                    break;
                case 6:
                    val = data.m6;
                    break;
                case 7:
                    val = data.m7;
                    break;
                case 8:
                    val = data.m8;
                    break;
                case 9:
                    val = data.m9;
                    break;
                case 10:
                    val = data.m10;
                    break;
                case 11:
                    val = data.m11;
                    break;
                default:
                    val = data.m12;
                    break;
            }
            return val;
        },
        /**
         * @author:杠上花
         * @date:2018-1-10
         * @modifydate:
         * @param:
         * @return:
         * @description:获取范围
         */
        getBounds:function(){
            var me = this;
            var url = Url_Config.gridServiceUrl+"services/AdminDivisionService/getDivisionInfoNew";
            var param = {
                areaName:"XN"//西南
            };
            param = JSON.stringify(param);
            var pro = new Promise(function (resolve, reject) {
                me.AJAX(url, param, false, function(){
                    resolve("err");
                }, function(data){
                    resolve(data);
                });
            });
            return pro;
        },
        /**
         * @author:杠上花
         * @date:2018-1-10
         * @modifydate:
         * @param:
         * @return:
         * @description:获取范围
         */
        getLineParam:function(pt1,pt2){
            var m = pt2.x - pt1.x;
            var lp = {};
            if(0 == m){
                lp.k = 10000.0;
                lp.b = pt1.y - lp.k*pt1.x;
            }
            else{
                lp.k = (pt2.y - pt1.y)/(pt2.x - pt1.x);
                lp.b = pt1.y - lp.k*pt1.x;
            }
            return lp;
        },
        /**
         * @author:杠上花
         * @date:2018-1-10
         * @modifydate:
         * @param:
         * @return:
         * @description:获取交点
         */
        getCross:function(pt1,pt2,pt3,pt4){
            var me = this;
            var param1 = me.getLineParam(pt1,pt2);
            var param2 = me.getLineParam(pt3,pt4);
            var pt = {};
            if(Math.abs(param1.k-param2.k)>0.5){
                pt.x = (param2.b - param1.b) / (param1.k - param2.k);
                pt.y = param1.k * pt.x + param1.b;
            }
            else{
                return null;
            }
            return pt;
        },
        /**
         * @author:杠上花
         * @date:2018-1-10
         * @modifydate:
         * @param:
         * @return:
         * @description:添加或移除遮罩
         */
        addCover:function(geo,tf){
            if(!tf){
                lmu.Remove("遮罩");
                return;
            }
            var layer = lmu.addLayer("遮罩","vector",null,"zhezhao");
            layer.removeAllFeatures();
            var zzPts = new Array();
            zzPts.push(new WeatherMap.Geometry.Point(-180, -90));
            zzPts.push(new WeatherMap.Geometry.Point(180, -90));
            zzPts.push(new WeatherMap.Geometry.Point(180, 90));
            zzPts.push(new WeatherMap.Geometry.Point(-180, 90));
            var zzLinearRings = new WeatherMap.Geometry.LinearRing(zzPts);
            var geoPts = new Array();
            var points = geo.geometry.points;
            for(var i=0;i<points.length;i++){
                var lon = points[i].x;
                var lat = points[i].y;
                var point = new WeatherMap.Geometry.Point(lon, lat);
                geoPts.push(point);
            }
            var geoLinearRings = new WeatherMap.Geometry.LinearRing(geoPts);
            var polygon = new WeatherMap.Geometry.Polygon([zzLinearRings,geoLinearRings]);
            var polygonVector = new WeatherMap.Feature.Vector(polygon);
            polygonVector.style = {
                strokeColor: "#ffffff",
                fillColor: "#ffffff",
                strokeWidth: 1,
                fillOpacity: 1,
                strokeOpacity: 0.4
            };
            layer.addFeatures([polygonVector]);
            layer.redraw();
        },
        /**
         * @author:杠上花
         * @date:2018-1-17
         * @modifydate:
         * @param:
         * @return:
         * @description:获取最新资料时间
         */
		getResLastDate:function(){
			var me = this;
            var param = {
                path: Physics_Config.modeHgtResOfMonthDir
            };
            param = JSON.stringify(param);
            var url = Url_Config.gridServiceUrl + "services/FileInfo/getModeResLastDate";
            var pro = new Promise(function (resolve, reject) {
                me.AJAX(url, param,false,reject, resolve);
            });
            return pro;
        },
        /**
         * @author:杠上花
         * @date:2018-1-17
         * @modifydate:
         * @param:
         * @return:
         * @description:获取资料状态
         */
        checkResStatus:function(strFile){
            var me = this;
            var param = {
                file: strFile
            };
            param = JSON.stringify(param);
            var url = Url_Config.gridServiceUrl + "services/FileInfo/checkResStatus";
            var pro = new Promise(function (resolve, reject) {
                me.AJAX(url, param,false,reject, resolve);
            });
            return pro;
        }
    }
});
