package ch.ilv.m295.airezept.repository;

import ch.ilv.m295.airezept.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByCreatedBy(String userId);
} 