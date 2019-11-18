package com.lambda.configurations.security_customization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomRestAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger
            = LoggerFactory.getLogger(CustomRestAccessDeniedHandler.class.getName());

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException, ServletException {

        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Access denied");
    }
}
