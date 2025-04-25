package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AwardRepository extends JpaRepository<Award, Integer> {

        Optional<Award> findAwardByAwardId(Integer awardId);

        List<Award> findAwardListByAwardName(String awardName);
}
