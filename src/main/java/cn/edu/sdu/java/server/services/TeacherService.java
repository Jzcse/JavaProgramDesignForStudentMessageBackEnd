package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.repositorys.UserRepository;
import cn.edu.sdu.java.server.repositorys.UserTypeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import cn.edu.sdu.java.server.dto.TeacherDTO;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private SystemService systemService;

    @Autowired
    private PdfExportService pdfExportService;

    /**
     * 获取老师信息列表
     */
    public DataResponse getTeacherList(@Valid DataRequest dataRequest) {
        Integer page = dataRequest.getInteger("page");
        Integer size = dataRequest.getInteger("size");

        if (page == null) {
            return errorResponse("页数不能为空");
        }
        if (size == null) {
            return errorResponse("页面大小不能为空");
        }

        page = Math.max(page, 0);
        size = Math.max(size, 10);
        size = Math.min(size, 50);

        Pageable pageable = PageRequest.of(page, size);
        List<Teacher> teacherList = teacherRepository.findAll(pageable).getContent();

        List<Map<String, String>> mapList = teacherList.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

        return successResponse(mapList, "获取教师信息成功");
    }

    /**
     * 删除老师
     */
    public DataResponse deleteTeacher(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if (personId == null) {
            return errorResponse("用户ID为空！");
        }

        Teacher teacher = teacherRepository.findByPersonId(personId);
        if (teacher == null) {
            return errorResponse("找不到该教师");
        }

        User user = userRepository.findByPersonId(personId);
        Person person = teacher.getPerson();

        userRepository.delete(user);
        teacherRepository.delete(teacher);
        personRepository.delete(person);

        return successResponse(null, "删除教师信息成功");
    }

    /**
     * 添加老师
     */
    public DataResponse addTeacher(@Valid DataRequest dataRequest) {
        Map teacherMap = dataRequest.getMap("teacherMap");
        Map personMap = dataRequest.getMap("personMap");

        if (teacherMap == null || personMap == null) {
            return errorResponse("教师信息或个人信息不能为空");
        }

        String num = CommonMethod.getString(personMap, "num");
        if (teacherRepository.findByNum(num) != null) {
            return errorResponse("工号不能和已有的重复");
        }

        User userPojo = new User();
        Teacher teacherPojo = new Teacher();
        Person personPojo = new Person();

        // 设置 Person 信息
        personPojo.setType("2");
        personPojo.setAddress(CommonMethod.getString(personMap, "address"));
        personPojo.setBirthday(CommonMethod.getString(personMap, "birthday"));
        personPojo.setCard(CommonMethod.getString(personMap, "card"));
        personPojo.setDept(CommonMethod.getString(personMap, "dept"));
        personPojo.setEmail(CommonMethod.getString(personMap, "email"));
        personPojo.setGender(CommonMethod.getString(personMap, "gender"));
        personPojo.setIntroduce(CommonMethod.getString(personMap, "introduce"));
        personPojo.setName(CommonMethod.getString(personMap, "name"));
        personPojo.setPhone(CommonMethod.getString(personMap, "phone"));
        personPojo.setNum(CommonMethod.getString(personMap, "num"));
        personRepository.save(personPojo);

        // 设置 Teacher 信息
        teacherPojo.setMajor(CommonMethod.getString(teacherMap, "major"));
        teacherPojo.setTitle(CommonMethod.getString(teacherMap, "title"));
        teacherPojo.setPerson(personPojo);
        teacherRepository.save(teacherPojo);

        // 设置 User 信息
        Integer personId = personPojo.getPersonId();
        userPojo.setCreateTime(DateTimeTool.parseDateTime(new Date()));
        userPojo.setUserName(CommonMethod.getString(personMap, "num"));
        userPojo.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER));
        userPojo.setPassword(encoder.encode("123456"));
        userPojo.setCreatorId(personId);
        userPojo.setLoginCount(0);
        userPojo.setPerson(personPojo);
        userRepository.save(userPojo);

        return successResponse(null, "新增教师成功！");
    }

    /**
     * 更改老师的个人信息
     */
    public DataResponse editTeacherInfo(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map form = dataRequest.getMap("form");

        if (personId == null || form == null) {
            return errorResponse("参数不能为空");
        }

        String num = CommonMethod.getString(form, "num");
        Teacher teacherTemp = teacherRepository.findByNum(num);
        if (teacherTemp != null && !teacherTemp.getPerson().getPersonId().equals(personId)) {
            return errorResponse("工号不能和已有的重复");
        }

        Teacher teacher = teacherRepository.findByPersonId(personId);
        if (teacher == null) {
            return errorResponse("找不到该教师");
        }

        Person person = teacher.getPerson();
        teacher.setTitle(CommonMethod.getString(form, "title"));
        teacher.setMajor(CommonMethod.getString(form, "major"));
        person.setName(CommonMethod.getString(form, "name"));
        person.setDept(CommonMethod.getString(form, "dept"));
        person.setCard(CommonMethod.getString(form, "card"));
        person.setGender(CommonMethod.getString(form, "gender"));
        person.setBirthday(CommonMethod.getString(form, "birthday"));
        person.setEmail(CommonMethod.getString(form, "email"));
        person.setPhone(CommonMethod.getString(form, "phone"));
        person.setAddress(CommonMethod.getString(form, "address"));
        person.setNum(CommonMethod.getString(form, "num"));

        personRepository.save(person);
        teacherRepository.save(teacher);

        return successResponse(null, "保存成功!");
    }

    /**
     * 获取一名老师的详细个人信息
     */
    public DataResponse getTeacherInfo(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if (personId == null) {
            return errorResponse("用户ID为空");
        }

        Optional<Person> personOptional = personRepository.findById(personId);
        if (!personOptional.isPresent()) {
            return errorResponse("未查找到该用户");
        }

        Person person = personOptional.get();
        Teacher teacher = teacherRepository.findByPersonId(personId);

        Map<String, String> teacherMap = new HashMap<>();
        teacherMap.put("major", teacher.getMajor());
        teacherMap.put("title", teacher.getTitle());

        Map<String, String> personMap = new HashMap<>();
        personMap.put("address", person.getAddress());
        personMap.put("birthday", person.getBirthday());
        personMap.put("card", person.getCard());
        personMap.put("dept", person.getDept());
        personMap.put("email", person.getEmail());
        personMap.put("gender", person.getGender());
        personMap.put("name", person.getName());
        personMap.put("num", person.getNum());
        personMap.put("type", person.getType());
        personMap.put("phone", person.getPhone());
        personMap.put("introduce", person.getIntroduce());

        Map<String, String> map = new HashMap<>(teacherMap);
        map.putAll(personMap);

        return successResponse(map, "success");
    }

    /**
     * 通过名字查找老师
     */
    public DataResponse searchTeacherByName(@Valid DataRequest dataRequest) {
        String name = dataRequest.getString("name");
        if (name == null) {
            return errorResponse("名称为空!");
        }

        List<Teacher> teacherList = teacherRepository.findByTeacherName("%" + name + "%");
        List<Map<String, String>> mapList = teacherList.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

        return successResponse(mapList, "success");
    }

    /**
     * 通过工号查询老师
     */
    public DataResponse searchTeacherByNum(@Valid DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        if (num == null) {
            return errorResponse("名称为空!");
        }

        List<Teacher> teacherList = teacherRepository.findByTeacherNum("%" + num + "%");
        List<Map<String, String>> mapList = teacherList.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());

        return successResponse(mapList, "success");
    }

    /**
     * 获取教师记录数
     */
    public DataResponse getTeacherCount(DataRequest dataRequest) {
        long count = teacherRepository.count();
        Map<String, String> map = Collections.singletonMap("count", String.valueOf(count));
        return successResponse(map, "success");
    }

    /**
     * 获取所有教师（用于导出）
     */
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 导出教师信息为 PDF
     */
    public void exportTeachersToPdf(HttpServletResponse response) throws IOException {
        List<TeacherDTO> teachers = getAllTeachers();
        pdfExportService.exportTeachersToPdf(teachers, response);
    }

    /**
     * 将 Teacher 实体转换为 DTO
     */
    private TeacherDTO convertToDTO(Teacher teacher) {
        Person person = teacher.getPerson();
        return new TeacherDTO(
                teacher.getPersonId(),
                person.getNum(),
                person.getName(),
                person.getDept(),
                teacher.getMajor(),
                teacher.getTitle(),
                person.getGender(),
                person.getEmail(),
                person.getPhone()
        );
    }

    /**
     * 将 Teacher 实体转换为 Map
     */
    private Map<String, String> convertToMap(Teacher teacher) {
        Person person = teacher.getPerson();
        Map<String, String> teacherMap = new HashMap<>();
        teacherMap.put("title", teacher.getTitle());
        teacherMap.put("major", teacher.getMajor());

        Map<String, String> personMap = new HashMap<>();
        personMap.put("address", person.getAddress());
        personMap.put("birthday", person.getBirthday());
        personMap.put("card", person.getCard());
        personMap.put("dept", person.getDept());
        personMap.put("email", person.getEmail());
        personMap.put("gender", person.getGender());
        personMap.put("name", person.getName());
        personMap.put("num", person.getNum());
        personMap.put("type", person.getType());
        personMap.put("phone", person.getPhone());
        personMap.put("introduce", person.getIntroduce());
        personMap.put("personId", person.getPersonId().toString());

        Map<String, String> map = new HashMap<>(teacherMap);
        map.putAll(personMap);
        return map;
    }

    /**
     * 辅助方法：创建成功响应
     */
    private DataResponse successResponse(Object data, String msg) {
        DataResponse response = new DataResponse();
        response.setData(data);
        response.setCode(0);
        response.setMsg(msg);
        return response;
    }

    /**
     * 辅助方法：创建错误响应
     */
    private DataResponse errorResponse(String msg) {
        DataResponse response = new DataResponse();
        response.setCode(1);
        response.setMsg(msg);
        return response;
    }
}