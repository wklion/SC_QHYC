package com.spd.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpServletRequestWrapper2 extends HttpServletRequestWrapper {

	Map<String, String[]> params = null;  
	
	public HttpServletRequestWrapper2(HttpServletRequest request, Map inParam) {
		super(request);
		params = new HashMap(inParam);  
	}

	public void setParameter(String key, String value) {
		params.put(key, new String[] { value });
		System.out.println(params);
	}

	public void setParameter(String key, String[] values) {
		params.put(key, values);
	}

	@Override
	public String getParameter(String name) {
		Object v = params.get(name);
		if (v == null) {
			return null;
		} else if (v instanceof String[]) {
			String[] strArr = (String[]) v;
			if (strArr.length > 0) {
				return strArr[0];
			} else {
				return null;
			}
		} else {
			return v.toString();
		}
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		return params;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		Vector l = new Vector(params.keySet());
		return l.elements();
	}

	@Override
	public String[] getParameterValues(String name) {
		return params.get(name);
	}

}
