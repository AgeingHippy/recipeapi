package com.ageinghippy.recipeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer minutesToMake;

    @Column(nullable = false)
    private Integer difficultyRating;

    @Transient
    private Integer reviewRating;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<Ingredient> ingredients; // = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<Step> steps; // = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<Review> reviews;

    @Transient
    @JsonIgnore
    private URI locationURI;

    public void setDifficultyRating(int difficultyRating) {
        if (difficultyRating < 0 || difficultyRating > 10) {
            throw new IllegalStateException(
                    "Difficulty rating must be between 0 and 10.");
        }
        this.difficultyRating = difficultyRating;
    }

    public void validate() throws IllegalStateException {
        if (ingredients.size() == 0) {
            throw new IllegalStateException(
                    "You need at least one ingredient for your recipe!");
        } else if (steps.size() == 0) {
            throw new IllegalStateException(
                    "You need at least one step for your recipe!");
        }
    }

    public void generateLocationURI() {
        try {
            locationURI = new URI(
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/recipes/")
                            .path(String.valueOf(id))
                            .toUriString());
        } catch (URISyntaxException e) {
            // exception should stop here.
        }
    }

    @PostLoad
    public void calculateReviewRating() {
        Integer reviewAverage = null;
        if (!getReviews().isEmpty()) {
            reviewAverage = getReviews().stream().map(Review::getRating).reduce(0, Integer::sum) / getReviews().size();
        }
        this.reviewRating = reviewAverage;
    }
}
