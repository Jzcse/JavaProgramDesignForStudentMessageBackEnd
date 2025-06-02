package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Internship_Time",
        uniqueConstraints = {})
@Getter
@Setter
public class InternshipTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer InternshipTimeId;


    @Size(max = 50)
    private String time;

}
