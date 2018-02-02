define([], function() {
    return{
        init:function(){
            $("#right").append(`
                <div class="processControl" id="processControl" style="display:none;"></div>
            `);
        },
        /**
         * @author:wangkun
         * @date:2017-12-10
         * @modifydate:
         * @param:
         * @return:
         * @description:显示进度
         */
        show:function(str){
            $("#processControl").html(str);
            $("#processControl").css("display","block");
        },
        /**
         * @author:wangkun
         * @date:2017-12-10
         * @modifydate:
         * @param:
         * @return:
         * @description:隐藏进度
         */
        hide:function(str,times){
            $("#processControl").css("display","block");
            $("#processControl").html(str);
            setTimeout(function(){
                $("#processControl").html("");
                $("#processControl").css("display","none");
            },times==undefined?2000:1000);
        }
    }
});