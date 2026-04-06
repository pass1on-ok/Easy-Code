package com.easycode.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Price in tenge (₸). 0 means free. */
    @Column(nullable = false)
    private Integer price = 0;

    /** Discount percentage 0-100 */
    @Column(nullable = false)
    private Integer discount = 0;

    @Column(length = 500)
    private String thumbnail;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished = true;

    @Column(name = "stripe_product_id")
    private String stripeProductId;

    @Column(name = "stripe_price_id")
    private String stripePriceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    // ---- Relationships ----

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("serialNumber ASC")
    @Builder.Default
    private List<Video> videos = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CourseMaterial> materials = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "course_prerequisites", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "description")
    @Builder.Default
    private List<String> prerequisites = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "course_learnings", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "description")
    @Builder.Default
    private List<String> learnings = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** Returns the discounted price (final price to pay). */
    public int getFinalPrice() {
        if (discount == null || discount <= 0) return price;
        return (int) (price * (1 - discount / 100.0));
    }
}
