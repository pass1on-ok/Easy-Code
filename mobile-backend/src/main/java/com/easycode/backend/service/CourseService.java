package com.easycode.backend.service;

import com.easycode.backend.dto.response.CourseDetailVO;
import com.easycode.backend.dto.response.CourseVO;
import com.easycode.backend.dto.response.PurchasedCourseVO;

import java.util.List;

public interface CourseService {

    List<CourseVO> getAllCourses();

    CourseDetailVO getCourseBySlug(String slug, String username);

    List<PurchasedCourseVO> getPurchasedCourses(String username);

    void enrollFreeCourse(String slug, String username);
}
