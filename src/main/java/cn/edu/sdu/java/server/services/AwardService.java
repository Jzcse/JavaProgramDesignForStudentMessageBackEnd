package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Award;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.AwardRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AwardService {

    @Autowired
    AwardRepository awardRepository;

    @Autowired
    StudentRepository studentRepository;

    @Transactional
    public DataResponse addAward(@Valid DataRequest datarequest) {
        Award award = new Award();
        DataResponse dataResponse = new DataResponse();
        Map awardMap = datarequest.getMap("awardMap");

        if(awardMap.isEmpty()){
            dataResponse.setCode(1);
            dataResponse.setMsg("新增奖项列表不能为空");
            return dataResponse;
        }else {
            award.setAwardId(CommonMethod.getInteger(awardMap,"awardId"));
            award.setAwardLevel(CommonMethod.getString(awardMap,"awardLevel"));
            award.setAwardName(CommonMethod.getString(awardMap,"awardName"));
            award.setAwardStudentList(CommonMethod.getList(awardMap,"awardStudent"));
            award.setAwardTime(CommonMethod.getString(awardMap,"awardTime"));
            award.setAwardType(CommonMethod.getString(awardMap,"awardType"));
        }
        awardRepository.save(award);

        dataResponse.setCode(0);
        dataResponse.setMsg("新增奖项成功");
        return dataResponse;
    }

    // 获取奖项列表
    public DataResponse getAwardList(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Pageable awardPage = Pageable.ofSize(10);
        try {
            List<Award> awardList = awardRepository.findByAwardName(datarequest.getString("awardName"),awardPage);
            // 将 List 转换为 Map，这里简单以 awardId 作为 key
            Map<Integer, Award> awardMap = new HashMap<>();
            for (Award award : awardList) {
                awardMap.put(award.getAwardId(), award);
            }
            dataResponse.setData(awardMap);
            dataResponse.setCode(0);
            dataResponse.setMsg("获取奖项列表成功");
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("获取奖项列表失败: " + e.getMessage());
        }
        return dataResponse;
    }

//if (awardId == null) 是对客户端请求数据的有效性进行验证，确保请求中包含必要的参数。
//Optional 对象检测是对数据库查询结果的有效性进行验证，确保数据库中存在与请求 ID 对应的记录。
//所以二者并不重复
    @Transactional
    public DataResponse deleteAward(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map deleteMap = datarequest.getMap("deleteMap");
        Integer awardId = CommonMethod.getInteger(deleteMap, "awardId");
        if (awardId == null) {
            dataResponse.setCode(1);
            dataResponse.setMsg("awardId不能为空");
            return dataResponse;
        }
        try {
            Optional<Award> award = awardRepository.findById(awardId);
            if (award.isEmpty()) {
                dataResponse.setCode(1);
                dataResponse.setMsg("该奖项不存在");
                return dataResponse;
            }
            awardRepository.deleteById(awardId);
            dataResponse.setCode(0);
            dataResponse.setMsg("删除奖项成功");
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("删除奖项失败: " + e.getMessage());
        }
        return dataResponse;
    }
    //更新奖项信息
    @Transactional
    public DataResponse updateAward(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map awardMap = datarequest.getMap("awardMap");
        Integer awardId = CommonMethod.getInteger(awardMap, "awardId");
        if (awardId == null) {
            dataResponse.setCode(1);
            dataResponse.setMsg("awardId不能为空");
            return dataResponse;
        }
        try {
            Optional<Award> award = awardRepository.findById(awardId);
            if (award.isEmpty()) {
                dataResponse.setCode(1);
                dataResponse.setMsg("该奖项不存在");
                return dataResponse;
            }
            Award awardUpdate = award.get();
            awardUpdate.setAwardId(CommonMethod.getInteger(awardMap,"awardId"));
            awardUpdate.setAwardLevel(CommonMethod.getString(awardMap,"awardLevel"));
            awardUpdate.setAwardName(CommonMethod.getString(awardMap,"awardName"));
            awardUpdate.setAwardStudentList(CommonMethod.getList(awardMap,"awardStudent"));
            awardUpdate.setAwardTime(CommonMethod.getString(awardMap,"awardTime"));
            awardUpdate.setAwardType(CommonMethod.getString(awardMap,"awardType"));
            awardRepository.save(awardUpdate);
            dataResponse.setCode(0);
            dataResponse.setMsg("修改奖项成功");
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("修改奖项失败: " + e.getMessage());
        }
        return dataResponse;
    }

    // 此方法需要更复杂的逻辑来判断学生是否获奖，这里仅作示例
    public boolean isQualified(Score score) {
        Integer scoreId = score.getScoreId();
        Integer ranking = score.getRanking();
        return ranking <= 3;
    }
    //设置获奖人名单
    @Transactional
    public DataResponse setCandidatesList(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map scoreMap = datarequest.getMap("scoreMap");
        List<Student> candidates= new ArrayList<>();
        if(scoreMap.isEmpty()){
            dataResponse.setCode(1);
            dataResponse.setMsg("学生成绩列表不能为空");
            return dataResponse;
        }
        List<Score> scoreList = CommonMethod.getList(scoreMap,"scoreMap");
        for (Score score : scoreList) {
            if (isQualified(score)) {
                Integer studentId = score.getStudent().getPersonId();
                Optional<Student> student = studentRepository.findById(studentId);
                //在获奖人名单中添加获奖学生
                student.ifPresent(candidates::add);
            }
        }
        Award awardForCad = new Award();
        awardForCad.setAwardStudentList(candidates);
        awardRepository.save(awardForCad);
        dataResponse.setCode(0);
        dataResponse.setMsg("设置获奖学生成功");
        return dataResponse;
    }

    //获取获奖人数量
    public DataResponse getCandidatesListSize(@Valid DataRequest datarequest) {
        Map awardMap = datarequest.getMap("awardMap");
        DataResponse dataResponse = new DataResponse();
        if (awardMap.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("要查询的获奖人列表不能为空");
            return dataResponse;
        }
        Integer TargetAwardId = CommonMethod.getInteger(awardMap, "awardId");
        List<Student> awardList = null;
        if (TargetAwardId != null) {
            Optional<Award> TargetAward = awardRepository.findById(TargetAwardId);
            awardList = TargetAward.get().getAwardStudentList();
        }
        Integer awardListSize = awardList.size();
        dataResponse.setData(awardListSize);
        dataResponse.setCode(0);
        dataResponse.setMsg("获取获奖人数量成功");
        return dataResponse;
    }
}
