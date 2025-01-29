package com.ageinghippy.recipeapi.service;

import com.ageinghippy.recipeapi.exception.NoSuchRecipeException;
import com.ageinghippy.recipeapi.exception.NoSuchReviewException;
import com.ageinghippy.recipeapi.model.Recipe;
import com.ageinghippy.recipeapi.model.Review;
import com.ageinghippy.recipeapi.repository.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    RecipeService recipeService;

    @Cacheable(value = "reviews", key = "#id")
    public Review getReviewById(Long id) throws NoSuchReviewException {
        Optional<Review> review = reviewRepo.findById(id);

        if (review.isEmpty()) {
            throw new NoSuchReviewException("The review with ID " + id + " could not be found.");
        }
        return review.get();
    }

    @Cacheable(value = "getReviews", key = "'r-' + #recipeId")
    public List<Review> getReviewByRecipeId(Long recipeId) throws NoSuchRecipeException, NoSuchReviewException {
        Recipe recipe = recipeService.getRecipeById(recipeId);

        List<Review> reviews = recipe.getReviews();

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("There are no reviews for this recipe.");
        }
        return reviews;
    }

    @Cacheable(value = "getReviews", key = "'a-' + #username")
    public List<Review> getReviewByUsername(String username) throws NoSuchReviewException {
        List<Review> reviews = reviewRepo.findByUser_username(username);

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("No reviews could be found for username " + username);
        }
        return reviews;
    }

    @Caching(evict = {
            @CacheEvict(value = "getReviews", key = "'r-' + #recipeId"),
            @CacheEvict(value = "getReviews", key = "'a-' + #review.user.username"),
            @CacheEvict(value = "getRecipes", allEntries = true)},
            put = @CachePut(value = "recipe", key = "#recipeId"))
    public Recipe postNewReview(Review review, Long recipeId) throws NoSuchRecipeException {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        if (recipe.getAuthor().equals(review.getAuthor())) {
            throw new IllegalArgumentException("Oy! You cannot post reviews for your own recipes!!");
        }
        recipe.getReviews().add(review);

        return recipeService.updateRecipe(recipe, false);
    }

    @Caching(evict = {
            @CacheEvict(value = "getReviews", allEntries = true), //We don't have enough info to be selective
            @CacheEvict(value = "getRecipes", allEntries = true), //we don't know which recipe is affected
            @CacheEvict(value = "recipes", allEntries = true)})   //we don't know which recipe is affected
    public Review deleteReviewById(Long id) throws NoSuchReviewException {
        Review review = getReviewById(id);

        if (null == review) {
            throw new NoSuchReviewException("The review you are trying to delete does not exist.");
        }
        reviewRepo.deleteById(id);
        return review;
    }

    @Caching(evict = {
            @CacheEvict(value = "getReviews", allEntries = true),
            @CacheEvict(value = "getRecipes", allEntries = true),
            @CacheEvict(value = "recipes", allEntries = true)},
            put = @CachePut(value = "review", key = "#reviewToUpdate.Id"))
    public Review updateReviewById(Review reviewToUpdate) throws NoSuchReviewException {
        try {
            Review review = getReviewById(reviewToUpdate.getId());
            if (reviewToUpdate.getDescription() != null && !reviewToUpdate.getDescription().isEmpty()) {
                review.setDescription(reviewToUpdate.getDescription());
            }
            if (reviewToUpdate.getRating() != null) {
                review.setRating(reviewToUpdate.getRating());
            }

            reviewToUpdate = reviewRepo.save(review);

            return reviewToUpdate;
        } catch (NoSuchReviewException e) {
            throw new NoSuchReviewException(
                    "The review you are trying to update. " +
                            "Maybe you meant to create one? If not," +
                            "please double-check the ID you passed in.");
        }
    }

}
