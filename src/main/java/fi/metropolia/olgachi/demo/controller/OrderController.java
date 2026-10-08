package fi.metropolia.olgachi.demo.controller;

import fi.metropolia.olgachi.demo.entity.*;
import fi.metropolia.olgachi.demo.repository.OrderRepository;
import fi.metropolia.olgachi.demo.repository.OrderItemRepository;
import fi.metropolia.olgachi.demo.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    public OrderController(
            OrderRepository orderRepository,
            OrderService orderService,
            OrderItemRepository orderItemRepository) {

        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Integer id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> create(
            @RequestBody OrderService.OrderRequest request) {

        Integer id = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("orderId", id));
    }

    @GetMapping("/{id}/items")
    public List<OrderItem> getOrderItems(@PathVariable Integer id) {

        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order not found");
        }

        return orderItemRepository.findByOrder_Id(id);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {

        return orderService.updateOrderStatus(
                id, request.get("status"));
    }

    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Integer id) {
        return orderService.cancelOrder(id);
    }
}