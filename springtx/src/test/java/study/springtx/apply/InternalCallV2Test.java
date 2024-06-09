package study.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV2Test {

    @Autowired
    CallService callService;


    @Test
    void printProxy() {
        log.info("callService class = {}", callService.getClass());
    }

    @Test
    void externalCallV2() {
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallService callService() {
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService() {
            return new InternalService();
        }
    }

    static class CallService {

        private final InternalService internalService;

        CallService(InternalService internalService) {
            this.internalService = internalService;
        }

        public void external() {
            log.info("call external method");
            printTxInfo();

            //프록시를 거치지 않은 실제 대상객체(target)의 함수를 호출하면 트랜잭션이 적용되지 않는다.
            //프록시를 사용하면 메서드 내부 호출에 프록시를 적용할 수 없다.
            internalService.internal();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active: {}", txActive);
        }

    }

    static class InternalService {

        //트랜잭션 AOP는 프록시를 사용한다.
        @Transactional
        public void internal() {
            log.info("call internal method");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active: {}", txActive);
        }
    }
}
