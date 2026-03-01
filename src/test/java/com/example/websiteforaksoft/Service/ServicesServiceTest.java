package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.ServicesDto;
import com.example.websiteforaksoft.Entity.Services;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.ServicesMapper;
import com.example.websiteforaksoft.Repo.ServicesRepo;
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
@DisplayName("ServicesService Tests")
class ServicesServiceTest {

    @Mock
    private ServicesRepo servicesRepo;
    @Mock
    private ServicesMapper servicesMapper;
    @InjectMocks
    private ServicesService servicesService;

    private Services testService;
    private ServicesDto testServiceDto;

    @BeforeEach
    void setUp() {
        testService = new Services();
        testService.setId(1L);
        testService.setTitle("Web Development");
        testService.setDescription("Custom websites");
        testService.setIconUrl("web-icon.svg");
        testService.setPrice(5000L);
        testService.setIsPublished(true);

        testServiceDto = new ServicesDto();
        testServiceDto.setId(1L);
        testServiceDto.setTitle("Web Development");
    }

    @Test
    @DisplayName("Should get all services")
    void getAllServices_Success() {
        when(servicesRepo.findAll()).thenReturn(Arrays.asList(testService));
        when(servicesMapper.toDtoList(any())).thenReturn(Arrays.asList(testServiceDto));

        List<ServicesDto> result = servicesService.getAllServices();

        assertEquals(1, result.size());
        verify(servicesRepo).findAll();
    }

    @Test
    @DisplayName("Should get only published services")
    void getAllPublishedServices_Success() {
        when(servicesRepo.findAllByIsPublishedTrue()).thenReturn(Arrays.asList(testService));
        when(servicesMapper.toDtoList(any())).thenReturn(Arrays.asList(testServiceDto));

        List<ServicesDto> result = servicesService.getAllPublishedServices();

        assertEquals(1, result.size());
        verify(servicesRepo).findAllByIsPublishedTrue();
    }

    @Test
    @DisplayName("Should get published service by ID")
    void getPublishedServiceById_Success() {
        when(servicesRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.of(testService));
        when(servicesMapper.toDto(testService)).thenReturn(testServiceDto);

        ServicesDto result = servicesService.getPublishedServiceById(1L);

        assertNotNull(result);
        verify(servicesRepo).findByIdAndIsPublishedTrue(1L);
    }

    @Test
    @DisplayName("Should add service with published=true")
    void addService_Success() {
        when(servicesMapper.toEntity(testServiceDto)).thenReturn(testService);
        when(servicesRepo.save(any())).thenReturn(testService);
        when(servicesMapper.toDto(testService)).thenReturn(testServiceDto);

        servicesService.addService(testServiceDto);

        ArgumentCaptor<Services> captor = ArgumentCaptor.forClass(Services.class);
        verify(servicesRepo).save(captor.capture());
        assertTrue(captor.getValue().getIsPublished());
    }

    @Test
    @DisplayName("Should update service fields including price")
    void updateService_Success() {
        ServicesDto updateDto = new ServicesDto();
        updateDto.setTitle("Updated Service");
        updateDto.setPrice(7500L);

        when(servicesRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.of(testService));
        when(servicesRepo.save(any())).thenReturn(testService);
        when(servicesMapper.toDto(testService)).thenReturn(testServiceDto);

        servicesService.updateService(1L, updateDto);

        ArgumentCaptor<Services> captor = ArgumentCaptor.forClass(Services.class);
        verify(servicesRepo).save(captor.capture());
        assertEquals("Updated Service", captor.getValue().getTitle());
        assertEquals(7500L, captor.getValue().getPrice());
    }

    @Test
    @DisplayName("Should soft delete service")
    void deleteService_Success() {
        when(servicesRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.of(testService));

        servicesService.deleteService(1L);

        ArgumentCaptor<Services> captor = ArgumentCaptor.forClass(Services.class);
        verify(servicesRepo).save(captor.capture());
        assertFalse(captor.getValue().getIsPublished());
    }

    @Test
    @DisplayName("Should restore service")
    void restoreService_Success() {
        testService.setIsPublished(false);
        when(servicesRepo.findByIdAndIsPublishedFalse(1L)).thenReturn(Optional.of(testService));

        servicesService.restoreService(1L);

        ArgumentCaptor<Services> captor = ArgumentCaptor.forClass(Services.class);
        verify(servicesRepo).save(captor.capture());
        assertTrue(captor.getValue().getIsPublished());
    }

    @Test
    @DisplayName("Should throw exception when service not found")
    void getPublishedServiceById_NotFound() {
        when(servicesRepo.findByIdAndIsPublishedTrue(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> servicesService.getPublishedServiceById(999L));
    }
}