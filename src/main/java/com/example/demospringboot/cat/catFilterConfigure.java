package com.example.demospringboot.cat;

import com.dianping.cat.servlet.CatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class catFilterConfigure {

    @Bean
    public FilterRegistrationBean catFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CatFilter filter = new CatFilter();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        //   registration.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        registrationBean.setName("cat-filter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
