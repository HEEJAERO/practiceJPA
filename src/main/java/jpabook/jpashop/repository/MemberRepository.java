package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // select m from Member m from m.name = :name 으로 자동 생성 -> name 변수에 해당하는 값 대입해서 가져옴
    List<Member> findByName(String name);
}
