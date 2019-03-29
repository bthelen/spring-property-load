package io.pivotal.example.springpropertyload;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Telling SpringBoot it does not need to authorize any calls.
        http.antMatcher("**").authorizeRequests().anyRequest().permitAll();
    }
}
