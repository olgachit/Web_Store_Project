package fi.metropolia.olgachi.demo.controller;

import fi.metropolia.olgachi.demo.entity.Customer;
import fi.metropolia.olgachi.demo.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer) {

        if (customer.getContact() != null) {
            customer.getContact().setCustomer(customer);
        }

        return repository.save(customer);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(
            @PathVariable Integer id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Integer id,
            @RequestBody Customer details) {

        return repository.findById(id)
                .map(customer -> {
                    customer.setFirstName(details.getFirstName());
                    customer.setLastName(details.getLastName());
                    customer.setEmail(details.getEmail());
                    customer.setPhone(details.getPhone());

                    return ResponseEntity.ok(repository.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}