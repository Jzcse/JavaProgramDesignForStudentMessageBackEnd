package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

/**
 * StudentController 主要是为学生管理数据管理提供的Web请求服务
 */

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/student")

public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    /**
     * getMapFromStudent 将学生表属性数据转换复制MAp集合里
     * @param dataRequest 包含特定学生的personId
     * @return 该学生的属性的map
     */
//    @GetMapping("/getMapFromStudent")
//    @PreAuthorize("hasRole('ADMIN')")
//    public DataResponse getMapFromStudent(@Valid @RequestBody DataRequest dataRequest){
//        return studentService.getMapFromStudent(dataRequest);
//    }

    /**
     * getStudentMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param numName 输入参数
     * @return Map List 集合
     */

    /**
     * 已完成
     * getStudentList 学生管理 点击查询按钮请求
     * 前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */
    @PostMapping("/getStudentList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.getStudentList(dataRequest);
    }


    /**
     * 已完成
     * studentDelete 删除学生信息Web服务 Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 这里注意删除顺序，应为user关联person,Student关联Person 所以要先删除Student,User，再删除Person
     * @param dataRequest 前端personId 要删除的学生的主键 person_id
     * @return 正常操作
     */
    @PostMapping("/studentDelete")
    @PreAuthorize("hasPole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.studentDelete(dataRequest);
    }

    /**
     * 已完成
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     *
     * @param dataRequest 从前端获取 personId 查询学生信息的主键 person_id
     * @return 根据personId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getStudentInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentInfo(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.getStudentInfo(dataRequest);
    }

    /**
     * 已完成
     * studentEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Person, User,Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * personId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     *
     * @return 新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/editStudentInfo")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.editStudentInfo(dataRequest);
    }

    @PostMapping("/addStudent")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addStudent(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.addStudent(dataRequest);
    }

    /**
     * getStudentScoreList 将Score对象列表集合转换成Score Map对象列表集合
     *
     * @param sList
     * @return
     */

    /**
     * getStudentMarkList 计算学生的的成绩等级
     *
     * @param sList 学生成绩列表
     * @return 成绩等级Map对象列表
     */
    /**
     * getStudentFeeList 获取学生的消费Map对象列表集合
     *
     * @param personId
     * @return
     */

    /**
     * importFeeData 前端上传消费流水Excl表数据服务
     *
     * @param barr         文件二进制数据
     * @param uploader     上传者
     * @param personIdStr student 主键
     * @param fileName     前端上传的文件名
     * @return
     */
    @PostMapping(path = "/importFeeData")
    public DataResponse importFeeData(@RequestBody byte[] barr,
                                      @RequestParam(name = "uploader") String uploader,
                                      @RequestParam(name = "personId") String personIdStr,
                                      @RequestParam(name = "fileName") String fileName) {
        return studentService.importFeeData(barr, personIdStr);
    }

    /**
     * getStudentListExcl 前端下载导出学生基本信息Excl表数据
     *
     * @param dataRequest
     * @return
     */
    @PostMapping("/getStudentListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getStudentListExcl(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.getStudentListExcl(dataRequest);
    }


    @PostMapping("/getStudentPageData")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getStudentPageData(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.getStudentPageData(dataRequest);
    }

    /*
        FamilyMember
     */
    @PostMapping("/getFamilyMemberList")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse getFamilyMemberList(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.getFamilyMemberList(dataRequest);
    }

    @PostMapping("/familyMemberSave")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberSave(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.familyMemberSave(dataRequest);
    }

    @PostMapping("/familyMemberDelete")
    @PreAuthorize(" hasRole('ADMIN') or  hasRole('STUDENT')")
    public DataResponse familyMemberDelete(@Valid @RequestBody DataRequest dataRequest) {
        return studentService.familyMemberDelete(dataRequest);
    }


    @PostMapping("/importFeeDataWeb")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse importFeeDataWeb(@RequestParam Map request, @RequestParam("file") MultipartFile file) {
        return studentService.importFeeDataWeb(request, file);
    }

    @PostMapping("/importStudentData")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse importStudentData(@RequestParam Map request, @RequestParam("file") MultipartFile file){
        System.out.println("success");
        return null;
    };
}
