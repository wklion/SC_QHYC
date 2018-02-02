package com.spd.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.context.ContextLoader;

import com.google.gson.Gson;
import com.spd.service.ICommon;

/**
 * 权限管理的时候，重置参数
 * @author Administrator
 *
 */
public class ServicesFilter implements Filter  {

	public Set<String> COMMONFILTER = new HashSet<String>();
	//参数中有站点的过滤
	public Set<String> STATIONFILTER = new HashSet<String>();
	//参数中有站点数组的过滤
	public Set<String> STATIONSFILTER = new HashSet<String>();
	//参数中没有站点
	public Set<String> NOSTATIONSFILTER = new HashSet<String>();
	
	public void initSet() {
		if(COMMONFILTER.size() == 0) {
			COMMONFILTER.add("/CommonStatisticsService");
			COMMONFILTER.add("/WinAvgCloCovHourService");
			COMMONFILTER.add("/RankServices/rank");
			COMMONFILTER.add("/ExtStatisticsService/ext");
			COMMONFILTER.add("/DaysStatisticsService/ext");
			COMMONFILTER.add("/PersistStatisticsService/rain");
			COMMONFILTER.add("/PersistStatisticsService/tmp");
			COMMONFILTER.add("/FirstDayService/rainTmpFirst");
			COMMONFILTER.add("/SeasonService/getSeasonByYear");
			COMMONFILTER.add("/HighTmpService/highTmpByTimes");
			COMMONFILTER.add("/AccumulatedTempService/accumulatedTempByTimes");
			COMMONFILTER.add("/DisasterService/rainstormByRange");
			COMMONFILTER.add("/DisasterService/lowTmpByRange");
			COMMONFILTER.add("/DisasterService/strongCoolingByRange");
			COMMONFILTER.add("/ClimDataQuery/queryClimByTimesRangeAndElement");
			COMMONFILTER.add("/ClimDataQuery/queryClimByTime");
			COMMONFILTER.add("/TmpGapService/getTmpByTimes");
			COMMONFILTER.add("/TmpGapService/getTmpByYear");
			
		}
		if(STATIONFILTER.size() == 0) {
			STATIONFILTER.add("/SameCalendarService/same");
			STATIONFILTER.add("/SequenceChangService/sequenceChangByTimes");
			STATIONFILTER.add("/TmpGapService/getTmpGapByYears");
//			STATIONFILTER.add("/DisasterService/highTmpByRange");
		}
		if(STATIONSFILTER.size() == 0) {
			STATIONSFILTER.add("/DisasterService/continuousRainsByRange");
			STATIONSFILTER.add("/DisasterService/continuousRainsYearsSequnence");
			STATIONSFILTER.add("/DisasterService/highTmpByRange");
			STATIONSFILTER.add("/DisasterService/highTmpByYears");
			STATIONSFILTER.add("/DisasterService/rainstormByYears");
			STATIONSFILTER.add("/DisasterService/maxWindByYear");
			STATIONSFILTER.add("/DisasterService/thundByYears");
			STATIONSFILTER.add("/DisasterService/snowByYears");
			STATIONSFILTER.add("/DisasterService/frostByYears");
			STATIONSFILTER.add("/DisasterService/fogByYears");
			STATIONSFILTER.add("/DisasterService/wepByRange");
		}
		if(NOSTATIONSFILTER.size() == 0) {
			NOSTATIONSFILTER.add("/DisasterService/mciByTime");
			NOSTATIONSFILTER.add("/DisasterService/agmesoilStatisticsByTime");
			NOSTATIONSFILTER.add("/DisasterService/mciStatisticsByTime");
			NOSTATIONSFILTER.add("/DisasterService/maxWindByRange");
			NOSTATIONSFILTER.add("/DisasterService/thundByRange");
			NOSTATIONSFILTER.add("/DisasterService/hailByRange");
			NOSTATIONSFILTER.add("/DisasterService/snowByRange");
			NOSTATIONSFILTER.add("/DisasterService/frostByRange");
			NOSTATIONSFILTER.add("/DisasterService/fogByRange");
			NOSTATIONSFILTER.add("/DisasterService/lowTmpByYear");
			NOSTATIONSFILTER.add("/DisasterService/strongCoolingByYears");
			NOSTATIONSFILTER.add("/DataCompleteService/getDataComplete");
		}
	}
	
	public boolean isContainFilter(String url) {
		Iterator<String> it = COMMONFILTER.iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(url.startsWith(key)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isContainStationFilter(String url) {
		Iterator<String> it = STATIONFILTER.iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(url.startsWith(key)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isContainStationsFilter(String url) {
		Iterator<String> it = STATIONSFILTER.iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(url.startsWith(key)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isContainNoStationsFilter(String url) {
		Iterator<String> it = NOSTATIONSFILTER.iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(url.startsWith(key)) {
				return true;
			}
		}
		return false;
	}
	
	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain)  {
		System.out.println("serviceFilter");
//		System.out.println("doFilter");
		initSet();
		Map map = request.getParameterMap();
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");  
//		//判断是否已经过期
//		HttpSession httpSession = httpServletRequest.getSession(false);
//		if(httpSession == null) {
//			try {
//				((HttpServletResponse)response).sendRedirect("/CIMAS/login/index.html");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return;
//		}
		String sessionName = "";
		String sessionId = httpServletRequest.getSession().getId();
		ServletContext servletContext  = httpServletRequest.getSession().getServletContext().getContext("/");
		 Cookie[] cookies = httpServletRequest.getCookies();
	        if(cookies != null) {
				for(int i = 0; i < cookies.length; i++) {
					String name = cookies[i].getName();
					String value = cookies[i].getValue();
					if("session".equals(name)) {
						sessionName = cookies[i].getValue();
						break;
					}
				}
	        }
	        
//		Object o = servletContext.getAttribute("session");
	    HashMap sessionMap = (HashMap) servletContext.getAttribute("session_map");
		Object o = sessionMap.get(sessionName);
		if(o == null) {
			timeout(httpServletRequest, httpServletResponse, servletContext);
			return;
		}
		HttpSession session = (HttpSession) o;
		if(session == null) {
			timeout(httpServletRequest, httpServletResponse, servletContext);
			return;
		}
		Object userName = null;
		try {
			userName = session.getAttribute("user");
		} catch(Exception e) {
			timeout(httpServletRequest, httpServletResponse, servletContext);
			return;
		}
		List<String> authorityCodes = queryAuthorityByUserName((String) session.getAttribute("user"));
		for(String authorityCode : authorityCodes) {
			if("BROWSEALL".equals(authorityCode)) {
				try {
//					System.out.println("browseall");
					chain.doFilter(request, response);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
		String pathInfo = httpServletRequest.getPathInfo();
		if(pathInfo == null) {
			try {
				chain.doFilter(request, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else  if(pathInfo.startsWith("/DisasterService/hourRain")) {
//			System.out.println("hourrain");
			HourRainFilter hourRainFilter = new HourRainFilter();
			String para = httpServletRequest.getParameter("para");
//			List<String> authorityCodes = (List<String>) session.getAttribute("AuthorityCode");
			
			String areaCode = (String) session.getAttribute("areaCode");
			String methodName = pathInfo.split("/")[2];
			String result = hourRainFilter.createQueryParam(para, authorityCodes, areaCode, methodName);
			if(result == null) {
//				Response response2  = Response.ok("no login").status(401).type(MediaType.APPLICATION_JSON).build();
//	            throw new WebApplicationException(response2);
//				timeout(httpServletResponse, servletContext);
				return;
			}
			HttpServletRequestWrapper2 httpServletRequestWrapper2 = new HttpServletRequestWrapper2(httpServletRequest, map);
			httpServletRequestWrapper2.setParameter("para", result);
			try {
				chain.doFilter(httpServletRequestWrapper2, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(isContainFilter(pathInfo)) {
//			System.out.println("isContainFilter");
			CommonFilter commonFilter = new CommonFilter();
			String para = httpServletRequest.getParameter("para");
//			List<String> authorityCodes = (List<String>) session.getAttribute("AuthorityCode");
//			List<String> authorityCodes = queryAuthorityByUserName((String) session.getAttribute("user"));
			String areaCode = (String) session.getAttribute("areaCode");
			String result = commonFilter.createQueryParam(para, authorityCodes, areaCode);
			HttpServletRequestWrapper2 httpServletRequestWrapper2 = new HttpServletRequestWrapper2(httpServletRequest, map);
			httpServletRequestWrapper2.setParameter("para", result);
			try {
				chain.doFilter(httpServletRequestWrapper2, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(isContainStationFilter(pathInfo)) {
//			System.out.println("isContainStationFilter");
			//在请求的时候过滤
			String para = httpServletRequest.getParameter("para");
			try {
				JSONObject jsonObject = new JSONObject(para);
				String station_Id_C = jsonObject.getString("station_Id_C");
				String areaCode = (String) session.getAttribute("areaCode");
				ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
				HashMap paramMap = new HashMap();
				paramMap.put("areaCode", areaCode);
				List<LinkedHashMap> station_idList = iCommon.queryStation_Id_CByAreaCode(paramMap);
				boolean flag = false;
				for(int i = 0; i < station_idList.size(); i++) {
					LinkedHashMap itemMap = station_idList.get(i);
					String itemStation_Id_C = (String) itemMap.get("Station_Id_C");
					if(itemStation_Id_C.equals(station_Id_C)) {
						flag  = true;
					}
				}
				if(flag) {
					chain.doFilter(request, response);
				} else {
					//
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(isContainStationsFilter(pathInfo)) {
//			System.out.println("isContainStationsFilter");
			String para = httpServletRequest.getParameter("para");
			try {
				JSONObject jsonObject = new JSONObject(para);
				String stationIdStr = jsonObject.getString("station_Id_Cs");
				String[] stationIds = stationIdStr.split(",");
				String areaCode = (String) session.getAttribute("areaCode");
				ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
				HashMap paramMap = new HashMap();
				paramMap.put("areaCode", areaCode);
				List<LinkedHashMap> station_idList = iCommon.queryStation_Id_CByAreaCode(paramMap);
				String resultStationStr = "";
				for(int i = 0; i < station_idList.size(); i++) {
					LinkedHashMap itemMap = station_idList.get(i);
					String itemStation_Id_C = (String) itemMap.get("Station_Id_C");
					for(int j = 0; j < stationIds.length; j++) {
						String itemStation = stationIds[j];
						if(itemStation_Id_C.equals(itemStation)) {
							resultStationStr += itemStation;
							resultStationStr += ",";
						}
					}
				}
				if(!resultStationStr.equals("")) {
					resultStationStr = resultStationStr.substring(0, resultStationStr.length() - 1);
				}
				Gson gson = new Gson();
				com.google.gson.internal.LinkedTreeMap treeMap = gson.fromJson(para, com.google.gson.internal.LinkedTreeMap.class);
				treeMap.put("station_Id_Cs", resultStationStr);
				String resultPara = gson.toJson(treeMap);
				
				HttpServletRequestWrapper2 httpServletRequestWrapper2 = new HttpServletRequestWrapper2(httpServletRequest, map);
				httpServletRequestWrapper2.setParameter("para", resultPara);
				try {
					chain.doFilter(httpServletRequestWrapper2, response);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if(isContainNoStationsFilter(pathInfo)) {
//			System.out.println("isContainNoStationsFilter");
			String para = httpServletRequest.getParameter("para");
			String areaCode = (String) session.getAttribute("areaCode");
			ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
			HashMap paramMap = new HashMap();
			paramMap.put("areaCode", areaCode);
			List<LinkedHashMap> station_idList = iCommon.queryStation_Id_CByAreaCode(paramMap);
			String resultStationStr = "";
			for(int i = 0; i < station_idList.size(); i++) {
				LinkedHashMap itemMap = station_idList.get(i);
				String itemStation_Id_C = (String) itemMap.get("Station_Id_C");
				resultStationStr += itemStation_Id_C;
				if(i != station_idList.size() - 1) {
					resultStationStr += ",";
				}
			}
			Gson gson = new Gson();
			com.google.gson.internal.LinkedTreeMap treeMap = gson.fromJson(para, com.google.gson.internal.LinkedTreeMap.class);
			if(treeMap == null) {
				treeMap = new com.google.gson.internal.LinkedTreeMap();
			}
			treeMap.put("station_Id_Cs", resultStationStr);
			String resultPara = gson.toJson(treeMap);
			HttpServletRequestWrapper2 httpServletRequestWrapper2 = new HttpServletRequestWrapper2(httpServletRequest, map);
			httpServletRequestWrapper2.setParameter("para", resultPara);
			try {
				chain.doFilter(httpServletRequestWrapper2, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
//			System.out.println("else");
			try {
				chain.doFilter(request, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void timeout(HttpServletRequest request, HttpServletResponse httpServletResponse, ServletContext servletContext){
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		String sessionId = httpServletRequest.getSession().getId();
		servletContext.removeAttribute(sessionId);
//		servletContext.removeAttribute("session");
		httpServletResponse.setStatus(408);
		try {
			httpServletResponse.sendRedirect("/CIMAS/login/index.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
//		try {
//			httpServletResponse.getWriter().print("超时或未登陆，需要重新连接");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private List<String> queryAuthorityByUserName(String userName) {
		ICommon iCommon = (ICommon)ContextLoader.getCurrentWebApplicationContext().getBean("CommonImpl");
		HashMap paramMap = new HashMap();
		paramMap.put("UserName", userName);
		List<LinkedHashMap> resultList = iCommon.queryAuthorityByUserName(paramMap);
		List<String> result= new ArrayList<String>();
		for(int i = 0; i < resultList.size(); i++) {
			LinkedHashMap itemMap = resultList.get(i);
			String authorityCode = (String) itemMap.get("AuthorityCode");
			result.add(authorityCode);
		}
		return result;
	}
	
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
