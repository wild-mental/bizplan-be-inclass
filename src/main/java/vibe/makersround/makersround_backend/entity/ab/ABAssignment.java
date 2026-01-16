package vibe.makersround.makersround_backend.entity.ab;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ab_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"visitor_id", "experiment_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ABAssignment {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", nullable = false)
    private ABExperiment experiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ABVariant variant;

    @Column(name = "visitor_id", nullable = false)
    private String visitorId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.assignedAt = LocalDateTime.now();
    }

    public String getExperimentId() {
        return experiment != null ? experiment.getId() : null;
    }

    public String getVariantId() {
        return variant != null ? variant.getId() : null;
    }
}
