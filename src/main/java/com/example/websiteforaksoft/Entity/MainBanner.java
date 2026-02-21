package com.example.websiteforaksoft.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "main banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainBanner {
    @Id
    private Long id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String buttonText;
    private String buttonLink;
    private Boolean isActive;
}
