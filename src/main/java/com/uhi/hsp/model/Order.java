package com.uhi.hsp.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import lombok.Data;

@Data
@Entity
@Table(schema = "hsp")
//@SequenceGenerator(name = "seq", initialValue = 100, allocationSize = 1000)
public class Order {
	@Id
	@Column(name = "order_id")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer orderId;
	@OneToOne
	private Address address;
	@Column(name ="billing_id")
	private Integer billingId;
	private String email;
	private String phone;
	private Timestamp timestamps;

}
