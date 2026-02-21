package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.AuthDto.AuthResponseDto;
import com.example.websiteforaksoft.Dto.AuthDto.LoginDto;
import com.example.websiteforaksoft.Dto.AuthDto.RegisterUpdateDto;
import com.example.websiteforaksoft.Dto.UserDto;
import com.example.websiteforaksoft.Entity.User;
import com.example.websiteforaksoft.Enum.Role;
import com.example.websiteforaksoft.Exception.DuplicateResourceException;
import com.example.websiteforaksoft.Mapper.UserMapper;
import com.example.websiteforaksoft.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @Transactional
    public UserDto register(RegisterUpdateDto registerDto) {
        if (userRepo.existsByName(registerDto.getName())) {
            throw new DuplicateResourceException("Пользователь", "name", registerDto.getName());
        }
        if (userRepo.existsByEmail(registerDto.getEmail())) {
            throw new DuplicateResourceException("Пользователь", "email", registerDto.getEmail());
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setIsActive(true);
        user.setCreatedAt(LocalDate.now());

        User savedUser = userRepo.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public AuthResponseDto login(LoginDto loginDto){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);
            return new AuthResponseDto(token, user.getEmail(), user.getRole());

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Неверный email или пароль");
        }
    }
}
