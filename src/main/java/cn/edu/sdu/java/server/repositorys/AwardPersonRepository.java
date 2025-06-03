package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.AwardPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AwardPersonRepository extends JpaRepository<AwardPerson, Integer> {
    List<AwardPerson> findByRelatedStudentId(Integer relatedStudentId);

    @Query("select ap from AwardPerson ap where ap.relatedStudentId = :studentId ")
    List<AwardPerson> findByStudentId(Integer studentId);
}
