package cn.edu.sdu.java.server;

import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.services.CourseService;
import cn.edu.sdu.java.server.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class JavaServerApplicationTests {

    @Autowired
    private TeacherService teacherService;

    @Autowired
	private CourseService courseService;

	@Test
	void contextLoads() {
		DataRequest dataRequest = new DataRequest();
		dataRequest.add("num", "123");
		dataRequest.add("name", "课程名称");
		dataRequest.add("coursePath", "课程路径");
		dataRequest.add("credit", 3);
		dataRequest.add("classroom", "教室");
		dataRequest.add("dayOfWeek", "周一");
		dataRequest.add("startTime", "09:00");
		dataRequest.add("endTime", "11:00");
		dataRequest.add("preCourseId", null);

		// 执行课程添加操作
		DataResponse response = courseService.courseAdd(dataRequest);


	}

}
