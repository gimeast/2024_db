package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String from, String to, int amount) throws SQLException {
        //해당 람다 로직은 밖으로 예외를 던질 수 없으므로
        // Checked Exception을 RuntimeException으로 전환하여 예외를 밖으로 던질 수 있게 하였다.
        txTemplate.executeWithoutResult((status) -> {
            //비즈니스 로직
            try {
                accountTransferBizLogic(from, to, amount);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
