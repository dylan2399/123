package com.nt.rookies.asset.repository;

import java.util.List;
import java.util.Optional;

import com.nt.rookies.asset.entity.CategoryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

	boolean existsByCategoryName(String name);

	boolean existsByPrefix(String prefix);

	Optional<CategoryEntity> findByCategoryName(String name);
	List<CategoryEntity> findAll();
}
