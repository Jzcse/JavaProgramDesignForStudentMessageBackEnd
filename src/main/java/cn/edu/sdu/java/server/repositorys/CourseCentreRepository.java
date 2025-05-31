package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.courseCentre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.net.ContentHandler;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCentreRepository extends JpaRepository<courseCentre,Integer> {
    @Query("SELECT cc FROM courseCentre cc JOIN cc.course c WHERE c.name LIKE %:name%")
    List<courseCentre> findByCourseNameContaining(@Param("name") String name);
    @Query("SELECT cc FROM courseCentre cc where cc.course.courseId = :courseId")
    courseCentre findByCourse(Integer courseId);
}

