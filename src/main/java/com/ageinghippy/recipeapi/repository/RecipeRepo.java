package com.ageinghippy.recipeapi.repository;

import com.ageinghippy.recipeapi.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);

    @Query(value = """
            SELECT	rc.*
            FROM	recipe rc
            JOIN 	review rv
            ON		rc.id = rv.recipe_id
            GROUP BY rc.id
            HAVING sum(rv.rating) / count(rv.id) >= ?
            """,
            nativeQuery = true)
    List<Recipe> findAllWithMinimumReviewRating(int minimumReviewRating);

    List<Recipe> findByDifficultyRatingLessThanEqualAndNameContaining(int maximumDifficultyRating, String name);
}
