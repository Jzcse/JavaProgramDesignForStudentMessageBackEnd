package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Internship;
import cn.edu.sdu.java.server.models.InternshipSpace;
import cn.edu.sdu.java.server.models.InternshipTime;
import cn.edu.sdu.java.server.models.Student;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship,Integer> {

    @Query("SELECT s FROM Internship s " + "WHERE s.student.person.personId = :personId ")
    List<Internship> findByPersonId(@Param("personId") Integer personId);
    @Query("SELECT s FROM Student s " + "WHERE s.person.personId = :personId ")
    Student findStudentByPersonId(@Param("personId") Integer personId);



}
