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