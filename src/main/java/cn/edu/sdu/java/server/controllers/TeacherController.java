package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")

public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.getTeacherList(dataRequest);
    }

    @DeleteMapping("/deleteTeacher")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteTeacher(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.deleteTeacher(dataRequest);
    }

    @PostMapping("/addTeacher")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addTeacher(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.addTeacher(dataRequest);
    }

    @PutMapping("/editTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse editTeacherInfo(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.editTeacherInfo(dataRequest);
    }

    @GetMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.getTeacherInfo(dataRequest);
    }

    @GetMapping("/searchTeacherByName")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse searchTeacherByName(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.searchTeacherByName(dataRequest);
    }
}
