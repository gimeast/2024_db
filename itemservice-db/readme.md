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