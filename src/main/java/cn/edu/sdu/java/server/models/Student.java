package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;


/**
 * Student学生表实体类 保存每个学生的信息，
 * Integer personId 学生表 student 主键 person_id 与Person表主键相同
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String major 专业
 * String className 班级
 *
 */

 //在Java JPA中，支持将对Java的对象操作映射为数据库记录的操作，这里我们设计了一个实体类Student映射数据库的表student
@Data
@Entity //表示这是一个实体类
@Table(	name = "student", //映射到数据库为student，Table来指定与那个表对应
        uniqueConstraints = {
        })
public class Student {
    @Id //表明personId是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @OneToOne //表明是一对一的关系,一个学生对应一个Person对象
    @JoinColumn(name="person_id")
    @JsonIgnore
    private Person person;

    @Size(max = 20) //表明字段长度不能超过20
    private String major;

    @Size(max = 50)
    private String className;

    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();
    public void addCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }
}
