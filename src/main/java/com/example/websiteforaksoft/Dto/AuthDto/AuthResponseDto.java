package com.example.websiteforaksoft.Dto.AuthDto;

import com.example.websiteforaksoft.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String email;
    private Role role;
}
