package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // member 및 item 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);
        //배송정보
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        // 이와같이 생성자가 아닌 생성메서드로 별도로 생성해줄때에는 생성자를 protected(JPA에서는 protected까지 지원해주므로)
        // 접근제약을 걸어 이후 생성할때 1가지의 스타일로 생성하도록 유도하는것이 좋다.
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);

        return order.getId();
    }
    //취소

    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문엔티티 조회 후 취소
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }
    //검색

    /*public List<Order> findOrders(){

    }*/
}
