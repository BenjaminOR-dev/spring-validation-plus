package dev.benjaminor.validationplus.example.service;

import dev.benjaminor.validationplus.example.dto.UserCreateRequest;
import dev.benjaminor.validationplus.example.dto.UserSearchRequest;
import dev.benjaminor.validationplus.example.dto.UserUpdateRequest;
import dev.benjaminor.validationplus.example.entity.User;
import dev.benjaminor.validationplus.example.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> search(UserSearchRequest request) {
        PageRequest page = PageRequest.of(request.getPage(), request.getSize());
        return userRepository.findAll(page).getContent();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public User create(UserCreateRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return userRepository.save(user);
    }

    public User update(Long id, UserUpdateRequest request) {
        User user = findById(id);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (StringUtils.hasText(request.getPassword())) {
            // Demo: in a real app the password hash would go here
        }
        return userRepository.save(user);
    }
}
