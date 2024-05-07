package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class UnCheckedAppTest {

    @Test
    @DisplayName("체크 예외 v1")
    void unchecked() {
        Controller controller = new Controller();
        assertThrows(Exception.class, controller::request).printStackTrace();
    }
    
    @Test
    @DisplayName("예외 포함")
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
//            e.printStackTrace(); 이렇게 해도 되지만 좋지않은 방식이다. 이 방식은 System.out 으로 출력하는것과 같다.
            log.info("ex", e);
        }
    }
    
    static class Controller {
        Service service = new Service();
        
        public void request() {
            service.logic();
        }
    }
    
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() { //throws SQLException, ConnectException을 throws Exception으로 해도 되지만 정말 좋지않은 방법이다.
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call()  {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSqlException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSqlException extends RuntimeException {
        public RuntimeSqlException(Throwable cause) { //기존 예외를 갖는 메커니즘이다.
            super(cause);
        }
    }
}
