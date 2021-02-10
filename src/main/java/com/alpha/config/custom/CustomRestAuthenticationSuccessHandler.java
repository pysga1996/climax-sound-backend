package com.alpha.config.custom;

import com.alpha.model.dto.UserDTO;
import com.alpha.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Log4j2
@Component
public class CustomRestAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    @Autowired
    public CustomRestAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = messageConverter.getObjectMapper();
        String username = authentication.getPrincipal().toString();
        Optional<UserDTO> user = userService.findByUsername(username);

        user.ifPresent(value -> log.info(value + " got is connected "));

        PrintWriter writer = response.getWriter();
        mapper.writeValue(writer, user);
        writer.flush();
    }
}
