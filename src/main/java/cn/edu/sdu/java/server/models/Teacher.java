package cn.edu.sdu.java.server.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Value;

/**
 * teacher 教师实体类，保存每个教师的信息
 * Integer personId 主键 与person表主键相同
 * Person person 保存个人相关信息
 * String major 专业
 * String title 职称
 */

@Table(name = "teacher")
@Entity
public class Teacher {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Integer personId;

    @Setter
    @Getter
    @OneToOne
    @JoinColumn(name = "person_id")
    @JsonIgnore
    private Person person;

    @Size(max = 20)
    private String major;

    @Size(max = 15)
    private String title;

    public @Size(max = 20) String getMajor() {
        return major;
    }

    public void setMajor(@Size(max = 20) String major) {
        this.major = major;
    }

    public @Size(max = 15) String getTitle() {
        return title;
    }

    public void setTitle(@Size(max = 15) String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "personId=" + personId +
                ", person=" + person +
                ", major='" + major + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
