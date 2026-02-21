package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.AboutDto;
import com.example.websiteforaksoft.Entity.About;
import com.example.websiteforaksoft.Mapper.AboutMapper;
import com.example.websiteforaksoft.Repo.AboutRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AboutService {
    private final AboutRepo aboutRepo;
    private final AboutMapper aboutMapper;

    private static final Long ABOUT_ID = 1L;

    public AboutDto getAboutInfo() {
        About about = aboutRepo.findById(ABOUT_ID).orElseThrow(() -> new RuntimeException("Информация 'О нас' не найдена"));
        return aboutMapper.toDto(about);
    }

    @Transactional
    public AboutDto saveOrUpdateAboutInfo(AboutDto aboutDto) {
        Optional<About> existingAbout = aboutRepo.findById(ABOUT_ID);

        About about;
        if (existingAbout.isPresent()) {
            about = existingAbout.get();
            about.setContent(aboutDto.getContent());
            about.setUpdatedAt(LocalDate.now());
        } else {
            about = aboutMapper.toEntity(aboutDto);
            about.setId(ABOUT_ID);
            about.setUpdatedAt(LocalDate.now());
        }

        About savedAbout = aboutRepo.save(about);
        return aboutMapper.toDto(savedAbout);
    }

    @Transactional
    public void deleteAboutInfo() {
        boolean exists = aboutRepo.existsById(ABOUT_ID);
        if (!exists) {
            throw new RuntimeException("Информация 'О нас' не существует");
        }
        aboutRepo.deleteById(ABOUT_ID);
    }
}