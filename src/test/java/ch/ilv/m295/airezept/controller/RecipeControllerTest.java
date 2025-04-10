package ch.ilv.m295.airezept.controller;

import ch.ilv.m295.airezept.dto.RecipeDto;
import ch.ilv.m295.airezept.entity.Recipe;
import ch.ilv.m295.airezept.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRecipes_ShouldReturnPageOfRecipes() {
        // Arrange
        List<Recipe> recipes = Arrays.asList(new Recipe(), new Recipe());
        Page<Recipe> recipePage = new PageImpl<>(recipes);
        when(recipeService.getAllRecipes(any(PageRequest.class))).thenReturn(recipePage);

        // Act
        ResponseEntity<Page<Recipe>> response = recipeController.getAllRecipes(0, 10, "createdAt", "desc");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());
        verify(recipeService, times(1)).getAllRecipes(any(PageRequest.class));
    }

    @Test
    void getRecipeById_ShouldReturnRecipe() {
        // Arrange
        Long recipeId = 1L;
        Recipe recipe = new Recipe();
        when(recipeService.getRecipeById(recipeId)).thenReturn(recipe);

        // Act
        ResponseEntity<Recipe> response = recipeController.getRecipeById(recipeId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipe, response.getBody());
        verify(recipeService, times(1)).getRecipeById(recipeId);
    }

    @Test
    void createRecipe_ShouldReturnCreatedRecipe() {
        // Arrange
        RecipeDto recipeDto = new RecipeDto();
        Recipe createdRecipe = new Recipe();
        when(recipeService.createRecipe(any(RecipeDto.class), anyString())).thenReturn(createdRecipe);

        // Act
        ResponseEntity<Recipe> response = recipeController.createRecipe(recipeDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdRecipe, response.getBody());
        verify(recipeService, times(1)).createRecipe(any(RecipeDto.class), anyString());
    }

    @Test
    void updateRecipe_ShouldReturnUpdatedRecipe() {
        // Arrange
        Long recipeId = 1L;
        RecipeDto recipeDto = new RecipeDto();
        Recipe updatedRecipe = new Recipe();
        when(recipeService.updateRecipe(anyLong(), any(RecipeDto.class), anyString())).thenReturn(updatedRecipe);

        // Act
        ResponseEntity<Recipe> response = recipeController.updateRecipe(recipeId, recipeDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRecipe, response.getBody());
        verify(recipeService, times(1)).updateRecipe(anyLong(), any(RecipeDto.class), anyString());
    }

    @Test
    void deleteRecipe_ShouldReturnNoContent() {
        // Arrange
        Long recipeId = 1L;
        doNothing().when(recipeService).deleteRecipe(anyLong(), anyString());

        // Act
        ResponseEntity<Void> response = recipeController.deleteRecipe(recipeId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(recipeService, times(1)).deleteRecipe(anyLong(), anyString());
    }
} 