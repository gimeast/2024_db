package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 마이바티스 매핑 XML을 호출해주는 매퍼 인터페이스이다.
 * @Mapper를 붙여야 인식을 할 수 있다.
 * 구현체는 자동으로 만들어진다.
 */
@Mapper
public interface ItemMapper {

    void save(Item item);

    //파라미터가 2개이상 넘어가는 경우 @Param을 넘겨줘야 한다.
    void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond itemSearch);

}
