package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ActivityRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.sdu.java.server.util.CommonMethod.getInteger;
import static cn.edu.sdu.java.server.util.CommonMethod.getString;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    // 添加活动
    public DataResponse addActivity(@Valid DataRequest dataRequest) {
        DataResponse response = new DataResponse();
        Map activityMap = dataRequest.getMap("activityMap");

        if (activityMap == null || activityMap.isEmpty()) {
            return CommonMethod.getReturnMessageError("新增活动信息不能为空");
        }

        Activity activity = new Activity();
        activity.setActivityName(getString(activityMap, "activityName"));
        activity.setActivityType(getString(activityMap, "activityType"));
        activity.setActivityTime(getString(activityMap, "activityTime"));
        activity.setLocation(getString(activityMap, "location"));
        activity.setDescription(getString(activityMap, "description"));

        activityRepository.save(activity);
        response.setCode(0);
        response.setMsg("新增活动成功");

        return response;
    }

    // 更新活动
    public DataResponse updateActivity(@Valid DataRequest dataRequest) {
        DataResponse response = new DataResponse();
        Map activityMap = dataRequest.getMap("activityMap");

        if (activityMap == null || activityMap.isEmpty()) {
            return CommonMethod.getReturnMessageError("更新活动信息不能为空");
        }

        Integer activityId = getInteger(activityMap, "activityId");
        if (activityId == null) {
            return CommonMethod.getReturnMessageError("活动ID不能为空");
        }

        try {
            Optional<Activity> optionalActivity = activityRepository.findById(activityId);
            if (optionalActivity.isPresent()) {
                Activity activity = optionalActivity.get();
                activity.setActivityName(getString(activityMap, "activityName"));
                activity.setActivityType(getString(activityMap, "activityType"));
                activity.setActivityTime(getString(activityMap, "activityTime"));
                activity.setLocation(getString(activityMap, "location"));
                activity.setDescription(getString(activityMap, "description"));

                activityRepository.save(activity);
                response.setCode(0);
                response.setMsg("更新活动成功");
            } else {
                response.setCode(1);
                response.setMsg("未找到要更新的活动记录");
            }
        } catch (Exception e) {
            response.setCode(1);
            response.setMsg("更新活动失败: " + e.getMessage());
        }
        return response;
    }

    // 删除活动
    public DataResponse deleteActivity(@Valid DataRequest dataRequest) {
        DataResponse response = new DataResponse();
        Map activityMap = dataRequest.getMap("activityMap");

        if (activityMap == null || activityMap.isEmpty()) {
            return CommonMethod.getReturnMessageError("删除活动信息不能为空");
        }

        Integer activityId = getInteger(activityMap, "activityId");
        if (activityId == null) {
            return CommonMethod.getReturnMessageError("活动ID不能为空");
        }

        try {
            Optional<Activity> optionalActivity = activityRepository.findById(activityId);
            if (optionalActivity.isPresent()) {
                activityRepository.deleteById(activityId);
                response.setCode(0);
                response.setMsg("删除活动成功");
            } else {
                response.setCode(1);
                response.setMsg("未找到要删除的活动记录");
            }
        } catch (Exception e) {
            response.setCode(1);
            response.setMsg("删除活动失败: " + e.getMessage());
        }
        return response;
    }

    // 根据ID查询活动
    public DataResponse getActivityById(@Valid DataRequest dataRequest) {
        DataResponse response = new DataResponse();
        Map activityMap = dataRequest.getMap("activityMap");
        Integer activityId = getInteger(activityMap, "activityId");

        if (activityId == null || activityId <= 0) {
            response.setCode(1);
            response.setMsg("输入的活动ID格式不正确");
            return response;
        }

        try {
            Optional<Activity> optionalActivity = activityRepository.findById(activityId);
            if (optionalActivity.isPresent()) {
                response.setCode(0);
                response.setMsg("查询成功");
                response.setData(optionalActivity.get());
            } else {
                response.setCode(1);
                response.setMsg("未找到对应活动记录");
            }
        } catch (Exception e) {
            response.setCode(1);
            response.setMsg("查询活动失败: " + e.getMessage());
        }
        return response;
    }

    // 分页查询活动
    public DataResponse getActivitiesByPage(@Valid DataRequest dataRequest) {
        DataResponse response = new DataResponse();
        try {
            int page = dataRequest.getInteger("page");
            int size = dataRequest.getInteger("size");
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "activityTime"));
            Page<Activity> pageData = activityRepository.findAll(pageable);

            List<Map<String, Object>> dataList = pageData.getContent().stream()
                    .map(activity -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("activityId", activity.getActivityId());
                        map.put("activityName", activity.getActivityName());
                        map.put("activityType", activity.getActivityType());
                        map.put("activityTime", activity.getActivityTime());
                        map.put("location", activity.getLocation());
                        map.put("description", activity.getDescription());
                        return map;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("data", dataList);
            responseData.put("totalPages", pageData.getTotalPages());
            responseData.put("totalElements", pageData.getTotalElements());

            response.setData(responseData);
            response.setCode(0);
            response.setMsg("分页查询成功");
        } catch (Exception e) {
            response.setCode(1);
            response.setMsg("分页查询失败: " + e.getMessage());
        }
        return response;
    }

    // 根据名称搜索活动
    public DataResponse searchActivitiesByName(@Valid DataRequest dataRequest) {
        DataResponse response = new DataResponse();
        try {
            String name = dataRequest.getString("name");
            List<Activity> activities = activityRepository.findByActivityNameContaining(name);

            List<Map<String, Object>> dataList = activities.stream()
                    .map(activity -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("activityId", activity.getActivityId());
                        map.put("activityName", activity.getActivityName());
                        map.put("activityType", activity.getActivityType());
                        map.put("activityTime", activity.getActivityTime());
                        map.put("location", activity.getLocation());
                        map.put("description", activity.getDescription());
                        return map;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("list", dataList);

            response.setData(responseData);
            response.setCode(0);
            response.setMsg("搜索活动成功");
        } catch (Exception e) {
            response.setCode(1);
            response.setMsg("搜索活动失败: " + e.getMessage());
        }
        return response;
    }
}