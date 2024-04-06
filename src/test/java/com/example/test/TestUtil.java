package com.example.test;

import com.example.dao.OrderRepository;
import com.example.dao.ProductRepository;
import com.example.dao.UserRepository;
import com.example.entity.Order;
import com.example.entity.Product;
import com.example.entity.User;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class TestUtil {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public void intiData() {
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

        User user = userRepository.findByEmail("David@tt.com");
        Product p1 = productRepository.findByName("PC");
        Product p2 = productRepository.findByName("Keyboard");

        Order order = new Order();
        order.setUserId(user.getId());
        order.setProducts(Set.of(p1, p2));
        order.setDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    void createAndSaveProduct(String name, String description, int price) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        productRepository.save(product);
    }

}
