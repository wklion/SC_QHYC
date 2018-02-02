/**
 * 数据缓存类
 * Created by zouwei on 2015/11/10.
 */

function DataCache(){

    this.caches = null;

    /*
    * 描述：添加缓存数据
    * 缓存结构如下：
    *       maketime
    *           -date
    *               -version
    *                   -element
    *                           -hourspan
    *
    * 参数：
    *   maketime：制作时间（标识）
    *   version：版本（标识）
    *   date：预报时间（标识）
    *   element：要素（标识）
    *   hourspan：时效（标识）
    *   datasetGrid：格点数据
    *   status：数据状态
    * 返回：无
    * */
    this.addData = function(maketime, version, date, element, hourspan, datasetGrid, status){
        if(this.caches == null)
            this.caches = {};

        if(!this.caches.hasOwnProperty(maketime))
            this.caches[maketime] = {};
        var maketimeData = this.caches[maketime];

        if(!maketimeData.hasOwnProperty(date))
            maketimeData[date] = {};
        var dateData = maketimeData[date];

        if(!dateData.hasOwnProperty(version))
            dateData[version] = {};
        var versionData = dateData[version];

        if(!versionData.hasOwnProperty(element))
            versionData[element] = {};
        var elementData = versionData[element];

        if(!elementData.hasOwnProperty(hourspan))
            elementData[hourspan] = {};
        var hourspanData = elementData[hourspan];


        hourspanData["data"] = datasetGrid;
        hourspanData["status"] = typeof(status)=="undefined"?0:status; //状态，-1，无数据，0-初始（默认），1-已修改，2-已提交，4-已（提交并）主观订正
    };

    /*
     * 描述：获取缓存数据
     * 参数：
     *   maketime：制作时间（检索条件）
     *   version：版本（检索条件）
     *   date：预报时间（检索条件）
     *   element：要素（检索条件）
     *   hourspan：时效（检索条件）
     * 返回：{data:xxx,status:yyyy}对象
     * */
    this.getData = function(maketime, version, date, element, hourspan){
        var result = null;
        if(this.caches == null)
            return result;

        if(!this.caches.hasOwnProperty(maketime))
            return result;

        var maketimeData = this.caches[maketime];
        if(!maketimeData.hasOwnProperty(date))
            return result;

        var dateData = maketimeData[date];
        if(!dateData.hasOwnProperty(version))
            return result;

        var versionData = dateData[version];
        if(!versionData.hasOwnProperty(element))
            return result;

        var elementData = versionData[element];
        if(typeof(hourspan) == "undefined")
            return elementData;

        if(!elementData.hasOwnProperty(hourspan))
            return result;

        var hourspanData = elementData[hourspan];
        return hourspanData;
    };

    /*
     * 描述：设置缓存数据状态
     * 参数：
     *   date：预报时间（检索条件）
     *   element：要素（检索条件）
     *   hourspan：时效（检索条件）
     *   status：状态
     * 返回：无
     * */
    this.setDataStatus = function(maketime, version, date, element, hourspan, status){
        var datacache = this.getData(maketime, version, date, element, hourspan);
        if(datacache != null)
        {
            datacache.status = status;

            if(element == GDYB.GridProductClass.currentElement){ //当且仅当该要素为当前要素，才更新UI
                if(status == 0){ //初始状态
                    $("#yubaoshixiao").find("#"+hourspan+"h").removeClass("disabled");
                }
                if(status == 1){ //已修改
                    $("#yubaoshixiao").find("#"+hourspan+"h").removeClass("disabled");
                    $("#yubaoshixiao").find("#"+hourspan+"h").addClass("modified");

                    //打破合理性
                    for(var key in CrossRelation){
                        var relation = CrossRelation[key];
                        if(relation.src == element){
                            relation.reasonable = false;
                        }
                    }
                }
                else if(status == 2){ //已提交
                    $("#yubaoshixiao").find("#"+hourspan+"h").removeClass("disabled");
                    $("#yubaoshixiao").find("#"+hourspan+"h").removeClass("modified");
                    $("#yubaoshixiao").find("#"+hourspan+"h").addClass("saved");
                }
                else if(status == 4){ //已（提交并）主观订正
                    $("#yubaoshixiao").find("#"+hourspan+"h").removeClass("disabled");
                    $("#yubaoshixiao").find("#"+hourspan+"h").removeClass("modified");
                    $("#yubaoshixiao").find("#"+hourspan+"h").addClass("subjective");
                }
            }
        }
    };
}