package com.lambda.repositories;

import com.lambda.models.entities.User;
import com.lambda.models.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    List<VerificationToken> findAllByExpiryDateBefore(Date date);
}
