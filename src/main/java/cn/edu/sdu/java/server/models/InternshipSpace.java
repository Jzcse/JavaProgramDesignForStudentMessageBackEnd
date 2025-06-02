package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Internship_Space",
        uniqueConstraints = {})
@Getter
@Setter
public class InternshipSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer InternshipSpaceId;


    @Size(max = 50)
    private String name;
}

