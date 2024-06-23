package study.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}