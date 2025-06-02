package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.InternshipSpace;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InternshipSpaceRepository extends JpaRepository<InternshipSpace,Integer> {
    @Query("SELECT MAX(i.InternshipSpaceId) FROM InternshipSpace i")
    Integer findMaxInternshipSpaceId();

    @Query("SELECT s FROM InternshipSpace s WHERE s.InternshipSpaceId = :id")
    InternshipSpace findInternshipSpaceByInternshipSpaceId(@Param("id") Integer id);
}

