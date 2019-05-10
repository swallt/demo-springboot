package com.example.demospringboot.api;

import com.example.demospringboot.dal.dao.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

@RestController
@RequestMapping(value = "/info")
public class TestController {
    private Logger logger= LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UserMapper userMapper;

    @RequestMapping(value = "/accept")
    public void accept(HttpServletRequest req, HttpServletResponse res){
        System.out.println("....");
    }

    //设置报文信息返回
    public void setResponse(HttpServletResponse res, String resReport){
        res.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        res.setDateHeader("expires",0);
        try {
            byte[] bytes = resReport.getBytes("utf-8");
            res.setHeader("Access-Control-Allow-Origin", "*");
            res.setHeader("Access-Control-Allow-Headers",
                    "Origin, X-Requested-With, Content-Type, Accept, Connection, CardInfo-Agent, Cookie");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(res.getOutputStream(),"utf-8"));
            bw.write(resReport.toString());
            bw.flush();
            bw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
