package com.alpha.config.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * @author thanhvt
 * @created 27/04/2021 - 12:33 SA
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Primary
@Component
public class CustomAuthenticationEntryPoint extends ErrorResponseProducer implements
    AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, AuthenticationException ex)
        throws IOException {
        log.error(ex);
        super.produceErrorResponse(httpServletRequest, httpServletResponse, ex,
            HttpStatus.UNAUTHORIZED);
    }
}
