package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.CourseTeaching;
import cn.edu.sdu.java.server.models.Teacher;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseTeachingRepository extends JpaRepository<CourseTeaching,Integer> {
    @Query("SELECT DISTINCT ct.course FROM CourseTeaching ct WHERE ct.teacher.personId = :teacherId AND ct.course.dayOfWeek = :dayOfWeek")
    List<Course> findByTeacherAndDayOfWeek(@Param("teacherId") Integer teacherId, @Param("dayOfWeek") String dayOfWeek);

    @Query(value="select item from CourseTeaching item where (item.teacher.personId=?1) and (item.course.courseId=?2)" )
    CourseTeaching findByCourseAndTeacher(Integer personId, Integer courseId);

    @Query("SELECT DISTINCT ct.course FROM CourseTeaching ct WHERE ct.teacher.personId = :teacherId")
    List<Course> findByTeacher(Integer teacherId);

    @Query("SELECT DISTINCT ct.teacher FROM CourseTeaching ct WHERE ct.course.courseId = :courseId")
    List<Teacher> findTeacherByCourse(Integer courseId);

    @Query("SELECT CT FROM CourseTeaching CT WHERE CT.teacher.personId = :teacherId")
    List<CourseTeaching> findByTeacherId(@Param("teacherId") String teacherId);
}
