package com.ageinghippy.recipeapi.repository;

import com.ageinghippy.recipeapi.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe,Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);

}