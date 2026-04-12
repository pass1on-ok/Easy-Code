package com.easycode.backend.dto.response;

import com.easycode.backend.entity.Course;
import com.easycode.backend.entity.UserCourse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Enrolled course with progress info — used for /api/purchased-courses/
 */
@Data
@Builder
public class PurchasedCourseVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Integer price;
    private Integer discount;
    private String thumbnail;
    private LocalDateTime enrollmentDate;
    private Boolean completed;
    private Integer grade;

    public static PurchasedCourseVO from(UserCourse uc) {
        Course c = uc.getCourse();
        return PurchasedCourseVO.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .description(c.getDescription())
                .price(c.getPrice())
                .discount(c.getDiscount())
                .thumbnail(c.getThumbnail())
                .enrollmentDate(uc.getEnrollmentDate())
                .completed(uc.isCompleted())
                .grade(uc.getGrade())
                .build();
    }
}
