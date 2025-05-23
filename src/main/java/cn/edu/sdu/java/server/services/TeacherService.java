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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeacherService {

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    UserTypeRepository userTypeRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private SystemService systemService;

    /**
     * 获取老师信息列表
     * @param dataRequest data = { page, size }
     * @return dataResponse
     */
    public DataResponse getTeacherList(@Valid DataRequest dataRequest) {
        Integer page = dataRequest.getInteger("page");
        Integer size = dataRequest.getInteger("size");
        //处理数据获取异常
        if (page == null){
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(1);
            dataResponse.setMsg("页数不能为空");
            return dataResponse;
        }
        if (size == null){
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(1);
            dataResponse.setMsg("页面大小不能为空");
            return dataResponse;
        }
        //处理默认页数及大小关系
        if (page < 0){
            page = 0;
        }
        if (size < 10){
            size = 10;
        }
        if (size > 50){
            size = 50;
        }
        //get teacher list
        Pageable pageable = PageRequest.of(page, size);
        List<Teacher> teacherList = teacherRepository.findAll(pageable).getContent();
        //get teacher map list
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Teacher teacher : teacherList) {
            Person person = teacher.getPerson();
            //teacher
            Map<String, String> teacherMap = new HashMap<>();
            teacherMap.put("title", teacher.getTitle());
            teacherMap.put("major", teacher.getMajor());
            //person
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
            //to end map
            Map<String, String> map = new HashMap<>(teacherMap);
            map.putAll(personMap);
            //to list
            mapList.add(map);
        }
        DataResponse dataResponse = new DataResponse();
        dataResponse.setData(mapList);
        dataResponse.setCode(0);
        dataResponse.setMsg("获取教师信息成功");
        return dataResponse;
    }

    /**
     * 删除老师
     * @param dataRequest data = { personId }
     * @return dataResponse
     */
    public DataResponse deleteTeacher(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        DataResponse dataResponse = new DataResponse();
        if (personId == null){
            dataResponse.setCode(1);
            dataResponse.setMsg("用户ID为空！");
        } else {
            Teacher teacher = teacherRepository.findByPersonId(personId);
            if (teacher == null){
                dataResponse.setCode(1);
                dataResponse.setMsg("找不到该教师");
            } else {
                User user = userRepository.findByPersonId(personId);
                Person person = teacher.getPerson();
                userRepository.delete(user);
                teacherRepository.delete(teacher);
                personRepository.delete(person);
                dataResponse.setCode(0);
                dataResponse.setMsg("删除教师信息成功");
                dataResponse.setData(null);
            }
        }
        return dataResponse;
    }

    /**
     * 更改老师的个人信息
     * @param dataRequest {  }
     * @return dataResponse
     */
    public DataResponse editTeacherInfo(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map form = dataRequest.getMap("form");
        String num = CommonMethod.getString(form,"num");
        Teacher teacherTemp = teacherRepository.findByNum(num);
        if (teacherTemp == null || teacherTemp.getPerson().getPersonId().equals(personId)){
            Teacher teacher = teacherRepository.findByPersonId(personId);
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
            DataResponse dataResponse = new DataResponse();
            dataResponse.setMsg("保存成功!");
            dataResponse.setCode(0);
            return dataResponse;
        } else {
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(1);
            dataResponse.setMsg("工号不能和已有的重复");
            return dataResponse;
        }
    }

    /**
     * 获取一名老师的详细个人信息
     * @param dataRequest { personId }
     * @return dataResponse
     */
    public DataResponse getTeacherInfo (@Valid DataRequest dataRequest){
        Integer personId = dataRequest.getInteger("personId");
        DataResponse dataResponse = new DataResponse();
        if (personId == null) {
            dataResponse.setCode(1);
            dataResponse.setMsg("用户ID为空");
        } else {
            Optional<Person> person = personRepository.findById(personId);
            if (person.isPresent()) {
                Map<String, String> teacherMap = new HashMap<>();
                Map<String, String> personMap = new HashMap<>();
                //person
                Person personPojo = person.get();
                personMap.put("address", personPojo.getAddress());
                personMap.put("birthday", personPojo.getBirthday());
                personMap.put("card", personPojo.getCard());
                personMap.put("dept", personPojo.getDept());
                personMap.put("email", personPojo.getEmail());
                personMap.put("gender", personPojo.getGender());
                personMap.put("name", personPojo.getName());
                personMap.put("num", personPojo.getNum());
                personMap.put("type", personPojo.getType());
                personMap.put("phone", personPojo.getPhone());
                personMap.put("introduce", personPojo.getIntroduce());
                //teacher
                Teacher teacherPojo = teacherRepository.findByPersonId(personId);
                teacherMap.put("major", teacherPojo.getMajor());
                teacherMap.put("title", teacherPojo.getTitle());
                //to one map
                Map<String, String> map = new HashMap<>(teacherMap);
                map.putAll(personMap);
                dataResponse.setData(map);
                dataResponse.setMsg("success");
                dataResponse.setCode(0);
            } else {
                dataResponse.setCode(1);
                dataResponse.setMsg("未查找到该用户");
            }

        }
        return dataResponse;
    }

    /**
     * 通过名字查找老师
     * @param dataRequest { name }
     * @return dataResponse
     */
    public DataResponse searchTeacherByName (@Valid DataRequest dataRequest){
        String name = dataRequest.getString("name");
        DataResponse dataResponse = new DataResponse();
        if (name == null) {
            dataResponse.setCode(1);
            dataResponse.setMsg("名称为空!");
        } else {
            List<Teacher> teacherList = teacherRepository.findByTeacherName("%" + name + "%");
            //get teacher map list
            List<Map<String, String>> mapList = new ArrayList<>();
            for (Teacher teacher : teacherList) {
                Person person = teacher.getPerson();
                //teacher
                Map<String, String> teacherMap = new HashMap<>();
                teacherMap.put("title", teacher.getTitle());
                teacherMap.put("major", teacher.getMajor());
                //person
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
                //to end map
                Map<String, String> map = new HashMap<>(teacherMap);
                map.putAll(personMap);
                //to list
                mapList.add(map);
            }
            dataResponse.setData(mapList);
            dataResponse.setCode(0);
            dataResponse.setMsg("success");
        }
        return dataResponse;
    }

    /**
     * 添加老师
     * @param dataRequest { teacherMap, personMap }
     * @return dataResponse
     */
    public DataResponse addTeacher(@Valid DataRequest dataRequest) {
        Map teacherMap = dataRequest.getMap("teacherMap");
        Map personMap = dataRequest.getMap("personMap");
        DataResponse dataResponse = new DataResponse();
        if (teacherMap == null || personMap == null){
            dataResponse.setCode(1);
            dataResponse.setMsg("教师信息或个人信息不能为空");
            dataResponse.setData(null);
            return dataResponse;
        } else {
            String num = CommonMethod.getString(personMap, "num");
            Teacher teacherTemp = teacherRepository.findByNum(num);
            if (teacherTemp != null){
                dataResponse.setMsg("工号不能和已有的重复");
                dataResponse.setCode(1);
                return dataResponse;
            }
            User userPojo = new User();
            Teacher teacherPojo = new Teacher();
            Person personPojo = new Person();
            //person
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
            Integer personId = personPojo.getPersonId();
            //teacher
            teacherPojo.setMajor(CommonMethod.getString(teacherMap, "major"));
            teacherPojo.setTitle(CommonMethod.getString(teacherMap, "title"));
            teacherPojo.setPerson(personPojo);
            teacherRepository.save(teacherPojo);
            //user
            userPojo.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            userPojo.setUserName(CommonMethod.getString(personMap, "num"));
            userPojo.setUserType(userTypeRepository.findByName(EUserType.ROLE_TEACHER));
            userPojo.setPassword(encoder.encode("123456"));
            userPojo.setCreatorId(personId);
            userPojo.setLoginCount(0);
            userPojo.setPerson(personPojo);
            userRepository.save(userPojo);

            dataResponse.setCode(0);
            dataResponse.setData(null);
            dataResponse.setMsg("新增教师成功！");
            return dataResponse;
        }
    }

    /**
     * 通过工号查询老师
     * @param dataRequest { num }
     * @return dataResponse
     */
    public DataResponse searchTeacherByNum(@Valid DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        DataResponse dataResponse = new DataResponse();
        if (num == null) {
            dataResponse.setCode(1);
            dataResponse.setMsg("名称为空!");
        } else {
            List<Teacher> teacherList = teacherRepository.findByTeacherNum("%" + num + "%");
            //get teacher map list
            List<Map<String, String>> mapList = new ArrayList<>();
            for (Teacher teacher : teacherList) {
                Person person = teacher.getPerson();
                //teacher
                Map<String, String> teacherMap = new HashMap<>();
                teacherMap.put("title", teacher.getTitle());
                teacherMap.put("major", teacher.getMajor());
                //person
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
                //to end map
                Map<String, String> map = new HashMap<>(teacherMap);
                map.putAll(personMap);
                //to list
                mapList.add(map);
            }
            dataResponse.setData(mapList);
            dataResponse.setCode(0);
            dataResponse.setMsg("success");
        }
        return dataResponse;
    }

    /**
     * 获取教师记录数
     * @param dataRequest { null }
     * @return dataResponse
     */
    public DataResponse getTeacherCount(DataRequest dataRequest){
        long count = teacherRepository.count();
        String countString = Long.toString(count);
        //to map
        Map<String, String> map = new HashMap<>();
        map.put("count", countString);
        //to response
        DataResponse dataResponse = new DataResponse();
        dataResponse.setCode(0);
        dataResponse.setData(map);
        dataResponse.setMsg("success");
        return dataResponse;
    }
}
