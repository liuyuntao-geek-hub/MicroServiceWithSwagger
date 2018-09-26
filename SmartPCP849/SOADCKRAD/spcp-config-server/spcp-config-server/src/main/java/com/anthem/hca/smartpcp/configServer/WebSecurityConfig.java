package com.anthem.hca.smartpcp.configServer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
               .antMatchers("/*crypt/*").authenticated()
               .anyRequest().permitAll()
             .and()
             .httpBasic()
             .and()
             .csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	/*
    	 * WE CAN CONSIDER MOVING TO A BETTER AUTHENTICATION/AUTHORIZATION MECHANISM IN FUTURE
    	 */
        auth
            .inMemoryAuthentication()
                .withUser("dockeradmin").password("d0ck3r@dm1n").roles("USER");
    }

}