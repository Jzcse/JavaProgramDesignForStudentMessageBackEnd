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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.AbstractAuditable_;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.print.DocFlavor;
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
            leaveRequestList.forEach(element -> arrayList.add(getMapFromLeaveRequestForStudent(element)));
            dataResponse.setCode(0);
            dataResponse.setMsg("Success to get leave request list");
            dataResponse.setData(arrayList);
            return dataResponse;
        }
    }

    private Map<String, String> getMapFromLeaveRequestForStudent(LeaveRequest leaveRequest) {
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

    /**
     * { String->page String->size }
     */
    public DataResponse getLeaveRequestListForAdmin(@Valid @RequestBody DataRequest dataRequest) {
        String page = dataRequest.getString("page");
        String size = dataRequest.getString("size");
        DataResponse dataResponse = new DataResponse();

        if(page == null || size == null){
            System.err.println("Error to get page or size in getLeaveRequestListForAdmin");
            dataResponse.setCode(1);
            dataResponse.setMsg("Please enter page and size");
        } else {
            int pageInt = Integer.parseInt(page);
            int sizeInt = Integer.parseInt(size);

            if(pageInt < 0){
                pageInt = 0;
            }
            if(sizeInt < 10){
                sizeInt = 10;
            }
            if(sizeInt > 50){
                sizeInt = 50;
            }
            Pageable pageable = PageRequest.of(pageInt, sizeInt);
            List<LeaveRequest> leaveRequestList = leaveRequestRepository.findAll(pageable).getContent();
            ArrayList<Map<String, String>> arrayList = new ArrayList<>();
            leaveRequestList.forEach(element -> arrayList.add(getMapFromLeaveRequestForAdmin(element)));
            dataResponse.setCode(0);
            dataResponse.setMsg("Success to get leave request list");
            dataResponse.setData(arrayList);
        }
        return dataResponse;
    }
    private Map<String, String> getMapFromLeaveRequestForAdmin(LeaveRequest leaveRequest) {
        Map<String, String> map = new HashMap<>();
        map.put("leaveRequestId", String.valueOf(leaveRequest.getLeaveRequestId()));
        map.put("name", leaveRequest.getStudent().getPerson().getName());
        map.put("num", leaveRequest.getStudent().getPerson().getNum());
        map.put("dept", leaveRequest.getStudent().getPerson().getDept());
        map.put("major", leaveRequest.getStudent().getMajor());
        map.put("className", leaveRequest.getStudent().getClassName());
        map.put("contact", leaveRequest.getContact());
        map.put("reason", leaveRequest.getReason());
        map.put("requestDate", leaveRequest.getRequestTime());
        map.put("startDate", leaveRequest.getStartTime());
        map.put("endDate", leaveRequest.getEndTime());
        map.put("status", String.valueOf(leaveRequest.getStatus()));
        return map;
    }

    public DataResponse changeLeaveRequestStatus(@Valid DataRequest dataRequest) {
        String leaveRequestId = dataRequest.getString("leaveRequestId");
        String result = dataRequest.getString("result"); // 1 or 2  1: 同意 2: 拒绝
        DataResponse dataResponse = new DataResponse();
        if(leaveRequestId == null || result == null){
            dataResponse.setCode(1);
            dataResponse.setMsg("Please enter leaveRequestId and result");
            return dataResponse;
        } else {
            LeaveRequest leaveRequest = leaveRequestRepository.findLeaveRequestByLeaveRequestId(leaveRequestId);
            if(leaveRequest == null){
                dataResponse.setCode(1);
                dataResponse.setMsg("Leave request not found");
                return dataResponse;
            } else {
                if (result.equals("1")) {
                    leaveRequest.setStatus(1);
                } else if (result.equals("2")) {
                    leaveRequest.setStatus(2);
                }
                leaveRequestRepository.save(leaveRequest);
                dataResponse.setCode(0);
                dataResponse.setMsg("Success to change leave request status");
                return dataResponse;
            }
        }
    }

    public DataResponse getLeaveRequestCount(@Valid DataRequest dataRequest) {
        Long count = leaveRequestRepository.count();
        return new DataResponse(0, String.valueOf(count), "Success to get leave request count");
    }

    public DataResponse getLeaveRequestDetailInfo(@Valid DataRequest dataRequest) {
        String leaveRequestId = dataRequest.getString("leaveRequestId");
        DataResponse dataResponse = new DataResponse();
        if(leaveRequestId == null){
            dataResponse.setCode(1);
            dataResponse.setMsg("Please enter leaveRequestId");
        } else {
            LeaveRequest leaveRequest = leaveRequestRepository.findLeaveRequestByLeaveRequestId(leaveRequestId);
            if(leaveRequest == null){
                dataResponse.setCode(1);
                dataResponse.setMsg("Leave request not found");
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("name", leaveRequest.getStudent().getPerson().getName());
                map.put("num", leaveRequest.getStudent().getPerson().getNum());
                map.put("startDate", leaveRequest.getStartTime());
                map.put("endDate", leaveRequest.getEndTime());
                map.put("reason", leaveRequest.getReason());
                map.put("contact", leaveRequest.getContact());
                map.put("status", String.valueOf(leaveRequest.getStatus()));
                dataResponse.setCode(0);
                dataResponse.setMsg("Success to get leave request detail info");
                dataResponse.setData(map);
            }
        }
        return dataResponse;
    }
}
