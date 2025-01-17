package com.ageinghippy.recipeapi.controller;

import com.ageinghippy.recipeapi.exception.NoSuchIngredientException;
import com.ageinghippy.recipeapi.exception.NoSuchRecipeException;
import com.ageinghippy.recipeapi.model.Recipe;
import com.ageinghippy.recipeapi.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recipes", description = "Manage recipes")
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @PostMapping
    public ResponseEntity<?> createNewRecipe(@Valid @RequestBody Recipe recipe) {
        try {
            Recipe insertedRecipe = recipeService.createNewRecipe(recipe);
            return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable("id") Long id) {
        try {
            Recipe recipe = recipeService.getRecipeById(id);
            return ResponseEntity.ok(recipe);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @Operation(summary = "Get all recipes matching a set of optional query parameters.",
            description = """
                    Legal combinations of these optional parameters are:-
                    1) none - all recipes in the database
                    2) name - all recipes with the recipe name containing the provided name
                    3) name and maximumDifficultyRating - all recipes with the recipe name containing the provided name AND with a maximum difficulty rating
                    4) username - all recipes with the provided username
                    5) minimumReviewRating - all recipes with the minimum average review rating as provided
                                
                    Note: Provision of an illegal combination will result in a 400 Bad Request
                    """)
    @GetMapping
    public ResponseEntity<?> getAllRecipes(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) Integer maximumDifficultyRating,
                                           @RequestParam(required = false) Integer minimumReviewRating) {
        List<Recipe> recipes = null;
        try {
            if (name == null &&
                    username == null &&
                    maximumDifficultyRating == null &&
                    minimumReviewRating == null) {
                //get all - no filters applied
                recipes = recipeService.getAllRecipes();
            } else if (name != null &&
                    maximumDifficultyRating == null &&
                    minimumReviewRating == null) {
                //recipes by name only
                recipes = recipeService.getRecipesByName(name);
            } else if (name != null &&
                    username == null &&
                    maximumDifficultyRating != null &&
                    minimumReviewRating == null) {
                //recipes by name and difficulty rating
                recipes = recipeService.getRecipesByNameAndMaximumDifficulty(name, maximumDifficultyRating);
            } else if (name == null &&
                    username == null &&
                    maximumDifficultyRating == null &&
                    minimumReviewRating != null) {
                //by minimum review rating
                recipes = recipeService.getAllRecipesByMinimumReviewRating(minimumReviewRating);
            } else if (name == null &&
                    username != null &&
                    maximumDifficultyRating == null &&
                    minimumReviewRating == null) {
                //by username
                recipes = recipeService.getRecipesByUsername(username);
            } else {
                return ResponseEntity.badRequest().body("""
                        Invalid combination of query parameters provided.
                        Legal combinations of these optional parameters are:-
                        1) none - all recipes in the database
                        2) name - all recipes with the recipe name containing the provided name
                        3) name and maximumDifficultyRating - all recipes with the recipe name containing the provided name AND with a maximum difficulty rating
                        4) username - all recipes with the provided username
                        5) minimumReviewRating - all recipes with the minimum average review rating as provided""");
            }
            return ResponseEntity.ok(recipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/review-rating/{minimumReviewRating}")
    public ResponseEntity<?> getRecipesByMinimumReviewRating(@PathVariable int minimumReviewRating) {
        try {
            List<Recipe> recipes = recipeService.getAllRecipesByMinimumReviewRating(minimumReviewRating);
            return ResponseEntity.ok(recipes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> getRecipesByName(@PathVariable("name") String name) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByName(name);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipeById(@PathVariable("id") Long id) {
        try {
            Recipe deletedRecipe = recipeService.deleteRecipeById(id);
            return ResponseEntity
                    .ok("The recipe with ID " + deletedRecipe.getId() +
                            " and name " + deletedRecipe.getName() +
                            " was deleted.");
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateRecipe(@RequestBody Recipe updatedRecipe) {
        try {
            Recipe returnedUpdatedRecipe = recipeService.patchRecipe(updatedRecipe);
            return ResponseEntity.ok(returnedUpdatedRecipe);
        } catch (NoSuchRecipeException | IllegalArgumentException | NoSuchIngredientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
