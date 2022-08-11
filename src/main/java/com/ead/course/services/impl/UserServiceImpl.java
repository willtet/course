package com.ead.course.services.impl;

import com.ead.course.models.UserModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.UserRepository;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository repository;

    @Autowired
    CourseRepository courseRepository;


    @Override
    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable page) {
        return repository.findAll(spec, page);
    }

    @Override
    public UserModel save(UserModel userModel) {

        return repository.save(userModel);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        courseRepository.deleteCourseUserByUser(userId);
        repository.deleteById(userId);
    }

    @Override
    public Optional<UserModel> findById(UUID userInstructor) {
        return repository.findById(userInstructor);
    }
}
