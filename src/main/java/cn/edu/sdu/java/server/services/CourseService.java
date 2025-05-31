package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import java.util.*;



@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final CourseChooseRepository courseChooseRepository;
    private final TeacherRepository teacherRepository;
    private final CourseTeachingRepository courseTeachingRepository;
    private final CourseCentreRepository courseCentreRepository; // 暂时没用到

    public CourseService( ScoreRepository scoreRepository,CourseCentreRepository courseCentreRepository,CourseTeachingRepository courseTeachingRepository,TeacherRepository teacherRepository,CourseRepository courseRepository, StudentRepository studentRepository, CourseChooseRepository courseChooseRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.scoreRepository = scoreRepository;
        this.courseChooseRepository = courseChooseRepository;
        this.teacherRepository = teacherRepository;
        this.courseTeachingRepository = courseTeachingRepository;
        this.courseCentreRepository = courseCentreRepository;
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
        if (page == null || page < 0) {
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(1);
            dataResponse.setMsg("页数不能为空");
            return dataResponse;
        }
        if (size == null) {
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(1);
            dataResponse.setMsg("页面大小不能为空");
            return dataResponse;
        }
        //处理默认页数及大小关系
        if (page < 0) {
            page = 0;
        }
        if (size < 10) {
            size = 10;
        }
        if (size > 50) {
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
        String num = CommonMethod.getString(courseMap, "num");
        String name = CommonMethod.getString(courseMap, "name");
        String coursePath = CommonMethod.getString(courseMap, "coursePath");
        Integer credit = CommonMethod.getInteger(courseMap, "credit");
        String classroom = CommonMethod.getString(courseMap, "classroom");
        String dayOfWeek = CommonMethod.getString(courseMap, "dayOfWeek");
        String time = CommonMethod.getString(courseMap, "time");
        String preCourseName = CommonMethod.getString(courseMap, "preCourseName");
        if (preCourseName != null && preCourseName.equals(name)) {
            return CommonMethod.getReturnMessageError("先修课程不能是当前课程");
        }
        Course course = new Course();
        Course preCourse = preCourseName != null ? courseRepository.findByName(preCourseName).orElseThrow(() -> new RuntimeException("未找到先修课程")) : null;

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
        Integer courseId = CommonMethod.getInteger(courseMap, "courseId");
        if (courseId == null) {
            return CommonMethod.getReturnMessageError("课程ID不能为空");
        }
        String num = CommonMethod.getString(courseMap, "num");
        String name = CommonMethod.getString(courseMap, "name");
        String coursePath = CommonMethod.getString(courseMap, "coursePath");
        Integer credit = CommonMethod.getInteger(courseMap, "credit");
        String classroom = CommonMethod.getString(courseMap, "classroom");
        String dayOfWeek = CommonMethod.getString(courseMap, "dayOfWeek");
        String time = CommonMethod.getString(courseMap, "time");
        String preCourseName = CommonMethod.getString(courseMap, "preCourseName");
        Course preCourse = null;

        Course course = courseRepository.findByNum(num).orElseThrow(() -> new RuntimeException("课程不存在"));
        if (preCourseName != null && preCourseName.equals(name)) {
            return CommonMethod.getReturnMessageError("先修课程不能是当前课程");
        } else if (preCourseName != null && preCourseName != name) {
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
        try {
            Map<String, Object> courseMap = dataRequest.getMap("course");
            String courseId1 = CommonMethod.getString(courseMap, "courseId");
            Integer courseId = Integer.parseInt(courseId1);
            Course sonCourse = courseRepository.findByPreCourseId(courseId);
            if (sonCourse != null) {
                String sonCourseName = sonCourse.getName();
                return CommonMethod.getReturnMessageError("该课程有子课程，不能删除"+"，请先删除子课程:"+sonCourseName);
            }
            Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("未找到该课程"));
            List<Student> students = courseChooseRepository.findStudentByCourse(courseId);
            List<Teacher> teachers = courseTeachingRepository.findTeacherByCourse(courseId);
            List<Score> scores = scoreRepository.findByCourseId(courseId);

            CourseChoose courseChoose;
            CourseTeaching courseTeaching;
            courseCentre courseCentre1 = courseCentreRepository.findByCourse(courseId);
            if (courseCentre1!= null) {
                courseCentreRepository.delete(courseCentre1);
            }
            for (Score score : scores) {
                scoreRepository.delete(score);
            }
            for (Student student : students) {
                courseChoose = courseChooseRepository.findByStudentCourse(student.getStudentId(), courseId);
                courseChooseRepository.delete(courseChoose);
            }
            for (Teacher teacher : teachers) {
             courseTeaching = courseTeachingRepository.findByCourseAndTeacher(teacher.getPersonId(), courseId);
            courseTeachingRepository.delete(courseTeaching);
        }
            courseRepository.delete(course);
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("删除失败");
        }

        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getCourseId(DataRequest dataRequest) {
        Map<String, Object> courseMap = dataRequest.getMap("course");
        String num = CommonMethod.getString(courseMap, "num");
        String name = CommonMethod.getString(courseMap, "name");
        if (num == null) {
            return CommonMethod.getReturnMessageError("课程编号不能为空");
        }
        if (name == null) {
            return CommonMethod.getReturnMessageError("课程名称不能为空");
        }
        Course course = courseRepository.findByNumAndName(num, name).orElseThrow(() -> new RuntimeException("未找到该课程"));
        Map<String, Object> result = new HashMap<>();
        result.put("courseId", course.getCourseId().toString());
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

    public DataResponse importCourses(@Valid DataRequest dataRequest) {
        List<Map<String, Object>> courses = (List<Map<String, Object>>) dataRequest.getList("courseList");
        for (Map<String, Object> courseData : courses) {
            String num = CommonMethod.getString(courseData, "num");
            String name = CommonMethod.getString(courseData, "name");
            String coursePath = CommonMethod.getString(courseData, "coursePath");
            Integer credit = CommonMethod.getInteger(courseData, "credit");
            String classroom = CommonMethod.getString(courseData, "classroom");
            String dayOfWeek = CommonMethod.getString(courseData, "dayOfWeek");
            String time = CommonMethod.getString(courseData, "time");
            String preCourseName = CommonMethod.getString(courseData, "preCourseName");
            if (preCourseName != null && preCourseName.equals(name)) {
                return CommonMethod.getReturnMessageError("先修课程不能是当前课程");
            }
            Course course = new Course();
            Course preCourse = preCourseName != null ? courseRepository.findByName(preCourseName).orElseThrow(() -> new RuntimeException("未找到先修课程")) : null;

            course.setNum(num);
            course.setName(name);
            course.setCoursePath(coursePath);
            course.setCredit(credit);
            course.setClassroom(classroom);
            course.setDayOfWeek(dayOfWeek);
            course.setTime(time);
            course.setPreCourse(preCourse);
            courseRepository.save(course);
        }
        return CommonMethod.getReturnMessageOK();
    }


// 然后在CourseService中实现选课方法：


    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public DataResponse selectCourse(DataRequest dataRequest) {
        Map<String, Object> selectionMap = (Map<String, Object>) dataRequest.getMap("selection");
        String courseId1 = CommonMethod.getString(selectionMap, "courseId");
        String studentId1 = CommonMethod.getString(selectionMap, "studentId");
        Integer courseId = Integer.parseInt(courseId1);
        Integer studentId = Integer.parseInt(studentId1);

        Course course = courseRepository.findByCourseId(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
        Student student = studentRepository.findByStudentId(studentId).orElseThrow(() -> new RuntimeException("学生不存在"));
        CourseChoose courseChoose = courseChooseRepository.findByStudentCourse(studentId, courseId);
        if (courseChoose != null) {
            return CommonMethod.getReturnMessageError("已选过该课程");
        }
        List<Course> originalCourses = courseChooseRepository.findCourseByStudent(studentId);
        for (Course course1 : originalCourses) {
            if (course1.getDayOfWeek().equals(course.getDayOfWeek()) && course1.getTime().equals(course.getTime())) {
                return CommonMethod.getReturnMessageError("与已选课程时间冲突");
            }
        }
        courseChoose = new CourseChoose();
        courseChoose.setCourse(course);
        courseChoose.setStudent(student);
        courseChooseRepository.save(courseChoose);
        return CommonMethod.getReturnMessageOK();
    }
//        try {
//            Map<String, Object> selectionMap = (Map<String, Object>) dataRequest.getMap("selection");
//            String courseId1 = CommonMethod.getString(selectionMap, "courseId");
//            String studentId1 = CommonMethod.getString(selectionMap, "studentId");
//            Integer courseId = Integer.parseInt(courseId1);
//            Integer studentId = Integer.parseInt(studentId1);
//            System.out.println("11111111111111111111111111111111111");
//
//            Course course = courseRepository.findByCourseId(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
//            System.out.println("222222222222222222222222222222222");
//            Student student = studentRepository.findByStudentId(studentId).orElseThrow(() -> new RuntimeException("学生不存在"));
//            System.out.println("333333333333333333333333333");
//
//            Set<Student> students = new HashSet<>(course.getStudents());
//            if (students.contains(student)) {
//                return CommonMethod.getReturnMessageError("已选过该课程");
//            }
//
//            System.out.println("44444444444444444444444444444");
//            List<Course> originalCourses = courseRepository.findCourseByStudent(studentId);
//            for (Course originalCourse : originalCourses) {
//                if (originalCourse.getDayOfWeek().equals(course.getDayOfWeek()) && originalCourse.getTime().equals(course.getTime())) {
//                    return CommonMethod.getReturnMessageError("与已选课程时间冲突");
//                }
//            }
//
//            System.out.println("55555555555555555555555555");
//            System.out.println("课程学生数量: " + course.getStudents().size());
//
//            System.out.println("66666666666666666666666666666666");
//            students.add(student);
//            System.out.println("7777777777777777777777777777777");
//            course.setStudents(students);
//
//            System.out.println("8888888888888888888888888888");
//            courseRepository.save(course);
//            System.out.println("999999999999999999999999999999999999");
//            return CommonMethod.getReturnMessageOK();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return CommonMethod.getReturnMessageError("选课失败");
//        }
//
//    }

    @Transactional
    public DataResponse dropCourse(DataRequest dataRequest) {
        Map<String, Object> selectionMap = (Map<String, Object>) dataRequest.getMap("selection");
        String courseId1 = CommonMethod.getString(selectionMap, "courseId");
        String studentId1 = CommonMethod.getString(selectionMap, "studentId");
        Integer courseId = Integer.parseInt(courseId1);
        Integer studentId = Integer.parseInt(studentId1);
        Course course = courseRepository.findByCourseId(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
        Student student = studentRepository.findByStudentId(studentId).orElseThrow(() -> new RuntimeException("学生不存在"));
        CourseChoose courseChoose = courseChooseRepository.findByStudentCourse(studentId, courseId);
        if (courseChoose == null) {
            return CommonMethod.getReturnMessageError("未选过该课程");
        }
        courseChooseRepository.delete(courseChoose);
        return CommonMethod.getReturnMessageOK();
//    Map<String, Object> selectionMap = (Map<String, Object>)dataRequest.getMap("selection");
//    String courseId1 = CommonMethod.getString(selectionMap, "courseId");
//    String studentId1 = CommonMethod.getString(selectionMap, "studentId");
//    Integer courseId = Integer.parseInt(courseId1);
//    Integer studentId = Integer.parseInt(studentId1);
//
//
//    Course course = courseRepository.findByCourseId(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
//    Student student = studentRepository.findByStudentId(studentId).orElseThrow(() -> new RuntimeException("学生不存在"));
//
//    if (!course.getStudents().contains(student)) {
//        return CommonMethod.getReturnMessageError("未选过该课程");
//    }
//
//    course.getStudents().remove(student);
//    courseRepository.save(course);
//    return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getStudentCourseList(@Valid DataRequest dataRequest) {
        Map<String, Object> selectionMap = dataRequest.getMap("selection");
        String studentId1 = CommonMethod.getString(selectionMap, "studentId");
        Integer studentId = Integer.parseInt(studentId1);


        List<String> weekDays = Arrays.asList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");
        Map<String, List<Map<String, String>>> result = new HashMap<>();

        for (String day : weekDays) {
            List<Map<String, String>> dayCourses = new ArrayList<>();

            for (Course course : courseChooseRepository.findByStudentAndDayOfWeek(studentId, day)) {
                Map<String, String> courseInfo = new HashMap<>();
                courseInfo.put("name", course.getName());
                courseInfo.put("time", course.getTime());
                dayCourses.add(courseInfo);
            }

            result.put(day, dayCourses);
        }

        return CommonMethod.getReturnData(result);
    }

    public DataResponse creditsCount(@Valid DataRequest dataRequest) {
        Map<String, Object> selectionMap = dataRequest.getMap("selection");
        String studentId1 = CommonMethod.getString(selectionMap, "studentId");
        Integer studentId = Integer.parseInt(studentId1);
        List<Course> courses = courseChooseRepository.findCourseByStudent(studentId);
        int totalCredits = 0;
        for (Course course : courses) {
            totalCredits += course.getCredit();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("totalCredits", totalCredits);
        return CommonMethod.getReturnData(result);
    }

    public DataResponse getStudentCourseListResult(@Valid DataRequest dataRequest) {
        Map<String, Object> selectionMap = dataRequest.getMap("selection");
        String studentId1 = CommonMethod.getString(selectionMap, "studentId");
        Integer studentId = Integer.parseInt(studentId1);

        List<Course> courses = courseChooseRepository.findCourseByStudent(studentId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Course course : courses) {
            Map<String, Object> courseInfo = new HashMap<>();
            courseInfo.put("courseId", course.getCourseId().toString());
            courseInfo.put("num", course.getNum().toString());
            courseInfo.put("name", course.getName());
            courseInfo.put("time", course.getTime());
            courseInfo.put("credit", course.getCredit());
            courseInfo.put("preCourseName", course.getPreCourse() != null ? course.getPreCourse().getName() : "");
            courseInfo.put("classroom", course.getClassroom());
            courseInfo.put("dayOfWeek", course.getDayOfWeek());
            result.add(courseInfo);
        }
        return CommonMethod.getReturnData(result);
    }

    public DataResponse getSingleCourse(@Valid DataRequest dataRequest) {
        Map<String, Object> courseMap = dataRequest.getMap("course");
        String courseId1 = CommonMethod.getString(courseMap, "courseId");
        Integer courseId = Integer.parseInt(courseId1);

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
        Map<String, Object> result = new HashMap<>();
        result.put("name", course.getName());
        result.put("num", course.getNum());
        result.put("coursePath", course.getCoursePath());
        result.put("credit", course.getCredit());
        result.put("classroom", course.getClassroom());
        result.put("dayOfWeek", course.getDayOfWeek());
        result.put("time", course.getTime());
        result.put("preCourseName", course.getPreCourse() != null ? course.getPreCourse().getName() : "");
        return CommonMethod.getReturnData(result);
    }


    public DataResponse getTeacherCourseList(@Valid DataRequest dataRequest) {
        Map<String, Object> selectionMap = dataRequest.getMap("selection");
        String teacherId1 = CommonMethod.getString(selectionMap, "teacherId");
        Integer teacherId = Integer.parseInt(teacherId1);

        List<String> weekDays = Arrays.asList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");
        Map<String, List<Map<String, String>>> result = new HashMap<>();

        for (String day : weekDays) {
            List<Map<String, String>> dayCourses = new ArrayList<>();

            for (Course course : courseTeachingRepository.findByTeacherAndDayOfWeek(teacherId, day)) {
                Map<String, String> courseInfo = new HashMap<>();
                courseInfo.put("name", course.getName());
                courseInfo.put("time", course.getTime());
                dayCourses.add(courseInfo);
            }

            result.put(day, dayCourses);
        }

        return CommonMethod.getReturnData(result);
    }

    public DataResponse addCourseTeacher(@Valid DataRequest dataRequest) {
        Map<String, Object> selectionMap = (Map<String, Object>) dataRequest.getMap("selection");
        String courseId1 = CommonMethod.getString(selectionMap, "courseId");
        String teacherId1 = CommonMethod.getString(selectionMap, "teacherId");
        Integer courseId = Integer.parseInt(courseId1);
        Integer teacherId = Integer.parseInt(teacherId1);

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new RuntimeException("教师不存在"));

        CourseTeaching courseTeaching = courseTeachingRepository.findByCourseAndTeacher(teacherId, courseId);
        if (courseTeaching != null) {
            return CommonMethod.getReturnMessageError("该课程已经有该教师");
        }
        List<Course> originalCourses = courseTeachingRepository.findByTeacher(teacherId);
        for (Course course1 : originalCourses) {
            if (course1.getDayOfWeek().equals(course.getDayOfWeek()) && course1.getTime().equals(course.getTime())) {
                return CommonMethod.getReturnMessageError("与已选课程时间冲突");
            }
        }
        courseTeaching = new CourseTeaching();
        courseTeaching.setCourse(course);
        courseTeaching.setTeacher(teacher);
        courseTeachingRepository.save(courseTeaching);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse deleteCourseTeacher(@Valid DataRequest dataRequest) {
        Map<String, Object> selectionMap = (Map<String, Object>) dataRequest.getMap("selection");
        String courseId1 = CommonMethod.getString(selectionMap, "courseId");
        String teacherId1 = CommonMethod.getString(selectionMap, "teacherId");
        Integer courseId = Integer.parseInt(courseId1);
        Integer teacherId = Integer.parseInt(teacherId1);

        CourseTeaching courseTeaching = courseTeachingRepository.findByCourseAndTeacher(teacherId, courseId);
        if (courseTeaching == null) {
            return CommonMethod.getReturnMessageError("该课程没有该教师");
        }
        courseTeachingRepository.delete(courseTeaching);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getTeacherCourseListResult(@Valid DataRequest dataRequest) {
        Map<String, Object> selectionMap = dataRequest.getMap("selection");
        String teacherId1 = CommonMethod.getString(selectionMap, "teacherId");
        Integer teacherId = Integer.parseInt(teacherId1);
        Integer studentTotal = 0;
        List<Course> courses = courseTeachingRepository.findByTeacher(teacherId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Course course : courses) {
            List<Student> students = courseChooseRepository.findStudentByCourse(course.getCourseId());
            studentTotal = 0;
            for (Student student : students) {
                studentTotal++;
            }
            Map<String, Object> courseInfo = new HashMap<>();
            courseInfo.put("studentTotal", studentTotal.toString());
            courseInfo.put("courseId", course.getCourseId().toString());
            courseInfo.put("num", course.getNum().toString());
            courseInfo.put("name", course.getName());
            courseInfo.put("time", course.getTime());
            courseInfo.put("credit", course.getCredit());
            courseInfo.put("preCourseName", course.getPreCourse() != null ? course.getPreCourse().getName() : "");
            courseInfo.put("classroom", course.getClassroom());
            courseInfo.put("dayOfWeek", course.getDayOfWeek());
            result.add(courseInfo);
        }
        return CommonMethod.getReturnData(result);
    }

    public DataResponse courseTotal(@Valid DataRequest dataRequest) {
        Map<String, Object> selectionMap = dataRequest.getMap("selection");
        String teacherId1 = CommonMethod.getString(selectionMap, "teacherId");
        Integer teacherId = Integer.parseInt(teacherId1);

        List<Course> courses = courseTeachingRepository.findByTeacher(teacherId);
        Integer totalCourse = 0;
        for (Course course : courses) {
            totalCourse += 1;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("totalCourse", totalCourse.toString());
        return CommonMethod.getReturnData(result);
    }

    public DataResponse getSingleCourseAndScore(@Valid DataRequest dataRequest) {
        Map<String, Object> courseMap = dataRequest.getMap("course");
        String courseId1 = CommonMethod.getString(courseMap, "courseId");
        Integer courseId = Integer.parseInt(courseId1);
        String weightOfPerformance;
        String weightOfMidTerm;
        String weightOfFinal;
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("课程不存在"));
        List<Score> scores = scoreRepository.findByCourseId(courseId);
        if (scores.isEmpty()) {
            return CommonMethod.getReturnMessageError("该课程没有成绩");
        }
        weightOfPerformance = scores.getFirst().getWeightOfPerformance();
        weightOfMidTerm = scores.getFirst().getWeightOfMidTerm();
        weightOfFinal = scores.getFirst().getWeightOfFinalTerm();
        Map<String, Object> result = new HashMap<>();
        result.put("name", course.getName());
        result.put("num", course.getNum());
        result.put("coursePath", course.getCoursePath());
        result.put("credit", course.getCredit());
        result.put("classroom", course.getClassroom());
        result.put("dayOfWeek", course.getDayOfWeek());
        result.put("time", course.getTime());
        result.put("weightOfPerformance", weightOfPerformance);
        result.put("weightOfMidTerm", weightOfMidTerm);
        result.put("weightOfFinal", weightOfFinal);
        result.put("preCourseName", course.getPreCourse() != null ? course.getPreCourse().getName() : "");
        return CommonMethod.getReturnData(result);
    }
}
