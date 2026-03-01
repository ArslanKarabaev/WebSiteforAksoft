package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.AuthDto.RegisterUpdateDto;
import com.example.websiteforaksoft.Dto.UserDto;
import com.example.websiteforaksoft.Entity.User;
import com.example.websiteforaksoft.Enum.Role;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.UserMapper;
import com.example.websiteforaksoft.Repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Test")
public class UserServiceTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;


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
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        List<UserDto> userDtos = Arrays.asList(testUserDto);

        when(userRepo.findAll()).thenReturn(users);
        when(userMapper.toDtoList(users)).thenReturn(userDtos);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());

        verify(userRepo, times(1)).findAll();
        verify(userMapper, times(1)).toDtoList(users);
    }

    @Test
    @DisplayName("Should get user by Id successfully")
    void getUserById_Success(){
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@gmail.com", result.getEmail());

        verify(userRepo, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void getUserById_NotFound() {
        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(999L));

        assertTrue(exception.getMessage().contains("id"));
        assertTrue(exception.getMessage().contains("999"));

        verify(userRepo, times(1)).findById(999L);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_Success() {
        RegisterUpdateDto updateDto = new RegisterUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");

        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepo.existsByName(updateDto.getName())).thenReturn(false);
        when(userRepo.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void deactivateUser_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUserById(1L);

        assertFalse(testUser.getIsActive());
        assertNotNull(testUser.getUpdatedAt());
        verify(userRepo, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should change user role successfully")
    void changeRole_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));

        userService.changeRole(1L, Role.ROLE_ADMIN);

        assertEquals(Role.ROLE_ADMIN, testUser.getRole());
        verify(userRepo, times(1)).save(testUser);
    }

}
