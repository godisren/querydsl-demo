package com.example.dto;

import com.example.entity.Order;
import com.example.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderUserDto {
        private Order order;
        private User user;
}
