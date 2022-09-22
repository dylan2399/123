package com.nt.rookies.asset.mapper;


import com.nt.rookies.asset.dto.CategoryDTO;
import com.nt.rookies.asset.entity.CategoryEntity;
import com.nt.rookies.asset.exception.CategoryException;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    @Autowired
    ModelMapper modelMapper;

    public CategoryDTO convertToDto(CategoryEntity category) {
        try {
            CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);
            return dto;
        } catch (Exception ex) {
            throw new CategoryException(CategoryException.ERR_CONVERT_DTO_ENTITY_FAIL);
        }

    }

    public CategoryEntity convertToEntity(CategoryDTO dto) {
        try {
            CategoryEntity entity = modelMapper.map(dto, CategoryEntity.class);

            return entity;
        } catch (Exception ex) {
            throw new CategoryException(CategoryException.ERR_CONVERT_DTO_ENTITY_FAIL);
        }
    }

    public CategoryDTO convertEntityToBasicDTO(CategoryEntity categoryEntity) throws CategoryException {
        try {
            CategoryDTO dto = modelMapper.map(categoryEntity, CategoryDTO.class);
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CategoryException(CategoryException.ERR_CONVERT_DTO_ENTITY_FAIL);
        }

    }

    public List<CategoryDTO> convertToListDTO(List<CategoryEntity> categoryEntityList) throws CategoryException {
        try {
            return categoryEntityList.stream().map(c -> {
                try {
                    return convertEntityToBasicDTO(c);
                } catch (CategoryException e) {
                    e.printStackTrace();
                }
                return new CategoryDTO();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CategoryException(CategoryException.ERR_CONVERT_DTO_ENTITY_FAIL);
        }

    }

    public List<CategoryDTO> toListDto(List<CategoryEntity> listEntity) {
        List<CategoryDTO> listDto = new ArrayList<>();

        listEntity.forEach(e -> {
            listDto.add(this.convertToDto(e));
        });
        return listDto;
    }

}
