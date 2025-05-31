package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

/**
 * Score 成绩表实体类  保存成绩的的基本信息信息，
 * Integer scoreId 人员表 score 主键 score_id
 * Student student 关联学生 student_id 关联学生的主键 student_id
 * Course course 关联课程 course_id 关联课程的主键 course_id
 * Integer mark 成绩
 * Integer ranking 排名
 */
@Entity
@Table(	name = "score",
        uniqueConstraints = {
        })
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scoreId;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String mark;

    //平时分
    private String markOfPerformance;
    //期中考试分
    private String markOfMidTerm;
    //期末考试分
    private String markOfFinalTerm;
    //平时分权重
    private String weightOfPerformance;
    //期中考权重
    private String weightOfMidTerm;
    //期末考权重
    private String weightOfFinalTerm;

    public String getMarkOfPerformance() {
        return markOfPerformance;
    }

    public void setMarkOfPerformance(String markOfPerformance) {
        this.markOfPerformance = markOfPerformance;
    }

    public String getMarkOfFinalTerm() {
        return markOfFinalTerm;
    }

    public void setMarkOfFinalTerm(String markOfFinalTerm) {
        this.markOfFinalTerm = markOfFinalTerm;
    }

    public String getWeightOfPerformance() {
        return weightOfPerformance;
    }

    public void setWeightOfPerformance(String weightOfPerformance) {
        this.weightOfPerformance = weightOfPerformance;
    }

    public String getWeightOfMidTerm() {
        return weightOfMidTerm;
    }

    public void setWeightOfMidTerm(String weightOfMidTerm) {
        this.weightOfMidTerm = weightOfMidTerm;
    }

    public String getWeightOfFinalTerm() {
        return weightOfFinalTerm;
    }

    public void setWeightOfFinalTerm(String weightOfFinalTerm) {
        this.weightOfFinalTerm = weightOfFinalTerm;
    }

    public String getMarkOfMidTerm() {
        return markOfMidTerm;
    }

    public void setMarkOfMidTerm(String markOfMidTerm) {
        this.markOfMidTerm = markOfMidTerm;
    }

    private Integer ranking;


    public Integer getScoreId() {
        return scoreId;
    }

    public void setScoreId(Integer scoreId) {
        this.scoreId = scoreId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
}