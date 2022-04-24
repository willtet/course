package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    LessonRepository repository;

    @Override
    public LessonModel save(LessonModel model) {
        return repository.save(model);
    }

    @Override
    public Optional<LessonModel> findLessonIntoModule(UUID lessonId, UUID moduleId) {
        return repository.findLessonIntoModule(lessonId, moduleId);
    }

    @Override
    public void delete(LessonModel lessonModel) {
        repository.delete(lessonModel);
    }

    @Override
    public List<LessonModel> findAllByModule(UUID moduleId) {
        return repository.findAllLessonsIntoModule(moduleId);
    }
}
