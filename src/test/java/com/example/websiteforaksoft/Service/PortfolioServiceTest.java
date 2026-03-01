package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.PortfolioDto;
import com.example.websiteforaksoft.Entity.Portfolio;
import com.example.websiteforaksoft.Mapper.PortfolioMapper;
import com.example.websiteforaksoft.Repo.PortfolioRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PortfolioService Tests")
class PortfolioServiceTest {

    @Mock
    private PortfolioRepo portfolioRepo;
    @Mock
    private PortfolioMapper portfolioMapper;
    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio testPortfolio;
    private PortfolioDto testPortfolioDto;

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio();
        testPortfolio.setId(1L);
        testPortfolio.setTitle("E-Commerce App");
        testPortfolio.setDescription("Online store");
        testPortfolio.setImageUrl("project.jpg");
        testPortfolio.setProjectUrl("http://project.com");
        testPortfolio.setIsPublished(true);
        testPortfolio.setCreatedAt(LocalDate.now());

        testPortfolioDto = new PortfolioDto();
        testPortfolioDto.setId(1L);
        testPortfolioDto.setTitle("E-Commerce App");
    }

    @Test
    @DisplayName("Should get all portfolio")
    void getAllPortfolio_Success() {
        when(portfolioRepo.findAll()).thenReturn(Arrays.asList(testPortfolio));
        when(portfolioMapper.toDtoList(any())).thenReturn(Arrays.asList(testPortfolioDto));

        List<PortfolioDto> result = portfolioService.getAllPortfolio();

        assertEquals(1, result.size());
        verify(portfolioRepo).findAll();
    }

    @Test
    @DisplayName("Should get only published portfolio")
    void getAllPublishedPortfolio_Success() {
        when(portfolioRepo.findAllByIsPublishedTrue()).thenReturn(Arrays.asList(testPortfolio));
        when(portfolioMapper.toDtoList(any())).thenReturn(Arrays.asList(testPortfolioDto));

        List<PortfolioDto> result = portfolioService.getAllPublishedPortfolio();

        assertEquals(1, result.size());
        verify(portfolioRepo).findAllByIsPublishedTrue();
    }

    @Test
    @DisplayName("Should get published portfolio by ID")
    void getPublishedPortfolioById_Success() {
        when(portfolioRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.of(testPortfolio));
        when(portfolioMapper.toDto(testPortfolio)).thenReturn(testPortfolioDto);

        PortfolioDto result = portfolioService.getPublishedPortfolioById(1L);

        assertNotNull(result);
        verify(portfolioRepo).findByIdAndIsPublishedTrue(1L);
    }

    @Test
    @DisplayName("Should add portfolio with createdAt and published=true")
    void addPortfolio_Success() {
        when(portfolioMapper.toEntity(testPortfolioDto)).thenReturn(testPortfolio);
        when(portfolioRepo.save(any())).thenReturn(testPortfolio);
        when(portfolioMapper.toDto(testPortfolio)).thenReturn(testPortfolioDto);

        portfolioService.addPortfolio(testPortfolioDto);

        ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepo).save(captor.capture());
        assertTrue(captor.getValue().getIsPublished());
        assertEquals(LocalDate.now(), captor.getValue().getCreatedAt());
    }

    @Test
    @DisplayName("Should update portfolio fields")
    void updatePortfolio_Success() {
        PortfolioDto updateDto = new PortfolioDto();
        updateDto.setTitle("Updated Title");
        updateDto.setDescription("Updated Description");

        when(portfolioRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.of(testPortfolio));
        when(portfolioRepo.save(any())).thenReturn(testPortfolio);
        when(portfolioMapper.toDto(testPortfolio)).thenReturn(testPortfolioDto);

        portfolioService.updatePortfolio(1L, updateDto);

        ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepo).save(captor.capture());
        assertEquals("Updated Title", captor.getValue().getTitle());
    }

    @Test
    @DisplayName("Should soft delete portfolio")
    void deletePortfolio_Success() {
        when(portfolioRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.of(testPortfolio));

        portfolioService.deletePortfolio(1L);

        ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepo).save(captor.capture());
        assertFalse(captor.getValue().getIsPublished());
    }

    @Test
    @DisplayName("Should restore portfolio")
    void restorePortfolio_Success() {
        testPortfolio.setIsPublished(false);
        when(portfolioRepo.findByIdAndIsPublishedFalse(1L)).thenReturn(Optional.of(testPortfolio));

        portfolioService.restorePortfolio(1L);

        ArgumentCaptor<Portfolio> captor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepo).save(captor.capture());
        assertTrue(captor.getValue().getIsPublished());
    }
}