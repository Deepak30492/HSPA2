package com.uhi.hsp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uhi.hsp.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer>{

}
