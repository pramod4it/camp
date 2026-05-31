package com.rajcloud.user;

import com.rajcloud.api.ApiResource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiResource.USERS)
public class UserController {
    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody CreateUserRequest request) {
        return repository.save(new User(request.name(), request.email()));
    }

    @GetMapping
    public List<User> findAll() {
        return repository.findAll();
    }

    @GetMapping(ApiResource.USER_BY_ID)
    @Cacheable(cacheNames = "users", key = "#id")
    public User findById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }
}
