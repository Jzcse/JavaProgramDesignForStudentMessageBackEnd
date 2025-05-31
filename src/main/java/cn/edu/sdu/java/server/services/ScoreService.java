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
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 默认两个id都是0，前端下拉框可以单独选择，另一个不选的话就只查询一个下拉框所选中的条件，两个都选是满足两个条件的查询
     * @param dataRequest
     * @return
     */
    @Transactional
    public DataResponse getScoreList(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if(studentId == null)
            studentId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if(courseId == null)
            courseId = 0;
        List<Score> studentList = null;
        if(studentId == 0 && courseId == 0) {
            studentList = scoreRepository.findAll();
        }
        if(studentId != 0 && courseId == 0) {
            studentList = scoreRepository.findByStudentPersonId(studentId);
        }
        if(studentId == 0 && courseId != 0) {
            studentList = scoreRepository.findByCourseCourseId(courseId);
        }
        if(studentId != 0 && courseId!= 0) {
            studentList = scoreRepository.findByStudentCourse(studentId,courseId);
        }
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> studentMap;
        for (Score s : studentList) {
            studentMap = new HashMap<>();
            if(s.getStudent() != null){
                studentMap.put("personId",s.getStudent().getStudentId()+"");
                studentMap.put("studentNum",s.getStudent().getPerson().getNum());
                studentMap.put("studentName",s.getStudent().getPerson().getName());
                studentMap.put("className",s.getStudent().getClassName());
            }
            studentMap.put("scoreId", s.getScoreId()+"");
            studentMap.put("courseId",s.getCourse().getCourseId()+"");
            studentMap.put("courseNum",s.getCourse().getNum());
            studentMap.put("courseName",s.getCourse().getName());
            studentMap.put("credit",""+s.getCourse().getCredit());
            studentMap.put("mark",""+s.getMark());
            studentMap.put("PerformanceMark",""+s.getMarkOfPerformance());
            studentMap.put("MidTermMark",""+s.getMarkOfMidTerm());
            studentMap.put("FinalTermMark",""+s.getMarkOfFinalTerm());
            studentMap.put("weightOfMidTerm",""+s.getWeightOfMidTerm());
            studentMap.put("weightOfFinalTerm",""+s.getWeightOfFinalTerm());
            studentMap.put("weightOfPerformance",""+s.getWeightOfPerformance());
            dataList.add(studentMap);
        }
        return CommonMethod.getReturnData(dataList);
    }
    @Transactional
    public DataResponse scoreSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer courseId = dataRequest.getInteger("courseId");
        String PerformanceMark = dataRequest.getString("PerformanceMark");
        String MidTermMark = dataRequest.getString("MidTermMark");
        String FinalTermMark = dataRequest.getString("FinalTermMark");

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
        String PerformanceWeight = score.getWeightOfPerformance();
        String MidTermWeight = score.getWeightOfMidTerm();
        String FinalTermWeight = score.getWeightOfFinalTerm();
        score.setMarkOfFinalTerm(FinalTermMark);
        score.setMarkOfMidTerm(MidTermMark);
        score.setMarkOfPerformance(PerformanceMark);
        score.setWeightOfFinalTerm(FinalTermWeight);
        score.setWeightOfMidTerm(MidTermWeight);
        score.setWeightOfPerformance(PerformanceWeight);
        double mark = Double.parseDouble(PerformanceMark)*Double.parseDouble(PerformanceWeight)+Double.parseDouble(MidTermMark)*Double.parseDouble(MidTermWeight)+Double.parseDouble(FinalTermMark)*Double.parseDouble(FinalTermWeight);
        score.setMark(String.valueOf(mark));
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
    public DataResponse setWeight(DataRequest dataRequest) {
        Map WeightMap = dataRequest.getMap("WeightMap");
        String scoreId = CommonMethod.getString(WeightMap,"scoreId");
        Optional<Score> originalScore;
        Score score;
        if (scoreId != null&&!scoreId.equals("")) {
            originalScore = scoreRepository.findById(Integer.parseInt(scoreId));
            if (originalScore.isPresent()) {
                score = originalScore.get();
                String PerformanceWeight = CommonMethod.getString(WeightMap,"PerformanceWeight");
                String MidTermWeight = CommonMethod.getString(WeightMap,"MidTermWeight");
                String FinalTermWeight = CommonMethod.getString(WeightMap,"FinalTermWeight");
                String PerformanceMark = CommonMethod.getString(WeightMap,"PerformanceMark");
                String MidTermMark = CommonMethod.getString(WeightMap,"MidTermMark");
                String FinalTermMark = CommonMethod.getString(WeightMap,"FinalTermMark");
                score.setWeightOfPerformance(PerformanceWeight);
                score.setWeightOfMidTerm(MidTermWeight);
                score.setWeightOfFinalTerm(FinalTermWeight);
                score.setMarkOfPerformance(PerformanceMark);
                score.setMarkOfMidTerm(MidTermMark);
                score.setMarkOfFinalTerm(FinalTermMark);
                score.setMark(String.valueOf(Double.parseDouble(PerformanceMark)*Double.parseDouble(PerformanceWeight)+Double.parseDouble(MidTermMark)*Double.parseDouble(MidTermWeight)+Double.parseDouble(FinalTermMark)*Double.parseDouble(FinalTermWeight)));
                scoreRepository.save(score);
            }
        }else{
            Score scoreAdd = new Score();
            String PerformanceWeight = CommonMethod.getString(WeightMap,"PerformanceWeight");
            String MidTermWeight = CommonMethod.getString(WeightMap,"MidTermWeight");
            String FinalTermWeight = CommonMethod.getString(WeightMap,"FinalTermWeight");
            String PerformanceMark = CommonMethod.getString(WeightMap,"PerformanceMark");
            String MidTermMark = CommonMethod.getString(WeightMap,"MidTermMark");
            String FinalTermMark = CommonMethod.getString(WeightMap,"FinalTermMark");
            String personId = CommonMethod.getString(WeightMap,"personId");
            String courseId = CommonMethod.getString(WeightMap,"courseId");

            if(!scoreRepository.findByStudentCourse(Integer.parseInt(personId), Integer.parseInt(courseId)).isEmpty())
                return CommonMethod.getReturnMessageError("该学生已经存在该课程成绩");

            Optional<Student> studentOptional = studentRepository.findById(Integer.parseInt(personId));
            if (studentOptional.isPresent()) {
                scoreAdd.setStudent(studentOptional.get());
            }
            Optional<Course> courseOptional = courseRepository.findById(Integer.parseInt(courseId));
            if (courseOptional.isPresent()) {
                scoreAdd.setCourse(courseOptional.get());
            }
            scoreAdd.setMarkOfPerformance(PerformanceMark);
            scoreAdd.setMarkOfMidTerm(MidTermMark);
            scoreAdd.setMarkOfFinalTerm(FinalTermMark);
            scoreAdd.setWeightOfPerformance(PerformanceWeight);
            scoreAdd.setWeightOfMidTerm(MidTermWeight);
            scoreAdd.setWeightOfFinalTerm(FinalTermWeight);
            scoreAdd.setMark(String.valueOf(Double.parseDouble(PerformanceMark)*Double.parseDouble(PerformanceWeight)+Double.parseDouble(MidTermMark)*Double.parseDouble(MidTermWeight)+Double.parseDouble(FinalTermMark)*Double.parseDouble(FinalTermWeight)));
            scoreRepository.save(scoreAdd);
        }
        return CommonMethod.getReturnMessageOK();
    }
}
