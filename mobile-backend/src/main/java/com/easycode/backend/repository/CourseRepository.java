package com.easycode.backend.repository;

import com.easycode.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Course> findByIsPublishedTrue();

    /** Eagerly fetch all associations to avoid N+1 on detail page. */
    @Query("""
            SELECT DISTINCT c FROM Course c
            LEFT JOIN FETCH c.videos
            LEFT JOIN FETCH c.materials
            WHERE c.slug = :slug AND c.isPublished = true
            """)
    Optional<Course> findBySlugWithDetails(String slug);
}
