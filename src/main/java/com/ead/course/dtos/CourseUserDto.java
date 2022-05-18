package com.ead.course.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
public class CourseUserDto {
    private UUID courseId;
    private UUID userId;
}
