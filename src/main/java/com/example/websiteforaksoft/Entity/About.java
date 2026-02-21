package com.example.websiteforaksoft.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.boot.models.annotations.internal.LobJpaAnnotation;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@Table(name = "about")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class About {
    @Id
    private Long id;
    private String content;
    private LocalDate updatedAt;
}
