package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(	name = "courseChoose",
        uniqueConstraints = {
        })
@Data
public class CourseChoose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_choose_id")
    private Integer courseChooseId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
