package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.NewsDto;
import com.example.websiteforaksoft.Entity.News;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.NewsMapper;
import com.example.websiteforaksoft.Repo.NewsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NewsService Tests")
class NewsServiceTest {

    @Mock
    private NewsRepo newsRepo;

    @Mock
    private NewsMapper newsMapper;

    @InjectMocks
    private NewsService newsService;

    private News testNews;
    private NewsDto testNewsDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testNews = new News();
        testNews.setId(1L);
        testNews.setTitle("Test News Title");
        testNews.setDescription("Test Description");
        testNews.setContent("Test Content");
        testNews.setImageUrl("http://example.com/image.jpg");
        testNews.setIsPublished(true);
        testNews.setCreatedAt(LocalDate.now());

        testNewsDto = new NewsDto();
        testNewsDto.setId(1L);
        testNewsDto.setTitle("Test News Title");
        testNewsDto.setDescription("Test Description");
        testNewsDto.setContent("Test Content");
        testNewsDto.setImageUrl("http://example.com/image.jpg");

        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Test
    @DisplayName("Should get all news paginated with DESC sorting")
    void getNewsPaginated_DescSort_Success() {
        List<News> newsList = Arrays.asList(testNews);
        Page<News> newsPage = new PageImpl<>(newsList, pageable, 1);

        when(newsRepo.findAll(any(Pageable.class))).thenReturn(newsPage);
        when(newsMapper.toDto(testNews)).thenReturn(testNewsDto);

        Page<NewsDto> result = newsService.getNewsPaginated(0, 10, "createdAt", "desc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test News Title", result.getContent().get(0).getTitle());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(newsRepo, times(1)).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(Sort.Direction.DESC, capturedPageable.getSort().getOrderFor("createdAt").getDirection());

        verify(newsMapper, times(1)).toDto(testNews);
    }

    @Test
    @DisplayName("Should get all news paginated with ASC sorting")
    void getNewsPaginated_AscSort_Success() {
        List<News> newsList = Arrays.asList(testNews);
        Pageable ascPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Page<News> newsPage = new PageImpl<>(newsList, ascPageable, 1);

        when(newsRepo.findAll(any(Pageable.class))).thenReturn(newsPage);
        when(newsMapper.toDto(testNews)).thenReturn(testNewsDto);

        Page<NewsDto> result = newsService.getNewsPaginated(0, 10, "title", "asc");

        assertNotNull(result);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(newsRepo, times(1)).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(Sort.Direction.ASC, capturedPageable.getSort().getOrderFor("title").getDirection());
    }

    @Test
    @DisplayName("Should return empty page when no news exist")
    void getNewsPaginated_EmptyPage() {
        Page<News> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        when(newsRepo.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<NewsDto> result = newsService.getNewsPaginated(0, 10, "createdAt", "desc");

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(newsRepo, times(1)).findAll(any(Pageable.class));
        verify(newsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should get only published news paginated")
    void getPublishedNewsPaginated_Success() {
        List<News> publishedNews = Arrays.asList(testNews);
        Page<News> newsPage = new PageImpl<>(publishedNews, pageable, 1);

        when(newsRepo.findByIsPublishedTrue(any(Pageable.class))).thenReturn(newsPage);
        when(newsMapper.toDto(testNews)).thenReturn(testNewsDto);

        Page<NewsDto> result = newsService.getPublishedNewsPaginated(0, 10, "createdAt", "desc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test News Title", result.getContent().get(0).getTitle());

        verify(newsRepo, times(1)).findByIsPublishedTrue(any(Pageable.class));
        verify(newsMapper, times(1)).toDto(testNews);
    }

    @Test
    @DisplayName("Should not include unpublished news")
    void getPublishedNewsPaginated_ExcludeUnpublished() {
        Page<News> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        when(newsRepo.findByIsPublishedTrue(any(Pageable.class))).thenReturn(emptyPage);

        Page<NewsDto> result = newsService.getPublishedNewsPaginated(0, 10, "createdAt", "desc");

        assertEquals(0, result.getTotalElements());
        verify(newsRepo, times(1)).findByIsPublishedTrue(any(Pageable.class));
    }


    @Test
    @DisplayName("Should get published news by ID successfully")
    void getNewsById_Success() {
        when(newsRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.of(testNews));
        when(newsMapper.toDto(testNews)).thenReturn(testNewsDto);

        NewsDto result = newsService.getNewsById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test News Title", result.getTitle());
        assertEquals("Test Description", result.getDescription());

        verify(newsRepo, times(1)).findByIdAndIsPublishedTrue(1L);
        verify(newsMapper, times(1)).toDto(testNews);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when published news not found")
    void getNewsById_NotFound() {
        when(newsRepo.findByIdAndIsPublishedTrue(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> newsService.getNewsById(999L)
        );

        assertTrue(exception.getMessage().contains("Новости"));
        assertTrue(exception.getMessage().contains("id"));
        assertTrue(exception.getMessage().contains("999"));

        verify(newsRepo, times(1)).findByIdAndIsPublishedTrue(999L);
        verify(newsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should throw exception when news exists but is unpublished")
    void getNewsById_UnpublishedNews() {
        when(newsRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> newsService.getNewsById(1L));

        verify(newsRepo, times(1)).findByIdAndIsPublishedTrue(1L);
    }

    @Test
    @DisplayName("Should add news successfully")
    void addNews_Success() {
        NewsDto newNewsDto = new NewsDto();
        newNewsDto.setTitle("New News");
        newNewsDto.setDescription("New Description");
        newNewsDto.setContent("New Content");

        News newNews = new News();
        newNews.setTitle("New News");

        when(newsMapper.toEntity(newNewsDto)).thenReturn(newNews);
        when(newsRepo.save(any(News.class))).thenReturn(testNews);
        when(newsMapper.toDto(testNews)).thenReturn(testNewsDto);

        NewsDto result = newsService.addNews(newNewsDto);

        assertNotNull(result);
        assertEquals("Test News Title", result.getTitle());

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo, times(1)).save(newsCaptor.capture());

        News savedNews = newsCaptor.getValue();
        assertNotNull(savedNews.getCreatedAt());
        assertTrue(savedNews.getIsPublished());
        assertEquals(LocalDate.now(), savedNews.getCreatedAt());

        verify(newsMapper, times(1)).toEntity(newNewsDto);
        verify(newsMapper, times(1)).toDto(testNews);
    }

    @Test
    @DisplayName("Should set createdAt to current date when adding news")
    void addNews_SetsCreatedAt() {
        News newNews = new News();
        when(newsMapper.toEntity(any())).thenReturn(newNews);
        when(newsRepo.save(any())).thenReturn(testNews);
        when(newsMapper.toDto(any())).thenReturn(testNewsDto);

        newsService.addNews(testNewsDto);

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo).save(newsCaptor.capture());

        assertEquals(LocalDate.now(), newsCaptor.getValue().getCreatedAt());
    }

    @Test
    @DisplayName("Should set isPublished to true when adding news")
    void addNews_SetsPublished() {
        News newNews = new News();
        when(newsMapper.toEntity(any())).thenReturn(newNews);
        when(newsRepo.save(any())).thenReturn(testNews);
        when(newsMapper.toDto(any())).thenReturn(testNewsDto);

        newsService.addNews(testNewsDto);

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo).save(newsCaptor.capture());

        assertTrue(newsCaptor.getValue().getIsPublished());
    }

    @Test
    @DisplayName("Should update all fields successfully")
    void updateNews_AllFields_Success() {
        NewsDto updateDto = new NewsDto();
        updateDto.setTitle("Updated Title");
        updateDto.setDescription("Updated Description");
        updateDto.setContent("Updated Content");
        updateDto.setImageUrl("http://example.com/new-image.jpg");

        when(newsRepo.findById(1L)).thenReturn(Optional.of(testNews));
        when(newsRepo.save(any(News.class))).thenReturn(testNews);
        when(newsMapper.toDto(testNews)).thenReturn(testNewsDto);

        NewsDto result = newsService.updateNews(1L, updateDto);

        assertNotNull(result);

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo, times(1)).save(newsCaptor.capture());

        News updatedNews = newsCaptor.getValue();
        assertEquals("Updated Title", updatedNews.getTitle());
        assertEquals("Updated Description", updatedNews.getDescription());
        assertEquals("Updated Content", updatedNews.getContent());
        assertEquals("http://example.com/new-image.jpg", updatedNews.getImageUrl());
        assertNotNull(updatedNews.getUpdatedAt());
        assertEquals(LocalDate.now(), updatedNews.getUpdatedAt());

        verify(newsRepo, times(1)).findById(1L);
        verify(newsMapper, times(1)).toDto(testNews);
    }

    @Test
    @DisplayName("Should update only non-null fields")
    void updateNews_PartialUpdate_Success() {
        NewsDto partialUpdateDto = new NewsDto();
        partialUpdateDto.setTitle("Updated Title Only");

        when(newsRepo.findById(1L)).thenReturn(Optional.of(testNews));
        when(newsRepo.save(any(News.class))).thenReturn(testNews);
        when(newsMapper.toDto(testNews)).thenReturn(testNewsDto);

        NewsDto result = newsService.updateNews(1L, partialUpdateDto);

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo, times(1)).save(newsCaptor.capture());

        News updatedNews = newsCaptor.getValue();
        assertEquals("Updated Title Only", updatedNews.getTitle());

        assertEquals("Test Description", updatedNews.getDescription());
        assertEquals("Test Content", updatedNews.getContent());
        assertEquals("http://example.com/image.jpg", updatedNews.getImageUrl());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent news")
    void updateNews_NotFound() {
        when(newsRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> newsService.updateNews(999L, testNewsDto)
        );

        assertTrue(exception.getMessage().contains("Новости"));
        assertTrue(exception.getMessage().contains("999"));

        verify(newsRepo, times(1)).findById(999L);
        verify(newsRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should set updatedAt when updating news")
    void updateNews_SetsUpdatedAt() {
        when(newsRepo.findById(1L)).thenReturn(Optional.of(testNews));
        when(newsRepo.save(any())).thenReturn(testNews);
        when(newsMapper.toDto(any())).thenReturn(testNewsDto);

        newsService.updateNews(1L, testNewsDto);

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo).save(newsCaptor.capture());

        assertNotNull(newsCaptor.getValue().getUpdatedAt());
        assertEquals(LocalDate.now(), newsCaptor.getValue().getUpdatedAt());
    }

    @Test
    @DisplayName("Should soft delete news by setting isPublished to false")
    void deleteNews_SoftDelete_Success() {
        when(newsRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.of(testNews));
        when(newsRepo.save(any(News.class))).thenReturn(testNews);

        newsService.deleteNews(1L);

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo, times(1)).save(newsCaptor.capture());

        News deletedNews = newsCaptor.getValue();
        assertFalse(deletedNews.getIsPublished());

        verify(newsRepo, times(1)).findByIdAndIsPublishedTrue(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent published news")
    void deleteNews_NotFound() {
        when(newsRepo.findByIdAndIsPublishedTrue(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> newsService.deleteNews(999L)
        );

        assertTrue(exception.getMessage().contains("Новости"));
        assertTrue(exception.getMessage().contains("999"));

        verify(newsRepo, times(1)).findByIdAndIsPublishedTrue(999L);
        verify(newsRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when trying to delete already unpublished news")
    void deleteNews_AlreadyUnpublished() {
        when(newsRepo.findByIdAndIsPublishedTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> newsService.deleteNews(1L));

        verify(newsRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should restore unpublished news by setting isPublished to true")
    void restoreNews_Success() {
        testNews.setIsPublished(false);
        when(newsRepo.findByIdAndIsPublishedFalse(1L)).thenReturn(Optional.of(testNews));
        when(newsRepo.save(any(News.class))).thenReturn(testNews);

        newsService.restoreNews(1L);

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo, times(1)).save(newsCaptor.capture());

        News restoredNews = newsCaptor.getValue();
        assertTrue(restoredNews.getIsPublished());

        verify(newsRepo, times(1)).findByIdAndIsPublishedFalse(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when restoring non-existent unpublished news")
    void restoreNews_NotFound() {
        when(newsRepo.findByIdAndIsPublishedFalse(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> newsService.restoreNews(999L)
        );

        assertTrue(exception.getMessage().contains("Новости"));
        assertTrue(exception.getMessage().contains("999"));

        verify(newsRepo, times(1)).findByIdAndIsPublishedFalse(999L);
        verify(newsRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when trying to restore already published news")
    void restoreNews_AlreadyPublished() {
        when(newsRepo.findByIdAndIsPublishedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> newsService.restoreNews(1L));

        verify(newsRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should change isPublished from false to true when restoring")
    void restoreNews_ChangesPublishedState() {
        testNews.setIsPublished(false);
        when(newsRepo.findByIdAndIsPublishedFalse(1L)).thenReturn(Optional.of(testNews));
        when(newsRepo.save(any())).thenReturn(testNews);

        newsService.restoreNews(1L);

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepo).save(newsCaptor.capture());

        News restoredNews = newsCaptor.getValue();
        assertTrue(restoredNews.getIsPublished());
    }
}