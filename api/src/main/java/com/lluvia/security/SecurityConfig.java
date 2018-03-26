package com.lluvia.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public SecurityConfig() {
        System.out.println("Security config invoked");
    }
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        System.out.println("FIlter Added");
        httpSecurity.addFilterBefore(new AuthFilter(), BasicAuthenticationFilter.class).csrf().disable();
    }
}
