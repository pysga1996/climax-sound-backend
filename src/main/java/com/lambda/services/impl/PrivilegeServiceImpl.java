package com.lambda.services.impl;

import com.lambda.models.entities.Privilege;
import com.lambda.repositories.PrivilegeRepository;
import com.lambda.services.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {
    @Autowired
    PrivilegeRepository privilegeRepository;

    @Override
    public Privilege findByName(String name) {
        return privilegeRepository.findByName(name);
    }

    @Override
    public void save(Privilege privilege) {
        privilegeRepository.save(privilege);
    }
}
