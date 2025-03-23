package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Award;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AwardRepository extends JpaRepository<Award, Integer> {

        List<Award> findByAwardName(String awardName, Pageable awardPage);
}
