package com.redhat.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@Configuration("kieServerSecurity")
@EnableWebSecurity
public class DefaultWebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Value("${kieserver.startup.strategy}")
    private String strategy;
    
    @Value("${kieserver.controlleruser}")
    private String controllerUser;
    
    @Value("${kieserver.controllerpwd}")
    private String controllerPwd;
    
    @Value("${kieserver.dateFormat}")
    private String dateFormat;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/rest/*").authenticated().and()
                .httpBasic().and()
                .headers().frameOptions().disable();
    }
    
    @PostConstruct
    public void init() {
    	System.setProperty("org.kie.server.controller.user", controllerUser);
    	System.setProperty("org.kie.server.controller.pwd", controllerPwd);
    	System.setProperty("org.kie.server.startup.strategy", strategy);
    	System.setProperty("org.kie.server.json.format.date", dateFormat);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        //auth.inMemoryAuthentication().withUser("user").password("user").roles("kie-server");
        //auth.inMemoryAuthentication().withUser("wbadmin").password("wbadmin").roles("admin");
        //auth.inMemoryAuthentication().withUser("kieserver").password("kieserver1!").roles("kie-server");
        //auth.inMemoryAuthentication().withUser("kris").password("password1!").roles("kie-server", "admin", "rest-all");
        auth.inMemoryAuthentication().withUser("rhpamAdmin").password("Pa$$w0rd").roles("user", "kie-server");
        auth.inMemoryAuthentication().withUser("user1").password("user1").roles("user", "kie-server");
        auth.inMemoryAuthentication().withUser("rama").password("Pa$$w0rd").roles("admin", "kie-server");
        auth.inMemoryAuthentication().withUser("adminKieUser").password("Pa$$w0rd").roles("user", "kie-server");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(),
                HttpMethod.POST.name(), HttpMethod.DELETE.name(), HttpMethod.PUT.name()));
        corsConfiguration.applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
