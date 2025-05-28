package cn.edu.sdu.java.server.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO {
    private Integer personId;
    private String num;       // 人员编号
    private String name;      // 姓名
    private String dept;      // 学院
    private String major;     // 专业
    private String title;     // 职称
    private String gender;    // 性别
    private String email;     // 邮箱
    private String phone;     // 电话
}
//步骤
//1.在pom添加依赖
//2.创建dto类
//3.创建pdf导出服务
//4.TeacherService
//5.TeacherController
//6。前端