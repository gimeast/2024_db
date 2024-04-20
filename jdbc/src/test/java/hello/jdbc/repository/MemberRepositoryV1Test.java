package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        //기본 DriverManager - 항상 새로운 커넥션 획득
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud () throws SQLException {
        //생성
        Member member = new Member("memberV4", 10000);
        repository.save(member);

        //조회
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember: {}", findMember);
        assertThat(findMember).isEqualTo(member);

        //전체조회
        List<Member> members = repository.findAll();
        log.info("members: {}", members);
        assertThat(members).hasSize(4);

        //수정
        repository.update(findMember.getMemberId(), 50000);
        Member findMember2 = repository.findById(findMember.getMemberId());
        log.info("findMember2: {}", findMember2);
        assertThat(findMember).isNotEqualTo(findMember2);

        //삭제
        repository.delete(findMember2.getMemberId());
        assertThrows(NoSuchElementException.class, () -> repository.findById(findMember2.getMemberId())).printStackTrace();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}