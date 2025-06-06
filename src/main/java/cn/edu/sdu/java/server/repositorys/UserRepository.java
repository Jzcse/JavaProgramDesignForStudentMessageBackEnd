package cn.edu.sdu.java.server.repositorys;


import cn.edu.sdu.java.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * User 数据操作接口，主要实现User数据的查询操作
 * Optional<User> findByUserName(String userName);  根据username查询获得Option<User>对象,  命名规范
 * Optional<User> findByPersonNum(String perNum);  根据关联的Person的num查询获得Option<User>对象  命名规范
 * Optional<User> findByPersonPersonId(Integer personId); 根据关联的Person的personId查询获得Option<User>对象  命名规范
 * Integer getMaxId()  user 表中的最大的user_id;    JPQL 注解
 * Optional<User> findByUserId(Integer userId);  根据userId查询获得Option<User>对象,  命名规范
 * Boolean existsByUserName(String userName);  判断userName的用户是否存在 命名规范
 */

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT user FROM User user WHERE user.person.num = :num1")
    User findByNum(@Param("num1") String num1);

    @Query(value = "SELECT user FROM User user WHERE user.person.personId = :personId")
    User findByPersonId(Integer personId);

    Optional<User> findByUserName(String userName);
    Optional<User> findByPersonNum(String perNum);
    Optional<User> findByPersonPersonId(Integer personId);


    Boolean existsByUserName(String userName);
    @Query(value="select count(*) from User where lastLoginTime >?1")
    Integer countLastLoginTime(String date);
    @Query(value = "select userType.id, count(personId) from User group by userType.id" )
    List getCountList();
}