package com.nt.rookies.asset.controller;


import com.nt.rookies.asset.dto.CategoryDTO;
import com.nt.rookies.asset.exception.NotFoundException;
import com.nt.rookies.asset.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CategoryDTO>> retrieveCategories() throws NotFoundException {
        return ResponseEntity.ok(categoryService.retrieveCategories());
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto){
        return new ResponseEntity<>(categoryService.createCategory(dto), HttpStatus.CREATED);
    }

}
