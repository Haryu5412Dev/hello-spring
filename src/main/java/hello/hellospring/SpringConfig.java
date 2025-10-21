package hello.hellospring;

import hello.hellospring.controller.MemberController;
import hello.hellospring.repository.MemoryMemberRepository;
import hello.hellospring.service.MemberService;
import hello.hellospring.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 컨트롤러가 Bean을 읽으면 SpringConfig 안의 내용을 읽어서 실행하게 한다
public class SpringConfig {

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }

//    @Bean
//    public MemberRepository memberRepository(){
//        return new dbMemberRepository();
//    }
}
