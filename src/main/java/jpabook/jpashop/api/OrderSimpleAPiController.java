package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * oneToONe, ManyToOne 에서의 성능최적화 위주
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleAPiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // lazy 강제 초기화
            order.getDelivery().getOrder();
        }
        return all;
    }
    @GetMapping("/api/v2/simple-orders")
    public MemberApiController.Result ordersV2(){
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<simpleOrderDto> collect = orders.stream()
                .map(o -> new simpleOrderDto(o))
                .collect(Collectors.toList());
        //return collect;
        return new MemberApiController.Result(collect.size(),collect);
    }

    @GetMapping("/api/v3/simple-orders")
    public List<simpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<simpleOrderDto> collect = orders.stream().map(o -> new simpleOrderDto(o)).collect(Collectors.toList());
        return collect;
    }
    @Data
    static class simpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderData;
        private OrderStatus orderStatus;
        private Address address;

        public simpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderData = order.getOrderDate();
            orderStatus = order.getOrderStatus();
            address = order.getMember().getAddress();
        }
    }

}
