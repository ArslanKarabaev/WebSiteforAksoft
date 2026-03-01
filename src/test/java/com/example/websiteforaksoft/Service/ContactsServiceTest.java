package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Dto.ContactsDto;
import com.example.websiteforaksoft.Entity.Contacts;
import com.example.websiteforaksoft.Exception.ResourceNotFoundException;
import com.example.websiteforaksoft.Mapper.ContactsMapper;
import com.example.websiteforaksoft.Repo.ContactsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContactsService Tests")
class ContactsServiceTest {

    @Mock
    private ContactsRepo contactsRepo;

    @Mock
    private ContactsMapper contactsMapper;

    @InjectMocks
    private ContactsService contactsService;

    private Contacts testContacts;
    private ContactsDto testContactsDto;

    @BeforeEach
    void setUp() {
        testContacts = new Contacts();
        testContacts.setId(1L);
        testContacts.setPhone("+996 555 123 456");
        testContacts.setEmail("info@gmail.com");
        testContacts.setAddress("Bishkek, Kyrgyzstan");
        testContacts.setGoogleMapUrl("https://maps.google.com/");
        testContacts.setUpdatedAt(LocalDate.now());

        testContactsDto = new ContactsDto();
        testContactsDto.setId(1L);
        testContactsDto.setPhone("+996 555 123 456");
        testContactsDto.setEmail("info@gmail.com");
        testContactsDto.setAddress("Bishkek, Kyrgyzstan");
        testContactsDto.setGoogleMapUrl("https://maps.google.com/");
    }

    @Test
    @DisplayName("Should get all contacts successfully")
    void getAllContacts_Success() {
        List<Contacts> contactsList = Arrays.asList(testContacts);
        List<ContactsDto> contactsDtoList = Arrays.asList(testContactsDto);

        when(contactsRepo.findAll()).thenReturn(contactsList);
        when(contactsMapper.toDtoList(contactsList)).thenReturn(contactsDtoList);

        List<ContactsDto> result = contactsService.getAllContacts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("+996 555 123 456", result.get(0).getPhone());

        verify(contactsRepo, times(1)).findAll();
        verify(contactsMapper, times(1)).toDtoList(contactsList);
    }

    @Test
    @DisplayName("Should return empty list when no contacts exist")
    void getAllContacts_EmptyList() {
        when(contactsRepo.findAll()).thenReturn(Arrays.asList());
        when(contactsMapper.toDtoList(any())).thenReturn(Arrays.asList());

        List<ContactsDto> result = contactsService.getAllContacts();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(contactsRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get contacts by ID successfully")
    void getContactsById_Success() {
        when(contactsRepo.findById(1L)).thenReturn(Optional.of(testContacts));
        when(contactsMapper.toDto(testContacts)).thenReturn(testContactsDto);

        ContactsDto result = contactsService.getContactsById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("+996 555 123 456", result.getPhone());
        assertEquals("info@gmail.com", result.getEmail());

        verify(contactsRepo, times(1)).findById(1L);
        verify(contactsMapper, times(1)).toDto(testContacts);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when contacts not found")
    void getContactsById_NotFound() {
        when(contactsRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> contactsService.getContactsById(999L)
        );

        assertTrue(exception.getMessage().contains("Контакты"));
        assertTrue(exception.getMessage().contains("999"));

        verify(contactsRepo, times(1)).findById(999L);
        verify(contactsMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should add contacts successfully")
    void addContacts_Success() {
        ContactsDto newContactsDto = new ContactsDto();
        newContactsDto.setPhone("+996 777 999 888");
        newContactsDto.setEmail("contact@gmail.com");

        Contacts newContacts = new Contacts();
        newContacts.setPhone("+996 777 999 888");

        when(contactsMapper.toEntity(newContactsDto)).thenReturn(newContacts);
        when(contactsRepo.save(any(Contacts.class))).thenReturn(testContacts);
        when(contactsMapper.toDto(testContacts)).thenReturn(testContactsDto);

        ContactsDto result = contactsService.addContacts(newContactsDto);

        assertNotNull(result);

        ArgumentCaptor<Contacts> contactsCaptor = ArgumentCaptor.forClass(Contacts.class);
        verify(contactsRepo, times(1)).save(contactsCaptor.capture());

        Contacts savedContacts = contactsCaptor.getValue();
        assertNotNull(savedContacts.getUpdatedAt());
        assertEquals(LocalDate.now(), savedContacts.getUpdatedAt());

        verify(contactsMapper, times(1)).toEntity(newContactsDto);
        verify(contactsMapper, times(1)).toDto(testContacts);
    }

    @Test
    @DisplayName("Should set updatedAt when adding contacts")
    void addContacts_SetsUpdatedAt() {
        Contacts newContacts = new Contacts();
        when(contactsMapper.toEntity(any())).thenReturn(newContacts);
        when(contactsRepo.save(any())).thenReturn(testContacts);
        when(contactsMapper.toDto(any())).thenReturn(testContactsDto);

        contactsService.addContacts(testContactsDto);

        ArgumentCaptor<Contacts> contactsCaptor = ArgumentCaptor.forClass(Contacts.class);
        verify(contactsRepo).save(contactsCaptor.capture());

        assertEquals(LocalDate.now(), contactsCaptor.getValue().getUpdatedAt());
    }

    @Test
    @DisplayName("Should update all contact fields successfully")
    void updateContacts_AllFields_Success() {
        ContactsDto updateDto = new ContactsDto();
        updateDto.setPhone("+996 999 888 777");
        updateDto.setEmail("updated@gmail.com");
        updateDto.setAddress("Updated address");
        updateDto.setGoogleMapUrl("https://maps.google.com/updated");

        when(contactsRepo.findById(1L)).thenReturn(Optional.of(testContacts));
        when(contactsRepo.save(any(Contacts.class))).thenReturn(testContacts);
        when(contactsMapper.toDto(testContacts)).thenReturn(testContactsDto);

        ContactsDto result = contactsService.updateContacts(1L, updateDto);

        assertNotNull(result);

        ArgumentCaptor<Contacts> contactsCaptor = ArgumentCaptor.forClass(Contacts.class);
        verify(contactsRepo, times(1)).save(contactsCaptor.capture());

        Contacts updatedContacts = contactsCaptor.getValue();
        assertEquals("+996 999 888 777", updatedContacts.getPhone());
        assertEquals("updated@gmail.com", updatedContacts.getEmail());
        assertEquals("Updated address", updatedContacts.getAddress());
        assertEquals("https://maps.google.com/updated", updatedContacts.getGoogleMapUrl());
        assertEquals(LocalDate.now(), updatedContacts.getUpdatedAt());

        verify(contactsRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update only non-null fields")
    void updateContacts_PartialUpdate_Success() {
        ContactsDto partialUpdateDto = new ContactsDto();
        partialUpdateDto.setPhone("+996 111 222 333");

        when(contactsRepo.findById(1L)).thenReturn(Optional.of(testContacts));
        when(contactsRepo.save(any(Contacts.class))).thenReturn(testContacts);
        when(contactsMapper.toDto(testContacts)).thenReturn(testContactsDto);

        ContactsDto result = contactsService.updateContacts(1L, partialUpdateDto);

        ArgumentCaptor<Contacts> contactsCaptor = ArgumentCaptor.forClass(Contacts.class);
        verify(contactsRepo, times(1)).save(contactsCaptor.capture());

        Contacts updatedContacts = contactsCaptor.getValue();
        assertEquals("+996 111 222 333", updatedContacts.getPhone());
        assertEquals("info@gmail.com", updatedContacts.getEmail());
        assertEquals("Bishkek, Kyrgyzstan", updatedContacts.getAddress());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent contacts")
    void updateContacts_NotFound() {
        when(contactsRepo.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> contactsService.updateContacts(999L, testContactsDto)
        );

        assertTrue(exception.getMessage().contains("Контакты"));

        verify(contactsRepo, times(1)).findById(999L);
        verify(contactsRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should set updatedAt when updating contacts")
    void updateContacts_SetsUpdatedAt() {
        when(contactsRepo.findById(1L)).thenReturn(Optional.of(testContacts));
        when(contactsRepo.save(any())).thenReturn(testContacts);
        when(contactsMapper.toDto(any())).thenReturn(testContactsDto);

        contactsService.updateContacts(1L, testContactsDto);

        ArgumentCaptor<Contacts> contactsCaptor = ArgumentCaptor.forClass(Contacts.class);
        verify(contactsRepo).save(contactsCaptor.capture());

        assertEquals(LocalDate.now(), contactsCaptor.getValue().getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete contacts successfully")
    void deleteContacts_Success() {
        when(contactsRepo.existsById(1L)).thenReturn(true);
        doNothing().when(contactsRepo).deleteById(1L);

        contactsService.deleteContacts(1L);

        verify(contactsRepo, times(1)).existsById(1L);
        verify(contactsRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent contacts")
    void deleteContacts_NotFound() {
        when(contactsRepo.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> contactsService.deleteContacts(999L)
        );

        assertTrue(exception.getMessage().contains("Контакты"));
        assertTrue(exception.getMessage().contains("999"));

        verify(contactsRepo, times(1)).existsById(999L);
        verify(contactsRepo, never()).deleteById(any());
    }
}