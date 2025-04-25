package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.PhotoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("/upload")
    public DataResponse upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "personId") String personId){
        return photoService.upload(file, personId);
    }

    @PostMapping("/download")
    public DataResponse download(@Valid @RequestBody DataRequest dataRequest){
        return photoService.download(dataRequest);
    }

    @PostMapping("/getBaseUrl")
    public DataResponse getBaseUrl(@Valid @RequestBody DataRequest dataRequest){
        return photoService.getBaseUrl(dataRequest);
    }
}
