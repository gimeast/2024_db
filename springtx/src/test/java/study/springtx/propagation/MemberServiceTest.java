package study.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON
     */
    @Test
    @DisplayName("트랜잭션을 각각 실행하는 예제")
    void outerTxOff_success() {
        //given
        String username = "outerTxOff_success";

        //when
        memberService.joinV1(username);

        //then
        Optional<Member> member = memberRepository.find(username);
        Optional<Log> logMessage = logRepository.find(username);

        assertTrue(member.isPresent());
        assertTrue(logMessage.isPresent());
    }

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON RuntimeException
     */
    @Test
    @DisplayName("트랜잭션을 각각 실행하는 중 예외가 발생하는 예제")
    void outerTxOff_fail() {
        //given
        String username = "로그예외";

        //when
        assertThrows(RuntimeException.class, () -> memberService.joinV1(username));

        //then
        Optional<Member> member = memberRepository.find(username);
        Optional<Log> logMessage = logRepository.find(username);

        assertTrue(member.isPresent());
        //로그 데이터만 롤백된다
        assertTrue(logMessage.isEmpty());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:OFF
     * logRepository    @Transactional:OFF
     */
    @Test
    @DisplayName("하나의 트랜잭션에서 실행하는 예제")
    void singleTx() {
        //given
        String username = "singleTx";

        //when
        memberService.joinV1Tx(username);

        //then
        Optional<Member> member = memberRepository.find(username);
        Optional<Log> logMessage = logRepository.find(username);

        assertTrue(member.isPresent());
        assertTrue(logMessage.isPresent());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON
     */
    @Test
    @DisplayName("트랜잭션 전파 예제 - default(REQUIRED)")
    void txPropagation() {
        //given
        String username = "REQUIRED";

        //when
        memberService.joinV1TxPropagation(username);

        //then
        Optional<Member> member = memberRepository.find(username);
        Optional<Log> logMessage = logRepository.find(username);

        assertTrue(member.isPresent());
        assertTrue(logMessage.isPresent());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception
     */
    @Test
    @DisplayName("트랜잭션 전파 예제 - default(REQUIRED)")
    void txPropagationRollback() {
        //given
        String username = "REQUIRED_로그예외";

        //when
        assertThrows(RuntimeException.class, () -> memberService.joinV1TxPropagation(username));

        //then
        Optional<Member> member = memberRepository.find(username);
        Optional<Log> logMessage = logRepository.find(username);

        //모든 데이터가 롤백된다.
        assertTrue(member.isEmpty());
        assertTrue(logMessage.isEmpty());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception
     */
    @Test
    @DisplayName("예외 복구 실패 예제")
    void recoverException_fail() {
        //given
        String username = "로그예외_recoverException_fail";

        //when
        assertThrows(UnexpectedRollbackException.class, () -> memberService.joinV2(username));
        // UnexpectedRollbackException 발생한 이유는
        // 논리 트랜잭션에서 예외가 발생하였고 이때 트랜잭션이 rollback-only 로 설정되었고
        // 서비스단에서 try~catch로 예외를 잡아 정상흐름으로 돌려놨지만
        // 물리 트랜잭션이 커밋을 호출하는 순간 rollback-only를 체크하여 UnexpectedRollbackException이 발생하게되었다.

        //then
        Optional<Member> member = memberRepository.find(username);
        Optional<Log> logMessage = logRepository.find(username);

        //모든 데이터가 롤백된다.
        assertTrue(member.isEmpty());
        assertTrue(logMessage.isEmpty());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON(REQUIRES_NEW) Exception
     */
    @Test
    @DisplayName("예외 복구 성공 예제")
    void recoverException_success() {
        //given
        String username = "로그예외_recoverException_fail";

        //when
        memberService.joinV2Recover(username);

        //then
        Optional<Member> member = memberRepository.find(username);
        Optional<Log> logMessage = logRepository.find(username);

        //회원 데이터는 저장되고
        assertTrue(member.isPresent());
        //로그 데이터는 롤백된다
        assertTrue(logMessage.isEmpty());
    }
}