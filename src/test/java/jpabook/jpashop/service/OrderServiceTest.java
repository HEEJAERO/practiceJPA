package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Item book = createBook("시골 JPA", 10000, 100);

        int orderCount = 2;
        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //then

        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getOrderStatus(), "상품 주문시 상태는 order");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품수가 정확해야함");
        assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문가격 = 개당 가격 * 주문수량");
        assertEquals(98, book.getStockQuantity(), "주문한 수만큼 재고가 줄어야함");

    }

    @Test
    public void 상품주문_재고수량초과()  throws Exception{
        //given
        Member member = createMember();
        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;
        //when
        Assertions.assertThrows(NotEnoughStockException.class, () ->
                orderService.order(member.getId(), book.getId(), orderCount));
        //then
    }
    @Test
    public void 주문_취소() throws Exception {
        Member member = createMember();
        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        orderService.cancelOrder(orderId);

        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL,getOrder.getOrderStatus(),"주문 취소시 상태는 cancel 로 변경");
        assertEquals(10,book.getStockQuantity(),"재고가 원복되어야합니다");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

}