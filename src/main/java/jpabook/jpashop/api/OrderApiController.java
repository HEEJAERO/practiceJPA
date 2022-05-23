package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // 현재 lazy 로딩 으로 설정되어있으므로, 프록시( db에서 실제 값을 불러오기 전의 임의의 가짜값) 을 강제로 초기화 시켜줘야한다.
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return collect;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        // order 가 orderItem 총 갯수만큼 데이터가 뻥튀기 되어서 나온다. (order 2개가 각각 2개의 orderItem 을 가지고 있다면 총4개의 데이터 반환
        // 단순히 패치조인을 하여 데이터를 가져오면 같은값이 중복되어 나오는 부작용이 있다.
        // -> distinct 를 추가하여 해결한다
        // db 에서는 모든 컬럼값이 똑같아야 하므로 데이터베이스에서 가져온 결과는 같다
        // 하지만 JPA 에서 가져온 id 값이 같으면 중복을 제거하여 준다.
        // 단점: 페이징이 불가능? -> setFirstResult,setMaxResult 와 같이 제약된 값을 가져오는 것(페이징) 이 불가능해진다
        // 디비에서 모든 데이터를 가져온 다음, 메모리에서 페이징을 진행한다 ex)10000개의 데이터, 100개만 가져온다고 제약을 걸면
        // 10000개의 데이터를 다 불러온다음에 메모리에서 100개만을 추려서 보내줌 -> 메모리 부하
        // 우리는 orderItem 이 아닌 order 를 기준으로 페이징을 하고싶다면 어떻게 해야할까?
        return orderRepository.findAllWithItem().stream().map(o -> new OrderDto(o)).collect(toList());
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        // *toOne 관계만 fetch 조인을 이용하면 paging 문제가 발생하지 않음
        // *toMany 는 BatchSize 로 해결한다.
        // batch size =100 이라면 query 의 in 에 100개 까지 가져옴
        // fetch 조인과  batchsize 를 혼합해서 사용하는것이 좋다
        // @BatchSize() or application.yml 에 batch size 옵션을 추가하자.
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return result;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        // 이 경우 쿼리 3개
        return orderQueryRepository.findOrderQueryDtos();
     }
     @GetMapping("/api/v5/orders")
     public List<OrderQueryDto> ordersV5(){
        // 이 경우 쿼리 2개 order 와 orderItems를 조회 -> 이후 orderId 값으로 매칭하여 데이터 반환
         return orderQueryRepository.findAllByDto_optimization();
     }
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        // 이 부분은 스트림에 대한 이해가 부족 -> 스트림에 대해 공부해보고 이후 정리
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),   e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }



    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getOrderStatus();
            this.address = order.getDelivery().getAddress();
            //order.getOrderItems().stream().forEach(o->o.getItem().getName());
            // orderItem 엔티티가 외부에 노출 -> 이것또한 dto로 바꾸어 진행해야한다

            this.orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
            // 이와 같이 필요한 정보만을 Dto 로 바꾸어 외부에 노출해야 한다.
        }
    }
    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;
        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();


        }
    }

}
