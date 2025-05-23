package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.Award;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {


    List<Activity> findByActivityName(String activityName, Pageable activityPage);

    List<Activity> findByActivityNameContaining(String name);
}
