/**
 * 格点分析
 * Created by zouwei
 */

WeatherMap.GridAnalyst = WeatherMap.Class({

    //构造函数
    initialize: function() {

    },

    /*
    * 描述：格点跟踪算法。逐个格点递归会造成递归次数过多，因此改为逐行查找，同时判断周边是否都已经跟踪过就不用递归跟踪了，目的是减少递归次数
    * 参数：
    *   dtGrid：格点数据集
    *   startColumn：开始列
    *   startRow：开始行
    *   dValueTarget：目标格点值
    *   method：方法，0-等于目标格点值，1-大于目标格点值，2-小于目标格点值
    *   arrayTag：标记是否满足条件的格点
    *   arrayTrack：标记是否跟踪过的格点
    * 返回：无
    * */
    track: function(dtGrid, startColumn, startRow, dValueTargetMin, dValueTargetMax, arrayTag, arrayTrack) {
		var index = startRow*dtGrid.cols+startColumn;
        var dValueStart = dtGrid.grid[index];
        if (dValueTargetMin == dValueTargetMax && dValueStart != dValueTargetMin ||
            (dValueStart < dValueTargetMin || dValueStart > dValueTargetMax)) //判断起始点是否满足条件
            return;

        arrayTrack[startRow][startColumn] = true;
        arrayTag[startRow][startColumn] = true;
        //八方向跟踪
        var pts = [];
        pts.push({x:startColumn+1, y:startRow});
        pts.push({x:startColumn+1,y:startRow+1});
        pts.push({x:startColumn,y:startRow+1});
        pts.push({x:startColumn-1,y:startRow+1});
        pts.push({x:startColumn-1,y:startRow});
        pts.push({x:startColumn-1,y:startRow-1});
        pts.push({x:startColumn,y:startRow-1});
        pts.push({x:startColumn+1,y:startRow-1});

        var ptsFind = [];

        for(var i = 0; i<pts.length; i++)
        {
            var pt = pts[i];
            if (pt.x >= 0 && pt.x < dtGrid.cols && pt.y >= 0 && pt.y < dtGrid.rows) //别超范围
            {
                if (!arrayTag[pt.y][pt.x] && !arrayTrack[pt.y][pt.x]) //必须是没有标记过、没有跟踪过的点
                {
                    arrayTrack[pt.y][pt.x] = true;
					index = pt.y*dtGrid.cols+pt.x;
                    var dValue = dtGrid.grid[index];
                    if (dValue == dtGrid.noDataValue)
                    {
                        //arrayTag[pt.X, pt.Y] = true;
                        continue;
                    }
                    if (dValueTargetMin == dValueTargetMax && dValue ==  dValueTargetMin ||
                        dValue >= dValueTargetMin && dValue <= dValueTargetMax) //找到下一个点
                    {
                        arrayTag[pt.y][pt.x] = true;
                        ptsFind.push(pt);

                        //再按行跟踪。这样重复显得很啰嗦，但是能减少递归次数。如果以后要改回去，注释掉下面代码即可
                        var deltaX = 1;
                        var j = pt.x;
                        while(true){
                            j+=deltaX;
                            if (j < 0 || j > dtGrid.cols - 1) //别超范围
                            {
                                if(deltaX == -1){
                                    break;
                                }
                                else{
                                    j = pt.x;
                                    deltaX = -1;
                                    continue;
                                }
                            }
                            if (!arrayTag[pt.y][j] && !arrayTrack[pt.y][j]) //必须是没有标记过、没有跟踪过的点
                            {
                                arrayTrack[pt.y][j] = true;
								index = pt.y*dtGrid.cols+j;
                                var dValue = dtGrid.grid[index];
                                if (dValue == dtGrid.noDataValue)  //不符合
                                {
                                    if(deltaX == -1){
                                        break;
                                    }
                                    else{
                                        j = pt.x;
                                        deltaX = -1;
                                        continue;
                                    }
                                }
                                if (dValueTargetMin == dValueTargetMax && dValue ==  dValueTargetMin ||
                                    dValue >= dValueTargetMin && dValue <= dValueTargetMax) //找到下一个点
                                {
                                    arrayTag[pt.y][j] = true;
                                    //ptsFind.push(pt);

                                    //行上的点，进行8方向跟踪，这样他们就不用递归了
                                    var startRowX =  pt.y;
                                    var startColumnX = j;
                                    var ptsX = [];
                                    ptsX.push({x:startColumnX+1, y:startRowX});
                                    ptsX.push({x:startColumnX+1,y:startRowX+1});
                                    ptsX.push({x:startColumnX,y:startRowX+1});
                                    ptsX.push({x:startColumnX-1,y:startRowX+1});
                                    ptsX.push({x:startColumnX-1,y:startRowX});
                                    ptsX.push({x:startColumnX-1,y:startRowX-1});
                                    ptsX.push({x:startColumnX,y:startRowX-1});
                                    ptsX.push({x:startColumnX+1,y:startRowX-1});
                                    for(var ii = 0; ii<ptsX.length; ii++) {
                                        var ptX = ptsX[ii];
                                        if (ptX.x >= 0 && ptX.x < dtGrid.cols && ptX.y >= 0 && ptX.y < dtGrid.rows) //别超范围
                                        {
                                            if (!arrayTag[ptX.y][ptX.x] && !arrayTrack[ptX.y][ptX.x]) //必须是没有标记过、没有跟踪过的点
                                            {
                                                arrayTrack[ptX.y][ptX.x] = true;
												index = pt.y*dtGrid.cols+ptX.x;
                                                var dValue = dtGrid.grid[index];
                                                if (dValue == dtGrid.noDataValue) {
                                                    //arrayTag[pt.X, pt.Y] = true;
                                                    continue;
                                                }
                                                if (dValueTargetMin == dValueTargetMax && dValue ==  dValueTargetMin ||
                                                    dValue >= dValueTargetMin && dValue <= dValueTargetMax) //找到下一个点
                                                {
                                                    arrayTag[ptX.y][ptX.x] = true;
                                                    ptsFind.push(ptX);

                                                    //再按列跟踪。这样重复显得很啰嗦，但是能减少递归次数。如果以后要改回去，注释掉下面代码即可
                                                    var deltaY = 1;
                                                    var k = pt.y;
                                                    while(true){
                                                        k+=deltaY;
                                                        if (k < 0 || k > dtGrid.rows - 1) //别超范围
                                                        {
                                                            if(deltaY == -1){
                                                                break;
                                                            }
                                                            else{
                                                                k = pt.y;
                                                                deltaY = -1;
                                                                continue;
                                                            }
                                                        }
                                                        if (!arrayTag[k][j] && !arrayTrack[k][j]) //必须是没有标记过、没有跟踪过的点
                                                        {
                                                            arrayTrack[k][j] = true;
															index = k*dtGrid.cols+j;
                                                            var dValue = dtGrid.grid[index];
                                                            if (dValue == dtGrid.noDataValue)  //不符合
                                                            {
                                                                if(deltaY == -1){
                                                                    break;
                                                                }
                                                                else{
                                                                    k = pt.y;
                                                                    deltaY = -1;
                                                                    continue;
                                                                }
                                                            }
                                                            if (dValueTargetMin == dValueTargetMax && dValue ==  dValueTargetMin ||
                                                                dValue >= dValueTargetMin && dValue <= dValueTargetMax) //找到下一个点
                                                            {
                                                                arrayTag[k][j] = true;
                                                                //ptsFind.push(pt);

                                                                //列上的点，进行8方向跟踪，这样他们就不用递归了
                                                                var startRowY =  k;
                                                                var startColumnY = j;
                                                                var ptsY = [];
                                                                ptsY.push({x:startColumnY+1, y:startRowY});
                                                                ptsY.push({x:startColumnY+1,y:startRowY+1});
                                                                ptsY.push({x:startColumnY,y:startRowY+1});
                                                                ptsY.push({x:startColumnY-1,y:startRowY+1});
                                                                ptsY.push({x:startColumnY-1,y:startRowY});
                                                                ptsY.push({x:startColumnY-1,y:startRowY-1});
                                                                ptsY.push({x:startColumnY,y:startRowY-1});
                                                                ptsY.push({x:startColumnY+1,y:startRowY-1});
                                                                for(var iii = 0; iii<ptsY.length; iii++) {
                                                                    var ptY = ptsY[iii];
                                                                    if (ptY.x >= 0 && ptY.x < dtGrid.cols && ptY.y >= 0 && ptY.y < dtGrid.rows) //别超范围
                                                                    {
                                                                        if (!arrayTag[ptY.y][ptY.x] && !arrayTrack[ptY.y][ptY.x]) //必须是没有标记过、没有跟踪过的点
                                                                        {
                                                                            arrayTrack[ptY.y][ptY.x] = true;
																			index = ptY.y*dtGrid.cols+ptY.x;
                                                                            var dValue = dtGrid.grid[index];
                                                                            if (dValue == dtGrid.noDataValue) {
                                                                                //arrayTag[pt.X, pt.Y] = true;
                                                                                continue;
                                                                            }
                                                                            if (dValueTargetMin == dValueTargetMax && dValue ==  dValueTargetMin ||
                                                                                dValue >= dValueTargetMin && dValue <= dValueTargetMax) //找到下一个点
                                                                            {
                                                                                arrayTag[ptY.y][ptY.x] = true;
                                                                                ptsFind.push(ptY);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            else //不符合
                                                            {
                                                                if(deltaY == -1){
                                                                    break;
                                                                }
                                                                else{
                                                                    k = pt.y;
                                                                    deltaY = -1;
                                                                    continue;
                                                                }
                                                            }
                                                        }
                                                        else  //没有跟踪过
                                                        {
                                                            if(deltaY == -1){
                                                                break;
                                                            }
                                                            else{
                                                                k = pt.y;
                                                                deltaY = -1;
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else //不符合
                                {
                                    if(deltaX == -1){
                                        break;
                                    }
                                    else{
                                        j = pt.x;
                                        deltaX = -1;
                                        continue;
                                    }
                                }
                            }
                            else  //没有跟踪过
                            {
                                if(deltaX == -1){
                                    break;
                                }
                                else{
                                    j = pt.x;
                                    deltaX = -1;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (ptsFind.length > 0)
        {
            for(var i = 0; i<ptsFind.length; i++)
            {
                var pt = ptsFind[i];
                this.track(dtGrid, pt.x, pt.y,dValueTargetMin, dValueTargetMax, arrayTag, arrayTrack); //递归
            }
        }
    },

    /*
    * 格点转轮廓线
    * 算法：
    *   1、判断是否边界格点，四边有其中之一处于边界（为false），即为边界点。
    *   2、沿8方向查找下一个边界格点。
    *   3、回到起点结束查找。
    * */
    gridToLine: function(arrayTag, left, top, deltaX, deltaY){
        if(arrayTag == null || arrayTag.length == 0)
            return null;

        var pointArray  = new Array();
        var rows = arrayTag.length;
        var cols = arrayTag[0].length;
        var pt = null;
        for(var i=0; i<rows - 1; i++)
        {
            var arrayTagRow = arrayTag[i];
            for(var j=0; j<cols - 1; j++)
            {
                if(arrayTagRow[j])
                {
                    pt = {x:j, y:i};
                    break;
                }
            }
            if(pt != null)
                break;
        }
        if(pt == null)
            return null;

        //pointArray.push(new WeatherMap.Geometry.Point(left + pt.x*deltaX, top - pt.y*deltaY));
        this.getBorderPointLonLat(arrayTag, left, top, deltaX, deltaY, pt, null, pointArray);
        this.findBorderPoint(arrayTag, left, top, deltaX, deltaY, pt, pt, null , pointArray);
        return new WeatherMap.Geometry.LineString(pointArray);
    },

    //递归查找边界点，从西边开始逆时针查找
    findBorderPoint: function(arrayTag, left, top, deltaX, deltaY, ptEnd, pt, vectorFrom, pointArray){
        var rows = arrayTag.length;
        var cols = arrayTag[0].length;

        var vectors = [{i:-1, j:0},{i:-1, j:1},{i:0, j:1},{i:1, j:1},
            {i:1, j:0},{i:1, j:-1},{i:0, j:-1},{i:-1, j:-1}];
        var nIndexFrom = 0;
        if(vectorFrom != null){
            for(var i=0; i<vectors.length; i++) {
                if (vectors[i].i == vectorFrom.i && vectors[i].j == vectorFrom.j)
                {
                    nIndexFrom = i;
                    break;
                }
            }
        }

        var find = false;
        for(var i= 0; i<vectors.length; i++)
        {
            var nIndex = (nIndexFrom + 1 + i)%vectors.length; //从来向（不含）开始查找，来向最后判断
            //if(vectors[nIndex].i == vectorFrom.i && vectors[nIndex].j == vectorFrom.j) //允许逆向查找，故注释掉
            //  continue;
            var ptTo = {x:pt.x + vectors[nIndex].i, y:pt.y + vectors[nIndex].j};
            if(ptTo.x < 0 || ptTo.x > cols - 1 || ptTo.y < 0 || ptTo.y > rows - 1)
                continue;
            if(arrayTag[ptTo.y][ptTo.x]) //前提是本身为true才行
            {
                if(this.isBorderPoint(arrayTag, ptTo))
                {
                    find = true;
                    //pointArray.push(new WeatherMap.Geometry.Point(left + ptTo.x*deltaX, top - ptTo.y*deltaY));
                    this.getBorderPointLonLat(arrayTag, left, top, deltaX, deltaY, ptTo, {i:vectors[nIndex].i*-1,j:vectors[nIndex].j*-1}, pointArray); //注意矢量方向需要反过来
                    if(ptTo.x == ptEnd.x && ptTo.y == ptEnd.y) //终点
                        return;
                    else
                        this.findBorderPoint(arrayTag, left, top, deltaX, deltaY, ptEnd, ptTo, {i:vectors[nIndex].i*-1,j:vectors[nIndex].j*-1}, pointArray); //注意矢量方向需要反过来
                    break;
                }
            }
        }
    },

    //四边有其中之一处于边界（为false），即为边界点。前提是本身为true
    isBorderPoint: function(arrayTag, pt){
        var rows = arrayTag.length;
        var cols = arrayTag[0].length;
        if(pt.x == 0 || pt.x == cols - 1 || pt.y == 0 || pt.y == rows - 1)
            return true;
        if(!arrayTag[pt.y][pt.x - 1]) //左
            return true;
        if(!arrayTag[pt.y][pt.x + 1]) //右
            return true;
        if(!arrayTag[pt.y - 1][pt.x]) //上
            return true;
        if(!arrayTag[pt.y + 1][pt.x]) //下
            return true;
        return false;
    },

    //获取边界格点的边界坐标
    //vector：方向矢量
    getBorderPointLonLat : function(arrayTag, left, top, deltaX, deltaY, pt, vectorFrom, pointArray){
        var rows = arrayTag.length;
        var cols = arrayTag[0].length;
        var x = left + pt.x*deltaX;
        var y = top - pt.y*deltaY;
        var side = 0;


        //判断应该从哪条边开始
        var vectors = [{i:-1, j:0},{i:0, j:1},{i:1, j:0},{i:0, j:-1}]; //左、下、右、上
        var nIndexFrom = 0;
        if(vectorFrom != null){
            if(vectorFrom.i == 0 && vectorFrom.j == -1 || vectorFrom.i == -1 && vectorFrom.j == -1 )  //从上或左上
            {
                nIndexFrom = 0;
            }
            if(vectorFrom.i == -1 && vectorFrom.j == 0 || vectorFrom.i == -1 && vectorFrom.j == 1 ) //从左或左下
            {
                nIndexFrom = 1;
            }
            if(vectorFrom.i == 0 && vectorFrom.j == 1 || vectorFrom.i == 1 && vectorFrom.j == 1 )  //从下或右下
            {
                nIndexFrom = 2;
            }
            if(vectorFrom.i == 1 && vectorFrom.j == 0 || vectorFrom.i == 1 && vectorFrom.j == -1 ) //从右或右上
            {
                nIndexFrom = 3;
            }
        }

        for(var i= 0; i<vectors.length; i++) {
            var nIndex = (nIndexFrom + i) % vectors.length; //从来向（不含）开始查找，来向最后判断

            if(nIndex == 0)  //左
            {
                if(pt.x == 0 || !arrayTag[pt.y][pt.x - 1])
                {
                    if(side == 0)
                        pointArray.push(new WeatherMap.Geometry.Point(x - deltaX/2, y + deltaY/2));
                    pointArray.push(new WeatherMap.Geometry.Point(x - deltaX/2, y - deltaY/2));
                    side++;
                }
                else if(side > 0)
                {
                    break;
                }
            }
            if(nIndex == 1) //下
            {
                if(pt.y == rows - 1 || !arrayTag[pt.y + 1][pt.x])
                {
                    if(side == 0)
                        pointArray.push(new WeatherMap.Geometry.Point(x - deltaX/2, y - deltaY/2));
                    pointArray.push(new WeatherMap.Geometry.Point(x + deltaX/2, y - deltaY/2));
                    side++;
                }
                else if(side > 0)
                {
                    break;
                }
            }

            if(nIndex == 2) //右
            {
                if(pt.x == cols - 1 || !arrayTag[pt.y][pt.x + 1])
                {
                    if(side == 0)
                        pointArray.push(new WeatherMap.Geometry.Point(x + deltaX/2, y - deltaY/2));
                    pointArray.push(new WeatherMap.Geometry.Point(x + deltaX/2, y + deltaY/2));
                    side++;
                }
                else if(side > 0)
                {
                    break;
                }
            }

            if(nIndex == 3) //上
            {
                if(pt.y == 0 || !arrayTag[pt.y - 1][pt.x])
                {
                    if(side == 0)
                        pointArray.push(new WeatherMap.Geometry.Point(x + deltaX/2, y + deltaY/2));
                    pointArray.push(new WeatherMap.Geometry.Point(x - deltaX/2, y + deltaY/2));
                    side++;
                }
                else if(side > 0)
                {
                    break;
                }
            }
        }

//        if(pt.x == 0 || !arrayTag[pt.y][pt.x - 1]) //左
//        {
//            pointArray.push(new WeatherMap.Geometry.Point(x - deltaX/2, y + deltaY/2));
//            pointArray.push(new WeatherMap.Geometry.Point(x - deltaX/2, y - deltaY/2));
//            side++;
//        }
//        if(pt.y == rows - 1 || !arrayTag[pt.y + 1][pt.x]) //下
//        {
//            pointArray.push(new WeatherMap.Geometry.Point(x - deltaX/2, y - deltaY/2));
//            pointArray.push(new WeatherMap.Geometry.Point(x + deltaX/2, y - deltaY/2));
//            side++;
//        }
//        else if(side > 0)
//        {
//            return;
//        }
//
//        if(pt.x == cols - 1 || !arrayTag[pt.y][pt.x + 1]) //右
//        {
//            pointArray.push(new WeatherMap.Geometry.Point(x + deltaX/2, y - deltaY/2));
//            pointArray.push(new WeatherMap.Geometry.Point(x + deltaX/2, y + deltaY/2));
//            side++;
//        }
//        else if(side > 0)
//        {
//            return;
//        }
//
//        if(pt.y == 0 || !arrayTag[pt.y - 1][pt.x]) //上
//        {
//            pointArray.push(new WeatherMap.Geometry.Point(x + deltaX/2, y + deltaY/2));
//            pointArray.push(new WeatherMap.Geometry.Point(x - deltaX/2, y + deltaY/2));
//        }
//        else if(side > 0)
//        {
//            return;
//        }
    },

    /*
     * 格点转轮廓线（淘汰）
     * */
    gridToLine2: function(arrayTag, left, top, deltaX, deltaY){
        var pointArray  = new Array();
        var ptLeft = [];
        var ptRight = [];
        for(var row=0; row<arrayTag.length - 1; row++)
        {
            var arrayTagRow = arrayTag[row];
            var nLeft = 0;
            for(var col=0; col<arrayTagRow.length - 1; col++)
            {
                if(arrayTagRow[col])
                {
                    nLeft = col;
                    ptLeft.push({x:left + col * deltaX,
                        y:top - row * deltaY});
                    break;
                }
            }

            if(nLeft < arrayTagRow.length - 1) //不是最后一列
            {
                for(var col=arrayTagRow.length - 1; col>nLeft; col--)
                {
                    if(arrayTagRow[col])
                    {
                        ptRight.push({x:left + col * deltaX,
                            y:top - row * deltaY});
                        break;
                    }
                }
            }
        }
        for(var i=0;i<ptLeft.length;i++)
        {
            pointArray.push(new WeatherMap.Geometry.Point(ptLeft[i].x, ptLeft[i].y));
        }
        for(var i=ptRight.length - 1;i>=0;i--)
        {
            pointArray.push(new WeatherMap.Geometry.Point(ptRight[i].x, ptRight[i].y));
        }
        pointArray.push(new WeatherMap.Geometry.Point(ptLeft[0].x, ptLeft[0].y));
        return new WeatherMap.Geometry.LineString(pointArray);
    },

    CLASS_NAME: 'WeatherMap.GridAnalyst'
});

