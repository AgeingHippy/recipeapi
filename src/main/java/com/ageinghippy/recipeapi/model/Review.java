package com.ageinghippy.recipeapi.model;

import com.ageinghippy.recipeapi.validator.RequiredAndNotGoofy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review implements Serializable {

    @Serial
    private static final long serialVersionUID = 910795327277529911L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private CustomUserDetails user;

    @NotNull
    @Min(value = 1, message = "must be between 1 and 10")
    @Max(value = 10, message = "must be between 1 and 10")
    private Integer rating;

    @RequiredAndNotGoofy
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getAuthor() {
        String userName = null;
        if (this.user != null) {
            userName = this.user.getUsername();
        }
        return userName;
    }

//    public void setRating(int rating) {
//        if (rating <= 0 || rating > 10) {
//            throw new IllegalArgumentException("Rating must be between 0 and 10.");
//        }
//        this.rating = rating;
//    }
}

