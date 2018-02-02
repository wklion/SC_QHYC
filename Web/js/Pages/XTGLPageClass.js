/**
 * @author wyp
 * 2015-12-23
 * @description 系统管理页面
 */

function XTGLPageClass(){
	  this.areaName = null;
	  this.forcusAreaName = null;
	  this.departCode = null;
	  this.mmg = null;
	  
	 // var departCode;
	
	 //渲染左侧菜单区域里的按钮
    this.renderMenu = function(){
    	
    	var htmlStr = "<div id='div_datetime' style='padding-left:10px'><div class='title1'>区域查询</div>"
    	  +"<div class='btn_line'><button id='qhqy' value='1' class='active' style='margin-top:10px;'>气候区划</button><button id='gzqy' class='active' value='2' style='margin-top:10px;'>关注区域</button></div></br>"
    	  +"<div id='div_datetime' ><div class='title1'>区域管理</div>"
    	  +"<div class='btn_line'><button style='margin-top:10px;' id='addArea'>添加区域</button><button style='margin-top:10px;' id='export'>导入区域</button>";
    	 
        $("#menu_bd").html(htmlStr);
         GDYB.GridProductClass.init();
         GDYB.GridProductClass.currentUserArea = $.cookie("departCode");
         GDYB.GridProductClass.layerFocusArea.visibility = true;
         GDYB.GridProductClass.showFocusArea();
        // 表格显示
        var gridString = "<div id='gridws' style='position:absolute;bottom:0px;left:396px;width:1350px;'><div style='z-index:999;' class='bottomPanelImg'><img src='imgs/top.png'></div><div id='gridDiv' flag='active'  >"
    	                    +"<table id='mmg' class='mmg'>"
                            +      "<tr>"
                            +          "<th rowspan='' colspan=''></th>"
                            +     "</tr>"
                            + "</table></div></div>";
							 
    	$("#workspace_div").append(gridString);

    	 var items;
    	 var cols = [   { title:'id', name:'id' ,width:100, sortable: true, hidden:true, align:'center'  },
                        { title:'区域名称', name:'name' ,width:100, sortable: true, align:'center'  },
                        { title:'经度', name:'centerX' ,width:100, sortable: true, align:'center' },
                        { title:'纬度', name:'centerY' ,width:100, sortable:true,align:'center'},
                        { title:'创建时间', name:'createDate' ,width:200, sortable:true,align:'center'},
                        { title:'创建人', name:'createUser' ,width:100, sortable:true,align:'center'},
                        { title:'部门', name:'depaertCode' ,width:100, sortable:true,align:'center'},
                        { title:'站点名称', name:'stationName' ,width:100, sortable:true,align:'center'},
                        { title:'站点编码', name:'stationCode' ,width:100, sortable:true,align:'center'},
                        { title:'站点横坐标', name:'stationX' ,width:100, sortable:true,align:'center'},
                        { title:'站点纵坐标', name:'stationY' ,width:100, sortable:true,align:'center'},
                        { title:'状态', name:'status' ,width:50, sortable:true,align:'center'},
                        { title:'操作', name:'' ,width:200, align:'center', lockWidth:true, lockDisplay: true, renderer: function(val){
                        return '<button class="btn btn-info">修改区域名称</button><button  class="btn btn-danger">删除</button>'
                        }}
                    
                ];
   
      
    // 为bottomPanel绑定事件	
      $(".bottomPanelImg").click(function(){
      	 var flag =  $(this).next().attr("flag");
      	 if(flag=="active"){
      	 	$(this).find("img").attr("src","imgs/bottom.png");
      	    $(this).next().hide(); 
      	    $("#div_legend").hide();
      	    $(this).next().attr("flag","hide");
      	 }else{
      	 	$(this).find("img").attr("src","imgs/top.png");
      	 	$(this).next().show();
      	 	$(this).next().attr("flag","active");
      	 }
      	

      	
      });
      
      //为查询气候区域绑定事件
      $("#qhqy").click(function(){
      	
      	 var value =  $(this).attr("value");
      	 $("#gzqy").removeClass("active");
      	 $(this).addClass("active");
      	 GDYB.GridProductClass.areaType = value;
      	 GDYB.GridProductClass.showFocusAreaByType();
      	
      });
      
      
      // 为查询关注区域绑定事件
      
      $("#gzqy").click(function(){
      	
         var value =  $(this).attr("value");
          $("#qhqy").removeClass("active");
          $(this).addClass("active");
      	GDYB.GridProductClass.areaType = value;
      	 GDYB.GridProductClass.showFocusAreaByType();
      });
       
      
      
              // 为添加区域绑定事件
        $("#addArea").click(function(){
		       var d = dialog({
					    title: '区域名称',
					    content: '区域名称：<input id="areainput" style="height:28px;width:220px;" autofocus></br>区域类型：<select id="areaSelect"><option>请选择</option><option value="1">气候区划</option><option value="2">关注区域</option></select>',
					    ok: function () {
					    	    var val = $("#areainput").val(); 
					    	    var selectVal = $("#areaSelect").val();
						        if(!val && typeof(val)!="undefined" ){
						        	this.title("名称不能为空，请重新输入!");
						            return false;	
						        }
						        else if(selectVal=="请选择"){
						        	this.title("区域类型不能为空，请选择区域类型");
						        	return false;
						        }else{
						        	GDYB.GridProductClass.forcusAreaName = val;
						        	GDYB.GridProductClass.areaType = selectVal;
						        	drowForcusArea();
					
						        }
						    },
					    cancelValue: '取消',
					    cancel: function () {}
					});
					d.showModal();
     

        	
        });
        
        
        //为导入区域绑定事件
        $("#export").click(function(){
        	       var url =  Url_Config.gridServiceUrl + "services/AreaService/exportAreas";
        	       var d = dialog({
					    title: '导入区域',
					    content: '<form id="areaExportForm" action='+url+' enctype="multipart/form-data" method="post"><div><input id="areaInputValue" style="height:30px;width:220px;float:left;" name="areaInputValue" type="text"><a href="javascript:;" style="height:30px;" class="a-upload"><input type="file" style="height:30px;" name="selectFile" id="selectFile" onChange="$(this).parent().prev().val(this.value)">浏览</a></div></form>',
					    ok: function () {
					    	  var areaValue = $("#areaInputValue").val();
					    	  var array = new Array();
					    	  array = areaValue.split(".");
					    	  if(array[1]!="xlsx"){
					    	  	this.title("请上excel文件!");
					    	  	return false;
					    	  }else{
					    	
						      $("#areaExportForm").ajaxSubmit(function(data){
						             alert(data);
						             // 刷新表格
						        	      var departCode = $.cookie("departCode"); 
    	                                  var param = '{"departCode":'+departCode+'}';
						        		  $.ajax({
									                type: 'post',
									                url: Url_Config.gridServiceUrl + "services/AreaService/getAreasForGrid",
									                data: {'para': param},
									                dataType:'text',
						        		            success:function(data){
						        		            	var dataObj = eval(data);
											             mmg.load(dataObj);
																	        		            	
						        		            },
						        		            error:function(){
						        		            	
						        		            }
						        		  
						        		  });
						            });
	
					    	  }
					    	  
						    },
					    cancelValue: '取消',
					    cancel: function () {}
					});
					d.showModal();
        	
        	
        	
        	
        });

        function drowForcusArea(){
          var map = GDYB.Page.curPage.map; 	
          map.addLayers([GDYB.GridProductClass.layerFocusArea]);
          GDYB.GridProductClass.drawFocusArea = new WeatherMap.Control.DrawFeature(GDYB.GridProductClass.layerFocusArea, WeatherMap.Handler.PolygonFree);
          map.addControl(GDYB.GridProductClass.drawFocusArea);
          GDYB.GridProductClass.layerFocusArea.visibility = true;
          GDYB.GridProductClass.showFocusArea();
          GDYB.GridProductClass.drawFocusArea.activate();
          stopDragMap();


            function stopDragMap()
            {
                var map = GDYB.Page.curPage.map;
                for(var i =0; i < map.events.listeners.mousemove.length; i++) {
                    var handler = map.events.listeners.mousemove[i];
                    if(handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag")
                    {
                        handler.obj.active = false;
                    }
                }
            }
        	
        
        GDYB.GridProductClass.drawFocusArea.events.on({"featureadded": drawCompletedFocusArea});
        	
        	        
    // 画完区域监听
        function drawCompletedFocusArea(e) {
        if (typeof(e.feature) != "undefined" && e.feature != null){
            var geo = e.feature.geometry;
            var name = GDYB.GridProductClass.forcusAreaName;
            var type = GDYB.GridProductClass.areaType;
            var centerX = geo.bounds.left + (geo.bounds.right - geo.bounds.left)/2.0;
            var centerY = geo.bounds.bottom + (geo.bounds.top - geo.bounds.bottom)/2.0;
            var coordinates = "";
            var createUser = GDYB.GridProductClass.currentUserName;
            var departCode = GDYB.GridProductClass.currentUserDepart.departCode;
            var status = 0;

            var lineString = geo.components[0];
            var pts = lineString.components;
            for(var i=0; i<pts.length; i++){
                var pt = pts[i];
                coordinates+=Math.floor(pt.x*10000)/10000 + "," + Math.floor(pt.y*10000)/10000+";";
            }
            coordinates = coordinates.substr(0, coordinates.length - 1);

            var url=Url_Config.gridServiceUrl+"services/AreaService/addArea";
            $.ajax({
                data: {"para": "{name:'" + name + "',centerX:"+ centerX + ",centerY:" + centerY + ",coordinates:'"+coordinates
                    +"',createUser:'" + createUser + "',departCode:'" + departCode + "',status:" + status + "',type:"+type+"}"},
                url: url,
                dataType: "json",
                success: function (data) {
                   if(data){
                   	alert("添加成功");
                   	        	// 刷新表格
						        	      var departCode = $.cookie("departCode"); 
    	                                  var param = '{"departCode":'+departCode+'}';
						        		  $.ajax({
									                type: 'post',
									                url: Url_Config.gridServiceUrl + "services/AreaService/getAreasForGrid",
									                data: {'para': param},
									                dataType:'text',
						        		            success:function(data){
						        		            	var dataObj = eval(data);
											             mmg.load(dataObj);
																	        		            	
						        		            },
						        		            error:function(){
						        		            	
						        		            }
						        		  
						        		  });
                   }else{
                   	 alert("添加失败");
                   }
                       
                      
                    
                      
                    GDYB.GridProductClass.drawFocusArea.deactivate();
                    startDragMap();
                },
                type: "POST"
            });
        }

        function startDragMap()
        {
            var map = GDYB.Page.curPage.map;
            for(var i =0; i < map.events.listeners.mousemove.length; i++) {
                var handler = map.events.listeners.mousemove[i];
                if(handler.obj.CLASS_NAME == "WeatherMap.Handler.Drag")
                {
                    handler.obj.active = true;
                }
            }
        }
    }
        	
        	
        }
        

    	
    	//初始化表格
    	function initGrid(){
    		
    		 	// 加载表格数据
    	 var departCode = $.cookie("departCode"); 
    	 var param = '{"departCode":'+departCode+'}';
    	 	  $.ajax({
                type: 'post',
                url: Url_Config.gridServiceUrl + "services/AreaService/getAreasForGrid",
                data: {'para': param},
                dataType:'text',
                error: function () {
                    alert('获取关注区域数据失败');
                },
                success: function (data) {
                   // mmgrid 
                   items = eval(data);
                    mmg = $('.mmg').mmGrid({
                   	height:200,
                   	cols: cols, 
                    remoteSort:true, 
                    items: items, 
                    sortName: 'SECUCODE', 
                    sortStatus: 'asc'
                    
                 });
                    mmg.on('cellSelected', function(e, item, rowIndex, colIndex){
//                  console.log('cellSelected!');
//                  console.log(this);
//                  console.log(e);
//                  console.log(item);
//                  console.log(rowIndex);
//                  console.log(colIndex);
                    if($(e.target).is('.btn-danger') && confirm('你确定要删除该行记录吗?')){
                        e.stopPropagation(); //阻止事件冒泡
                        var dataParam = '{"id":'+item.id+'}';
                        //删除区域
                         $.ajax({
			                type: 'post',
			                url: Url_Config.gridServiceUrl + "services/AreaService/deleteArea",
			                data: {'para': dataParam},
			                dataType:'text',
			                error: function () {
			                    alert('删除失败');
			                },
			                success: function (data) {
			                   if(data=true){
			                   	alert("删除成功");
			                   	mmg.removeRow(rowIndex);
			                   	GDYB.GridProductClass.showFocusArea();
			                   	
			                   }
			        
			                   
			                }
		              });
                    }else if($(e.target).is('.btn-info')){
                    	var areaName = item.name;
                    	var d = dialog({
                    		     title:'修改区域名称',
                    		     content:'区域名称：<input style="height:28px;" id="areaNameUpdate" >',
                    		      ok:function(){
                    		      	// 校验修改内容
                    		      	var areaNameUpdateValue = $("#areaNameUpdate").val();
                    		      	if(!areaNameUpdateValue && typeof(areaNameUpdateValue)!="undefined"){
                    		      		this.title("修改名称不能为空！");
                    		      		return false;
                    		      	}else{
                    		      		var id = item.id;
                    		      		var name = $("#areaNameUpdate").val();
                    		      		var param = '{"id":'+id+',"name":'+name+'}';
                    		      		 areaName = $("#areaNameUpdate").val();
                    		      		$.ajax({
							                type: 'post',
							                url: Url_Config.gridServiceUrl + "services/AreaService/updateAreaName",
							                data: {'para': param},
							                dataType:'text',
							                error: function () {
							                    alert('修改失败');
							                },
							                success: function (data) {
							                   if(data=true){
							                   	alert("修改成功");
							                   	item.name = areaName;
							                   	mmg.updateRow(item,rowIndex);
							                   }
							        
							                   
							                }
						              });
				                    		      		
                    		      		
                    		      	}
                    		      	
                    		      	
                    		      	
                    		      },
                    		      okValue:'确定',
                    		      cancelValue: '取消',
					              cancel: function () {}
                    		      
                    		
                    	});
                    	d.showModal();
                    	
                    }
                }).on('loadSuccess', function(e, data){
                    //这个事件需要在数据加载之前注册才能触发
                    console.log('loadSuccess!');
                    console.log(data);
                    console.log(mmg.rowsLength());
                }).load();

                   
                }
            });

    		
    	}
    	
    	initGrid();
    	
   
    }// rendMenu end
    
   

	
    
}


XTGLPageClass.prototype = new PageBase();
