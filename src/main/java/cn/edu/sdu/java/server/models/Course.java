package cn.edu.sdu.java.server.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


/**
 * Course 课程表实体类  保存课程的的基本信息信息，
 * Integer courseId 人员表 course 主键 course_id
 * String num 课程编号
 * String name 课程名称
 * Integer credit 学分
 * Course preCourse 前序课程 pre_course_id 关联前序课程的主键 course_id
 */
@Entity
@Table(	name = "course",
        uniqueConstraints = {
        })
@Data
public class Course  {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;
    @NotBlank
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String name;
    private Integer credit;
    @ManyToOne
    @JoinColumn(name="pre_course_id")
    private Course preCourse;
    @Size(max = 12)
    private String coursePath;
    @Column(name = "classroom")
    private String classroom;
    @Column(name = "day_of_week")
    private String dayOfWeek;
    @Column(name = "time")
    private String time;
}
