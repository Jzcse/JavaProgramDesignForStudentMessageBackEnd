package cn.edu.sdu.java.server.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


// 奖项表实体类  保存奖项的的基本信息
// Integer awardId 奖项表 award 主键 award_id
// String awardName 奖项名称
// String awardType 奖项类型
// String awardLevel 奖项级别
// String awardTime 获奖时间
// String awardStudentList 获奖学生列表
@Entity
@Table(	name = "award",
        uniqueConstraints = {
        })
public class Award {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_id")
    @NotBlank
    private Integer awardId;

    @Setter
    @Getter
    @Column(name = "award_name")
    @Size(max = 50)
    @NotBlank
    private String awardName;

    @Setter
    @Getter
    @Column(name = "award_type")
    private String awardType;

    @Setter
    @Getter
    @Column(name = "award_level")
    private String awardLevel;

    @Setter
    @Getter
    @Column(name = "award_time")
    private String awardTime;

    @Setter
    @Getter
    @Column(name = "award_size")
    private String awardSize;


    @NotBlank
    @ManyToMany
    @JoinTable(
            name = "award_person",
            joinColumns = @JoinColumn(name = "award_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> awardStudentList;


    public @NotBlank List<Student> getAwardStudentList() {
        return awardStudentList;
    }

    public void setAwardStudentList(@NotBlank List<Student> awardStudentList) {
        this.awardStudentList = awardStudentList;
    }

    @Override
    public String toString() {
        return "Award{" +
                "awardId=" + awardId +
                ", awardName='" + awardName + '\'' +
                ", awardType='" + awardType + '\'' +
                ", awardLevel='" + awardLevel + '\'' +
                ", awardTime='" + awardTime + '\'' +
                ", awardStudentList=" + awardStudentList +
                '}';
    }

}
