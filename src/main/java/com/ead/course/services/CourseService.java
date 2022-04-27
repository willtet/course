package com.ead.course.services;

import com.ead.course.models.CourseModel;
import com.ead.course.specfications.SpecificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseService {

    void delete(CourseModel model);

    CourseModel save(CourseModel model);

    Optional<CourseModel> findById(UUID id);

    Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable page);
}
