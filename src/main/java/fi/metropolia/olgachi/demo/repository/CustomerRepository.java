package fi.metropolia.olgachi.demo.repository;

import fi.metropolia.olgachi.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository
        extends JpaRepository<Customer, Integer> {
}
