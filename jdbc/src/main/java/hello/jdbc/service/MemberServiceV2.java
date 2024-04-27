package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String from, String to, int amount) throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("con: {}", con.toString());
        try {
            //트랜잭션 시작
            con.setAutoCommit(false);

            accountTransferBizLogic(con, from, to, amount);

            //성공시 커밋
            con.commit();
        } catch (Exception e) {
            //실패시 롤백
            con.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    private void accountTransferBizLogic(Connection con, String from, String to, int amount) throws SQLException {
        //비즈니스 로직
        Member fromMember = memberRepository.findById(con, from);
        Member toMember = memberRepository.findById(con, to);

        memberRepository.update(con, from, fromMember.getMoney() - amount);
        //예외를 일으킨다
        validation(toMember);

        memberRepository.update(con, to, toMember.getMoney() + amount);
    }

    private static void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
