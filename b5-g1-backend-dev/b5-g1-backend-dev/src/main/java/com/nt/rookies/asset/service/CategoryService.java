package com.nt.rookies.asset.service;


import com.nt.rookies.asset.dto.CategoryDTO;
import com.nt.rookies.asset.entity.CategoryEntity;
import com.nt.rookies.asset.exception.CategoryException;
import com.nt.rookies.asset.mapper.CategoryMapper;
import com.nt.rookies.asset.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public List<CategoryDTO> retrieveCategories() {
        try {
            List<CategoryEntity> categories;
            try {
                categories = categoryRepository.findAll();
            } catch (Exception e) {
                throw new CategoryException(CategoryException.ERR_RETRIEVE_CATEGORY_FAIL);
            }
            List<CategoryDTO> viewAssetsCategoryDTO = categoryMapper.convertToListDTO(categories);
            return viewAssetsCategoryDTO;
        } catch (Exception e) {
            throw new CategoryException(CategoryException.ERR_RETRIEVE_CATEGORY_FAIL);
        }
    }

    public CategoryDTO createCategory(CategoryDTO dto) {
        CategoryEntity entity = categoryMapper.convertToEntity(dto);
        if (categoryRepository.existsByCategoryName(dto.getCategoryName())) {
            throw new CategoryException(CategoryException.CATEGORY_NAME_UNIQUE);
        }
        if (dto.getPrefix() == null || dto.getPrefix().isEmpty()) {
            String prefix = "";
            if (dto.getCategoryName().contains(" ")) {
                String[] strings = dto.getCategoryName().split(" ");
                for (String str : strings) {
                    prefix += str.charAt(0);
                }
            } else {
                prefix = dto.getCategoryName().substring(0, 2);
            }
            entity.setPrefix(prefix.toUpperCase());
        } else {
            entity.setPrefix(dto.getPrefix().toUpperCase());
        }
        if (categoryRepository.existsByPrefix(entity.getPrefix())) {
            throw new CategoryException(CategoryException.CATEGORY_PREFIX_UNIQUE);
        }
        try {
            return categoryMapper.convertToDto(categoryRepository.save(entity));
        } catch (Exception ex) {
            throw new CategoryException(CategoryException.CATEGORY_CREATE_FAIL);
        }
    }

}
