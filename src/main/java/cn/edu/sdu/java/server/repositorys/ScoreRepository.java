package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Score 数据操作接口，主要实现Score数据的查询操作
 * List<Score> findByStudentPersonId(Integer personId);  根据关联的Student的student_id查询获得List<Score>对象集合,  命名规范
 */

@Repository
public interface ScoreRepository extends JpaRepository<Score,Integer> {
    @Query("select score from Score score where score.student.studentId = :personId")
    List<Score> findByStudentPersonId(@Param("personId") Integer personId);

    @Query(value="from Score where student.studentId=?1 and  course.courseId=?2" )
    List<Score> findByStudentCourse(Integer studentId, Integer courseId);

    @Query(value="from Score where student.studentId=?1 and  course.courseId=?2" )
    Score findByStudentAndCourse(Integer studentId, Integer courseId);

    List<Score> findByCourseCourseId(Integer courseId);

    @Query(value="select score from Score score where score.course.courseId = :courseId" )
    List<Score> findByCourseId(Integer courseId);

    @Query("select score from Score score where score.teacher.personId = :teacherId")
    List<Score> findByTeacherId(@Param("teacherId") String string);

    @Query("select score from Score score where score.student.studentId = :studentId")
    List<Score> findByStudentId(Integer studentId);
}
