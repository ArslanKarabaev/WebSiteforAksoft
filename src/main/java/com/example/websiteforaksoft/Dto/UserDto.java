package com.example.websiteforaksoft.Dto;

import com.example.websiteforaksoft.Enum.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Boolean isActive;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}