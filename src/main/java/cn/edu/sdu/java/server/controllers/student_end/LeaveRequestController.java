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

    /**
     * 查询所有学生的所有请假申请，管理员可以查看全部学生或特定某学生的请假申请，方便管理。
     * 权限：管理员
     */
    @PostMapping("/getLeaveRequestListForAdmin")
    @PreAuthorize("hasRole = 'ADMIN'")
    public DataResponse getLeaveRequestListForAdmin(@Valid  @RequestBody DataRequest dataRequest) {
        return leaveRequestService.getLeaveRequestListForAdmin(dataRequest);
    }

    /**
     * 修改请假申请的状态，管理员可以修改任何学生的任何请假申请的状态。
     * 权限：管理员
     */
    @PostMapping("/changeLeaveRequestStatus")
    @PreAuthorize("hasRole = 'ADMIN'")
    public DataResponse changeLeaveRequestStatus(@Valid  @RequestBody DataRequest dataRequest) {
        return leaveRequestService.changeLeaveRequestStatus(dataRequest);
    }

    /**
     * 获取请假申请的总数
     * 权限：管理员
     */
    @PostMapping("/getLeaveRequestCount")
    @PreAuthorize("hasRole = 'ADMIN'")
    public DataResponse getLeaveRequestCount(@Valid  @RequestBody DataRequest dataRequest) {
        return leaveRequestService.getLeaveRequestCount(dataRequest);
    }

    /**
     * 获取某条请假的详细信息
     */
    @PostMapping("/getLeaveRequestDetailInfo")
    @PreAuthorize("hasRole = 'ADMIN'")
    public DataResponse getLeaveRequestDetailInfo(@Valid  @RequestBody DataRequest dataRequest) {
        return leaveRequestService.getLeaveRequestDetailInfo(dataRequest);
    }
}
