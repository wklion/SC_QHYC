package com.spd.efs.controller;

import com.jfinal.core.ActionKey;

public class IndexController extends BaseController{
	
	
	public void index(){
		
		render("nativ.htm");
		
	}
	
	
	/**
	 * 基本元素页面入口
	 */
	
    public void element(){
    	
    	render("element.htm");
    	
    }
    
    /**
     * 产品展示页面入口
     */
    public void showProduct(){
    	
    	render("product.htm");
    	
    }
    
    /**
     * 环流分析页面入口
     */
    
    public void circulationAnalysis(){
    	
    	render("circulationAnalysis.htm");
    }
    
    
    /**
     * 预报制作页面入口
     */
    
    public void productionForecast(){
    	
    	
    	render("productionForecast.htm");
    }
	
	 
	

}
