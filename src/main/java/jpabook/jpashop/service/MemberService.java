package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)  //대부분이 읽기만을 하기때문에 readOnly 를 해주고
@RequiredArgsConstructor // final 선언된 필드에 관해서만 생성자를 생성해준다.
public class MemberService {


    private final MemberRepository memberRepository;

    //생성자 인잭션이 제일 권장됨  그외에도 setter 인젝션도 있긴함

//    //@Autowired  //생성자가 하나만 있는 경우 스프링이 자동으로 autowired 를 해준다
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    @Transactional // DB값을 변경하는 로직이므로 별도로 따로 써줌
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // 중복회원이면 exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findMember(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
