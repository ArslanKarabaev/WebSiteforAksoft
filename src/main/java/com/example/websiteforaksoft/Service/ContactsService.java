package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.ContactsDto;
import com.example.websiteforaksoft.Entity.Contacts;
import com.example.websiteforaksoft.Exception.DuplicateResourceException;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.ContactsMapper;
import com.example.websiteforaksoft.Repo.ContactsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactsService {
    private final ContactsRepo contactsRepo;
    private final ContactsMapper contactsMapper;

    public List<ContactsDto> getAllContacts() {
        List<Contacts> contacts = contactsRepo.findAll();
        return contactsMapper.toDtoList(contacts);
    }

    public ContactsDto getContactsById(Long id) {
        Contacts contacts = contactsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Контакты", "id", id));
        return contactsMapper.toDto(contacts);
    }

    @Transactional
    public ContactsDto addContacts(ContactsDto contactsDto) {

        Contacts contacts = contactsMapper.toEntity(contactsDto);
        contacts.setUpdatedAt(LocalDate.now());
        Contacts savedContacts = contactsRepo.save(contacts);
        return contactsMapper.toDto(savedContacts);
    }

    @Transactional
    public ContactsDto updateContacts(Long id, ContactsDto contactsDto) {
        Contacts contacts = contactsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Контакты", "id", id));

        if (contactsDto.getPhone() != null) {
            contacts.setPhone(contactsDto.getPhone());
        }
        if (contactsDto.getEmail() != null) {
            contacts.setEmail(contactsDto.getEmail());
        }
        if (contactsDto.getAddress() != null) {
            contacts.setAddress(contactsDto.getAddress());
        }
        if (contactsDto.getGoogleMapUrl() != null) {
            contacts.setGoogleMapUrl(contactsDto.getGoogleMapUrl());
        }
        contacts.setUpdatedAt(LocalDate.now());

        Contacts savedContacts = contactsRepo.save(contacts);
        return contactsMapper.toDto(savedContacts);
    }

    @Transactional
    public void deleteContacts(Long id) {
        if (!contactsRepo.existsById(id)) {
            throw new ResourceNotFoundException("Контакты", "id", id);
        }
        contactsRepo.deleteById(id);
    }

}