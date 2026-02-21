package com.example.websiteforaksoft.Mapper;

import com.example.websiteforaksoft.Dto.MainBannerDto;
import com.example.websiteforaksoft.Entity.MainBanner;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MainBannerMapper {
    MainBannerDto toDto(MainBanner mainBanner);

    MainBanner toEntity(MainBannerDto mainBannerDto);

    List<MainBannerDto> toDtoList(List<MainBanner> mainBanners);
}
