package com.ageinghippy.recipeapi.controller;

import com.ageinghippy.recipeapi.exception.NoSuchRecipeException;
import com.ageinghippy.recipeapi.exception.NoSuchReviewException;
import com.ageinghippy.recipeapi.model.CustomUserDetails;
import com.ageinghippy.recipeapi.model.Recipe;
import com.ageinghippy.recipeapi.model.Review;
import com.ageinghippy.recipeapi.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {


    @Autowired
    ReviewService reviewService;

    @Autowired
    CacheManager cacheManager;

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) throws NoSuchReviewException {
        Review retrievedReview = reviewService.getReviewById(id);
        return ResponseEntity.ok(retrievedReview);
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<?> getReviewByRecipeId(@PathVariable("recipeId") Long recipeId) throws NoSuchReviewException, NoSuchRecipeException {
        List<Review> reviews = reviewService.getReviewByRecipeId(recipeId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getReviewByUsername(@PathVariable("username") String username) throws NoSuchReviewException {
        List<Review> reviews = reviewService.getReviewByUsername(username);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{recipeId}")
    public ResponseEntity<?> postNewReview(@Valid @RequestBody Review review,
                                           @PathVariable("recipeId") Long recipeId,
                                           Authentication authentication) throws NoSuchRecipeException {
        review.setUser((CustomUserDetails) authentication.getPrincipal());
        Recipe insertedRecipe = reviewService.postNewReview(review, recipeId);
        return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Review', 'delete')")
    public ResponseEntity<?> deleteReviewById(@PathVariable("id") Long id) throws NoSuchReviewException {
        Review review = reviewService.deleteReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PatchMapping
    @PreAuthorize("hasPermission(#reviewToUpdate.id, 'Review', 'edit')")
    public ResponseEntity<?> updateReviewById(@RequestBody Review reviewToUpdate) throws NoSuchReviewException {
        Review review = reviewService.updateReviewById(reviewToUpdate);
        return ResponseEntity.ok(review);
    }


}
