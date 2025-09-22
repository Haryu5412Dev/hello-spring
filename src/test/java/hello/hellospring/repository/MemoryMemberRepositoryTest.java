package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MemoryMemberRepositoryTest {
    MemberRepository repository = new MemoryMemberRepository();

    @Test
    public void save() {
        Member member = new Member();
        member.setName("Spring");

        repository.Save(member);

        Member result = repository.findById(member.getId()).get();

        //System.out.println("result = " + (member == result));
        //Assertions.assertEquals(member, result);
        assertThat(member).isEqualTo(result);
    }
}
