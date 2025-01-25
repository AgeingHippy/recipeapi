package com.ageinghippy.recipeapi.model;

import com.ageinghippy.recipeapi.validator.ListSize;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer minutesToMake;

    @NotNull
    @Min(0)
    @Max(10)
    @Column(nullable = false)
    private Integer difficultyRating;

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private CustomUserDetails user;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getAuthor() {
        String userName = null;
        if (this.user != null) {
            userName = this.user.getUsername();
        }
        return userName;
    }

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer reviewRating;

    @ListSize(minSize = 1)
    @Valid
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<Ingredient> ingredients = new ArrayList<>();

    @ListSize(minSize = 1)
    @Valid
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    private List<Step> steps = new ArrayList<>();

    @Valid
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Review> reviews;

    @Transient
    @JsonIgnore
    private URI locationURI;

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
