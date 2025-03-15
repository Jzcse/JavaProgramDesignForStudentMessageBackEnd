package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Teacher;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    @Query("SELECT teacher FROM Teacher teacher WHERE teacher.personId = :personId")
    Teacher findByPersonId(@Param("personId") Integer personId);

    @Query("SELECT teacher FROM Teacher teacher WHERE teacher.person.name LIKE :name")
    List<Teacher> findByTeacherName(@Param("name") String name);


}
