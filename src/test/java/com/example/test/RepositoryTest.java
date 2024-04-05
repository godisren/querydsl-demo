package com.example.test;

import com.example.dao.OrderRepository;
import com.example.dao.ProductRepository;
import com.example.dao.UserRepository;
import com.example.dto.OrderUserDto;
import com.example.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@SpringBootTest
class RepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    void queryDslTest() {
        JPAQuery<?> query = new JPAQuery<>(entityManager);

        QUser user = QUser.user;
        String emailLikeKeyword = "%tt.com%";
        System.out.println("== find user by email like '" + emailLikeKeyword + "'");
        List<User> findUsers = query.select(user)
                .from(user)
                .where(user.email.like(emailLikeKeyword))
                .orderBy(user.email.desc())
                .fetch();
        findUsers.forEach(System.out::println);


        System.out.println("== find product ");
        QProduct product = QProduct.product;
        List<Product> findProducts = query.select(product)
                .from(product)
                .fetch();
        findProducts.forEach(System.out::println);


        System.out.println("== find orders");
        QOrder order = QOrder.order;
        List<Order> findOrders = query.select(order)
                .from(order)
                .where(order.userId.eq(2L))
                .fetch();
        findOrders.forEach(System.out::println);


        System.out.println("== find order by join");
        List<OrderUserDto> findOrderUser = query
//                .select(Projections.constructor(OrderUserDto.class, order.id, user.id, user.email))
                .select(Projections.constructor(OrderUserDto.class, order, user))
                .from(order)
                .join(user).on(order.userId.eq(user.id))
                .limit(3)
                .fetch();
        findOrderUser.forEach(System.out::println);

        System.out.println("== group by prices avg");
        JPAQuery<?> query2 = new JPAQuery<>(entityManager);
        QProduct productForJoin = new QProduct("productForJoin");
        List<Tuple> avgPrices = query2.select(order.id, productForJoin.price.avg())
                .from(order)
                .join(order.products, productForJoin)
                .groupBy(order.id)
                .fetch();
        avgPrices.forEach(tuple ->
                System.out.println("order id=" + tuple.get(order.id) + ", avg price=" + tuple.get(productForJoin.price.avg())));
    }

    @Test
    void queryJpa() {
        orderRepository.findAll().forEach(System.out::println);
    }

    @Test
    void queryJpaWithQueryDsl() {
        QProduct product = QProduct.product;
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "price"));
        Page<Product> result = productRepository
                .findAll(product.price.gt(BigDecimal.valueOf(20)), pageable);

        result.forEach(System.out::println);
        System.out.println("Total size:" + result.getTotalElements());
        System.out.println("Total page:" + result.getTotalPages());
        System.out.println("Size per page:" + result.getSize());
        System.out.println("Page number:" + result.getNumber());
    }

    @Test
    void createUser() {
        Assertions.assertNotNull(userRepository);

        User u = new User();
        u.setFirstName("David");
        u.setLastName("Huang");
        u.setEmail("David@tt.com");
        userRepository.save(u);

        User u2 = new User();
        u2.setFirstName("Ham");
        u2.setLastName("Lu");
        u2.setEmail("Ham@tt.com");
        userRepository.save(u2);
    }

    @Test
    void createProduct() {
        createAndSaveProduct("PC", "game mode", 100);
        createAndSaveProduct("Laptop", "document type usage", 50);
        createAndSaveProduct("Keyboard", "", 200);
        createAndSaveProduct("Power", "", 75);
        createAndSaveProduct("Book", "", 75);
        createAndSaveProduct("Desk", "", 75);
        createAndSaveProduct("Tapler", "", 10);
        createAndSaveProduct("Cup", "", 15);
        createAndSaveProduct("Pen", "", 30);
        createAndSaveProduct("Bottle", "", 20);
        createAndSaveProduct("Adapter", "", 35);
    }

    void createAndSaveProduct(String name, String description, int price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        productRepository.save(product);
    }

    @Test
    void createOrder() {
        User user = userRepository.findByEmail("David@tt.com");
        Product p1 = productRepository.findByName("PC");
        Product p2 = productRepository.findByName("Keyboard");

        Order order = new Order();
        order.setUserId(user.getId());
        order.setProducts(Set.of(p1, p2));
        order.setDate(LocalDateTime.now());
        orderRepository.save(order);
    }

}
