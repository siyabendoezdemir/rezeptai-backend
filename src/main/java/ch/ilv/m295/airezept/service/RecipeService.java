package ch.ilv.m295.airezept.service;

import ch.ilv.m295.airezept.dto.RecipeDto;
import ch.ilv.m295.airezept.entity.Recipe;
import ch.ilv.m295.airezept.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + id));
    }

    public List<Recipe> getRecipesByUser(String userId) {
        return recipeRepository.findByCreatedBy(userId);
    }

    @Transactional
    public Recipe createRecipe(RecipeDto recipeDto, String userId) {
        Recipe recipe = new Recipe();
        updateRecipeFromDto(recipe, recipeDto);
        recipe.setCreatedBy(userId);
        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipe(Long id, RecipeDto recipeDto, String userId) {
        Recipe recipe = getRecipeById(id);
        if (!recipe.getCreatedBy().equals(userId)) {
            throw new SecurityException("You can only update your own recipes");
        }
        updateRecipeFromDto(recipe, recipeDto);
        return recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteRecipe(Long id, String userId) {
        Recipe recipe = getRecipeById(id);
        if (!recipe.getCreatedBy().equals(userId)) {
            throw new SecurityException("You can only delete your own recipes");
        }
        recipeRepository.delete(recipe);
    }

    private void updateRecipeFromDto(Recipe recipe, RecipeDto dto) {
        recipe.setTitle(dto.getTitle());
        recipe.setDescription(dto.getDescription());
        recipe.setIngredients(dto.getIngredients());
        recipe.setInstructions(dto.getInstructions());
        recipe.setPreparationTime(dto.getPreparationTime());
        recipe.setCookingTime(dto.getCookingTime());
        recipe.setServings(dto.getServings());
    }
} 