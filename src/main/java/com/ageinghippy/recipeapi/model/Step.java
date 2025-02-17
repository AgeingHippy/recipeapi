package com.ageinghippy.recipeapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class Step implements Serializable {

    @Serial
    private static final long serialVersionUID = 4465479183904380338L;

    @Id
    @GeneratedValue
    private Long id; //todo add unique key on id,stepNumber ?

    @NotNull
    @Min(1)
    private Integer stepNumber;

    @NotEmpty
    private String description;
}