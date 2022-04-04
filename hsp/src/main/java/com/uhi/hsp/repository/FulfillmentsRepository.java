package com.uhi.hsp.repository;

import com.uhi.hsp.model.Fulfillments;


import org.springframework.data.jpa.repository.JpaRepository;

public interface FulfillmentsRepository extends JpaRepository<Fulfillments, Integer> {
	Fulfillments findByFulfillmentIdAndProviderProviderId(Integer id,Integer provide);
	
}
