package vibe.makersround.makersround_backend.entity.ab;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ab_experiments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ABExperiment {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_page")
    private String targetPage;

    @Column(name = "target_element")
    private String targetElement;

    @Column(nullable = false)
    @Builder.Default
    private String status = "draft";

    @Column(name = "traffic_percentage")
    @Builder.Default
    private Integer trafficPercentage = 100;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "experiment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ABVariant> variants = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addVariant(ABVariant variant) {
        variants.add(variant);
        variant.setExperiment(this);
    }

    public void removeVariant(ABVariant variant) {
        variants.remove(variant);
        variant.setExperiment(null);
    }

    public boolean isRunning() {
        return "running".equals(this.status);
    }

    public void start() {
        this.status = "running";
        this.startDate = LocalDateTime.now();
    }

    public void stop() {
        this.status = "paused";
        this.endDate = LocalDateTime.now();
    }

    public void complete() {
        this.status = "completed";
        this.endDate = LocalDateTime.now();
    }
}
