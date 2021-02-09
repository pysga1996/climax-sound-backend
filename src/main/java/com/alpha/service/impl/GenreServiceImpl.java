package com.alpha.service.impl;

import com.alpha.mapper.GenreMapper;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.entity.Genre;
import com.alpha.repositories.GenreRepository;
import com.alpha.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GenreDTO> findById(Integer id) {
        return this.genreRepository.findById(id)
                .map(this.genreMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreDTO> findAll(Pageable pageable) {
        Page<Genre> genrePage = this.genreRepository.findAll(pageable);
        return new PageImpl<>(genrePage.getContent()
                .stream()
                .map(this.genreMapper::entityToDto)
                .collect(Collectors.toList()), pageable, genrePage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public GenreDTO findByName(String name) {
        return this.genreMapper.entityToDto(genreRepository.findByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GenreDTO> findAllByNameContaining(String name) {
        return StreamSupport
                .stream(this.genreRepository.findAllByNameContaining(name).spliterator(), false)
                .map(this.genreMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(GenreDTO genre) {
        this.genreRepository.save(this.genreMapper.dtoToEntity(genre));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        this.genreRepository.deleteById(id);
    }
}
