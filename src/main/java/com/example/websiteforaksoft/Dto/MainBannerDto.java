package com.example.websiteforaksoft.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MainBannerDto {
    private Long id;

    @NotBlank(message = "Заголовок не может быть пустым", groups = OnCreate.class)
    private String title;

    @NotBlank(message = "Подзаголовок не может быть пустым", groups = OnCreate.class)
    private String subtitle;

    @NotBlank(message = "Фото не может быть пустым", groups = OnCreate.class)
    private String imageUrl;

    @NotBlank(message = "Текст кнопки не может быть пустым", groups = OnCreate.class)
    private String buttonText;

    @NotBlank(message = "Ссылка кнопки не может быть пустой", groups = OnCreate.class)
    private String buttonLink;

    private Boolean isActive = true;

    public interface OnCreate {}
    public interface OnUpdate {}
}
