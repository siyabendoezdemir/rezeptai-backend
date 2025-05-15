package ch.ilv.m295.airezept.service;

import ch.ilv.m295.airezept.dto.RecipeDto;
import ch.ilv.m295.airezept.entity.Recipe;
import ch.ilv.m295.airezept.entity.RequestHistory;
import ch.ilv.m295.airezept.repository.RecipeRepository;
import ch.ilv.m295.airezept.repository.RequestHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RequestHistoryRepository requestHistoryRepository;

    public Page<Recipe> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
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

        // Find and delete associated request history
        List<RequestHistory> histories = requestHistoryRepository.findByGeneratedRecipe_Id(id);
        if (histories != null && !histories.isEmpty()) {
            requestHistoryRepository.deleteAll(histories);
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