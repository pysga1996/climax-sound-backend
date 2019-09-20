package com.lambda.configuration.security;

import com.lambda.configuration.security_customization.CustomFailureHandler;
import com.lambda.configuration.security_customization.CustomSuccessHandler;
import com.lambda.configuration.security_filter.CustomCsrfFilter;
import com.lambda.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] CSRF_IGNORE = {"/api/login", "/api/register"};

    @Autowired
    CustomSuccessHandler customSuccessHandler;

    @Autowired
    CustomFailureHandler customFailureHandler;

    @Autowired
    UserDetailServiceImpl userDetailServiceImpl;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName(CustomCsrfFilter.CSRF_COOKIE_NAME);
        return repository;
    }

//    @Autowired
//    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("bill").password("abc123").roles("USER");
//        auth.inMemoryAuthentication().withUser("admin").password("root123").roles("ADMIN");
//        auth.inMemoryAuthentication().withUser("dba").password("root123").roles("ADMIN","DBA");
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
            throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailServiceImpl) // Cung cáp userservice cho spring security
                .passwordEncoder(passwordEncoder); // cung cấp password encoder
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/register", "/login", "/", "/home").permitAll()
//                .anyRequest().authenticated()
//                .antMatchers("/api/user/list").access("hasRole('USER')")
                .antMatchers("/admin/**").access("hasRole('ADMIN')")
                .antMatchers("/dba/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .antMatchers("/note").access("(hasRole('USER')) or hasRole('ADMIN')")
                .antMatchers("/category").access("(hasRole('USER')) or hasRole('ADMIN')")
                .and().formLogin().loginPage("/login")
                .loginProcessingUrl("/appLogin")
                .successHandler(customSuccessHandler)
                .failureHandler(customFailureHandler)
                .usernameParameter("ssoId").passwordParameter("password")
                .and().csrf().ignoringAntMatchers(CSRF_IGNORE)
                .csrfTokenRepository(csrfTokenRepository()) // defines a repository where tokens are stored
                .and()
                .addFilterAfter(new CustomCsrfFilter(), CsrfFilter.class)
                .
//                        headers().addHeaderWriter(
//                new StaticHeadersWriter("Access-Control-Allow-Origin", "*")).and()
//                .
        exceptionHandling().accessDeniedPage("/Access_Denied")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
    }
}
