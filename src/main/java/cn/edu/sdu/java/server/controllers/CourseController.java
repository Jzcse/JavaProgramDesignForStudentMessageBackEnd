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

    @PostMapping("/getCourseId")
    public DataResponse getCourseId(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseId(dataRequest);
    }
    @PostMapping("/getCourseIdByNum")
    public DataResponse getCourseIdByNum(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseIdByNum(dataRequest);
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

    @PostMapping("/courseUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseUpdate(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseUpdate(dataRequest);
    }

    @PostMapping("/importCourses")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse importCourses(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.importCourses(dataRequest);
    }
    @PostMapping("/getWeekDaysList")
    public DataResponse getWeekDaysList(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getWeekDaysList(dataRequest);
    }
    @PostMapping("/selectCourse")
    public DataResponse selectCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.selectCourse(dataRequest);
    }
    @PostMapping("/dropCourse")
    public DataResponse dropCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.dropCourse(dataRequest);
    }
    @PostMapping("/getStudentCourseList")
    public DataResponse getStudentCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getStudentCourseList(dataRequest);
    }
    @PostMapping("/creditsCount")
    public DataResponse creditsCount(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.creditsCount(dataRequest);
    }
    @PostMapping("/getStudentCourseListResult")
    public DataResponse getStudentCourseListResult(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getStudentCourseListResult(dataRequest);
    }
    @PostMapping("/getSingleCourse")
    public DataResponse getSingleCourse(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getSingleCourse(dataRequest);
    }
    @PostMapping("/getSingleCourseAndScore")
    public DataResponse getSingleCourseAndScore(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getSingleCourseAndScore(dataRequest);
    }
    @PostMapping("/getTeacherCourseList")
    public DataResponse getTeacherCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getTeacherCourseList(dataRequest);
    }
    @PostMapping("/addCourseTeacher")
    public DataResponse addCourseTeacher(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.addCourseTeacher(dataRequest);
    }
    @PostMapping("/deleteCourseTeacher")
    public DataResponse deleteCourseTeacher(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.deleteCourseTeacher(dataRequest);
    }
    @PostMapping("/getTeacherCourseListResult")
    public DataResponse getTeacherCourseListResult(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getTeacherCourseListResult(dataRequest);
    }
    @PostMapping("/courseTotal")
    public DataResponse courseTotal(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseTotal(dataRequest);
    }
}