package com.uhi.hsp.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.io.Serializable;
import java.util.List;

@Data
@Table(schema = "hsp")
@Entity
//@ToString
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Categories implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "category_id")
	private Integer categoryId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "provider_id", nullable = false)

	private Provider provider;

	@OneToMany(mappedBy = "categories", cascade = CascadeType.ALL)

	@ToString.Exclude
	private List<Fulfillments> fulfillments;

	@Column(name = "name")
	private String name;

}
