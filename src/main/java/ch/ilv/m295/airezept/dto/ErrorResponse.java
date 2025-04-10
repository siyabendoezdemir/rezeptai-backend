package ch.ilv.m295.airezept.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response containing details about the error that occurred")
public class ErrorResponse {
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error message", example = "Invalid input data")
    private String message;
    
    @Schema(description = "Timestamp when the error occurred")
    private LocalDateTime timestamp;
    
    @Schema(description = "Path where the error occurred", example = "/api/recipes")
    private String path;
    
    @Schema(description = "Detailed error description", example = "Validation failed for field 'title'")
    private String details;
} 