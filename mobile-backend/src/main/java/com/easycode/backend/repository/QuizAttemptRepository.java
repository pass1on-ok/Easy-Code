package com.easycode.backend.repository;

import com.easycode.backend.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findByUserIdAndVideoId(Long userId, Long videoId);

    @Query("select a from QuizAttempt a where a.user.id = :userId and a.video.course.id = :courseId")
    List<QuizAttempt> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
}

