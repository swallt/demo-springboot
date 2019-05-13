package com.example.demospringboot.cat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatMybatisPluginConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Bean
    public CatMybatisPlugin catMybatisPlugin(){
        CatMybatisPlugin catMybatisPlugin = new CatMybatisPlugin(url);
        return catMybatisPlugin;
    }
}
