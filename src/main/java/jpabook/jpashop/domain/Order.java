package jpabook.jpashop.domain;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // cascade = CascadeType.ALL 로 지정해주면 각각의 orderitem을 persist 하지 않고 order만 persist 하면
    // 모든 orderitem  이 저장된다

    // cascade = CascadeType.ALL 적용전 : entity 별로 persist를 해주어야 DB에 저장된다.
    // em.persist(orderItemA)
    // em.persist(orderItemB)
    // em.persist(orderItemC)
    // em.persist(order)

    // cascade = CascadeType.ALL 적용 후 : em.persist(order) ->order 저장 시 order의 컬렉션에 있는 모든 orderItems 를 저장해준다

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id") // 연관관계의 주인에 작성
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // 연관관계 편의 메서드 - 컨트롤 하는쪽이 들고 있는것이 좋다?(양방향에서)
    // 양쪽값을 하나의 코드(원자적으로) 세팅이 가능
    public void setMember(Member member){ // order에는 member값을 세팅하고  member에는 order를 더해준다/
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);

    }



}
