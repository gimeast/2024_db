package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.connection.DBConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SpringExceptionTranslatorTest {

    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }
    
    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammar"; //잘못된 문법
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.executeQuery();

        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(42122);
            log.info("errorCode={}", errorCode);
            log.info("error",e);
        } finally {
            JdbcUtils.closeStatement(stmt);
            JdbcUtils.closeConnection(con);
        }
    }
    
    @Test
    @DisplayName("스프링이 제공하는 예외 변환기")
    void exceptionTranslator() {
        String sql = "select bad grammar"; //잘못된 문법
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(sql);
            stmt.executeQuery();

        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);

            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException resultEx = exTranslator.translate("작업명", sql, e);
            log.info("resultEx", resultEx);
            assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        } finally {
            JdbcUtils.closeStatement(stmt);
            JdbcUtils.closeConnection(con);
        }
    }
    
}
