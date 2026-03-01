package com.example.websiteforaksoft.Controller;

import com.example.websiteforaksoft.Dto.MainBannerDto;
import com.example.websiteforaksoft.Service.MainBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/main-banner")
@RequiredArgsConstructor
public class MainBannerController {
    private final MainBannerService mainBannerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MainBannerDto>> getAllMainBanners() {
        List<MainBannerDto> mainBanners = mainBannerService.getMainBanners();
        return ResponseEntity.ok(mainBanners);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<MainBannerDto> getActiveMainBannerById(@PathVariable("id") Long id) {
        MainBannerDto mainBannerDto = mainBannerService.getActiveMainBannerById(id);
        return ResponseEntity.ok(mainBannerDto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MainBannerDto> addMainBanner(@Validated(MainBannerDto.OnCreate.class)
                                                       @RequestBody MainBannerDto mainBannerDto) {
        MainBannerDto savedBanner = mainBannerService.addMainBanner(mainBannerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBanner);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MainBannerDto> updateMainBanner(
            @PathVariable Long id,
            @Validated(MainBannerDto.OnUpdate.class)
            @RequestBody MainBannerDto mainBannerDto) {
        MainBannerDto updatedMainBanner = mainBannerService.updateMainBanner(id, mainBannerDto);
        return ResponseEntity.ok(updatedMainBanner);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void deleteMainBanner(@PathVariable Long id){
        mainBannerService.deleteMainBanner(id);
    }

    @PutMapping(path = "/restore-main-banner/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void restoreMainBanner(@PathVariable Long id){
        mainBannerService.restoreMainBanner(id);
    }

}
