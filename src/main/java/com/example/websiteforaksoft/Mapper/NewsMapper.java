package com.example.websiteforaksoft.Mapper;

import com.example.websiteforaksoft.Dto.NewsDto;
import com.example.websiteforaksoft.Entity.News;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    NewsDto toDto(News news);
    News toEntity(NewsDto newsDto);
}
