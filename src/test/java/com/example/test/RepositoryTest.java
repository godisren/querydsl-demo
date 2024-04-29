package com.example.test;

import com.example.ProductService;
import com.example.dao.OrderRepository;
import com.example.dao.ProductRepository;
import com.example.dto.OrderUserDto;
import com.example.entity.Order;
import com.example.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ProductService productService;

    JPAQuery<?> query;

    @BeforeAll
    public void init() {
        testUtil.intiData();
    }

    @BeforeEach
    public void eachInit() {
        query = new JPAQuery<>(entityManager);
    }

    @Test
    void queryByJpa() {
        orderRepository.findAll().forEach(System.out::println);
    }

    @Test
    void queryByJpaWithQueryDsl() {
        QProduct product = QProduct.product;
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "price"));
        Page<Product> result = productRepository
                .findAll(product.price.gt(BigDecimal.valueOf(20)), pageable);

        result.forEach(System.out::println);
        System.out.println("Total size:" + result.getTotalElements());
        System.out.println("Total page:" + result.getTotalPages());
        System.out.println("Size per page:" + result.getSize());
        System.out.println("Page number:" + result.getNumber());

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void queryUserByQuerydsl() {
        QUser user = QUser.user;
        String emailLikeKeyword = "%tt.com%";
        System.out.println("== find user by email like '" + emailLikeKeyword + "'");
        List<User> findUsers = query
                .select(user)
                .from(user)
                .where(user.email.like(emailLikeKeyword))
                .orderBy(user.email.desc())
                .fetch();
        findUsers.forEach(System.out::println);

        Assertions.assertFalse(findUsers.isEmpty());
    }

    @Test
    void queryProductByQuerydsl() {
        System.out.println("== find product ");
        QProduct product = QProduct.product;
        List<Product> findProducts = query.select(product)
                .from(product)
                .fetch();
        findProducts.forEach(System.out::println);

        Assertions.assertFalse(findProducts.isEmpty());
    }

    @Test
    void queryOrdersByQuerydsl() {
        System.out.println("== find orders");
        QOrder order = QOrder.order;

        List<Order> findOrders = query.select(order)
                .from(order)
                .fetch();
        findOrders.forEach(System.out::println);

        Assertions.assertFalse(findOrders.isEmpty());
    }

    @Test
    void findOrdersWithJoinByQuerydsl() {
        QOrder order = QOrder.order;
        QUser user = QUser.user;

        System.out.println("== find order by join");
        List<OrderUserDto> findOrderUser = query
                .select(Projections.constructor(OrderUserDto.class, order, user))
                .from(order)
                .join(user).on(order.userId.eq(user.id))
                .limit(3)
                .fetch();
        findOrderUser.forEach(System.out::println);

        Assertions.assertFalse(findOrderUser.isEmpty());
    }

    @Test
    void groupingByQuerydsl() {
        QOrder order = QOrder.order;

        System.out.println("== group by prices avg");
        QProduct productForJoin = new QProduct("productForJoin");
        List<Tuple> avgPrices = query.select(order.id, productForJoin.price.avg())
                .from(order)
                .join(order.products, productForJoin)
                .groupBy(order.id)
                .fetch();
        avgPrices.forEach(tuple ->
                System.out.println("order id=" + tuple.get(order.id) + ", avg price=" + tuple.get(productForJoin.price.avg())));

        Assertions.assertFalse(avgPrices.isEmpty());
    }

    @Test
    void updateProductByQuerydsl() {
        System.out.println("== update product price");

        String productName = "Cup";
        BigDecimal addPrice = BigDecimal.valueOf(10);
        QProduct product = QProduct.product;
        Product findProduct = query
                .select(product)
                .from(product)
                .where(product.name.eq(productName))
                .fetchOne();

        System.out.println("current product " + productName + " price " + addPrice.stripTrailingZeros().toPlainString());
        BigDecimal newPrice = findProduct.getPrice().add(addPrice);

        // update data should be put in @Transactional
        productService.updateProductPrice(findProduct.getId(), newPrice);

        Product findUpdateProduct = query
                .select(product)
                .from(product)
                .where(product.id.eq(findProduct.getId()))
                .fetchOne();
        System.out.println("current product " + productName + " new price " + addPrice.stripTrailingZeros().toPlainString());

        Assertions.assertEquals(newPrice.stripTrailingZeros(),
                findUpdateProduct.getPrice().stripTrailingZeros());
    }


}
