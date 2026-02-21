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
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    private Long id;
    private String title;
    private String description;
    private String content;
    private String imageUrl;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Boolean isPublished;

}
