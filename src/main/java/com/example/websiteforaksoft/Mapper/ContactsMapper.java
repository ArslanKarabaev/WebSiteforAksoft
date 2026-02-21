package com.example.websiteforaksoft.Mapper;

import com.example.websiteforaksoft.Dto.ContactsDto;
import com.example.websiteforaksoft.Entity.Contacts;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactsMapper {
    ContactsDto toDto(Contacts contacts);
    List<ContactsDto> toDtoList(List<Contacts> contacts);

    Contacts toEntity(ContactsDto contactsDto);

}
