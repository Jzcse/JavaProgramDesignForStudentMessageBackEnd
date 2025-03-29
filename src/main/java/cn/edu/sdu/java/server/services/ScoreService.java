package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ScoreService {
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;

    public ScoreService(CourseRepository courseRepository, ScoreRepository scoreRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.scoreRepository = scoreRepository;
        this.studentRepository = studentRepository;
    }
    public OptionItemList getStudentItemOptionList( DataRequest dataRequest) {
        List<Student> studentList = studentRepository.findStudentListByNumName("");  //数据库查询操作
//        OptionItem item;这里暂时没用到
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : studentList) {
            itemList.add(new OptionItem( s.getStudentId(),s.getStudentId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
        List<Course> studentList = courseRepository.findAll();  //数据库查询操作
//        OptionItem item;这里暂时没用到
        List<OptionItem> itemList = new ArrayList<>();
        for (Course c : studentList) {
            itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"", c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse getScoreList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if(personId == null)
            personId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if(courseId == null)
            courseId = 0;
        List<Score> studentList = scoreRepository.findByStudentCourse(personId, courseId);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> studentMap;
        for (Score s : studentList) {
            studentMap = new HashMap<>();
            studentMap.put("scoreId", s.getScoreId()+"");
            studentMap.put("personId",s.getStudent().getStudentId()+"");
            studentMap.put("courseId",s.getCourse().getCourseId()+"");
            studentMap.put("studentNum",s.getStudent().getPerson().getNum());
            studentMap.put("studentName",s.getStudent().getPerson().getName());
            studentMap.put("className",s.getStudent().getClassName());
            studentMap.put("courseNum",s.getCourse().getNum());
            studentMap.put("courseName",s.getCourse().getName());
            studentMap.put("credit",""+s.getCourse().getCredit());
            studentMap.put("mark",""+s.getMark());
            dataList.add(studentMap);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse scoreSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer mark = dataRequest.getInteger("mark");
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> originalScore;
        Score score = null;
        if(scoreId != null) {
            originalScore= scoreRepository.findById(scoreId);
            if(originalScore.isPresent())
                score = originalScore.get();
        }
        if(score == null) {
            score = new Score();
            //将原来的学生和课程信息保存到score对象中，建立联系，保证数据库无对应id时可抛出对应异常
            Optional<Student> studentOptional = studentRepository.findById(personId);
            if (studentOptional.isPresent()) {
                score.setStudent(studentOptional.get());
            } else {
                throw new RuntimeException("未找到对应学生，ID：" + personId);
            }

            Optional<Course> courseOptional = courseRepository.findById(courseId);
            if (courseOptional.isPresent()) {
                score.setCourse(courseOptional.get());
            } else {
                throw new RuntimeException("未找到对应课程，ID：" + courseId);
            }
        }
        score.setMark(mark);
        scoreRepository.save(score);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse scoreDelete(DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> originalScore;
        Score score;
        if(scoreId != null) {
            originalScore= scoreRepository.findById(scoreId);
            if(originalScore.isPresent()) {
                score = originalScore.get();
                scoreRepository.delete(score);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
