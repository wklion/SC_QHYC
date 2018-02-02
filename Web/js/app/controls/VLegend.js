/**
 * @author:wangkun
 * @date:2017-09-01
 * @param:
 * @return:
 * @description:竖直的
 */
define(['vue'], function(Vue) {
    function VLegend(){
        this.vueLegend = null,
        /**
         * @author:wangkun
         * @date:2017-09-04
         * @param:
         * @return:
         * @description:初始化
         */
        this.Init = function(id){
            var me = this;
            if($("#vlegend_div").length>0){
                return;
            }
            $("#"+id).append(`
                <div id="vlegend_div" class="vlegend_div delete">
                    <div v-for="item in datas" class="vlegend_item_div"><div v-bind:style="{background:'rgb('+item.startColor.red+','+item.startColor.green+','+item.startColor.blue+')'}"></div><div style="padding-top: 14px;">{{ item.start }}</div></div>
                </div>
            `);

            initRes();
            function initRes(){
                me.vueLegend = new Vue({
                    el:"#vlegend_div",
                    data:{
                        datas:[]
                    }
                });
            }
        }
        /**
         * @author:wangkun
         * @date:2017-09-04
         * @param:
         * @return:
         * @description:设置样式
         */
        this.setStyle = function(styles){
            var me = this;
            me.vueLegend.datas = [];
            me.vueLegend.datas = styles;
        }
    }
    return new VLegend();
});