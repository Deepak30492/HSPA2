package com.uhi.hsp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uhi.hsp.model.Order;

public interface OrderRepository extends JpaRepository<Order,Integer>{

}
