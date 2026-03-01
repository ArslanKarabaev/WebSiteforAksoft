package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.AboutDto;
import com.example.websiteforaksoft.Entity.About;
import com.example.websiteforaksoft.Mapper.AboutMapper;
import com.example.websiteforaksoft.Repo.AboutRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AboutService Tests")
class AboutServiceTest {

    @Mock
    private AboutRepo aboutRepo;

    @Mock
    private AboutMapper aboutMapper;

    @InjectMocks
    private AboutService aboutService;

    private About testAbout;
    private AboutDto testAboutDto;
    private static final Long ABOUT_ID = 1L;

    @BeforeEach
    void setUp() {
        testAbout = new About();
        testAbout.setId(ABOUT_ID);
        testAbout.setContent("Test content about company");
        testAbout.setUpdatedAt(LocalDate.now());

        testAboutDto = new AboutDto();
        testAboutDto.setId(ABOUT_ID);
        testAboutDto.setContent("Test content about company");
    }

    @Test
    @DisplayName("Should get about info successfully")
    void getAboutInfo_Success() {
        when(aboutRepo.findById(ABOUT_ID)).thenReturn(Optional.of(testAbout));
        when(aboutMapper.toDto(testAbout)).thenReturn(testAboutDto);

        AboutDto result = aboutService.getAboutInfo();

        assertNotNull(result);
        assertEquals("Test content about company", result.getContent());

        verify(aboutRepo, times(1)).findById(ABOUT_ID);
        verify(aboutMapper, times(1)).toDto(testAbout);
    }

    @Test
    @DisplayName("Should throw RuntimeException when about info not found")
    void getAboutInfo_NotFound() {
        when(aboutRepo.findById(ABOUT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> aboutService.getAboutInfo()
        );

        assertTrue(exception.getMessage().contains("Информация 'О нас' не найдена"));

        verify(aboutRepo, times(1)).findById(ABOUT_ID);
        verify(aboutMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should update existing about info")
    void saveOrUpdateAboutInfo_Update_Success() {
        AboutDto updateDto = new AboutDto();
        updateDto.setContent("Updated content");

        when(aboutRepo.findById(ABOUT_ID)).thenReturn(Optional.of(testAbout));
        when(aboutRepo.save(any(About.class))).thenReturn(testAbout);
        when(aboutMapper.toDto(testAbout)).thenReturn(testAboutDto);

        AboutDto result = aboutService.saveOrUpdateAboutInfo(updateDto);

        assertNotNull(result);

        ArgumentCaptor<About> aboutCaptor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepo, times(1)).save(aboutCaptor.capture());

        About savedAbout = aboutCaptor.getValue();
        assertEquals("Updated content", savedAbout.getContent());
        assertEquals(LocalDate.now(), savedAbout.getUpdatedAt());

        verify(aboutRepo, times(1)).findById(ABOUT_ID);
        verify(aboutMapper, times(1)).toDto(testAbout);
        verify(aboutMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("Should create new about info when not exists")
    void saveOrUpdateAboutInfo_Create_Success() {
        AboutDto newAboutDto = new AboutDto();
        newAboutDto.setContent("New content");

        About newAbout = new About();
        newAbout.setContent("New content");

        when(aboutRepo.findById(ABOUT_ID)).thenReturn(Optional.empty());
        when(aboutMapper.toEntity(newAboutDto)).thenReturn(newAbout);
        when(aboutRepo.save(any(About.class))).thenReturn(testAbout);
        when(aboutMapper.toDto(testAbout)).thenReturn(testAboutDto);

        AboutDto result = aboutService.saveOrUpdateAboutInfo(newAboutDto);

        assertNotNull(result);

        ArgumentCaptor<About> aboutCaptor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepo, times(1)).save(aboutCaptor.capture());

        About savedAbout = aboutCaptor.getValue();
        assertEquals(ABOUT_ID, savedAbout.getId());
        assertNotNull(savedAbout.getUpdatedAt());
        assertEquals(LocalDate.now(), savedAbout.getUpdatedAt());

        verify(aboutRepo, times(1)).findById(ABOUT_ID);
        verify(aboutMapper, times(1)).toEntity(newAboutDto);
        verify(aboutMapper, times(1)).toDto(testAbout);
    }

    @Test
    @DisplayName("Should set updatedAt to current date when updating")
    void saveOrUpdateAboutInfo_SetsUpdatedAt() {
        when(aboutRepo.findById(ABOUT_ID)).thenReturn(Optional.of(testAbout));
        when(aboutRepo.save(any())).thenReturn(testAbout);
        when(aboutMapper.toDto(any())).thenReturn(testAboutDto);

        aboutService.saveOrUpdateAboutInfo(testAboutDto);

        ArgumentCaptor<About> aboutCaptor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepo).save(aboutCaptor.capture());

        assertEquals(LocalDate.now(), aboutCaptor.getValue().getUpdatedAt());
    }

    @Test
    @DisplayName("Should set ID to 1 when creating new about info")
    void saveOrUpdateAboutInfo_Create_SetsIdTo1() {
        About newAbout = new About();
        when(aboutRepo.findById(ABOUT_ID)).thenReturn(Optional.empty());
        when(aboutMapper.toEntity(any())).thenReturn(newAbout);
        when(aboutRepo.save(any())).thenReturn(testAbout);
        when(aboutMapper.toDto(any())).thenReturn(testAboutDto);

        aboutService.saveOrUpdateAboutInfo(testAboutDto);

        ArgumentCaptor<About> aboutCaptor = ArgumentCaptor.forClass(About.class);
        verify(aboutRepo).save(aboutCaptor.capture());

        assertEquals(ABOUT_ID, aboutCaptor.getValue().getId());
    }

    @Test
    @DisplayName("Should delete about info successfully")
    void deleteAboutInfo_Success() {
        when(aboutRepo.existsById(ABOUT_ID)).thenReturn(true);
        doNothing().when(aboutRepo).deleteById(ABOUT_ID);

        aboutService.deleteAboutInfo();

        verify(aboutRepo, times(1)).existsById(ABOUT_ID);
        verify(aboutRepo, times(1)).deleteById(ABOUT_ID);
    }

    @Test
    @DisplayName("Should throw RuntimeException when deleting non-existent about info")
    void deleteAboutInfo_NotFound() {
        when(aboutRepo.existsById(ABOUT_ID)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> aboutService.deleteAboutInfo()
        );

        assertTrue(exception.getMessage().contains("Информация 'О нас' не существует"));

        verify(aboutRepo, times(1)).existsById(ABOUT_ID);
        verify(aboutRepo, never()).deleteById(any());
    }
}