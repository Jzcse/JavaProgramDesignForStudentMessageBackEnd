package cn.edu.sdu.java.server.repositorys.student_end;

import cn.edu.sdu.java.server.models.student_end.LeaveRequest;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    @Query("select leaveRequest from LeaveRequest leaveRequest where leaveRequest.student.person.num = :num")
    List<LeaveRequest> findLeaveRequestByStudentNum(@Param("num") String num);
}
