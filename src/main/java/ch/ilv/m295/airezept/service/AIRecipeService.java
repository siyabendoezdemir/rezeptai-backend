package ch.ilv.m295.airezept.service;

import ch.ilv.m295.airezept.dto.RecipeDto;
import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.ContentBlock;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.springframework.transaction.annotation.Transactional;
import ch.ilv.m295.airezept.entity.Recipe;
import ch.ilv.m295.airezept.entity.RequestHistory;
import ch.ilv.m295.airezept.repository.RequestHistoryRepository;

@Service
@RequiredArgsConstructor
public class AIRecipeService {
    private static final Logger logger = LoggerFactory.getLogger(AIRecipeService.class);
    private final RecipeService recipeService;
    private final RequestHistoryRepository requestHistoryRepository;

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Bean
    public AnthropicClient anthropicClient() {
        return AnthropicOkHttpClient.builder()
            .apiKey(apiKey)
            .build();
    }

    @Transactional
    public RecipeDto generateRecipe(String ingredientsOrIdea, String userId) {
        String prompt = """
            You are a recipe generator. Create a recipe based on the following input: %s
            
            Respond with a JSON object in this exact format:
            {
                "title": "Recipe title",
                "description": "Brief description",
                "ingredients": [
                    "Ingredient 1 with quantity",
                    "Ingredient 2 with quantity"
                ],
                "instructions": [
                    "Step 1 instruction",
                    "Step 2 instruction"
                ],
                "preparation_time": 15,
                "cooking_time": 30,
                "servings": 4
            }
            
            Guidelines:
            1. Use only the fields shown above
            2. preparation_time and cooking_time should be numbers in minutes
            3. servings should be a number
            4. ingredients and instructions should be arrays of strings
            5. Do not include any additional fields or text
            6. Do not include any markdown formatting or code block markers
            7. The response must be valid JSON that can be parsed directly
            """.formatted(ingredientsOrIdea);

        logger.info("Sending prompt to AI: {}", prompt);

        MessageCreateParams params = MessageCreateParams.builder()
            .maxTokens(1024L)
            .addUserMessage(prompt)
            .model(Model.CLAUDE_3_7_SONNET_LATEST)
            .build();

        Message message = anthropicClient().messages().create(params);
        ContentBlock contentBlock = message.content().get(0);
        String recipeJson = contentBlock.text()
            .orElseThrow(() -> new RuntimeException("No text content in AI response"))
            .text();
        
        logger.info("Received response from AI: {}", recipeJson);

        // Clean the response by removing any markdown code block markers
        recipeJson = recipeJson.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
        
        try {
            RecipeDto recipeDto = parseRecipeJson(recipeJson);
            
            // Save the recipe
            Recipe savedRecipe = recipeService.createRecipe(recipeDto, userId);
            
            // Save the request history
            RequestHistory requestHistory = new RequestHistory();
            requestHistory.setUserId(userId);
            requestHistory.setUserInput(ingredientsOrIdea);
            requestHistory.setAiResponse(recipeJson);
            requestHistory.setGeneratedRecipe(savedRecipe);
            requestHistoryRepository.save(requestHistory);
            
            return recipeDto;
        } catch (Exception e) {
            logger.error("Failed to parse AI response: {}", recipeJson, e);
            throw new RuntimeException("Failed to parse recipe from AI response. The response was not in the expected JSON format.", e);
        }
    }

    private RecipeDto parseRecipeJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            // Validate required fields
            validateField(root, "title", JsonNode::isTextual);
            validateField(root, "description", JsonNode::isTextual);
            validateField(root, "ingredients", JsonNode::isArray);
            validateField(root, "instructions", JsonNode::isArray);
            validateField(root, "preparation_time", JsonNode::isNumber);
            validateField(root, "cooking_time", JsonNode::isNumber);
            validateField(root, "servings", JsonNode::isNumber);

            RecipeDto recipeDto = new RecipeDto();
            recipeDto.setTitle(root.get("title").asText());
            recipeDto.setDescription(root.get("description").asText());
            
            List<String> ingredients = new ArrayList<>();
            root.get("ingredients").forEach(node -> {
                if (!node.isTextual()) {
                    throw new IllegalArgumentException("Ingredients must be strings");
                }
                ingredients.add(node.asText());
            });
            recipeDto.setIngredients(ingredients);
            
            List<String> steps = new ArrayList<>();
            root.get("instructions").forEach(node -> {
                if (!node.isTextual()) {
                    throw new IllegalArgumentException("Instructions must be strings");
                }
                steps.add(node.asText());
            });
            recipeDto.setInstructions(String.join("\n", steps));
            
            int prepTime = root.get("preparation_time").asInt();
            int cookTime = root.get("cooking_time").asInt();
            int servings = root.get("servings").asInt();

            // Validate numeric values
            if (prepTime <= 0) throw new IllegalArgumentException("Preparation time must be positive");
            if (cookTime <= 0) throw new IllegalArgumentException("Cooking time must be positive");
            if (servings <= 0) throw new IllegalArgumentException("Servings must be positive");

            recipeDto.setPreparationTime(prepTime);
            recipeDto.setCookingTime(cookTime);
            recipeDto.setServings(servings);

            return recipeDto;
        } catch (JsonProcessingException e) {
            logger.error("Invalid JSON format: {}", e.getMessage());
            throw new RuntimeException("The AI response is not valid JSON: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid field value: {}", e.getMessage());
            throw new RuntimeException("The AI response contains invalid values: " + e.getMessage());
        }
    }

    private void validateField(JsonNode root, String fieldName, Predicate<JsonNode> validator) {
        JsonNode node = root.get(fieldName);
        if (node == null) {
            throw new IllegalArgumentException("Missing required field: " + fieldName);
        }
        if (!validator.test(node)) {
            throw new IllegalArgumentException("Invalid type for field: " + fieldName);
        }
    }
} 