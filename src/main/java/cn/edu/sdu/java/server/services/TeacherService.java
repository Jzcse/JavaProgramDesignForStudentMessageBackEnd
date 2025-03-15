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

    private PasswordEncoder encoder;
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
        if (page <= 0){
            page = 1;
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
        List<Map<String, Map<String, String>>> mapList = new ArrayList<>();
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
            Map<String, Map<String, String>> map = new HashMap<>();
            map.put("teacherMap", teacherMap);
            map.put("personMap", personMap);
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
        String num = CommonMethod.getString(form, "num");
        Teacher t = null;
        Person p;
        User u;
        Optional<Teacher> op;
        if (personId != null) {
            op = teacherRepository.findById(personId);//查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                t = op.get();
            }
        }

        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (t != null && t.getPerson().getNum().equals(num)) {
                p = t.getPerson();
                personId = p.getPersonId();
                Optional<User> uOp = userRepository.findByPersonPersonId(personId);
                if (uOp.isPresent()) {
                    u = uOp.get();
                    u.setUserName(num);
                    userRepository.saveAndFlush(u);
                }
                p.setNum(num);
                p.setName(CommonMethod.getString(form, "name"));
                p.setDept(CommonMethod.getString(form, "dept"));
                p.setCard(CommonMethod.getString(form, "card"));
                p.setGender(CommonMethod.getString(form, "gender"));
                p.setBirthday(CommonMethod.getString(form, "birthday"));
                p.setEmail(CommonMethod.getString(form, "email"));
                p.setPhone(CommonMethod.getString(form, "phone"));
                p.setAddress(CommonMethod.getString(form, "address"));
                personRepository.save(p);  // 修改保存人员信息
                t.setMajor(CommonMethod.getString(form, "major"));
                t.setTitle(CommonMethod.getString(form, "title"));
                teacherRepository.save(t);  //修改保存学生信息
                systemService.modifyLog(t, true);
                return CommonMethod.getReturnData(t.getPersonId());  // 将personId返回前端
            }
        }
            return CommonMethod.getReturnMessageError("id不存在，不能添加或修改！");
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
                    List<Map<String, String>> rList = new ArrayList<>();
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
                    //to List
                    rList.add(personMap);
                    rList.add(teacherMap);
                    dataResponse.setData(rList);
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
                List<Map<String, Map<String, String>>> mapList = new ArrayList<>();
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
                    Map<String, Map<String, String>> map = new HashMap<>();
                    map.put("teacherMap", teacherMap);
                    map.put("personMap", personMap);
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
        User userPojo = new User();
        Teacher teacherPojo = new Teacher();
        Person personPojo = new Person();
        Map teacherMap = dataRequest.getMap("teacherMap");
        Map personMap = dataRequest.getMap("personMap");
        DataResponse dataResponse = new DataResponse();
        if (teacherMap == null || personMap == null){
            dataResponse.setCode(1);
            dataResponse.setMsg("教师信息或个人信息不能为空");
            dataResponse.setData(null);
            return dataResponse;
        } else {
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
}
