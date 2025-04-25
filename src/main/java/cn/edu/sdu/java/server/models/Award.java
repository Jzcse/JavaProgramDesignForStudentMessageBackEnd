package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "award")
@Getter
@Setter
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_id")
    private Integer awardId;

    @Column(name = "award_name")
    @Size(max = 50)
    private String awardName;

    @Column(name = "award_type")
    private String awardType;

    @Column(name = "award_level")
    private String awardLevel;

    @Column(name = "award_time")
    private String awardTime;

    @Column(name = "award_size")
    private Integer awardSize;

    @OneToMany(mappedBy = "award", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AwardPerson> awardStudentList = new ArrayList<>();
}
