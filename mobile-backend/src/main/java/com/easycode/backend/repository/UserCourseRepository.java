package com.easycode.backend.repository;

import com.easycode.backend.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);

    /** Fetch enrolled courses with their course data in one query. */
    @Query("SELECT uc FROM UserCourse uc JOIN FETCH uc.course WHERE uc.user.id = :userId")
    List<UserCourse> findByUserIdWithCourse(Long userId);
}
