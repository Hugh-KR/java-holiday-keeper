package com.planitsquare.holiday_keeper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI holidayKeeperOpenAPI() {
        return new OpenAPI().info(new Info().title("Holiday Keeper API").description("""
                전 세계 공휴일 관리 서비스 API 문서

                Nager.Date API를 활용하여 2020-2025년 전 세계 공휴일 데이터를 저장·조회·관리하는 서비스입니다.
                """).version("v1.0.0"));
    }
}
