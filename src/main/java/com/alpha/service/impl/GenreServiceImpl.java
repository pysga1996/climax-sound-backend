package com.alpha.service.impl;

import com.alpha.mapper.GenreMapper;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.entity.Genre;
import com.alpha.repositories.GenreRepository;
import com.alpha.service.GenreService;
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
    public GenreDTO findById(Integer id) {
        return this.genreRepository.findById(id)
            .map(this.genreMapper::entityToDto)
            .orElseThrow(() -> new EntityNotFoundException("Genre not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreDTO> findAll(Pageable pageable) {
        return this.genreRepository.findAllByOrderByUpdateTimeDescCreateTimeDesc(pageable)
            .map(this.genreMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreDTO findByName(String name) {
        return this.genreRepository.findByName(name).map(this.genreMapper::entityToDto)
            .orElseThrow(() -> new EntityNotFoundException("Genre not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreDTO> findAllByNameContaining(String name,
        Pageable pageable) {
        return this.genreRepository.findAllByNameContaining(name, pageable)
            .map(this.genreMapper::entityToDto);
    }

    @Override
    @Transactional
    public GenreDTO create(GenreDTO genreDTO) {
        Optional<Genre> exitedGenreOptional = this.genreRepository.findByName(genreDTO.getName());
        if (exitedGenreOptional.isPresent()) {
            throw new EntityExistsException("Genre with the a name existed");
        }
        Genre createGenre = this.genreMapper.dtoToEntity(genreDTO);
        createGenre.setCreateTime(new Date());
        createGenre.setStatus(1);
        createGenre = this.genreRepository.saveAndFlush(createGenre);
        return this.genreMapper.entityToDto(createGenre);
    }

    @Override
    @Transactional
    public GenreDTO update(Integer id, GenreDTO genreDTO) {
        boolean existed = this.genreRepository.existsByIdAndName(id, genreDTO.getName());
        if (existed) {
            throw new EntityExistsException("Genre with the a name existed");
        }
        Optional<Genre> existedGenreOptional = this.genreRepository.findById(id);
        if (existedGenreOptional.isPresent()) {
            existedGenreOptional.get().setName(genreDTO.getName());
            existedGenreOptional.get().setUpdateTime(new Date());
            this.genreRepository.save(existedGenreOptional.get());
            return this.genreMapper.entityToDto(existedGenreOptional.get());
        } else {
            throw new EntityNotFoundException("Genre not found");
        }
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        this.genreRepository.deleteById(id);
    }
}
