package hello.jdbc.exception.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UnCheckedAppTest {

    @Test
    @DisplayName("체크 예외 v1")
    void unchecked() {
        Controller controller = new Controller();
        assertThrows(Exception.class, controller::request).printStackTrace();
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
        public RuntimeSqlException(Throwable cause) {
            super(cause);
        }
    }
}
