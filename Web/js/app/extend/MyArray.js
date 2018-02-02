class ArrayExtend extends Array{
    constructor(...args) {
        super(...args);
    }
    /**
     * @author:wangkun
     * @date:2017-11-10
     * @modifydate:2017-11-10
     * @param:
     * @return:
     * @description:包含
     */
    contain(obj){
        var size = this.length;
        var result = false;
        for(var i=0;i<size;i++){
            if(this[i] === obj){
                result = true;
                break;
            }
        }
        return result;
    }
}