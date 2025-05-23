package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // 添加活动
    @PostMapping("/add")
    public DataResponse addActivity(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.addActivity(dataRequest);
    }

    // 更新活动
    @PostMapping("/update")
    public DataResponse updateActivity(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.updateActivity(dataRequest);
    }

    // 删除活动
    @PostMapping("/delete")
    public DataResponse deleteActivity(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.deleteActivity(dataRequest);
    }

    // 根据ID查询活动
    @GetMapping("/{id}")
    public DataResponse getActivityById(@PathVariable("id") Integer id) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.getMap("activityMap").put("activityId", id);
        return activityService.getActivityById(dataRequest);
    }

    // 分页查询活动
    @PostMapping("/page")
    public DataResponse getActivitiesByPage(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivitiesByPage(dataRequest);
    }

    // 根据名称搜索活动
    @PostMapping("/search")
    public DataResponse searchActivitiesByName(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.searchActivitiesByName(dataRequest);
    }
}