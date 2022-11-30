package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.OrderItem;
import jpabook2.jpashop2.domain.item.Order;
import jpabook2.jpashop2.repository.OrderRepository;
import jpabook2.jpashop2.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
        final List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            //Hibernate5Module가 설정이 되어있어서 이렇게 .getName()을 호출하면서
            //프록시를 강제 초기화하면 화면에 뿌려진다. (프록시가 초기화 되지 않으면 화면에 안뿌려짐.)
            final List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }
}
