package fi.metropolia.olgachi.demo.service;

import fi.metropolia.olgachi.demo.entity.*;
import fi.metropolia.olgachi.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public record ItemRequest(Integer productId, Integer quantity) {
    }

    public record OrderRequest(Integer customerId, List<ItemRequest> items) {
    }

    @Transactional
    public Integer createOrder(OrderRequest request) {

        if (request == null || request.customerId() == null
                || request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Customer and items are required");
        }

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));

        List<ItemRequest> items = new ArrayList<>(request.items());
        items.sort(Comparator.comparing(
                ItemRequest::productId,
                Comparator.nullsFirst(Comparator.naturalOrder())
        ));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("NEW");

        order = orderRepository.save(order);

        Integer previousProductId = null;

        for (ItemRequest item : items) {

            if (item == null || item.productId() == null
                    || item.quantity() == null || item.quantity() <= 0
                    || item.productId().equals(previousProductId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid or duplicate product");
            }

            previousProductId = item.productId();

            Product product = productRepository
                    .findByIdForUpdate(item.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Product not found"));

            if (product.getStockQuantity() == null
                    || product.getStockQuantity() < item.quantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Insufficient stock");
            }

            if (product.getPrice() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Product price is missing");
            }

            product.setStockQuantity(
                    product.getStockQuantity() - item.quantity()
            );

            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setId(new OrderItemId(order.getId(), product.getId()));
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(product.getPrice());

            orderItemRepository.save(orderItem);
        }

        return order.getId();
    }

    @Transactional
    public Order updateOrderStatus(Integer id, String status) {

        if (status == null || !List.of(
                "NEW", "PROCESSING", "SHIPPED", "DELIVERED"
        ).contains(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid order status");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found"));

        if ("CANCELLED".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot update a cancelled order");
        }

        order.setStatus(status);

        return orderRepository.saveAndFlush(order);
    }

    @Transactional
    public Order cancelOrder(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found"));

        if ("CANCELLED".equals(order.getStatus())) {
            return order;
        }

        if ("SHIPPED".equals(order.getStatus())
                || "DELIVERED".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Shipped or delivered orders cannot be cancelled");
        }

        List<OrderItem> items = orderItemRepository.findByOrder_Id(id);

        items.sort(Comparator.comparing(item -> item.getProduct().getId()));

        for (OrderItem item : items) {

            Product product = productRepository
                    .findByIdForUpdate(item.getProduct().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Product not found"));

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );

            productRepository.save(product);
        }

        order.setStatus("CANCELLED");

        return orderRepository.saveAndFlush(order);
    }
}