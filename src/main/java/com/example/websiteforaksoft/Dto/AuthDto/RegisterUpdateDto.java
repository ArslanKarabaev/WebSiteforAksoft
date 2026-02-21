package com.example.websiteforaksoft.Dto.AuthDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUpdateDto {
    @NotBlank(message = "Имя пользователя не может быть пустым", groups = OnRegister.class)
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов", groups = {OnRegister.class, OnUpdate.class})
    private String name;

    @NotBlank(message = "Email не может быть пустым", groups = OnRegister.class)
    @Email(message = "Некорректный формат email", groups = {OnRegister.class, OnUpdate.class})
    private String email;

    @NotBlank(message = "Пароль не может быть пустым", groups = OnRegister.class)
    @Size(min = 6, message = "Пароль должен быть не менее 6 символов", groups = {OnRegister.class, OnUpdate.class})
    private String password;

    public interface OnRegister{}
    public interface OnUpdate{}
}
