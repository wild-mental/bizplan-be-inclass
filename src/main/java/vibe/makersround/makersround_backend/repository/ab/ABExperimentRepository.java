package vibe.makersround.makersround_backend.repository.ab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vibe.makersround.makersround_backend.entity.ab.ABExperiment;

import java.util.List;
import java.util.Optional;

@Repository
public interface ABExperimentRepository extends JpaRepository<ABExperiment, String> {

    Optional<ABExperiment> findByName(String name);

    List<ABExperiment> findByStatus(String status);

    List<ABExperiment> findByTargetPage(String targetPage);

    List<ABExperiment> findByStatusAndTargetPage(String status, String targetPage);

    @Query("SELECT e FROM ABExperiment e LEFT JOIN FETCH e.variants WHERE e.status = :status AND e.targetPage = :targetPage")
    List<ABExperiment> findActiveExperimentsWithVariants(@Param("status") String status, @Param("targetPage") String targetPage);

    @Query("SELECT e FROM ABExperiment e LEFT JOIN FETCH e.variants WHERE e.id = :id")
    Optional<ABExperiment> findByIdWithVariants(@Param("id") String id);

    boolean existsByName(String name);
}
