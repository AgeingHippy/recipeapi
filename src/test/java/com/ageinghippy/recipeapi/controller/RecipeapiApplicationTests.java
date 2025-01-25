package com.ageinghippy.recipeapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ageinghippy.recipeapi.TestUtil;
import com.ageinghippy.recipeapi.exception.ResponseErrorMessage;
import com.ageinghippy.recipeapi.model.Ingredient;
import com.ageinghippy.recipeapi.model.Recipe;
import com.ageinghippy.recipeapi.model.Review;
import com.ageinghippy.recipeapi.model.Step;
import com.ageinghippy.recipeapi.repository.RecipeRepo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecipeapiApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RecipeRepo recipeRepo;

    @Test
    @Order(1)
    public void testGetRecipeByIdSuccessBehavior() throws Exception {

        final long recipeId = 1;

        // set up GET request
        mockMvc.perform(get("/recipes/" + recipeId))

                // print response
                .andDo(print())
                // expect status 200 OK
                .andExpect(status().isOk())
                // expect return Content-Type header as application/json
                .andExpect(content().contentType(
                        MediaType.APPLICATION_JSON_VALUE))

                // confirm returned JSON values
                .andExpect(jsonPath("id").value(recipeId))
                .andExpect(jsonPath("minutesToMake").value(2))
                .andExpect(jsonPath("reviews", hasSize(1)))
                .andExpect(jsonPath("ingredients", hasSize(1)))
                .andExpect(jsonPath("steps", hasSize(2)))
                .andExpect(jsonPath("username").value("bob"));
    }

    @Test
    @Order(2)
    public void testGetRecipeByIdFailureBehavior() throws Exception {

        final long recipeId = 5000;

        // set up guaranteed to fail in testing environment request
        mockMvc.perform(get("/recipes/" + recipeId))

                //print response
                .andDo(print())
                //expect status 404 NOT FOUND
                .andExpect(status().isNotFound())
                //confirm that HTTP body contains correct error message
                .andExpect(jsonPath("$.errorMessages[0]")
                        .value("No recipe with ID " + recipeId + " could be found."));
    }

    @Test
    @Order(3)
    public void testGetAllRecipesSuccessBehavior() throws Exception {

        // set up get request for all recipe endpoint
        mockMvc.perform(get("/recipes"))

                // expect status is 200 OK
                .andExpect(status().isOk())

                // expect it will be returned as JSON
                .andExpect(content().contentType(
                        MediaType.APPLICATION_JSON_VALUE))

                // expect there are 4 entries
                .andExpect(jsonPath("$", hasSize(4)))

                // expect the first entry to have ID 1
                .andExpect(jsonPath("$[0].id").value(1))

                // expect the first entry to have name test recipe
                .andExpect(jsonPath("$[0].name").value("test recipe"))

                // expect the second entry to have id 2
                .andExpect(jsonPath("$[1].id").value(2))

                // expect the second entry to have a minutesToMake value of 2
                .andExpect(jsonPath("$[1].minutesToMake").value(2))

                // expect the third entry to have id 3
                .andExpect(jsonPath("$[2].id").value(3))

                // expect the third entry to have difficulty rating
                .andExpect(jsonPath("$[2].difficultyRating").value(5));
    }

    @Test
    @Order(4)
    public void testCreateNewRecipeSuccessBehavior() throws Exception {

        Ingredient ingredient = Ingredient.builder()
                .name("brown sugar")
                .state("dry")
                .amount("1 cup")
                .build();

        Step step1 = Step.builder()
                .description("heat pan")
                .stepNumber(1)
                .build();
        Step step2 = Step.builder().
                description("add sugar")
                .stepNumber(2)
                .build();

        Review review = Review.builder()
                .description("was just caramel")
                .rating(3)
                .author("idk")
                .build();

        Recipe recipe = Recipe.builder()
                .name("caramel in a pan")
                .difficultyRating(10)
                .minutesToMake(2)
                .author("bill")
                .ingredients(List.of(ingredient))
                .steps(List.of(step1, step2))
                .reviews(List.of(review))
                .build();

        MockHttpServletResponse response = mockMvc
                .perform(post("/recipes")
                        // set request Content-Type header
                        .contentType("application/json")
                        // set HTTP body equal to JSON based on recipe object
                        .content(TestUtil.convertObjectToJsonBytes(recipe)))

                // confirm HTTP response meta
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                // confirm Location header with new location of
                // object matches the correct URL structure
                .andExpect(header().string(
                        "Location",
                        containsString("http://localhost/recipes/")))

                // confirm some recipe data
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("name").value("caramel in a pan"))
                .andExpect(jsonPath("username").value("bill"))

                // confirm ingredient data
                .andExpect(jsonPath("ingredients", hasSize(1)))
                .andExpect(jsonPath("ingredients[0].name")
                        .value("brown sugar"))
                .andExpect(jsonPath("ingredients[0].amount")
                        .value("1 cup"))

                // confirm step data
                .andExpect(jsonPath("steps", hasSize(2)))
                .andExpect(jsonPath("steps[0]").isNotEmpty())
                .andExpect(jsonPath("steps[1]").isNotEmpty())

                // confirm review data
                .andExpect(jsonPath("reviews", hasSize(1)))
                .andExpect(jsonPath("reviews[0].username")
                        .value("idk"))
                .andReturn()
                .getResponse();
    }

    @Test
    @Order(5)
    public void testCreateNewRecipeFailureBehavior() throws Exception {

        Recipe recipe = new Recipe();

        // force failure with empty User object
        mockMvc.perform(post("/recipes")
                        // set body equal to empty recipe object
                        .content(TestUtil.convertObjectToJsonBytes(recipe))
                        // set Content-Type header
                        .contentType("application/json"))
                // confirm status code 400 BAD REQUEST
                .andExpect(status().isBadRequest())
                // confirm the body contains an array of strings
                .andExpect(jsonPath("$.errorMessages").isArray());

    }

    @Test
    @Order(6)
    public void testGetRecipesByNameSuccessBehavior() throws Exception {

        // get request to search for recipes with names including "recipe"
        MvcResult mvcResult =
                mockMvc.perform(get("/recipes/search/recipe"))
                        // expect 200 OK
                        .andExpect(status().isOk())
                        // expect JSON in return
                        .andExpect(content().contentType("application/json"))
                        // return the MvcResult
                        .andReturn();

        // pull json byte array from the result
        byte[] jsonByteArray =
                mvcResult.getResponse().getContentAsByteArray();
        // convert the json bytes to an array of Recipe objects
        Recipe[] returnedRecipes = TestUtil.convertJsonBytesToObject(
                jsonByteArray, Recipe[].class);

        // confirm 3 recipes were returned
        assertThat(returnedRecipes.length).isEqualTo(3);

        for (Recipe r : returnedRecipes) {
            // confirm none of the recipes are null
            assertThat(r).isNotNull();
            // confirm they all have IDs
            assertThat(r.getId()).isNotNull();
            // confirm they all contain recipe in the name
            assertThat(r.getName()).contains("recipe");
        }

        // get request to search for recipes with names containing potato
        byte[] jsonBytes = mockMvc.perform(get("/recipes/search/potato"))
                // expect 200 OK
                .andExpect(status().isOk())
                // expect json
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                // return response byte array
                .andReturn().getResponse().getContentAsByteArray();

        // get recipes as a java array
        returnedRecipes =
                TestUtil.convertJsonBytesToObject(jsonBytes, Recipe[].class);

        // confirm only one recipe was returned
        assertThat(returnedRecipes.length).isEqualTo(1);

        // make sure the recipe isn't null
        assertThat(returnedRecipes[0]).isNotNull();

        // expect that the name should contain potato
        assertThat(returnedRecipes[0].getName()).contains("potato");
    }

    @Test
    @Order(7)
    public void testGetRecipeByNameFailureBehavior() throws Exception {

        byte[] contentAsByteArray = mockMvc.perform(
                        get("/recipes/search/should not exist"))
                // expect 404 NOT FOUND
                .andExpect(status().isNotFound())
                // expect JSON object in the body
                .andExpect(content().contentType(
                        MediaType.APPLICATION_JSON_VALUE))
                // retrieve content byte array
                .andReturn().getResponse().getContentAsByteArray();

        // convert JSON to ResponseErrorMessage
        ResponseErrorMessage responseErrorMessage = TestUtil.convertJsonBytesToObject(contentAsByteArray,ResponseErrorMessage.class);

        // confirm error message is correct
        assertThat(responseErrorMessage.getErrorMessages().getFirst()).isEqualTo(
                "No recipes could be found with that name.");
    }

    @Test
    @Order(8)
    public void testDeleteRecipeByIdSuccessBehavior() throws Exception {
        final long recipeId = 3;
        // get the recipe with ID 3 for future error message confirmation
        byte[] responseByteArr =
                mockMvc.perform(get("/recipes/" + recipeId))
                        .andExpect(status().isOk())
                        // confirm correct recipe was returned
                        .andExpect(jsonPath("id").value(recipeId))
                        .andReturn().getResponse().getContentAsByteArray();

        Recipe recipe3 = TestUtil.convertJsonBytesToObject(
                responseByteArr, Recipe.class);

        // set up delete request
        byte[] deleteResponseByteArr =
                mockMvc.perform(delete("/recipes/" + recipeId))
                        // confirm 200 OK was returned
                        .andExpect(status().isOk())
                        // confirm a String was returned
                        .andExpect(content().contentType(
                                MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                        .andReturn().getResponse().getContentAsByteArray();

        // pull delete message from byte[]
        String returnedDeleteConfirmationMessage =
                new String(deleteResponseByteArr);

        // confirm the message is as expected
        // using the previously acquired Recipe object
        assertThat(returnedDeleteConfirmationMessage)
                .isEqualTo("The recipe with ID " +
                        recipe3.getId() + " and name " +
                        recipe3.getName() + " was deleted.");
    }

    @Test
    @Order(9)
    public void testDeleteRecipeByIdFailureBehavior() throws Exception {
        // force error with invalid ID
        byte[] responseContent =
                mockMvc.perform(delete("/recipes/-1"))
                        // expect 404 NOT FOUND
                        .andExpect(status().isNotFound())
                        // expect plain text aka a String
                        .andExpect(content().contentType(
                                MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse().getContentAsByteArray();

        ResponseErrorMessage responseErrorMessage =
                TestUtil.convertJsonBytesToObject(responseContent,ResponseErrorMessage.class);

        // confirm correct error message
        assertThat(responseErrorMessage.getErrorMessages().getFirst()).isEqualTo(
                "No recipe with ID -1 could be found. Could not delete.");
    }


    @Test
    // make sure this test runs last
    @Order(11)
    public void testGetAllRecipesFailureBehavior() throws Exception {

        // delete all entries to force error
        recipeRepo.deleteAll();

        // perform GET all recipes
        mockMvc.perform(get("/recipes"))

                .andDo(print())

                // expect 404 NOT FOUND
                .andExpect(status().isNotFound())

                // expect error message defined in RecipeService class
                .andExpect(jsonPath("$.errorMessages[0]").value(
                        "There are no recipes yet :( " +
                                "feel free to add one though"));
    }


}
