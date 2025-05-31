package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Award;
import cn.edu.sdu.java.server.models.AwardPerson;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.AwardPersonRepository;
import cn.edu.sdu.java.server.repositorys.AwardRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AwardService {

    @Autowired
    AwardRepository awardRepository;

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    private AwardPersonRepository awardPersonRepository;

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
            award.setAwardLevel(CommonMethod.getString(awardMap,"awardLevel"));
            award.setAwardName(CommonMethod.getString(awardMap,"awardName"));
            award.setAwardStudentList(CommonMethod.getList(awardMap,"awardStudentList"));
            award.setAwardTime(CommonMethod.getString(awardMap,"awardTime"));
            award.setAwardType(CommonMethod.getString(awardMap,"awardType"));
            award.setAwardSize(CommonMethod.getInteger(awardMap,"awardSize"));
        }
        awardRepository.save(award);

        dataResponse.setCode(0);
        dataResponse.setMsg("新增奖项成功");
        return dataResponse;
    }

    public Map getMapFromAward(Award award) {
        Map m = new HashMap();
        if(award == null)
            return m;
        m.put("awardId",award.getAwardId());
        m.put("awardName",award.getAwardName());
        m.put("awardLevel",award.getAwardLevel());
        m.put("awardTime",award.getAwardTime());
        m.put("awardType",award.getAwardType());
        m.put("awardSize",award.getAwardSize());
        return m;
    }

    public List getAwardMapList(String awardName) {
        List dataList = new ArrayList();
        List<Award> awardList = awardRepository.findAwardListByAwardName(awardName);  //数据库查询操作
        if (awardList == null || awardList.isEmpty())
            return dataList;
        for (int i = 0; i < awardList.size(); i++) {
            dataList.add(getMapFromAward(awardList.get(i)));
        }
        return dataList;
    }

    public DataResponse getSelectedList(DataRequest dataRequest) {
        String awardName = dataRequest.getString("awardName");
        List dataList = getAwardMapList(awardName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    // 获取奖项列表
    public DataResponse getAwardList(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        try {
            List<Award> awardList = awardRepository.findAll();
            Map<Integer, Map<String, String>> awardMap = new HashMap<>();
            for (Award award : awardList) {
                Map<String, String> awardData = new HashMap<>();
                awardData.put("awardId", String.valueOf(award.getAwardId()));
                awardData.put("awardName", award.getAwardName());
                awardData.put("awardLevel", award.getAwardLevel());
                awardData.put("awardTime", award.getAwardTime());
                awardData.put("awardType", award.getAwardType());
                awardData.put("awardSize", String.valueOf(award.getAwardSize()));
                // 其他属性也类似设置
                awardMap.put(award.getAwardId(), awardData);
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
        Integer awardId = datarequest.getInteger("awardId");
        DataResponse dataResponse = new DataResponse();
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
        Map awardMap = datarequest.getMap("form");
        String awardIdstr = CommonMethod.getString(awardMap, "awardId");
        double awardIdD = Double.parseDouble(awardIdstr);
        Integer awardId = (int) awardIdD;
        if (awardMap.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("更新奖项列表不能为空");
            return dataResponse;
        }
        try {
            Optional<Award> award = awardRepository.findAwardByAwardId(awardId);
            if (award.isEmpty()) {
                dataResponse.setCode(1);
                dataResponse.setMsg("该奖项不存在");
                return dataResponse;
            }
            Award awardUpdate = award.get();
            awardUpdate.setAwardLevel(CommonMethod.getString(awardMap,"awardLevel"));
            awardUpdate.setAwardName(CommonMethod.getString(awardMap,"awardName"));
            awardUpdate.setAwardStudentList(CommonMethod.getList(awardMap,"awardStudent"));
            awardUpdate.setAwardTime(CommonMethod.getString(awardMap,"awardTime"));
            awardUpdate.setAwardType(CommonMethod.getString(awardMap,"awardType"));
            awardUpdate.setAwardSize(CommonMethod.getInteger(awardMap,"awardSize"));;

            awardRepository.save(awardUpdate);
            dataResponse.setCode(0);
            dataResponse.setMsg("修改奖项成功");
        } catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("修改奖项失败: " + e.getMessage());
        }
        return dataResponse;
    }

    //设置获奖人名单，此功能也可以用于添加获奖候选人
    @Transactional
    public DataResponse setCandidatesList(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map awardMap = datarequest.getMap("awardMap");
        Map studentMap = datarequest.getMap("studentMap");
        if(awardMap.isEmpty()){
            dataResponse.setCode(1);
            dataResponse.setMsg("获取奖项信息失败");
            return dataResponse;
        }
        if(studentMap.isEmpty()){
            dataResponse.setCode(1);
            dataResponse.setMsg("获取学生信息失败");
            return dataResponse;
        }
        return dataResponse;
    }

    @Transactional
    public DataResponse addCandidate(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map studentMap = datarequest.getMap("studentMap");
        String awardId = datarequest.getString("awardId");
        String num = CommonMethod.getString(studentMap, "num");
        Optional<Student> student = studentRepository.findStudentByNum(num);
        if (student.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("该学生不存在");
            return dataResponse;
        }
        Student studentSave = student.get();

        Optional<Award> award = awardRepository.findById(Integer.parseInt(awardId));
        Award awardSave = award.get();

        if (studentMap.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("学生信息不能为空");
            return dataResponse;
        }

        //---------------------------------------------------------//
        studentMap.put("relatedStudentId", studentSave.getStudentId());
        studentMap.put("gender", studentSave.getPerson().getGender());
        studentMap.put("phone", studentSave.getPerson().getPhone());
        studentMap.put("major", studentSave.getMajor());
        studentMap.put("name", studentSave.getPerson().getName());
        studentMap.put("email", studentSave.getPerson().getEmail());

        AwardPerson awardPerson = new AwardPerson();
        awardPerson.setStudentAge(CommonMethod.getInteger(studentMap, "age"));
        awardPerson.setStudentGander(CommonMethod.getString(studentMap, "gender"));
        awardPerson.setStudentPhone(CommonMethod.getString(studentMap, "phone"));
        awardPerson.setStudentName(CommonMethod.getString(studentMap, "name"));
        awardPerson.setStudentEmail(CommonMethod.getString(studentMap, "email"));
        awardPerson.setRelatedStudentId(CommonMethod.getInteger(studentMap, "relatedStudentId"));
        awardPerson.setAward(awardSave);

        try {
            awardPersonRepository.save(awardPerson);
            awardSave.getAwardStudentList().add(awardPerson);
            awardRepository.save(awardSave);
            dataResponse.setCode(0);
            dataResponse.setMsg("添加获奖学生成功");
            dataResponse.setData(studentMap);
        }
        catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("添加获奖学生失败: " + e.getMessage());
        }
        return dataResponse;
    }

    @Transactional
    public DataResponse CandidatesDelete(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Map awardPersonMap = datarequest.getMap("awardPersonMap");
        String awardPersonId = datarequest.getString("personId");
        if (awardPersonMap.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("学生信息不能为空");
            return dataResponse;
        }
        try {
            Optional<AwardPerson> awardPerson = awardPersonRepository.findById(Integer.parseInt(awardPersonId));
            if (awardPerson.isEmpty()) {
                dataResponse.setCode(1);
                dataResponse.setMsg("该获奖学生不存在");
                return dataResponse;
            }
            awardPersonRepository.deleteById(Integer.parseInt(awardPersonId));
            dataResponse.setCode(0);
            dataResponse.setMsg("删除获奖学生成功");
            return dataResponse;
        }
        catch (Exception e) {
            dataResponse.setCode(1);
            dataResponse.setMsg("删除获奖学生失败: " + e.getMessage());
            return dataResponse;
        }
    }

    @Transactional
    public DataResponse getCandidatesList(@Valid DataRequest datarequest) {
        DataResponse dataResponse = new DataResponse();
        Integer awardId = datarequest.getInteger("awardId");
        if (awardId == null) {
            dataResponse.setCode(1);
            dataResponse.setMsg("awardId不能为空");
        }
        Optional<Award> award = awardRepository.findById(awardId);
        if (award.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("该奖项不存在");
            return dataResponse;
        }
        List<AwardPerson> awardPersonList = award.get().getAwardStudentList();
        List<Map<String, String>> mapList = new ArrayList<>();

        for (AwardPerson awardPerson : awardPersonList) {
            Map<String, String> map = getMapFromAwardPerson(awardPerson);
            mapList.add(map);
        }
        dataResponse.setCode(0);
        dataResponse.setMsg("获取获奖学生列表成功");
        dataResponse.setData(mapList);
        return dataResponse;
    }

    //
    private Map<String, String> getMapFromAwardPerson(AwardPerson awardPerson) {
        Map<String, String> map = new HashMap<>();
        map.put("awardPersonId", String.valueOf(awardPerson.getAwardPersonId()));
        map.put("name", awardPerson.getStudentName());
        map.put("age", String.valueOf(awardPerson.getStudentAge()));
        map.put("gander", awardPerson.getStudentGander());
        map.put("phone", awardPerson.getStudentPhone());
        map.put("email", awardPerson.getStudentEmail());
        map.put("relatedStudentId", awardPerson.getRelatedStudentId().toString());
        return map;
    }

    public DataResponse getRelatedStudentForPdf(@Valid DataRequest datarequest){
        DataResponse dataResponse = new DataResponse();
        Integer numName = datarequest.getInteger("numName");
        Integer relatedStudentId;
        //这里是为了通过学号获取studentId
        List<Student> targetStudent = studentRepository.findStudentListByNumName(numName.toString());
        if(targetStudent.isEmpty()){
            dataResponse.setCode(1);
            dataResponse.setMsg("该学生不存在");
            return dataResponse;
        }else{
            relatedStudentId = targetStudent.get(0).getStudentId();
        }

        List<AwardPerson> relatedStudent = awardPersonRepository.findByRelatedStudentId(relatedStudentId);
        if (relatedStudent.isEmpty()) {
            dataResponse.setCode(1);
            dataResponse.setMsg("该学生在校期间未获奖");
            return dataResponse;
        }
        List<Map> awardPersonList = new ArrayList<>();
        for (AwardPerson awardPerson : relatedStudent) {
            awardPersonList.add(getMapFromRelatedStudent(awardPerson));
        }
        dataResponse.setCode(0);
        dataResponse.setMsg("获取学生获奖信息成功");
        dataResponse.setData(awardPersonList);
        return dataResponse;
    }

    public Map getMapFromRelatedStudent(AwardPerson awardPerson) {
        Map m = new HashMap();
        if(awardPerson == null)
            return m;
        m.put("awardPersonId",awardPerson.getAwardPersonId());
        m.put("studentName",awardPerson.getStudentName());
        m.put("studentAge",awardPerson.getStudentAge());
        m.put("studentGander",awardPerson.getStudentGander());
        m.put("studentPhone",awardPerson.getStudentPhone());
        m.put("studentEmail",awardPerson.getStudentEmail());
        m.put("relatedStudentId",awardPerson.getRelatedStudentId());
        m.put("awardName",awardPerson.getAward().getAwardName());
        m.put("awardLevel",awardPerson.getAward().getAwardLevel());
        m.put("awardTime",awardPerson.getAward().getAwardTime());
        m.put("awardType",awardPerson.getAward().getAwardType());
        m.put("awardSize",awardPerson.getAward().getAwardSize());
        return m;
    }

}
