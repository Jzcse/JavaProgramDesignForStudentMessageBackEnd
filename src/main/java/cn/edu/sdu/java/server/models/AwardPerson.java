package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "award_person_list")
@Getter
@Setter
public class AwardPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_person_id")
    private Integer awardPersonId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_age")
    private Integer studentAge;

    @Column(name = "student_gander")
    private String studentGander;

    @Column(name = "student_email")
    private String studentEmail;

    @Column(name = "student_phone")
    private String studentPhone;

    @Column(name = "student_address")
    private String studentAddress;

    @Column(name = "student_dept")
    private String studentDept;

    @ManyToOne
    @JoinColumn(name = "award_id")
    private Award award;
}
