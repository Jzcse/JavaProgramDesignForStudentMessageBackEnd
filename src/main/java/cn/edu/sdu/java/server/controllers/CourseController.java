package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CourseService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/course")

public class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseList(dataRequest);
    }

    @PostMapping("/courseAdd")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseAdd(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseAdd(dataRequest);
    }
    @PostMapping("/courseDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseDelete(dataRequest);
    }
    @PutMapping("/courseUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseUpdate(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseUpdate(dataRequest);
    }
    @PostMapping("/addCourseRelation")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addCourseRelation(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.addCourseRelation(dataRequest);
    }
    @PostMapping("/deleteCourseRelation")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse removeCourseRelation(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.removeCourseRelation(dataRequest);
    }
    @PostMapping("/getCourseRelations")
    public DataResponse getCourseRelations(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseRelations(dataRequest);
    }
    @PostMapping("/scheduleCourse")

    public DataResponse scheduleCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.scheduleCourse(dataRequest);
    }
    @PostMapping("/getCourseSchedule")
    public DataResponse getCourseSchedule(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseSchedule(dataRequest);
    }

}
