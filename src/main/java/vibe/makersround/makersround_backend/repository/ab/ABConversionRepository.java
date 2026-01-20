package vibe.makersround.makersround_backend.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.makersround.makersround_backend.entity.ab.ABConversion;

import java.util.List;

@Repository
public interface ABConversionRepository extends JpaRepository<ABConversion, String> {

    @Query("SELECT c FROM ABConversion c WHERE c.assignment.id = :assignmentId")
    List<ABConversion> findByAssignmentId(@Param("assignmentId") String assignmentId);

    List<ABConversion> findByEventType(String eventType);

    @Query("SELECT c FROM ABConversion c WHERE c.assignment.id = :assignmentId AND c.eventType = :eventType")
    List<ABConversion> findByAssignmentIdAndEventType(@Param("assignmentId") String assignmentId, @Param("eventType") String eventType);

    @Query("SELECT COUNT(c) FROM ABConversion c WHERE c.assignment.id = :assignmentId")
    long countByAssignmentId(@Param("assignmentId") String assignmentId);

    @Query("SELECT COUNT(c) FROM ABConversion c WHERE c.assignment.variant.id = :variantId")
    long countByVariantId(@Param("variantId") String variantId);

    @Query("SELECT COUNT(c) FROM ABConversion c WHERE c.assignment.experiment.id = :experimentId")
    long countByExperimentId(@Param("experimentId") String experimentId);

    @Query("SELECT COUNT(c) FROM ABConversion c WHERE c.assignment.variant.id = :variantId AND c.eventType = :eventType")
    long countByVariantIdAndEventType(@Param("variantId") String variantId, @Param("eventType") String eventType);

    @Query("SELECT DISTINCT c.eventType FROM ABConversion c WHERE c.assignment.experiment.id = :experimentId")
    List<String> findDistinctEventTypesByExperimentId(@Param("experimentId") String experimentId);
}
