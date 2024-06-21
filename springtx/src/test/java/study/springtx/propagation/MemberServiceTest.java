package study.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

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
        String username = "test";

        //when
        memberService.joinV1(username);

        //then
        Optional<Member> member = memberRepository.find(username);
        Optional<Log> logMessage = logRepository.find(username);

        assertTrue(member.isPresent());
        assertTrue(logMessage.isPresent());
    }

}