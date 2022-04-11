package com.uhi.hsp.repository;

import com.uhi.hsp.model.Practitioner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PractitionerRepository extends JpaRepository<Practitioner, Integer> {
	
}
