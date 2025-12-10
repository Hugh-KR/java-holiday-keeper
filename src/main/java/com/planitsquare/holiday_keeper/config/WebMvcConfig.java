package com.planitsquare.holiday_keeper.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String API_PREFIX = "/api";

    @Override
    public void configurePathMatch(final PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX, c -> c.getPackageName().contains("api.controller"));
    }
}
