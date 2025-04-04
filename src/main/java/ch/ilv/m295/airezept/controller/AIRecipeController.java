package ch.ilv.m295.airezept.controller;

import ch.ilv.m295.airezept.dto.RecipeDto;
import ch.ilv.m295.airezept.service.AIRecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/recipes")
@RequiredArgsConstructor
@Tag(name = "AI Recipe Generator", description = "AI-powered recipe generation APIs")
public class AIRecipeController {

    private final AIRecipeService aiRecipeService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a recipe using AI")
    public ResponseEntity<RecipeDto> generateRecipe(@RequestBody String ingredientsOrIdea) {
        return ResponseEntity.ok(aiRecipeService.generateRecipe(ingredientsOrIdea));
    }
} 