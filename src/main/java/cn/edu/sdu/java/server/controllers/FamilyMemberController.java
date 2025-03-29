package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.FamilyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/familyMember")

public class FamilyMemberController {
    @Autowired
    FamilyMemberService familyMemberService;

    @PostMapping("/addFamilyMember")
    public DataResponse addFamilyMember(@RequestBody DataRequest dataRequest){
        return familyMemberService.addFamilyMember(dataRequest);
    }

    @PostMapping("/deleteFamilyMember")
    public DataResponse deleteFamilyMember(@RequestBody  DataRequest dataRequest){
        return familyMemberService.deleteFamilyMember(dataRequest);
    }

    @PostMapping("/editFamilyMemberInfo")
    public DataResponse editFamilyMemberInfo(@RequestBody DataRequest dataRequest){
        return familyMemberService.editFamilyMemberInfo(dataRequest);
    }

}
