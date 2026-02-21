package com.example.websiteforaksoft.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ServicesDto {
    private Long id;

    @NotBlank(message = "Заголовок не может быть пустым", groups = OnCreate.class)
    private String title;

    @NotBlank(message = "Краткое описание не может быть пустым", groups = OnCreate.class)
    private String description;

    @NotBlank(message = "Иконка не может быть пустой", groups = OnCreate.class)
    private String iconUrl;

    @Positive(groups = {OnCreate.class, OnUpdate.class})
    private Long price;

    private Boolean isPublished;

    public interface OnCreate{}
    public interface OnUpdate{}
}
