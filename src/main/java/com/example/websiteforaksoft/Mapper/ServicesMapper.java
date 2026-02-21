package com.example.websiteforaksoft.Mapper;

import com.example.websiteforaksoft.Dto.ServicesDto;
import com.example.websiteforaksoft.Entity.Services;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServicesMapper {
    ServicesDto toDto(Services services);
    Services toEntity(ServicesDto servicesDto);

    List<ServicesDto> toDtoList(List<Services> services);
}
