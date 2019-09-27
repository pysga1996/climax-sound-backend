package com.lambda.service.impl;

import com.lambda.model.entity.Privilege;
import com.lambda.repository.PrivilegeRepository;
import com.lambda.service.PrivilegeService;
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
