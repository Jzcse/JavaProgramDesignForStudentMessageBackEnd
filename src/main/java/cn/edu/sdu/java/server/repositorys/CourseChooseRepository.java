package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.CourseChoose;
import cn.edu.sdu.java.server.models.Score;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseChooseRepository extends JpaRepository<CourseChoose,Integer> {
    @Query(value="from CourseChoose where (student.studentId=?1) and (course.courseId=?2)" )
    CourseChoose findByStudentCourse(Integer studentId, Integer courseId);

    @Query("SELECT DISTINCT cc.course FROM CourseChoose cc " +
            "WHERE cc.student.id = :studentId AND cc.course.dayOfWeek = :dayOfWeek")
    List<Course> findByStudentAndDayOfWeek(@Param("studentId") Integer studentId, @Param("dayOfWeek") String dayOfWeek);

    @Query("SELECT DISTINCT cc.course FROM CourseChoose cc WHERE cc.student.id = :studentId")
    List<Course> findCourseByStudent(@Param("studentId")Integer studentId);
}
