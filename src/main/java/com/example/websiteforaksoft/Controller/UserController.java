package com.example.websiteforaksoft.Controller;

import com.example.websiteforaksoft.Dto.AuthDto.RegisterUpdateDto;
import com.example.websiteforaksoft.Dto.UserDto;
import com.example.websiteforaksoft.Enum.Role;
import com.example.websiteforaksoft.Service.UserService;
import jakarta.servlet.ServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers(ServletRequest servletRequest) {
        List<UserDto> userDto = userService.getAllUsers();
        return ResponseEntity.ok(userDto);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Validated(RegisterUpdateDto.OnUpdate.class)
            @RequestBody RegisterUpdateDto updateDto) {
        UserDto updatedUser = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @PutMapping(path = "restoreUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
    }

    @PutMapping(path = "/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void changeRole(@PathVariable Long id, @RequestParam Role role) {
        userService.changeRole(id, role);
    }
}

