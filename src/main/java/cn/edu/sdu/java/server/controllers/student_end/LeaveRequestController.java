package cn.edu.sdu.java.server.controllers.student_end;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.student_end.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/leaveRequest")
public class LeaveRequestController {
    @Autowired
    LeaveRequestService leaveRequestService;

    /**
     * 添加请假申请
     * 权限：学生
     */
    @PostMapping("/addLeaveRequest")
    @PreAuthorize("hasRole = 'STUDENT'")
    public DataResponse addLeaveRequest(@Valid  @RequestBody DataRequest dataRequest) {
        return leaveRequestService.addLeaveRequest(dataRequest);
    }

    /**
     * 查询某学生所有请假申请，学生只能查看自己的所有请假申请，管理员可以查看全部学生或特定某学生的请假申请，方便管理。
     * 权限：学生或者管理员
     */
    @PostMapping("/getLeaveRequestListForStudent")
    @PreAuthorize("hasRole = 'STUDENT'")
    public DataResponse getLeaveRequestListForStudent(@Valid  @RequestBody DataRequest dataRequest) {
        return leaveRequestService.getLeaveRequestListForStudent(dataRequest);
    }
}
