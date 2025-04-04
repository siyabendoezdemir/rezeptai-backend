package ch.ilv.m295.airezept.controller;

import ch.ilv.m295.airezept.dto.RecipeDto;
import ch.ilv.m295.airezept.entity.Recipe;
import ch.ilv.m295.airezept.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/recipes", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Recipe", description = "Recipe management APIs")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    @Operation(summary = "Get all recipes")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by ID")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Create a new recipe")
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody RecipeDto recipeDto) {
        return new ResponseEntity<>(
                recipeService.createRecipe(recipeDto, "test-user"),
                HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    @Operation(summary = "Update an existing recipe")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeDto recipeDto) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipeDto, "test-user"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recipe")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id, "test-user");
        return ResponseEntity.noContent().build();
    }
} 