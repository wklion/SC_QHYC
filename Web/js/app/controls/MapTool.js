/**
 * @author:杠上花
 * @date:2017-12-05
 * @modifydate:
 * @param:
 * @return:
 * @description:格点工具
 */
define(["vue","displayUtil",'vlegend','Common','processControl','layerManageUtil','gridUtil'], function(Vue,displayUtil,vlegend,com,processControl,lmu,gridUtil) {
    return{
        myVue:null,
        style:null,//风格
        flag:0,//要素
        year:-1,//年份
        month:-1,//月份
        realData:[],//距平数据
        Init:function(id){
            var me = this;
            $("#"+id).append(`
                <div id="mapTool" class="mapTool delete">
                    <button :class="{active:item.isActive}" class="btn btn-default" v-for="item in items" :id="item.id" @click="btnClick(item)">{{ item.name }}</button>
                </div>
            `);
            initRes();
            function initRes(){
                vlegend.Init("map");
                me.myVue = new Vue({
                    el:"#mapTool",
                    data:{
                        items:[{
                            id:"fillColor",
                            name:"填图",
                            isActive:false
                        },
                        {
                            id:"fillVal",
                            name:"填值",
                            isActive:false
                        },
                        {
                            id:"contourLine",
                            name:"等值线",
                            isActive:false
                        },
                        {
                            id:"invert",
                            name:"反演",
                            isActive:false
                        },
                        {
                            id:"station",
                            name:"站名",
                            isActive:false
                        }]
                    },
                    methods:{
                        btnClick:function(item){
                            var id = item.id;
                            item.isActive = !item.isActive;
                            if(id==="fillColor"){
                                this.fillColor(item);
                            }
                            else if(id === "fillVal"){
                                this.fillVal(item);
                            }
                            else if(id === "contourLine"){
                                this.contourLine(item);
                            }
                            else if(id === "station"){
                                if(item.isActive){
                                    this.showStationName();
                                }
                                else{
                                    lmu.Remove("站点");
                                }
                            }
                            else if(id === "invert"){
                                this.invert(item);
                            }
                        },
                        fillColor:function(){
                        },
                        /**
                         * @author:杠上花
                         * @date:2017-12-10
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:显示站名
                         */
                        showStationName:function(){
                            processControl.show("正在请求站点数据!");
                            com.getAllStation(function(data){
                                //成功
                                displayUtil.displayStationLayer(data);
                                processControl.hide("站点数据显示完成!");
                            },function(){
                                //失败
                                processControl.hide("站点数据请求失败!");
                            })
                        },
                        invert:async function(){
                        },
                        /**
                         * @author:杠上花
                         * @date:2017-12-27
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:填值
                         */
                        fillVal:function(){

                        },
                        /**
                         * @author:杠上花
                         * @date:2017-12-30
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:等值线
                         */
                        contourLine:function(){

                        },
                        /**
                         * @author:杠上花
                         * @date:2017-1-31
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:填值实现
                         */
                        fillValHelper:function(data){
                            var me = this;
                            var name = "填值";
                            if(data==null){
                                processControl.hide("无数据");
                                return;
                            }
                            var layer = lmu.addLayer(name, "vector", null,"dot");
                            layer.removeAllFeatures();
                            var features = [];
                            data.forEach(item=>{
                                let lon=item.longitude==undefined?item.lon:item.longitude;
                                let lat=item.latitude==undefined?item.lat:item.latitude;
                                var val = item.value;
                                val = parseInt(val);
                                var geometry = new WeatherMap.Geometry.Point(lon,lat);
                                var color = "blue";
                                if(val<0){
                                    color = "red";
                                }
                                var style = {
                                    label:val+"",
                                    fontColor:color,
                                    fontSize:"1em",
                                    strokeColor:"black", 
                                    strokeOpacity:1,
                                    strokeWidth:1,
                                    pointRadius:2,
                                    labelXOffset:5,
                                    labelYOffset:5
                                }
                                var pointFeature = new WeatherMap.Feature.Vector(geometry,null,style);
                                features.push(pointFeature);
                            });
                            layer.addFeatures(features);
                            layer.redraw();
                            me.items[0].isActive = false;
                            document.getElementById("fillColor").click();
                            vueTitle.type = "距平";
                            if(me.items[2].isActive){
                                me.contourLine(me.items[2]);
                            }
                        },
                        /**
                         * @author:杠上花
                         * @date:2017-1-31
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:填图实现
                         */
                        fillColorHelper:function(name,data,style){
                            var me = this;
                            var layer = lmu.addLayer(name, null, null,"grid");
                            var dg = gridUtil.interpolate(data);
                            layer.items = style;
                            layer.setDatasetGrid(dg);
                            layer.refresh();
                        },
                        /**
                         * @author:杠上花
                         * @date:2017-1-31
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:反演实现
                         */
                        invertHelper:async function(elementID,strMonths,data,name){
                            var me = this;
                            //取消填值选中
                            lmu.Remove("填值");
                            var fillValObj = null;
                            me.items.forEach(item=>{
                                if(item.id === "fillVal"){
                                    fillValObj = item;
                                    return;
                                }
                            });
                            fillValObj.isActive = false;
                            //去掉填值
                            me.items.forEach(item=>{
                                if(item.id === "fillVal"){
                                    item.isActive = false;
                                    return;
                                }
                            });
                            //请求距平
                            var flag = elementID == "temp"?1:0;
                            var jpData = await com.getJPMonthData(flag);
                            if(jpData.suc==undefined||jpData.suc==null){
                                processControl.hide("距平数据请求失败!");
                                return;
                            }
                            var mapData = com.convertStationDataToMap(jpData.suc);
                            var months = [];
                            strMonths.forEach(item=>{
                                var month = item.substring(4,6);
                                month = parseInt(month);
                                months.push(month);
                            });
                            me.realData = [];
                            var monthSize = months.length;
                            data.forEach(item=>{
                                let sn = item.stationNum;
                                let obj = mapData.get(sn);
                                let val = item.value;
                                var jpValSum = 0;
                                months.forEach(monthItem=>{
                                    let tempVal = com.getMonthStationData(obj,monthItem);
                                    jpValSum += tempVal;
                                });
                                let jpVal = jpValSum/monthSize;
                                let newVal = 0;
                                if(elementID === "temp"){
                                    newVal = val+jpVal;
                                }
                                else{
                                    newVal = (val*jpVal)/100+jpVal;
                                }
                                var newItem = $.extend(true,{},item);
                                newItem.value = newVal;
                                me.realData.push(newItem);
                            });
                            var layer = lmu.addLayer(name, "vector", null,"dot");
                            layer.removeAllFeatures();
                            var features = [];
                            me.realData.forEach(item=>{
                                let lon=item.longitude==undefined?item.lon:item.longitude;
                                let lat=item.latitude==undefined?item.lat:item.latitude;
                                var val = item.value;
                                val = parseInt(val);
                                var geometry = new WeatherMap.Geometry.Point(lon,lat);
                                var color = "blue";
                                if(val<0){
                                    color = "red";
                                }
                                var style = {
                                    label:val+"",
                                    fontColor:color,
                                    fontSize:"1em",
                                    strokeColor:"#339933", 
                                    strokeOpacity:1,
                                    strokeWidth:1,
                                    pointRadius:2,
                                    labelXOffset:1,
                                    labelYOffset:1 
                                }
                                var pointFeature = new WeatherMap.Feature.Vector(geometry,null,style);
                                features.push(pointFeature);
                            });
                            layer.addFeatures(features);
                            layer.redraw();
                            //填图
                            var fillColorObj = null;
                            me.items.forEach(item=>{
                                if(item.id === "fillColor"){
                                    fillColorObj = item;
                                    return;
                                }
                            });
                            
                            if(!fillColorObj.isActive){
                                return;
                            }
                            fillColorObj.isActive = false;
                            document.getElementById("fillColor").click();
                            vueTitle.type = "值";
                            if(me.items[2].isActive){
                                me.contourLine(me.items[2]);
                            }
                        },
                        /**
                         * @author:杠上花
                         * @date:2017-1-31
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:等值线实现
                         */
                        contourLineHelper:function(name,data,style){
                            var me = this;
                            var tepData = null;
                            if(me.items[3].isActive){
                                tepData = me.realData;
                            }
                            else{
                                tepData = data;
                            }
                            if(tepData == null){
                                return;
                            }
                            var dg = gridUtil.interpolate(tepData);
                            if(dg == null){
                                return;
                            }
                            var dZValues = [];
                            let styleSize = style.length;
                            for(let i=0;i<styleSize;i++){
                                let val=style[i].end;
                                dZValues.push(val);
                            }
                            var contour = new WeatherMap.Analysis.Contour();
                            var result = contour.analysis(dg, dZValues);
                            var features = [];
                            var resultSize=result.length;
                            if(resultSize > 0){
                                for(var i=0;i<resultSize;i++){
                                    var geoline = result[i].geoline;
                                    var dZValue = result[i].dZValue;
                                    var feature = new WeatherMap.Feature.Vector(geoline);
                                    feature.attributes.dZValue = dZValue.toString();
                                    features.push(feature);
                                }
                            }
                            var layer = lmu.addLayer(name, "vector", null,"line","Contour");
                            layer.renderer.labelField = "dZValue";
                            layer.style = {
                                fontFamily:"Arial",
                                fontColor:"red",
                                fontSize:"16px",
                                fontWeight:"bold",
                                strokeColor: "#c47a55",
                                strokeWidth: 1.0
                            };
                            layer.removeAllFeatures();
                            layer.addFeatures(features);
                            layer.redraw();
                        }
                    }
                });
            }
        }
    }
});