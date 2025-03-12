package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
//    private final PersonRepository personRepository; // 暂时没用到
    public CourseService(CourseRepository courseRepository, PersonRepository personRepository) {
        this.courseRepository = courseRepository;
//        this.personRepository = personRepository;
    }

    public DataResponse getCourseList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Course> courseList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> courseMap;
        Course preCourse;
        for (Course course : courseList) {
            courseMap = new HashMap<>();
            courseMap.put("courseId", course.getCourseId()+"");
            courseMap.put("num",course.getNum());
            courseMap.put("name",course.getName());
            courseMap.put("credit",course.getCredit()+"");
            courseMap.put("coursePath",course.getCoursePath());
            preCourse =course.getPreCourse();
            if(preCourse != null) {
                courseMap.put("preCourse",preCourse.getName());
                    courseMap.put("preCourseId",preCourse.getCourseId());
            }
            dataList.add(courseMap);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse courseSave(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        Integer preCourseId = dataRequest.getInteger("preCourseId");
        Optional<Course> originalCourse;
        Course course= null;

        if(courseId != null) {
            originalCourse = courseRepository.findById(courseId);
            if(originalCourse.isPresent())
                course= originalCourse.get();
        }
        if(course== null)
            course = new Course();
        Course preCourse =null;
        if(preCourseId != null) {
            originalCourse = courseRepository.findById(preCourseId);
            if(originalCourse.isPresent())
                preCourse = originalCourse.get();
        }
        course.setNum(num);
        course.setName(name);
        course.setCredit(credit);
        course.setCoursePath(coursePath);
        course.setPreCourse(preCourse);
        courseRepository.save(course);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse courseDelete(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> originalCourse;
        Course course;
        if(courseId != null) {
            originalCourse = courseRepository.findById(courseId);
            if(originalCourse.isPresent()) {
                course = originalCourse.get();
                courseRepository.delete(course);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
