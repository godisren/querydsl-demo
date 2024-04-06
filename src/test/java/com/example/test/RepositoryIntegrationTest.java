package com.example.test;

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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration")
class RepositoryIntegrationTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TestUtil testUtil;

    JPAQuery<?> query;


    @BeforeEach
    public void eachInit() {
        query = new JPAQuery<>(entityManager);
    }

    @Test
    void initData() {
        // only execute once for integration test (external MySQL)
        // testUtil.intiData();
    }

    @Test
    void queryUserByQuerydsl() {
        QUser user = QUser.user;
        String emailLikeKeyword = "%tt.com%";
        System.out.println("== find user by email like '" + emailLikeKeyword + "'");
        List<User> findUsers = query.select(user)
                .from(user)
                .where(user.email.like(emailLikeKeyword))
                .orderBy(user.email.desc())
                .fetch();
        findUsers.forEach(System.out::println);

        Assertions.assertFalse(findUsers.isEmpty());
    }
}
