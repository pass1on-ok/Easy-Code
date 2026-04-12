package com.easycode.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts_v2")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // NOTE: nullable=true to tolerate legacy/null rows during schema update in dev DB.
    // Data cleanup/migration can tighten this later.
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    // NOTE: nullable=true to tolerate legacy/null rows during schema update in dev DB.
    @JoinColumn(name = "video_id", nullable = true)
    private Video video;

    @Column(nullable = false)
    @Builder.Default
    private Integer score = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer total = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean passed = false;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        submittedAt = LocalDateTime.now();
    }
}

