package com.example.demospringboot.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.example.demospringboot.dal.dao")
public class MapperConfig {

    private final Logger logger = LoggerFactory.getLogger(MapperConfig.class);

    public MapperConfig(){
        if (logger.isInfoEnabled()){
            logger.info("Scan dao interface");
        }
    }
}
