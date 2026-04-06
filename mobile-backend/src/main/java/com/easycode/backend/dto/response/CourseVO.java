package com.easycode.backend.dto.response;

import com.easycode.backend.entity.Course;
import lombok.Builder;
import lombok.Data;

/**
 * Lightweight course list item — used for /api/courses/ and /api/purchased-courses/
 */
@Data
@Builder
public class CourseVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Integer price;
    private Integer discount;
    private Integer finalPrice;   // price after discount
    private String thumbnail;

    public static CourseVO from(Course course) {
        return CourseVO.builder()
                .id(course.getId())
                .name(course.getName())
                .slug(course.getSlug())
                .description(course.getDescription())
                .price(course.getPrice())
                .discount(course.getDiscount())
                .finalPrice(course.getFinalPrice())
                .thumbnail(course.getThumbnail())
                .build();
    }
}
