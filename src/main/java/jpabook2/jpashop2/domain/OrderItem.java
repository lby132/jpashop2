package jpabook2.jpashop2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook2.jpashop2.domain.item.Item;
import jpabook2.jpashop2.domain.item.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore //엔티티를 직접호출할때는 양방향 연관관계가 걸려있으면 양쪽을 서로 호출하면서 무한루프가 걸리기 때문에 한쪽은 @JsonIgnore를 해줘야한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;

    //생성 메서드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        final OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //비즈니스 로직
    public void cancel() {
        getItem().addStock(count);
    }

    //조회 로직
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

    //검색
//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }
}
