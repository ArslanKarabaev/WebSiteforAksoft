package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.PortfolioDto;
import com.example.websiteforaksoft.Entity.Portfolio;
import com.example.websiteforaksoft.Exception.DuplicateResourceException;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.PortfolioMapper;
import com.example.websiteforaksoft.Repo.PortfolioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepo portfolioRepo;
    private final PortfolioMapper portfolioMapper;

    public List<PortfolioDto> getAllPortfolio() {
        List<Portfolio> portfolios = portfolioRepo.findAll();
        return portfolioMapper.toDtoList(portfolios);
    }

    public List<PortfolioDto> getAllPublishedPortfolio() {
        List<Portfolio> portfolios = portfolioRepo.findAllByIsPublishedTrue();
        return portfolioMapper.toDtoList(portfolios);
    }

    public PortfolioDto getPublishedPortfolioById(Long id) {
        Portfolio portfolio = portfolioRepo.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нашей работы", "id", id));
        return portfolioMapper.toDto(portfolio);
    }

    @Transactional
    public PortfolioDto addPortfolio(PortfolioDto portfolioDto) {

        Portfolio portfolio = portfolioMapper.toEntity(portfolioDto);
        portfolio.setCreatedAt(LocalDate.now());
        portfolio.setIsPublished(true);
        Portfolio savedPortfolio = portfolioRepo.save(portfolio);
        return portfolioMapper.toDto(savedPortfolio);
    }

    @Transactional
    public PortfolioDto updatePortfolio(Long id, PortfolioDto portfolioDto) {
        Portfolio portfolio = portfolioRepo.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нашей работы", "id", id));
        if (portfolioDto.getTitle() != null) {
            portfolio.setTitle(portfolioDto.getTitle());
        }
        if (portfolioDto.getDescription() != null) {
            portfolio.setDescription(portfolioDto.getDescription());
        }
        if (portfolioDto.getImageUrl() != null) {
            portfolio.setImageUrl(portfolioDto.getImageUrl());
        }
        if (portfolioDto.getProjectUrl() != null) {
            portfolio.setProjectUrl(portfolioDto.getProjectUrl());
        }

        Portfolio savedPortfolio = portfolioRepo.save(portfolio);
        return portfolioMapper.toDto(savedPortfolio);
    }

    @Transactional
    public void deletePortfolio(Long id) {
        Portfolio portfolio = portfolioRepo.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нашей работы", "id", id));
        if(portfolio.getIsPublished()) {
            portfolio.setIsPublished(false);
        }else throw new RuntimeException("Наша работа уже деактивирована");
    }

    @Transactional
    public void restorePortfolio(Long id) {
        Portfolio portfolio = portfolioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нашей работы", "id", id));
        if(!portfolio.getIsPublished()) {
            portfolio.setIsPublished(true);
        }else throw new RuntimeException("Наша работа уже активирована");
    }

}
