package ch.ilv.m295.airezept.repository;

import ch.ilv.m295.airezept.entity.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {
    List<RequestHistory> findByUserId(String userId);
    List<RequestHistory> findByGeneratedRecipe_Id(Long recipeId);
} 