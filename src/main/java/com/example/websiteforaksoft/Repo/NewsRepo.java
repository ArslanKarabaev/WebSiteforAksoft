package com.example.websiteforaksoft.Repo;

import com.example.websiteforaksoft.Entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepo extends JpaRepository<News, Long> {
    Page<News> findAll(Pageable pageable);
    Page<News> findByIsPublishedTrue(Pageable pageable);
    Optional<News> findByIdAndIsPublishedTrue(Long id);
}
