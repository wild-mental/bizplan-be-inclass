package vibe.makersround.makersround_backend.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.makersround.makersround_backend.entity.ab.ABVariant;

import java.util.List;
import java.util.Optional;

@Repository
public interface ABVariantRepository extends JpaRepository<ABVariant, String> {

    @Query("SELECT v FROM ABVariant v WHERE v.experiment.id = :experimentId")
    List<ABVariant> findByExperimentId(@Param("experimentId") String experimentId);

    @Query("SELECT v FROM ABVariant v WHERE v.experiment.id = :experimentId AND v.isControl = true")
    Optional<ABVariant> findByExperimentIdAndIsControlTrue(@Param("experimentId") String experimentId);

    @Query("SELECT v FROM ABVariant v WHERE v.experiment.id = :experimentId AND v.name = :name")
    Optional<ABVariant> findByExperimentIdAndName(@Param("experimentId") String experimentId, @Param("name") String name);

    @Query("SELECT v FROM ABVariant v WHERE v.experiment.id = :experimentId ORDER BY v.weight DESC")
    List<ABVariant> findByExperimentIdOrderByWeightDesc(@Param("experimentId") String experimentId);

    @Query("SELECT SUM(v.weight) FROM ABVariant v WHERE v.experiment.id = :experimentId")
    Integer sumWeightsByExperimentId(@Param("experimentId") String experimentId);

    @Query("DELETE FROM ABVariant v WHERE v.experiment.id = :experimentId")
    void deleteByExperimentId(@Param("experimentId") String experimentId);
}
