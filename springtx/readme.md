# 트랜잭션

## 스프링 컨테이너에 트랜잭션 프록시 등록
```
- @Transactional 애노테이션이 특정 클래스나 메서드에 하나라도 있으면
    트랜잭션 AOP는 프록시를 만들어서 스프링 컨테이너에 등록한다.
    
- @Autowired BasicService basicService 로 의존관계 주입을 요청하면
    스프링 컨테이너에는 실제 객체 대신 프록시가 스프링 빈으로 등록되어 있으므로 프록시를 주입한다.
    
- 프록시는 BasicService를 상송해서 만들어지기 때문에 다형성을 활용할 수 있다.
    따라서 BasicService 대신 프록시인 BasicService$$CGLIB를 주입할 수 있다.
    
- @Transactional이 tx()에 붙어 프록시를 생성하였어도 nonTx()는 어노테이션이 붙지 않았으므로
    트랜잭션을 실행 하지 않고 nonTx()만 호출하고 종료한다. 
```

## 트랜잭션 두 번 사용
```
히카리 커넥션 풀에서 커넥션을 획득하면 실제 커넥션을 그대로 반환하는 것이 아니라 내부 관리를 위해 히카리 프록시 커넥션이라는
객체를 생성해서 반환한다.
내부에는 실제 커넥션이 포함되어있다. 객체의 주소를 확인하면 커넥션 풀에서 획득한 커넥션을 구분할 수 있다.

트랜잭션1: Acquired Connection [HikariProxyConnection@1481591973 wrapping conn0
트랜잭션2: Acquired Connection [HikariProxyConnection@1082862306 wrapping conn0

물리커넥션은 conn0으로 같지만 객체의 주소가 다르다.
즉 이전 커넥션풀을 반납하고 새로 조회하는 것이다.
```