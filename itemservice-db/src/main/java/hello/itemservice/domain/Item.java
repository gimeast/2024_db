package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
//@Table(name="item") //생략가능
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "item_name", length = 10) //camel case는 snake case로 자동 변환해 준다.
    private String itemName;
    private Integer price;
    private Integer quantity;

    /*
    JPA는 public 또는 protected의 기본 생성자가 필수이다.
    필수인 이유는 기본 생성자를 이용해 프록시 기술을 사용하기 때문이다.
     */
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
