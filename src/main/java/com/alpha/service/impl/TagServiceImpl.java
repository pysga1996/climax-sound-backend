package com.alpha.service.impl;

import com.alpha.mapper.TagMapper;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.entity.Tag;
import com.alpha.repositories.TagRepository;
import com.alpha.service.TagService;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TagDTO> findById(Long id) {
        return this.tagRepository.findById(id).map(this.tagMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TagDTO findByName(String name) {
        return this.tagMapper.entityToDto(this.tagRepository.findByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDTO> findAll(Pageable pageable) {
        Page<Tag> tagPage = this.tagRepository.findAll(pageable);
        return new PageImpl<>(tagPage.getContent()
            .stream()
            .map(this.tagMapper::entityToDto)
            .collect(Collectors.toList()), pageable, tagPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDTO> findAllByNameContaining(String name, Pageable pageable) {
        Page<Tag> tagPage = this.tagRepository.findAllByNameContaining(name, pageable);
        return new PageImpl<>(tagPage.getContent()
            .stream()
            .map(this.tagMapper::entityToDto)
            .collect(Collectors.toList()), pageable, tagPage.getTotalElements());
    }

    @Override
    @Transactional
    public void save(TagDTO tag) {
        this.tagRepository.save(this.tagMapper.dtoToEntity(tag));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.tagRepository.deleteById(id);
    }
}
