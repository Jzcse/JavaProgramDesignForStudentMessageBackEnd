package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(	name = "courseTeaching",
        uniqueConstraints = {
        })
@Data
public class CourseTeaching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_teaching_id")
    private Integer courseTeachingId;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
