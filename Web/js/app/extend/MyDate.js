class MyDate extends Date {
    constructor(...args) {
        super(...args);
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:将指定的毫秒数加到此实例的值上
     */
    addMilliseconds(value) {
        var millisecond = this.getMilliseconds();
        this.setMilliseconds(millisecond + value);
        return this;
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:将指定的秒数加到此实例的值上
     */
    addSeconds(value) {
        var second = this.getSeconds();
        this.setSeconds(second + value);
        return this;
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:将指定的分钟数加到此实例的值上
     */
    addMinutes(value) {
        var minute = this.getMinutes();
        this.setMinutes(minute + value);
        return this;
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:将指定的小时数加到此实例的值上
     */
    addHours(value) {
        var hour = this.getHours();
        this.setHours(hour + value);
        return this;
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:将指定的天数加到此实例的值上
     */
    addDays(value) {
        var date = this.getDate();
        this.setDate(date + value);
        return this;
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:将指定的星期数加到此实例的值上
     */
    addWeeks(value) {
        return this.addDays(value * 7);
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:将指定的月份数加到此实例的值上
     */
    addMonths(value) {
        var month = this.getMonth();
        this.setMonth(month + value);
        return this;
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:将指定的年份数加到此实例的值上
     */
    addYears(value) {
        var year = this.getFullYear();
        this.setFullYear(year + value);
        return this;
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:转换
     */
    convert(date){
        var lon = date.getTime();
        this.setTime(lon);
        return this;
    }
    /**
     * @author:wangkun
     * @date:2017-03-28
     * @param:
     * @return:
     * @description:格式化
     */
    format(fmt) {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }
}
