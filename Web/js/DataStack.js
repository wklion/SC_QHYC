/**
 * 数据堆栈类，用于数据编辑（撤销和重做）
 * Created by zouwei on 2015/11/12.
 */

function DataStack(){
    this.stack = [];		    //堆栈，数据结构采用数组
    this.maxStackCount = 10;    //最大堆栈数
    this.cursor = -1;		    //游标，指向当前编辑对象

    /*
    * 描述：压栈
    * 参数：
    *   obj：任意对象
    * 返回：无
    * */
    this.push = function(obj){
        if(this.cursor == this.stack.length-1) //如果游标在栈顶，移除第一个对象（出栈）
        {
            if(this.stack.length >= this.maxStackCount)
                this.stack.shift();
        }
        else  //如果游标不在栈顶，而在中间，移除其上的栈，使游标置顶
        {
            for(var i=this.stack.length-1; i>this.cursor; i--){
                this.stack.pop();
            }
        }

        var newObj = this.clone(obj);
        this.stack.push(newObj);
        this.cursor = this.stack.length - 1;
    };

    /*
     * 描述：撤销
     * 参数：
     * 返回：对象
     * */
    this.undo = function(){
        if(this.cursor < 0)
            return null;
        else
        {
            if(this.cursor > 0)
                this.cursor--;
            return this.clone(this.stack[this.cursor]);
        }
    };

    /*
     * 描述：重做
     * 参数：
     * 返回：对象
     * */
    this.redo  = function(){
        if(this.cursor < 0 || this.cursor >= this.stack.length - 1)
            return null;
        else
        {
            this.cursor++;
            return this.clone(this.stack[this.cursor]);
        }
    };

    /*
    * 取出指定索引的（拷贝）对象
    * */
    this.get = function(i){
        if(i < this.stack.length)
            return this.clone(this.stack[i]);
        else
            return null;
    };

    /*
     * 描述：对象克隆，解决对象属性为数组的问题
     * 参数：
     *  obj：对象
     * 返回：新对象
     * */
     this.clone = function(obj){
        var oClass=isClass(obj);
        var result=oClass=="Array"?[]:{};
        for(var key in obj) {
            var copy = obj[key];
            if (isClass(copy) == "Object") {
                var newCopy = arguments.callee(copy); //递归调用
                if (oClass == "Array")
                    result.push(newCopy);
                else
                    result[key] = newCopy;
            } else if (isClass(copy) == "Array") {
                var newCopy = arguments.callee(copy);
                if (oClass == "Array")
                    result.push(newCopy);
                else
                    result[key] = newCopy;
            } else {
                result[key] = obj[key];
            }
        }
         return result;

         function isClass(o){
             if(o===null) return "Null";
             if(o===undefined) return "Undefined";
             return Object.prototype.toString.call(o).slice(8,-1);
         }
    }
}