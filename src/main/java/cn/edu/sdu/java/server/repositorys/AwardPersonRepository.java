package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.AwardPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardPersonRepository extends JpaRepository<AwardPerson, Integer> {
}
