package com.example.demospringboot.service;

import com.example.demospringboot.api.DemoExecutor;
import com.example.demospringboot.dal.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoTask {

    @Autowired
    private DemoExecutor demoExecutor;

    @Autowired
    private UserMapper userMapper;

    @Scheduled(cron = "0 */1 * * * ?")
    public void serviceDemoTask(){
        demoExecutor.query("111");
    }
}
