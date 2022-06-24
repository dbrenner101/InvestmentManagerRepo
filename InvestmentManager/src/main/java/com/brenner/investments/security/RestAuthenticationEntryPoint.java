package com.brenner.investments.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	@Override
    public void commence
      (HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) 
      throws IOException {
		
		System.out.println("REQUEST HEADERS: ");
		Enumeration<String> reqEnum = request.getHeaderNames();
		while (reqEnum.hasMoreElements()) {
			String headerName = reqEnum.nextElement();
			System.out.println(headerName + " :: " + request.getHeader(headerName));
		}
		
		System.out.println("\n\nRESPONSE HEADERS: ");
		Collection<String> resCollection = response.getHeaderNames();
		Iterator<String> iter = resCollection.iterator();
		while(iter.hasNext()) {
			String headerName = iter.next();
			System.out.println(headerName + " :: " + response.getHeader(headerName));
		}
		
		
		System.out.println("\n\nAuth Message: " + authEx.getMessage());
		
		
		
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println("HTTP Status 401 - " + authEx.getMessage());
    }
 
    @Override
    public void afterPropertiesSet() {
        setRealmName("Brenner");
        super.afterPropertiesSet();
    }

}
