package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    @DisplayName("DriverManager를 이용한 커넥션 획득")
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection = {}, class={}", con1, con1.getClass());
        log.info("connection = {}, class={}", con2, con2.getClass());
    }

    @Test
    @DisplayName("스프링이 제공하는 DriverManager DataSource를 이용한 커넥션 얻기")
    void dataSourceDriverManager() throws SQLException {
        //DriverManager DataSource - 항상 새로운 커넥션을 획득
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }
    
    /**
     * @Method         : useDataSource
     * @Description    : 커넥션 비교 함수
     * @Author         : gimeast
     * @Date           : 2024. 04. 18.
     * @params         : dataSource
     * @return         : 
     */
    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();

        log.info("connection = {}, class={}", con1, con1.getClass());
        log.info("connection = {}, class={}", con2, con2.getClass());
    }

    @Test
    @DisplayName("Hikari CP 사용")
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000);
    }


}
