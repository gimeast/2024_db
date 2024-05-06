package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class UncheckedTest {
    
    @Test
    @DisplayName("언체크 예외 catch 예제")
    void unchecked_catch() {
        Service service = new Service();
        service.callCatch();
    }
    
    @Test
    @DisplayName("언체크 예외 throw 예제")
    void unchecked_throw() {
        Service service = new Service();
        assertThrows(MyUncheckedException.class, service::callThrow);
    }

    /**
     * RuntimeException을 상속받은 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String message) {
            super(message);
        }
    }

    /**
     * UnChecked 예외는
     * 예외를 잡거나 던지지 않다도 된다.
     * 예외를 잡지않으면 자동으로 밖으로 던진다.
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 필요한 경우 예외를 잡아서 처리하면 된다.
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyUncheckedException e) {
                log.error("Caught MyUncheckedException: {}", e.getMessage());
            }
        }

        /**
         * 예외를 잡지 않아도 자연스럽게 상위로 넘어간다.
         * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
         */
        public void callThrow() {
            repository.call();
        }
    }

    static class Repository {

        /**
         * throws MyUncheckedException은 생략이 가능하다
         */
        public void call() {
            throw new MyUncheckedException("ex");
        }
    }

}
