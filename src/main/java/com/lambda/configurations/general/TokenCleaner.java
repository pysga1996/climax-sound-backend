package com.lambda.configurations.general;

import com.lambda.models.entities.VerificationToken;
import com.lambda.repositories.VerificationTokenRepository;
import com.lambda.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Scope("singleton")
public class TokenCleaner {
    private static final Logger LOG = LoggerFactory.getLogger(TokenCleaner.class);

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Scheduled(fixedDelay=86400000)
    public void herokuNotIdle(){
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAllByExpiryDateBefore(new Date());
        verificationTokenRepository.deleteAll(verificationTokenList);
        LOG.debug("Tokens cleared!");
    }
}
