package com.example.websiteforaksoft.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contacts {
    @Id
    private Long id;
    private String phone;
    private String email;
    private String address;
    private String googleMapUrl;
    private LocalDate updatedAt;

}
