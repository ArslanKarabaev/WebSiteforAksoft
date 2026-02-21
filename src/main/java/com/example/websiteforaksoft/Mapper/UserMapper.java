package com.example.websiteforaksoft.Mapper;

import com.example.websiteforaksoft.Dto.UserDto;
import com.example.websiteforaksoft.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "password", ignore = true)
    User toEntity(UserDto userDto);
}
