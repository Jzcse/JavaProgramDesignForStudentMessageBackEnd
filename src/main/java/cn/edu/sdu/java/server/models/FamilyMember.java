package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * memberId 成员主键
 * student 对应的学生
 * relation 与学生的关系
 * name 家长姓名
 * gender 性别 1 男 2 女
 * age 年龄
 * unit 工作单位
 */

@Setter
@Getter
@Entity
@Table(	name = "family_member",
        uniqueConstraints = {
        })
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="student_id")
    private Student student;

    @Size(max=10)
    private String relation;
    @Size(max=30)
    private String name;
    @Size(max=10)
    private String gender;
    private Integer age;
    @Size(max=50)
    private String unit;
}
