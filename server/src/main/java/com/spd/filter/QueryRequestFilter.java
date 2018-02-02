package com.spd.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;


public class QueryRequestFilter { 
//implements ContainerRequestFilter {
//	 
//	@Context   
//    private HttpServletRequest servletRequest;  
//    
//	@Context  
//    private HttpServletResponse servletResponse;
//
//	public ContainerRequest filter(ContainerRequest request) {
//		System.out.println("QueryRequestFilter");
//		String path = request.getPath();
//		Form form = request.getFormParameters();
//		List<String> list = form.get("logstatus");
//		ServletContext servletContext  = servletRequest.getSession().getServletContext().getContext("/SPDUser");
//		Object o = servletContext.getAttribute("session");
//		if(o == null) {
//			Response response  = Response.ok("no login").status(401).type(MediaType.APPLICATION_JSON).build();
////			try {
////				servletResponse.sendRedirect("/CIMAS/login/index.html");
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
//            throw new WebApplicationException(response);
//		}
//		HttpSession session = (HttpSession) o;
//		if(session == null) {
//			Response response  = Response.ok("no login").status(401).type(MediaType.APPLICATION_JSON).build();
////			try {
////				servletResponse.sendRedirect("/CIMAS/login/index.html");
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
//            throw new WebApplicationException(response);
//		}
//		String user = (String) session.getAttribute("user");
//		List<String> authorityCodes = (List<String>) session.getAttribute("AuthorityCode");
//		String areaCode = (String) session.getAttribute("areaCode");
////		if(path.startsWith("DisasterService/hourRain")) {
////			HourRainFilter hourRainFilter = new HourRainFilter();
////			String result = hourRainFilter.createQueryParam(list.get(0), authorityCodes, areaCode);
////			HttpServletRequestWrapper2 amHttpServletRequestWrapper = new HttpServletRequestWrapper2(servletRequest, servletRequest.getParameterMap()); 
////			amHttpServletRequestWrapper.setParameter("resultType", "AWS");
////		}
//		if(user == null) {
//			try {
//				servletResponse.sendRedirect("/CIMAS/land.html");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		System.out.println("user:" + user);
//		return request;
//	}
}
