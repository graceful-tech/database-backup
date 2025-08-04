package com.database_backup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.database_backup.entity.customer.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

	@Query(value = "select * from hurecom_v2.customers where is_active = 'Y'", nativeQuery = true)
	List<CustomerEntity> getAllActiveCustomers();
}
