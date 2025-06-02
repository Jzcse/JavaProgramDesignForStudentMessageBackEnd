package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.InternshipService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/Internship")
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipController(InternshipService InternshipService) {
        this.internshipService = InternshipService;
    }
    @PostMapping("/getStudentItemOptionList")//获取学生下拉框数据
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.getStudentItemOptionList(dataRequest);
    }

    @PostMapping("getInternshipList")
    public DataResponse getInternshipList(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.getInternshipList(dataRequest);
    }
    @PostMapping("/addInternship")
    public DataResponse addInternship(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.addInternship(dataRequest);
    }
    @PostMapping("/Query_getInternshipList")//查询
    public DataResponse Query_getInternshipList(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.Query_getInternshipList(dataRequest);
    }
    @PostMapping("/deleteInternship")
    public DataResponse deleteInternship(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.deleteInternship(dataRequest);
    }
    @PostMapping("/editInternship")
    public DataResponse editInternship(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.editInternship(dataRequest);
    }




}
