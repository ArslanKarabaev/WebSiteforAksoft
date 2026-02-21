package com.example.websiteforaksoft.Mapper;

import com.example.websiteforaksoft.Dto.AboutDto;
import com.example.websiteforaksoft.Entity.About;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AboutMapper {
    AboutDto toDto(About about);
    About toEntity(AboutDto aboutDto);
}
