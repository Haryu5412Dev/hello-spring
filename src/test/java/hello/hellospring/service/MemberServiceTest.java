package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceTest {
    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void join() {
        // given -> when -> then 절차
        //given
        Member member = new Member();
        member.setName("Spring");
        System.out.println("member: " + member.getName());
        // when
        Long saveId = memberService.join(member);
        System.out.println("saveId: " + saveId);
        // then (중복 확인)
        Member result = memberService.findOne(saveId).get();
        assertThat(result.getName()).isEqualTo(member.getName());
    }

    // 중북 회원 예외 테스트 함수
    @Test
    public void 중복_회원_예외(){
        //given
        Member member1 = new Member();
        member1.setName("tuesday");
        Member member2 = new Member();
        member2.setName("tuesday");
        //when
        memberService.join(member1);
        //then
//        try{
//            memberService.join(member2);
//            fail();
//        }catch(IllegalStateException e){
//            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
//        }
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}