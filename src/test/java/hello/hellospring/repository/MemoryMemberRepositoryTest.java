package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemoryMemberRepositoryTest {
    MemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
//        Member member1 = new Member();
//        member1.setName("Spring1");
//        repository.Save(member1);
//
//        Member member2 = new Member();
//        member2.setName("Spring2");
//        repository.Save(member2);

//        List<Member> members = repository.findAll();
//        assertThat(members.size()).isEqualTo(2);
//        for (int i = 0; i < members.size(); i++) {
//            System.out.println("member: " + members.get(i).getName());
//        }

        Member member = new Member();
        member.setName("Spring");
        repository.Save(member);

        Member result = repository.findById(member.getId()).get();
        System.out.println("result = " + (member == result));
        Assertions.assertEquals(member, result);
        assertThat(member).isEqualTo(result);
    }
}
