package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.MainBannerDto;
import com.example.websiteforaksoft.Entity.MainBanner;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.MainBannerMapper;
import com.example.websiteforaksoft.Repo.MainBannerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MainBannerService Tests")
class MainBannerServiceTest {

    @Mock
    private MainBannerRepo mainBannerRepo;
    @Mock
    private MainBannerMapper mainBannerMapper;
    @InjectMocks
    private MainBannerService mainBannerService;

    private MainBanner testBanner;
    private MainBannerDto testBannerDto;

    @BeforeEach
    void setUp() {
        testBanner = new MainBanner();
        testBanner.setId(1L);
        testBanner.setTitle("Welcome");
        testBanner.setSubtitle("Best IT Company");
        testBanner.setImageUrl("http://example.com/banner.jpg");
        testBanner.setButtonText("Learn More");
        testBanner.setButtonLink("/about");
        testBanner.setIsActive(true);

        testBannerDto = new MainBannerDto();
        testBannerDto.setId(1L);
        testBannerDto.setTitle("Welcome");
    }

    @Test
    @DisplayName("Should get all banners")
    void getMainBanners_Success() {
        when(mainBannerRepo.findAll()).thenReturn(Arrays.asList(testBanner));
        when(mainBannerMapper.toDtoList(any())).thenReturn(Arrays.asList(testBannerDto));

        List<MainBannerDto> result = mainBannerService.getMainBanners();

        assertEquals(1, result.size());
        verify(mainBannerRepo).findAll();
    }

    @Test
    @DisplayName("Should get active banner by ID")
    void getActiveMainBannerById_Success() {
        when(mainBannerRepo.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testBanner));
        when(mainBannerMapper.toDto(testBanner)).thenReturn(testBannerDto);

        MainBannerDto result = mainBannerService.getActiveMainBannerById(1L);

        assertNotNull(result);
        verify(mainBannerRepo).findByIdAndIsActiveTrue(1L);
    }

    @Test
    @DisplayName("Should throw exception when active banner not found")
    void getActiveMainBannerById_NotFound() {
        when(mainBannerRepo.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> mainBannerService.getActiveMainBannerById(999L));
    }

    @Test
    @DisplayName("Should add banner")
    void addMainBanner_Success() {
        when(mainBannerMapper.toEntity(testBannerDto)).thenReturn(testBanner);
        when(mainBannerRepo.save(any())).thenReturn(testBanner);
        when(mainBannerMapper.toDto(testBanner)).thenReturn(testBannerDto);

        MainBannerDto result = mainBannerService.addMainBanner(testBannerDto);

        assertNotNull(result);
        verify(mainBannerRepo).save(any());
    }

    @Test
    @DisplayName("Should update all banner fields")
    void updateMainBanner_Success() {
        MainBannerDto updateDto = new MainBannerDto();
        updateDto.setTitle("New Title");
        updateDto.setSubtitle("New Subtitle");
        updateDto.setImageUrl("new.jpg");
        updateDto.setButtonText("Click");
        updateDto.setButtonLink("/contact");

        when(mainBannerRepo.findById(1L)).thenReturn(Optional.of(testBanner));
        when(mainBannerRepo.save(any())).thenReturn(testBanner);
        when(mainBannerMapper.toDto(testBanner)).thenReturn(testBannerDto);

        mainBannerService.updateMainBanner(1L, updateDto);

        ArgumentCaptor<MainBanner> captor = ArgumentCaptor.forClass(MainBanner.class);
        verify(mainBannerRepo).save(captor.capture());
        assertEquals("New Title", captor.getValue().getTitle());
        assertEquals("New Subtitle", captor.getValue().getSubtitle());
    }

    @Test
    @DisplayName("Should soft delete banner")
    void deleteMainBanner_Success() {
        when(mainBannerRepo.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testBanner));
        when(mainBannerRepo.save(any())).thenReturn(testBanner);

        mainBannerService.deleteMainBanner(1L);

        ArgumentCaptor<MainBanner> captor = ArgumentCaptor.forClass(MainBanner.class);
        verify(mainBannerRepo).save(captor.capture());
        assertFalse(captor.getValue().getIsActive());
    }

    @Test
    @DisplayName("Should restore banner")
    void restoreMainBanner_Success() {
        testBanner.setIsActive(false);
        when(mainBannerRepo.findByIdAndIsActiveFalse(1L)).thenReturn(Optional.of(testBanner));
        when(mainBannerRepo.save(any())).thenReturn(testBanner);

        mainBannerService.restoreMainBanner(1L);

        ArgumentCaptor<MainBanner> captor = ArgumentCaptor.forClass(MainBanner.class);
        verify(mainBannerRepo).save(captor.capture());
        assertTrue(captor.getValue().getIsActive());
    }
}