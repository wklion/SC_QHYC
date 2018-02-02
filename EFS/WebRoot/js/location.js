  //弹出窗口  
    function pop(){  
        //将窗口居中  
        makeCenter();  
        //初始化分类列表  
        initLayerClass();  
        //默认情况下, 给第一个分类添加choosen样式  
        $('[class-id="1"]').addClass('choosen');  
        //初始化ITEM列表  
        initItemList(1);
    }  
  
    //隐藏窗口  
    function hide() {  
        $('#choose-box-wrapper').css("display", "none");  
    }  
  
    //获取选择值  
    function doSelect() {  
        var list = $(".class-item-ck");  
        var text = "";  
        var value = "";  
        list.each(function () {  
            if ($(this).is(':checked')) {  
                text += $(this).attr("item-name") + ";";  
                value += $(this).attr("item-id");  
            };  
        });  
        $('#my-name').val('').val(text);  
        $('#my-value').val('').val(value);  
        //关闭弹窗  
        hide();  
    };  
  
    function initLayerClass()  
    {  
        //原先的分类列表清空  
        $('#choose-a-class').html('');  
        for (i = 0; i < itemList.length; i++) {  
            $('#choose-a-class').append('<a class="class-item" class-id="' + itemList[i].id + '">' + itemList[i].name + '</a>');  
        }  
        //添加分类列表项的click事件  
        $('.class-item').bind('click', function () {  
            var item = $(this);  
            var classid = item.attr('class-id');  
            var choosenItem = item.parent().find('.choosen');  
            if (choosenItem) {  
                $(choosenItem).removeClass('choosen');  
            }  
            item.addClass('choosen');  
            //更新列表  
            initItemList(classid);  
        }  
        );  
    }  
  
    function initItemList(classid)  
    {  
        //原先列表清空  
        $('#choose-a-item').html('');  
        var mitems = itemList[classid - 1].items;  
        for (i = 0; i < mitems.length; i++) {  
            var html = '<li class="itemli">';  
            html += '<a class="mdata-item" item-id="' + mitems[i].id + '">' + mitems[i].name + '</a>';  
            html += '<input type="checkbox" class="class-item-ck" item-id="' + mitems[i].id + '" item-name="' + mitems[i].name + '" />';  
            html += '</li>';  
            $('#choose-a-item').append(html);  
        }  
        //添加详细列表项的click事件  
        $('.mdata-item').bind('click', function(){  
                var item=$(this);  
                //更新选择文本框中的值  
                $('#my-name').val(item.text());  
                //关闭弹窗  
                hide();  
            }  
        );  
    }  
  
    function makeCenter()  
    {  
        $('#choose-box-wrapper').css("display","block");  
        $('#choose-box-wrapper').css("position","absolute");  
        $('#choose-box-wrapper').css("top", Math.max(0, (($(window).height() - $('#choose-box-wrapper').outerHeight()) / 2) + $(window).scrollTop()) + "px");  
        $('#choose-box-wrapper').css("left", Math.max(0, (($(window).width() - $('#choose-box-wrapper').outerWidth()) / 2) + $(window).scrollLeft()) + "px");  
    }  