package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;

    @Column(nullable = false, length = 100)
    private String activityName;

    @Column(nullable = false)
    private String activityType;

    @Column(nullable = false)
    private String activityTime;

    @Column(length = 100)
    private String location;

    @Lob
    private String description;


}