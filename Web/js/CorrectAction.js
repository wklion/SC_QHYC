/**
 * 订正动作（枚举）
 */
var CorrectAction = function(){
    this.none = -1,         //无任何自定义动作
    this.pickLuoqu = 0,     //拾取落区
    this.moveLuoqu = 1,     //移动落区
    this.modifyLuoqu = 2,   //修改落区
    this.editCell = 3       //单元格编辑
}