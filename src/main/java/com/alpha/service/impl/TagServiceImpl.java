package com.alpha.service.impl;

import com.alpha.constant.ModelStatus;
import com.alpha.mapper.TagMapper;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.entity.Tag;
import com.alpha.repositories.TagRepository;
import com.alpha.service.TagService;
import java.util.Date;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
        return this.tagRepository.findByName(name).map(this.tagMapper::entityToDto)
            .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDTO> findAll(Pageable pageable) {
        return this.tagRepository.findAllByOrderByUpdateTimeDescCreateTimeDesc(pageable)
            .map(this.tagMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagDTO> findAllByNameContaining(String name, Pageable pageable) {
        return this.tagRepository.findAllByNameContaining(name, pageable)
            .map(this.tagMapper::entityToDto);
    }

    @Override
    @Transactional
    public TagDTO create(TagDTO tagDTO) {
        Optional<Tag> exitedGenreOptional = this.tagRepository.findByName(tagDTO.getName());
        if (exitedGenreOptional.isPresent()) {
            throw new EntityExistsException("Tag with the a name existed");
        }
        Tag createdTag = this.tagMapper.dtoToEntity(tagDTO);
        createdTag.setCreateTime(new Date());
        createdTag.setStatus(ModelStatus.ACTIVE);
        createdTag = this.tagRepository.saveAndFlush(createdTag);
        return this.tagMapper.entityToDto(createdTag);
    }

    @Override
    @Transactional
    public TagDTO update(Long id, TagDTO tagDTO) {
        boolean existed = this.tagRepository.existsByIdAndName(id, tagDTO.getName());
        if (existed) {
            throw new EntityExistsException("Tag with the a name existed");
        }
        Optional<Tag> existedTagOptional = this.tagRepository.findById(id);
        if (existedTagOptional.isPresent()) {
            existedTagOptional.get().setName(tagDTO.getName());
            existedTagOptional.get().setUpdateTime(new Date());
            this.tagRepository.save(existedTagOptional.get());
            return this.tagMapper.entityToDto(existedTagOptional.get());
        } else {
            throw new EntityNotFoundException("Tag not found");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<Tag> optionalTag = this.tagRepository.findById(id);
        if (optionalTag.isPresent()) {
            Tag country = optionalTag.get();
            country.setStatus(ModelStatus.INACTIVE);
            country.setUpdateTime(new Date());
            this.tagRepository.saveAndFlush(optionalTag.get());
        } else {
            throw new EntityNotFoundException("Tag not found!");
        }
    }
}
