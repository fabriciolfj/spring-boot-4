package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.entity.User;
import com.github.fabriciolfj.study.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final User user) {
        userService.create(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") final Long id) {
        var user = userService.getUser(id);
        return user.map(value -> ResponseEntity.accepted().body(value)).orElseGet(() -> ResponseEntity.notFound().build());

    }
}
