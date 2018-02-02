package com.spd.grid.service.impl;

import com.spd.grid.config.ConfigHelper;
import com.spd.grid.model.Config;
import com.sun.jna.Library;

public interface FactorDllLibary extends Library{
	void corr(int m);//计算相关系数m月份
	void reg(int y,int m,int ele);//回归(年、月、要素编码0降水,1气温)
}
