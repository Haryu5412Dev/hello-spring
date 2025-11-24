package hello.hellospring.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;


@Controller
public class MemberController {
    private final MemberService memberService;

    // @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // `HomeController`에서 이미 `/members/new` GET 매핑을 제공하므로 중복을 피하기 위해
    // 회원가입 폼 노출은 `HomeController`에 위임합니다.

    @PostMapping("/members/new")
    public String create(MemberForm form) {
        Member member = new Member();
        member.setName(form.getName());
        member.setLoginId(form.getLoginId());
        member.setPassword(form.getPassword());
        member.setPhoneNumber(form.getPhoneNumber());
        try {
            memberService.join(member);
            return "redirect:/";
        } catch (IllegalStateException e) {
            // 중복 사용자 ID 예외 발생 시 사용자에게 메시지 전달
            return "members/joinError"; // 간단한 에러 페이지에서 메시지를 보여주도록 처리
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String loginId, @RequestParam String password, Model model) {
        Optional<Member> opt = memberService.authenticate(loginId, password);
        if (opt.isPresent()) {
            // 로그인 성공 페이지로 이동하여 사용자 정보를 보여줍니다.
            model.addAttribute("member", opt.get());
            return "loginSuccess";
        } else {
            // 로그인 실패 - 같은 로그인 폼에 메시지 출력
            model.addAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login";
        }
    }

    @GetMapping("/find-login-id")
    public String findLoginIdForm() {
        return "findLoginIdForm";
    }

    @PostMapping("/find-login-id")
    public String findLoginId(@RequestParam String name, @RequestParam String phoneNumber, Model model) {
        Optional<Member> result = memberService.findByNameAndPhone(name, phoneNumber);
        if (result.isPresent()) {
            model.addAttribute("loginId", result.get().getLoginId());
            return "findLoginIdResult";
        } else {
            model.addAttribute("message", "일치하는 사용자 정보가 없습니다.");
            return "findLoginIdResult";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm() {
        return "resetPasswordForm";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String loginId,
                                @RequestParam String phoneNumber,
                                @RequestParam String newPassword,
                                Model model) {
        boolean ok = memberService.updatePassword(loginId, phoneNumber, newPassword);
        if (ok) {
            model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            model.addAttribute("message", "비밀번호 변경에 실패했습니다. 입력 정보를 확인하세요.");
        }
        return "resetPasswordResult";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
    
}
