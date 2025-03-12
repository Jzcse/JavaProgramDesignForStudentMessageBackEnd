package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Teacher;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    @Query(value = "SELECT teacher FROM Teacher teacher WHERE teacher.personId = :personId", nativeQuery = true)
    Teacher findByPersonId(@Param("personId") Integer personId);

    @Query(value = "SELECT teacher FROM Teacher teacher WHERE teacher.person.name LIKE :name", nativeQuery = true)
    List<Teacher> findByTeacherName(@Param("name") String name);
}
