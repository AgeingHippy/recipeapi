package com.ageinghippy.recipeapi.repository;

import com.ageinghippy.recipeapi.model.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo
        extends JpaRepository<CustomUserDetails, Long> {

    CustomUserDetails findByUsername(String username);
}

