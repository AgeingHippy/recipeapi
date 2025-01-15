package com.ageinghippy.recipeapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Step {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private int stepNumber;

    @NotNull
    private String description;
}