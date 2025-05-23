package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Achievement;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.AchievementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievement")
@CrossOrigin(origins = "*",  maxAge = 3600)
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    // 1. 添加成就
    @PostMapping("/add")
    public DataResponse addAchievement(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.addAchievement(dataRequest);
    }

    // 2. 更新成就
    @PostMapping("/update")
    public DataResponse updateAchievement(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.updateAchievement(dataRequest);
    }

    // 3. 删除成就
    @PostMapping("/deleteAchievement")
    public DataResponse deleteAchievement(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.deleteAchievement(dataRequest);
    }

    // 4. 根据ID查询成就
    @GetMapping("/{id}")
    public DataResponse getAchievementById(@PathVariable("id") Integer id) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.getMap("achievementMap").put("achievementId", id);
        return achievementService.getAchievementById(dataRequest);
    }

    // 6. 按名称搜索成就
    @PostMapping("/search")
    public DataResponse searchAchievementByName(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.searchAchievementByName(dataRequest);
    }
    @PostMapping("/getAchievementsByPage")
    public DataResponse getAchievementsByPage(@Valid @RequestBody DataRequest dataRequest) {
        return achievementService.getAchievementsByPage(dataRequest);
    }
}