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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Test")
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDto testUserDto;
    private LoginDto loginDto;
    private RegisterUpdateDto registerDto;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@gmail.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.ROLE_USER);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDate.now());

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("Test User");
        testUserDto.setEmail("test@gmail.com");
        testUserDto.setRole(Role.ROLE_USER);

        loginDto = new LoginDto();
        loginDto.setEmail("test@gmail.com");
        loginDto.setPassword("password123");

        registerDto = new RegisterUpdateDto();
        registerDto.setName("New User");
        registerDto.setEmail("new@gmail.com");
        registerDto.setPassword("password123");
    }

    @Test
    @DisplayName("Should register new user successfully")
    void register_Success() {
        when(userRepo.existsByName(registerDto.getName())).thenReturn(false);
        when(userRepo.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = authService.register(registerDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());

        verify(userRepo, times(1)).existsByName(registerDto.getName());
        verify(userRepo, times(1)).existsByEmail(registerDto.getEmail());
        verify(passwordEncoder, times(1)).encode(registerDto.getPassword());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when name already exists")
    void register_DuplicateName() {
        when(userRepo.existsByName(registerDto.getName())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(registerDto)
        );

        assertTrue(exception.getMessage().contains("name"));

        verify(userRepo, times(1)).existsByName(registerDto.getName());
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void register_DuplicateEmail() {
        when(userRepo.existsByName(registerDto.getName())).thenReturn(false);
        when(userRepo.existsByEmail(registerDto.getEmail())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(registerDto)
        );

        assertTrue(exception.getMessage().contains("email"));

        verify(userRepo, times(1)).existsByEmail(registerDto.getEmail());
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully and return JWT token")
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(testUser)).thenReturn("jwt.token.here");

        AuthResponseDto result = authService.login(loginDto);

        assertNotNull(result);
        assertEquals("jwt.token.here", result.getToken());
        assertEquals("test@gmail.com", result.getEmail());
        assertEquals(Role.ROLE_USER, result.getRole());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(testUser);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when login fails")
    void login_BadCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginDto)
        );

        assertTrue(exception.getMessage().contains("пароль"));
        verify(jwtService, never()).generateToken(any());
    }
}
