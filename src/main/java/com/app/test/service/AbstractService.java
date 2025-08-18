package com.app.test.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.app.test.exception.ResourceNotFoundException;
import com.app.test.model.base.BaseAuditing;
import com.app.test.model.base.BasePageDto;
import com.app.test.repository.BaseRepository;
import com.app.test.specification.common.CommonSpecificationBuilder;

import java.lang.reflect.*;
import java.util.*;


public abstract class AbstractService<MODEL extends BaseAuditing, GET_DTO, CREATE_DTO, UPDATE_DTO, PAGEABLE_DTO extends BasePageDto<GET_DTO>, PK_TYPE>{


    @Autowired
    public ModelMapper modelMapper;

    private final Type[] typeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

    protected BaseRepository<MODEL, PK_TYPE> repository;

    public Optional<GET_DTO> get(PK_TYPE primaryKey) {
        return repository.findById(primaryKey).map(this::convertToDto);
    }

    public PAGEABLE_DTO getAll(CommonSpecificationBuilder<MODEL> builder) {
        return getAll(builder, null, false);
    }

    public PAGEABLE_DTO getAll(CommonSpecificationBuilder<MODEL> builder, Pageable pageable, Boolean showTotalCount) {
        if (pageable != null) {
            Page<GET_DTO> itemPage = repository.findAll(builder.build(), pageable).map(this::convertToDto);
            PAGEABLE_DTO pageableDto = convertToPageDto(itemPage);
            if (showTotalCount != null && showTotalCount) {
                long totalCount = repository.count(builder.build());
                pageableDto.setTotalCount(totalCount);
            }
            return pageableDto;
        } else {
            List<GET_DTO> list = repository.findAll(builder.build()).stream().map(this::convertToDto).toList();
            Page<GET_DTO> page;
            if (list.isEmpty()) {
                page = Page.empty();
            } else {
                pageable = PageRequest.of(0, list.size());
                page = new PageImpl<>(list, pageable, list.size());
            }
            return convertToPageDto(page);
        }
    }

    public GET_DTO create(CREATE_DTO createDto) {
        validateCreateDto(createDto);
        MODEL entity = convertToEntity(createDto);
        doCreate(entity);
        MODEL saved = save(entity);
        return convertToDto(saved);
    }

    public Optional<GET_DTO> update(PK_TYPE id, UPDATE_DTO updateDto) {
        MODEL existing = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException((Class<?>) typeArguments[0], id.toString())
        );

        validateUpdateDto(updateDto, existing);

        convertUpdateToEntity(updateDto, existing);
        doUpdate(existing, updateDto);
        MODEL saved = save(existing);

        return Optional.of(convertToDto(saved));
    }

    public void delete(PK_TYPE id) {
        validateDelete(id);
        //Soft delete implemented by default. If you want to hard delete, override this method in the service class
        softDelete(id);
    }

    protected void softDelete(PK_TYPE id) {
        Optional<MODEL> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return;
        }

        MODEL toDelete = existing.get();
        doDelete(toDelete);

        toDelete.setDeleted(true);
        save(toDelete);
    }

    protected void hardDelete(PK_TYPE id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
    }

    private MODEL save(MODEL entity) {
        return repository.save(entity);
    }

    protected abstract void validateCreateDto(CREATE_DTO createDto);

    protected abstract void validateUpdateDto(UPDATE_DTO updateDto, MODEL existing);

    protected abstract void validateDelete(PK_TYPE id);

    protected void doCreate(MODEL toCreate) {
    }

    protected void doUpdate(MODEL toUpdate, UPDATE_DTO updateDto) {
    }

    protected void doDelete(MODEL entity) {
    }

    public GET_DTO convertToDto(MODEL entity) {
        return modelMapper.map(entity, getDtoClass());
    }

    public PAGEABLE_DTO convertToPageDto(Page<GET_DTO> itemPage) {
        return modelMapper.map(itemPage, getPageableDtoClass());
    }

    public MODEL convertToEntity(CREATE_DTO createDto) {
        return modelMapper.map(createDto, getModelClass());
    }

    public MODEL convertDtoToEntity(GET_DTO getDto) {
        return modelMapper.map(getDto, getModelClass());
    }

    public MODEL convertUpdateToEntity(UPDATE_DTO updateDto, MODEL toUpdate) {
        modelMapper.map(updateDto, toUpdate);
        return toUpdate;
    }

    public UPDATE_DTO convertToUpdateDto(MODEL entity) {
        return modelMapper.map(entity, getUpdateDtoClass());
    }

    protected Class<GET_DTO> getDtoClass() {
        return (Class<GET_DTO>) typeArguments[1];

    }

    protected Class<MODEL> getModelClass() {
        return (Class<MODEL>) typeArguments[0];
    }

    protected Class<CREATE_DTO> getCreateDtoClass() {
        return (Class<CREATE_DTO>) typeArguments[2];
    }

    protected Class<UPDATE_DTO> getUpdateDtoClass() {
        return (Class<UPDATE_DTO>) typeArguments[3];
    }

    protected Class<PAGEABLE_DTO> getPageableDtoClass() {
        return (Class<PAGEABLE_DTO>) typeArguments[4];
    }
}
