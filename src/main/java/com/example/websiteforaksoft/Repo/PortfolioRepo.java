package com.example.websiteforaksoft.Repo;

import com.example.websiteforaksoft.Entity.MainBanner;
import com.example.websiteforaksoft.Entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepo extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByIdAndIsPublishedTrue(Long id);
    List<Portfolio> findAllByIsPublishedTrue();
}
