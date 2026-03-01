package com.example.websiteforaksoft.Controller;

import com.example.websiteforaksoft.Dto.PortfolioDto;
import com.example.websiteforaksoft.Service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PortfolioDto>> getAllPortfolio(){
        List<PortfolioDto> portfolioDto = portfolioService.getAllPortfolio();
        return ResponseEntity.ok(portfolioDto);
    }

    @GetMapping(path = "/published")
    public ResponseEntity<List<PortfolioDto>> getAllPublishedPortfolio(){
        List<PortfolioDto> portfolioDto = portfolioService.getAllPublishedPortfolio();
        return ResponseEntity.ok(portfolioDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PortfolioDto> getPublishedPortfolioById(@PathVariable Long id){
        PortfolioDto portfolioDto = portfolioService.getPublishedPortfolioById(id);
        return ResponseEntity.ok(portfolioDto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PortfolioDto> addPortfolio(@Validated(PortfolioDto.OnCreate.class)
                                                     @RequestBody PortfolioDto portfolioDto){
        PortfolioDto savedPortfolio = portfolioService.addPortfolio(portfolioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPortfolio);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PortfolioDto> updatePortfolio(
            @PathVariable Long id,
            @Validated(PortfolioDto.onUpdate.class)
            @RequestBody PortfolioDto portfolioDto){
        PortfolioDto updatedPortfolio = portfolioService.updatePortfolio(id, portfolioDto);
        return ResponseEntity.ok(updatedPortfolio);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void deletePortfolio(@PathVariable Long id){
        portfolioService.deletePortfolio(id);
    }

    @PutMapping(path = "/restore-portfolio/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void restorePortfolio(@PathVariable Long id){
        portfolioService.restorePortfolio(id);
    }

}
