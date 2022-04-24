package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    @Autowired
    LessonService lessonService;

    @Autowired
    ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(@RequestBody @Valid LessonDto dto,
                                             @PathVariable(value = "moduleId") UUID id){
        Optional<ModuleModel> optionalModuleModel = moduleService.findById(id);
        if(!optionalModuleModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        LessonModel model = new LessonModel();
        BeanUtils.copyProperties(dto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setModule(optionalModuleModel.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(model));
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "lessonId") UUID lessonId,
                                               @PathVariable(value = "moduleId") UUID moduleId){


        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(lessonId, moduleId);
        if(!optionalLessonModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this Module");
        }

        lessonService.delete(optionalLessonModel.get());

        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable(value = "lessonId") UUID lessonId,
                                               @PathVariable(value = "moduleId") UUID moduleId,
                                               @RequestBody @Valid LessonDto dto){
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(lessonId, moduleId);
        if(!optionalLessonModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this Module");
        }
        LessonModel model = optionalLessonModel.get();
        BeanUtils.copyProperties(dto, model);

        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(model));
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<List<LessonModel>> getAllLesson(@PathVariable(value = "moduleId") UUID moduleId){
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.findAllByModule(moduleId));
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(@PathVariable(value = "lessonId") UUID lessonId,
                                               @PathVariable(value = "moduleId") UUID moduleId){
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(lessonId, moduleId);
        if(!optionalLessonModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this Module");
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalLessonModel.get());
    }
}
