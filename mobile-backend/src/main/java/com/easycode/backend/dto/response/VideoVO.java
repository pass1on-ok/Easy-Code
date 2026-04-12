package com.easycode.backend.dto.response;

import com.easycode.backend.entity.Video;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoVO {

    private Long id;
    private String title;
    private String description;
    private String videoUrl;        // JSON: video_url
    private Integer serialNumber;   // JSON: serial_number
    private Boolean isPreview;      // JSON: is_preview
    private Integer durationSec;    // JSON: duration_sec

    /**
     * @param enrolled whether the current user has enrolled in this course.
     *                 Non-enrolled users only see preview URLs.
     */
    public static VideoVO from(Video video, boolean enrolled) {
        return VideoVO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl((enrolled || video.isPreview()) ? video.getVideoUrl() : null)
                .serialNumber(video.getSerialNumber())
                .isPreview(video.isPreview())
                .durationSec(video.getDurationSec())
                .build();
    }
}
