package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specfications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderDsl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    ModuleService moduleService;

    @Autowired
    CourseService courseService;

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(@RequestBody @Valid ModuleDto dto, @PathVariable(value = "courseId") UUID id){
        log.debug("POST saveModule moduleDto received {} ", dto.toString());
        Optional<CourseModel> optionalCourseModel = courseService.findById(id);
        if(!optionalCourseModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        ModuleModel model = new ModuleModel();
        BeanUtils.copyProperties(dto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setCourse(optionalCourseModel.get());

        log.debug("POST saveModule moduleId saved {} ", model.getModuleId());
        log.info("Module saved successfully moduleId {} ", model.getModuleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(model));
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId){
        log.debug("DELETE deleteModule moduleId received {} ", moduleId);


        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(!optionalModuleModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this Course");
        }

        moduleService.delete(optionalModuleModel.get());

        log.debug("DELETE deleteModule moduleId deleted {} ", moduleId);
        log.info("Module deleted successfully moduleId {} ", moduleId);
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId,
                                               @RequestBody @Valid ModuleDto dto){
        log.debug("PUT updateModule moduleDto received {} ", dto.toString());
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(!optionalModuleModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this Course");
        }
        ModuleModel model = optionalModuleModel.get();
        BeanUtils.copyProperties(dto, model);

        log.debug("PUT updateModule moduleId saved {} ", model.getModuleId());
        log.info("Module updated successfully moduleId {} ", model.getModuleId());
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(model));
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModule(@PathVariable(value = "courseId") UUID courseId,
                                                          SpecificationTemplate.ModuleSpec spec,
                                                          @PageableDefault(page = 0, size = 10, sort = "moduleId", direction = Sort.Direction.ASC) Pageable page){
        Page<ModuleModel> modelPage = moduleService.findAllByCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), page);
        if (!modelPage.isEmpty()){
            for (ModuleModel model : modelPage.toList()){
                model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ModuleController.class).getOneModule(courseId, model.getModuleId())).withSelfRel());
            }
        }


        return ResponseEntity.status(HttpStatus.OK).body(modelPage);
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId){
        Optional<ModuleModel> optionalModuleModel = moduleService.findModuleIntoCourse(courseId, moduleId);
        if(!optionalModuleModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this Course");
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalModuleModel.get());
    }

}
