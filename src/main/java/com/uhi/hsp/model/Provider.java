package com.uhi.hsp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@Table(schema = "hsp2", name = "provider")
@Entity
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class Provider implements Serializable {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "provider_id")
	private Integer providerId;

	@Column(name = "name")
	private String name;

	@OneToMany(mappedBy = "provider")
	@ToString.Exclude
	private List<Categories> categories;

	@OneToMany(mappedBy = "provider")
	@ToString.Exclude
	private List<Fulfillments> fulfillments;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

}
