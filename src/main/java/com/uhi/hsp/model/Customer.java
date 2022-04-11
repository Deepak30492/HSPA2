package com.uhi.hsp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


import lombok.Data;
@Data
@Entity
@Table(schema = "hsp2")
public class Customer {
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "customer_id")
	private String customerId;
	private String cred;
	
	
}
