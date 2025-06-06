package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(	name = "voluntaryWork",
        uniqueConstraints = {
        })
public class VoluntaryWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    @Getter
    @Setter
    private Integer workId;

    @Column(name = "work_name")
    @Getter
    @Setter
    private String workName;

    @Column(name = "work_type")
    @Getter
    @Setter
    private String workType;

    @Column(name = "work_level")
    @Getter
    @Setter
    private String workLevel;

    @Column(name = "work_time")
    @Getter
    @Setter
    private String workTime;

    @Column(name = "work_size")
    @Getter
    @Setter
    private String workSize;

    @Override
    public String toString() {
        return "VoluntaryWork{" +
                "workId=" + workId +
                ", workName='" + workName + '\'' +
                ", workType='" + workType + '\'' +
                ", workLevel='" + workLevel + '\'' +
                ", workTime='" + workTime + '\'' +
                ", workSize=" + workSize +
                '}';
    }
}
