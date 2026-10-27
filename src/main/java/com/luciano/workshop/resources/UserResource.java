package com.luciano.workshop.resources;

import com.luciano.workshop.dto.*;
import com.luciano.workshop.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserResource {
    private final UserService service;
    public UserResource(UserService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> insert(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = service.insert(request);
        var location = org.springframework.web.servlet.support.ServletUriComponentsBuilder
            .fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable @Positive Long id,
                                              @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
