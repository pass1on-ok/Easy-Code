package com.easycode.backend.repository;

import com.easycode.backend.entity.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {

    Optional<VideoProgress> findByUserIdAndVideoId(Long userId, Long videoId);

    List<VideoProgress> findByUserId(Long userId);
}
