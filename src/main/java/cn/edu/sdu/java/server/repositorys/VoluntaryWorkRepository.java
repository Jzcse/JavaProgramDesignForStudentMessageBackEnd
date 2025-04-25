package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Award;
import cn.edu.sdu.java.server.models.VoluntaryWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoluntaryWorkRepository extends JpaRepository<VoluntaryWork, Integer> {

    List<VoluntaryWork> findVoluntaryWorkByWorkName(String workName);

    Optional<VoluntaryWork> findVoluntaryWorkByWorkId(Integer workId);
}
