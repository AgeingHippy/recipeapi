package com.ageinghippy.recipeapi.repository;

import com.ageinghippy.recipeapi.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends JpaRepository<Review,Long> {

    List<Review> findByUser_username(String username);

}
