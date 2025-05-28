package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.courseCentre;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseCentreRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class CourseCentreService {
    private final CourseCentreRepository courseCentreRepository;
    private final CourseRepository courseRepository;  // 新增

    public CourseCentreService(CourseCentreRepository courseCentreRepository,
                             CourseRepository courseRepository) {  // 修改构造方法
        this.courseCentreRepository = courseCentreRepository;
        this.courseRepository = courseRepository;
    }
    public DataResponse addCourseCentre(@Valid @RequestBody DataRequest dataRequest) {
        Map<String, Object> courseCentre = (Map<String, Object>) dataRequest.getMap("courseCentre");
        String name = CommonMethod.getString(courseCentre, "courseCentreName");
        Course course = courseRepository.findByName(name).orElseThrow(() -> new RuntimeException("课程名称错误"));
        if (course == null) {
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(1);
            dataResponse.setMsg("课程名称错误");
            return dataResponse;
        }
        String textbook = CommonMethod.getString(courseCentre, "courseCentreTextbook");
        String courseware = CommonMethod.getString(courseCentre, "courseCentreCourseware");
        String reference = CommonMethod.getString(courseCentre, "courseCentreReference");
        courseCentre courseCentre1 = new courseCentre();
        courseCentre1.setCourse(course);
        courseCentre1.setTextbook(textbook);
        courseCentre1.setCourseware(courseware);
        courseCentre1.setReference(reference);
        courseCentreRepository.save(courseCentre1);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse deleteCourseCentre(@Valid @RequestBody DataRequest dataRequest) {
        Map<String, Object> courseCentre = (Map<String, Object>) dataRequest.getMap("courseCentre");
        String courseCentreId = CommonMethod.getString(courseCentre, "courseCentreId");
        courseCentreRepository.deleteById(Integer.parseInt(courseCentreId));
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getCourseCentreList(@Valid @RequestBody DataRequest dataRequest) {
        Map<String, Object> dataRequestMap = dataRequest.getMap("courseCentre");
        String name = CommonMethod.getString(dataRequestMap, "courseCentreName");
        if (name == null)
            name = "";
        Integer page = CommonMethod.getInteger(dataRequestMap, "page");
        Integer size = CommonMethod.getInteger(dataRequestMap, "pageSize");
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
       List<courseCentre> courseCentreList = name.isEmpty() ? courseCentreRepository.findAll(pageable).getContent() : courseCentreRepository.findByCourseNameContaining(name);  //数据库查询操作
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> courseCentreMap;
      for (courseCentre courseCentre1 : courseCentreList) {
          courseCentreMap = new HashMap<>();
          courseCentreMap.put("courseCentreId", courseCentre1.getCourseCentreId() + "");
          courseCentreMap.put("courseCentreName", courseCentre1.getCourse().getName());
          courseCentreMap.put("courseCentreTextbook", courseCentre1.getTextbook());
          courseCentreMap.put("courseCentreCourseware", courseCentre1.getCourseware());
          courseCentreMap.put("courseCentreReference", courseCentre1.getReference());
          dataList.add(courseCentreMap);
      }
        return CommonMethod.getReturnData(dataList);
    }
    /*

    /
     */
    public DataResponse getCourseCentreId(@Valid @RequestBody DataRequest dataRequest) {
        Map<String, Object> courseCentre = (Map<String, Object>) dataRequest.getMap("courseCentre");
        String name = CommonMethod.getString(courseCentre, "name");


        return CommonMethod.getReturnData(null);
    }

    public DataResponse updateCourseCentre(@Valid @RequestBody DataRequest dataRequest) {
        Map<String, Object> courseCentre = (Map<String, Object>) dataRequest.getMap("courseCentre");
        String courseCentreId = CommonMethod.getString(courseCentre, "courseCentreId");
        String name = CommonMethod.getString(courseCentre, "courseCentreName");
        Course course = courseRepository.findByName(name).orElseThrow(() -> new RuntimeException("课程名称错误"));
        if (course == null) {
            DataResponse dataResponse = new DataResponse();
            dataResponse.setCode(1);
            dataResponse.setMsg("课程名称错误");
            return dataResponse;
        }
        String textbook = CommonMethod.getString(courseCentre, "courseCentreTextbook");
        String courseware = CommonMethod.getString(courseCentre, "courseCentreCourseware");
        String reference = CommonMethod.getString(courseCentre, "courseCentreReference");
        courseCentre courseCentre1 = courseCentreRepository.findById(Integer.parseInt(courseCentreId)).orElseThrow(() -> new RuntimeException("课程中心不存在"));
        courseCentre1.setCourse(course);
        courseCentre1.setTextbook(textbook);
        courseCentre1.setCourseware(courseware);
        courseCentre1.setReference(reference);
        courseCentreRepository.save(courseCentre1);
        return CommonMethod.getReturnMessageOK();
    }
}
