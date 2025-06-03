package cn.edu.sdu.java.server.repositorys.student_end;

import cn.edu.sdu.java.server.models.student_end.LeaveRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    @Query("select leaveRequest from LeaveRequest leaveRequest where leaveRequest.student.person.num = :num")
    List<LeaveRequest> findLeaveRequestByStudentNum(@Param("num") String num);

    @Query("select leaveRequest from LeaveRequest leaveRequest where leaveRequest.leaveRequestId = :leaveRequestId")
    LeaveRequest findLeaveRequestByLeaveRequestId(@Param("leaveRequestId") String leaveRequestId);

    @Query("select leaveRequest from LeaveRequest leaveRequest where leaveRequest.student.studentId = :studentId")
    List<LeaveRequest> getLeaveRequestListByStudentId(Integer studentId);
}
