package com.example.websiteforaksoft.Repo;

import com.example.websiteforaksoft.Entity.MainBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MainBannerRepo extends JpaRepository<MainBanner, Long> {
    List<MainBanner> findAllByIsActiveTrue();

    Optional<MainBanner> findByIdAndIsActiveTrue(Long id);

    Optional<MainBanner> findByIdAndIsActiveFalse(Long id);

}
