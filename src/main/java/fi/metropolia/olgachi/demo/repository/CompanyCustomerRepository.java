package fi.metropolia.olgachi.demo.repository;

import fi.metropolia.olgachi.demo.entity.CompanyCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyCustomerRepository
        extends JpaRepository<CompanyCustomer, Integer> {
}