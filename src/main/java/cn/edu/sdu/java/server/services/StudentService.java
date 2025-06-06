package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.models.student_end.LeaveRequest;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.repositorys.student_end.LeaveRequestRepository;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Service  //spring的Bean
public class StudentService {

    @Autowired
    StudentStatisticsRepository studentStatisticsRepository;
    @Autowired
    AchievementRepository achievementRepository;
    @Autowired
    CourseChooseRepository courseChooseRepository;
    @Autowired
    AwardRepository awardRepository;
    @Autowired
    AwardPersonRepository awardPersonRepository;

    private final PersonRepository personRepository;  //人员数据操作自动注入
    private final StudentRepository studentRepository;  //学生数据操作自动注入
    private final UserRepository userRepository;  //学生数据操作自动注入
    private final UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    private final PasswordEncoder encoder;  //密码服务自动注入
    private final ScoreRepository scoreRepository;  //成绩数据操作自动注入
    private final FeeRepository feeRepository;  //消费数据操作自动注入
    private final LeaveRequestRepository leaveRequestRepository;
    private final InternshipRepository internshipRepository;
    private FamilyMemberRepository familyMemberRepository;
    private final BaseService baseService;   //基本数据处理数据操作自动注入
    private final SystemService systemService;
    @Autowired
    private ActivityRepository activityRepository;

    public StudentService(PersonRepository personRepository, StudentRepository studentRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder, ScoreRepository scoreRepository, FeeRepository feeRepository, FamilyMemberRepository familyMemberRepository, BaseService baseService, SystemService systemService, LeaveRequestRepository leaveRequestRepository, InternshipRepository internshipRepository) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.encoder = encoder;
        this.scoreRepository = scoreRepository;
        this.feeRepository = feeRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.baseService = baseService;
        this.systemService = systemService;
        this.leaveRequestRepository = leaveRequestRepository;
        this.internshipRepository = internshipRepository;
    }

    public Map getMapFromStudent(Student s) {
        Map m = new HashMap();
        Person p;
        if(s == null)
            return m;
        m.put("major",s.getMajor());
        m.put("className",s.getClassName());
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("personId", p.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }

    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， StudentController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // StudentController中的方法可以直接使用

    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(getMapFromStudent(sList.get(i)));
        }
        return dataList;
    }

    public DataResponse getStudentList(DataRequest dataRequest) {
        String num = dataRequest.getString("numName");
        List dataList = getStudentMapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }



    public DataResponse studentDelete(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");  //获取student_id值
        Student s = null;
        Optional<Student> op;
        if (personId != null) {
            op = studentRepository.findByPersonId(personId);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            Optional<User> uOp = userRepository.findByPersonPersonId(s.getPerson().getPersonId()); //查询对应该学生的账户
            if (uOp.isPresent()) {
                userRepository.delete(uOp.get()); //删除对应该学生的账户
            }

            // 删除学生的家庭成员信息
            List<FamilyMember> familyMembers = familyMemberRepository.findByStudentPersonId(personId);
            familyMemberRepository.deleteAll(familyMembers);

            // 删除学生的请假信息
            List<LeaveRequest> leaveRequests = leaveRequestRepository.getLeaveRequestListByStudentId(s.getStudentId());
            leaveRequestRepository.deleteAll(leaveRequests);

            // 删除学生的消费信息
            List<Fee> fees = feeRepository.findListByStudent(personId);
            feeRepository.deleteAll(fees);

            // 删除学生的校外实习信息
            List<Internship> internships = internshipRepository.findByPersonId(personId);
            internshipRepository.deleteAll(internships);

            // 删除学生的统计信息
            List<StudentStatistics> studentStatistics = studentStatisticsRepository.findByStudentId(s.getStudentId());

            // 删除学生的成绩信息
            List<Score> scores = scoreRepository.findByStudentId(s.getStudentId());
            scoreRepository.deleteAll(scores);

            // 删除学生的选课信息
            List<CourseChoose> courseChooses = courseChooseRepository.findByStudentId(s.getStudentId());
            courseChooseRepository.deleteAll(courseChooses);

            // 删除学生的奖项信息
            List<AwardPerson> awardPersons = awardPersonRepository.findByStudentId(s.getStudentId());
            awardPersonRepository.deleteAll(awardPersons);



            Person p = s.getPerson();
            studentRepository.delete(s);    //首先数据库永久删除学生信息
            personRepository.delete(p);   // 然后数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getStudentInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Student student = null;
        if (personId != null) {
            student = studentRepository.findStudentByPersonId(personId.toString()); //根据学生主键从数据库查询学生的信息
        }
        return CommonMethod.getReturnData(getMapFromStudent(student)); //这里回传包含学生信息的Map对象
    }

    /**
     * 添加新的学生
     * @param dataRequest { map }
     * @return dataResponse
     */
    public DataResponse addStudent(DataRequest dataRequest){
        Map<String, String> map = dataRequest.getMap("map");
        String name = CommonMethod.getString(map, "name");
        String num = CommonMethod.getString(map, "num");
        DataResponse dataResponse = new DataResponse();
        if (name.isEmpty() || num.isEmpty()){
            dataResponse.setMsg("名称或学号不能为空");
            dataResponse.setCode(1);
            return dataResponse;
        }
        Student student = studentRepository.findStudentByPersonNum(num);
        if (student != null) {
            dataResponse.setCode(1);
            dataResponse.setMsg("该学号已存在，无法添加");
            return dataResponse;
        }
        student = new Student();
        Person person = new Person();
        User user = new User();
        student.setClassName(CommonMethod.getString(map, "className"));
        student.setMajor(CommonMethod.getString(map, "major"));
        person.setNum(CommonMethod.getString(map, "num"));
        person.setName(CommonMethod.getString(map, "name"));
        person.setDept(CommonMethod.getString(map, "dept"));
        person.setPhone(CommonMethod.getString(map, "phone"));
        person.setGender(CommonMethod.getString(map, "gender"));
        person.setEmail(CommonMethod.getString(map, "email"));
        person.setCard(CommonMethod.getString(map, "card"));
        person.setType("1");
        person.setAddress(CommonMethod.getString(map, "address"));
        person.setBirthday(CommonMethod.getString(map, "birthday"));
        personRepository.save(person);
        Integer personId = person.getPersonId();
        user.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT));
        user.setUserName(num);
        user.setCreateTime(DateTimeTool.parseDateTime(new Date()));
        user.setPassword(encoder.encode("123456"));
        user.setCreatorId(personId);
        user.setLoginCount(0);
        user.setPerson(person);
        userRepository.save(user);
        student.setPerson(person);
        studentRepository.save(student);
        dataResponse.setMsg("添加成功");
        dataResponse.setCode(0);
        return dataResponse;
    }

    /**
     * 修改已有学生的相关信息
     * @param dataRequest { map }
     * @return dataResponse
     */
    public DataResponse editStudentInfo(DataRequest dataRequest){
        Map<String, String> map = dataRequest.getMap("map");
        String num = CommonMethod.getString(map, "num");
        String name = CommonMethod.getString(map, "name");
        DataResponse dataResponse = new DataResponse();
        if ( num.isEmpty() || name.isEmpty() ){
            dataResponse.setCode(1);
            dataResponse.setMsg("名称或学号不能为空");
            return dataResponse;
        }
        String personId = CommonMethod.getString(map, "personId");
        Student student = studentRepository.findStudentByPersonNum(num);
        if (student == null || student.getPerson().getPersonId().toString().equals(personId)){
            student = studentRepository.findStudentByPersonId(personId);
            Person person = student.getPerson();
            person.setNum(CommonMethod.getString(map, "num"));
            person.setName(CommonMethod.getString(map, "name"));
            person.setDept(CommonMethod.getString(map, "dept"));
            person.setPhone(CommonMethod.getString(map, "phone"));
            person.setGender(CommonMethod.getString(map, "gender"));
            person.setEmail(CommonMethod.getString(map, "email"));
            person.setCard(CommonMethod.getString(map, "card"));
            person.setType("1");
            person.setAddress(CommonMethod.getString(map, "address"));
            person.setBirthday(CommonMethod.getString(map, "birthday"));
            student.setClassName(CommonMethod.getString(map, "className"));
            student.setMajor(CommonMethod.getString(map, "major"));
            personRepository.save(person);
            studentRepository.save(student);
            dataResponse.setMsg("success");
            dataResponse.setCode(0);
        } else {
            dataResponse.setCode(1);
            dataResponse.setMsg("该学号已存在，无法修改");
        }
        return dataResponse;
    }

    public DataResponse studentEditSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String num = CommonMethod.getString(form, "num");  //Map 获取属性的值
        Student s = null;
        Person p;
        User u;
        Optional<Student> op;
        boolean isNew = false;
        if (personId != null) {
            op = studentRepository.findById(personId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        Optional<Person> nOp = personRepository.findByNum(num); //查询是否存在num的人员
        if (nOp.isPresent()) {
            if (s == null || !s.getPerson().getNum().equals(num)) {
                return CommonMethod.getReturnMessageError("新学号已经存在，不能添加或修改！");
            }
        }
        if (s == null) {
            p = new Person();
            p.setNum(num);
            p.setType("1");
            personRepository.saveAndFlush(p);  //插入新的Person记录
            personId = p.getPersonId();
            String password = encoder.encode("123456");
            u = new User();
            u.setPersonId(personId);
            u.setUserName(num);
            u.setPassword(password);
            u.setUserType(userTypeRepository.findByName(EUserType.ROLE_STUDENT));
            u.setCreateTime(DateTimeTool.parseDateTime(new Date()));
            u.setCreatorId(CommonMethod.getPersonId());
            userRepository.saveAndFlush(u); //插入新的User记录
            s = new Student();   // 创建实体对象
            s.setStudentId(personId);
            studentRepository.saveAndFlush(s);  //插入新的Student记录
            isNew = true;
        } else {
            p = s.getPerson();
        }
        personId = p.getPersonId();
        if (!num.equals(p.getNum())) {   //如果人员编号变化，修改人员编号和登录账号
            Optional<User> uOp = userRepository.findByPersonPersonId(personId);
            if (uOp.isPresent()) {
                u = uOp.get();
                u.setUserName(num);
                userRepository.saveAndFlush(u);
            }
            p.setNum(num);  //设置属性
        }
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(p);  // 修改保存人员信息
        s.setMajor(CommonMethod.getString(form, "major"));
        s.setClassName(CommonMethod.getString(form, "className"));
        studentRepository.save(s);  //修改保存学生信息
        systemService.modifyLog(s,isNew);
        return CommonMethod.getReturnData(s.getStudentId());  // 将personId返回前端
    }

    public List getStudentScoreList(List<Score> sList) {
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for (Score s : sList) {
            m = new HashMap();
            c = s.getCourse();
            m.put("studentNum", s.getStudent().getPerson().getNum());
            m.put("scoreId", s.getScoreId());
            m.put("courseNum", c.getNum());
            m.put("courseName", c.getName());
            m.put("credit", c.getCredit());
            m.put("mark", s.getMark());
            m.put("ranking", s.getRanking());
            list.add(m);
        }
        return list;
    }


    public List getStudentMarkList(List<Score> sList) {
        String title[] = {"优", "良", "中", "及格", "不及格"};
        int count[] = new int[5];
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for (Score s : sList) {
            c = s.getCourse();
            if (Integer.parseInt(s.getMark())>= 90)
                count[0]++;
            else if (Integer.parseInt(s.getMark()) >= 80)
                count[1]++;
            else if (Integer.parseInt(s.getMark()) >= 70)
                count[2]++;
            else if (Integer.parseInt(s.getMark()) >= 60)
                count[3]++;
            else
                count[4]++;
        }
        for (int i = 0; i < 5; i++) {
            m = new HashMap();
            m.put("name", title[i]);
            m.put("title", title[i]);
            m.put("value", count[i]);
            list.add(m);
        }
        return list;
    }


    public List getStudentFeeList(Integer personId) {
        List<Fee> sList = feeRepository.findListByStudent(personId);  // 查询某个学生消费记录集合
        List list = new ArrayList();
        if (sList == null || sList.size() == 0)
            return list;
        Map m;
        Course c;
        for (Fee s : sList) {
            m = new HashMap();
            m.put("title", s.getDay());
            m.put("value", s.getMoney());
            list.add(m);
        }
        return list;
    }




    public String importFeeData(Integer personId, InputStream in){
        try {
            Student student = studentRepository.findById(personId).get();
            XSSFWorkbook workbook = new XSSFWorkbook(in);  //打开Excl数据流
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row row;
            Cell cell;
            int i;
            i = 1;
            String day, money;
            Optional<Fee> fOp;
            Double dMoney;
            Fee f;
            rowIterator.next();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                cell = row.getCell(0);
                if (cell == null)
                    break;
                day = cell.getStringCellValue();  //获取一行消费记录 日期 金额
                cell = row.getCell(1);
                money = cell.getStringCellValue();
                fOp = feeRepository.findByStudentPersonIdAndDay(personId, day);  //查询是否存在记录
                if (!fOp.isPresent()) {
                    f = new Fee();
                    f.setDay(day);
                    f.setStudent(student);  //不存在 添加
                } else {
                    f = fOp.get();  //存在 更新
                }
                if (money != null && money.length() > 0)
                    dMoney = Double.parseDouble(money);
                else
                    dMoney = 0d;
                f.setMoney(dMoney);
                feeRepository.save(f);
            }
            workbook.close();  //关闭Excl输入流
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传错误！";
        }

    }

    public DataResponse importFeeData(@RequestBody byte[] barr,
                                      String personIdStr
                                      ) {
        Integer personId =  Integer.parseInt(personIdStr);
        String msg = importFeeData(personId,new ByteArrayInputStream(barr));
        if(msg == null)
            return CommonMethod.getReturnMessageOK();
        else
            return CommonMethod.getReturnMessageError(msg);
    }

    public ResponseEntity<StreamingResponseBody> getStudentListExcl( DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List list = getStudentMapList(numName);
        Integer widths[] = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30};
        int i, j, k;
        String titles[] = {"序号", "学号", "姓名", "学院", "专业", "班级", "证件号码", "性别", "出生日期", "邮箱", "电话", "地址"};
        String outPutSheetName = "student.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle styleTitle = CommonMethod.createCellStyle(wb, 20);
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for (j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }
        //合并第一行
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = null;
        XSSFCell cell[] = new XSSFCell[widths.length];
        row = sheet.createRow((int) 0);
        for (j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
            cell[j].getCellStyle();
        }
        Map m;
        if (list != null && list.size() > 0) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                m = (Map) list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m, "num"));
                cell[2].setCellValue(CommonMethod.getString(m, "name"));
                cell[3].setCellValue(CommonMethod.getString(m, "dept"));
                cell[4].setCellValue(CommonMethod.getString(m, "major"));
                cell[5].setCellValue(CommonMethod.getString(m, "className"));
                cell[6].setCellValue(CommonMethod.getString(m, "card"));
                cell[7].setCellValue(CommonMethod.getString(m, "genderName"));
                cell[8].setCellValue(CommonMethod.getString(m, "birthday"));
                cell[9].setCellValue(CommonMethod.getString(m, "email"));
                cell[10].setCellValue(CommonMethod.getString(m, "phone"));
                cell[11].setCellValue(CommonMethod.getString(m, "address"));
            }
        }
        try {
            StreamingResponseBody stream = outputStream -> {
                wb.write(outputStream);
            };
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    public DataResponse getStudentPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List dataList = new ArrayList();
        Page<Student> page = null;
        Pageable pageable = PageRequest.of(cPage, size);
        page = studentRepository.findStudentPageByNumName(numName, pageable);
        Map m;
        Student s;
        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List list = page.getContent();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    s = (Student) list.get(i);
                    m = getMapFromStudent(s);
                    dataList.add(m);
                }
            }
        }
        HashMap data = new HashMap();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }



    /*
        FamilyMember
     */
    public DataResponse getFamilyMemberList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        List<FamilyMember> fList = familyMemberRepository.findByPersonId(personId.toString());
        List dataList = new ArrayList();
        Map m;
        if (fList != null) {
            for (FamilyMember f : fList) {
                m = new HashMap();
                m.put("memberId", f.getMemberId().toString());
                m.put("personId", f.getStudent().getStudentId());
                m.put("relation", f.getRelation());
                m.put("name", f.getName());
                m.put("gender", f.getGender());
                m.put("age", f.getAge()+"");
                m.put("unit", f.getUnit());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse familyMemberSave(DataRequest dataRequest) {
        Map form = dataRequest.getMap("form");
        Integer personId = CommonMethod.getInteger(form,"personId");
        Integer memberId = CommonMethod.getInteger(form,"memberId");
        Optional<FamilyMember> op;
        FamilyMember f = null;
        if(memberId != null) {
            op = familyMemberRepository.findById(memberId);
            if(op.isPresent()) {
                f = op.get();
            }
        }
        if(f== null) {
            f = new FamilyMember();
            f.setStudent(studentRepository.findById(personId).get());
        }
        f.setRelation(CommonMethod.getString(form,"relation"));
        f.setName(CommonMethod.getString(form,"name"));
        f.setGender(CommonMethod.getString(form,"gender"));
        f.setAge(CommonMethod.getInteger(form,"age"));
        f.setUnit(CommonMethod.getString(form,"unit"));
        familyMemberRepository.save(f);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse familyMemberDelete(DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        Optional<FamilyMember> op;
        op = familyMemberRepository.findById(memberId);
        if(op.isPresent()) {
            familyMemberRepository.delete(op.get());
        }
        return CommonMethod.getReturnMessageOK();
    }


    public DataResponse importFeeDataWeb(Map request,MultipartFile file) {
        Integer personId = CommonMethod.getInteger(request, "personId");
        try {
            String msg= importFeeData(personId,file.getInputStream());
            if(msg == null)
                return CommonMethod.getReturnMessageOK();
            else
                return CommonMethod.getReturnMessageError(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonMethod.getReturnMessageError("上传错误！");
    }

}
