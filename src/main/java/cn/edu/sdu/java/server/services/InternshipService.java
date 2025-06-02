package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Internship;
import cn.edu.sdu.java.server.models.InternshipSpace;
import cn.edu.sdu.java.server.models.InternshipTime;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.InternshipRepository;
import cn.edu.sdu.java.server.repositorys.InternshipSpaceRepository;
import cn.edu.sdu.java.server.repositorys.InternshipTimeRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
@Service
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final StudentRepository studentRepository;
    private final InternshipTimeRepository internshipTimeRepository;
    private final InternshipSpaceRepository internshipSpaceRepository;

    public InternshipService(InternshipRepository InternshipRepository, StudentRepository studentRepository, InternshipTimeRepository InternshipTimeRepository, InternshipSpaceRepository InternshipSpaceRepository) {
        this.internshipRepository = InternshipRepository;
        this.studentRepository = studentRepository;
        this.internshipTimeRepository = InternshipTimeRepository;
        this.internshipSpaceRepository = InternshipSpaceRepository;
    }

    public OptionItemList getStudentItemOptionList(DataRequest dataRequest) {
        List<Student> studentList = studentRepository.findStudentListByNumName("");
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : studentList) {
            itemList.add(new OptionItem(s.getStudentId(), s.getPerson().getPersonId() + "", s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public DataResponse Query_getInternshipList(DataRequest dataRequest) {//把从前端获取的数据传到后端，其中给的名字还是那样
        Integer personId = (Integer) dataRequest.get("personId");//这里的命名还是和前端一样
        if (personId == null)
            personId = 0;
        List<Internship> InternshipList = internshipRepository.findByPersonId(personId);//通过这仨来查询数据库里面和这仨相关的数据
        //通过这仨来从数据库里找和这仨相关的数据，然后存储为Internship对象，每个对象都有相应的数据，再存储到List里面
        List<Map<String, Object>> dataList = new ArrayList<>();//创建一个List，把刚才数据库获取的数据存到这里面
        Map<String, Object> studentMap;
        for (Internship s : InternshipList) {//遍历刚才从服务器获取的数据，然后从里面找到各个Internship对象数据的值
            studentMap = new HashMap<>();//每次遍历都要把studentMap里面的数据刷新一下
            studentMap.put("InternshipId", s.getInternshipId() + "");
            studentMap.put("personId", s.getStudent().getPerson().getPersonId() + "");
            studentMap.put("studentNum", s.getStudent().getPerson().getNum());
            studentMap.put("studentName", s.getStudent().getPerson().getName());
            studentMap.put("className", s.getStudent().getClassName());
            studentMap.put("InternshipTime", s.getInternshipTime().getTime());
            studentMap.put("InternshipSpace", s.getInternshipSpace().getName());
            dataList.add(studentMap);
        }
        return CommonMethod.getReturnData(dataList);//返回封装好的实习表数据给前端
    }


    public DataResponse getInternshipList(DataRequest dataRequest) {
        List<Internship> InternshipList = internshipRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();//创建一个List，把刚才数据库获取的数据存到这里面
        Map<String, Object> InternshipMap;
        for (Internship s : InternshipList) {//遍历刚才从服务器获取的数据，然后从里面找到各个Internship对象数据的值
            InternshipMap = new HashMap<>();//每次遍历都要把studentMap里面的数据刷新一下
            InternshipMap.put("InternshipId", s.getInternshipId() + "");
            InternshipMap.put("personId", s.getStudent().getPerson().getPersonId() + "");
            InternshipMap.put("studentNum", s.getStudent().getPerson().getNum());
            InternshipMap.put("studentName", s.getStudent().getPerson().getName());
            InternshipMap.put("className", s.getStudent().getClassName());
            InternshipMap.put("InternshipTime", s.getInternshipTime().getTime());
            InternshipMap.put("InternshipSpace", s.getInternshipSpace().getName());
            dataList.add(InternshipMap);
        }
        return CommonMethod.getReturnData(dataList);//返回封装好的实习表数据给前端
    }

    public DataResponse addInternship(@Valid DataRequest dataRequest) {
        Integer personId = (Integer) dataRequest.get("personId");
        String internshipSpace = (String) dataRequest.get("InternshipSpace"); // 地点
        String internshipTime = (String) dataRequest.get("InternshipTime"); //
        Student student = internshipRepository.findStudentByPersonId(personId);
        Internship internship = new Internship();
        InternshipTime internshiptime = new InternshipTime();
        internshiptime.setTime(internshipTime);
        internshipTimeRepository.save(internshiptime);
        InternshipSpace internshipspace = new InternshipSpace();
        internshipspace.setName(internshipSpace);
        internshipSpaceRepository.save(internshipspace);

        internship.setInternshipSpace(internshipspace);
        internship.setInternshipTime(internshiptime);
        internship.setStudent(student);
        internshipRepository.save(internship);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse deleteInternship(@Valid DataRequest dataRequest) {
        Integer InternshipId = dataRequest.getInteger("InternshipId");

        internshipRepository.deleteById(InternshipId);

        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse editInternship(@Valid DataRequest dataRequest) {
        Integer internshipId = dataRequest.getInteger("InternshipId");
        Integer personId = (Integer) dataRequest.get("personId");
        String internshipSpace = (String) dataRequest.get("InternshipSpace"); // 地点
        String internshipTime = (String) dataRequest.get("InternshipTime"); //
        Student student = internshipRepository.findStudentByPersonId(personId);
        Internship internship =internshipRepository.findById(internshipId).get();
        internship.getInternshipTime().setTime(internshipTime);
        internshipTimeRepository.save(internship.getInternshipTime());
        internship.getInternshipSpace().setName(internshipSpace);
        internshipSpaceRepository.save(internship.getInternshipSpace());
        internship.setStudent(student);
        internshipRepository.save(internship);

        return CommonMethod.getReturnMessageOK();
    }
}


