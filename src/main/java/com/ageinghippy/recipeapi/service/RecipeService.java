package com.ageinghippy.recipeapi.service;

import com.ageinghippy.recipeapi.exception.NoSuchRecipeException;
import com.ageinghippy.recipeapi.exception.NoSuchIngredientException;
import com.ageinghippy.recipeapi.model.Ingredient;
import com.ageinghippy.recipeapi.model.Recipe;
import com.ageinghippy.recipeapi.model.Step;
import com.ageinghippy.recipeapi.repository.RecipeRepo;
import com.ageinghippy.recipeapi.utils.Utils;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    EntityManager entityManager;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe) throws IllegalArgumentException {
        if (recipe.getId() != null) {
            throw new IllegalArgumentException("ID cannot be specified for a new recipe");
        }
        return saveRecipe(recipe);
    }

    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);

        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe with ID " + id + " could be found.");
        }

        Recipe recipe = recipeOptional.get();
        recipe.generateLocationURI();
        return recipe;
    }

    public List<Recipe> getRecipesByName(String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCase(name);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name.");
        }

        return matchingRecipes;
    }

    public List<Recipe> getRecipesByUsername(String username) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByUser_username(username);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that username.");
        }

        return matchingRecipes;
    }

    public List<Recipe> getRecipesByNameAndMaximumDifficulty(String name, int maximumDifficultyRating) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(name,maximumDifficultyRating);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name and given maximum difficulty rating.");
        }

        return matchingRecipes;
    }

    public List<Recipe> getAllRecipes() throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findAll();

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes yet :( feel free to add one though");
        }

        return recipes;
    }

    public List<Recipe> getAllRecipesByMinimumReviewRating(int minimumReviewRating) {
        if (minimumReviewRating < 0 || minimumReviewRating > 10) {
            throw new IllegalArgumentException("Minimum rating must be within the range of 0-10");
        }
        return recipeRepo.findAllWithMinimumReviewRating(minimumReviewRating);
    }

    @Transactional
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(e.getMessage() + " Could not delete.");
        }
    }

    @Transactional
    public Recipe patchRecipe(Recipe recipe) throws NoSuchRecipeException, NoSuchIngredientException {
        Recipe patchRecipe = getRecipeById(recipe.getId());

        //update non null elements from recipe
        patchRecipe.setName(Utils.nvl(recipe.getName(), patchRecipe.getName()));
        patchRecipe.setMinutesToMake(Utils.nvl(recipe.getMinutesToMake(), patchRecipe.getMinutesToMake()));
        patchRecipe.setDifficultyRating(Utils.nvl(recipe.getDifficultyRating(), patchRecipe.getDifficultyRating()));

        //add or patch ingredients as relevant
        recipe.getIngredients().forEach(
                ingredient -> {
                    if (ingredient.getId() == null || ingredient.getId() == 0) {
                        patchRecipe.getIngredients().add(ingredient);
                    } else {
                        Ingredient existingIngredient = patchRecipe.getIngredients().stream()
                                .filter(o -> o.getId().equals(ingredient.getId())).findFirst().orElse(null);
                        if (existingIngredient != null) {
                            //patch the ingredient
                            existingIngredient.setName(Utils.nvl(ingredient.getName(), existingIngredient.getName()));
                            existingIngredient.setAmount(Utils.nvl(ingredient.getAmount(), existingIngredient.getAmount()));
                            existingIngredient.setState(Utils.nvl(ingredient.getState(), existingIngredient.getState()));
                        } else {
                            //id provided but does not exist in the database
                            //todo - ignore for now. Convert to method or add signature to functional interface
                            //throw new NoSuchIngredientException("Patched ingredient with id "+ ingredient.getId()+" does not exist in the database.");
                        }
                    }
                }
        );

        //patch steps
        recipe.getSteps().forEach(
                step -> {
                    if (step.getId() == null || step.getId() == 0) {
                        patchRecipe.getSteps().add(step);
                    } else {
                        Step existingStep = patchRecipe.getSteps().stream()
                                .filter(o -> o.getId().equals(step.getId())).findFirst().orElse(null);
                        if (existingStep != null) {
                            //patch the step
                            existingStep.setStepNumber(Utils.nvl(step.getStepNumber(), existingStep.getStepNumber()));
                            existingStep.setDescription(Utils.nvl(step.getDescription(), existingStep.getDescription()));
                        } else {
                            //id provided but does not exist in the database
                            //todo - ignore for now. Convert to method or add signature to functional interface
                            //throw new NoSuchStepException("Patched step with id "+ step.getId()+" does not exist in the database.");
                        }
                    }
                }
        );

        //todo patch reviews

        //return
        return updateRecipe(patchRecipe, false);
    }

    @Transactional
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) { //using forceCheck as a PATCH indicator
                getRecipeById(recipe.getId());
            }
            return saveRecipe(recipe);
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(
                    "The recipe you passed in did not have an ID found " +
                            "in the database. Double check that it is correct. " +
                            "Or maybe you meant to POST a recipe not PATCH one.");
        }
    }

    private Recipe saveRecipe(Recipe recipe) {
        recipeRepo.saveAndFlush(recipe);

        //force a refresh from the database to ensure all reviews are loaded and average rating calculated
        entityManager.refresh(recipe);

        Recipe savedRecipe = recipeRepo.findById(recipe.getId()).get();
        savedRecipe.generateLocationURI();

        return savedRecipe;
    }
}
