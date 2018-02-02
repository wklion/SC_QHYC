package com.spd.qhyc.application;

import com.mg.objects.Workspace;


public class WorkspaceHelper {
	public static Workspace m_Workspace = null;
	/**
	 * @作者:杠上花
	 * @日期:2018年1月23日
	 * @修改日期:2018年1月23日
	 * @参数:
	 * @返回:
	 * @说明:获取工作空间
	 */
	public static Workspace getWorkspace() {
		if(m_Workspace == null) {
			m_Workspace = new Workspace();
		}
		return m_Workspace;
	}
}
