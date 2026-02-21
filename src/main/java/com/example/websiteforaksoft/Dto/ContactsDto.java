package com.example.websiteforaksoft.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContactsDto {
    private Long id;

    @Pattern(
            regexp = "^(996|0)\\d{9}$",
            message = "Номер телефона должен быть в формате 996XXXXXXXXX или 0XXXXXXXXX",
            groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "Телефон не может быть пустым", groups = OnCreate.class)
    private String phone;

    @Email(message = "Некорректный формат email", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "Email обязателен для заполнения", groups = OnCreate.class)
    private String email;

    @NotBlank(message = "Адрес не может быть пустым", groups = OnCreate.class)
    private String address;

    @NotBlank(message = "Ссылка на GoogleMap не может быть пустой",groups = OnCreate.class)
    private String googleMapUrl;

    private LocalDate updatedAt;

    public interface OnCreate {}
    public interface OnUpdate {}
}
