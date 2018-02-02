/*
* 预报时效工具
* by zouwei, 2015-05-10
* */
function YuBaoshixiaoTools(div, startDate, type){//div:容器
    this.div = div;
    this.type = type;
    this.hourSpan;
    //数字数组
    this.numbers = [24,48,72,96,120,144,168,192,216,240,264,288,312,336,360,384,408,432,456,480,504,528,552,576,600,624,648,672,696,720,744,768,792,816,840,864,888,912,936,960];
    //创建dom元素
    this.createDom = function(date){
        var t = this;
        this.div.html("");
        if(typeof(this.type)=="undefined" || this.type==0) {
            var title = $("<div>预报时效</div>").addClass("title1").appendTo(this.div);
            var div = $("<div>").attr("style", "margin-left:50px;margin-top:5px").appendTo(this.div);
            var tb = $("<table id='table_yubaoshixiao' tabindex='2' border=1 cellPadding=0 cellSpacing=0>")
                .addClass("yubaoshixiao_table").appendTo(div);
            for (var i = 0; i < this.numbers.length; i++) {
                var delta = this.numbers[i] - (i == 0 ? 0 : this.numbers[i - 1]);
                var colspan = 1;//delta > 12 ? 12 : delta;
                var tdWidth = 60;//delta > 12 ? 12 * 20 : delta * 20;
                if (this.numbers[i] % 96 == delta) {
                    var curTr = $("<tr>").appendTo(tb);
                }
                var td = $("<td>").attr("id", this.numbers[i] + "h").attr("colspan", colspan).css("width", tdWidth).html(this.numbers[i]/24).appendTo(curTr).click(function () {
//                if($(this).hasClass("disabled"))
//                    return;
                    $(".yubaoshixiao_table").find("td.active").removeClass("active");
                    $(this).addClass("active");
                    t.clickHandle($(this).html());
                });
                //if (delta == 24)
                //    td.css("height", "40px");
            }

            function getWeek(day) {
                if (day == 0)
                    day = "周日";
                else if (day == 1)
                    day = "周一";
                else if (day == 2)
                    day = "周二";
                else if (day == 3)
                    day = "周三";
                else if (day == 4)
                    day = "周四";
                else if (day == 5)
                    day = "周五";
                else if (day == 6)
                    day = "周六";
                return day;
            }
        }
        else if(this.type == 1){
            //$("#table_yubaoshixiao").html("");//清空
            $("#selectTimes").bind("change",function(){
                changeTimePanel(false);
            });
            function changeTimePanel(firstTime){
                var strTimes=$("#selectTimes option:selected")[0].innerHTML;
                if(firstTime){
                    var div = $("<div>").attr("style", "").appendTo(t.div);
                    var tb = $("<table id='table_yubaoshixiao' tabindex='2' border=1 cellPadding=0 cellSpacing=0>")
                        .addClass("yubaoshixiao_table").appendTo(div);
                    var maxCols = 20; //一排最多放20项
                    var rows = Math.ceil(t.numbers.length/maxCols);
                    var cols = (t.numbers.length<maxCols)?t.numbers.length:maxCols;
                    var totalWidth = parseInt($("#"+t.div[0].id).css("width"))-2;
                    var tdWidth = totalWidth/cols;
                    for (var i = 0; i < t.numbers.length; i++) {
                        if (i%maxCols==0) {
                            var curTr = $("<tr>").appendTo(tb);
                        }
                        var td = $("<td>").attr("id", t.numbers[i]/24 + "h").css("width", tdWidth).html(t.numbers[i]/24).appendTo(curTr).click(function () {
                            if($(this).hasClass("disabled"))
                                return;
                            $(".yubaoshixiao_table").find("td.active").removeClass("active");
                            $(this).addClass("active");
                            t.clickHandle($(this).html());
                        });
                    }
                }
                if(strTimes=="天"){
                    for(var c=0;c< t.numbers.length;c++){
                        $($("#table_yubaoshixiao td")[c]).show();
                    }
                }
                else if(strTimes=="侯"){
                    var maxSpanTime=t.numbers[t.numbers.length-1];
                    var maxDay=maxSpanTime/24;
                    var maxHou=maxDay/5;
                    for(var c=0;c< t.numbers.length;c++){
                        if(c<maxHou){
                            $($("#table_yubaoshixiao td")[c]).show();
                        }
                        else{
                            $($("#table_yubaoshixiao td")[c]).hide();
                        }
                    }
                }
                else if(strTimes=="旬"){
                    var maxSpanTime=t.numbers[t.numbers.length-1];
                    var maxDay=maxSpanTime/24;
                    var maxXum=maxDay/10;
                    for(var c=0;c< t.numbers.length;c++){
                        if(c<maxXum){
                            $($("#table_yubaoshixiao td")[c]).show();
                        }
                        else{
                            $($("#table_yubaoshixiao td")[c]).hide();
                        }
                    }
                }
            }
            changeTimePanel(true);//第一次調用
        }
    };

    //点击事件
    this.clickHandle = function(number){
        this.hourSpan = Number(number)*24;
    };
    this.createDom(startDate);
}