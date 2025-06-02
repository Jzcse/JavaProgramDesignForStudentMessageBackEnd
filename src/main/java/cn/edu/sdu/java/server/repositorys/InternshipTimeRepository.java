package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.InternshipTime;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InternshipTimeRepository extends JpaRepository<InternshipTime,Integer> {
    @Query("SELECT MAX(i.InternshipTimeId) FROM InternshipTime i")
    Integer findMaxInternshipTimeId();

    @Query("SELECT t FROM InternshipTime t WHERE t.InternshipTimeId = :id")
    InternshipTime findInternshipTimeByInternshipTimeId(@Param("id") Integer id);

}

