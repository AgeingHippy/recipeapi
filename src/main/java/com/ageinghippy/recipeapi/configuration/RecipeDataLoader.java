package com.ageinghippy.recipeapi.configuration;

import com.ageinghippy.recipeapi.model.*;
import com.ageinghippy.recipeapi.repository.RecipeRepo;
import com.ageinghippy.recipeapi.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("test")
public class RecipeDataLoader implements CommandLineRunner {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("STARTING WITH TEST DATABASE SETUP");
        if (recipeRepo.findAll().isEmpty()) {

            //users

            CustomUserDetails userIdfk = CustomUserDetails.builder()
                    .username("idfk")
                    .password(passwordEncoder.encode("password"))
                    .authorities(List.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .userMeta(UserMeta.builder().name("idfk").email("idfk@email.com").build())
                    .build();

            CustomUserDetails userBob = CustomUserDetails.builder()
                    .username("bob")
                    .password(passwordEncoder.encode("password"))
                    .authorities(List.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .userMeta(UserMeta.builder().name("bob").email("bob@email.com").build())
                    .build();

            CustomUserDetails userSally = CustomUserDetails.builder()
                    .username("sally")
                    .password(passwordEncoder.encode("password"))
                    .authorities(List.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .userMeta(UserMeta.builder().name("sally").email("sally@email.com").build())
                    .build();

            CustomUserDetails userMark = CustomUserDetails.builder()
                    .username("mark")
                    .password(passwordEncoder.encode("password"))
                    .authorities(List.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .userMeta(UserMeta.builder().name("mark").email("mark@email.com").build())
                    .build();

            CustomUserDetails userBen = CustomUserDetails.builder()
                    .username("ben")
                    .password(passwordEncoder.encode("password"))
                    .authorities(List.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .userMeta(UserMeta.builder().name("ben").email("ben@email.com").build())
                    .build();

            CustomUserDetails userBill = CustomUserDetails.builder()
                    .username("bill")
                    .password(passwordEncoder.encode("password"))
                    .authorities(List.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .userMeta(UserMeta.builder().name("bill").email("bill@email.com").build())
                    .build();

            userRepo.saveAll(List.of(userBen,userBill,userBob,userIdfk,userSally,userMark));

            // Ingredients

            Ingredient ingredient = Ingredient.builder()
                    .name("flour")
                    .state("dry")
                    .amount("2 cups")
                    .build();

            Step step1 = Step.builder()
                    .description("put flour in bowl")
                    .stepNumber(1)
                    .build();
            Step step2 = Step.builder()
                    .description("eat it?")
                    .stepNumber(2)
                    .build();

            Review review = Review.builder()
                    .description("tasted pretty bad")
                    .rating(2)
                    .user(userIdfk)
                    .build();

            Recipe recipe1 = Recipe.builder()
                    .name("test recipe")
                    .difficultyRating(10)
                    .minutesToMake(2)
                    .ingredients(List.of(ingredient))
                    .steps(List.of(step1, step2))
                    .reviews(List.of(review))
                    .user(userBob)
                    .build();

            recipeRepo.save(recipe1);

            ingredient.setId(null);
            Recipe recipe2 = Recipe.builder()
                    .steps(List.of(Step.builder()
                            .description("test")
                            .stepNumber(1)
                            .build()))
                    .ingredients(List.of(Ingredient.builder()
                            .name("test ing")
                            .amount("1")
                            .state("dry")
                            .build()))
                    .name("another test recipe")
                    .difficultyRating(10)
                    .minutesToMake(2)
                    .user(userSally)
                    .build();
            recipeRepo.save(recipe2);

            Recipe recipe3 = Recipe.builder()
                    .steps(List.of(Step.builder()
                            .description("test 2")
                            .stepNumber(1)
                            .build()))
                    .ingredients(List.of(Ingredient.builder()
                            .name("test ing 2")
                            .amount("2")
                            .state("wet")
                            .build()))
                    .name("another another test recipe")
                    .difficultyRating(5)
                    .minutesToMake(2)
                    .user(userMark)
                    .build();

            recipeRepo.save(recipe3);

            Recipe recipe4 = Recipe.builder()
                    .name("chocolate and potato chips")
                    .difficultyRating(10)
                    .minutesToMake(1)
                    .ingredients(List.of(
                            Ingredient.builder()
                                    .name("potato chips")
                                    .amount("1 bag")
                                    .build(),
                            Ingredient.builder()
                                    .name("chocolate")
                                    .amount("1 bar")
                                    .build()))
                    .steps(List.of(Step.builder()
                            .stepNumber(1)
                            .description("eat both items together")
                            .build()))
                    .reviews(List.of(Review.builder()
                            .user(userBen)
                            .rating(10)
                            .description("this stuff is so good")
                            .build()))
                    .user(userBill)
                    .build();

            recipeRepo.save(recipe4);
            System.out.println("FINISHED TEST DATABASE SETUP");
        }
    }
}

