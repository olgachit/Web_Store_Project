package fi.metropolia.olgachi.demo.repository;

import fi.metropolia.olgachi.demo.entity.OrderItem;
import fi.metropolia.olgachi.demo.entity.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByOrder_Id(Integer orderId);
}