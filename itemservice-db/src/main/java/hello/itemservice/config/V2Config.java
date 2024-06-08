package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV3;
import hello.itemservice.repository.v2.ItemQueryRepositoryV2;
import hello.itemservice.repository.v2.ItemRepositoryV2;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;


@Configuration
@RequiredArgsConstructor
public class V2Config {

    private final EntityManager em;
    private final ItemRepositoryV2 ItemRepositoryV2;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV2(ItemRepositoryV2, itemQueryRepositoryV2());
    }

    /*
    JpaRepository는 직접 bean으로 등록하지 않아도 된다
    @Bean
    public ItemRepositoryV2 ItemRepositoryV2() {
        JpaRepositoryFactory repositoryFactory = new JpaRepositoryFactory(em);
        return repositoryFactory.getRepository(ItemRepositoryV2.class);
    }
     */

    
    @Bean
    public ItemQueryRepositoryV2 itemQueryRepositoryV2() {
        return new ItemQueryRepositoryV2(em);
    }

    /*
    이 메서드는 TestDataInit 클래스에서 테스트 데이터 주입용으로 사용되므로
    삭제하지 않고 두었다.
     */
    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }
}
