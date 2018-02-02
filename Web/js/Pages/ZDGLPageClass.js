/**
 * Created by Administrator on 2016/3/29.
 */
/**
 * @author zj
 * 2016-3-29
 * @description 站点导入页面
 */

function ZDGLPageClass(){
    var t= this;
    this.num = 0;
    this.renderMenu = function(){
        var htmlStr = "<div id='div_datetime' style='padding-left:10px'>"
            +"<div id='div_datetime' ><div class='title1'>站点管理</div>"
            +"<div class='btn_line'><button style='margin-top:10px;' id='downTemplate'>下载模板</button><button style='margin-top:10px;' id='uploadStations'>导入站点</button>";
        $("#menu_bd").html(htmlStr);

    //为导入区域绑定事件
        $("#uploadStations").click(function(){
            $.ajax({
                type: 'post',
                url: Url_Config.gridServiceUrl + "services/ForecastfineService/getZDYBStationType",
                data: null,
                dataType: 'json',
                error: function () {
                    alert('获取制作时间出错!');
                },
                success: function (data) {
                    var options = "";
                    for(var i=0;i<data.length;i++){
                       options += "<option flag='"+data[i].type+"'>"+data[i].name+"</option>";
                        if(parseInt(data[i].type)> t.num){
                            t.num = parseInt(data[i].type);
                        }
                    }
                    var url =  Url_Config.gridServiceUrl + "services/ForecastfineService/uploadStations";
                    var d = dialog({
                        title: '导入站点',
                        content: '<form id="areaExportForm" action='+url+' enctype="multipart/form-data" method="post"><div style="height: 40px;">' +
                            '<span style="float: left;line-height: 28px;margin: 2px 5px 0px 0px;">站点表格</span>' +
                            '<input id="areaInputValue" style="height:30px;width:180px;float:left;margin-right: 3px;" name="areaInputValue" type="text">' +
                            '<a href="javascript:;" style="height:30px;" class="a-upload"><input type="file" style="height:30px;margin-top: -10px;float: right;width: 50px;" name="selectFile" id="selectFile" onChange="$(this).parent().prev().val(this.value)">浏览</a></div>' +
                            '<div><span style="float: left;line-height: 28px;margin: 2px 5px 0px 0px;">站点类型</span>' +
                            '<input id="addStationType" style="height:30px;width:180px;float:left;display: none;margin: 0px 3px 0px 0px" type="text" onchange="setStationType(this)" flag="addStationType" typeNum="'+(t.num+1)+'">' +
                            '<select id="StationType" name="addStationName"  style="width:180px;margin: 0px 3px 0px 0px;" onchange="setStationType(this)" flag="stationType">'+options+'</select>' +
                            '<input id="addStationInput" name="stationType" style="display:none;" type="text" value="'+data[0].type+'"><a onclick="addStation(this);" href="javascript:;" style="height:30px;" class="a-upload">新增</a></div></form>',
                        ok: function () {
                            var areaValue = $("#areaInputValue").val();
                            var array = new Array();
                            array = areaValue.split(".");
                            if(array[array.length-1]!="xlsx"&&array[array.length-1]!="xls"){
                                this.title("请上excel文件!");
                                return false;
                            }else{
                                $("#areaExportForm").ajaxSubmit(function(data){
                                    alert(data);
                                });
                            }
                        },
                        cancelValue: '取消',
                        cancel: function () {}
                    });
                    d.showModal();
                }
            });
        });
        $("#downTemplate").click(function(){
            var url = "./xml/ZDTemplate.xlsx";
            window.open(url);
        });
    }
}
function addStation(obj){
    if($(obj).html()=="新增"){
        $(obj).html("返回");
        $("#addStationType").css("display","");
        $("#addStationType").attr("name","addStationName");
        $("#StationType").css("display","none");
        $("#StationType").attr("name","");
        $("#addStationType")[0].focus();
        $("#addStationInput").attr("name","addStationType");
    }
    else{
        $(obj).html("新增");
        $("#addStationType").css("display","none");
        $("#addStationType").attr("name","");
        $("#StationType").css("display","");
        $("#StationType").attr("name","addStationName");
        $(obj)[0].onfocus();
        $("#addStationInput").attr("name","stationType");
    }
}
function setStationType(obj){
    if($(obj).attr("flag")=="addStationType"){
        $("#addStationInput").val($(obj).attr("typeNum"))
    }
    else{
        $("#addStationInput").val($(obj).find("option:selected").attr("flag"));
    }
}

ZDGLPageClass.prototype = new PageBase();
