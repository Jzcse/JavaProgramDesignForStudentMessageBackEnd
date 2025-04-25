package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    //    private final PersonRepository personRepository; // 暂时没用到
    public CourseService(CourseRepository courseRepository, PersonRepository personRepository) {
        this.courseRepository = courseRepository;
//        this.personRepository = personRepository;
    }

    @Autowired
    private EntityManager entityManager;

    /*
     * 此处是老师写的，应该是获取课程列表
     * @param dataRequest = {numName}课程编号或课程名称
     * return DataResponse = {dataList}课程列表
     *
     * */
    public DataResponse getCourseList(DataRequest dataRequest) {
        Map<String, Object> dataRequestMap = dataRequest.getMap("course");
        String numName = CommonMethod.getString(dataRequestMap, "numName");
        if (numName == null)
            numName = "";
        Integer page = dataRequest.getInteger("page");
        Integer size = dataRequest.getInteger("size");
        //处理数据获取异常
        if (page == null || page < 0){
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
        Pageable pageable = PageRequest.of(page, size);
        List<Course> courseList = numName.isEmpty() ? courseRepository.findAll(pageable).getContent() : courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> courseMap;
        Course preCourse;
        for (Course course : courseList) {
            courseMap = new HashMap<>();
            courseMap.put("courseId", course.getCourseId() + "");
            courseMap.put("num", course.getNum());
            courseMap.put("name", course.getName());
            courseMap.put("credit", course.getCredit() + "");
            courseMap.put("coursePath", course.getCoursePath());
            courseMap.put("classroom", course.getClassroom());
            courseMap.put("dayOfWeek", course.getDayOfWeek());
            courseMap.put("time", course.getTime());
            preCourse = course.getPreCourse();
            if (preCourse != null) {
                courseMap.put("preCourseName", preCourse.getName());
                courseMap.put("preCourseId", preCourse.getCourseId());
            }
            dataList.add(courseMap);
        }
        return CommonMethod.getReturnData(dataList);
    }

    /*
    * 原版老师写的是新增和修改一体，我将分开，前端写的时候记得把原来sava改成add和update
    * /
    /*
    * * 新增：
    * @param dataRequest = {num,name,coursePath,credit,preCourseId}课程信息
    * return DataResponse
    * */
    @Transactional
    public DataResponse courseAdd(DataRequest dataRequest) {
        Map<String, Object> courseMap = dataRequest.getMap("course");
        String num = CommonMethod.getString(courseMap,"num");
        String name = CommonMethod.getString(courseMap,"name");
        String coursePath = CommonMethod.getString(courseMap,"coursePath");
        Integer credit = CommonMethod.getInteger(courseMap,"credit");
        String classroom = CommonMethod.getString(courseMap,"classroom");
        String dayOfWeek = CommonMethod.getString(courseMap,"dayOfWeek");
        String time = CommonMethod.getString(courseMap,"time");
        String preCourseName = CommonMethod.getString(courseMap,"preCourseName");
        if (preCourseName != null && preCourseName.equals(name)) {
            return CommonMethod.getReturnMessageError("先修课程不能是当前课程");
        }
        Course course = new Course();
        Course preCourse = preCourseName != null ?courseRepository.findByName(preCourseName).orElseThrow(() -> new RuntimeException("未找到先修课程")) : null;

        course.setNum(num);
        course.setName(name);
        course.setCoursePath(coursePath);
        course.setCredit(credit);
        course.setClassroom(classroom);
        course.setDayOfWeek(dayOfWeek);
        course.setTime(time);
        course.setPreCourse(preCourse);

        entityManager.persist(course);
        return CommonMethod.getReturnMessageOK();
    }

    /*
     * 修改：
     * @param dataRequest = {courseId,num,name,coursePath,credit,preCourseId}课程信息
     *  return DataResponse
     * */
    @Transactional
    public DataResponse courseUpdate(DataRequest dataRequest) {
        Map<String, Object> courseMap = dataRequest.getMap("course");
        Integer courseId = CommonMethod.getInteger(courseMap,"courseId");
        if (courseId == null) {
            return CommonMethod.getReturnMessageError("课程ID不能为空");
        }
        String num = CommonMethod.getString(courseMap,"num");
        String name = CommonMethod.getString(courseMap,"name");
        String coursePath = CommonMethod.getString(courseMap,"coursePath");
        Integer credit = CommonMethod.getInteger(courseMap,"credit");
        String classroom = CommonMethod.getString(courseMap,"classroom");
        String dayOfWeek = CommonMethod.getString(courseMap,"dayOfWeek");
        String time = CommonMethod.getString(courseMap,"time");
        String preCourseName = CommonMethod.getString(courseMap,"preCourseName");
        Course preCourse = null;

        Course course = courseRepository.findByNum(num).orElseThrow(() -> new RuntimeException("课程不存在"));
        if (preCourseName != null && preCourseName.equals(name)) {
            return CommonMethod.getReturnMessageError("先修课程不能是当前课程");
        }
        else if ( preCourseName != null && preCourseName != name) {
             preCourse = courseRepository.findByName(preCourseName).orElseThrow(() -> new RuntimeException("未找到先修课程"));
        }

        course.setNum(num);
        course.setName(name);
        course.setCoursePath(coursePath);
        course.setCredit(credit);
        course.setClassroom(classroom);
        course.setDayOfWeek(dayOfWeek);
        course.setTime(time);
        course.setPreCourse(preCourse);
        courseRepository.save(course);
        return CommonMethod.getReturnMessageOK();
    }

    /*
     * 删除：
     * @param dataRequest = {num}课程编号
     * return getReturnMessageOK()
     * */
    @Transactional
    public DataResponse courseDelete(DataRequest dataRequest) {
        Map<String, Object> courseMap = dataRequest.getMap("course");
        Integer courseId = CommonMethod.getInteger(courseMap,"courseId");
        if (courseId == null || courseId < 1) {
            return CommonMethod.getReturnMessageError("课程不存在");
        }
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("未找到该课程"));
        courseRepository.delete(course);
        return CommonMethod.getReturnMessageOK();
    }
    /*
    /添加课程关系：
    * @param dataRequest = {num,relatedNum,relationType}课程信息
    * return DataResponse
     */
    @Transactional
    public DataResponse addCourseRelation(DataRequest dataRequest) {
        Map<String, Object> courseRelationMap = dataRequest.getMap("courseRelation");
        String num = CommonMethod.getString(courseRelationMap,"num");
        String relatedNum = CommonMethod.getString(courseRelationMap,"relatedNum");
        String relationType = CommonMethod.getString(courseRelationMap,"relationType"); // 先修课程和互斥课程

        if (num == null || relatedNum == null || relationType == null) {
            return CommonMethod.getReturnMessageError("参数不能为空");
        }

        Course course = courseRepository.findByNum(num)
                .orElseThrow(() -> new RuntimeException("未找到主课程"));
        Course relatedCourse = courseRepository.findByNum(relatedNum)
                .orElseThrow(() -> new RuntimeException("未找到关联课程"));

        switch (relationType) {
            case "PRE":
                course.setPreCourse(relatedCourse);
                break;
            case "EXCLUSIVE":
                // 处理互斥关系，现在还未建立互斥课程的对象，暂时注释掉
                //
                break;
            default:
                return CommonMethod.getReturnMessageError("无效的关系类型");
        }

        entityManager.merge(course);
        return CommonMethod.getReturnMessageOK();
    }

    /*
     * 这个地方是删除课程关系的，暂时没用到，如果要有选择的删除，建议先把对应关系建立起来
     * @param dataRequest = {num,relatedNum,relationType}课程信息
     * return DataResponse
     *
     * */
    @Transactional
    public DataResponse removeCourseRelation(DataRequest dataRequest) {
        Map<String, Object> courseRelationMap = dataRequest.getMap("courseRelation");
        String num = CommonMethod.getString(courseRelationMap,"num");
        String relatedNum = CommonMethod.getString(courseRelationMap,"relatedNum");
        String relationType = CommonMethod.getString(courseRelationMap,"relationType"); // 先修课程和互斥课程

        if (num == null || relatedNum == null || relationType == null) {
            return CommonMethod.getReturnMessageError("参数不能为空");
        }

        Course course = courseRepository.findByNum(num)
                .orElseThrow(() -> new RuntimeException("未找到主课程"));

        switch (relationType) {
            case "PRE":
                course.setPreCourse(null);
                break;
            case "EXCLUSIVE":
                // 删除互斥关系
                // courseExclusionRepository.deleteByCourseAndRelatedCourse(course, relatedCourse);
                break;
            default:
                return CommonMethod.getReturnMessageError("无效的关系类型");
        }

        entityManager.merge(course);
        return CommonMethod.getReturnMessageOK();
    }

    /*
     * 获取课程关系：
     * @param dataRequest = {num}课程编号
     * return DataResponse = {preCourse,exclusiveCourses}课程关系
     *
     * */
    public DataResponse getCourseRelations(DataRequest dataRequest) {
        Map<String, Object> courseRelationMap = dataRequest.getMap("courseRelation");
        String num = CommonMethod.getString(courseRelationMap,"num");
        if (num == null) {
            return CommonMethod.getReturnMessageError("课程编号不能为空");
        }

        Course course = courseRepository.findByNum(num)
                .orElseThrow(() -> new RuntimeException("未找到该课程"));

        Map<String, Object> result = new HashMap<>();

        // 获取先修课程
        if (course.getPreCourse() != null) {
            result.put("preCourse", course.getPreCourse().getName());
        }

        // 获取互斥课程
        //if()
        // 暂时不写
        // List<Course> exclusiveCourses = courseExclusionRepository.findExclusiveCourses(course);
        // result.put("exclusiveCourses", exclusiveCourses);

        return CommonMethod.getReturnData(result);
    }
    /*
    /课程与教室，时间安排：
    * @param dataRequest = {num,classroom,dayOfWeek,startTime,endTime}课程信息
    * return DataResponse
     */
    @Transactional
    public DataResponse scheduleCourse(DataRequest dataRequest) {
        Map<String, Object> courseScheduleMap = dataRequest.getMap("courseSchedule");
        String num = CommonMethod.getString(courseScheduleMap,"num");
        String classroom = CommonMethod.getString(courseScheduleMap,"classroom");
        String dayOfWeek = CommonMethod.getString(courseScheduleMap,"dayOfWeek");
        String time = CommonMethod.getString(courseScheduleMap,"time");

        if (num == null || classroom == null || dayOfWeek == null || time == null ) {
            return CommonMethod.getReturnMessageError("参数不能为空");
        }

        Course course = courseRepository.findByNum(num).orElseThrow(() -> new RuntimeException("未找到该课程"));
        // 检查教室和时间是否冲突
        // 设置课程安排
        course.setClassroom(classroom);
        course.setDayOfWeek(dayOfWeek);
        course.setTime(time);

        entityManager.merge(course);
        return CommonMethod.getReturnMessageOK();
    }

    /*
    /获取课程与教室、时间安排：
    * @param dataRequest = {num}课程编号
    * return DataResponse = {classroom,dayOfWeek,startTime,endTime}课程安排
     */
    public DataResponse getCourseSchedule(DataRequest dataRequest) {
        Map<String, Object> courseScheduleMap = dataRequest.getMap("courseSchedule");
        String num = CommonMethod.getString(courseScheduleMap,"num");
        if (num == null || num.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程编号不能为空");
        }

        Course course = courseRepository.findByNum(num).orElseThrow(() -> new RuntimeException("未找到该课程"));

        Map<String, Object> schedule = new HashMap<>();
        schedule.put("classroom", course.getClassroom());
        schedule.put("dayOfWeek", course.getDayOfWeek());
        schedule.put("time", course.getTime());

        return CommonMethod.getReturnData(schedule);
    }
    public DataResponse getCourseId(DataRequest dataRequest) {
        Map<String, Object> courseMap = dataRequest.getMap("course");
        String num = CommonMethod.getString(courseMap,"num");
        String name = CommonMethod.getString(courseMap,"name");
        if (num == null) {
            return CommonMethod.getReturnMessageError("课程编号不能为空");
        }
        if (name == null) {
            return CommonMethod.getReturnMessageError("课程名称不能为空");
        }
        Course course = courseRepository.findByNumAndName(num, name).orElseThrow(() -> new RuntimeException("未找到该课程"));
        Map<String, Object> result = new HashMap<>();
        result.put("courseId", course.getCourseId());
        return CommonMethod.getReturnData(result);
    }


    public DataResponse getWeekDaysList(@Valid DataRequest dataRequest) {
        List<String> weekDays = Arrays.asList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");
        Map<String, List<Map<String, String>>> result = new HashMap<>();

        for (String day : weekDays) {
            List<Map<String, String>> dayCourses = new ArrayList<>();

            for (Course course : courseRepository.findByDayOfWeek(day)) {
                Map<String, String> courseInfo = new HashMap<>();
                courseInfo.put("name", course.getName());
                courseInfo.put("time", course.getTime());
                dayCourses.add(courseInfo);
            }

            result.put(day, dayCourses);
        }

        return CommonMethod.getReturnData(result);
    }
}