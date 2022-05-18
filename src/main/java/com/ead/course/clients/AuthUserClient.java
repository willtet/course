package com.ead.course.clients;

import com.ead.course.dtos.CourseUserDto;
import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class AuthUserClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsService utilsService;

    @Value("${ead.api.url.authuser}")
    String REQUEST_URI_AUTHUSER;

    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable page){
        List<UserDto> searchResult = null;
        String endpoint = REQUEST_URI_AUTHUSER + utilsService.createUrlGetAllUsersByCourse(courseId, page);
        log.debug("Request URL {}", endpoint);
        log.info("Request URL {}", endpoint);

        try {
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType =
                    new ParameterizedTypeReference<ResponsePageDto<UserDto>>() {
                    };
            ResponseEntity<ResponsePageDto<UserDto>> result = restTemplate.exchange(endpoint, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();
            log.debug("Response Number of Elements: {}", searchResult.size());
        }catch (HttpStatusCodeException e){

            log.error("Error request /users {}", e);
        }

        log.info("Ending request /users courseId {}", courseId);
        return new PageImpl<>(searchResult);
    }

    public ResponseEntity<UserDto> getOneUserById(UUID id){
        String url = REQUEST_URI_AUTHUSER + "/users/" + id;
        return restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }


    public void postSubscriptionUserInCourse(UUID courseId, UUID userId) {
        String url = REQUEST_URI_AUTHUSER + "/users/"+ userId + "/courses/subscription";
        CourseUserDto courseUserDto = new CourseUserDto();
        courseUserDto.setCourseId(courseId);
        courseUserDto.setUserId(userId);
        restTemplate.postForObject(url, courseUserDto, String.class);

    }
}
