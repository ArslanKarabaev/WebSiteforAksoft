package com.example.websiteforaksoft.Repo;

import com.example.websiteforaksoft.Entity.About;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutRepo extends JpaRepository<About,Long> {

}
