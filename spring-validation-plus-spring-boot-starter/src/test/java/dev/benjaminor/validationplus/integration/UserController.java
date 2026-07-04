package dev.benjaminor.validationplus.integration;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.benjaminor.validationplus.constraints.MinValue;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping(value = "/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, String>> createForm(@Valid @ModelAttribute UserRequest request) {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getById(@PathVariable("id") @MinValue(1) Long id) {
        return ResponseEntity.ok(Map.of("id", String.valueOf(id)));
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody PasswordRequest request) {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, String>> search(@RequestParam("size") Integer size) {
        return ResponseEntity.ok(Map.of("size", String.valueOf(size)));
    }

    @GetMapping("/flags")
    public ResponseEntity<Map<String, String>> flags(@RequestParam("active") Boolean active) {
        return ResponseEntity.ok(Map.of("active", String.valueOf(active)));
    }
}
