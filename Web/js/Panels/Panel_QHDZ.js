/*
 * 区划订正
 * by zouwei 2015-05-10
 * */
function Panel_QHDZ(div){
    this.div = div;
    this.createPanelDom = function(){
        this.panel = $("<div id=\"Panel_QHDZ\" class=\"dragPanel\">"
            +"<div class=\"title\"><span>区划订正</span><a class=\"closeBtn\">×</a></div>"
            +"<div class=\"body\">"
            +"<div class=\"row1\"><span>区划类型：</span><select id='selectClimaticRegionType_QH' style='width:100px;height:25px;line-height:25px;margin-top:5px;'><option>无</option></select></div>"
            +"<div class=\"row1\"><span>区划选择：</span><select id='selectClimaticRegionItem_QH' style='width:100px;height:25px;line-height:25px;margin-top:5px;'><option>无</option></select></div>"
            +"<div class=\'row1\'><span>统一赋值：</span><input id='inputValue' style='width: 100px;padding-left: 5px;height:25px;line-height:25px;margin-top:5px;' type='number' value='32'/><span id='unit1'>℃</span></div>"
            +"<div class=\"row1\"><span style='margin-left:5px;outline: none;'>27</span><input type='range' id='rangeValue' name='value1' min='27' max='37' style='width: 120px'/><span>37</span></div>"
            +"<div class=\'row1\'><span>统一加减：</span><input id='inputAdd' style='width: 100px;padding-left: 5px;height:25px;line-height:25px;margin-top:5px;' type='number' value='-1'/><span id='unit2'>℃</span></div>"
            +"<div class=\"row1\"><span style='margin-left:5px;outline: none;'>-10</span><input type='range' id='rangeAdd' name='value2' min='-10' max='10' style='width: 120px'/><span>10</span></div>"
            +"<div class=\'row1\'><span>统一增量：</span><input id='inputIncrement' style='width: 100px;padding-left: 5px;height:25px;line-height:25px;margin-top:5px;' type='number' value='-5'/><span>%</span></div>"
            +"<div class=\"row1\"><span style='margin-left:5px;outline: none;'>-10</span><input type='range' id='rangeIncrement' name='value3' min='-10' max='10' style='width: 120px'/><span>10</span></div>"
            +"<div id='divTool' style='display:inline;margin-right: 20px;margin-top: 10px;margin-bottom: 5px;float:right;'>"
            +"<button id='btnApply_QH' style='width: 80px'>应用</button>"
            +"</div>"
            +"</div>"
            +"</div>")
            .appendTo(this.div);
    }
    this.init();
    this.panel.css({
        "top":"5px",
        "right":"240px"
    });

    var method = 0;
    var value = $("#inputValue").val();
    $("#rangeValue").change(function(){
        method=0;
        value = $(this).val();
        $("#inputValue").val(value);
    });
    $("#rangeAdd").change(function(){
        method=1;
        value = $(this).val();
        $("#inputAdd").val(value);
    });
    $("#rangeIncrement").change(function(){
        method=2;
        value = $(this).val()/100.0;
        $("#inputIncrement").val($(this).val());
    });

    $("#inputValue").change(function(){
        method=0;
        value = $(this).val();
    });
    $("#inputAdd").change(function(){
        method=1;
        value = $(this).val();
    });
    $("#inputIncrement").change(function(){
        method=2;
        value = $(this).val()/100.0;
    });

    if(GDYB.GridProductClass.layerClimaticRegion == null)
        GDYB.GridProductClass.createClimaticRegionLayer();
    initType();
    function initType(){
        $("#selectClimaticRegionType_QH").empty();
        var url=Url_Config.gridServiceUrl+"services/ClimaticRegionService/getClimaticRegionTypes";
        $.ajax({
            data: {"para": "{}"},
            url: url,
            dataType: "json",
            success: function (data) {
                if(data.length > 0)
                {
                    for(var i=0; i<data.length; i++)
                    {
                        $("#selectClimaticRegionType_QH").append("<option value='" + data[i].datasetName + "'>" + data[i].typeName + "</option>");
                    }
                    fillClimaticRegionItem(data[0].datasetName);
                }
            },
            type: "POST"
        });
    }

    $("#selectClimaticRegionType_QH").change(function(){
        fillClimaticRegionItem($(this).val());
    });

    function fillClimaticRegionItem(datasetname){
        $("#selectClimaticRegionItem_QH").empty();
        var url=Url_Config.gridServiceUrl+"services/ClimaticRegionService/getClimaticRegionItemNames";
        $.ajax({
            data: {"para": "{datasetname:'" + datasetname + "'}"},
            url: url,
            dataType: "json",
            success: function (data) {
                for(var i=0; i<data.length; i++)
                {
                    $("#selectClimaticRegionItem_QH").append("<option value='" + data[i].regionId + "'>" + data[i].regionName + "</option>");
                }
                if(data.length > 0)
                    showClimaticRegionItem(datasetname, data[0].regionId);
            },
            type: "POST"
        });
    }

    //显示区域
    $("#selectClimaticRegionItem_QH").change(function(){
        showClimaticRegionItem($("#selectClimaticRegionType_QH").val(), $(this).val());
    });

    //显示气候区划
    function showClimaticRegionItem(datasetName, regionId)
    {
        var url=Url_Config.gridServiceUrl+"services/ClimaticRegionService/getClimaticRegionItem";
        $.ajax({
            data: {"para": "{datasetName:'" + datasetName+ "',regionId:" + regionId + "}"},
            url: url,
            dataType: "json",
            success: function (data) {
                var feature = GDYB.FeatureUtilityClass.getFeatureFromJson(data);
                var fAttributes = feature.attributes;

                if(GDYB.GridProductClass.layerMarkers != null)
                    GDYB.GridProductClass.layerMarkers.clearMarkers();

                //地图展示
                if(GDYB.GridProductClass.layerClimaticRegion != null)
                {
                    GDYB.GridProductClass.layerClimaticRegion.removeAllFeatures();
                    fAttributes["FEATUREID"] = regionId;
                    feature.style = {
                        strokeColor: "#a548ca",
                        strokeWidth: 2,
                        fillColor: "#FF0000",
                        fillOpacity: "0"
                    };
                    var features = [];
                    features.push(feature);
                    GDYB.GridProductClass.layerClimaticRegion.addFeatures(features);
                }

            },
            type: "POST"
        });
    }

    //应用
    $("#btnApply_QH").click(function(){
        GDYB.GridProductClass.updateGridByClimaticRegion(Number(value), method);
    });
}
Panel_QHDZ.prototype = new DragPanelBase();