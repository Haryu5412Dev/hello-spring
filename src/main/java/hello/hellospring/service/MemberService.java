package hello.hellospring.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;

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
        // 회원가입 시 중복 사용자 ID 체크 (기존 이름 기준 중복 체크에서 로그인ID 기준으로 변경)
        validateDuplicateMember(member, memberRepository);
        memberRepository.Save(member);
        return member.getId();
    }

    // 이런식으로 추출 -> 메서드 변환 하여 사용
    private static void validateDuplicateMember(Member member, MemberRepository memberRepository) {
        // 사용자 로그인ID 중복 확인
        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 사용자 ID입니다.");
                });
    }

    // 로그인 ID로 회원 조회
    public Optional<Member> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }

    // 이름과 전화번호로 사용자 ID 찾기
    public Optional<Member> findByNameAndPhone(String name, String phoneNumber) {
        return memberRepository.findAll().stream()
                .filter(m -> name != null && name.equals(m.getName()) && phoneNumber != null && phoneNumber.equals(m.getPhoneNumber()))
                .findAny();
    }

    // 비밀번호 재설정: 임시 비밀번호를 발급하고 해당 회원의 password 필드를 업데이트
    public Optional<String> resetPassword(String loginId, String phoneNumber) {
        Optional<Member> opt = memberRepository.findByLoginId(loginId);
        if (opt.isPresent()) {
            Member m = opt.get();
            if (m.getPhoneNumber() != null && m.getPhoneNumber().equals(phoneNumber)) {
                // 간단한 임시 비밀번호 생성 (실환경에서는 이메일/SMS 전송 필요)
                String temp = generateTempPassword();
                m.setPassword(temp);
                return Optional.of(temp);
            }
        }
        return Optional.empty();
    }

    // 사용자가 직접 새 비밀번호를 입력하여 변경하도록 하는 메서드
    public boolean updatePassword(String loginId, String phoneNumber, String newPassword) {
        Optional<Member> opt = memberRepository.findByLoginId(loginId);
        if (opt.isPresent()) {
            Member m = opt.get();
            if (m.getPhoneNumber() != null && m.getPhoneNumber().equals(phoneNumber)) {
                m.setPassword(newPassword);
                // 변경사항을 영구 저장소에 반영
                memberRepository.update(m);
                return true;
            }
        }
        return false;
    }

    private String generateTempPassword() {
        // 8자리 임시 비밀번호 (영숫자 혼합)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // 인증: 로그인ID와 패스워드가 일치하면 해당 멤버 반환
    public Optional<Member> authenticate(String loginId, String password) {
        Optional<Member> opt = memberRepository.findByLoginId(loginId);
        if (opt.isPresent()) {
            Member m = opt.get();
            if (m.getPassword() != null && m.getPassword().equals(password)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
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
