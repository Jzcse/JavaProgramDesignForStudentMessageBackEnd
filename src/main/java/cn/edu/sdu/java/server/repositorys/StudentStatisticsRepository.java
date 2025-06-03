package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.StudentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentStatisticsRepository extends JpaRepository<StudentStatistics,Integer> {

    @Query("SELECT ss FROM StudentStatistics ss WHERE ss.student.studentId = :studentId")
    List<StudentStatistics> findByStudentId(Integer studentId);
}
