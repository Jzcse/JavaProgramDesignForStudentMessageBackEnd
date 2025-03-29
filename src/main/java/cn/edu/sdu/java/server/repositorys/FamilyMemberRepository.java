package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.FamilyMember;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {

    @Query("select member from FamilyMember member where member.student.person.id = :personId")
    List<FamilyMember> findByStudentPersonId(@Param("personId") Integer personId);

    //增加验证信息
    @Query("select member from FamilyMember member where member.student.person.id = :personId and member.memberId = :memberId")
    FamilyMember findByStudentPersonIdAndMemberId(@Param("personId") String personId, @Param("memberId") String memberId);

    //通过关系查找家庭成员
    @Query("select member from FamilyMember member where member.relation = :relation and member.student.person.id = :personId")
    FamilyMember findByRelation(@Param("relation") String relation, @Param("personId") String personId);

    //
    @Query("select member from FamilyMember member where member.student.person.id = :personId")
    List<FamilyMember> findByPersonId(@Param("personId") String personId);
}
