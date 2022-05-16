package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {
    // 상속관계의 부모에 상속전략을 지정해 줘야함 (JOINED ,SINGLE_TABLE, TABLE_PER_CLASS)
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

    //==비지니스 로직==//
    // item 에 관련된 핵심 비지니스 로직은 따로 작성하는것보다는 item 엔티티에 넣는것이 더 좋다?
    public void addStock(int quantity){  //stock 증가
        this.stockQuantity+=quantity;
    }
    public void removeStock(int quantity){ // stock 감소
        int result = this.stockQuantity - quantity;
        if (result < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = result;
    }

}
