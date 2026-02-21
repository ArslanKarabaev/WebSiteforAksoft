package com.example.websiteforaksoft.Repo;

import com.example.websiteforaksoft.Entity.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactsRepo extends JpaRepository<Contacts, Long> {
}
