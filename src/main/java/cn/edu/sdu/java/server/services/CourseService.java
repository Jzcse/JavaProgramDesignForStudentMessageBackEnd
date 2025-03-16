package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
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
        String numName = dataRequest.getString("numName");
        if (numName == null)
            numName = "";
        List<Course> courseList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
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
            courseMap.put("startTime", course.getStartTime());
            courseMap.put("endTime", course.getEndTime());
            preCourse = course.getPreCourse();
            if (preCourse != null) {
                courseMap.put("preCourse", preCourse.getName());
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
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        String classroom = dataRequest.getString("classroom");
        String dayOfWeek = dataRequest.getString("dayOfWeek");
        String startTime = dataRequest.getString("startTime");
        String endTime = dataRequest.getString("endTime");
        Integer preCourseId = dataRequest.getInteger("preCourseId");

        Course course = new Course();
        Course preCourse = preCourseId != null ? entityManager.getReference(Course.class, preCourseId) : new Course();

        course.setNum(num);
        course.setName(name);
        course.setCoursePath(coursePath);
        course.setCredit(credit);
        course.setClassroom(classroom);
        course.setDayOfWeek(dayOfWeek);
        course.setStartTime(startTime);
        course.setEndTime(endTime);
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
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null) {
            return CommonMethod.getReturnMessageError("课程ID不能为空");
        }

        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        String classroom = dataRequest.getString("classroom");
        String dayOfWeek = dataRequest.getString("dayOfWeek");
        String startTime = dataRequest.getString("startTime");
        String endTime = dataRequest.getString("endTime");
        Integer preCourseId = dataRequest.getInteger("preCourseId");

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
        Course preCourse = preCourseId != null ? entityManager.getReference(Course.class, preCourseId) : new Course();

        course.setNum(num);
        course.setName(name);
        course.setCoursePath(coursePath);
        course.setCredit(credit);
        course.setClassroom(classroom);
        course.setDayOfWeek(dayOfWeek);
        course.setStartTime(startTime);
        course.setEndTime(endTime);
        course.setPreCourse(preCourse);

        entityManager.merge(course);
        return CommonMethod.getReturnMessageOK();
    }

    /*
     * 删除：
     * @param dataRequest = {num}课程编号
     * return getReturnMessageOK()
     * */
    @Transactional
    public DataResponse courseDelete(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        if (num == null || num.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程不存在");
        }
        Course course = courseRepository.findByNum(num).orElseThrow(() -> new RuntimeException("课程不存在"));
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
        String num = dataRequest.getString("num");
        String relatedNum = dataRequest.getString("relatedNum");
        String relationType = dataRequest.getString("relationType"); // 先修课程和互斥课程

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
        String num = dataRequest.getString("num");
        String relatedNum = dataRequest.getString("relatedNum");
        String relationType = dataRequest.getString("relationType");

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
        String num = dataRequest.getString("num");
        if (num == null) {
            return CommonMethod.getReturnMessageError("课程ID不能为空");
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
        String num = dataRequest.getString("num");
        String classroom = dataRequest.getString("classroom");
        String dayOfWeek = dataRequest.getString("dayOfWeek");
        String startTime = dataRequest.getString("startTime");
        String endTime = dataRequest.getString("endTime");

        if (num == null || classroom == null || dayOfWeek == null || startTime == null || endTime == null) {
            return CommonMethod.getReturnMessageError("参数不能为空");
        }

        Course course = courseRepository.findByNum(num).orElseThrow(() -> new RuntimeException("未找到该课程"));
        // 检查教室和时间是否冲突
        if (isScheduleConflict(classroom, dayOfWeek, startTime, endTime)) {
            return CommonMethod.getReturnMessageError("时间或教室冲突");
        }

        // 设置课程安排
        course.setClassroom(classroom);
        course.setDayOfWeek(dayOfWeek);
        course.setStartTime(startTime);
        course.setEndTime(endTime);

        entityManager.merge(course);
        return CommonMethod.getReturnMessageOK();
    }

    private boolean isScheduleConflict(String classroom, String dayOfWeek, String startTime, String endTime) {
        // 查询该教室在指定日期的所有课程
        List<Course> scheduledCourses = courseRepository.findByClassroomAndDayOfWeek(classroom, dayOfWeek);

        for (Course course : scheduledCourses) {
            if (isTimeOverlap(startTime, endTime, course.getStartTime(), course.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        // 比较时间是否重叠
        return !(end1.compareTo(start2) <= 0 || start1.compareTo(end2) >= 0);
    }
    /*
    /获取课程与教室、时间安排：
    * @param dataRequest = {num}课程编号
    * return DataResponse = {classroom,dayOfWeek,startTime,endTime}课程安排
     */
    public DataResponse getCourseSchedule(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        if (num == null || num.isEmpty()) {
            return CommonMethod.getReturnMessageError("课程ID不能为空");
        }

        Course course = courseRepository.findByNum(num).orElseThrow(() -> new RuntimeException("未找到该课程"));

        Map<String, Object> schedule = new HashMap<>();
        schedule.put("classroom", course.getClassroom());
        schedule.put("dayOfWeek", course.getDayOfWeek());
        schedule.put("startTime", course.getStartTime());
        schedule.put("endTime", course.getEndTime());

        return CommonMethod.getReturnData(schedule);
    }


}