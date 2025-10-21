package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private MemberRepository memberRepository = new MemoryMemberRepository();

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /* 회원 가입 */
    public Long join(Member member) {
        // ifPresent는 Optional 타입일때만 사용 가능 (기업에서는 잘 사용하지 않는 방법)
//        Optional<Member> result = memberRepository.findByName(member.getName());
//        result.ifPresent(m -> 
//        {
//            throw new IllegalStateException("이미 존재하는 회원(ID)입니다.");
//        });
        validateDuplicateMember(member, memberRepository);
        memberRepository.Save(member);
        return member.getId();
    }

    // 이런식으로 추출 -> 메서드 변환 하여 사용
    private static void validateDuplicateMember(Member member, MemberRepository memberRepository) {
        memberRepository.findByName(member.getName())
        .ifPresent(m ->
        {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
    }

    /* 회원 전체 조회 */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /* 특정 회원 조회 */
    public Optional<Member> findOne(Long id) {
        return memberRepository.findById(id);
    }

}
