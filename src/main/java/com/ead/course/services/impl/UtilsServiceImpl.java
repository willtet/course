package com.ead.course.services.impl;

import com.ead.course.services.UtilsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {


    @Override
    public String createUrlGetAllUsersByCourse(UUID courseId, Pageable page){
        return "/users?courseId="+courseId+"&page="+page.getPageNumber()+
                "&size="+page.getPageSize()+"&sort="+page.getSort().toString().replaceAll(": ", ",");
    }
}
