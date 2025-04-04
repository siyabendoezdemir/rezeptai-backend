package ch.ilv.m295.airezept.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class RequestHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId; // Keycloak user ID

    @Column(columnDefinition = "TEXT")
    private String userInput; // What the user entered (ingredients or recipe idea)

    @Column(columnDefinition = "TEXT")
    private String aiResponse; // The full AI response

    @OneToOne
    @JoinColumn(name = "recipe_id")
    private Recipe generatedRecipe; // The recipe that was created from this request

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }
} 