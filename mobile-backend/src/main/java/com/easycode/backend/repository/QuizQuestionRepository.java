package com.easycode.backend.repository;

import com.easycode.backend.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByVideoIdOrderByIdAsc(Long videoId);

    @Query("select distinct q.video.id from QuizQuestion q where q.video.course.id = :courseId")
    List<Long> findDistinctVideoIdsByCourseId(@Param("courseId") Long courseId);
}

