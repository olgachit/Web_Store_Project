package fi.metropolia.olgachi.demo.repository;

import fi.metropolia.olgachi.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}