package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
@Data
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

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private String mark;

    private String ranking;
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


}