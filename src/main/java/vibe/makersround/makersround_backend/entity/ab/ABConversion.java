package vibe.makersround.makersround_backend.entity.ab;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ab_conversions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ABConversion {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private ABAssignment assignment;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_data_json", columnDefinition = "TEXT")
    private String eventDataJson;

    @Column(name = "converted_at", nullable = false, updatable = false)
    private LocalDateTime convertedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.convertedAt = LocalDateTime.now();
    }

    public String getAssignmentId() {
        return assignment != null ? assignment.getId() : null;
    }
}
