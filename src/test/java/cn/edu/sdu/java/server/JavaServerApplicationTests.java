package cn.edu.sdu.java.server;

import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
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

	@Test
	void contextLoads() {
//		Map<String, String> teacherMap = new HashMap<>();
//		teacherMap.put("major", "Computer Science");
//		teacherMap.put("title", "Professor");


//		Map<String, String> personMap = new HashMap<>();
//		personMap.put("address", "124 Street");
//		personMap.put("birthday", "1990-01-01");
//		personMap.put("card", "1234567890");
//		personMap.put("dept", "Computer Science Department");
//		personMap.put("email", "teacher@example.com");
//		personMap.put("gender", "2");
//		personMap.put("introduce", "Experienced teacher");
//		personMap.put("name", "John");
//		personMap.put("phone", "1234567890");
//		personMap.put("num", "T12345");
//		personMap.put("personId","9");
//
//		DataRequest dataRequestT = new DataRequest();
//
//
//		dataRequestT.add("teacherMap",teacherMap);
//		dataRequestT.add("personMap",personMap);
//
//		dataRequestT.setData(dataRequestT.getData());
//
////		teacherService.addTeacher(dataRequestT);
//
//		teacherService.editTeacherInfo(dataRequestT);
			// 创建扁平化的form数据
			Map<String, String> form = new HashMap<>();
			form.put("num", "T12345");
			form.put("name", "John");
			form.put("address", "124 Street");
			form.put("birthday", "1990-01-01");
			form.put("card", "1234567890");
			form.put("dept", "Computer Science Department");
			form.put("email", "teacher@example.com");
			form.put("gender", "2");
			form.put("introduce", "Experienced teacher");
			form.put("phone", "1234567890");
			form.put("major", "Computer Science");
			form.put("title", "Professor");

			// 创建DataRequest对象
			DataRequest dataRequestT = new DataRequest();
			dataRequestT.add("personId", "9"); // 单独设置personId
			dataRequestT.add("form", form);   // 设置扁平化的form数据

			// 调用editTeacherInfo方法
			teacherService.editTeacherInfo(dataRequestT);
	}

}
