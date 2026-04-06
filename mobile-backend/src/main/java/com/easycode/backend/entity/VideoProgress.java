package com.easycode.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_progress")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VideoProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "watched_seconds", nullable = false)
    private Integer watchedSeconds = 0;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "last_watched_at", nullable = false)
    private LocalDateTime lastWatchedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastWatchedAt = LocalDateTime.now();
    }
}
