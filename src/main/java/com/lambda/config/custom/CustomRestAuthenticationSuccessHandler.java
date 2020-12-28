package com.lambda.config.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambda.model.entities.User;
import com.lambda.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Component
public class CustomRestAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRestAuthenticationSuccessHandler.class);

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = messageConverter.getObjectMapper();
        String username = authentication.getPrincipal().toString();
        Optional<User> user = userService.findByUsername(username);

        user.ifPresent(value -> LOGGER.info(value + " got is connected "));

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, user);
        writer.flush();
    }
}
