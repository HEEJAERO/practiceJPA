package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
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
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // lazy 강제 초기화
            order.getDelivery().getOrder();
        }
        return all;
    }
    @GetMapping("/api/v2/simple-orders")
    public MemberApiController.Result ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        //return collect;
        return new MemberApiController.Result(collect.size(),collect);
    }
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return collect;
        // 패치조인을 이용한 해결법 -> 쿼리를  n+1 에서 1 개로 줄어들 수 있다는 장점
        // -> 그러나 select 절을 보면 우리가 필요없는 데이터또한 가져옴을 확인할 수 있다.
        // 장점으로는 로직이 어디서나 사용할 수 있다.
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        //jpa 에서 Dto 직접 조회 -> 우리가 필요한 데이터만 select 로 가져올 수 있다
        // 그렇다면 이게 v3 보다 좋은가? -> 성능상에 이점은 있다( 그러나 아주 근소함, 데이터가 아주 큰 경우는 좀 다르긴 함)
        // 하지만 재사용성이 굉장히 떨어진다 -> 오직 이 코드에서만 사용할 수 있는 코드이다
        //(리포지토리 재사용성 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점)
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderData;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderData = order.getOrderDate();
            orderStatus = order.getOrderStatus();
            address = order.getMember().getAddress();
        }
    }

}
