package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Inter_Ship",
        uniqueConstraints = {})
@Getter
@Setter
public class Internship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer InternshipId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "InternshipTime_id")
    private  InternshipTime InternshipTime;
    @ManyToOne
    @JoinColumn(name = "InternshipSpace_id")
    private InternshipSpace InternshipSpace;
}
