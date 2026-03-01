package com.example.websiteforaksoft.Controller;

import com.example.websiteforaksoft.Dto.ContactsDto;
import com.example.websiteforaksoft.Service.ContactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactsController {

    private final ContactsService contactsService;

    @GetMapping
    public ResponseEntity<List<ContactsDto>> getAllContacts() {
        List<ContactsDto> contacts = contactsService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactsDto> getContactsById(@PathVariable("id") Long id) {
        ContactsDto contacts = contactsService.getContactsById(id);
        return ResponseEntity.ok(contacts);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ContactsDto> addContacts(@Validated(ContactsDto.OnCreate.class)
                                                       @RequestBody ContactsDto contactsDto) {
        ContactsDto savedContacts = contactsService.addContacts(contactsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContacts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ContactsDto> updateContacts(
            @PathVariable Long id,
            @Validated(ContactsDto.OnUpdate.class)
            @RequestBody ContactsDto contactsDto) {
        ContactsDto updatedContacts = contactsService.updateContacts(id, contactsDto);
        return ResponseEntity.ok(updatedContacts);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void deleteContacts(@PathVariable Long id) {
        contactsService.deleteContacts(id);
    }

}