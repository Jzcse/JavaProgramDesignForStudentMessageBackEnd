package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Achievement;
import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.AchievementRepository;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.sdu.java.server.util.CommonMethod.getInteger;
import static cn.edu.sdu.java.server.util.CommonMethod.getString;

@Service
public class AchievementService {

    @Autowired
    AchievementRepository achievementRepository;


    // 添加成就
    public DataResponse addAchievement(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map achievementMap = datarequest.getMap("achievementMap");

        if (achievementMap == null || achievementMap.isEmpty()) {
            return CommonMethod.getReturnMessageError("新增成就信息不能为空");
        }

        Achievement achievement = new Achievement();
        achievement.setAchievementName(CommonMethod.getString(achievementMap, "achievementName"));
        achievement.setAchievementDescription(CommonMethod.getString(achievementMap, "achievementDescription"));
        achievement.setAchievementTime(CommonMethod.getString(achievementMap, "achievementTime"));
        achievement.setAchievementPersonName(CommonMethod.getString(achievementMap, "achievementPerson"));

        achievementRepository.save(achievement);
        dataResponse.setCode(0);
        dataResponse.setMsg("新增成就成功");

        return dataResponse;
    }

    // 更新成就
    public DataResponse updateAchievement(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map achievementMap = datarequest.getMap("achievementMap");

        if (achievementMap == null || achievementMap.isEmpty()) {
            return CommonMethod.getReturnMessageError("更新成就信息不能为空");
        }

        Integer achievementId = getInteger(achievementMap, "achievementId");
        if (achievementId == null) {
            return CommonMethod.getReturnMessageError("成就ID不能为空");
        }

        try {
            Optional<Achievement> optionalAchievement = achievementRepository.findById(achievementId);
            if (optionalAchievement.isPresent()) {
                Achievement achievementUpdate = optionalAchievement.get();
                achievementUpdate.setAchievementName(CommonMethod.getString(achievementMap, "achievementName"));
                achievementUpdate.setAchievementDescription(CommonMethod.getString(achievementMap, "achievementDescription"));
                achievementUpdate.setAchievementTime(CommonMethod.getString(achievementMap, "achievementTime"));
                achievementUpdate.setAchievementPersonName(CommonMethod.getString(achievementMap, "achievementPersonName"));

                achievementRepository.save(achievementUpdate);
                dataResponse.setCode(0);
                dataResponse.setMsg("更新成就成功");
            } else {
                dataResponse.setCode(1);
                dataResponse.setMsg("未找到要更新的成就记录");
            }
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("更新成就失败: " + e.getMessage());
        }
        return dataResponse;
    }

    // 删除成就
    public DataResponse deleteAchievement(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map achievementMap = datarequest.getMap("achievementMap");

        if (achievementMap == null || achievementMap.isEmpty()) {
            return CommonMethod.getReturnMessageError("删除成就信息不能为空");
        }

        String achievementIdstr = getString(achievementMap, "achievementId");
        if (achievementIdstr == null) {
            return CommonMethod.getReturnMessageError("成就ID不能为空");
        }
        Double achievementIdD = Double.parseDouble(achievementIdstr);
        Integer achievementId = achievementIdD.intValue();

        try {
            Optional<Achievement> optionalAchievement = achievementRepository.findById(achievementId);
            if (optionalAchievement.isPresent()) {
                achievementRepository.deleteById(achievementId);
                dataResponse.setCode(0);
                dataResponse.setMsg("删除成就成功");
            } else {
                dataResponse.setCode(1);
                dataResponse.setMsg("未找到要删除的成就记录");
            }
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("删除成就失败: " + e.getMessage());
        }
        return dataResponse;
    }

    // 根据id查询成就
    public DataResponse getAchievementById(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map achievementMap = datarequest.getMap("achievementMap");
        Integer achievementId = getInteger(achievementMap, "achievementId");

        if (achievementId == null || achievementId <= 0) {
            dataResponse.setCode(1);
            dataResponse.setMsg("输入的成就ID格式不正确");
            return dataResponse;
        }

        try {
            Optional<Achievement> optionalAchievement = achievementRepository.findById(achievementId);
            if (optionalAchievement.isPresent()) {
                dataResponse.setCode(0);
                dataResponse.setMsg("查询成功");
                dataResponse.setData(optionalAchievement.get());
            } else {
                dataResponse.setCode(1);
                dataResponse.setMsg("未找到对应成就记录");
            }
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("查询成就失败: " + e.getMessage());
        }
        return dataResponse;
    }

    // 分页查询
    public DataResponse getAchievementsByPage(@Valid DataRequest dataRequest) {
        DataResponse res = new DataResponse();
        try {
            int page = dataRequest.getInteger("page");
            int size = dataRequest.getInteger("size");
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "achievementTime"));
            Page<Achievement> pageData = achievementRepository.findAll(pageable);

            List<Map<String, Object>> dataList = pageData.getContent().stream()
                    .map(achievement -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("achievementId", achievement.getAchievementId());
                        m.put("name", achievement.getAchievementName());
                        m.put("description", achievement.getAchievementDescription());
                        m.put("time", achievement.getAchievementTime());
                        m.put("personName", achievement.getAchievementPersonName());
                        return m;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("data", dataList);
            responseMap.put("totalPages", pageData.getTotalPages());
            responseMap.put("totalElements", pageData.getTotalElements());

            res.setData(responseMap);
            res.setCode(0);
            res.setMsg("分页查询成功");
        } catch (Exception e) {
            res.setCode(1);
            res.setMsg("分页查询失败: " + e.getMessage());
        }
        return res;
    }

    // 搜索成就
    public DataResponse searchAchievementByName(@Valid DataRequest dataRequest) {
        DataResponse res = new DataResponse();
        try {
            String name = dataRequest.getString("name");
            List<Achievement> list = achievementRepository.findByAchievementNameContaining(name);

            List<Map<String, Object>> dataList = list.stream()
                    .map(achievement -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("achievementId", achievement.getAchievementId());
                        m.put("name", achievement.getAchievementName());
                        m.put("description", achievement.getAchievementDescription());
                        m.put("time", achievement.getAchievementTime());
                        m.put("personName", achievement.getAchievementPersonName());
                        return m;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("list", dataList);

            res.setData(responseData);
            res.setCode(0);
            res.setMsg("搜索成就成功");
        } catch (Exception e) {
            res.setCode(1);
            res.setMsg("搜索成就失败: " + e.getMessage());
        }
        return res;
    }
}
