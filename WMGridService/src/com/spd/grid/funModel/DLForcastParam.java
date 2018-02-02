package com.spd.grid.funModel;

public class DLForcastParam {
    private String makeDate;//制作日期
    private String[] forcastDate;//预报日期
    private String elementID;//要素
    private String methodName;
    public String getMakeDate() {
        return makeDate;
    }
    public void setMakeDate(String makeDate) {
        this.makeDate = makeDate;
    }
    
    public String[] getForcastDate() {
        return forcastDate;
    }
    public void setForcastDate(String[] forcastDate) {
        this.forcastDate = forcastDate;
    }
    public String getElementID() {
        return elementID;
    }
    public void setElementID(String elementID) {
        this.elementID = elementID;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    
}
