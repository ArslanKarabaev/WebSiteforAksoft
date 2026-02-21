package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.ServicesDto;
import com.example.websiteforaksoft.Entity.Services;
import com.example.websiteforaksoft.Exception.DuplicateResourceException;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.ServicesMapper;
import com.example.websiteforaksoft.Repo.ServicesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicesService {
    private final ServicesRepo servicesRepo;
    private final ServicesMapper servicesMapper;

    public List<ServicesDto> getAllServices() {
        List<Services> services = servicesRepo.findAll();
        return servicesMapper.toDtoList(services);
    }

    public List<ServicesDto> getAllPublishedServices() {
        List<Services> services = servicesRepo.findAllByIsPublishedTrue();
        return servicesMapper.toDtoList(services);
    }

    public ServicesDto getPublishedServiceById(Long id) {
        Services services = servicesRepo.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Услуга", "id", id));
        return servicesMapper.toDto(services);
    }

    @Transactional
    public ServicesDto addService(ServicesDto servicesDto) {
        if (servicesRepo.existsById(servicesDto.getId())) {
            throw new DuplicateResourceException("Услуга", "id", servicesDto.getId());
        }
        Services services = servicesMapper.toEntity(servicesDto);
        services.setIsPublished(true);
        Services savedService = servicesRepo.save(services);
        return servicesMapper.toDto(savedService);

    }

    @Transactional
    public ServicesDto updateService(Long id, ServicesDto servicesDto) {
        Services services = servicesRepo.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Услуга", "id", id));
        if (servicesDto.getTitle() != null) {
            services.setTitle(servicesDto.getTitle());
        }
        if (servicesDto.getDescription() != null) {
            services.setDescription(servicesDto.getDescription());
        }
        if (servicesDto.getIconUrl() != null) {
            services.setIconUrl(servicesDto.getIconUrl());
        }
        if (servicesDto.getPrice() != null) {
            services.setPrice(servicesDto.getPrice());
        }
        Services savedService = servicesRepo.save(services);
        return servicesMapper.toDto(savedService);
    }

    @Transactional
    public void deleteService(Long id) {
        Services services = servicesRepo.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Услуга", "id", id));
        if(services.getIsPublished()) {
            services.setIsPublished(false);
            servicesRepo.save(services);
        }else throw new RuntimeException("Услуга уже деактивирована");
    }

    public void restoreService(Long id) {
        Services services = servicesRepo.findByIdAndIsPublishedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Услуга", "id", id));
        if(!services.getIsPublished()) {
            services.setIsPublished(true);
            servicesRepo.save(services);
        }else throw new RuntimeException("Услуга уже активирована");
    }
}
