package com.example;

import com.example.entity.QProduct;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateProductPrice(Long productId, BigDecimal newPrice) {
        QProduct product = QProduct.product;

        final JPQLQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        queryFactory.update(product)
                .set(product.price, newPrice)
                .where(product.id.eq(productId))
                .execute();
    }

}
