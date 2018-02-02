/*
* 可拖拽面板基类
* by zouwei, 2015-05-10
* */
function DragPanelBase(div){}
DragPanelBase.prototype.div = null;
DragPanelBase.prototype.panel = null;
DragPanelBase.prototype.dragArea = null;
DragPanelBase.prototype.createPanelDom = function(){
    this.panel = $("<div class=\"dragPanel\">"
        +"<div class=\"title\"></div>"
        +"<div class=\"body\"></div>"
        +"</div>")
        .appendTo(this.div);
};
DragPanelBase.prototype.bind = function(){
    var t = this;
    this.dragArea.droppable({
        drag: function(event){
            t.drag(event);
        }
    });
    this.panel.find(".closeBtn").click(function(){
        if(this.innerHTML == "×")
            t.hide();
        else if(this.innerHTML == "△"){
            t.panel.find(".body").css("display","none");
            this.innerHTML = "▽";
        }
        else if(this.innerHTML == "▽"){
            t.panel.find(".body").css("display","block");
            this.innerHTML = "△";
        }
    });
};
DragPanelBase.prototype.init = function(){
    this.createPanelDom();
    this.dragArea = this.panel.find(".title");
    this.bind();
};
DragPanelBase.prototype.drag = function(event){
    var position = event.position;
    var position1 = this.div.position();
    var left = position.left-position1.left;
    var top = position.top-position1.top-90;
    var divWidth = this.div.width();
    var divHeight = this.div.height();
    var panelWidth = this.panel.width();
    var panelHeight = this.panel.height();
    if(left<0){
        left=0;
    }
    else if(left>divWidth-panelWidth-5){
        left = divWidth-panelWidth-5;
    }
    if(top<0){
        top = 0;
    }
    else if(top>divHeight-panelHeight-5){
        top = divHeight-panelHeight-5;
    }
    this.panel.css({
        "left":left+"px",
        "top":top+"px",
        "right":"auto",
        "bottom":"auto"
    });
};
DragPanelBase.prototype.hide = function(){
    this.panel.css({"display":"none"});
};
DragPanelBase.prototype.show = function(){
    this.panel.css({"display":"block"});
}