package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

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

    @PostMapping("/deleteTeacher")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteTeacher(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.deleteTeacher(dataRequest);
    }

    @PostMapping("/addTeacher")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addTeacher(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.addTeacher(dataRequest);
    }

    @PostMapping("/editTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse editTeacherInfo(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.editTeacherInfo(dataRequest);
    }

    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.getTeacherInfo(dataRequest);
    }

    @PostMapping("/searchTeacherByName")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse searchTeacherByName(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.searchTeacherByName(dataRequest);
    }

    @PostMapping("/searchTeacherByNum")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse searchTeacherByNum(@Valid @RequestBody DataRequest dataRequest){
        return teacherService.searchTeacherByNum(dataRequest);
    }

    @PostMapping("/getTeacherCount")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherCount(DataRequest dataRequest){
        return teacherService.getTeacherCount(dataRequest);
    }

    @PostMapping("demo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse demo(DataRequest dataRequest){
        DataResponse dataResponse = new DataResponse();
        Integer integer = 1;
        dataResponse.setData(integer);
        return dataResponse;
    }
    @PostMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportToPdf(@RequestBody(required = false) Map<String, Object> params,
                            HttpServletResponse response) throws IOException {
        teacherService.exportTeachersToPdf(response);
    }
}
