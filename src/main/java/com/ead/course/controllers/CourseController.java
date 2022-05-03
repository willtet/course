package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
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
@RequestMapping("/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    CourseService service;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@RequestBody @Valid CourseDto dto){
        log.debug("POST saveCourse courseDto received {} ", dto.toString());
        CourseModel model = new CourseModel();
        BeanUtils.copyProperties(dto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        log.debug("POST saveCourse courseId saved {} ", model.getCourseId());
        log.info("Course saved successfully courseId {} ", model.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(model));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId") UUID id){
        log.debug("DELETE deleteCourse courseId received {} ", id);
        Optional<CourseModel> optionalCourseModel = service.findById(id);
        if(!optionalCourseModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        service.delete(optionalCourseModel.get());

        log.debug("DELETE deleteCourse courseId deleted {} ", id);
        log.info("Course deleted successfully courseId {} ", id);
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable(value = "courseId") UUID id,
                                               @RequestBody @Valid CourseDto dto){
        log.debug("PUT updateCourse courseDto received {} ", dto.toString());
        Optional<CourseModel> optionalCourseModel = service.findById(id);
        if(!optionalCourseModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        CourseModel model = optionalCourseModel.get();
        BeanUtils.copyProperties(dto, model);

        model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));


        log.debug("PUT updateCourse courseId saved {} ", model.getCourseId());
        log.info("Course updated successfully courseId {} ", model.getCourseId());
        return ResponseEntity.status(HttpStatus.OK).body(service.save(model));
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourse(SpecificationTemplate.CourseSpec spec,
                                                          @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable page,
                                                          @RequestParam(required = false) UUID userId) {
        Page<CourseModel> pageModel = null;
        if(userId != null){
            pageModel = service.findAll(SpecificationTemplate.courseUserId(userId).and(spec), page);
        }else{
            pageModel = service.findAll(spec, page);
        }

        if(!pageModel.isEmpty()){
            for(CourseModel model: pageModel.toList()){
                model.add(linkTo(methodOn(CourseController.class).getOneCourse(model.getCourseId())).withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(pageModel);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable(value = "courseId") UUID id){
        Optional<CourseModel> optionalCourseModel = service.findById(id);
        if(!optionalCourseModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalCourseModel.get());
    }
}
