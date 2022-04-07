package com.uhi.hsp.repository;

import com.uhi.hsp.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository extends JpaRepository<Billing, Integer> {
	public Billing findByFulfillmentsFulfillmentId(Integer fulfillmentId);
	public Billing findByCustomerCustomerIdAndFulfillmentsProviderProviderId(String customerId,Integer providerId);
	//public Billing findByCustomerCustomerIdAndFulfillmentsProviderProviderId(Integer customerId,Integer providerId);
}
