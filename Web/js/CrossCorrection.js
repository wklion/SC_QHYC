/**
 * 交叉订正
 * Created by allen_000 on 2015/12/15.
 */
function CrossCorrection() {
    this.cal = function (recall, element, elementTarget) {
        var t = this;
        var dataCache = GDYB.GridProductClass.dataCache;
        var makeTime = GDYB.GridProductClass.currentMakeTime;
        var version = GDYB.GridProductClass.currentVersion;
        var currentDateTime = GDYB.GridProductClass.currentDateTime;

        //恢复（一致性）合理性
        var relation = getCrossRelation(element, elementTarget);
        relation.reasonable = true;
        if(element == "tmin" || element == "tmax"){
            relation = getCrossRelation(element=="tmin"?"tmax":"tmin", "2t");
            relation.reasonable = true;
        }
        if(elementTarget == "tmin" || elementTarget == "tmax"){
            relation = getCrossRelation("2t", elementTarget=="tmin"?"tmax":"tmin");
            relation.reasonable = true;
        }

        function getCrossRelation(src, target) {
            var result = null;
            for (var key in CrossRelation) {
                var relation = CrossRelation[key];
                if (relation.src == src && relation.target == target) {
                    result = relation;
                    break;
                }
            }
            return result;
        }

        if((element == "r12" || element == "tmax" || element == "tmin" || element == "wmax") &&
            (elementTarget == "r3" || elementTarget == "2t" || elementTarget == "10uv")) //拆分（逐日-->逐时）
        {
//            //1.确定关联要素与统计计算关系
//            var elementTarget = null;
//            if(element == "r12") {
//                elementTarget = "r3";
//            }
//            else if(element == "tmax" || element == "tmin"){
//                elementTarget = "2t";
//            }
//            else if(element == "wmax"){
//                elementTarget = "10uv";
//            }
            var dataCacheElement = dataCache.getData(makeTime, version, currentDateTime, element);
            if(dataCacheElement == null || dataCacheElement.length == 0){
                recall&&recall();
                return;
            }

            //遍历源数据各时效
            var hourspans = t.getHourSpan(element);
            for(var key in hourspans){
                var hour = hourspans[key];
                var dataCacheHourSpan = dataCache.getData(makeTime, version, currentDateTime, element, hour);
                if(dataCacheHourSpan == null || dataCacheHourSpan.data == null)
                    continue;
                var dg = dataCacheHourSpan.data;

                var bMaxTemp = false;
                var dgAnother = null;
                if(element == "tmax" || element == "tmin"){
                    var bMaxTemp = element == "tmax";
                    var elementAnother = bMaxTemp ?"tmin":"tmax";
                    var dataCacheHourSpan = dataCache.getData(makeTime, version, currentDateTime, elementAnother, hour);
                    if(dataCacheHourSpan == null || dataCacheHourSpan.data == null)
                    {
                        console.error("请先下载要素：" + elementTargetAnother);
                        recall&&recall();
                        continue;
                    }
                    dgAnother = dataCacheHourSpan.data;
                }

                var cols = dg.cols;
                var rows = dg.rows;
                //2.确定时效范围
                var hourSpan = 1; //以最小时效1小时去查找吧，同时解决时效不等的情况
                var hourSpanStart = Number(key)==0?hourSpan:hourspans[Number(key)-1]+hourSpan;
                var hourSpanEnd = hour;
                //3.遍历各时效，记录所有的目标数据集
                var arrayDataset = [];
                for(var h=hourSpanStart; h<=hourSpanEnd; h+=hourSpan)
                {
                    var dataCacheHourSpan  = dataCache.getData(makeTime, version, currentDateTime, elementTarget, h);
                    if(dataCacheHourSpan != null && dataCacheHourSpan.data != null)
                    {
                        if(dataCacheHourSpan.data.cols == cols && dataCacheHourSpan.data.rows == rows)
                            arrayDataset.push(dataCacheHourSpan.data);
                        else
                            alert("目标场与参考场行列数不一致");
                    }
                }

                //3.计算累计值，存到数组中
                var arryTotalValues = [];
                if(elementTarget == "r3") {
                    for (var i = 0; i < rows; i++) {
                        var arrayTotalValueRow = [];
                        for (var j = 0; j < cols; j++) {
                            var dValueTotal = 0.0;
                            for (var dd = 0; dd < arrayDataset.length; dd++) {
                                var dgTemp = arrayDataset[dd];
                                var dValueTemp = dgTemp.grid[i][j].z;
                                if (dValueTemp != dgTemp.noDataValue && dValueTemp >= 0)
                                    dValueTotal += dValueTemp;
                            }
                            arrayTotalValueRow.push(dValueTotal);
                        }
                        arryTotalValues.push(arrayTotalValueRow);
                    }
                }

                //3.计算极值，存到数组中
                var noDataValue = dg.noDataValue;
                var noDataValueABS = Math.abs(noDataValue);
                var arryMinValues = [];
                var arryMaxValues = [];
                if(elementTarget == "2t" || elementTarget == "10uv") {
                    for (var i = 0; i < rows; i++)
                    {
                        var arryMinValuesRow = [];
                        var arryMaxValuesRow = [];
                        for (var j = 0; j < cols; j++)
                        {
                            var dValueMax = noDataValueABS * -1;
                            var dValueMin = noDataValueABS;
                            for (var dd = 0; dd < arrayDataset.length; dd++)
                            {
                                var dgTemp = arrayDataset[dd];
                                var dValueTemp = dgTemp.grid[i][j].z;
                                if (dValueTemp != dgTemp.noDataValue) {
                                    if (dValueTemp > dValueMax)
                                        dValueMax = dValueTemp;
                                    if (dValueTemp < dValueMin)
                                        dValueMin = dValueTemp;
                                }
                            }
                            if (Math.abs(dValueMin) == noDataValueABS)
                                arryMinValuesRow.push(noDataValue);
                            else
                                arryMinValuesRow.push(dValueMin);
                            if (Math.abs(dValueMax) == noDataValueABS)
                                arryMaxValuesRow.push(noDataValue);
                            else
                                arryMaxValuesRow.push(dValueMax);
                        }
                        arryMinValues.push(arryMinValuesRow);
                        arryMaxValues.push(arryMaxValuesRow);
                    }
                }

                //4.遍历所有数据集，更新格点值
                if(arrayDataset.length > 0)
                {
                    for(var d=0; d<arrayDataset.length; d++)
                    {
                        var dgTarget  = arrayDataset[d];
                        if(dgTarget != null)
                        {
                            for (var i = 0; i < rows ; i++)
                            {
                                if(true)
                                {
                                    for (var j = 0; j < cols; j++)
                                    {
                                        //5.获取当前值
                                        var dValue = dgTarget.grid[i][j].z;
                                        if(elementTarget == "r3") {
                                            var dValueSrc = dg.grid[i][j].z;
                                            if (dValueSrc == dg.noDataValue || dValue == dgTarget.noDataValue) //如果日雨量为无效值，或者当前格点没有降水，则不订正逐时雨量
                                            {
                                            }
                                            else if(dValue == 0.0){
                                                var dValueTotal = arryTotalValues[i][j];
                                                if(dValueTotal == 0.0){
                                                    dgTarget.grid[i][j].z = Math.round(dValueSrc/arrayDataset.length*10.0)/10.0; //如果逐3小时降水都为0，则均分12小时降水
                                                }
                                            }
                                            else {
                                                var dValueTotal = arryTotalValues[i][j];
                                                if (dValueTotal == 0.0) //如果都没有降水
                                                {
                                                }
                                                else {
                                                    if (dValueSrc != dValueTotal) //如果它们相同，原则上说明没有订正过，无需计算。从数学角度看，也无需计算
                                                    {
                                                        //dgTarget.grid[i][j].z = Math.floor(dValueSrc * dValue / dValueTotal * 10.0) / 10.0; //7.计算目标值，并赋值
                                                        dgTarget.grid[i][j].z = Math.round(dValueSrc * dValue / dValueTotal * 10.0) / 10.0; //7.计算目标值，并赋值
                                                    }
                                                }
                                            }
                                        }
                                        else if(elementTarget == "2t"){
                                            var maxTemp = bMaxTemp?dg.grid[i][j].z:dgAnother.grid[i][j].z;
                                            var minTemp = bMaxTemp?dgAnother.grid[i][j].z:dg.grid[i][j].z;
                                            if(dValue == dgTarget.noDataValue || minTemp == dgTarget.noDataValue || maxTemp == dgTarget.noDataValue) //如果气温为无效值，则不订正
                                            {

                                            }
                                            else
                                            {
                                                if(minTemp == maxTemp) //如果最高温等于最低温，直接赋值
                                                {
                                                    dgTarget.grid[i][j].z = minTemp;
                                                }
                                                else
                                                {
                                                    var dValueMax = arryMaxValues[i][j];
                                                    var dValueMin = arryMinValues[i][j];
                                                    if(dValueMin == dValueMax) //如果极大值等于极小值，如何处理？
                                                    {
                                                        dgTarget.grid[i][j].z = minTemp;
                                                    }
                                                    else
                                                    {
                                                        var x = (dValue*(maxTemp - minTemp) - dValueMin*maxTemp+minTemp*dValueMax)/(dValueMax-dValueMin); //7.计算目标值
                                                        //dgTarget.grid[i][j].z = Math.floor(x*10.0)/10.0;
                                                        dgTarget.grid[i][j].z = Math.round(x*10.0)/10.0;
                                                    }
                                                }
                                            }
                                        }
                                        else if(elementTarget == "10uv"){
                                            var dValueMax = arryMaxValues[i][j];
                                            var dValueMin = arryMinValues[i][j];
                                            var maxWind = dg.grid[i][j].z;
                                            var minWind = dValueMin;
                                            var maxWindDiretion = dg.grid[i][j].direction;
                                            if(dValue == dgTarget.noDataValue)
                                            {

                                            }
                                            else
                                            {
                                                if(minWind == maxWind) //如果最高温等于最低温，直接赋值
                                                {
                                                    dgTarget.grid[i][j].z = maxWind;
                                                    dgTarget.grid[i][j].direction = maxWindDiretion;
                                                }
                                                else
                                                {
                                                    if(dValueMin == dValueMax) //如果极大值等于极小值，如何处理？
                                                    {
                                                        dgTarget.grid[i][j].z = maxWind;
                                                        dgTarget.grid[i][j].direction = maxWindDiretion;
                                                    }
                                                    else
                                                    {
                                                        var x = (dValue*(maxWind - minWind) - dValueMin*maxWind+minWind*dValueMax)/(dValueMax-dValueMin); //7.计算目标值
                                                        dgTarget.grid[i][j].z = Math.round(x*10.0)/10.0;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else if((element == "r3" || element == "2t" || element == "10uv") &&
            (elementTarget == "r12" || elementTarget == "tmax" || elementTarget == "tmin" || elementTarget == "wmax")) //组合（逐时-->逐日）
        {
//            //1.确定关联要素与统计计算关系
//            var elementTarget = null;
//            var elementTargetAnother = null;
//            if(element == "r3"){
//                elementTarget = "r12";
//            }
//            else if(element == "2t"){
//                elementTarget = "tmax";
//                elementTargetAnother = "tmin";
//            }
//            else if(element == "10uv"){
//                elementTarget = "wmax";
//            }
            var elementTargetAnother = null;
            if(element == "2t") {
                elementTarget = "tmax";
                elementTargetAnother = "tmin";
            }

            var dataCacheElement = dataCache.getData(makeTime, version, currentDateTime, element);
            if(dataCacheElement == null || dataCacheElement.length == 0){
                recall&&recall();
                return;
            }

            //遍历目标数据各时效，注意这里是“目标数据”！！！
            var hourspans = t.getHourSpan(elementTarget);
            for(var key in hourspans){
                var hour = hourspans[key];
                var dataCacheHourSpanTarget = dataCache.getData(makeTime, version, currentDateTime, elementTarget, hour);
                if(dataCacheHourSpanTarget == null || dataCacheHourSpanTarget.data == null)
                    continue;
                var dgTarget = dataCacheHourSpanTarget.data;

                var isMaxTemp = false;
                var dgTargetAnother = null;
                if(elementTarget == "tmax" || elementTarget == "tmin"){
                    var dataCacheHourSpan = dataCache.getData(makeTime, version, currentDateTime, elementTargetAnother, hour);
                    if(dataCacheHourSpan == null || dataCacheHourSpan.data == null)
                    {
                        console.error("请先下载要素：" + elementTargetAnother);
                        recall&&recall();
                        continue;
                    }
                    dgTargetAnother = dataCacheHourSpan.data;
                }

                var cols = dgTarget.cols;
                var rows = dgTarget.rows;
                //2.确定时效范围
                var hourSpan = 1; //以最小时效1小时去查找吧，同时解决时效不等的情况
                var hourSpanStart = Number(key)==0?hourSpan:hourspans[Number(key)-1]+hourSpan;
                var hourSpanEnd = hour;
                //3.遍历各时效，记录所有的源数据集，注意这里是“源数据集”！！！
                var arrayDataset = [];
                for(var h=hourSpanStart; h<=hourSpanEnd; h+=hourSpan)
                {
                    var dataCacheHourSpan  = dataCache.getData(makeTime, version, currentDateTime, element, h);
                    if(dataCacheHourSpan != null && dataCacheHourSpan.data != null)
                    {
                        if(dataCacheHourSpan.data.cols == cols && dataCacheHourSpan.data.rows == rows)
                            arrayDataset.push(dataCacheHourSpan.data);
                        else
                            alert("目标场与参考场行列数不一致");
                    }
                }

                //3.计算累计值
                if(element == "r3") {
                    for (var i = 0; i < rows; i++) {
                        for (var j = 0; j < cols; j++) {
                            var dValueTotal = 0.0;
                            for (var dd = 0; dd < arrayDataset.length; dd++) {
                                var dgTemp = arrayDataset[dd];
                                var dValueTemp = dgTemp.grid[i][j].z;
                                if (dValueTemp != dgTemp.noDataValue && dValueTemp >= 0)
                                    dValueTotal += dValueTemp;
                            }
                            dgTarget.grid[i][j].z = Math.round(dValueTotal*10)/10;
                        }
                    }
                }

                //3.计算极值
                var noDataValue = dgTarget.noDataValue;
                var noDataValueABS = Math.abs(noDataValue);
                var bwind = element == "10uv";
                if(element == "2t" || element == "10uv") {
                    for (var i = 0; i < rows; i++)
                    {
                        for (var j = 0; j < cols; j++)
                        {
                            var dValueMax = noDataValueABS * -1;
                            var dValueMaxWindDirection = noDataValue;
                            var dValueMin = noDataValueABS;
                            for (var dd = 0; dd < arrayDataset.length; dd++)
                            {
                                var dgTemp = arrayDataset[dd];
                                var dValueTemp = dgTemp.grid[i][j].z;
                                if (dValueTemp != dgTemp.noDataValue) {
                                    if (dValueTemp > dValueMax){
                                        dValueMax = dValueTemp;
                                        if(bwind)
                                            dValueMaxWindDirection = dgTemp.grid[i][j].direction;
                                    }
                                    if (dValueTemp < dValueMin)
                                        dValueMin = dValueTemp;
                                }
                            }
                            dgTarget.grid[i][j].z = Math.round(dValueMax*10)/10;
                            if(bwind)
                                dgTarget.grid[i][j].direction = dValueMaxWindDirection;
                            if(dgTargetAnother != null)
                                dgTargetAnother.grid[i][j].z = Math.round(dValueMin*10)/10;
                        }
                    }
                }
            }
        }

        if(element == "r3" && elementTarget == "tcc"){  //时效相同的要素间交叉订正，3小时降水量-->总云量
            var elementR3 = "r3";
//            var elementTarget = "tcc";
            var dataCacheElement  = dataCache.getData(makeTime, version, currentDateTime, elementTarget);
            //遍历目标源数据各时效
            var hourspans = t.getHourSpan(elementTarget);
            for(var key in hourspans) {
                var hour = hourspans[key];
                var dataCacheHourSpanTCC = dataCache.getData(makeTime, version, currentDateTime, elementTarget, hour);
                if(dataCacheHourSpanTCC == null || dataCacheHourSpanTCC.data == null)
                    continue;
                var dgTCC = dataCacheHourSpanTCC.data;

                var dataCacheHourSpanR3 = dataCache.getData(makeTime, version, currentDateTime, elementR3, hour);
                if(dataCacheHourSpanR3 == null || dataCacheHourSpanR3.data == null)
                    continue;
                var dgR3 = dataCacheHourSpanR3.data;
                var noDataValue = dgR3.noDataValue;


                var rows = dgTCC.rows;
                var cols = dgTCC.cols;
                var rowsR3 = dgR3.rows;
                var colsR3 = dgR3.cols;
                for(var i=0; i<rows; i++){
                    if(i>=rowsR3)
                        continue;
                    for(var j=0; j<cols; j++){
                        if(j>=colsR3)
                            continue;
                        var r3 = dgR3.grid[i][j].z;
                        if(r3 != noDataValue && r3>0)
                            dgTCC.grid[i][j].z = 10;
                    }
                }
            }
        }

        if((element == "tcc" || element == "r12") && elementTarget == "w"){  //时效相同的要素间交叉订正，总云量、天气现象-->天气现象
            var elementTCC = "tcc";
            var elementR12 = "r12";
//            var elementTarget = "w";
            var dataCacheElement  = dataCache.getData(makeTime, version, currentDateTime, elementTarget);
            //遍历目标源数据各时效
            var hourspans = t.getHourSpan(element);
            for(var key in hourspans) {
                var hour = hourspans[key];
                var dataCacheHourSpanWeather = dataCache.getData(makeTime, version, currentDateTime, elementTarget, hour);
                if(dataCacheHourSpanWeather == null || dataCacheHourSpanWeather.data == null)
                    continue;
                var dgWeather = dataCacheHourSpanWeather.data;
                var noDataValue = dgWeather.noDataValue;

                var dataCacheHourSpanTCC = dataCache.getData(makeTime, version, currentDateTime, elementTCC, hour);
                if(dataCacheHourSpanTCC == null || dataCacheHourSpanTCC.data == null)
                    continue;
                var dgTCC = dataCacheHourSpanTCC.data;

                var dataCacheHourSpanR12 = dataCache.getData(makeTime, version, currentDateTime, elementR12, hour);
                if(dataCacheHourSpanR12 == null || dataCacheHourSpanR12.data == null)
                    continue;
                var dgR12 = dataCacheHourSpanR12.data;

                var hasTag = typeof(dgR12.tag) != "undefined";
                var tag = null;

                var rows = dgWeather.rows;
                var cols = dgWeather.cols;
                for(var i=0; i<rows; i++){
                    for(var j=0; j<cols; j++){
                        var r12 = dgR12.grid[i][j].z;
                        var tcc = dgTCC.grid[i][j].z;
                        if(hasTag)
                            tag = dgR12.tag[i][j];
                        var w = getWeatherCode(noDataValue, r12, tcc, tag);
                        dgWeather.grid[i][j].z = w;
                    }
                }
            }

            function getWeatherCode(noDataValue, dValueR12, dValueTCC, tag){
                var dValueTarget = noDataValue;
                if(dValueR12 != noDataValue && dValueR12>=0.1)
                {
//                    if(dValueR12<0.1) //微量降雨，按小雨处理
//                        dValueTarget = 7.0;
//                    else if(dValueR12<5.0) //小雨
//                        dValueTarget = 7.0;
//                    else if(dValueR12<10.0) //小-中雨
//                        dValueTarget = 21.0;
//                    else if(dValueR12<17.0) //中雨
//                        dValueTarget = 8.0;
//                    else if(dValueR12<25.0) //中-大雨
//                        dValueTarget = 22.0;
//                    else if(dValueR12<38.0) //大雨
//                        dValueTarget = 9.0;
//                    else if(dValueR12<50.0) //大-暴雨
//                        dValueTarget = 23.0;
//                    else if(dValueR12<75.0) //暴雨
//                        dValueTarget = 10.0;
//                    else if(dValueR12<100.0) //暴雨-大暴雨
//                        dValueTarget = 24.0;
//                    else if(dValueR12<175.0) //大暴雨
//                        dValueTarget = 11.0;
//                    else if(dValueR12<250) //大暴雨-特大暴雨
//                        dValueTarget = 25.0;
//                    else //特大暴雨
//                        dValueTarget = 12.0;

                    if(tag == null || tag == 0) //雨量转换天气现象（按24小时标准），tag=1是雪，tag=0是缺省降水
                    {
                        if (dValueR12 < 0.1) //微量降雨，按小雨处理
                            dValueTarget = 7.0;
                        else if (dValueR12 < 10.0) //小雨
                            dValueTarget = 7.0;
                        else if (dValueR12 < 25.0) //中雨
                            dValueTarget = 8.0;
                        else if (dValueR12 < 50.0) //大雨
                            dValueTarget = 9.0;
                        else if (dValueR12 < 100.0) //暴雨
                            dValueTarget = 10.0;
                        else if (dValueR12 < 250.0) //大暴雨
                            dValueTarget = 11.0;
                        else //特大暴雨
                            dValueTarget = 12.0;
                    }
                    else if(tag == 1) //降雪转换天气现象（按24小时标准），tag=1是雪，tag=0是缺省降水
                    {
                        if (dValueR12 < 2.5)        //小雪
                            dValueTarget = 14.0;
                        else if (dValueR12 < 5.0)   //中雪
                            dValueTarget = 15.0;
                        else if (dValueR12 < 10.0)  //大雪
                            dValueTarget = 16.0;
                        else                        //暴雪（天气现象没有大暴雪和特大暴雪）
                            dValueTarget = 17.0;
                    }
                }
                else { //否则根据总云量换算，晴0=[0-30]，多云1=(30,70]，阴2=(70-100]
                    if(dValueTCC != noDataValue)
                    {
                        if(dValueTCC > 7.0)
                            dValueTarget = 2.0;
                        else if (dValueTCC > 3.0)
                            dValueTarget = 1.0;
                        else
                            dValueTarget = 0.0;
                    }
                }
                return dValueTarget;
            }
        }

        recall&&recall();
    };

    this.getHourSpan = function(element){
        var hourspans = [24,48,72,96,120,144,168,192,216,240,264,288,312,336,360,384,408,432,456,480,504,528,552,576,600,624,648,672,696,720];
        return hourspans;
    };
}