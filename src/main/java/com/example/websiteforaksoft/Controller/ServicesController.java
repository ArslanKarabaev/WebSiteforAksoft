package com.example.websiteforaksoft.Controller;

import com.example.websiteforaksoft.Dto.ServicesDto;
import com.example.websiteforaksoft.Service.ServicesService;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/services")
@RequiredArgsConstructor
public class ServicesController {
    private final ServicesService servicesService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ServicesDto>> getAllServices() {
        List<ServicesDto> servicesDto = servicesService.getAllServices();
        return ResponseEntity.ok(servicesDto);
    }

    @GetMapping(path = "/published")
    public ResponseEntity<List<ServicesDto>> getAllPublishedServices() {
        List<ServicesDto> servicesDto = servicesService.getAllPublishedServices();
        return ResponseEntity.ok(servicesDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ServicesDto> getPublishedServiceById(@PathVariable Long id) {
        ServicesDto servicesDto = servicesService.getPublishedServiceById(id);
        return ResponseEntity.ok(servicesDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicesDto> addService(@Validated(ServicesDto.OnCreate.class)
                                                  @RequestBody ServicesDto servicesDto) {
        ServicesDto savedServices = servicesService.addService(servicesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedServices);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicesDto> updateService(
            @PathVariable Long id,
            @Validated(ServicesDto.OnUpdate.class)
            @RequestBody ServicesDto servicesDto
            ) {
        ServicesDto updatedService = servicesService.updateService(id, servicesDto);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteService(@PathVariable Long id) {
        servicesService.deleteService(id);
    }

    @PutMapping(path = "restoreService/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void restoreService(@PathVariable Long id) {
        servicesService.restoreService(id);
    }
}
