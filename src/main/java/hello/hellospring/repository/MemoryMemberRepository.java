package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository {

    private static long sequence = 0L;
    private static final Map<Long, Member> store = new HashMap<>();

    @Override
    public Member Save(Member member) {
        member.setId(++sequence); // id 값 세팅
        store.put(member.getId(), member); // 저장소에 넣기
        return member;
    }


    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName().equals(name))
                .findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }
}
