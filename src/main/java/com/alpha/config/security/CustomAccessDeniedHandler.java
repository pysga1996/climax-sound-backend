package com.alpha.config.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * @author thanhvt
 * @created 26/04/2021 - 11:56 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Primary
@Component
public class CustomAccessDeniedHandler extends ErrorResponseProducer implements
    AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, AccessDeniedException ex)
        throws IOException {
        log.error(ex);
        super.produceErrorResponse(httpServletRequest, httpServletResponse, ex,
            HttpStatus.FORBIDDEN);
    }
}
