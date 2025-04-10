package ch.ilv.m295.airezept.entity;

import ch.ilv.m295.airezept.config.TestConfig;
import ch.ilv.m295.airezept.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class RecipeIntegrationTest {

    @Autowired
    private RecipeRepository recipeRepository;

    private Recipe testRecipe;

    @BeforeEach
    void setUp() {
        testRecipe = new Recipe();
        testRecipe.setTitle("Test Recipe");
        testRecipe.setDescription("A test recipe description");
        testRecipe.setIngredients(new ArrayList<>(Arrays.asList("Ingredient 1", "Ingredient 2")));
        testRecipe.setInstructions("Test instructions");
        testRecipe.setPreparationTime(30);
        testRecipe.setCookingTime(45);
        testRecipe.setServings(4);
        testRecipe.setCreatedBy("test-user");
    }

    @Test
    void testCreateRecipe() {
        // Act
        Recipe savedRecipe = recipeRepository.save(testRecipe);

        // Assert
        assertNotNull(savedRecipe.getId());
        assertEquals("Test Recipe", savedRecipe.getTitle());
        assertEquals("test-user", savedRecipe.getCreatedBy());
        assertNotNull(savedRecipe.getCreatedAt());
        assertNotNull(savedRecipe.getUpdatedAt());
        assertEquals(2, savedRecipe.getIngredients().size());
    }

    @Test
    void testReadRecipe() {
        // Arrange
        Recipe savedRecipe = recipeRepository.save(testRecipe);

        // Act
        Optional<Recipe> foundRecipe = recipeRepository.findById(savedRecipe.getId());

        // Assert
        assertTrue(foundRecipe.isPresent());
        assertEquals(savedRecipe.getTitle(), foundRecipe.get().getTitle());
        assertEquals(savedRecipe.getDescription(), foundRecipe.get().getDescription());
        assertEquals(savedRecipe.getIngredients().size(), foundRecipe.get().getIngredients().size());
    }

    @Test
    void testUpdateRecipe() {
        // Arrange
        Recipe savedRecipe = recipeRepository.save(testRecipe);
        savedRecipe.setTitle("Updated Test Recipe");
        savedRecipe.setDescription("Updated description");
        savedRecipe.setIngredients(new ArrayList<>(Arrays.asList("Updated Ingredient 1", "Updated Ingredient 2")));
        savedRecipe.setPreparationTime(45);
        savedRecipe.setCookingTime(60);
        savedRecipe.setServings(6);

        // Act
        Recipe updatedRecipe = recipeRepository.save(savedRecipe);

        // Assert
        assertEquals(savedRecipe.getId(), updatedRecipe.getId());
        assertEquals("Updated Test Recipe", updatedRecipe.getTitle());
        assertEquals("Updated description", updatedRecipe.getDescription());
        assertEquals(2, updatedRecipe.getIngredients().size());
        assertEquals(45, updatedRecipe.getPreparationTime());
        assertEquals(60, updatedRecipe.getCookingTime());
        assertEquals(6, updatedRecipe.getServings());
    }

    @Test
    void testDeleteRecipe() {
        // Arrange
        Recipe savedRecipe = recipeRepository.save(testRecipe);

        // Act
        recipeRepository.deleteById(savedRecipe.getId());

        // Assert
        assertFalse(recipeRepository.existsById(savedRecipe.getId()));
    }

    @Test
    void testFindAllRecipes() {
        // Arrange
        Recipe recipe1 = new Recipe();
        recipe1.setTitle("Recipe 1");
        recipe1.setCreatedBy("test-user");
        recipe1.setIngredients(new ArrayList<>(Arrays.asList("Ingredient 1")));

        Recipe recipe2 = new Recipe();
        recipe2.setTitle("Recipe 2");
        recipe2.setCreatedBy("test-user");
        recipe2.setIngredients(new ArrayList<>(Arrays.asList("Ingredient 2")));

        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);

        // Act
        List<Recipe> recipes = recipeRepository.findAll();

        // Assert
        assertEquals(2, recipes.size());
        assertTrue(recipes.stream().anyMatch(r -> r.getTitle().equals("Recipe 1")));
        assertTrue(recipes.stream().anyMatch(r -> r.getTitle().equals("Recipe 2")));
    }

    @Test
    void testFindByCreatedBy() {
        // Arrange
        Recipe recipe1 = new Recipe();
        recipe1.setTitle("User1 Recipe");
        recipe1.setCreatedBy("user1");
        recipe1.setIngredients(new ArrayList<>(Arrays.asList("Ingredient 1")));

        Recipe recipe2 = new Recipe();
        recipe2.setTitle("User2 Recipe");
        recipe2.setCreatedBy("user2");
        recipe2.setIngredients(new ArrayList<>(Arrays.asList("Ingredient 2")));

        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);

        // Act
        List<Recipe> user1Recipes = recipeRepository.findByCreatedBy("user1");

        // Assert
        assertEquals(1, user1Recipes.size());
        assertEquals("User1 Recipe", user1Recipes.get(0).getTitle());
    }
} 