package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.NewsDto;
import com.example.websiteforaksoft.Entity.News;
import com.example.websiteforaksoft.Exception.DuplicateResourceException;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.NewsMapper;
import com.example.websiteforaksoft.Repo.NewsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepo newsRepo;
    private final NewsMapper newsMapper;

    public Page<NewsDto> getNewsPaginated(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(sortDirection, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<News> newsPage = newsRepo.findAll(pageable);

        return newsPage.map(newsMapper::toDto);
    }

    public Page<NewsDto> getPublishedNewsPaginated(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(sortDirection, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<News> newsPage = newsRepo.findByIsPublishedTrue(pageable);

        return newsPage.map(newsMapper::toDto);
    }

    public NewsDto getNewsById(Long id) {
        News news = newsRepo.findByIdAndIsPublishedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Новости", "id", id));
        return newsMapper.toDto(news);
    }

    @Transactional
    public NewsDto addNews(NewsDto newsDto) {
        if (newsRepo.existsById(newsDto.getId())) {
            throw new DuplicateResourceException("Новости", "id", newsDto.getId());
        }
        News news = newsMapper.toEntity(newsDto);
        news.setCreatedAt(LocalDate.now());
        news.setIsPublished(true);
        News savedNews = newsRepo.save(news);
        return newsMapper.toDto(savedNews);
    }

    @Transactional
    public NewsDto updateNews(Long id, NewsDto newsDto) {
        News news = newsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Новости", "id", id));
        if (newsDto.getTitle() != null) {
            news.setTitle(newsDto.getTitle());
        }
        if (newsDto.getDescription() != null) {
            news.setDescription(newsDto.getDescription());
        }
        if (newsDto.getContent() != null) {
            news.setContent(newsDto.getContent());
        }
        if (newsDto.getImageUrl() != null) {
            news.setImageUrl(newsDto.getImageUrl());
        }
        news.setUpdatedAt(LocalDate.now());

        News savedNews = newsRepo.save(news);
        return newsMapper.toDto(savedNews);
    }

    @Transactional
    public void deleteNews(Long id) {
        News news = newsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Новости", "id", id));
        if(news.getIsPublished()) {
            news.setIsPublished(false);
        }else throw new RuntimeException("Новости уже деактивированы");

    }

    @Transactional
    public void restoreNews(Long id){
        News news = newsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Новости", "id", id));
        if(!news.getIsPublished()) {
            news.setIsPublished(true);
        }else throw new RuntimeException("Новости уже активированы");
    }
}
