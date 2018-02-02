package com.spd.grid.service.impl;

import com.sun.jna.Library;

public interface FactorDllLibary extends Library{
//	void corr( );
	void reg(int y,int m,int c);
}
