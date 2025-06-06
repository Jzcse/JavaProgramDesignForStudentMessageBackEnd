package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.AwardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping ("/api/award")
public class AwardController {

    @Autowired
    AwardService awardService;

    @PostMapping("/getSelectedList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getSelectedList(@Valid @RequestBody DataRequest dataRequest){
        return awardService.getSelectedList(dataRequest);
    }

    @PostMapping("/addAward")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addAward(@Valid @RequestBody DataRequest dataRequest){
        return awardService.addAward(dataRequest);
    }

    @PostMapping("/getAwardList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAwardList(@Valid @RequestBody DataRequest dataRequest){
        return awardService.getAwardList(dataRequest);
    }

    @PostMapping("/deleteAward")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteAward(@Valid @RequestBody DataRequest dataRequest){
        return awardService.deleteAward(dataRequest);
    }

    @PostMapping("/updateAward")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateAward(@Valid @RequestBody DataRequest dataRequest){
        return awardService.updateAward(dataRequest);
    }

    @PostMapping("/setCandidatesList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse setCandidatesList(@Valid @RequestBody DataRequest dataRequest){
        return awardService.setCandidatesList(dataRequest);
    }

    @PostMapping("/getCandidatesList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCandidatesList(@Valid @RequestBody DataRequest dataRequest){
        return awardService.getCandidatesList(dataRequest);
    }

    @PostMapping("/addCandidate")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addCandidate(@Valid @RequestBody DataRequest dataRequest){
        return awardService.addCandidate(dataRequest);
    }

    @PostMapping("/CandidatesDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse CandidatesDelete(@Valid @RequestBody DataRequest dataRequest){
        return awardService.CandidatesDelete(dataRequest);
    }

    @PostMapping("/getRelatedStudentForPdf")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAwardPersonList(@Valid @RequestBody DataRequest dataRequest){
        return awardService.getRelatedStudentForPdf(dataRequest);
    }

}
