package com.lambda.climaxsound;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@TestConfiguration
public class ClimaxSoundTestConfiguration {
    @MockBean
    private JavaMailSender javaMailSender;
}
