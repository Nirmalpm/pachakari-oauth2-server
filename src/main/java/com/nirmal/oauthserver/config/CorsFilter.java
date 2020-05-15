package com.nirmal.oauthserver.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
	
	public CorsFilter() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse httpresponse = (HttpServletResponse)response;
		HttpServletRequest httprequest = (HttpServletRequest)request;
		httpresponse.setHeader("Access-Control-Allow-Origin", "*");
		httpresponse.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
		httpresponse.setHeader("Access-Control-Max-Age", "3600");
		httpresponse.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,Content-Type");
		
		if("OPTIONS".equalsIgnoreCase(httprequest.getMethod())) {
			httpresponse.setStatus(HttpServletResponse.SC_OK);
		}else {
			chain.doFilter(httprequest, httpresponse);
		}
	}

}
