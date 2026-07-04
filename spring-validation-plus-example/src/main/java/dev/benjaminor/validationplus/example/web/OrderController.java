package dev.benjaminor.validationplus.example.web;

import dev.benjaminor.validationplus.example.dto.OrderCreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Patrón: listas con {@code @Valid} anidado ({@code items[0].quantity}, etc.).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody OrderCreateRequest request) {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "items", String.valueOf(request.getItems().size())));
    }
}
