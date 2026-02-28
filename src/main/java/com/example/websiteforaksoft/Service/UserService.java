package com.example.websiteforaksoft.Service;


import com.example.websiteforaksoft.Dto.AuthDto.RegisterUpdateDto;
import com.example.websiteforaksoft.Dto.UserDto;
import com.example.websiteforaksoft.Entity.User;
import com.example.websiteforaksoft.Enum.Role;
import com.example.websiteforaksoft.Exception.DuplicateResourceException;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.UserMapper;
import com.example.websiteforaksoft.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с именем " + email + " не найден"));
    }

    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(userRepo.findAll());
    }

    public UserDto getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, RegisterUpdateDto updateDto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));
        if (updateDto.getName() != null) {
            if (userRepo.existsByName(updateDto.getName())) {
                throw new DuplicateResourceException("Пользователь", "name", updateDto.getName());
            }
            user.setName(updateDto.getName());
        }
        if (updateDto.getEmail() != null) {
            if (userRepo.existsByEmail(updateDto.getEmail())) {
                throw new DuplicateResourceException("Пользователь", "email", updateDto.getEmail());
            }
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        user.setUpdatedAt(LocalDate.now());
        User savedUser = userRepo.save(user);
        return userMapper.toDto(savedUser);
    }


    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));
        if (user.getIsActive()) {
            user.setIsActive(false);
            user.setUpdatedAt(LocalDate.now());
            userRepo.save(user);
        } else throw new RuntimeException("Пользователь уже деактивирован");
    }

    @Transactional
    public void restoreUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));
        if (!user.getIsActive()) {
            user.setIsActive(true);
            user.setUpdatedAt(LocalDate.now());
            userRepo.save(user);
        } else throw new RuntimeException("Пользователь уже активирован");
    }

    @Transactional
    public void changeRole(Long id, Role role) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));
        user.setRole(role);
        user.setUpdatedAt(LocalDate.now());
        userRepo.save(user);
    }
}
