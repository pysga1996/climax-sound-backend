package com.lambda.configurations.security;

import com.lambda.customizations.*;
import com.lambda.filters.JwtAuthenticationFilter;
import com.lambda.services.PlaylistService;
import com.lambda.services.UserService;
import com.lambda.services.impl.PlaylistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] CSRF_IGNORE = {"/api/login", "/api/register"};

    private CustomRestAuthenticationSuccessHandler customRestAuthenticationSuccessHandler;

    @Autowired
    public void setCustomRestAuthenticationSuccessHandler(CustomRestAuthenticationSuccessHandler customRestAuthenticationSuccessHandler) {
        this.customRestAuthenticationSuccessHandler = customRestAuthenticationSuccessHandler;
    }

    private CustomRestAuthenticationFailureHandler customRestAuthenticationFailureHandler;

    @Autowired
    public void setCustomRestAuthenticationFailureHandler(CustomRestAuthenticationFailureHandler customRestAuthenticationFailureHandler) {
        this.customRestAuthenticationFailureHandler = customRestAuthenticationFailureHandler;
    }

    private CustomRestAccessDeniedHandler customRestAccessDeniedHandler;

    @Autowired
    public void setCustomRestAccessDeniedHandler(CustomRestAccessDeniedHandler customRestAccessDeniedHandler) {
        this.customRestAccessDeniedHandler = customRestAccessDeniedHandler;
    }


    private CustomRestAuthenticationEntryPoint customRestAuthenticationEntryPoint;

    @Autowired
    public void setCustomRestAuthenticationEntryPoint(CustomRestAuthenticationEntryPoint customRestAuthenticationEntryPoint) {
        this.customRestAuthenticationEntryPoint = customRestAuthenticationEntryPoint;
    }

    private CustomRestLogoutSuccessHandler customRestLogoutSuccessHandler;

    @Autowired
    public void setCustomRestLogoutSuccessHandler(CustomRestLogoutSuccessHandler customRestLogoutSuccessHandler) {
        this.customRestLogoutSuccessHandler = customRestLogoutSuccessHandler;
    }

    private UserDetailsService userService;

    @Autowired
    public void setUserService(UserDetailsService userService) {
        this.userService = userService;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

//    private CsrfTokenRepository csrfTokenRepository() {
//        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//        repository.setHeaderName(CustomCsrfFilter.CSRF_COOKIE_NAME);
//        return repository;
//    }

//    @Autowired
//    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("bill").password("abc123").autorities("USER");
//        auth.inMemoryAuthentication().withUser("admin").password("root123").autorities("ADMIN");
//        auth.inMemoryAuthentication().withUser("dba").password("root123").autorities("ADMIN","DBA");
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .requiresChannel()
                // Heroku https config
//                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
//                .requiresSecure().and()
                .authorizeRequests()
                .antMatchers("/api/admin/**").access("hasRole('ADMIN')")
                .antMatchers("/oauth/token", "/api/login", "/api/register", "/api/song/download/**", "/api/song/upload", "/api/album/upload", "/api/album/download/**", "/api/**").permitAll()
                .and().formLogin()
//                .loginPage("/login")
//                .loginProcessingUrl("/appLogin")
                .successHandler(customRestAuthenticationSuccessHandler)
                .failureHandler(customRestAuthenticationFailureHandler)
                .usernameParameter("ssoId").passwordParameter("password")
                .and().csrf().disable()
//                .ignoringAntMatchers(CSRF_IGNORE)
//                .csrfTokenRepository(csrfTokenRepository()) // defines a repository where tokens are stored
//                .and()
//                .addFilterAfter(new CustomCsrfFilter(), CsrfFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(customRestAuthenticationEntryPoint)
                .accessDeniedHandler(customRestAccessDeniedHandler)
//                .accessDeniedPage("/accessDenied")
                .and()
                .logout().logoutSuccessHandler(customRestLogoutSuccessHandler)
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors();
        // Thêm một lớp Filter kiểm tra jwt
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.requiresChannel().anyRequest().requiresSecure();
    }
}
