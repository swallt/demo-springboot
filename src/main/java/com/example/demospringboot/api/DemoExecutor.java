package com.example.demospringboot.api;

import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.example.demospringboot.dal.bean.User;
import com.example.demospringboot.dal.dao.UserMapper;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DemoExecutor {
    private Logger logger = LoggerFactory.getLogger(DemoExecutor.class);

    @Resource
    private UserMapper userMapper;

    public void query(String id){
        Transaction t = Cat.newTransaction("URL","demoquery");

        try {
            Cat.logEvent("URL.Method","query",Event.SUCCESS," ");
            Cat.logMetricForCount("metric.key");
            Cat.logMetricForDuration("metric.key", 5);

             /* RestTemplate restTemplate = new RestTemplate();
        String url = "";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("id",id);
        String authpass = auth(map);
        map.add("sign",authpass);
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(map,headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url,request,String.class);
        JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());*/
            User user = new User();
//        user.setEmail(jsonObject.getString("eamil"));
            user.setEmail("aaa@163.com");
            user.setId(id);
            userMapper.updateById(user);

            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e){
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }


    }

    public String auth(MultiValueMap<String,?> content){
        String authContent = authContent(content, false);
        String authpass = DigestUtils.md5DigestAsHex(authContent.getBytes());
        return authpass;
    }

    private static String authContent(MultiValueMap<String,?> content,boolean encodeValue){
        if (MapUtils.isEmpty(content)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(content.keySet());
        Collections.sort(keys);
        int i = 1;
        for (String key : keys){
            String value = String.valueOf(content.get(key));
            if (StringUtils.hasText(key)&&StringUtils.hasText(value)){
                sb.append(key).append("=");
                if (encodeValue){
                    try {
                        sb.append(URLEncoder.encode(value,"UTF-8"));
                    }catch (UnsupportedEncodingException e){}
                } else {
                    sb.append(value);
                }
                sb.append(i == keys.size() ? "" : "&");
            }
            ++i;
        }
        return sb.toString();
    }
}
