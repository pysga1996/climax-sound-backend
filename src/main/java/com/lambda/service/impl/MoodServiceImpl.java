package com.lambda.service.impl;

import com.lambda.model.Mood;
import com.lambda.repository.MoodRepository;
import com.lambda.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoodServiceImpl implements MoodService {
    @Autowired
    MoodRepository moodRepository;

    @Override
    public Optional<Mood> findById(Integer id) {
        return moodRepository.findById(id);
    }

    @Override
    public Mood findByName(String name) {
        return moodRepository.findByName(name);
    }

    @Override
    public Page<Mood> findAll(Pageable pageable) {
        return moodRepository.findAll(pageable);
    }

    @Override
    public Page<Mood> findAllByNameContaining(String name, Pageable pageable) {
        return moodRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public void save(Mood mood) {
        moodRepository.save(mood);
    }

    @Override
    public void deleteById(Integer id) {
        moodRepository.deleteById(id);
    }
}
