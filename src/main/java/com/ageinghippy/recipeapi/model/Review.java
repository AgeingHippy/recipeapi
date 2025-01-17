package com.ageinghippy.recipeapi.model;

import com.ageinghippy.recipeapi.validator.RequiredAndNotGoofy;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String username;

    @NotNull
    @Min(value = 1, message = "must be between 1 and 10")
    @Max(value = 10, message = "must be between 1 and 10")
    private Integer rating;

    @RequiredAndNotGoofy
    private String description;

//    public void setRating(int rating) {
//        if (rating <= 0 || rating > 10) {
//            throw new IllegalArgumentException("Rating must be between 0 and 10.");
//        }
//        this.rating = rating;
//    }
}

