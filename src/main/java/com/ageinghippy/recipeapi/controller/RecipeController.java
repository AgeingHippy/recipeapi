package com.ageinghippy.recipeapi.controller;

import com.ageinghippy.recipeapi.exception.NoSuchIngredientException;
import com.ageinghippy.recipeapi.exception.NoSuchRecipeException;
import com.ageinghippy.recipeapi.model.Recipe;
import com.ageinghippy.recipeapi.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recipe", description = "Manage recipes")
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    CacheManager cacheManager;

    @PostMapping
    public ResponseEntity<?> createNewRecipe(@Valid @RequestBody Recipe recipe, Authentication authentication) {
        recipe.setUser(recipeService.castToCustomUserDetails((UserDetails) authentication.getPrincipal()));
        Recipe insertedRecipe = recipeService.createNewRecipe(recipe);
        return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable("id") Long id) throws NoSuchRecipeException {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDodgeCache() throws NoSuchRecipeException {
        List<Recipe> recipes = recipeService.getAllDodgeCache();
        return ResponseEntity.ok(recipes);
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
                                           @RequestParam(required = false) String author,
                                           @RequestParam(required = false) Integer maximumDifficultyRating,
                                           @RequestParam(required = false) Integer minimumReviewRating) throws NoSuchRecipeException {
        List<Recipe> recipes = null;
        if (name == null &&
                author == null &&
                maximumDifficultyRating == null &&
                minimumReviewRating == null) {
            //get all - no filters applied
            recipes = recipeService.getAllRecipes();
        } else if (name != null &&
                author == null &&
                maximumDifficultyRating == null &&
                minimumReviewRating == null) {
            //recipes by name only
            recipes = recipeService.getRecipesByName(name);
        } else if (name != null &&
                author == null &&
                maximumDifficultyRating != null &&
                minimumReviewRating == null) {
            //recipes by name and difficulty rating
            recipes = recipeService.getRecipesByNameAndMaximumDifficulty(name, maximumDifficultyRating);
        } else if (name == null &&
                author == null &&
                maximumDifficultyRating == null &&
                minimumReviewRating != null) {
            //by minimum review rating
            recipes = recipeService.getAllRecipesByMinimumReviewRating(minimumReviewRating);
        } else if (name == null &&
                author != null &&
                maximumDifficultyRating == null &&
                minimumReviewRating == null) {
            //by username
            recipes = recipeService.getRecipesByUsername(author);
        } else {
            //handled by ControllerAdvice
            throw new IllegalArgumentException("""
                    Invalid combination of query parameters provided.
                    Legal combinations of these optional parameters are:-
                    1) none - all recipes in the database
                    2) name - all recipes with the recipe name containing the provided name
                    3) name and maximumDifficultyRating - all recipes with the recipe name containing the provided name AND with a maximum difficulty rating
                    4) username - all recipes with the provided username
                    5) minimumReviewRating - all recipes with the minimum average review rating as provided""");
        }
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/review-rating/{minimumReviewRating}")
    public ResponseEntity<?> getRecipesByMinimumReviewRating(@PathVariable int minimumReviewRating) {
        List<Recipe> recipes = recipeService.getAllRecipesByMinimumReviewRating(minimumReviewRating);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> getRecipesByName(@PathVariable("name") String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeService.getRecipesByName(name);
        return ResponseEntity.ok(matchingRecipes);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Recipe', 'delete')")
    public ResponseEntity<?> deleteRecipeById(@PathVariable("id") Long id) throws NoSuchRecipeException {
        Recipe deletedRecipe = recipeService.deleteRecipeById(id);
        return ResponseEntity
                .ok("The recipe with ID " + deletedRecipe.getId() +
                        " and name " + deletedRecipe.getName() +
                        " was deleted.");
    }

    @PatchMapping
    @PreAuthorize("hasPermission(#updatedRecipe.id, 'Recipe', 'edit')")
    public ResponseEntity<?> updateRecipe(@RequestBody Recipe updatedRecipe)
            throws NoSuchIngredientException, NoSuchRecipeException {
        Recipe returnedUpdatedRecipe = recipeService.patchRecipe(updatedRecipe);
        return ResponseEntity.ok(returnedUpdatedRecipe);
    }

}
