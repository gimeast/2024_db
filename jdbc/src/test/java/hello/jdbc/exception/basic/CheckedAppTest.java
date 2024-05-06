package hello.jdbc.exception.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class CheckedAppTest {

    @Test
    @DisplayName("체크 예외 v1")
    void checked() {
        Controller controller = new Controller();
        assertThrows(Exception.class, controller::request).printStackTrace();
    }
    
    static class Controller {
        Service service = new Service();
        
        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }
    
    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException { //throws SQLException, ConnectException을 throws Exception으로 해도 되지만 정말 좋지않은 방법이다.
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("SQL 예외 발생");
        }
    }
}
