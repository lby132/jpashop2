package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.OrderStatus;
import jpabook2.jpashop2.domain.item.*;
import jpabook2.jpashop2.exception.NotEnoughStockException;
import jpabook2.jpashop2.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        final Member member = createMember();

        final Book book = createBook("JPA", 10000, 10);

        int orderCount = 2;

        //when
        final Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        final Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus());
        assertEquals(1, getOrder.getOrderItems().size());
        assertEquals(10000 * orderCount, getOrder.getTotalPrice());
        assertEquals(8, book.getStockQuantity());
    }


    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        final Member member = createMember();
        final Item item = createBook("JPA", 10000, 10);

        int orderCount = 11;

        //expect
        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        });

    }


    @Test
    public void 주문취소() throws Exception {
        //given
        final Member member = createMember();
        final Item item = createBook("JPA", 10000, 10);
        int orderCount = 2;
        final Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        final Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals(10, item.getStockQuantity());
    }


    private Book createBook(String name, int price, int stockQuantity) {
        final Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        final Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

}