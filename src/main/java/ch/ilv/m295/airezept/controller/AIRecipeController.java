package ch.ilv.m295.airezept.controller;

import ch.ilv.m295.airezept.dto.RecipeDto;
import ch.ilv.m295.airezept.service.AIRecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/recipes")
@RequiredArgsConstructor
@Tag(name = "AI Recipe Generator", description = "AI-powered recipe generation APIs")
@SecurityRequirement(name = "bearerAuth")
public class AIRecipeController {

    private final AIRecipeService aiRecipeService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a recipe using AI")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecipeDto> generateRecipe(
            @RequestBody String ingredientsOrIdea,
            Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new SecurityException("Authentication required");
        }
        String userId = ((Jwt) authentication.getPrincipal()).getSubject();
        return ResponseEntity.ok(aiRecipeService.generateRecipe(ingredientsOrIdea, userId));
    }
} 