package com.example.dao;

import com.example.entity.Product;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>
        , QuerydslPredicateExecutor<Product> {
    Product findByName(String pc);
}
