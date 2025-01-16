package com.ageinghippy.recipeapi.controller;

import com.ageinghippy.recipeapi.exception.NoSuchIngredientException;
import com.ageinghippy.recipeapi.exception.NoSuchRecipeException;
import com.ageinghippy.recipeapi.model.Recipe;
import com.ageinghippy.recipeapi.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<?> createNewRecipe(@RequestBody Recipe recipe) {
        try {
            Recipe insertedRecipe = recipeService.createNewRecipe(recipe);
            return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (IllegalStateException e) {
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

    @GetMapping
    public ResponseEntity<?> getAllRecipes() {
        try {
            return ResponseEntity.ok(recipeService.getAllRecipes());
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
        } catch (IllegalStateException e) {
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
        } catch (NoSuchRecipeException | IllegalStateException | NoSuchIngredientException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
