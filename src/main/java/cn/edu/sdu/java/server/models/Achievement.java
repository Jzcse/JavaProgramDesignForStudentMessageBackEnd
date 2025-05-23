package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



/**
 * Achievement 成就实体类 保存成就的基本信息，
 * Integer achievementId 科技成就表 scientific_achievement 主键 achievement_id
 * String achievementName 科技成就名称
 * String description 科技成就描述
 * String achievementTime 科技成就时间
 * Person persons 科技成就所属人员
 */




@Entity
@Table(name = "achievement",
        uniqueConstraints = {
        })

public class  Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Integer achievementId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "achievement_name")
    private String achievementName;

    @Size(max = 500)
    @Column(name = "achievement_description")
    private String achievementDescription;

    @Column(name = "achievement_time")
    private String achievementTime;

    @Column(name = "achievement_personName")
    private String achievementPersonName;


    public Achievement(Integer achievementId, String achievementName, String achievementDescription, String achievementTime) {
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.achievementTime = achievementTime;
        this.achievementPersonName = achievementPersonName;

    }
    public Achievement() {

    }

    public Integer getAchievementId() {
        return achievementId;
    }



    public String getAchievementName() {
        return achievementName;
    }

    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }

    public String getAchievementDescription() {
        return achievementDescription;
    }

    public void setAchievementDescription(String achievementDescription) {
        this.achievementDescription = achievementDescription;
    }

    public String getAchievementTime() {
        return achievementTime;
    }

    public void setAchievementTime(String achievementTime) {
        this.achievementTime = achievementTime;
    }
    public String getAchievementPersonName() {
        return achievementPersonName;
    }
    public void setAchievementPersonName(String achievementPersonName) {
        this.achievementPersonName = achievementPersonName;
    }

}

