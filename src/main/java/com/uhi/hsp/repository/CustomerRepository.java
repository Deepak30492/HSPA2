package com.uhi.hsp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uhi.hsp.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer,String>{

}
