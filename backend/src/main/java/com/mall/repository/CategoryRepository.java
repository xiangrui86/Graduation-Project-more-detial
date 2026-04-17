package com.mall.repository;

import com.mall.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<Category> findByParentIdIsNullOrderBySortOrderAsc();

    Optional<Category> findByNameAndParentIdIsNull(String name);

    Optional<Category> findByNameAndParentId(String name, Long parentId);

    boolean existsByParentId(Long parentId);
}
