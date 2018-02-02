/**
 * 魔术棒工具，落区选择
 */
function MagicTool(){

    this.arrayTag = [];

    this.geoline = null;

    this.map = null;

    this.isCopy = false;

    this.isModifyWindDirection = false;

    this.isBetweenMinAndMax = false; //（要拾取的格点值）是否鉴于最小值和最大值之间

    this.init = function(map){
        var t = this;
        t.map = map;
        map.events.register("click", map, function (event) {
            if (GDYB.GridProductClass.layerMagic == null)
                return;
            if(GDYB.GridProductClass.drawFreePath.active)
                return;

            if (GDYB.GridProductClass.action != GDYB.CorrectAction.pickLuoqu &&
                GDYB.GridProductClass.action != GDYB.CorrectAction.modifyLuoqu &&
                GDYB.GridProductClass.action != GDYB.CorrectAction.moveLuoqu)
                return;
            if (GDYB.GridProductClass.action == GDYB.CorrectAction.modifyLuoqu)
                t.arrayTag = [];
            var ptPixel = event.xy;
            var lonlat = this.getLonLatFromPixel(ptPixel);
            if(GDYB.GridProductClass.action == GDYB.CorrectAction.pickLuoqu)
            {
                GDYB.GridProductClass.layerMagic.removeAllFeatures();
                t.pick(lonlat);
            }
        });

        var ptMouseDown = null;
        map.events.register("mousedown", map, function (event) {
            if(GDYB.GridProductClass.drawFreePath == null)
                return;
            if (GDYB.GridProductClass.layerMagic != null && (GDYB.GridProductClass.action == GDYB.CorrectAction.pickLuoqu ||  GDYB.GridProductClass.action == GDYB.CorrectAction.moveLuoqu))
                ptMouseDown = event.xy;
            if (GDYB.GridProductClass.layerMagic != null && GDYB.GridProductClass.action == GDYB.CorrectAction.none)
                GDYB.GridProductClass.layerMagic.removeAllFeatures();
        });

        //移动后订正格点
        map.events.register("mouseup", map, function (event) {
            if (GDYB.GridProductClass.action != GDYB.CorrectAction.moveLuoqu)
                return;
            if (GDYB.GridProductClass.layerMagic.features.length > 0 && t.arrayTag.length > 0) {
                var dtGrid = GDYB.GridProductClass.datasetGrid;
                var arrayTagOld = [];
                var arrayTagNew = [];
                var gridOld = []; //原始值
                var gridDirectionOld = GDYB.GridProductClass.currentElement == "10uv" && t.isModifyWindDirection? [] : null; //是否订正风向
                for (var i = 0; i < dtGrid.rows; i++) {
                    var arrayTagRow = [];
                    var arrayTagRowNew = [];
                    var gridRow = [];
                    var gridDirectionRow = [];
                    for (var j = 0; j < dtGrid.cols; j++) {
                        arrayTagRow.push(t.arrayTag[i][j]);
                        arrayTagRowNew.push(false);
						var index = i*dtGrid.cols+j;
                        gridRow.push(dtGrid.grid[index]);
                        if(gridDirectionOld != null)
                            gridDirectionRow.push(dtGrid.grid[i][j].direction);
                    }
                    arrayTagOld.push(arrayTagRow);
                    arrayTagNew.push(arrayTagRowNew);
                    gridOld.push(gridRow);
                    if(gridDirectionOld != null)
                        gridDirectionOld.push(gridDirectionRow)
                }

                var dValueDefault = GDYB.GridProductClass.currentGridValueDown;
                if(GDYB.GridProductClass.currentElement == "r3" ||
                    GDYB.GridProductClass.currentElement == "r6" ||
                    GDYB.GridProductClass.currentElement == "r12" ||
                    GDYB.GridProductClass.currentElement == "r24")
                    dValueDefault = GDYB.GridProductClass.currentGridValueDown >= 0.1 ? GDYB.GridProductClass.currentGridValueDown : 0; //代替值
                var ptMouseUp = event.xy;
                var lonlatMouseDown = this.getLonLatFromPixel(ptMouseDown);
                var lonlatMouseUp = this.getLonLatFromPixel(ptMouseUp);
                var ptGridMouseDown = dtGrid.xyToGrid(lonlatMouseDown.lon, lonlatMouseDown.lat);
                var ptGridMouseUp = dtGrid.xyToGrid(lonlatMouseUp.lon, lonlatMouseUp.lat);
                var xOffset = ptGridMouseUp.x - ptGridMouseDown.x;
                var yOffset = ptGridMouseUp.y - ptGridMouseDown.y;
                var minValue = Math.abs(dtGrid.noDataValue);
                for (var i = 0; i < dtGrid.rows; i++) {
                    for (var j = 0; j < dtGrid.cols; j++) {
                        if (arrayTagOld[i][j]) {
                            //var dValue = dtGrid.grid[i][j].z;
                            var dValue = gridOld[i][j];
                            //dtGrid.grid[i][j].z = dValueDefault;
                            var ii = i + yOffset;
                            var jj = j + xOffset;
                            if (ii < 0 || ii >= dtGrid.rows || jj < 0 || jj >= dtGrid.cols)
                                continue;
							var index = ii*dtGrid.cols+jj;
                            dtGrid.grid[index] = dValue;
                            if(gridDirectionOld != null)
                                dtGrid.grid[ii][jj].direction = gridDirectionOld[i][j];
                            t.arrayTag[ii][jj] = false;
                            arrayTagNew[ii][jj] = true;
                            if(dValue < minValue)
                                minValue = dValue;
                        }
                    }
                }

                //计算原来位置上的格点值
                if(t.isCopy){
                    t.isCopy = false; //复制仅一次有效
                }
                else{
                    if(dValueDefault == dtGrid.noDataValue)
                        dValueDefault = minValue;
                    for (var i = 0; i < dtGrid.rows; i++) {
                        for (var j = 0; j < dtGrid.cols; j++) {
                            if (arrayTagOld[i][j]) {
                                //dtGrid.grid[i][j].z = dValueDefault;

                                //线性插值
                                var nIndexLeft = j;
                                var nIndexRight = -1;
                                for (var k = nIndexLeft; k < dtGrid.cols; k++) {
                                    if (!arrayTagOld[i][k]) {
                                        nIndexRight = k - 1;
                                        break;
                                    }
                                }
                                if (nIndexRight == -1) {
                                    nIndexRight = dtGrid.cols - 1;
                                }


                                {
                                    for (var k = nIndexLeft; k <= nIndexRight; k++) {
                                        if (!t.arrayTag[i][k])
                                            continue;

                                        var nIndexTop = -1;
                                        var nIndexBottom = -1;
                                        for (var l = i; l >= 0; l--) {
                                            if (!arrayTagOld[l][k]) {
                                                nIndexTop = l + 1;
                                                break;
                                            }
                                        }
                                        if (nIndexTop == -1) {
                                            nIndexTop = 0;
                                        }
                                        for (var l = nIndexTop; l < dtGrid.rows; l++) {
                                            if (!arrayTagOld[l][k]) {
                                                nIndexBottom = l - 1;
                                                break;
                                            }
                                        }
                                        if (nIndexBottom == -1) {
                                            nIndexBottom = dtGrid.rows - 1;
                                        }

                                        t.arrayTag[i][k] = false;


										var index = i*dtGrid.cols+k;
                                        if (nIndexLeft == 0 || nIndexRight == dtGrid.cols - 1 ||
                                            gridOld[i][nIndexLeft - 1] == dtGrid.noDataValue || gridOld[i][nIndexRight + 1] == dtGrid.noDataValue) //左右到边界
                                        {
											
                                            if (nIndexTop == 0 || nIndexBottom == dtGrid.rows - 1 ||
                                                gridOld[nIndexTop - 1][j] == dtGrid.noDataValue || gridOld[nIndexBottom + 1][j] == dtGrid.noDataValue) //上下到边界
                                            {
                                                dtGrid.grid[index] = dValueDefault;
                                                continue;
                                            }
                                            else
                                            {
                                                //上下插值
                                                var weightY = (i - (nIndexTop - 1)) / (nIndexBottom + 1 - (nIndexTop - 1));
                                                var dValueY = gridOld[nIndexTop - 1][k] * (1 - weightY) + gridOld[nIndexBottom + 1][k] * weightY;
                                                dtGrid.grid[index] = Math.round(dValueY*10)/10;
                                                continue;
                                            }
                                        }

                                        if (nIndexTop == 0 || nIndexBottom == dtGrid.rows - 1 ||
                                            gridOld[nIndexTop - 1][j] == dtGrid.noDataValue || gridOld[nIndexBottom + 1][j] == dtGrid.noDataValue) //上下到边界
                                        {
                                            if (nIndexLeft == 0 || nIndexRight == dtGrid.cols - 1 ||
                                                gridOld[i][nIndexLeft - 1] == dtGrid.noDataValue || gridOld[i][nIndexRight + 1] == dtGrid.noDataValue) //左右到边界
                                            {
                                                dtGrid.grid[index] = dValueDefault;
                                                continue;
                                            }
                                            else
                                            {
                                                //左右插值
                                                var weightX = (k - (nIndexLeft - 1)) / (nIndexRight + 1 - (nIndexLeft - 1));
                                                var dValueX = gridOld[i][nIndexLeft - 1] * (1 - weightX) + gridOld[i][nIndexRight + 1] * weightX;
                                                dtGrid.grid[index] = Math.round(dValueX*10)/10;
                                                continue;
                                            }
                                        }

                                        var weightX = (k - (nIndexLeft - 1)) / (nIndexRight + 1 - (nIndexLeft - 1));
                                        var weightY = (i - (nIndexTop - 1)) / (nIndexBottom + 1 - (nIndexTop - 1));
                                        var dValueX = gridOld[i][nIndexLeft - 1] * (1 - weightX) + gridOld[i][nIndexRight + 1] * weightX;
                                        var dValueY = gridOld[nIndexTop - 1][k] * (1 - weightY) + gridOld[nIndexBottom + 1][k] * weightY;
                                        dtGrid.grid[index] = Math.round((dValueX + dValueY) / 2.0 * 10.0) / 10.0;
                                        if (dtGrid.grid[index] > 25.0)
                                            t.arrayTag[i][k] = false;
                                    }
                                }
                            }
                        }
                    }
                }
                GDYB.GridProductClass.layerFillRangeColor.refresh();
                GDYB.GridProductClass.addLabel(null, map, null);

                gridOld = [];
                t.arrayTag = arrayTagNew;
                arrayTagOld = [];

                //更新已修改状态
                GDYB.GridProductClass.dataCache.setDataStatus(GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion, GDYB.GridProductClass.currentDateTime, GDYB.GridProductClass.currentElement, GDYB.GridProductClass.currentHourSpan, 1);
                GDYB.GridProductClass.dataStack.push(GDYB.GridProductClass.datasetGrid); //压入堆栈
                GDYB.GridProductClass.updateStationLayer();

                //更新落区
                GDYB.GridProductClass.layerMagic.removeAllFeatures();
                t.getLine(GDYB.GridProductClass.datasetGrid);
            }
        });
    };

    this.getLine = function(dtGrid){
        var ga = new WeatherMap.GridAnalyst();
        var geoline = ga.gridToLine(this.arrayTag, dtGrid.left, dtGrid.top, dtGrid.deltaX, dtGrid.deltaY);
        if (geoline != null) {
            var style = {
                strokeColor: "black",
                strokeWidth: 1
            };
            var lineVector = new WeatherMap.Feature.Vector(geoline, {
                FEATUREID: 0,
                TIME: 1
            }, style);

            GDYB.GridProductClass.layerMagic.addFeatures([lineVector]);
            GDYB.GridProductClass.layerMagic.renderer.frameCount = 0;
            GDYB.GridProductClass.layerMagic.setZIndex(999);
            GDYB.GridProductClass.layerMagic.animator.start();
        }
        this.geoline = geoline;
        return geoline;
    };

    this.pick = function(lonlat){
        var t = this;
        var map = t.map;
        if (GDYB.GridProductClass.datasetGrid != null) {
            var dtGrid = GDYB.GridProductClass.datasetGrid;
            if(GDYB.GridProductClass.currentGridValueDown == Math.abs(dtGrid.noDataValue))
                return;
            t.arrayTag = [];
            var arrayTack = [];
            for (var i = 0; i < dtGrid.rows; i++) {
                var arrayTagRow = [];
                var arrayTackRow = [];
                for (var j = 0; j < dtGrid.cols; j++) {
                    arrayTagRow.push(false);
                    arrayTackRow.push(false);
                }
                t.arrayTag.push(arrayTagRow);
                arrayTack.push(arrayTackRow);
            }
            var ptGrid = dtGrid.xyToGrid(lonlat.lon, lonlat.lat);
            if (ptGrid == null) {
                startDragMap();
                return;
            }
            var ga = new WeatherMap.GridAnalyst();
            //ga.track(dtGrid, ptGrid.x, ptGrid.y, GDYB.GridProductClass.currentGridValueDown, dtGrid.dMax, t.arrayTag, arrayTack); //订正后最大值变了，这个dMax没有更新
            //ga.track(dtGrid, ptGrid.x, ptGrid.y, GDYB.GridProductClass.currentGridValueDown, GDYB.GridProductClass.currentGridValueUp, t.arrayTag, arrayTack); //指定的上限，不好操作
            //ga.track(dtGrid, ptGrid.x, ptGrid.y, GDYB.GridProductClass.currentGridValueDown, Math.abs(dtGrid.noDataValue), t.arrayTag, arrayTack);

            if(this.isBetweenMinAndMax)
                ga.track(dtGrid, ptGrid.x, ptGrid.y, GDYB.GridProductClass.currentGridValueDown, GDYB.GridProductClass.currentGridValueUp, t.arrayTag, arrayTack);
            else
                ga.track(dtGrid, ptGrid.x, ptGrid.y, GDYB.GridProductClass.currentGridValueDown, Math.abs(dtGrid.noDataValue), t.arrayTag, arrayTack);

            //构线
            t.geoline = t.getLine(dtGrid);
            if (t.geoline != null) {
                //清除滤镜工具
                GDYB.FilterTool.clear();
                if (GDYB.GridProductClass.action == GDYB.CorrectAction.modifyLuoqu) //如果拾取成功，开始画线
                {
                    GDYB.GridProductClass.drawFreePath.activate();
                }
                stopDragMap();
                //GDYB.GDYBPage.myPanel_Tools.showDivSettingAssignment();
            }
            else {
                startDragMap();
                //GDYB.GDYBPage.myPanel_Tools.hideDivSettingAssignment();
            }

            arrayTack = null;
        }

        function stopDragMap() {
            for (var i = 0; i < map.events.listeners.mousemove.length; i++) {
                var handler = map.events.listeners.mousemove[i];
                if (handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag") {
                    handler.obj.active = false;
                }
            }
        }

        function startDragMap() {
            for (var i = 0; i < map.events.listeners.mousemove.length; i++) {
                var handler = map.events.listeners.mousemove[i];
                if (handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag") {
                    handler.obj.active = true;
                }
            }
        }
    };

    /*
    *   从面对象拾取落区：矢量落区转栅格
    * */
    this.pickFromRegion = function(gr){
        var t = this;
        var map = t.map;
        if (gr != null && GDYB.GridProductClass.datasetGrid != null) {
            var dg = GDYB.GridProductClass.datasetGrid;
            t.arrayTag = [];
            for (var i = 0; i < dg.rows; i++) {
                var arrayTagRow = [];
                var arrayTackRow = [];
                for (var j = 0; j < dg.cols; j++) {
                    arrayTagRow.push(false);
                }
                t.arrayTag.push(arrayTagRow);
            }

            //矢量转栅格
            var minValue = Math.abs(GDYB.GridProductClass.datasetGrid.noDataValue);
            var bounds = gr.bounds;
            var nLeft = (bounds.left - dg.left) / dg.deltaX;
            if(nLeft < 0)
                nLeft = 0;
            else if(nLeft >= dg.cols)
                return;
            else
                nLeft = Math.floor(nLeft);

            var nTop = (dg.top - bounds.top) / dg.deltaX;
            if(nTop < 0)
                nTop = 0;
            else if(nTop >= dg.rows)
                return;
            else
                nTop = Math.floor(nTop);

            var nRight = (bounds.right - dg.left) / dg.deltaX;
            if(nRight <= 0)
                return;
            else if(nRight >= dg.cols)
                nRight = dg.cols - 1;
            else
                nRight = Math.floor(nRight);

            var nBottom = (dg.top - bounds.bottom) / dg.deltaX;
            if(nBottom < 0)
                return;
            else if(nBottom >= dg.rows)
                nBottom = dg.rows - 1;
            else
                nBottom = Math.floor(nBottom);

            function sortNumber(a,b)
            {
                return a - b;
            }
            for(var i = nTop; i<= nBottom; i++)
            {
                var gridRow = dg.grid[i];
                var pt1 = gridRow[0];
                var pt2 = gridRow[gridRow.length - 1];
                var pts = GDYB.GridProductClass.getIntersections(pt1, pt2, gr);
                var arrayX = [];
                for(var j = 0; j<pts.length; j++){
                    arrayX.push((pts[j].x - dg.left) / dg.deltaX);
                }
                if(arrayX.length == 0)
                    continue;
                var arrayXSort = arrayX.sort(sortNumber); //从小到大排序

                for(var j = nLeft; j<=nRight; j++)
                {
                    var pt = gridRow[j];
                    if(j < arrayXSort[0])
                        continue;
                    var k = arrayXSort.length - 1;
                    for(k; k>=0; k--){
                        if(j>=arrayXSort[k]){
                            break;
                        }
                    }
                    if(k%2 != 0)
                        continue;

                    t.arrayTag[i][j] = true;
                    if(gridRow[j] < minValue)
                        minValue = gridRow[j];
                }
            }

            //构线
            t.geoline = t.getLine(dg);
        }
    };

    //更新格点
    this.updateGrid = function(dvalue, method){
        var element = GDYB.GridProductClass.currentElement;
        var bMinIsZero = element == "10uv" || element == "r1" || element == "r3" || element == "r6" || element == "r12" || element == "24";
        if(method == 0 && dvalue<0 && bMinIsZero)
            return false;
        for(var i=0; i<this.arrayTag.length; i++){
            for(var j=0; j<this.arrayTag[0].length; j++){
                if(this.arrayTag[i][j]){
					var index = i*GDYB.GridProductClass.datasetGrid.cols+j;
                    var targetValue = GDYB.GridProductClass.datasetGrid.grid[index];
                    if(method == 0) //统一赋值，value=x
                    {
                        targetValue = dvalue;
                    }
                    else if(method == 1) //统一加减值，value+=x
                    {
                        targetValue += dvalue;
                    }
                    else if(method == 2) //统一增量（百分比），value*=(1+x)
                    {
                        targetValue*=(1+dvalue);
                    }
                    if(bMinIsZero && targetValue < 0)
                        targetValue = 0.0;
                    targetValue = Math.floor(targetValue*10)/10;
                    if(bMinIsZero && targetValue < 0)
                        targetValue = 0;
                    GDYB.GridProductClass.datasetGrid.grid[index] = targetValue;
                }
            }
        }
        //更新已修改状态
        var t = GDYB.GridProductClass;
        t.dataCache.setDataStatus(t.currentMakeTime, t.currentVersion, t.currentDateTime, t.currentElement, t.currentHourSpan, 1);
        GDYB.GridProductClass.dataStack.push(GDYB.GridProductClass.datasetGrid); //压入堆栈
        GDYB.GridProductClass.updateStationLayer();
    };

    //更新风场（仅订正风向）
    this.updateGridWind = function(geoline){
        for(var i=0; i<this.arrayTag.length; i++){
            for(var j=0; j<this.arrayTag[0].length; j++){
                if(this.arrayTag[i][j]){
                //计算格点到直线的最小距离，及对应线段
                var x0 = GDYB.GridProductClass.datasetGrid.grid[i][j].x;
                var y0 = GDYB.GridProductClass.datasetGrid.grid[i][j].y;
                var ptMin1;
                var ptMin2;
                var dMin = GDYB.GridProductClass.datasetGrid.width;
                var lineString = geoline;
                for (var kk = 1; kk < lineString.components.length; kk++) {
                    var pt1 = lineString.components[kk - 1];
                    var pt2 = lineString.components[kk];
                    //代入直线方程两点式，得一般式（Ax0+By0+C=0）的A B C
                    var a = pt1.y - pt2.y;
                    var b = pt2.x - pt1.x;
                    var c = pt1.x*pt2.y - pt1.y*pt2.x;
                    //根据点到直线距离公式d=Math.abs(A*x+B*y+C)/Math.sqrt(A*A+B*B)，得
                    var d=Math.abs(a*x0+b*y0+c)/Math.sqrt(a*a+b*b);
                    if(d < dMin){
                        dMin = d;
                        ptMin1 = pt1;
                        ptMin2 = pt2;
                    }
                }
                var direction = Math.atan2(ptMin2.y - ptMin1.y, ptMin2.x - ptMin1.x);
                direction = 270.0 - direction / Math.PI * 180;
                GDYB.GridProductClass.datasetGrid.grid[i][j].direction = direction;
                }
            }
        }

        GDYB.GridProductClass.dataCache.setDataStatus(GDYB.GridProductClass.currentMakeTime, GDYB.GridProductClass.currentVersion, GDYB.GridProductClass.currentDateTime, GDYB.GridProductClass.currentElement, GDYB.GridProductClass.currentHourSpan, 1); //更新已修改状态
        GDYB.GridProductClass.dataStack.push(GDYB.GridProductClass.datasetGrid); //压入堆栈
        GDYB.GridProductClass.updateStationLayer();
    };
}
