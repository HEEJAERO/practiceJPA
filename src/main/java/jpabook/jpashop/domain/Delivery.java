package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery",fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)  //기본타입은 ORDINAL -> 반드시 String으로 지정해줘야 문자열로 구분 기본은 숫자(1,2,3 .. _) 이런식으로
    //구분되기 때문에 enum값이 변경되거나 추가되면 잘못된 값이 들어갈 수도 있다
    // 1대1관계일때 foreign키는 주로 접근하는 쪽에 넣는것이 좋다
    // > 이 예제에서는 order를 통해 delivery를 주로 탐색하기 때문에 order 테이블에 놓는다.
    private DeliveryStatus status;
}
