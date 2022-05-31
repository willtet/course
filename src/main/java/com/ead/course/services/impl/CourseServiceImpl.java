package com.ead.course.services.impl;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import com.ead.course.specfications.SpecificationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    CourseUserRepository courseUserRepository;

    @Autowired
    AuthUserClient authUserClient;

    @Transactional
    @Override
    public void delete(CourseModel model) {
        boolean deleteCourseUserInAuthUser = false;



        List<ModuleModel> moduleModelList = moduleRepository.findAllModulesIntoCourse(model.getCourseId());
        if (!moduleModelList.isEmpty()){
            for(ModuleModel module: moduleModelList){
                List<LessonModel> lessonModelList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());
                if(!lessonModelList.isEmpty()){
                    lessonRepository.deleteAll(lessonModelList);
                }
            }
            moduleRepository.deleteAll(moduleModelList);
        }

        List<CourseUserModel> courseUserModelList = courseUserRepository.findAllCourseUserIntoCourse(model.getCourseId());
        if(!courseUserModelList.isEmpty()){
            courseUserRepository.deleteAll(courseUserModelList);
            deleteCourseUserInAuthUser = true;
        }

        courseRepository.delete(model);

        if(deleteCourseUserInAuthUser){
            authUserClient.deleteCourseInAuthUser(model.getCourseId());
        }


    }

    @Override
    public CourseModel save(CourseModel model) {
        return courseRepository.save(model);
    }

    @Override
    public Optional<CourseModel> findById(UUID id) {
        return courseRepository.findById(id);
    }

    @Override
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable page) {
        return courseRepository.findAll(spec, page);
    }

}
