package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
/**
 * courseCentre 课程中心实体类，保存每个课程的相关信息
 * Integer courseCentreId 主键
 * Course course 保存课程信息
 * String textbook 教材
 * String courseware 课件
 * String reference 参考书
 */
@Entity
@Table(	name = "courseCentre",
        uniqueConstraints = {
        })
@Data
public class courseCentre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_centre_id")
    private Integer courseCentreId;

    @OneToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @Column(name = "textbook")
    private String textbook;

    @Column(name = "courseware")
    private String courseware;

    @Column(name = "reference")
    private String reference;

    public Integer getCourseCentreId() {
        return courseCentreId;
    }

    public void setCourseCentreId(Integer courseCentreId) {
        this.courseCentreId = courseCentreId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTextbook() {
        return textbook;
    }

    public void setTextbook(String textbook) {
        this.textbook = textbook;
    }

    public String getCourseware() {
        return courseware;
    }

    public void setCourseware(String courseware) {
        this.courseware = courseware;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
