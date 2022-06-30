package com.brenner.portfoliomgmt;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for the Spring MVC
 * 
 * @author dbrenner
 *
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/error").setViewName("error");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	registry.addMapping("/restful/**")
        	.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }
}