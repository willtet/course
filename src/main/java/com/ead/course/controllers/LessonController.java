package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specfications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
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
        log.debug("POST saveLesson lessonDto received {} ", dto.toString());
        Optional<ModuleModel> optionalModuleModel = moduleService.findById(id);
        if(!optionalModuleModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        LessonModel model = new LessonModel();
        BeanUtils.copyProperties(dto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setModule(optionalModuleModel.get());

        log.debug("POST saveLesson lessonId saved {} ", model.getLessonId());
        log.info("Lesson saved successfully lessonId {} ", model.getLessonId());
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(model));
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "lessonId") UUID lessonId,
                                               @PathVariable(value = "moduleId") UUID moduleId){
        log.debug("DELETE deleteLesson lessonId received {} ", lessonId);

        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(lessonId, moduleId);
        if(!optionalLessonModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this Module");
        }

        lessonService.delete(optionalLessonModel.get());

        log.debug("DELETE deleteLesson lessonId deleted {} ", lessonId);
        log.info("Lesson deleted successfully lessonId {} ", lessonId);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable(value = "lessonId") UUID lessonId,
                                               @PathVariable(value = "moduleId") UUID moduleId,
                                               @RequestBody @Valid LessonDto dto){
        log.debug("PUT updateLesson lessonDto received {} ", dto.toString());
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(lessonId, moduleId);
        if(!optionalLessonModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this Module");
        }
        LessonModel model = optionalLessonModel.get();
        BeanUtils.copyProperties(dto, model);

        log.debug("PUT updateLesson lessonId saved {} ", model.getLessonId());
        log.info("Lesson updated successfully lessonId {} ", model.getLessonId());
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(model));
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                                          SpecificationTemplate.LessonSpec spec,
                                                          @PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.ASC) Pageable page){
        Page<LessonModel> modelPage = lessonService.findAllByModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), page);
        if (!modelPage.isEmpty()){
            for (LessonModel model: modelPage.toList()){
                model.add(linkTo(methodOn(LessonController.class).getOneLesson(moduleId, model.getLessonId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(modelPage);
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
