package dev.benjaminor.validationplus.example.web;

import dev.benjaminor.validationplus.example.dto.ConditionalUserRequest;
import dev.benjaminor.validationplus.example.dto.PasswordRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Demo endpoints for advanced rules (conditional fields, matching fields).
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    /** Pattern: {@code @RequiredIf} — when role=ADMIN, adminCode is required. */
    @PostMapping("/conditional")
    public ResponseEntity<Map<String, String>> conditional(@Valid @RequestBody ConditionalUserRequest request) {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    /** Pattern: {@code @Same} — password must match passwordConfirmation. */
    @PostMapping("/password")
    public ResponseEntity<Map<String, String>> password(@Valid @RequestBody PasswordRequest request) {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
