/*
* 主程序
* by zouwei, 2015-05-10
* */
var GDYB = {};
//page类，负责页面公用js逻辑
GDYB.Page = {
    curPage:null,
    //绑定页面事件
    bindPageEvent:function(){
        var t = this;
        //给导航按钮绑定事件

        //数据浏览按钮事件
        $("#sjll_btn").click(function(){
            if(!$(this).hasClass("active")){
                $(".navigation").find("button.active").removeClass("active");
                $(this).addClass("active");
                t.curPage&&t.curPage.destroy();
                t.curPage = GDYB.SZMSPage;
                GDYB.SZMSPage.active();

                $("#nav_menu").html("<li id='menu_szms'>"
                    +"        <a data-index='3'>"
                    +"            <img src='imgs/img_level.png'>"
                    +"                <span>数值模式</span>"
                    +"            </a></li>");
                GDYB.SideWrapper.setActive("menu_szms");
                GDYB.SideWrapper.register();
                
                 $("#gridws").remove();
            }
        });
        //格点预报按钮事件
        $("#gdyb_btn").click(function(){
            if(!$(this).hasClass("active")){
                $(".navigation").find("button.active").removeClass("active");
                $(this).addClass("active");
                t.curPage&&t.curPage.destroy();
                t.curPage = GDYB.GDYBPage;
                GDYB.GDYBPage.active();

                $("#nav_menu").html("<li id='cfs'>"
                +   "<a data-index='1'>"
                +"    <img src='imgs/img_level.png'>"
                +"        <span>CFS</span>"
                +"    </a></li>"
                +"<li id='derf'>"
                +"    <a data-index='2'>"
                +"        <img src='imgs/img_level.png'>"
                +"            <span>Derf</span>"
                +"        </a></li>"
                +"<li id='last'>"
                +"    <a data-index='2'>"
                +"        <img src='imgs/img_level.png'>"
                +"            <span>上一期</span>"
                +"        </a></li>");
                GDYB.SideWrapper.setActive("");
                GDYB.SideWrapper.register();
                
                 $("#gridws").remove();
            }
        });
        $("#zsyb_btn").click(function(){
            if(!$(this).hasClass("active")){
                $(".navigation").find("button.active").removeClass("active");
                $(this).addClass("active");
                t.curPage&&t.curPage.destroy();
                t.curPage = GDYB.LSWPage;
                GDYB.LSWPage.active();
                $("#nav_menu").html("<li id='lsw'>"
                +   "<a data-index='1'>"
                +"    <img src='imgs/img_level.png'>"
                +"        <span>LSW</span>"
                +"    </a></li>"
                +"<li id='mjo'>"
                +"    <a data-index='2'>"
                +"        <img src='imgs/img_level.png'>"
                +"            <span>MJO</span>"
                +"        </a></li>");
                GDYB.SideWrapper.setActive("lsw");
                GDYB.SideWrapper.register();
                 $("#gridws").remove();
            }
        });
        //站点预报按钮事件
        $("#zdyb_btn").click(function(){
            if(!$(this).hasClass("active")){
                $(".navigation").find("button.active").removeClass("active");
                $(this).addClass("active");
                t.curPage&&t.curPage.destroy();
                t.curPage = GDYB.ZDYBPage;
                GDYB.ZDYBPage.active();
                $("#nav_menu").html("<li id='InitialField'>"
                    +"<a data-index='1'>"
                    +"    <img src='imgs/img_level.png'>"
                    +"        <span>分县预报</span>"
                    +"    </a></li>");

                $("#gridws").remove();
            }
        });
        //预报测评按钮事件
        $("#ybcp_btn").click(function(){
            if(!$(this).hasClass("active")){
                $(".navigation").find("button.active").removeClass("active");
                $(this).addClass("active");
                t.curPage&&t.curPage.destroy();
                t.curPage = GDYB.YBCPPage;
                GDYB.YBCPPage.active();
                $("#nav_menu").html("<li id='dz'>"
                    +"<a data-index='1'>"
                    +"    <img src='imgs/img_level.png'>"
                    +"        <span>单站</span>"
                    +"    </a></li>"
                    +"<li id='qy'>"
                    +"<a data-index='1'>"
                    +"    <img src='imgs/img_level.png'>"
                    +"        <span>区域</span>"
                    +"    </a></li>"
                    +"<li id='dzsk'>"
                    +"<a data-index='1'>"
                    +"    <img src='imgs/img_level.png'>"
                    +"        <span>单站实况</span>"
                    +"    </a></li>");
                GDYB.SideWrapper.setActive("dz");
                GDYB.SideWrapper.register();

                $("#gridws").remove();
            }
        });
        //系统管理按钮事件
        $("#xtgl_btn").click(function(){
            window.open(host+"/qhyc/display.html");
          /*  if(!$(this).hasClass("active")){
                $(".navigation").find("button.active").removeClass("active");
                $(this).addClass("active");
                t.curPage&&t.curPage.destroy();
                t.curPage = GDYB.XTGLPage;
                GDYB.XTGLPage.active();
                $("#nav_menu").html("<li id='qygl'>"
                    +"<a data-index='1'>"
                    +"    <img src='imgs/img_level.png'>"
                    +"        <span>区域管理</span>"
                    +"    </a></li>"
                    +"<li id='zdyb'>"
                    +"<a data-index='1'>"
                    +"    <img src='imgs/img_level.png'>"
                    +"        <span>站点预报</span>"
                    +"    </a></li>"
                    +"<li id='zddr'>"
                    +"<a data-index='1'>"
                    +"    <img src='imgs/img_level.png'>"
                    +"        <span>站点管理</span>"
                    +"    </a></li>");
                GDYB.SideWrapper.setActive("qygl");
                GDYB.SideWrapper.register();

                $("#gridws").remove();
            }*/
        });
        //给左侧菜单按钮绑定事件
        $("#displayBtn").click(function(){
            if($(this).html()=="&lt;"){//<
                $(this).html("&gt;");//>
                $("#menu").removeClass("menu_normal").addClass("menu_minimize");
                $("#map_div").removeClass("map_normal").addClass("map_maximize");
                //$("#gridDiv").removeClass("grid_normal").addClass("grid_maximize");
                $("#gridws").css("left","56px"); // 改变grid工作空间的宽度自适应屏幕宽度
                $("#gridDiv").css("width","1690px");
                $("#menu_bd").css("display","none");
                $("#ZDYBDiv").css("width",parseInt($("#ZDYBDiv").css("width"))+338);//左侧面板宽度77px
                $("#zdybHeaderdiv").css("width",parseInt($("#zdybHeaderdiv").css("width"))+338);
                $("#zdybMaindiv").css("width",parseInt($("#zdybMaindiv").css("width"))+338);
                $("#YBCPDiv").css("width",parseInt($("#YBCPDiv").css("width"))+338);//预报测评
                $("#YBCPDiv").css("left",60);//预报测评
                t.curPage.map.updateSize();
            }
            else{
                $(this).html("&lt;");//<
                $("#menu").removeClass("menu_minimize").addClass("menu_normal");
                $("#map_div").removeClass("map_maximize").addClass("map_normal");
               // $("#gridDiv").removeClass("grid_maximize").addClass("grid_normal");
                $("#gridws").css("left","396px"); // 改变grid工作空间的宽度自适应屏幕宽度
                $("#gridDiv").css("width","1350px");
                $("#menu_bd").css("display","block");
                $("#zdybHeaderdiv").css("width",parseInt($("#zdybHeaderdiv").css("width"))-338);
                $("#zdybMaindiv").css("width",parseInt($("#zdybMaindiv").css("width"))-338);
                $("#ZDYBDiv").css("width",parseInt($("#ZDYBDiv").css("width"))-338);//左侧面板宽度415px
                $("#YBCPDiv").css("width",parseInt($("#YBCPDiv").css("width"))-338);//预报测评
                $("#YBCPDiv").css("left",400);//预报测评
                t.curPage.map.updateSize();
            }
        });
    },
    //入口方法
    main:function(){
    	
        this.bindPageEvent();
        GDYB.SZMSPage.active();
        this.curPage = GDYB.SZMSPage;

        GDYB.SideWrapper.setActive("menu_szms");
        GDYB.SideWrapper.register();

        var userName = $.cookie("userName");
        var password = $.cookie("password");
        if (userName != null && password != null) {
            GDYB.GridProductClass.currentUserName = userName;
            if(typeof($("#span_user")[0]) != "undefined" && typeof($("#a_exit")[0]) != "undefined"){
                $("#span_user")[0].innerHTML = $.cookie("showName");
                $("#a_exit")[0].innerHTML = "退出";
            }
        }
        else{
            if(typeof($("#a_exit")[0]) != "undefined")
                $("#a_exit")[0].innerHTML = "登录";
        }
    }
}



GDYB.SZMSPage = new SZMSPageClass();
GDYB.GDYBPage = new GDYBPageClass();
GDYB.ZDYBPage = new ZDYBPageClass();
GDYB.XTGLPage = new XTGLPageClass();
GDYB.YBCPPage = new YBCPPageClass();
GDYB.YBCPSKPage = new YBCPSKPageClass();
GDYB.QYCPPage = new QYCPPageClass();
GDYB.ZDYBSZPageClass = new ZDYBSZPageClass();
GDYB.LSWPage=new LSWPageClass();
GDYB.MJOPage=new MJOPageClass();


GDYB.GridProductClass = new GridProductClass();
GDYB.RadarDataClass = new RadarDataClass();
GDYB.AWXDataClass = new AWXDataClass();
GDYB.MicapsDataClass = new MicapsDataClass();
GDYB.TextDataClass = new TextDataClass();

GDYB.ChartClass = new ChartClass();

GDYB.SideWrapper = new SideWrapper();

GDYB.Legend = new Legend();

GDYB.CorrectAction = new CorrectAction();
GDYB.MagicTool = new MagicTool();
GDYB.FilterTool = new FilterTool();

function InputOnChange(e){
    GDYB.ChartClass.refreshChart(e.id, e.value);
}

