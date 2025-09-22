package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import java.awt.*;
import java.util.List;
import java.util.Optional;


public interface MemberRepository{
    Member Save(Member member); // 등록
    Optional<Member> findById(Long id); // 조회
    Optional<Member> findByName(String name); // 조회
    List<Member> findAll(); // 조회
    void clearStore();
}

