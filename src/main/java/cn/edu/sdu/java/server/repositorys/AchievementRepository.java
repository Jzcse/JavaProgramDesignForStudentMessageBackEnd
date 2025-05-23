package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Achievement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {


    Optional<Achievement> findById(Integer achievementId);

    //根据名称模糊查询
    List<Achievement> findByAchievementNameContaining(String name);
}
