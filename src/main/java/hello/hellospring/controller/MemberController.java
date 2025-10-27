package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import hello.hellospring.controller.MemberForm;

@Controller
public class MemberController {
    private final MemberService memberService;

    // @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
