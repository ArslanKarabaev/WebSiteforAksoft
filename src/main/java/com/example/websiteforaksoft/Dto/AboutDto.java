package com.example.websiteforaksoft.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
@Data
public class AboutDto {
    private Long id;

    @NotBlank(message = "Описание не может быть пустым")
    private String content;

    private LocalDate updatedAt;
}
