package fi.metropolia.olgachi.demo.controller;

import fi.metropolia.olgachi.demo.entity.Supplier;
import fi.metropolia.olgachi.demo.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierRepository repository;

    public SupplierController(SupplierRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Supplier> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Supplier create(@RequestBody Supplier supplier) {
        return repository.save(supplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> update(
            @PathVariable Integer id,
            @RequestBody Supplier details) {

        return repository.findById(id)
                .map(supplier -> {
                    supplier.setName(details.getName());
                    supplier.setContactName(details.getContactName());
                    supplier.setPhone(details.getPhone());
                    supplier.setEmail(details.getEmail());

                    return ResponseEntity.ok(repository.save(supplier));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}