package study.springtx.apply;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class InitTxTest {

    @Autowired
    Hello hello;

    @Test
    void go() {
        //V1 @PostConstruct 초기화 코드는 스프링이 초기화 시점에 호출한다.
        //V2 @EventListener(ApplicationReadyEvent.class) 스프링 컨테이너가 완전희 올라왔을때 동작한다.
    }

    @TestConfiguration
    static class InitTxTestConfig {
        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1() {
            //@PostConstruct와 @Transactional을 함께 사용하면 트랜잭션이 적용되지 않는다.
            //왜냐하면 초기화 코드가 먼저 실행되고 그 다음 트랜잭션 AOP가 적용되기 때문이다.
            //따라서 초기화 시점에는 해당 메서드에서 트랜잭션을 획득할 수 없다.
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active: {}", isActive); //false
        }

        @EventListener(ApplicationReadyEvent.class) //스프링 컨테이너가 완전희 올라왔을때 동작한다.
        @Transactional
        public void initV2() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReadyEvent tx active: {}", isActive); //true
        }
    }
}
