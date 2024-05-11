package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 * MemberRepository 인터페이스 의존
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 {

    private final MemberRepository memberRepository;

    @Transactional
    public void accountTransfer(String from, String to, int amount) {
        //비즈니스 로직
        accountTransferBizLogic(from, to, amount);
    }

    private void accountTransferBizLogic(String from, String to, int amount) {
        //비즈니스 로직
        Member fromMember = memberRepository.findById(from);
        Member toMember = memberRepository.findById(to);

        memberRepository.update(from, fromMember.getMoney() - amount);
        //예외를 일으킨다
        validation(toMember);

        memberRepository.update(to, toMember.getMoney() + amount);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
