package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CourseCentreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courseCentre")
public class CourseCentreController {
    private final CourseCentreService courseCentreService;
    public CourseCentreController(CourseCentreService courseCentreService) {
        this.courseCentreService = courseCentreService;
    }
    @PostMapping ("/addCourseCentre")
    public DataResponse addCourseCentre(@Valid @RequestBody DataRequest dataRequest) {
        return courseCentreService.addCourseCentre(dataRequest);
    }
    @PostMapping ("/deleteCourseCentre")
    public DataResponse deleteCourseCentre(@Valid @RequestBody DataRequest dataRequest) {
        return courseCentreService.deleteCourseCentre(dataRequest);
    }
    @PostMapping ("/getCourseCentreList")
    public DataResponse getCourseCentreList(@Valid @RequestBody DataRequest dataRequest) {
        return courseCentreService.getCourseCentreList(dataRequest);
    }
    @PostMapping ("/getCourseCentreId")
    public DataResponse getCourseCentreId(@Valid @RequestBody DataRequest dataRequest) {
        return courseCentreService.getCourseCentreId(dataRequest);
    }
    @PostMapping ("/updateCourseCentre")
    public DataResponse updateCourseCentre(@Valid @RequestBody DataRequest dataRequest) {
        return courseCentreService.updateCourseCentre(dataRequest);
    }
}
