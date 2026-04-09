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

    /**
     * Fetch course with videos in one query.
     * Materials and element collections (tags, prerequisites, learnings)
     * are loaded lazily within the same @Transactional service call.
     * Two simultaneous List JOIN FETCHes cause MultipleBagFetchException.
     */
    @Query("""
            SELECT DISTINCT c FROM Course c
            LEFT JOIN FETCH c.videos
            WHERE c.slug = :slug AND c.isPublished = true
            """)
    Optional<Course> findBySlugWithDetails(String slug);
}
