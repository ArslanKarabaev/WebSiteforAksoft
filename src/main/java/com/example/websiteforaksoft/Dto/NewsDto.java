package com.example.websiteforaksoft.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewsDto {
    private Long id;

    @NotBlank(message = "Заголовок не может быть пустым", groups = OnCreate.class)
    private String title;

    @NotBlank(message = "Краткое описание не может быть пустым", groups = OnCreate.class)
    private String description;

    @NotBlank(message = "Описание не может быть пустым", groups = OnCreate.class)
    private String content;

    @NotBlank(message = "Фото не может быть пустым", groups = OnCreate.class)
    private String imageUrl;

    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Boolean isPublished;

    public interface OnCreate {}

    public interface OnUpdate {}
}
