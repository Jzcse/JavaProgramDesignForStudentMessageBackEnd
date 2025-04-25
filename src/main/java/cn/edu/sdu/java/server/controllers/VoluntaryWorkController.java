package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.VoluntaryWorkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/voluntaryWork")
public class VoluntaryWorkController {

    @Autowired
    VoluntaryWorkService voluntaryWorkService;

    @RequestMapping("/addVoluntaryWork")
    public DataResponse addVoluntaryWork(@Valid @RequestBody DataRequest dataRequest){
        return voluntaryWorkService.addVoluntaryWork(dataRequest);
    }

    @RequestMapping("/getVoluntaryWorkList")
    public DataResponse getVoluntaryWorkList(@Valid @RequestBody DataRequest dataRequest){
        return voluntaryWorkService.getVoluntaryWorkList(dataRequest);
    }

    @RequestMapping("/deleteVoluntaryWork")
    public DataResponse deleteVoluntaryWork(@Valid @RequestBody DataRequest dataRequest){
        return voluntaryWorkService.deleteVoluntaryWork(dataRequest);
    }

    @RequestMapping("/updateVoluntaryWork")
    public DataResponse updateVoluntaryWork(@Valid @RequestBody DataRequest dataRequest){
        return voluntaryWorkService.updateVoluntaryWork(dataRequest);
    }

    @RequestMapping("/getSelectedList")
    public DataResponse getSelectedList(@Valid @RequestBody DataRequest dataRequest){
        return voluntaryWorkService.getSelectedList(dataRequest);
    }
}
