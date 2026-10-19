package com.proj.miniats.entity;

import com.proj.miniats.enums.Stage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "candidates",
        uniqueConstraints = @UniqueConstraint(name = "uk_candidates_email", columnNames = "email"),
        indexes = {
                @Index(name = "idx_candidates_stage", columnList = "current_stage"),
                @Index(name = "idx_candidates_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "role_applied", nullable = false, length = 120)
    private String roleApplied;

    /** Comma-separated, e.g. "Java,Spring Boot,React". Simple and searchable with LIKE. */
    @Column(length = 500)
    private String skills;

    @Column(name = "experience_years")
    private Double experienceYears;

    /** Where the candidate came from: LinkedIn, Referral, Job Board, etc. */
    @Column(length = 60)
    private String source;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_stage", nullable = false, length = 20)
    @Builder.Default
    private Stage currentStage = Stage.APPLIED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    @ToString.Exclude
    @Builder.Default
    private List<StageHistory> history = new ArrayList<>();
}