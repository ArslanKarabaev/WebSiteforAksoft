package com.example.websiteforaksoft.Controller;

import com.example.websiteforaksoft.Dto.AboutDto;
import com.example.websiteforaksoft.Service.AboutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/AboutUs")
public class AboutController {
    private final AboutService aboutService;

    @GetMapping
    public ResponseEntity<AboutDto> getAboutInfo() {
        return ResponseEntity.ok(aboutService.getAboutInfo());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AboutDto> saveOrUpdateAboutInfo(@Valid @RequestBody AboutDto aboutDto) {
        AboutDto savedAbout = aboutService.saveOrUpdateAboutInfo(aboutDto);
        return ResponseEntity.ok(savedAbout);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAboutInfo() {
        aboutService.deleteAboutInfo();
    }

}
