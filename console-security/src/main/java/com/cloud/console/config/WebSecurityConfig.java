package com.cloud.console.config;

import com.cloud.console.filter.LoginFilter;
import com.cloud.console.service.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity/*(debug = true)*/
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    AuthenticationProvider authenticationProvider;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity
                .authorizeRequests()
                .antMatchers("/oauth/**").authenticated()
                .and()
                .authorizeRequests().antMatchers(
                "/encrypt/getPublicKey/**",
                "/static/**",
                "/favicon.ico",
                "/**/register",
                "/**/user/isExist/**",
                "/actuator/**",
                "/forgetPwd",
                "/getBackPwd",
                "/verifyBackPwd",
                "/restPwd",
                "/getAuthConfig",
                "/actuator/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/**/v3/api-docs",
                "/webjars/springfox-swagger-ui/**").permitAll().anyRequest().authenticated()
                /*.and()
                .formLogin().loginPage("http://www.frank.com/login").permitAll()*/
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new UnauthorizedEntryPoint())
                .and()
                .addFilterBefore(loginFilter(),
                        UsernamePasswordAuthenticationFilter.class);
        //httpSecurity.headers().cacheControl();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth", "POST"));
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        return loginFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.addExposedHeader("*");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }
}
