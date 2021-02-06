package com.alpha.config.general;

import org.springframework.stereotype.Component;

@Component
public class TokenCleaner {

//    private static final Logger LOG = LoggerFactory.getLogger(TokenCleaner.class);
//
//    private VerificationTokenRepository verificationTokenRepository;
//    private PasswordResetTokenRepository passwordResetTokenRepository;
//
//    @Autowired
//    public void setVerificationTokenRepository(VerificationTokenRepository verificationTokenRepository) {
//        this.verificationTokenRepository = verificationTokenRepository;
//    }
//
//    @Autowired
//    public void setPasswordResetTokenRepository(PasswordResetTokenRepository passwordResetTokenRepository) {
//        this.passwordResetTokenRepository = passwordResetTokenRepository;
//    }
//
//    @Scheduled(fixedDelay = 86400000)
//    public void herokuNotIdle() {
//        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAllByExpiryDateBefore(new Date());
//        List<PasswordResetToken> passwordResetTokenList = passwordResetTokenRepository.findAllByExpiryDateBefore(new Date());
//        verificationTokenRepository.deleteAll(verificationTokenList);
//        passwordResetTokenRepository.deleteAll(passwordResetTokenList);
//        LOG.debug("Tokens cleared!");
//    }
}
