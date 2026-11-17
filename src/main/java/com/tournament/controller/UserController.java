package com.tournament.controller;
import com.tournament.dto.user.UserFilter;
import com.tournament.dto.user.UserRequest;
import com.tournament.dto.user.UserResponse;
import com.tournament.model.enums.UserRole;
import com.tournament.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
/**
 * REST-контроллер пользователей (слой Controller модели MVC).
 * Содержит только маршрутизацию запросов; бизнес-логика находится в сервисе.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    /** CREATE: POST /api/users */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    /** READ: GET /api/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
    /** READ: GET /api/users */
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }
    /** UPDATE: PUT /api/users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }
    /** DELETE: DELETE /api/users/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    /**
     * READ с фильтром: GET /api/users/search?role=COACH&amp;lastName=Ив
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> search(@RequestParam(required = false) String role,
                                                     @RequestParam(required = false) String lastName) {
        UserFilter filter = new UserFilter();
        filter.setLastName(lastName);
        if (role != null && !role.isBlank()) {
            filter.setRole(UserRole.valueOf(role.trim().toUpperCase()));
        }
        return ResponseEntity.ok(userService.findByFilter(filter));
    }
    /** READ с фильтром по email: GET /api/users/email/{email} */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }
}
