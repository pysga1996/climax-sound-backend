package com.lambda.service.impl;

import com.lambda.model.entity.Activity;
import com.lambda.repository.ActivityRepository;
import com.lambda.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    ActivityRepository activityRepository;

    @Override
    public Activity findByName(String name){
        return activityRepository.findByName(name);
    }

    @Override
    public Optional<Activity> findById(Integer id) {
        return activityRepository.findById(id);
    }

    @Override
    public Page<Activity> findAll(Pageable pageable) {
        return activityRepository.findAll(pageable);
    }

    @Override
    public Page<Activity> findAllByNameContaining(String name, Pageable pageable) {
        return activityRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public void save(Activity activity) {
        activityRepository.save(activity);
    }

    @Override
    public void deleteById(Integer id) {
        activityRepository.deleteById(id);
    }
}
