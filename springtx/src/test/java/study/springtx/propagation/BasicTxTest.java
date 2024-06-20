package study.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager transactionManager;

    @TestConfiguration
    static class Config {

        //spring boot가 기본적으로 트랜잭션 매니저를 생성해주지만 이렇게 정의하면 정의한 트랜잭션 매니저를 사용한다.
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        transactionManager.commit(status);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        transactionManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
    }

    @Test
    void double_commit() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션1 커밋");
        transactionManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션2 커밋");
        transactionManager.commit(tx2);

    }

    @Test
    void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션1 커밋");
        transactionManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션2 롤백");
        transactionManager.rollback(tx2);

    }

    @Test
    @DisplayName("외부 트랜잭션 수행중, 내부 트랜잭션 추가 수행 테스트")
    void inner_commit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction() = {}", outer.isNewTransaction()); //DataSourceTransactionManager: Participating in existing transaction

        inner();
        
        log.info("외부 트랜잭션 커밋");
        transactionManager.commit(outer);

        /*
        결론: 트랜잭션이 전파된다. 내부 트랜잭션이 모두 커밋되야 커밋을 완료한다.
        */
    }

    void inner() {
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction() = {}", inner.isNewTransaction());

        log.info("내부 트랜잭션 커밋");
        transactionManager.commit(inner);
    }


    @Test
    @DisplayName("외부 트랜잭션 롤백 테스트")
    void outer_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionAttribute());

        inner();

        log.info("외부 트랜잭션 롤백");
        transactionManager.rollback(outer);

        /*
        결론: 외부, 내부 전체 롤백된다.
        */
    }

    @Test
    @DisplayName("내부 트랜잭션 롤백 테스트")
    void inner_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = transactionManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 롤백");
        transactionManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        transactionManager.commit(outer);

        assertThrows(UnexpectedRollbackException.class, () -> transactionManager.commit(outer));

        /*
        결론: 내부 트랜잭션 롤백시 물리트랜잭션을 롤백하지는 않고
        기존 트랜잭션을 롤백 전용으로 표시한다. 그러므로 커밋이 되지않는다.
        */
    }
    
}
