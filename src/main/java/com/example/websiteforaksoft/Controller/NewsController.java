package com.example.websiteforaksoft.Controller;

import com.example.websiteforaksoft.Dto.NewsDto;
import com.example.websiteforaksoft.Service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<NewsDto>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction)
    {
        Page<NewsDto> newsPage = newsService.getNewsPaginated(page, size, sortBy, direction);
        return ResponseEntity.ok(newsPage);
    }

    @GetMapping("/published")
    public ResponseEntity<Page<NewsDto>> getPublishedNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction)
    {
        Page<NewsDto> newsPage = newsService.getPublishedNewsPaginated(page, size, sortBy, direction);
        return ResponseEntity.ok(newsPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDto> getPublishedNewsById(@PathVariable Long id) {
        NewsDto news = newsService.getNewsById(id);
        return ResponseEntity.ok(news);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<NewsDto> addNews(@Validated(NewsDto.OnCreate.class)
                                           @RequestBody NewsDto newsDto) {
        NewsDto savedNews = newsService.addNews(newsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<NewsDto> updateNews(
            @PathVariable Long id,
            @Validated(NewsDto.OnUpdate.class)
            @RequestBody NewsDto newsDto) {
        NewsDto updatedNews = newsService.updateNews(id, newsDto);
        return ResponseEntity.ok(updatedNews);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
    }

    @PutMapping(path = "/restore-news/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void restoreNews(@PathVariable Long id){
        newsService.restoreNews(id);
    }
}