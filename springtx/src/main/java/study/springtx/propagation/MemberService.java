package study.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    /**
     * 트랜잭션을 각각 사용하는 예제
     * @param username
     */
    public void joinV1(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository 호출 시작");
        memberRepository.save(member);
        log.info("== memberRepository 호출 종료");

        log.info("== logRepository 호출 종료");
        logRepository.save(logMessage);
        log.info("== logRepository 호출 종료");
    }

    /**
     * 트랜잭션을 각각 사용하는 예제
     * 로그 저장시 예외가 발생하면 예외를 복구한다.
     * 별도의 트랜잭션을 설정하지 않았다.
     * @param username
     */
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("== memberRepository 호출 시작");
        memberRepository.save(member);
        log.info("== memberRepository 호출 종료");

        log.info("== logRepository 호출 종료");
        try {
            logRepository.save(logMessage);
        } catch (RuntimeException e) {
            log.info("log 저장에 실패했습니다. logMessage={}", logMessage.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("== logRepository 호출 종료");
    }



}