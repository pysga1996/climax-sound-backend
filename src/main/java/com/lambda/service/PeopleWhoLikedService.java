package com.lambda.service;

import org.springframework.stereotype.Service;

public interface PeopleWhoLikedService {
    void like(Long id);
    void unlike(Long id);
}
