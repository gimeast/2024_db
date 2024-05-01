package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 트랜잭션 - @Transactional AOP
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    @Transactional
    public void accountTransfer(String from, String to, int amount) throws SQLException {
        //비즈니스 로직
        accountTransferBizLogic(from, to, amount);
    }

    private void accountTransferBizLogic(String from, String to, int amount) throws SQLException {
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
