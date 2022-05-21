package com.ead.course.services.impl;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {
    @Autowired
    CourseUserRepository repository;

    @Autowired
    AuthUserClient authUserClient;

    @Override
    public boolean existsByCourseAndUserId(CourseModel course, UUID userId) {
        return repository.existsByCourseAndUserId(course, userId);
    }

    @Override
    public CourseUserModel save(CourseUserModel model) {
        return repository.save(model);
    }

    @Transactional
    @Override
    public CourseUserModel saveAndSendSubscriptionUserInCourse(CourseUserModel courseUserModel) {
        courseUserModel = repository.save(courseUserModel);

        authUserClient.postSubscriptionUserInCourse(courseUserModel.getCourse().getCourseId(), courseUserModel.getUserId());
        return courseUserModel;
    }


}
