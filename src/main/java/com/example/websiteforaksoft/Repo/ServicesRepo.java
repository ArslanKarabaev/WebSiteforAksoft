package com.example.websiteforaksoft.Repo;

import com.example.websiteforaksoft.Entity.Portfolio;
import com.example.websiteforaksoft.Entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicesRepo extends JpaRepository<Services, Long> {
    Optional<Services> findByIdAndIsPublishedTrue(Long id);
    Optional<Services> findByIdAndIsPublishedFalse(Long id);
    List<Services> findAllByIsPublishedTrue();
}
