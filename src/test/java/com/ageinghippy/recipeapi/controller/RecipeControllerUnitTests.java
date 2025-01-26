package com.ageinghippy.recipeapi.controller;

import com.ageinghippy.recipeapi.TestUtil;
import com.ageinghippy.recipeapi.exception.NoSuchRecipeException;
import com.ageinghippy.recipeapi.exception.ResponseErrorMessage;
import com.ageinghippy.recipeapi.model.*;
import com.ageinghippy.recipeapi.service.RecipeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeControllerUnitTests {

    private Recipe recipe1;
    private Recipe recipe2;
    private Recipe recipe3;
    private Recipe recipe4;

    private CustomUserDetails userBill;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RecipeService recipeService;

    @BeforeAll
    //Initialise 4 recipes for use by following tests
    public void setup() {


        userBill = CustomUserDetails.builder()
                .username("bill")
                .id(106L)
//                .password(passwordEncoder.encode("pass"))
                .authorities(
                        List.of(
                                Role.builder()
                                        .id(206L)
                                        .role(Role.Roles.ROLE_USER).build()))
                .userMeta(
                        UserMeta.builder()
                                .id(306L)
                                .email("bill@mail.com")
                                .name("bill").build())
                .build();

        CustomUserDetails userIdfk = CustomUserDetails.builder()
                .username("idfk")
                .id(101L)
//                .password(passwordEncoder.encode("pass"))
                .authorities(
                        List.of(
                                Role.builder()
                                        .id(201L)
                                        .role(Role.Roles.ROLE_USER).build()))
                .userMeta(
                        UserMeta.builder()
                                .id(301L)
                                .email("idfk@mail.com")
                                .name("idfk").build())
                .build();

        CustomUserDetails userBob = CustomUserDetails.builder()
                .username("bob")
                .id(102L)
//                .password(passwordEncoder.encode("pass"))
                .authorities(
                        List.of(
                                Role.builder()
                                        .id(202L)
                                        .role(Role.Roles.ROLE_USER).build()))
                .userMeta(
                        UserMeta.builder()
                                .id(302L)
                                .email("bob@mail.com")
                                .name("bob").build())
                .build();

        CustomUserDetails userSally = CustomUserDetails.builder()
                .username("sally")
                .id(103L)
//                .password(passwordEncoder.encode("pass"))
                .authorities(
                        List.of(
                                Role.builder()
                                        .id(203L)
                                        .role(Role.Roles.ROLE_USER).build()))
                .userMeta(
                        UserMeta.builder()
                                .id(303L)
                                .email("sally@mail.com")
                                .name("sally").build())
                .build();

        CustomUserDetails userMark = CustomUserDetails.builder()
                .username("mark")
                .id(104L)
//                .password(passwordEncoder.encode("pass"))
                .authorities(
                        List.of(
                                Role.builder()
                                        .id(204L)
                                        .role(Role.Roles.ROLE_USER).build()))
                .userMeta(
                        UserMeta.builder()
                                .id(304L)
                                .email("mark@mail.com")
                                .name("mark").build())
                .build();

        CustomUserDetails userBen = CustomUserDetails.builder()
                .username("ben")
                .id(105L)
//                .password(passwordEncoder.encode("pass"))
                .authorities(
                        List.of(
                                Role.builder()
                                        .id(205L)
                                        .role(Role.Roles.ROLE_USER).build()))
                .userMeta(
                        UserMeta.builder()
                                .id(305L)
                                .email("ben@mail.com")
                                .name("ben").build())
                .build();

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

        recipe1 = Recipe.builder()
                .id(1L)
                .name("test recipe")
                .difficultyRating(10)
                .minutesToMake(2)
                .ingredients(List.of(ingredient))
                .steps(List.of(step1, step2))
                .reviews(List.of(review))
                .user(userBob)
                .build();

        ingredient.setId(null);
        recipe2 = Recipe.builder()
                .id(2L)
                .name("another test recipe")
                .difficultyRating(10)
                .minutesToMake(2)
                .user(userSally)
                .steps(List.of(Step.builder()
                        .description("test")
                        .stepNumber(1)
                        .build()))
                .ingredients(List.of(Ingredient.builder()
                        .name("test ing")
                        .amount("1")
                        .state("dry")
                        .build()))
                .build();

        recipe3 = Recipe.builder()
                .id(3L)
                .name("another test recipe")
                .difficultyRating(5)
                .minutesToMake(2)
                .user(userMark)
                .steps(List.of(Step.builder()
                        .description("test 2")
                        .stepNumber(1)
                        .build()))
                .ingredients(List.of(Ingredient.builder()
                        .name("test ing 2")
                        .amount("2")
                        .state("wet")
                        .build()))
                .build();

        recipe4 = Recipe.builder()
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
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    public void testGetRecipeByIdSuccessBehavior() throws Exception {

        when(recipeService.getRecipeById(1L))
                .thenReturn(recipe1);

        final long recipeId = 1;

        // set up GET request
        mockMvc.perform(get("/recipe/" + recipeId))

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
                .andExpect(jsonPath("author").value("bob"));
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    public void testGetRecipeByIdFailureBehavior() throws Exception {

        final long recipeId = 5000;

        when(recipeService.getRecipeById(recipeId))
                .thenThrow(new NoSuchRecipeException("No recipe with ID " + recipeId + " could be found."));

        // set up guaranteed to fail in testing environment request
        mockMvc.perform(get("/recipe/" + recipeId))

                //print response
                .andDo(print())
                //expect status 404 NOT FOUND
                .andExpect(status().isNotFound())
                //confirm that HTTP body contains correct error message
                .andExpect(jsonPath("$.errorMessages[0]")
                        .value("No recipe with ID " + recipeId + " could be found."));
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    public void testGetAllRecipesSuccessBehavior() throws Exception {

        when(recipeService.getAllRecipes())
                .thenReturn(List.of(recipe1, recipe2, recipe3, recipe4));

        // set up get request for all recipe endpoint
        mockMvc.perform(get("/recipe"))

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
//    @WithUserDetails(value = "bill", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @WithMockUser(username = "bill", roles = {"ADMIN"})
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

        Recipe recipe = Recipe.builder()
                .name("caramel in a pan")
                .difficultyRating(10)
                .minutesToMake(2)
                .user(userBill)
                .ingredients(List.of(ingredient))
                .steps(List.of(step1, step2))
                .locationURI(new URI("http://localhost/recipe/"))
                .build();

        when(recipeService.createNewRecipe(any(Recipe.class))).thenReturn(recipe);
        when(recipeService.castToCustomUserDetails(any(UserDetails.class))).thenReturn(userBill);

        mockMvc.perform(post("/recipe")
                        // set request Content-Type header
                        .contentType("application/json")
                        // set HTTP body equal to JSON based on recipe object
                        .content(TestUtil.convertObjectToJsonBytes(recipe))
                        .with(csrf()))
                .andDo(print())
                // confirm HTTP response meta
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                // confirm Location header with new location of
                // object matches the correct URL structure
                .andExpect(header().string(
                        "Location",
                        containsString("http://localhost/recipe/")))

                // confirm some recipe data
                .andExpect(jsonPath("name").value("caramel in a pan"))
                .andExpect(jsonPath("author").value("bill"))

                // confirm ingredient data
                .andExpect(jsonPath("ingredients", hasSize(1)))
                .andExpect(jsonPath("ingredients[0].name")
                        .value("brown sugar"))
                .andExpect(jsonPath("ingredients[0].amount")
                        .value("1 cup"))

                // confirm step data
                .andExpect(jsonPath("steps", hasSize(2)))
                .andExpect(jsonPath("steps[0]").isNotEmpty())
                .andExpect(jsonPath("steps[1]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "bill", roles = {"ADMIN"})
    public void testCreateNewRecipeFailureBehaviorValidAnnotation() throws Exception {

        Recipe recipe = new Recipe();

        //following mockBean method should not be executed if @Valid on controller works as expected
        when(recipeService.createNewRecipe(any(Recipe.class))).thenReturn(recipe);

        // force failure with empty User object
        mockMvc.perform(post("/recipe")
                        // set request Content-Type header
                        .contentType("application/json")
                        // set HTTP body equal to JSON based on recipe object
                        .content(TestUtil.convertObjectToJsonBytes(recipe))
                        .with(csrf()))
                .andDo(print())
                .andDo(print())
                // confirm status code 400 BAD REQUEST
                .andExpect(status().isBadRequest())
                // confirm the body contains an array of strings
                .andExpect(jsonPath("$.errorMessages").isArray());
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    public void testGetRecipesByNameSuccessBehavior() throws Exception {

        when(recipeService.getRecipesByName("recipe")).thenReturn(List.of(recipe1, recipe2, recipe3));
        when(recipeService.getRecipesByName("potato")).thenReturn(List.of(recipe4));

        // get request to search for recipes with names including "recipe"
        MvcResult mvcResult =
                mockMvc.perform(get("/recipe/search/recipe").with(csrf()))
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
        byte[] jsonBytes = mockMvc.perform(get("/recipe/search/potato"))
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
    @WithMockUser(roles = "ANONYMOUS")
    public void testGetRecipeByNameFailureBehavior() throws Exception {

        when(recipeService.getRecipesByName(anyString())).thenThrow(new NoSuchRecipeException("Random error message"));

        byte[] contentAsByteArray = mockMvc.perform(
                        get("/recipe/search/should not exist"))
                .andDo(print())
                // expect 404 NOT FOUND
                .andExpect(status().isNotFound())
                // expect JSON object in the body
                .andExpect(content().contentType(
                        MediaType.APPLICATION_JSON_VALUE))
                // retrieve content byte array
                .andReturn().getResponse().getContentAsByteArray();

        // convert JSON to ResponseErrorMessage
        ResponseErrorMessage responseErrorMessage = TestUtil.convertJsonBytesToObject(contentAsByteArray, ResponseErrorMessage.class);

        // confirm error message is correct
        assertThat(responseErrorMessage.getErrorMessages().getFirst()).isEqualTo("Random error message");
    }

    @Test
    @WithMockUser(username = "bill", roles = {"USER"})
    //todo - not acting as expected. @PreAuthorize not being evaluated
    public void testDeleteRecipeByIdSuccessBehavior() throws Exception {
        final long recipeId = recipe3.getId();

        when(recipeService.deleteRecipeById(recipeId)).thenReturn(recipe3);

        // set up delete request
        byte[] deleteResponseByteArr =
                mockMvc.perform(delete("/recipe/" + recipeId)
                                .with(csrf()))
                        .andDo(print())
                        // confirm 200 OK was returned
                        .andExpect(status().isOk())
                        // confirm a String was returned
                        .andExpect(content().contentType(
                                MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                        .andReturn().getResponse().getContentAsByteArray();

        // pull delete message from byte[]
        String returnedDeleteConfirmationMessage = new String(deleteResponseByteArr);

        // confirm the message is as expected
        // using the previously acquired Recipe object
        assertThat(returnedDeleteConfirmationMessage)
                .isEqualTo("The recipe with ID " +
                        recipe3.getId() + " and name " +
                        recipe3.getName() + " was deleted.");
    }

    @Test
    @WithMockUser(username = "bill", roles = {"ADMIN"})
    //todo - not acting as expected. @PreAuthorize not being evaluated
    public void testDeleteRecipeByIdFailureBehavior() throws Exception {

        when(recipeService.deleteRecipeById(any())).thenThrow(new NoSuchRecipeException("No Such Recipe"));

        // force error with invalid ID
        byte[] responseContent =
                mockMvc.perform(delete("/recipe/-1")
                                .with(csrf()))
                        // expect 404 NOT FOUND
                        .andExpect(status().isNotFound())
                        // expect plain text aka a String
                        .andExpect(content().contentType(
                                MediaType.APPLICATION_JSON_VALUE))
                        .andReturn().getResponse().getContentAsByteArray();

        ResponseErrorMessage responseErrorMessage =
                TestUtil.convertJsonBytesToObject(responseContent, ResponseErrorMessage.class);

        // confirm correct error message
        assertThat(responseErrorMessage.getErrorMessages().getFirst()).isEqualTo("No Such Recipe");
    }


    @Test
    @WithMockUser("ANONYMOUS")
    //todo - not acting as expected. @PreAuthorize not being evaluated
    public void testGetAllRecipesFailureBehavior() throws Exception {

        when(recipeService.getAllRecipes()).thenThrow(new NoSuchRecipeException("A message here"));

        // perform GET all recipes
        mockMvc.perform(get("/recipe"))

                .andDo(print())

                // expect 404 NOT FOUND
                .andExpect(status().isNotFound())

                // expect error message defined in RecipeService class
                .andExpect(jsonPath("$.errorMessages[0]").value("A message here"));
    }


}
