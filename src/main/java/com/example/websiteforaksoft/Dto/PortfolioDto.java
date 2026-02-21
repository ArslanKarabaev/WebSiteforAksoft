package com.example.websiteforaksoft.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PortfolioDto {
    private Long id;

    @NotBlank(message = "Заголовок не может быть пустым", groups = OnCreate.class)
    private String title;

    @NotBlank(message = "Краткое описание не может быть пустым", groups = OnCreate.class)
    private String description;

    @NotBlank(message = "Фото не может быть пустым", groups = OnCreate.class)
    private String imageUrl;

    private String projectUrl;
    private LocalDate createdAt;
    private Boolean isPublished;

    public interface OnCreate{}
    public interface onUpdate{}
}
