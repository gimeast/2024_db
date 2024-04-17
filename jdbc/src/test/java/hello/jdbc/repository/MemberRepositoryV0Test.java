package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud () throws SQLException {
        //생성
        Member member = new Member("memberV1", 10000);
        repository.save(member);

        //조회
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember: {}", findMember);
        assertThat(findMember).isEqualTo(member);

        //수정
        repository.update(findMember.getMemberId(), 50000);
        Member findMember2 = repository.findById(findMember.getMemberId());
        log.info("findMember2: {}", findMember2);
        assertThat(findMember).isNotEqualTo(findMember2);

        //삭제
        repository.delete(findMember2.getMemberId());
        assertThrows(NoSuchElementException.class, () -> repository.findById(findMember2.getMemberId())).printStackTrace();
    }

}