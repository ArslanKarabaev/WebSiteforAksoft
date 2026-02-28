package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.MainBannerDto;
import com.example.websiteforaksoft.Entity.MainBanner;
import com.example.websiteforaksoft.Exception.DuplicateResourceException;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.MainBannerMapper;
import com.example.websiteforaksoft.Repo.MainBannerRepo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainBannerService {
    private final MainBannerRepo mainBannerRepo;
    private final MainBannerMapper mainBannerMapper;

    public List<MainBannerDto> getMainBanners() {
        List<MainBanner> mainBanners = mainBannerRepo.findAll();
        return mainBannerMapper.toDtoList(mainBanners);
    }

    public MainBannerDto getActiveMainBannerById(Long id) {
        MainBanner mainBanner = mainBannerRepo.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Баннер", "id", id));
        return mainBannerMapper.toDto(mainBanner);
    }

    @Transactional
    public MainBannerDto addMainBanner(MainBannerDto mainBannerDto) {

        MainBanner savedBanner = mainBannerRepo.save(mainBannerMapper.toEntity(mainBannerDto));
        return mainBannerMapper.toDto(savedBanner);
    }

    @Transactional
    public MainBannerDto updateMainBanner(Long id, MainBannerDto mainBannerDto) {
        MainBanner mainBanner = mainBannerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Баннер", "id", id));

        if (mainBannerDto.getTitle() != null) {
            mainBanner.setTitle(mainBannerDto.getTitle());
        }
        if(mainBannerDto.getSubtitle() != null){
            mainBanner.setSubtitle(mainBannerDto.getSubtitle());
        }
        if(mainBannerDto.getImageUrl() != null){
            mainBanner.setImageUrl(mainBannerDto.getImageUrl());
        }
        if(mainBannerDto.getButtonText() != null){
            mainBanner.setButtonText(mainBannerDto.getButtonText());
        }
        if(mainBannerDto.getButtonLink() != null){
            mainBanner.setButtonLink(mainBannerDto.getButtonLink());
        }

        MainBanner savedBanner = mainBannerRepo.save(mainBanner);
        return mainBannerMapper.toDto(savedBanner);
    }

    @Transactional
    public void deleteMainBanner(Long id){
        MainBanner mainBanner = mainBannerRepo.findByIdAndIsActiveTrue(id)
                .orElseThrow(()-> new ResourceNotFoundException("Баннер", "id", id));
            mainBanner.setIsActive(false);
            mainBannerRepo.save(mainBanner);
    }

    @Transactional
    public void restoreMainBanner(Long id){
        MainBanner mainBanner = mainBannerRepo.findByIdAndIsActiveFalse(id)
                .orElseThrow(()-> new ResourceNotFoundException("Баннер", "id", id));
            mainBanner.setIsActive(true);
            mainBannerRepo.save(mainBanner);
    }
}
