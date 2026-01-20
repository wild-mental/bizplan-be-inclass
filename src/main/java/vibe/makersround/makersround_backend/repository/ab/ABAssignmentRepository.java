package vibe.makersround.makersround_backend.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.makersround.makersround_backend.entity.ab.ABAssignment;

import java.util.List;
import java.util.Optional;

@Repository
public interface ABAssignmentRepository extends JpaRepository<ABAssignment, String> {

    @Query("SELECT a FROM ABAssignment a WHERE a.visitorId = :visitorId AND a.experiment.id = :experimentId")
    Optional<ABAssignment> findByVisitorIdAndExperimentId(@Param("visitorId") String visitorId, @Param("experimentId") String experimentId);

    List<ABAssignment> findByVisitorId(String visitorId);

    @Query("SELECT a FROM ABAssignment a WHERE a.experiment.id = :experimentId")
    List<ABAssignment> findByExperimentId(@Param("experimentId") String experimentId);

    @Query("SELECT a FROM ABAssignment a WHERE a.variant.id = :variantId")
    List<ABAssignment> findByVariantId(@Param("variantId") String variantId);

    @Query("SELECT COUNT(a) FROM ABAssignment a WHERE a.experiment.id = :experimentId")
    long countByExperimentId(@Param("experimentId") String experimentId);

    @Query("SELECT COUNT(a) FROM ABAssignment a WHERE a.variant.id = :variantId")
    long countByVariantId(@Param("variantId") String variantId);

    @Query("SELECT a FROM ABAssignment a JOIN FETCH a.variant JOIN FETCH a.experiment WHERE a.visitorId = :visitorId")
    List<ABAssignment> findByVisitorIdWithDetails(@Param("visitorId") String visitorId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM ABAssignment a WHERE a.visitorId = :visitorId AND a.experiment.id = :experimentId")
    boolean existsByVisitorIdAndExperimentId(@Param("visitorId") String visitorId, @Param("experimentId") String experimentId);
}
