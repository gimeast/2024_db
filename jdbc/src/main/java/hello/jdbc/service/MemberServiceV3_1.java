package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV3_1 {

//    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String from, String to, int amount) throws SQLException {
        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            //비즈니스 로직
            accountTransferBizLogic(from, to, amount);
            //성공시 커밋
            transactionManager.commit(status);
        } catch (Exception e) {
            //실패시 롤백
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }

    }

    private void accountTransferBizLogic(String from, String to, int amount) throws SQLException {
        //비즈니스 로직
        Member fromMember = memberRepository.findById(from);
        Member toMember = memberRepository.findById(to);

        memberRepository.update(from, fromMember.getMoney() - amount);
        //예외를 일으킨다
        validation(toMember);

        memberRepository.update(to, toMember.getMoney() + amount);
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
