package com.mall.repository;

import com.mall.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    List<News> findByPublishedTrueAndTypeOrderByCreatedAtDesc(String type, Pageable pageable);
}
