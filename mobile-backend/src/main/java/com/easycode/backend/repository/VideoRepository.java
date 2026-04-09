package com.easycode.backend.repository;

import com.easycode.backend.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByCourseIdOrderBySerialNumberAsc(Long courseId);
}
