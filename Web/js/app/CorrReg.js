/**
 * @author:wangkun
 * @date:2017-11-12
 * @modifydate:
 * @param:
 * @return:
 * @description:回归模型
 */
var CorrRegModule = ['mapUtil','vue','Common','displayUtil','mapTool','processControl','layerManageUtil','gridUtil','vlegend'];
define(CorrRegModule, function(mapUtil,Vue,com,displayUtil,mapTool,processControl,lmu,gridUtil,vlegend) {
    return {
        elements: [
            { id: "prec", name: "降水",flag:0,isActive: true },
			{ id: "temp", name: "气温",flag:1,isActive: false }
        ],
        computes:[
            { id: "comFactor", name: "计算因子"},
			{ id: "comForecast", name: "预测结果"}
        ],
        data:null,//数据
        calData:null,
        /**
         * @author:wangkun
         * @date:2017-11-12
         * @modifydate:
         * @param:
         * @return:
         * @description:初始化,入口
         */
        Init:function(){
            var me = this;
            initRes();
            initEvent();
            mapUtil.map.updateSize();//更新地图
            function initRes(){
                $("#left").css("display","block");
                $("#left").html(`
                    <div id="element">
                        <div class="menu_title">要素</div>
                        <div class="row_3cols">
                            <button class="btn btn-default w100" v-for="(item,index) in elements" :flag="item.flag" :id="item.id" v-bind:class="{ 'active': item.isActive }" @click="click(item)">{{ item.name }}</button>
                        </div>
                    </div>
                    <div id="datetime">
                        <div class="menu_title">时间</div>
                        <div id="dateSelect" class="yearmonth">
                            <select v-model="yearSelected" @change="yearChange">
                                <option v-for="item in years" v-bind:value="item">
                                    {{ item }}
                                </option>
                            </select>
                            <select v-model="monthSelected" @change="monthChange">
                                <option v-for="item in months" v-bind:value="item">
                                    {{ item }}
                                </option>
                            </select>
                        </div>
                    </div>
                    <div id="compute">
                        <div class="menu_title">计算</div>
                        <div class="row_3cols">
                            <button class="btn btn-default w100" v-for="(item,index) in computes" :id="item.id"  @click="click(item)">{{ item.name }}</button>
                        </div>
                    </div>
                    <div id="factor" class="factor">
                        <span id="factorSet" @click="openSet" class="glyphicon glyphicon-cog factorset" title="配置"></span>
                        <span id="callFactor" @click="importFactorPre" class="glyphicon glyphicon-arrow-down callfactor" title="调入因子"></span>
                        <span id="factorSet" @click="saveSetPre" class="glyphicon glyphicon-saved savefactorset" title="保存配置"></span>
                        <h3>因子</h3>
                        <div>
                            <div class="factorTitle"><span style="flex:2;">名称</span><span>月份</span><span>相关系数</span><span>是否选中</span></div>
                            <div class="factor_select">
                                <ul>
                                    <li v-for="(item,index) in factor"><span :class="{hightlight:item.hightlight}" style="flex:2;">{{ item.name }}</span><span>{{ item.month }}</span><span>{{ item.val }}</span><span><input @click="checkClick(item)" type="checkbox" v-model="item.state"></span></li>
                                </ul>
                            </div>
                        </div>
                        <div class="search_div"><span>搜索因子:</span><input v-model="searchContent" @keyup="search"/></div>
                        <div id="saveCurSet" class="saveCurSet_div" :style="{display:saveSetDivVis}">
                            <input type="text" name="newFactorSetName" @keyup="checkName" placeholder="请输入名称..." v-model="inputSchemeName">
                            <button id="saveFactorScheme" @click="saveSet">确定</button><button id="cancelFactorScheme" @click="cancelSaveSet">取消</button>
                            <div class="invailidate" v-if="availableName">当前名字可用!</div>
                            <div class="noInvailidate" v-else>当前名字不可用!</div>
                        </div>
                        <div id="importFactor" class="importFactor" :style="{display:schemeSelectVis}">
                            <div class="title">可用因子</div>
                            <div>
                                <ul>
                                    <li @click="schemeSelect(item)" v-for="item in schemeS"><span>{{ item.name }}</span><span><input type="checkbox" v-model="item.isDefault"></span></li>
                                </ul>
                            </div>
                            <div class="op"><button class="btn btn-default" @click="importFactor">确定</button><button @click="schemeSelectClose" class="btn btn-default">取消</button></div>
                        </div>
                    </div>
                `);
                $("#right").append(`
                    <div id="factor-set" class="factor-setting-div delete" :style="{display:visiable}">
                        <span class="close" @click="close">&times;</span>
                        <h1>{{ title }}</h1>
                        <div class="month">
                            <div :class="{active:item.active}" v-for="item in months" :flag="item.month" @click="monthClick(item)">{{ item.name }}</div>
                        </div>
                        <div class="schemeName">
                            <span>选择方案：</span>
                            <select @change="schemeChange">
                                <option :id="scheme.id" v-for="scheme in schemes">{{ scheme.name }}</option>
                            </select>
                            <span class="close" title="删除当前方案" @click="deleteSchemePre">&times;</span>
                            <div class="isDelete" :style="{display:isDelete}"><button class="btn btn-success" @click="deleteScheme">确定</button><button @click="deleteSchemeCancel" class="btn btn-danger">取消</button></div>
                            <button class="btn" :class="{'btn-success':!schemeIsDefault}" :disabled="schemeIsDefault" @click="setDefaultScheme">设为默认方案</button>
                        </div>
                        <div class="factor-month"><span>分类</span><span style="flex:3;">名称</span><span>1月</span><span>2月</span><span>3月</span><span>4月</span><span>5月</span><span>6月</span><span>7月</span><span>8月</span><span>9月</span><span>10月</span><span>11月</span><span>12月</span></div>
                        <div class="factor-set-content">
                            <ul>
                                <li v-for="factor in factors">
                                    <span>{{ factor.category }}</span>
                                    <span style="flex:3;">{{ factor.name }}</span>
                                    <span><input type="checkbox" v-model="factor.month1"></span>
                                    <span><input type="checkbox" v-model="factor.month2"></span>
                                    <span><input type="checkbox" v-model="factor.month3"></span>
                                    <span><input type="checkbox" v-model="factor.month4"></span>
                                    <span><input type="checkbox" v-model="factor.month5"></span>
                                    <span><input type="checkbox" v-model="factor.month6"></span>
                                    <span><input type="checkbox" v-model="factor.month7"></span>
                                    <span><input type="checkbox" v-model="factor.month8"></span>
                                    <span><input type="checkbox" v-model="factor.month9"></span>
                                    <span><input type="checkbox" v-model="factor.month10"></span>
                                    <span><input type="checkbox" v-model="factor.month11"></span>
                                    <span><input type="checkbox" v-model="factor.month12"></span>
                                </li>
                            </ul>
                        </div>
                        <div class="factor-set-other">
                            <button @click="save" class="btn btn-default">保存</button>
                            <span class="saveStatus">{{ saveStatus }}</span>
                        </div>
                    </div>
                `);
                mapTool.Init("right");
                me.vueElement = new Vue({
					el: "#element",
					data: {
                        selectID:"prec",
						elements: me.elements
					},
					methods: {
						click: function (target) {
                            me.elementID = target.id;
                            this.selectID = target.id;
							me.elementName = target.name;
							me.elements.forEach(item => {
								if (item.id === me.elementID) {
                                    item.isActive = true;
                                    mapTool.flag = item.flag;
								}
								else {
									item.isActive = false;
								}
                            });
						}
					}
                });
                me.dateSelect = new Vue({
                    el: '#dateSelect',
                    data: {
                        yearSelected:1,
                        years: [],
                        monthSelected: 1,
                        months: [1,2,3,4,5,6,7,8,9,10,11,12]
                    },
                    methods:{
                        yearChange:function(target){
                            this.yearSelected = target.target.value;
                            mapTool.year = this.yearSelected;
                        },
                        monthChange:function(target){
                            this.monthSelected = target.target.value;
                            mapTool.year = this.monthSelected;
                        }
                    },
                    created: function () {
                        var now = new Date();
                        //年
                        var year = now.getFullYear();
                        this.yearSelected = year;
                        for(var i=0;i<30;i++){
                            this.years.push(year);
                            year--;
                        }
                        //月
                        var month = now.getMonth()+1;
                        this.monthSelected = month;
                        mapTool.month = month;
                    }
                });
                me.vueCompute = new Vue({
					el: "#compute",
					data: {
						computes: me.computes
					},
					methods: {
						click: function (target) {
                            var id = target.id;
                            var flag = 0;
                            me.vueElement.elements.forEach(item=>{
                                if(item.isActive){
                                    flag = item.flag;
                                    return;
                                }
                            });
                            console.log(flag);
                            if(id === "comFactor"){
                                processControl.show("正在计算因子!");
                                me.calCorrelation(flag);
                            }
                            else if(id === "comForecast"){
                                me.calForcast(flag);
                            }
						}
					}
                });
                me.vueFactor = new Vue({
                    el: '#factor',
                    data:{
                        searchContent:"",
                        inputSchemeName:"",
                        availableName:false,
                        saveSetDivVis:"none",
                        schemeSelectVis:"none",
                        factor:[],
                        schemeS:[]
                    },
                    methods:{
                        openSet:function(){
                            me.vueFactorSet.visiable = "block";
                        },
                        importFactorPre:async function(){
                            console.log("调入因子");
                            var data = await me.SchemeByMonthAndEle();
                            if(data.suc==null){
                                console.log(data.err);
                                processControl.hide("获取方案失败!");
                            }
                            else{
                                this.schemeS = [];
                                data.suc.forEach(item=>{
                                    item.isDefaul = item.isDefault==1?true:false;
                                });
                                this.schemeS = data.suc;
                            }
                            this.schemeSelectVis = "block";
                        },
                        saveSet:async function(){
                            if(this.factor.length<1){
                                processControl.hide("没有可保存的因子!");
                                return;
                            }
                            var result = await me.addFactorScheme(this.inputSchemeName);
                            me.saveCurFactorSet(result.suc,this.factor);
                        },
                        search:function(){
                            var sCon = this.searchContent;
                            if(sCon.length<1){
                                return;
                            }
                            this.factor.forEach(item=>{
                                item.hightlight = false;
                            });
                            var i = 0;
                            var size = this.factor.length;
                            for(i=0;i<size;i++){
                                let item = this.factor[i];
                                let name = item.name;
                                if(name.indexOf(sCon)>-1){
                                    item.hightlight = true;
                                    break;
                                }
                            }
                            var height = i*30;
                            $(".factor_select").scrollTop(height);
                        },
                        cancelSaveSet:function(){
                            this.saveSetDivVis = "none";
                        },
                        saveSetPre:function(){
                            this.saveSetDivVis = "block";
                        },
                        /**
                         * @author:wangkun
                         * @date:2017-11-12
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:检查名称是否可用
                         */
                        checkName:function(){
                            var self = this;
                            var param = {
                                name:self.inputSchemeName,
                                month:me.dateSelect.monthSelected
                            }
                            param = JSON.stringify(param);
                            var url = Url_Config.gridServiceUrl + "services/ForcastService/checkSchemeName";
                            com.AJAX(url, param,true,function(data){
                                self.availableName = false;
                            }, function(data){
                                if(data.suc==null){
                                    self.availableName = false;
                                    processControl.hide("检查名称失败!");
                                }
                                else{
                                    if(data.suc){
                                        self.availableName = true;
                                    }
                                    else{
                                        self.availableName = false;
                                    }
                                }
                            });
                        },
                        /**
                         * @author:wangkun
                         * @date:2017-11-12
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:方案选择
                         */
                        schemeSelect:function(target){
                            this.schemeS.forEach(item=>{
                                item.isDefault = false;
                            });
                            target.isDefault = true;
                        },
                        /**
                         * @author:wangkun
                         * @date:2017-11-12
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:方案选择关闭
                         */
                        schemeSelectClose:function(){
                            this.schemeSelectVis = "none";
                        },
                        /**
                         * @author:wangkun
                         * @date:2017-12-12
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:调入因子
                         */
                        importFactor:function(){
                            console.log("调入因子!");
                            var schemeID = 1;
                            this.schemeS.forEach(item=>{
                                if(item.isDefault){
                                    schemeID = item.id;
                                    return;
                                }
                            });
                            this.schemeSelectVis = "none";
                            if(this.factor.length==0){
                                processControl.hide("请先计算因子!");
                                return;
                            }
                            me.importSetFactor(schemeID);
                        },
                        checkClick:function(obj){
                            // var selectName = obj.name;
                            // this.factor.forEach(item=>{
                            //     let thisName = item
                            // });
                        }
                    }
                });
                me.vueFactorSet = new Vue({
                    el:"#factor-set",
                    data:{
                        visiable:"none",
                        title:"因子设置",
                        saveStatus:"",
                        isDelete:"none",
                        selectSchemeID:"",
                        schemeIsDefault:true,//是否默认方案
                        months:[
                            {
                                month:1,
                                name:"一月",
                                active:true    
                            },
                            {
                                month:2,
                                name:"二月",
                                active:false    
                            },
                            {
                                month:3,
                                name:"三月",
                                active:false    
                            },
                            {
                                month:4,
                                name:"四月",
                                active:false    
                            },
                            {
                                month:5,
                                name:"五月",
                                active:false    
                            },
                            {
                                month:6,
                                name:"六月",
                                active:false    
                            },
                            {
                                month:7,
                                name:"七月",
                                active:false    
                            },
                            {
                                month:8,
                                name:"八月",
                                active:false    
                            },
                            {
                                month:9,
                                name:"九月",
                                active:false    
                            },
                            {
                                month:10,
                                name:"十月",
                                active:false    
                            },
                            {
                                month:11,
                                name:"十一月",
                                active:false    
                            },
                            {
                                month:12,
                                name:"十二月",
                                active:false    
                            }
                        ],
                        factors:[],
                        schemes:[]
                    },
                    watch:{
                        visiable:async function(val){
                            if(val === "block"){
                                var data = await me.SchemeByMonthAndEle(1);
                                this.schemes = [];
                                if(data.suc){
                                    this.schemes = data.suc;
                                    let firstID = this.schemes[0].id;
                                    this.schemeIsDefault = this.schemes[0].isDefault == 1?true:false;
                                    this.selectSchemeID = firstID;
                                    this.getFactorSet(firstID);
                                }
                            }
                        }
                    },
                    methods:{
                        close:function(){
                            this.visiable = "none";
                        },
                        save:function(){
                            me.saveFactorSet(this.factors);
                        },
                        importFactor:function(){
                            me.importSetFactor();
                        },
                        monthClick:async function(obj){
                            this.months.forEach(item=>{
                                item.active = false
                            });
                            obj.active = true;
                            //me.getFactorSet(obj.month);
                            var data = await me.SchemeByMonthAndEle(obj.month);
                            this.schemes = [];
                            if(data.suc){
                                this.schemes = data.suc;
                                let firstID = this.schemes[0].id;
                                this.selectSchemeID = firstID;
                                this.schemeIsDefault = this.schemes[0].isDefault == 1?true:false;
                                this.getFactorSet(firstID);
                            }
                        },
                        /**
                         * @author:wangkun
                         * @date:2017-11-14
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:方案更改
                         */
                        schemeChange:function(e){
                            let name = e.target.value;
                            let scheme = this.getSchemeByName(name);
                            this.selectSchemeID = scheme.id;
                            this.schemeIsDefault = scheme.isDefault == 1?true:false;
                            var activeMonth = this.getActiveMonth();
                            me.getFactorSet(activeMonth,this.selectSchemeID);
                        },
                        getFactorSet:function(schemeID){
                            var activeMonth = this.getActiveMonth();
                            me.getFactorSet(activeMonth,schemeID);
                        },
                        getActiveMonth:function(){
                            var month = 1;
                            this.months.forEach(item=>{
                                if(item.active){
                                    month = item.month;
                                    return;
                                }
                            });
                            return month;
                        },
                        getSchemeByName:function(name){
                            var result = null;
                            this.schemes.forEach(item=>{
                                if(item.name === name){
                                    result = item;
                                }
                            });
                            return result;
                        },
                        deleteSchemePre:function(){
                            this.isDelete = "block";
                        },
                        /**
                         * @author:wangkun
                         * @date:2017-11-14
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:删除方案
                         */
                        deleteScheme:function(){
                            var T = this;
                            let url = Url_Config.gridServiceUrl + "services/ForcastService/deleteScheme";
                            var param = {
                                schemeID:this.selectSchemeID
                            };
                            param = JSON.stringify(param);
                            com.AJAX(url, param,true,function(){
                                processControl.hide("删除失败!");
                                this.isDelete = "none";
                            },async function(data){
                                if(data.suc==null){
                                    processControl.hide("删除失败!");
                                }
                                else{
                                    processControl.hide("删除成功!");
                                    T.isDelete = "none";
                                    let month = T.getActiveMonth();
                                    var data = await me.SchemeByMonthAndEle(month);
                                    T.schemes = [];
                                    if(data.suc){
                                        T.schemes = data.suc;
                                        let firstID = T.schemes[0].id;
                                        T.selectSchemeID = firstID;
                                        T.getFactorSet(firstID);
                                    }
                                }
                            });
                        },
                        deleteSchemeCancel:function(){
                            this.isDelete = "none";
                        },
                        /**
                         * @author:wangkun
                         * @date:2017-12-16
                         * @modifydate:
                         * @param:
                         * @return:
                         * @description:设置默认方案
                         */
                        setDefaultScheme:function(){
                            var activeMonth = this.getActiveMonth();
                            this.schemes.forEach(item=>{
                                let id = item.id;
                                if(id === this.selectSchemeID){
                                    item.isDefault = 1;
                                }
                                else{
                                    item.isDefault = 0;
                                }
                            });
                            this.schemeIsDefault = true;
                            let url = Url_Config.gridServiceUrl + "services/ForcastService/setDefaultScheme";
                            var param = JSON.stringify(this.schemes);
                            com.AJAX(url, param,true,function(){
                                processControl.hide("设置失败!");
                            },function(data){
                                if(data.suc==null){
                                    processControl.hide("设置失败!");
                                }
                                else{
                                    processControl.hide("设置成功!");
                                }
                            });
                            //console.log(activeMonth+"月的"+this.selectSchemeID+"设为默认");
                        }
                    }
                });
                mapTool.myVue.fillVal = function(obj){//重新注册填值事件
                    me.fillVal(obj);
                }
                mapTool.myVue.fillColor = function(obj){//重新注册填图事件
                    me.fillColor(obj);
                }
                mapTool.myVue.invert = function(obj){//重新注册反演事件
                    me.invert(obj);
                }
            }
            function initEvent(){
                com.getBounds().then(function(data){
                    if (data.suc != null) {
                        me.geo = JSON.parse(data.suc);
                        com.addCover(me.geo, true);
                    }
                    else {
                        console.log("获取区域数据失败!");
                    }
                });
            }
        },
        /**
         * @author:wangkun
         * @date:2017-11-12
         * @modifydate:
         * @param:
         * @return:
         * @description:计算相关系数
         */
        calCorrelation:function(flag){
            var me = this;
            var param = {
                month:me.dateSelect.monthSelected,
                flag:flag
            };
            param = JSON.stringify(param);
            var url = Url_Config.gridServiceUrl + "services/ForcastService/getFactor";
            com.AJAX(url, param,true, function(data){
                console.log(data);
                processControl.hide();
            }, function(data){
                processControl.show("因子计算完成!");
                if(data.suc!=null){
                    me.reflushFactorView(data.suc);
                }
                else{
                    console.log(data.err);
                }
                processControl.hide();
            });
            
        },
        /**
         * @author:wangkun
         * @date:2017-11-20
         * @modifydate:
         * @param:
         * @return:
         * @description:预报
         */
        calForcast:function(flag){
            var me = this;
            processControl.show("正在生成预报,预计耗时30秒!");
            var month = me.dateSelect.monthSelected;
            var selectedItems = me.vueFactor.factor.filter((item)=>{
                return item.state;
            });
            var url = Url_Config.gridServiceUrl + "services/ForcastService/calRegForecast";
            var param = {
                month:month,
                flag:flag,
                lsFactor:selectedItems
            };
            param = JSON.stringify(param);
            com.AJAX(url, param,true,function(){
                processControl.hide("失败!");
            },function(data){
                if(data.suc==null){
                    processControl.hide(data.err);
                }
                else{
                    var tempData = me.dealData(data.suc);
                    me.data = tempData;
                    mapTool.myVue.items[1].isActive = false;
                    document.getElementById("fillVal").click();
                    processControl.hide("完成");
                    vueTitle.datetime = month+"月";
                }
            });
        },
        /**
         * @author:wangkun
         * @date:2017-11-26
         * @modifydate:
         * @param:
         * @return:
         * @description:获取因子配置
         */
        getFactorSet:function(month,schemeID){
            var me = this;
            var param = {
                month:month,
                schemeID:schemeID
            };
            param = JSON.stringify(param);
            var url = Url_Config.gridServiceUrl + "services/ForcastService/getFactorSetByMonthAndSchemeID";
            com.AJAX(url, param,true,function(){
                console.log("失败!");
            },function(data){
                if(data.suc==null){
                    console.log(data.err);
                }
                else{
                    data.suc.forEach(item=>{
                        item.hightlight = false;
                    });
                    me.vueFactorSet.factors = data.suc;
                }
            });
        },
        /**
         * @author:wangkun
         * @date:2017-11-30
         * @modifydate:
         * @param:
         * @return:
         * @description:保存配置
         */
        saveFactorSet:function(data){
            var me = this;
            me.vueFactorSet.saveStatus = "";
            var url = Url_Config.gridServiceUrl + "services/ForcastService/saveFactorSet";
            var newData = $.extend(true,{},data);
            param = JSON.stringify(data);
            com.AJAX(url, param,true,function(){
                me.vueFactorSet.saveStatus = "保存失败!";
            },function(data){
                if(data.suc==null){
                    me.vueFactorSet.saveStatus = "保存失败!";
                }
                else{
                    me.vueFactorSet.saveStatus = "保存成功!";
                }
            });
        },
        /**
         * @author:wangkun
         * @date:2017-12-04
         * @modifydate:
         * @param:
         * @return:
         * @description:刷新因子显示
         */
        reflushFactorView:function(data){
            var me = this;
            var tempData = data;
            //排序
            tempData.sort(function(obj1,obj2){
                return obj2.val-obj1.val;
            });
            tempData.forEach(item=>{
                if(item.val>=0.5){
                    item.state = true;
                }
                else{
                    item.state = false;
                }
            });
            //取最大索引
            var mapMax = new Map();
            var index = 0;
            tempData.forEach(item=>{
                if(!item.state){
                    return;
                }
                let factorID = item.factorID;
                let val = item.val;
                let obj = mapMax.get(factorID);
                if(obj==undefined){
                    mapMax.set(factorID,index);
                }
                else{
                    if(obj>val){
                        mapMax.set(factorID,index);
                    }
                }
                index++;
            });
            index = 0;
            tempData.forEach(item=>{
                if(!item.state){
                    return;
                }
                let factorID = item.factorID;
                let tempIndex = mapMax.get(factorID);
                if(tempIndex === index){
                    item.state = true;
                }
                else{
                    item.state = false;
                }
                index++;
            });
            me.vueFactor.factor = tempData;
        },
        /**
         * @author:wangkun
         * @date:2017-12-03
         * @modifydate:
         * @param:
         * @return:
         * @description:导入配置因子
         */
        importSetFactor:function(schemeID){
            var me = this;
            var param = {
                month:me.dateSelect.monthSelected,
                schemeID:schemeID
            };
            param = JSON.stringify(param);
            var url = Url_Config.gridServiceUrl + "services/ForcastService/getFactorSetByMonth";
            com.AJAX(url, param,true,function(){
                console.log("导入配置失败!");
            },function(data){
                if(data.suc==null){
                    console.log("导入配置失败!");
                }
                else{
                    if(me.vueFactor.factor.length<1){
                        console.log("请先下载因子!");
                        return;
                    }
                    console.log(data.suc);
                    me.vueFactor.factor.forEach(item=>{
                        let factorID = item.factorID;
                        let month = item.month;
                        let val = me.getValFromObjByMonth(data.suc,month,factorID);
                        var b = val==0?false:true;
                        if(b){
                            console.log("T");
                        }
                        item.state = b;
                    });
                }
            });
        },
        /**
         * @author:wangkun
         * @date:2017-12-04
         * @modifydate:
         * @param:
         * @return:
         * @description:从对象中获取月数据
         */
        getValFromObjByMonth:function(objs,month,factorID){
            var findItem = objs.find(item=>{
                return item.indexID == factorID;
            });
            var val = 0;
            switch(month){
                case 1:
                    val = findItem.month1;
                    break;
                case 2:
                    val = findItem.month2;
                    break;
                case 3:
                    val = findItem.month3;
                    break;
                case 4:
                    val = findItem.month4;
                    break;
                case 5:
                    val = findItem.month5;
                    break;
                case 6:
                    val = findItem.month6;
                    break;
                case 7:
                    val = findItem.month7;
                    break;
                case 8:
                    val = findItem.month8;
                    break;
                case 9:
                    val = findItem.month9;
                    break;
                case 10:
                    val = findItem.month10;
                    break;
                case 11:
                    val = findItem.month11;
                    break;
                case 12:
                    val = findItem.month12;
                    break;
                default:
                    val = 0;
                    break;
            }
            return val;
        },
        /**
         * @author:wangkun
         * @date:2017-12-05
         * @modifydate:
         * @param:
         * @return:
         * @description:保存当前配置
         */
        saveCurFactorSet:function(schemeID,data){
            var me = this;
            var map = new Map();
            data.forEach(item=>{
                let factorID = item.factorID;
                let month = item.month;
                let state = item.state;
                let arr = map.get(factorID);
                if(arr==undefined||arr==null){
                    arr = new Array(12);
                    map.set(factorID,arr);
                }
                state = state?1:0;
                arr[month-1] = state;
            });
            //构建新的数组
            var result = [];
            let month = me.dateSelect.monthSelected;
            map.forEach((item,index)=>{
                var obj = {
                    indexID:index,
                    month:month,
                    month1:item[0],
                    month2:item[1],
                    month3:item[2],
                    month4:item[3],
                    month5:item[4],
                    month6:item[5],
                    month7:item[6],
                    month8:item[7],
                    month9:item[8],
                    month10:item[9],
                    month11:item[10],
                    month12:item[11],
                    schemeID:schemeID
                };
                result.push(obj);
            });
            var url = Url_Config.gridServiceUrl + "services/ForcastService/addFactorSet";
            param = JSON.stringify(result);
            com.AJAX(url, param,true,function(){
                processControl.hide("增加失败!");
            },function(data){
                if(data.suc==null){
                    processControl.hide("增加失败!");
                }
                else{
                    me.vueFactor.saveSetDivVis = "none";
                    processControl.hide("增加成功!");
                }
            });
        },
        /**
         * @author:wangkun
         * @date:2017-12-12
         * @modifydate:
         * @param:
         * @return:
         * @description:增加因子方案
         */
        addFactorScheme:function(name){
            var me = this;
            //保存方案
            var elementID = me.vueElement.selectID;
            let param = {
                name:name,
                elementID:elementID
            };
            param = JSON.stringify(param);
            let url = Url_Config.gridServiceUrl + "services/ForcastService/addFactorScheme";
            var pro = new Promise(function (resolve, reject) {
                com.AJAX(url, param,true,resolve, resolve);
            });
            return pro;
        },
        /**
         * @author:wangkun
         * @date:2017-12-12
         * @modifydate:
         * @param:
         * @return:
         * @description:根据月和要素获取方案
         */
        SchemeByMonthAndEle:function(pMonth){
            var me = this;
            var month = pMonth;
            if(pMonth==undefined){
                month = me.dateSelect.monthSelected;
            }
            var elementID = me.vueElement.selectID;
            var param = {
                month:month,
                elementID:elementID
            };
            param = JSON.stringify(param);
            let url = Url_Config.gridServiceUrl + "services/ForcastService/getSchemeByMonthAndEle";
            var pro = new Promise(function (resolve, reject) {
                com.AJAX(url, param,true, resolve, resolve);
            });
            return pro;
        },
        /**
         * @author:杠上花
         * @date:2017-12-22
         * @modifydate:
         * @param:
         * @return:
         * @description:填值
         */
        fillVal:function(obj){
            var me = this;
            var name = "距平填值";
            if(!obj.isActive){
                lmu.Remove(name);
                return;
            }
            //取消反演选中
            lmu.Remove("实际值");
            var invertObj = null;
            mapTool.myVue.items.forEach(item=>{
                if(item.id === "invert"){
                    invertObj = item;
                    return;
                }
            });
            invertObj.isActive = false;

            if(me.data==null){
                processControl.hide("无数据");
                return;
            }
            var layer = lmu.addLayer(name, "vector", null,"dot");
            layer.removeAllFeatures();
            var features = [];
            me.data.forEach(item=>{
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
                    pointRadius:2 
                }
                var pointFeature = new WeatherMap.Feature.Vector(geometry,null,style);
                features.push(pointFeature);
            });
            layer.addFeatures(features);
            var element = me.getSelectedElement();
            if(element.id === "temp"){
                vueTitle.name = "距平";
            }
            else{
                vueTitle.name = "距平百分率";
            }
            mapTool.myVue.items[0].isActive = false;
			document.getElementById("fillColor").click();
        },
        /**
         * @author:杠上花
         * @date:2017-12-28
         * @modifydate:
         * @param:
         * @return:
         * @description:填图
         */
        fillColor:function(obj,uData,uStyle){
            var me = this;
            var name = "填图";
            if(!obj.isActive){
                lmu.Remove(name);
                return;
            }
            var invertObj = null;
            mapTool.myVue.items.forEach(item=>{
                if(item.id === "invert"){
                    invertObj = item;
                    return;
                }
            });
            var tempData = null;
            var style = null;
            var element = me.getSelectedElement();
            if(invertObj.isActive){
                tempData = me.calData;
                if(element.id === "prec"){
                    style = month_prec;
                }
                else if(element.id === "temp"){
                    style = temp;
                }
            }
            else{
                tempData = me.data;
                if(element.id === "prec"){
                    style = month_jp_rain;
                }
                else if(element.id === "temp"){
                    style = temp_jp;
                }
            }

            if(tempData==null){
                processControl.hide("无数据");
                return;
            }
            var layer = lmu.addLayer(name, null, null,"grid");
            var dg = gridUtil.interpolate(tempData);
            layer.items = style;
            layer.setDatasetGrid(dg);
            layer.refresh();
            vlegend.setStyle(style);
        },
        /**
         * @author:杠上花
         * @date:2017-12-28
         * @modifydate:
         * @param:
         * @return:
         * @description:反演
         */
        invert:async function(obj){
            var me = this;
            var name = "实际值";
            if(!obj.isActive){
                lmu.Remove(name);
                return;
            }
            //取消填值选中
            lmu.Remove("距平填值");
            var fillValObj = null;
            mapTool.myVue.items.forEach(item=>{
                if(item.id === "fillVal"){
                    fillValObj = item;
                    return;
                }
            });
            fillValObj.isActive = false;

            if(me.data==null){
                processControl.hide("无数据");
                return;
            }
            //去掉填值
            mapTool.myVue.items.forEach(item=>{
                if(item.id === "fillVal"){
                    item.isActive = false;
                    return;
                }
            });
            var element = me.getSelectedElement();
            //请求距平
            var jpData = await com.getJPMonthData(element.flag);
            if(jpData.suc==undefined||jpData.suc==null){
                processControl.hide("距平数据请求失败!");
                return;
            }
            var mapData = com.convertStationDataToMap(jpData.suc);
            me.calData = [];
            var month = me.dateSelect.monthSelected;
            me.data.forEach(item=>{
                let sn = item.stationNum;
                let obj = mapData.get(sn);
                let val = item.value;
                let jpVal = com.getMonthStationData(obj,month);
                let newVal = (val*jpVal)/100+jpVal;
                var newItem = $.extend(true,{},item);
                newItem.value = newVal;
                me.calData.push(newItem);
            });
            var layer = lmu.addLayer(name, "vector", null,"dot");
            layer.removeAllFeatures();
            var features = [];
            me.calData.forEach(item=>{
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
                    pointRadius:2 
                }
                var pointFeature = new WeatherMap.Feature.Vector(geometry,null,style);
                features.push(pointFeature);
            });
            layer.addFeatures(features);
            layer.redraw();
            //获取当前项
            var fillColorObj = null;
            mapTool.myVue.items.forEach(item=>{
                if(item.id === "fillColor"){
                    fillColorObj = item;
                    return;
                }
            });
            if(!fillColorObj.isActive){
                return;
            }
            fillColorObj.isActive = true;
            me.fillColor(fillColorObj,me.calData);
            vueTitle.name = "预报值";
        },
        /**
         * @author:杠上花
         * @date:2017-12-28
         * @modifydate:
         * @param:
         * @return:
         * @description:获取选中要素
         */
        getSelectedElement:function(){
            var me = this;
            var obj = null;
            me.vueElement.elements.forEach(item=>{
                if(item.isActive){
                    obj = item;
                    return;
                }
            });
            return obj;
        },
        /**
         * @author:杠上花
         * @date:2017-12-28
         * @modifydate:
         * @param:
         * @return:
         * @description:处理数据
         */
        dealData:function(data){
            var result = [];
            data.forEach(item=>{
                if(item.value<-100){
                    return;
                }
                result.push(item);
            });
            return result;
        }
    }   
})