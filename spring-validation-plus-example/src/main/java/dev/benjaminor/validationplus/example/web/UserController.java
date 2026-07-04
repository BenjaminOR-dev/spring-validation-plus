package dev.benjaminor.validationplus.example.web;

import dev.benjaminor.validationplus.example.dto.UserCreateRequest;
import dev.benjaminor.validationplus.example.dto.UserSearchRequest;
import dev.benjaminor.validationplus.example.dto.UserUpdateRequest;
import dev.benjaminor.validationplus.example.entity.User;
import dev.benjaminor.validationplus.example.service.UserService;
import dev.benjaminor.validationplus.constraints.MinValue;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CRUD endpoints — patterns documented in {@code spring-validation-plus-example/README.md}.
 */
@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** Pattern: {@code @Valid @ModelAttribute} for query params. */
    @GetMapping
    public List<User> search(@Valid @ModelAttribute UserSearchRequest request) {
        return userService.search(request);
    }

    /** Pattern: {@code @Validated} + constraint on {@code @PathVariable}. */
    @GetMapping("/{id}")
    public User find(@PathVariable @MinValue(1) Long id) {
        return userService.findById(id);
    }

    /** Pattern: {@code @Unique} on create. */
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    /** Pattern: {@code @Unique} + {@code excludeParameter} on update. */
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }
}
