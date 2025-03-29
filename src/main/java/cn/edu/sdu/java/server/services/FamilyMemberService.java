package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.FamilyMember;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.FamilyMemberRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FamilyMemberService {
    @Autowired
    FamilyMemberRepository familyMemberRepository;

    @Autowired
    StudentRepository studentRepository;

    /**
     * 为某个学生添加家庭成员
     * @param dataRequest { Map 包括 *关系 年龄 单位 *姓名 性别 学生ID }
     * @return DataResponse
     */
    public DataResponse addFamilyMember(DataRequest dataRequest) {
        Map<String, String> map = dataRequest.getMap("map");
        String relation = CommonMethod.getString(map, "relation");
        Integer age = Integer.parseInt(CommonMethod.getString(map, "age"));
        String name = CommonMethod.getString(map, "name");
        String unit = CommonMethod.getString(map, "unit");
        String gender = CommonMethod.getString(map, "gender");
        String personId = CommonMethod.getString(map, "personId");
        if (name.isEmpty() || relation.isEmpty()){
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(1);
            dataResponse.setMsg("姓名或关系不能为空");
            return dataResponse;
        } else {
            Student student = studentRepository.findStudentByPersonId(personId);
            FamilyMember familyMember = new FamilyMember();
            familyMember.setStudent(student);
            familyMember.setName(name);
            familyMember.setAge(age);
            familyMember.setUnit(unit);
            familyMember.setRelation(relation);
            familyMember.setGender(gender);
            familyMemberRepository.save(familyMember);
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(0);
            return dataResponse;
        }
    }

    /**
     * 删除某个学生的家庭成员
     * @param dataRequest { Map 包括学生ID 以及 家长的MemberID }
     * @return DataResponse
     */
    public DataResponse deleteFamilyMember(DataRequest dataRequest) {
        String memberId = dataRequest.getString("memberId");
        String personId = dataRequest.getString("personId");
        FamilyMember familyMember = familyMemberRepository.findByStudentPersonIdAndMemberId(personId, memberId);
        familyMemberRepository.delete(familyMember);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setCode(0);
        dataResponse.setMsg("success");
        return dataResponse;
    }

    /**
     * 修改某个学生的某个家庭成员的信息
     * @param dataRequest { Map 包括 *关系 年龄 单位 *姓名 性别 学生ID MemberID }
     * @return DataResponse
     */
    public DataResponse editFamilyMemberInfo(DataRequest dataRequest) {
        Map<String, String> map = dataRequest.getMap("map");
        String relation = CommonMethod.getString(map, "relation");
        Integer age = Integer.parseInt(CommonMethod.getString(map, "age"));
        String name = CommonMethod.getString(map, "name");
        String unit = CommonMethod.getString(map, "unit");
        String gender = CommonMethod.getString(map, "gender");
        String personId = CommonMethod.getString(map, "personId");
        String memberId = CommonMethod.getString(map, "memberId");
        FamilyMember familyMember = familyMemberRepository.findByStudentPersonIdAndMemberId(personId, memberId);
        familyMember.setGender(gender);
        familyMember.setName(name);
        familyMember.setAge(age);
        familyMember.setUnit(unit);
        familyMember.setRelation(relation);
        familyMemberRepository.save(familyMember);
        DataResponse dataResponse = new DataResponse();
        dataResponse.setCode(0);
        dataResponse.setMsg("success");
        return dataResponse;
    }
}
