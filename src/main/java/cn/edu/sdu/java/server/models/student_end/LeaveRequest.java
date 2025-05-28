package cn.edu.sdu.java.server.models.student_end;

import cn.edu.sdu.java.server.models.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

/**
 * leaveRequestId 主键
 * contact 联系方式
 * reason 请假原因
 * requestTime 申请时间
 * startTime 开始时间
 * endTime 结束时间
 * status 状态 0 未处理 1 已处理 2 已拒绝
 */

@Setter
@Getter
@Entity
@Table(name = "leave_request")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveRequestId;
    @Size(max = 30)
    @Column(name = "contact")
    private String contact;
    @Column(name = "reason")
    private String reason;
    @Column(name = "request_time")
    private String requestTime;
    @Column(name = "start_time")
    private String startTime;
    @Column(name = "end_time")
    private String endTime;
    @Column(name = "status")
    private Integer status;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
