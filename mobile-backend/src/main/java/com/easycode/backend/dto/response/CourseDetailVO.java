package com.easycode.backend.dto.response;

import com.easycode.backend.entity.Course;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Full course detail — used for /api/course/{slug}/
 */
@Data
@Builder
public class CourseDetailVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Integer price;
    private Integer discount;
    private Integer finalPrice;
    private String thumbnail;
    private List<String> tags;
    private List<String> prerequisites;
    private List<String> learnings;
    private List<VideoVO> videos;
    private List<CourseMaterialVO> materials;
    private Boolean isEnrolled;   // set based on authenticated user context

    public static CourseDetailVO from(Course course, boolean enrolled) {
        return CourseDetailVO.builder()
                .id(course.getId())
                .name(course.getName())
                .slug(course.getSlug())
                .description(course.getDescription())
                .price(course.getPrice())
                .discount(course.getDiscount())
                .finalPrice(course.getFinalPrice())
                .thumbnail(course.getThumbnail())
                .tags(course.getTags())
                .prerequisites(course.getPrerequisites())
                .learnings(course.getLearnings())
                .videos(course.getVideos().stream()
                        .map(v -> VideoVO.from(v, enrolled))
                        .collect(Collectors.toList()))
                .materials(enrolled
                        ? course.getMaterials().stream()
                                .map(CourseMaterialVO::from)
                                .collect(Collectors.toList())
                        : List.of())
                .isEnrolled(enrolled)
                .build();
    }
}
