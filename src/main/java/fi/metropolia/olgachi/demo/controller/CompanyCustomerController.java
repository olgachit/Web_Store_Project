package fi.metropolia.olgachi.demo.controller;

import fi.metropolia.olgachi.demo.entity.CompanyCustomer;
import fi.metropolia.olgachi.demo.repository.CompanyCustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company-customers")
public class CompanyCustomerController {

    private final CompanyCustomerRepository repository;

    public CompanyCustomerController(
            CompanyCustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<CompanyCustomer> getAllCompanyCustomers() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyCustomer> updateCompanyCustomer(
            @PathVariable Integer id,
            @RequestBody CompanyCustomer details) {

        return repository.findById(id)
                .map(customer -> {
                    customer.setFirstName(details.getFirstName());
                    customer.setLastName(details.getLastName());
                    customer.setEmail(details.getEmail());
                    customer.setPhone(details.getPhone());
                    customer.setCompanyName(details.getCompanyName());

                    return ResponseEntity.ok(repository.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CompanyCustomer create(
            @RequestBody CompanyCustomer customer) {
        return repository.save(customer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyCustomer> getById(
            @PathVariable Integer id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }
}