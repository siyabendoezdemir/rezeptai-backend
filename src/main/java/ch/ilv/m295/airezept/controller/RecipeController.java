package ch.ilv.m295.airezept.controller;

import ch.ilv.m295.airezept.dto.RecipeDto;
import ch.ilv.m295.airezept.dto.ErrorResponse;
import ch.ilv.m295.airezept.entity.Recipe;
import ch.ilv.m295.airezept.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/recipes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Recipe", description = "Recipe management APIs")
@SecurityRequirement(name = "bearerAuth")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    @Operation(
        summary = "Get all recipes with pagination",
        description = "Retrieves a paginated list of all recipes. Results can be sorted by any field."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved recipes",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<Recipe>> getAllRecipes(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toLowerCase());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        return ResponseEntity.ok(recipeService.getAllRecipes(pageRequest));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get recipe by ID",
        description = "Retrieves a specific recipe by its unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved recipe",
            content = @Content(schema = @Schema(implementation = Recipe.class))),
        @ApiResponse(responseCode = "404", description = "Recipe not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Recipe> getRecipeById(
            @Parameter(description = "Recipe ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Create a new recipe",
        description = "Creates a new recipe. Requires authentication."
    )
    @PreAuthorize("isAuthenticated()")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Recipe created successfully",
            content = @Content(schema = @Schema(implementation = Recipe.class))),
        @ApiResponse(responseCode = "400", description = "Invalid recipe data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Recipe> createRecipe(
            @Parameter(description = "Recipe data to create", required = true)
            @Valid @RequestBody RecipeDto recipeDto) {
        return new ResponseEntity<>(
                recipeService.createRecipe(recipeDto, "test-user"),
                HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Update an existing recipe",
        description = "Updates an existing recipe. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recipe updated successfully",
            content = @Content(schema = @Schema(implementation = Recipe.class))),
        @ApiResponse(responseCode = "400", description = "Invalid recipe data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Recipe not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Recipe> updateRecipe(
            @Parameter(description = "Recipe ID", example = "1")
            @PathVariable Long id,
            
            @Parameter(description = "Updated recipe data", required = true)
            @Valid @RequestBody RecipeDto recipeDto) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipeDto, "test-user"));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a recipe",
        description = "Deletes an existing recipe. Requires ADMIN role."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Recipe deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Recipe not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteRecipe(
            @Parameter(description = "Recipe ID", example = "1")
            @PathVariable Long id) {
        recipeService.deleteRecipe(id, "test-user");
        return ResponseEntity.noContent().build();
    }
} 