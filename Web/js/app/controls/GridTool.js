/**
 * @author:wangkun
 * @date:2017-12-05
 * @modifydate:
 * @param:
 * @return:
 * @description:格点工具
 */
define(["vue","displayUtil",'vlegend'], function(Vue,displayUtil,vlegend) {
    return{
        myVue:null,
        stationData:null,//站点数据
        Init:function(id){
            var me = this;
            $("#"+id).append(`
                <div id="gridTool" class="gridTool">
                    <button id="fillColor" @click="fillColor" class="btn btn-default">填图</button>
                    <button id="fillVal" class="btn btn-default">填值</button>
                    <button id="station" class="btn btn-default">站点</button>
                </div>
            `);
            initRes();
            function initRes(){
                vlegend.Init("map");
                me.myVue = new Vue({
                    el:"#gridTool",
                    data:{
                    },
                    methods:{
                        fillColor:function(){
                            console.log("填图");
                            if(me.stationData==null){
                                console.log("站点数据为空!");
                                return;
                            }
                            displayUtil.displayFillColor("热点",me.stationData);
                            vlegend.setStyle(month_prec);
                        }
                    }
                });
            }
        }
    }
});