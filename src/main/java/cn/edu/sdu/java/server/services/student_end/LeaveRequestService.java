package cn.edu.sdu.java.server.services.student_end;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.student_end.LeaveRequest;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.student_end.LeaveRequestRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class LeaveRequestService {

    @Autowired
    LeaveRequestRepository leaveRequestRepository;
    @Autowired
    StudentRepository studentRepository;

    /**
     * {num contact reason requestTime startTime endTime}
     */
    public DataResponse addLeaveRequest(@Valid @RequestBody DataRequest dataRequest) {
        Map<String, String> data = dataRequest.getMap("data");
        DataResponse dataResponse = new DataResponse();
        if (data == null) {
            System.err.println("Error to get form in addLeaveRequest");
            dataResponse.setCode(1);
            dataResponse.setMsg("Error to get form in addLeaveRequest");
            return dataResponse;
        }
        String num = CommonMethod.getString(data, "num");
        Optional<Student> optionalStudent = studentRepository.findStudentByNum(num);
        if (optionalStudent.isEmpty()) {
            System.err.println("student is not found by num in addLeaveRequest");
            dataResponse.setCode(1);
            dataResponse.setMsg("Student not found");
            return dataResponse;
        }
        Student student = optionalStudent.get();
        String contact = CommonMethod.getString(data, "contact");
        String reason = CommonMethod.getString(data, "reason");
        String requestTime = CommonMethod.getString(data, "requestDate");
        String startTime = CommonMethod.getString(data, "startDate");
        String endTime = CommonMethod.getString(data, "endDate");
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setContact(contact);
        leaveRequest.setReason(reason);
        leaveRequest.setRequestTime(requestTime);
        leaveRequest.setStartTime(startTime);
        leaveRequest.setEndTime(endTime);
        leaveRequest.setStatus(0);
        leaveRequest.setStudent(student);
        leaveRequestRepository.save(leaveRequest);
        dataResponse.setCode(0);
        dataResponse.setMsg("Success to add leave request");
        return dataResponse;
    }

    /**
     *  { num }
     */
    public DataResponse getLeaveRequestListForStudent(@Valid @RequestBody DataRequest dataRequest) {
        DataResponse dataResponse = new DataResponse();
        String num = dataRequest.getString("num");
        Optional<Student> optionalStudent = studentRepository.findStudentByNum(num);
        if (optionalStudent.isEmpty()) {
            System.err.println("student is not found by num in getLeaveRequestListForStudent");
            return new DataResponse(1, null, "not Found");
        } else {
            Student student = optionalStudent.get();
            List<LeaveRequest> leaveRequestList = leaveRequestRepository.findLeaveRequestByStudentNum(num);
            ArrayList<Map<String, String>> arrayList = new ArrayList<>();
            leaveRequestList.forEach(element -> arrayList.add(getMapFromLeaveRequest(element)));
            dataResponse.setCode(0);
            dataResponse.setMsg("Success to get leave request list");
            dataResponse.setData(arrayList);
            return dataResponse;
        }
    }

    private Map<String, String> getMapFromLeaveRequest(LeaveRequest leaveRequest) {
        Map<String, String> map = new HashMap<>();
        map.put("name", leaveRequest.getStudent().getPerson().getName());
        map.put("num", leaveRequest.getStudent().getPerson().getNum());
        map.put("contact", leaveRequest.getContact());
        map.put("reason", leaveRequest.getReason());
        map.put("requestTime", leaveRequest.getRequestTime());
        map.put("startTime", leaveRequest.getStartTime());
        map.put("endTime", leaveRequest.getEndTime());
        map.put("status", String.valueOf(leaveRequest.getStatus()));
        return map;
    }
}
