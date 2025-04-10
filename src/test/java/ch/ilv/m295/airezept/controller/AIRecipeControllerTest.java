package ch.ilv.m295.airezept.controller;

import ch.ilv.m295.airezept.dto.RecipeDto;
import ch.ilv.m295.airezept.service.AIRecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AIRecipeControllerTest {

    @Mock
    private AIRecipeService aiRecipeService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AIRecipeController aiRecipeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateRecipe_WithValidAuthentication_ShouldReturnRecipe() {
        // Arrange
        String ingredients = "chicken, rice, vegetables";
        String userId = "test-user";
        RecipeDto expectedRecipe = new RecipeDto();
        
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(userId);
        when(aiRecipeService.generateRecipe(anyString(), anyString())).thenReturn(expectedRecipe);

        // Act
        ResponseEntity<RecipeDto> response = aiRecipeController.generateRecipe(ingredients, authentication);

        // Assert
        assertNotNull(response);
        assertEquals(expectedRecipe, response.getBody());
        verify(aiRecipeService, times(1)).generateRecipe(ingredients, userId);
    }

    @Test
    void generateRecipe_WithNullAuthentication_ShouldThrowSecurityException() {
        // Arrange
        String ingredients = "chicken, rice, vegetables";

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            aiRecipeController.generateRecipe(ingredients, null);
        });
    }

    @Test
    void generateRecipe_WithInvalidPrincipal_ShouldThrowSecurityException() {
        // Arrange
        String ingredients = "chicken, rice, vegetables";
        when(authentication.getPrincipal()).thenReturn(new Object());

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            aiRecipeController.generateRecipe(ingredients, authentication);
        });
    }
} 