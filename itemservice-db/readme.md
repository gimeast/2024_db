## JdbcTemplate 소개와 설정
__장점__  
- 설정의 편리함
  - JdbcTemplate은 spring-jdbc 라이브러리에 포함되어 있는데, 이 라이브러리는 스프링으로 JDBC를 사용할 때 기본으로 사용되는 라이브러리이다.
    그리고 별도의 복잡한 설정 없이 바로 사용할 수 있다.
- 반복 문제 해결
  - JdbcTemplate은 템플릿 콜백 패턴을 사용해서, JDBC를 직접 사용할 때 발생하는 대부분의 반복 작업을 대신 처리해준다.
  - 개발자는 SQL작성, 전달 파라미터 정의, 응답 값 매핑만 하면 된다.
  - 커넥션 획득, statement 준비 실행, 결과 반복 루프 실행, 
    (커넥션, statement, resultset) 종료, 커넥션 동기화, 예외 변환기

__단점__  
- 동적 SQL을 해결하기 어렵다.

## JdbcTemplate - 이름 지정 파라미터
### 이름 지정 파라미터
```
이름 지정 파라미터는 key, value 형식으로 전달하면된다.
바인딩에서 사용하는 파라미터 종류는 크게 3가지가 있다   
```
- Map
  ```
  Map<String, Object> param = Map.of("id", id);
  Item item = template.queryForObject(sql, param, itemRowMapper());
  ```
- SqlParameterSource
  - MapSqlParameterSource
    - ```
      SqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);
      
      jdbcTemplate.update(sql, param);
      ```
  - BeanPropertySqlParameterSource (제일 편하지만 MapSqlParameterSource의 예제는 Map이나 MapSqlParameterSource를 사용해야한다.)
    - ```java
      SqlParameterSource param = new BeanPropertySqlParameterSource(item);
      ```
      
## JdbcTemplate 정리
```
실무에서 가장 간단하고 실용적인 방법으로 SQL을 사용하려면 JdbcTemplate을 사용하면 된다.
JPA와 같은 ORM 기술을 사용하면 동시에 SQL을 직접 사용해야 할 때가 있는데 그때도 JdbcTemplate을 함께 사용하면 된다.
그런데 JdbcTemplate의 최대 장점이있는데 바로 동적 쿼리 문제이다.
그리고 SQL을 자바 코드로 작성하기 때문에 SQL라인이 코드로 넘어갈때마다 문자 더하기를 해야하는 단점도 있다.

이러한 문제를 해결해주는 기술이 바로 MyBatis이다.
참고로 JOOQ라는 기술도 동적쿼리 문제를 편리하게 해결해주지만 사용자가 많지 않고 설정도 까다롭다.
```
- 장점: 반복문제 해결
- 단점: 동적쿼리 문제
  
  
### @Transactional 원리
```
스프링이 제공하는 @Transactional 애노테이션은 
로직이 성공적으로 수행되면 커밋하도록 동작하지만
테스트에서 사용하면 자동 롤백시키도록 동작한다.

만약 테스트 클래스가 아닌 서비스 단에서 @Transactional이 선언 되어있다면
테스트에 선언된 트랜잭션을 참조한다.
따라서 같은 트랜잭션을 사용한다는 의미이며 같은 커넥션을 사용한다는 뜻이기도 하다.
```

## 테스트 - 임베디드 모드 DB
__임베디드 모드__
```
H2 데이터베이스는 자바로 개발되어 있고 
JVM안에서 메모리 모드로 동작하는 특별한 기능을 제공한다.
애플리케이션 종료시 H2 데이터베이스도 종료되며 데이터도 모두 사라진다.
```

---
## MyBatis
- MyBatis는 JdbcTemplate보다 더 많은 기능을 제공하는 SQL Mapper 이다.
- MyBatis는 SQL을 XML에 편리하게 작성 할 수 있다. 
- MyBatis는 동적쿼리를 편리하게 작성할 수 있다.
- JdbcTemplate은 별도의 설정없이 사용했지만 MyBatis는 약간의 설정이 필요하다.

```
MyBatis 사용시 Mapper interface에 @Mapper 어노테이션을 선언한다.
어노테이션을 선언하게되면 애플리케이션이 실행 될때 
@Mapper가 붙은 인터페이스를 조회하여 프록시 객체를 생성하고
생성된 구현체를 빈으로 등록 해준다.
구현체는 MyBatis에서 발생한 예외를 DataAccessException으로 변환 해준다.
```

## JPA 적용3 - 예외 변환
JPA의 경우 예외가 발생하면 JPA 예외가 발생하게 된다.
- EntityManager는 순수한 JPA 기술이고 스프링과는 관계가 없다. 따라서 엔티티 매니저는 예외가 발생하면 JPA 관련 예외를 발생시킨다.
- JPA는 PersistenceException과 그 하위 예외를 발생시킨다.
  - 추가로 JPA는 IllegalStateException, IllegalArgumentException을 발생시킬 수 있다.
- JPA 예외를 스프링 예외 추상화(DataAccessException)로 변환 시켜주는 비밀은 @Repository이다.
  - @Repository의 기능은 컴포넌트 스캔의 대상이 되는것
  - 예외 변환 AOP 적용대상이 되는것
    - 스프링과 JPA를 함께 사용하는 경우 스프링은 JPA 예외 변환기(PersistenceExceptionTranslator)를 등록한다.
    - 예외 변환 AOP 프록시는 JPA 관련 예외가 발생하면 JPA 예외 변환기를 통해 발생한 예외를 스프링 데이터 접근 예외로 변환한다.
   
  
  
  
  
  
  
  





