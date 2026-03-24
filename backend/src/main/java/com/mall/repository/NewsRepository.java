package com.mall.repository;

import com.mall.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** 资讯仓储：提供资讯与公告的分页/列表查询。 */
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<News> findByPublishedTrueAndTypeNotOrderByCreatedAtDesc(String type, Pageable pageable);

    List<News> findByPublishedTrueAndTypeOrderByCreatedAtDesc(String type, Pageable pageable);
}
