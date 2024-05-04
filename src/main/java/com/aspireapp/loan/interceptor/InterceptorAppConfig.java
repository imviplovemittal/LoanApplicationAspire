package com.aspireapp.loan.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Component
public class InterceptorAppConfig implements WebMvcConfigurer {

    @Autowired
    private ValidateUserTokenInterceptor validateUserTokenInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(validateUserTokenInterceptor)
            .addPathPatterns("/api/loans/**", "/api/payment/**");

    }
}