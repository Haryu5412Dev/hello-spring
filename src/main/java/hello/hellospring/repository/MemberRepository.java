package hello.hellospring.repository;

import java.util.List;
import java.util.Optional;

import hello.hellospring.domain.Member;


public interface MemberRepository{
    Member Save(Member member); // 등록
    Optional<Member> findById(Long id); // 조회
    Optional<Member> findByName(String name); // 조회
    // 로그인 ID로 회원 조회 (추가 기능)
    Optional<Member> findByLoginId(String loginId);
    List<Member> findAll(); // 조회
    void clearStore();
    // 기존 회원 정보를 갱신 (예: 비밀번호 변경)
    void update(Member member);
}

