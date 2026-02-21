package com.example.websiteforaksoft.Mapper;

import com.example.websiteforaksoft.Dto.PortfolioDto;
import com.example.websiteforaksoft.Entity.Portfolio;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    PortfolioDto toDto (Portfolio portfolio);
    List<PortfolioDto> toDtoList(List<Portfolio> portfolios);
    Portfolio toEntity (PortfolioDto portfolioDto);


}
