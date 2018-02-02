define(['jquery'],function($){
    return {
        ActiveButton:function(){
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
        },
        ToggleButton:function(){
            if($(this).hasClass("active")){
                $(this).removeClass("active");
            }
            else{
                $(this).addClass("active");
            }
        },
        MapSpread:function(){//地图最大最小
            var val=$(this).attr("val");
            if(val==0){//放大
                //改变图标
                $(this).children("span").removeClass("glyphicon-chevron-left");
                $(this).children("span").addClass("glyphicon-chevron-right");
                //地图缩放
                $("#left").css("display","block");
                $(this).attr("val",1);
            }
            else{
                //改变图标
                $(this).children("span").removeClass("glyphicon-chevron-right");
                $(this).children("span").addClass("glyphicon-chevron-left");
                //地图缩放
                $("#left").css("display","none");
                $(this).attr("val",0);
            }
        },
        ChangeDisplay:function(id){
            var self=require('commonfun');
            //初始化
            if(id=="dismap"){
                $("#chart").addClass("hidden");
                $("#map").removeClass("hidden");
                require(['maputil'],function(mu){
                    //mu.initTDT();
                    mu.initWeatherMap();
                    require(['data'],function(d){
                        let data=d.GetStationData();
                        if(data!=null){
                            self.Display(data);
                        }
                    });
                });
            }
            else{
                $("#map").addClass("hidden");
                $("#chart").removeClass("hidden");
                require(['ChartService'],function(cs){
                    cs.init();
                    //cs.initmap();
                    require(['data'],function(d){
                        let data=d.GetData();
                        if(data!=null){
                            self.Display(data);
                        }
                    });
                });
            }
        },
        GetStation:function(recall){
            var url = "http://127.0.0.1:8080/MGDemo/services/DataQuery/GetAllStation";
            var paramdata="";
            var errortext="请求站点失败!";
            this.AJAX(url,paramdata,errortext,recall);
        },
        AJAX:function(url,paramdata,errRecall,sucRecall){
            $.ajax({
                type: 'post',
                url: url,
                data: {'para':paramdata},
                dataType: 'json',
                error: function (data) {
                    errRecall(data);
                },
                success: function(data){
                    sucRecall(data);
                }
            });
        },
        Query:function(){
            //获取显示方式
            var typeid=$("#type button.active")[0].id;
            var typename=$("#type button.active")[0].innerText;
            if(typeid==undefined){
                console.log("类型为空!");
                return;
            }
            //取元素
            var elementid=$("#element button.active")[0].id;
            var elementname=$("#element button.active")[0].innerText;
            if(elementid==undefined){
                console.log("要素为空!");
                return;
            }
            //获取日期
            var datetime=$("#datepicker").datepicker('getDate').format("yyyy-MM-dd");

            var self=require('commonfun');
            if(typeid=="live"){
                let danwei=elementid=="temp"?"℃":"mm";
                self.QueryLive(elementid,datetime,danwei);
            }
            else if(typeid=="hos"){
                let danwei=elementid=="temp"?"℃":"mm";
                self.QueryHos(elementid,datetime,danwei);
            }
            else if(typeid=="jp"){
                let danwei=elementid=="temp"?"℃":"%";
                self.QueryJP(elementid,datetime,danwei);
            }
            else if(typeid=="nh"){
                let danwei=elementid=="temp"?"℃":"mm";
                self.QueryNH(elementid,datetime,danwei);
            }   
        },
        QueryLive:function(elementid,datetime,danwei){
            let url = Url_Config.gridServiceUrl+"services/DBService/GetLiveYearMonthData";
            var paramdata="{elementid:'" + elementid +"',datetime:'"+datetime +"'}";
            var errortext="请求站点实况请求失败!";
            var self=require('commonfun');
            self.AJAX(url,paramdata,errortext,function(data){
                if(data!=undefined){
                    self.Display(data,danwei);
                    require(['data'],function(d){
                        d.SetStationData(data);
                    });
                }
                //var mu=require('maputil');
                //mu.DisplayByIDW(data);
                /*var label=["1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"];
                var legend=[];
                elementname="实况"+elementname;
                legend.push(elementname);
                require(['ChartService'],function(cs){
                    if(isadd){
                        cs.adddata(elementname,data,label);
                    }
                    else{
                        cs.updatedata(elementname,data,label);
                    }
                });*/
            });
        },
        QueryHos:function(elementid,datetime,danwei){
            let url = Url_Config.gridServiceUrl+"services/DBService/GetHosYearMonthData";
            var paramdata="{elementid:'" + elementid +"',datetime:'"+datetime +"'}";
            var errortext="历史数据请求失败!";
            var self=require('commonfun');
            self.AJAX(url,paramdata,errortext,function(data){
                if(data!=undefined){
                    self.Display(data,danwei);
                    require(['data'],function(d){
                        d.SetStationData(data);
                    });
                }
            });
        },
        //距平
        QueryJP:function(elementid,datetime,danwei){
            var url = "http://127.0.0.1:8080/MGDemo/services/DataQuery/GetMonthJP";
            var paramdata="{elementid:'" + elementid +"',datetime:'"+datetime +"'}";
            var errortext="距平请求失败!";
            var self=require('commonfun');
            self.AJAX(url,paramdata,errortext,function(data){
                if(data!=undefined){
                    self.Display(data,danwei);
                    require(['data'],function(d){
                        d.SetStationData(data);
                    });
                }
            });
        },
        //拟合
        QueryNH:function(elementid,datetime,danwei){
            var url = "http://127.0.0.1:8080/MGDemo/services/DataQuery/GetNiHeByMonth";
            var paramdata="{elementid:'" + elementid +"',datetime:'"+datetime +"'}";
            var errortext="拟合请求失败!";
            var self=require('commonfun');
            self.AJAX(url,paramdata,errortext,function(data){
                if(data!=undefined){
                    self.Display(data,danwei);
                    require(['data'],function(d){
                        d.SetStationData(data);
                    });
                }
            });
        },
        Display:function(data,danwei){
            var displayid=$("#display button.active")[0].id;
            if(displayid=="dismap"){
                var mu=require('maputil');
                mu.DisplayByIDW(data);
            }
            else{
                let elementname=$("#element button.active")[0].innerText;
                var typename=$("#type button.active")[0].innerText;
                var name=typename+"("+elementname+")";
                let displaymethod=$("#opration button.active")[0].id;
                var label=[];
                var legend=[];
                data.forEach((item,i)=>{
                    let sn=item.stationName;
                    label.push(sn);
                });
                legend.push(name);
                require(['ChartService','danwei'],function(cs,dw){
                    if(danwei==undefined){
                        danwei=dw.GetForcastDanWei();
                    }
                    if(displaymethod=="queryandadd"){
                        cs.adddata(name,data,label,danwei);
                    }
                    else if(displaymethod=="query"){
                        cs.updatedata(name,data,label,danwei);
                    }
                });
            }
        },
        DataQuery:function(){
            var id=this.id;
            var isadd=id=="query"?false:true;
            var self=require('commonfun');
            //取元素
            var elementid=$("#element button.active")[0].id;
            var elementname=$("#element button.active")[0].innerText;

            //取类型
            var typeid=$("#type button.active")[0].id;
            var typename=$("#type button.active")[0].innerText;
            if(elementid==undefined){
                console.log("类型为空!");
                return;
            }
            //获取日期
            var datetime=$("#datepicker").datepicker('getDate').format("yyyy-MM-dd");
            var month=Number(datetime.substring(5,7));
            if(typeid=="live"){
                var url = "http://127.0.0.1:8080/MGDemo/services/DataQuery/GetLiveByStation";
                var paramdata="{element:'" + elementid +"',stationnum:'"+stationnum+"',datetime:'"+datetime +"'}";
                var errortext="请求站点实况请求失败!";
                self.AJAX(url,paramdata,errortext,function(data){
                    var label=["1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"];
                    var legend=[];
                    elementname="实况"+elementname;
                    legend.push(elementname);
                    require(['ChartService'],function(cs){
                        if(isadd){
                            cs.adddata(elementname,data,label);
                        }
                        else{
                            cs.updatedata(elementname,data,label);
                        }
                    });
                });
            }
            else if(typeid=="hos"){
                elementname="历史平均"+elementname;
                var url = "http://127.0.0.1:8080/MGDemo/services/DataQuery/GetHosByStation";
                var paramdata="{element:'" + elementid +"',stationnum:'"+stationnum +"'}";
                var errortext="站点历史请求失败!";
                self.AJAX(url,paramdata,errortext,function(data){
                    var label=["1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"];
                    var legend=[];
                    legend.push(elementname);
                    require(['ChartService'],function(cs){
                        if(isadd){
                            cs.adddata(elementname,data);
                        }
                        else{
                            cs.updatedata(elementname,data);
                        }
                    });
                });
            }
            else if(typeid=="jp"){
                elementname=elementname+"距平";
                var url = "http://127.0.0.1:8080/MGDemo/services/DataQuery/GetJPByMonth";
                var paramdata="{element:'" + elementid +"'}";
                var errortext="站点历史请求失败!";
                self.AJAX(url,paramdata,errortext,function(data){
                    var label=["1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"];
                    var legend=[];
                    legend.push(elementname);
                    require(['ChartService'],function(cs){
                        if(isadd){
                            cs.adddata(elementname,data);
                        }
                        else{
                            cs.updatedata(elementname,data);
                        }
                    });
                });
            }
            else if(typeid=="nhjp"){
                elementname=elementname+"拟合距平";
                var url = "http://127.0.0.1:8080/MGDemo/services/SynthesizeService/GetNiHeOfJP";
                var paramdata="{element:'" + elementid +"'}";
                var errortext="站点拟合距平请求失败!";
                self.AJAX(url,paramdata,errortext,function(data){
                    var label=["1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"];
                    var legend=[];
                    legend.push(elementname);
                    require(['ChartService'],function(cs){
                        if(isadd){
                            cs.adddata(elementname,data);
                        }
                        else{
                            cs.updatedata(elementname,data);
                        }
                    });
                });
            }
        },
        Test:function(){
            require(['maputil'],function(mu){
                console.log(mu.GetMap());
            });
        },
        GetGridURL:function(){
            return Url_Config.gridServiceUrl;
        },
        convertStringToDate:function(str){
            var year = parseInt(str.substring(0,4));
            var month = parseInt(str.substring(4,6));
            var day = parseInt(str.substring(6,8));
            var date = new Date(year,month-1,day);
            return date;
        },
        /**
         * @author:wangkun
         * @date:2017-09-10
         * @param:datasetgrid-格点数据,elementid-要素id,valType-数据类型
         * @return:
         * @description:数据处理
         */
        datasetGridDeal:function(datasetgrid,elementid,valType){
            var rows = datasetgrid.rows;
            var cols = datasetgrid.cols;
            var grid = datasetgrid.grid;
            if(elementid === "prec"){
                if(valType === "zhi"){
                    for(var i=0,total=grid.length;i<total;i++){
                        grid[i] = grid[i]*3600*6*10000;
                    }
                }
            }
            if(elementid === "temp"){
                if(valType === "zhi"){
                    for(var i=0,total=grid.length;i<total;i++){
                        grid[i] = grid[i] - 273.15;
                    }
                }
            }
        },
        getStylebyElementID:function(elementID){
            var style = month_prec_model;
            if(elementID === "prec"){

            }
        }
    }
})